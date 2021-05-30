
use std::convert::AsRef;
use std::borrow::Borrow;
use std::ops::Index;
use std::ops::IndexMut;

use super::placement_position::PlacementPosition;
use super::placement_position::PlacementPositionIterator;
use super::super::tile_structures::TilePlacement;

struct Placement<'a, 'b, S: Borrow<str>>(Option<&'b TilePlacement<'a, 'b, S>>);
impl<'a, 'b, S: Borrow<str>> From<Option<&'b TilePlacement<'a, 'b, S>>> for Placement<'a, 'b, S> {
    fn from(option_tile_placement: Option<&'b TilePlacement<'a, 'b, S>>) -> Self {
        Placement(option_tile_placement)
    }
}
impl<'a, 'b, S: Borrow<str>> From<&'b TilePlacement<'a, 'b, S>> for Placement<'a, 'b, S> {
    fn from(tile_ref: &'b TilePlacement<'a, 'b, S>) -> Self {
        Placement::from(Some(tile_ref))
    }
}
impl<'a, 'b, S: Borrow<str>> From<&'b MutPlacement<'a, 'b, S>> for Placement<'a, 'b, S> {
    fn from(mut_place: &'b MutPlacement<'a, 'b, S>) -> Self {
        let inner: Option<&'b TilePlacement<'a, 'b, S>> = mut_place.0.map(|mut_ref: &'b mut TilePlacement<'a, 'b, S>| {
            mut_ref.borrow()
        });
        Placement(inner)
    }
}
impl<'a, 'b, S: Borrow<str>> From<&'b MutPlacement<'a, 'b, S>> for &Placement<'a, 'b, S> {
    fn from(mut_ref: &'b MutPlacement<'a, 'b, S>) -> Self {
        mut_ref.as_ref()
    }
}

struct MutPlacement<'a, 'b, S: Borrow<str>>(Option<&'b mut TilePlacement<'a, 'b, S>>);
impl<'a, 'b, S: Borrow<str>> From<Option<&'b mut TilePlacement<'a, 'b, S>>> for MutPlacement<'a, 'b, S> {
    fn from(option_tile_placement: Option<&'b mut TilePlacement<'a, 'b, S>>) -> Self {
        MutPlacement(option_tile_placement)
    }
}
impl<'a, 'b, S: Borrow<str>> From<&'b mut TilePlacement<'a, 'b, S>> for MutPlacement<'a, 'b, S> {
    fn from(tile_ref: &'b mut TilePlacement<'a, 'b, S>) -> Self {
        MutPlacement::from(Some(tile_ref))
    }
}
impl<'a, 'b, S: Borrow<str>> AsRef<Placement<'a, 'b, S>> for MutPlacement<'a, 'b, S> {
    fn as_ref(&self) -> &Placement<'a, 'b, S> {
        let inner: Option<&'b TilePlacement<'a, 'b, S>> = self.0.map(|mut_ref: &'b mut TilePlacement<'a, 'b, S>| {
            mut_ref.borrow()
        });
        &Placement(inner)
    }
}

type PlacementArray<'a, 'b, S> = Box<[MutPlacement<'a, 'b, S>]>;

#[derive(Default)]
pub struct PlacementMap<'a, 'b, S: Borrow<str>> {
    placements: PlacementArray<'a, 'b, S>,
    positions: PlacementPositionIterator
}

impl<'a, 'b, S: Borrow<str>> Index<PlacementPosition> for PlacementMap<'a, 'b, S> {
    type Output = MutPlacement<'a, 'b, S>;
    fn index(&'_ self, pos: PlacementPosition) -> &'_ MutPlacement<'a, 'b, S> {
        let side_length = self.positions.side_length();
        let index = pos.flat_position(side_length);
        &self.placements.get(index).expect("failed to const index placements array")
    }
}
impl<'a, 'b, S: Borrow<str>> IndexMut<PlacementPosition> for PlacementMap<'a, 'b, S> {
    fn index_mut(self: &mut PlacementMap<'a, 'b, S>, pos: PlacementPosition) -> &mut MutPlacement<'a, 'b, S> {
        let side_length = self.positions.side_length();
        let index = pos.flat_position(side_length);
        &mut self.placements.get_mut(index).expect("failed to index placements array")
    }
}

pub struct AdjacentPlacements<'a, 'b, S: Borrow<str>> {
    up: Option<&'b Placement<'a, 'b, S>>,
    left: Option<&'b Placement<'a, 'b, S>>,
    out: &'b mut MutPlacement<'a, 'b, S>
}

impl<'a, 'b, S: Borrow<str>> PlacementMap<'a, 'b, S> {
    fn get_adjacent(&'b mut self, pos: PlacementPosition) -> AdjacentPlacements<'a, 'b, S> {
        let up: Option<&Placement<S>> = {
            let mut_up: Option<&MutPlacement<S>> = pos.up().map(|pos| &self[pos]);
            mut_up.map(<&Placement<S>>::from)
        };
        let left: Option<&Placement<S>> = {
            let mut_left: Option<&MutPlacement<S>> = pos.left().map(|pos| &self[pos]);
            mut_left.map(<&Placement<S>>::from)
        };
        let out: &mut MutPlacement<S> = &mut self.placements[pos.flat_position(self.positions.side_length())];
        AdjacentPlacements {
            up, left, out
        }
    }
}

impl<'a, 'b, S: Borrow<str>> Iterator for PlacementMap<'a, 'b, S> {
    type Item = AdjacentPlacements<'a, 'b, S>;
    fn next(self: &mut PlacementMap<'a, 'b, S>) -> Option<AdjacentPlacements<'a, 'b, S>> {
        let next_pos = self.positions.next();
        next_pos.map(|pos|{ 
            self.get_adjacent(pos)
        })
    }
}

impl<'a, 'b, S: Borrow<str>> PlacementMap<'a, 'b, S> {
    pub fn new(edge_size: u16) -> Self {
        let total_size: usize = (edge_size as usize).pow(2);
        let iter_placements = std::iter::repeat_with(||MutPlacement(None)).take(total_size);
        let placement_vec = Vec::with_capacity(total_size);
        placement_vec.extend(iter_placements);
        let placements: PlacementArray<'a, 'b, S> = placement_vec.into();
        PlacementMap {
            placements,
            positions: PlacementPositionIterator::new(edge_size)
        }
    }
}