
use std::fmt;
use std::fmt::Display;
use std::fmt::Debug;
use std::fmt::Formatter;
use std::iter::IntoIterator;
use std::ops::Index;
use std::ops::IndexMut;

use super::adjacent_placements::AdjacentPlacements;
use super::placement_position::PlacementPosition;
use super::placement_position::PlacementPositionIterator;
use super::super::wrappers::ListWrapper;
use super::super::wrappers::OptionPrintWrapper;
use super::super::lib_square;
use super::super::lib_square::MapDisplay;

type PlacementArray<'a, T> = Box<[Option<&'a T>]>;

#[derive(Default, Clone, Debug)]
pub struct PlacementMap<'a, T:?Sized> {
    placements: PlacementArray<'a, T>,
    positions: PlacementPositionIterator,
    peek_cache: Option<Option<PlacementPosition>>
}

impl<'a, T:?Sized> PlacementMap<'a, T> {
    pub fn new(side_length: u16) -> Self {
        let total_size: usize = (side_length as usize).pow(2);
        let placements: PlacementArray<'a, T> = vec![None; total_size].into();
        PlacementMap {
            placements,
            positions: PlacementPositionIterator::new(side_length),
            peek_cache: None
        }
    }
    pub fn side_length(&self) -> u16 {
        self.positions.side_length()
    }
    pub fn capacity(&self) -> usize {
        self.placements.len()
    }
    pub fn clear(&mut self) {
        self.positions = PlacementPositionIterator::new(self.positions.side_length());
    }
}

impl<'a, T:?Sized + Display> PlacementMap<'a, T> {
    pub fn as_list_fmt(&'a self) -> impl Display + 'a {
        ListWrapper::new(self)
    }
}

impl<'a, T:?Sized> Index<PlacementPosition> for PlacementMap<'a, T> {
    type Output = Option<&'a T>;
    fn index(&'_ self, pos: PlacementPosition) -> &'_ Option<&'a T> {
        let index = pos.flat_position(self.side_length()) as usize;
        &self.placements.get(index).expect("failed to const index placements array")
    }
}
impl<'a, T:?Sized> IndexMut<PlacementPosition> for PlacementMap<'a, T> {
    fn index_mut(self: &mut PlacementMap<'a, T>, pos: PlacementPosition) -> &mut Option<&'a T> {
        let index = pos.flat_position(self.side_length()) as usize;
        (&mut self.placements).get_mut(index).expect("failed to index placements array")
    }
}

#[derive(Debug)]
pub struct SquareMapFullError{}

impl<'a, T: Copy> PlacementMap<'a, T> {
    pub fn as_box(&self) -> Option<Box<[T]>> {
        let mut mut_vec = Vec::<T>::with_capacity(self.placements.len());
        for opt_item in self.placements.iter() {
            match opt_item {
                None => { return None; },
                // otherwise load the next item into the vector
                Some(&item) => mut_vec.push(item)
            }
        }
        Some(mut_vec.into())
    }
}
impl<'a, T:?Sized + Display> Display for PlacementMap<'a, T> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        let last_pos = self.last_position();
        if last_pos.is_none() {
            return write!(fmt, "[<Empty PlacementMap>]");
        };
        let filled_count = self.len();
        let square_width = lib_square::isqrt(filled_count);
        let square_width = if square_width.pow(2) != filled_count { square_width + 1 } else { square_width };
        let square_length = square_width.pow(2);
        let tile_square: &[Option<&'a T>] = &self.placements[square_length..];
        MapDisplay(tile_square).fmt_map(fmt, |opt_t: &Option<&'a T>| -> &OptionPrintWrapper<'a, T> {
            unsafe {
                std::mem::transmute::<&Option<&'a T>, &OptionPrintWrapper<'a, T>>(opt_t)
            }
        })
    }
}

impl<'a, T:?Sized> PlacementMap<'a, T> {
    pub fn len(&'a self) -> usize {
        let len = self.placements.len() - self.positions.len();
        if self.peek_cache.is_some() {
            len - 1
        } else {
            len
        }
    }
    pub fn as_slice(&'a self) -> &'a [Option<&'a T>] {
        &self.placements
    }
    pub fn get_adjacents(&'a self, pos: PlacementPosition) -> AdjacentPlacements<'a, T> {
        let slice: &[Option<&'a T>] = &self.placements;
        AdjacentPlacements::from_slice(slice, pos, self.side_length())
    }
    pub fn peek_pos(&mut self) -> Option<PlacementPosition> {
        let peek_cache = &mut self.peek_cache;
        let positions = &mut self.positions;
        *peek_cache.get_or_insert_with(|| {
            positions.next()
        })
    }
    pub fn push(&'_ mut self, value: &'a T) -> Result<(), SquareMapFullError> {
        match self.peek_cache.as_mut() {
            Some(ref mut next) => next.take(),
            None => self.positions.next()
        }.and_then(|pos| {
            let flat_pos = pos.flat_position(self.side_length()) as usize;
            self.placements.get_mut(flat_pos).map(|out|{
                *out = Some(value);
            })
        }).ok_or(SquareMapFullError{})
    }
    pub fn pop(&mut self) -> Option<&'a T> {
        if let Some(cache) = self.peek_cache.take() {
            assert!(cache == self.positions.next_back());
        }
        self.positions.next_back().and_then(|pos|{
            let flat_pos = pos.flat_position(self.side_length()) as usize;
            self.placements.get_mut(flat_pos).and_then(|out|{
                let value = *out;
                *out = None;
                value
            })
        })
    }
    pub fn last_position(&self) -> Option<PlacementPosition> {
        if let Some(Some(_peeked)) = self.peek_cache {
            // peek cache and current position will return equivalent positions
            self.positions.peek_back()
        } else {
            // if the peek cache is None or Some(None), current position is valid
            // if it's Some(None) current position is valid because
            // the iterator can't advance more
            self.positions.current_position()
        }
    }
    pub fn replace_last(&mut self, new_t: &'a T) -> Option<&'a T> {
        self.last_position().and_then(|position|{
            let old_t = self[position];
            if old_t.is_none() {
                panic!("failed to find inner edge of Some region");
            }
            self[position] = Some(new_t);
            old_t
        })
    }
}

pub struct PlacementMapIterator<'a, 'b, T:?Sized> {
    map: &'b PlacementMap<'a, T>,
    positions: PlacementPositionIterator
}

impl<'a, T:?Sized> IntoIterator for &'a PlacementMap<'a, T> {
    type IntoIter = PlacementMapIterator<'a, 'a, T>;
    type Item = &'a T;
    fn into_iter(self) -> Self::IntoIter {
        PlacementMapIterator {
            map: self,
            positions: PlacementPositionIterator::new(self.side_length())
        }
    }
}

impl<'a, 'b, T:?Sized> Iterator for PlacementMapIterator<'a, 'b, T> {
    type Item = &'a T;
    fn next(&mut self) -> Option<&'a T> {
        self.positions.next().and_then(|pos|{
            self.map.placements[pos.flat_position(self.map.side_length()) as usize]
        })
    }
}

#[cfg(test)]
#[path="./test/test_placement_map.rs"]
mod test_placement_map;
