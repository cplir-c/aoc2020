
use std::iter;
use std::iter::ExactSizeIterator;
use std::ops::Index;
use std::ops::IndexMut;

use super::adjacent_placements::AdjacentPlacements;
use super::placement_position::PlacementPosition;
use super::placement_position::PlacementPositionIterator;
use super::placement_slot::MutPlacementSlot;

type PlacementArray<'a, T> = Box<[MutPlacementSlot<'a, T>]>;

#[derive(Default)]
pub struct PlacementMap<'a, T> {
    placements: PlacementArray<'a, T>,
    positions: PlacementPositionIterator
}

impl<'a, T> Index<PlacementPosition> for PlacementMap<'a, T> {
    type Output = MutPlacementSlot<'a, T>;
    fn index(&'_ self, pos: PlacementPosition) -> &'_ MutPlacementSlot<'a, T> {
        let side_length = self.positions.side_length();
        let index = pos.flat_position(side_length) as usize;
        &self.placements.get(index).expect("failed to const index placements array")
    }
}
impl<'a, T> IndexMut<PlacementPosition> for PlacementMap<'a, T> {
    fn index_mut(self: &mut PlacementMap<'a, T>, pos: PlacementPosition) -> &mut MutPlacementSlot<'a, T> {
        let side_length = self.positions.side_length();
        let index = pos.flat_position(side_length) as usize;
        (&mut self.placements).get_mut(index).expect("failed to index placements array")
    }
}

impl<'a, T> ExactSizeIterator for PlacementMap<'a, T> {
    fn len(&self) -> usize {
        self.placements.len() - self.positions.len()
    }
}

impl<'a, T> PlacementMap<'a, T> {
    pub fn get_adjacents(&'a mut self, pos: PlacementPosition) -> AdjacentPlacements<'a, T> {
        let slice: &mut [MutPlacementSlot<T>] = &mut self.placements;
        let side_length = self.positions.side_length();
        AdjacentPlacements::from_slice(slice, pos, side_length)
    }
}

impl<'a, T> Iterator for PlacementMap<'a, T> {
    type Item = PlacementPosition;
    fn next(self: &'_ mut PlacementMap<'a, T>) -> Option<PlacementPosition> {
        self.positions.next()
    }
}

impl<'a, T> DoubleEndedIterator for PlacementMap<'a, T> {
    fn next_back(&mut self) -> Option<Self::Item> {
        self.positions.next_back()
    }
}

impl<'a, T> PlacementMap<'a, T> {
    pub fn new(edge_size: u16) -> Self {
        let total_size: usize = (edge_size as usize).pow(2);
        let iter_placements = iter::repeat_with(||MutPlacementSlot::from(None)).take(total_size);
        let mut placement_vec = Vec::with_capacity(total_size);
        placement_vec.extend(iter_placements);
        let placements: PlacementArray<'a, T> = placement_vec.into();
        PlacementMap {
            placements,
            positions: PlacementPositionIterator::new(edge_size)
        }
    }
}
