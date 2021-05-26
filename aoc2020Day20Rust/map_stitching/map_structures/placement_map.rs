
use std::borrow::Borrow;
use std::ops::Index;
use std::ops::IndexMut;

use super::placement_position::PlacementPosition;
use super::placement_position::PlacementPositionIterator;
use super::super::tile_structures::TilePlacement;

type Placement<'a, 'b, S> = Option<&'b TilePlacement<'a, 'a, S>>;
type PlacementArray<'a, 'b, S> = Box<[Placement<'a, 'b, S>]>;

#[derive(Default)]
pub struct PlacementMap<'a, 'b, 'c, 'd, S: Borrow<str>> {
    placements: PlacementArray<'a, 'b, S>,
    positions: PlacementPositionIterator,
    out_ref: Option<&'d mut Placement<'a, 'b, S>>
}

impl<'a, 'b, 'c, 'd, S: Borrow<str>> Index<PlacementPosition> for PlacementMap<'a, 'b, 'c, 'd, S> {
    type Output = Placement<'a, 'b, S>;
    fn index(&self, pos: PlacementPosition) -> &'d Placement<'a, 'b, S> {
        let side_length = self.positions.side_length();
        let index = pos.flat_position(side_length);
        &(self.placements.get(index).expect("failed to index placements array"))
    }
}
impl<'a, 'b, 'c, 'd, S: Borrow<str>> IndexMut<PlacementPosition> for PlacementMap<'a, 'b, 'c, 'd, S> {
    fn index_mut(self: &mut PlacementMap<'a, 'b, 'c, 'd, S>, pos: PlacementPosition) -> &'d mut Placement<'a, 'b, S> {
        let side_length = self.positions.side_length();
        let index = pos.flat_position(side_length);
        &mut (self.placements.get_mut(index).expect("failed to index placements array"))
    }
}

pub struct AdjacentPlacements<'a, 'b, 'c, 'd, S: Borrow<str>> {
    up: Option<&'c Placement<'a, 'b, S>>,
    left: Option<&'c Placement<'a, 'b, S>>,
    out: &'d &'c mut Placement<'a, 'b, S>
}

impl<'a, 'b, 'c, 'd, S: Borrow<str>> PlacementMap<'a, 'b, 'c, 'd, S> {
    fn get_out(&'_ mut self, pos: PlacementPosition) -> &'d &'c mut Placement<'a, 'b, S> {
        self.out_ref = Some(self.index_mut(pos));
        // unless multithreading shennanigans happen, this is safe
        self.out_ref.as_ref().unwrap()
    }
    fn get_adjacent(&'_ mut self, pos: PlacementPosition) -> AdjacentPlacements<'a, 'b, '_, 'd, S> {
        let up: Option<&'c Placement<'a, 'b, S>> = pos.up().map(|pos| &self[pos]);
        let left: Option<&'c Placement<'a, 'b, S>> = pos.left().map(|pos| &self[pos]);
        let out: &'d &'c mut Placement<'a, 'b, S> = self.get_out(pos);
        
        AdjacentPlacements {
            up, left, out
        }
    }
}

impl<'a, 'b, 'c, 'd, S: Borrow<str>> Iterator for PlacementMap<'a, 'b, 'c, 'd, S> {
    type Item = AdjacentPlacements<'a, 'b, 'c, 'd, S>;
    fn next(&'_ mut self) -> Option<AdjacentPlacements<'a, 'b, '_, 'd, S>> {
        let next_pos = self.positions.next();
        next_pos.map(|pos| self.get_adjacent(pos))
    }
}

impl<'a, 'b, 'c, 'd, S: Borrow<str>> PlacementMap<'a, 'b, 'c, 'd, S> {
    pub fn new(edge_size: u16) -> Self {
        let total_size: usize = (edge_size as usize).pow(2);
        let placements: PlacementArray<'a, 'b, S> = (vec![None; total_size]).into();
        PlacementMap {
            placements,
            positions: PlacementPositionIterator::new(edge_size),
            out_ref: None
        }
    }
}