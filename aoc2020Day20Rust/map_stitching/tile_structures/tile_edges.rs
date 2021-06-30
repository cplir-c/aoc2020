
use std::borrow::Borrow;
use std::fmt;
use std::fmt::Debug;
use std::fmt::Display;
use std::fmt::Formatter;
use std::ops::Index;

use super::edges::Edge;
use super::edges::EdgeBits;
use super::sides::Side;

type AllocEdge<S> = Edge<S, S>;
type SemiOwnedEdge<'a, S> = Edge<&'a str, S>;
#[allow(dead_code)]
type BorrowedEdge<'a> = Edge<&'a str, &'a str>;

#[derive(Default, Clone, PartialEq, Eq)]
pub struct TileEdges<'a, S: Borrow<str>> {
    pub top: SemiOwnedEdge<'a, S>,
    pub right: AllocEdge<S>,
    pub bottom: SemiOwnedEdge<'a, S>,
    pub left: AllocEdge<S>
}

impl<'a, S: Borrow<str>> TileEdges<'a, S> {
    #[allow(dead_code)]
    fn get_edge(&self, index: Side) -> BorrowedEdge {
        match index {
            Side::Top => Edge::to_borrowed(&self.top),
            Side::Right => Edge::to_borrowed(&self.right),
            Side::Bottom => Edge::to_borrowed(&self.bottom),
            Side::Left => Edge::to_borrowed(&self.left)
        }
    }
    pub fn index_edge(&self, index: Side, backwards: bool) -> &EdgeBits {
        match index {
            Side::Top => &self.top[backwards],
            Side::Right => &self.right[backwards],
            Side::Bottom => &self.bottom[backwards],
            Side::Left => &self.left[backwards]
        }
    }
}

impl<'a, 'b, S: Borrow<str>> Index<Side> for TileEdges<'a, S> {
    type Output = EdgeBits;
    fn index(&self, index: Side) -> &Self::Output {
        match index {
            Side::Top => &(self.top.forward.bits),
            Side::Right => &(self.right.forward.bits),
            Side::Bottom => &(self.bottom.forward.bits),
            Side::Left => &(self.left.forward.bits)
        }
    }
}

pub struct EdgeIterator<'a, S: Borrow<str>> {
    pub next_side: Side,
    complete: bool,
    edges: &'a TileEdges<'a, S>
}

impl<'a, S: Borrow<str>> Iterator for EdgeIterator<'a, S> {
    type Item = EdgeBits;
    fn next(&mut self) -> std::option::Option<Self::Item> {
        if self.complete {
            return None;
        }
        let next_side = self.next_side;
        let result = self.edges[next_side];
        if next_side != Side::Left {
            self.next_side += 1;
        } else {
            self.complete = true;
        }
        Some(result)
    }
}

impl<'a, S: Borrow<str>> IntoIterator for &'a TileEdges<'a, S> {
    type Item = EdgeBits;
    type IntoIter = EdgeIterator<'a, S>;
    fn into_iter(self) -> EdgeIterator<'a, S> {
        EdgeIterator {
            next_side: Side::Top,
            complete: false,
            edges: &self
        }
    }
}

impl<'a, S: Borrow<str>> Debug for TileEdges<'a, S> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        if fmt.alternate() {
            write!(fmt, "TileEdges {{
                top: {},
                right: {},
                bottom: {},
                left: {}
            }}", self.top, self.right, self.bottom, self.left)
        } else {
            write!(fmt, "Edges[^{} >{} v{} <{}]", self.top, self.right, self.bottom, self.left)
        }
    }
}

impl<'a, S: Borrow<str>> Display for TileEdges<'a, S> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        Debug::fmt(self, fmt)
    }
}

