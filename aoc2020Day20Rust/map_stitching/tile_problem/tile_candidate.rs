
use std::borrow::Borrow;
use std::cell::Cell;
use std::fmt;
use std::fmt::Debug;
use std::fmt::Formatter;

use super::EdgeReference;
use super::Tile;

pub enum TileCandidate<'a, 'b, S: Borrow<str>> {
    RootTiles(Cell<&'b [Tile<'a, S>]>),
    LeafTiles(Cell<&'b [EdgeReference<'a, S>]>),
}

impl<'a, 'b, S: Borrow<str>> Debug for TileCandidate<'a, 'b, S> {
    fn fmt(&self, fmt: &mut Formatter) -> fmt::Result {
        fn help_debug(fmt: &mut Formatter, variant: &str, d: impl Debug) -> fmt::Result {
            if fmt.alternate() {
                write!(fmt, "TileCandidate::{}(
                    {:#?}
                )", variant, d)
            } else {
                write!(fmt, "TileCandidate::{}({:?})", variant, d)
            }
        }
        match self {
            TileCandidate::RootTiles(tiles) => {
                help_debug(fmt, "RootTiles", tiles)
            },
            TileCandidate::LeafTiles(places) => {
                help_debug(fmt, "LeafTiles", places)
            }
        }
        
    }
}
impl<'a, 'b, S: Borrow<str>> TileCandidate<'a, 'b, S> {
    pub fn is_empty(&self) -> bool {
        match self {
            TileCandidate::RootTiles(tiles) => tiles.get().is_empty(),
            TileCandidate::LeafTiles(places) => places.get().as_ref().is_empty(),
        }
    }
}
impl<'a, 'b, S: Borrow<str>> Clone for TileCandidate<'a, 'b, S> {
    fn clone(&self) -> Self {
        match self {
            TileCandidate::RootTiles(tiles) => TileCandidate::RootTiles(Cell::new(tiles.get())),
            TileCandidate::LeafTiles(places) => TileCandidate::LeafTiles(Cell::new(places.get())),
        }
    }
}
//impl<'a, 'b, S: Borrow<str>>
