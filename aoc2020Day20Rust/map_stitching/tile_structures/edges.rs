use std::borrow::Borrow;
use std::ops::Index;

use super::super::lib_square::reverse_str;

pub type EdgeBits = u16;

#[derive(Debug, Default, Clone, PartialEq)]
pub struct DirectedEdge<T: Borrow<str>> {
    pub string : T,
    pub bits : EdgeBits
}

impl<T: Copy + Borrow<str>> Copy for DirectedEdge<T> {}
impl<T: Eq + Borrow<str>> Eq for DirectedEdge<T> {}

impl<T: Borrow<str>> DirectedEdge<T> {
    fn new(string: T) -> DirectedEdge<T> {
        let mut bits: EdgeBits = 0;
        Borrow::borrow(&string)
            .chars()
            .map(|chr| {chr == '#'})
            .for_each(|bit| {
                if bit {
                    bits |= 1;
                }
                bits <<= 1;
            });
        // undo the last shift
        bits >>= 1;
        DirectedEdge {
            string, bits
        }
    }
}

impl<T: Borrow<str>> From<DirectedEdge<T>> for EdgeBits {
    fn from(t: DirectedEdge<T>) -> Self {
        t.bits
    }
}

#[derive(Debug, Default, Clone, PartialEq)]
pub struct Edge<F: Borrow<str>, B: Borrow<str>> {
    pub forward: DirectedEdge<F>,
    pub backward: DirectedEdge<B>
}

impl<F: Copy + Borrow<str>, B: Copy + Borrow<str>> Copy for Edge<F, B> {}
impl<F: Eq + Borrow<str>, B: Eq + Borrow<str>> Eq for Edge<F, B> {}

impl<F: Borrow<str>, B: Borrow<str>> From<Edge<F, B>> for EdgeBits {
    fn from(edge: Edge<F, B>) -> Self {
        EdgeBits::from(edge.forward)
    }
}

impl<F: Borrow<str>, B: Borrow<str> + From<String>> From<F> for Edge<F, B> {
    fn from(forward: F) -> Edge<F, B> {
        let backward: B = reverse_str(Borrow::borrow(&forward));
        Edge::new(forward, backward)
    }
}

impl<F: Borrow<str>, B: Borrow<str>> Edge<F, B> {
    fn new(forward: F, backward: B) -> Edge<F, B> {
        Edge {
            forward: DirectedEdge::new(forward),
            backward: DirectedEdge::new(backward)
        }
    }
}

impl<F: Borrow<str>, B: Borrow<str>> Index<bool> for Edge<F, B> {
    type Output = EdgeBits;
    fn index(&self, backwards: bool) -> &<Self as std::ops::Index<bool>>::Output {
        if backwards {
            &self.backward.bits
        } else {
            &self.forward.bits
        }
    }
}
impl<F: Borrow<str>, B: Borrow<str>> Edge<F, B> {
    pub fn to_borrowed(&self) -> Edge<&str, &str> {
        Edge::<&str, &str> {
            forward: DirectedEdge {
                string: &Borrow::borrow(&self.forward.string),
                bits: self.forward.bits
            },
            backward: DirectedEdge {
                string: &Borrow::borrow(&self.backward.string),
                bits: self.backward.bits
            }
        }
    }
}