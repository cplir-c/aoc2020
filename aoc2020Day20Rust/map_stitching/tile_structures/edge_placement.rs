
use std::borrow::Borrow;
use std::clone::Clone;
use std::fmt;
use std::fmt::Debug;
use std::fmt::Formatter;

use super::sides::Side;
use super::sides::SideIterator;
use super::edges::EdgeBits;
use super::tile_placement::TilePlacement;

#[derive(Debug, Default, Eq, PartialEq, Hash, Copy, Clone)]
pub struct EdgePlacement {
    pub side: Side,
    pub bits: EdgeBits
}

impl<'a, 'b, S: Borrow<str>> From<&EdgeReference<'a, S>> for EdgePlacement {
    fn from(edge_ref: &EdgeReference<'a, S>) -> Self {
        EdgePlacement {
            side: edge_ref.side,
            bits: edge_ref.placement[edge_ref.side]
        }
    }
}

impl<'a, 'b, S: Borrow<str>> From<EdgeReference<'a, S>> for EdgePlacement {
    fn from(edge_ref: EdgeReference<'a, S>) -> Self {
        EdgePlacement::from(&edge_ref)
    }
}

pub struct PlacementEdgePlacementIterator<'a, 'b, S: Borrow<str>> {
    pub side_iter: SideIterator,
    pub this: &'b TilePlacement<'a, S>
}

impl<'a, 'b, S: Borrow<str>> Iterator for PlacementEdgePlacementIterator<'a, 'b, S> {
    type Item = EdgePlacement;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        self.side_iter.next().map(|side|{self.this.get_edge_placement(side)})
    }
}

#[derive(PartialEq, Eq)]
pub struct EdgeReference<'a, S: Borrow<str>> {
    pub side: Side,
    pub placement: TilePlacement<'a, S>
}

impl<'a, 'b, S: Borrow<str>> Copy for EdgeReference<'a, S> {}
impl<'a, 'b, S: Borrow<str>> Clone for EdgeReference<'a, S> {
    fn clone(&self) -> Self {
        Self {
            side: self.side,
            placement: self.placement
        }
    } 
}

impl <'a, S: Borrow<str>> Debug for EdgeReference<'a, S> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        if fmt.alternate() {
            write!(fmt, "EdgeReference {{
                side: {:#?},
                placement: {:#?},
            }}", self.side, self.placement)
        } else {
            write!(fmt, "EdgeReference {{ side: {:?}, placement: {:?}, }}", self.side, self.placement)
        }
    }
}

pub struct PlacementEdgeReferenceIterator<'a, 'b, S: Borrow<str>> {
    pub side_iter: SideIterator,
    pub this: &'b TilePlacement<'a, S>
}

impl<'a, 'b, 'c, S: Borrow<str>> Iterator for PlacementEdgeReferenceIterator<'a, 'c, S> {
    type Item = EdgeReference<'a, S>;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        self.side_iter.next().map(|side|{self.this.get_edge_ref(side)})
    }
}