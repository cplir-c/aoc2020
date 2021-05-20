
use std::borrow::Borrow;
use std::clone::Clone;

use super::sides::Side;
use super::sides::SideIterator;
use super::edges::EdgeBits;
use super::tile_placement::TilePlacement;

#[derive(Debug, Default, Eq, PartialEq, Hash, Copy, Clone)]
pub struct EdgePlacement {
    pub side: Side,
    pub bits: EdgeBits
}

impl<'a, 'b, S: Borrow<str>> From<EdgeReference<'a, 'b, S>> for EdgePlacement {
    fn from(edge_ref: EdgeReference<'a, 'b, S>) -> Self {
        EdgePlacement {
            side: edge_ref.side,
            bits: edge_ref.placement[edge_ref.side]
        }
    }
}

impl<'a, 'b, S: Borrow<str>> From<&EdgeReference<'a, 'b, S>> for EdgePlacement {
    fn from(edge_ref: &EdgeReference<'a, 'b, S>) -> Self {
        EdgePlacement {
            side: edge_ref.side,
            bits: edge_ref.placement[edge_ref.side]
        }
    }
}

pub struct PlacementEdgePlacementIterator<'a, 'b, S: Borrow<str>> {
    pub side_iter: SideIterator,
    pub this: &'b TilePlacement<'a, 'b, S>
}

impl<'a, 'b, S: Borrow<str>> Iterator for PlacementEdgePlacementIterator<'a, 'b, S> {
    type Item = EdgePlacement;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        self.side_iter.next().map(|side|{self.this.get_edge_placement(side)})
    }
}

#[derive(Debug, Clone)]
pub struct EdgeReference<'a, 'b, S: Borrow<str>> {
    pub side: Side,
    pub placement: TilePlacement<'a, 'b, S>
}

impl<'a, 'b, S: Borrow<str> + Clone> Copy for EdgeReference<'a, 'b, S> {}

pub struct PlacementEdgeReferenceIterator<'a, 'b, S: Borrow<str>> {
    pub side_iter: SideIterator,
    pub this: &'b TilePlacement<'a, 'b, S>
}

impl<'a, 'b, S: Borrow<str> + Clone> Iterator for PlacementEdgeReferenceIterator<'a, 'b, S> {
    type Item = EdgeReference<'a, 'b, S>;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        self.side_iter.next().map(|side|{self.this.get_edge_ref(side)})
    }
}