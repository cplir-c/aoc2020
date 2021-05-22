
use std::borrow::Borrow;
use std::clone::Clone;
use std::fmt;
use std::fmt::Display;
use std::iter::IntoIterator;
use std::ops::Index;

use super::super::lib_square::SquareFormat;
use super::super::lib_square::write_line;
use super::super::lib_square::write_line_backwards;
use super::super::lib_square::write_left_char;
use super::super::lib_square::write_right_char;

use super::tile_orientation::TileOrientation;
use super::tile_orientation::TileOrientationIterator;

use super::tile::Tile;

use super::sides::Side;
use super::sides::SideIterator;

use super::edges::EdgeBits;

use super::edge_placement::EdgePlacement;
use super::edge_placement::EdgeReference;
use super::edge_placement::PlacementEdgePlacementIterator;
use super::edge_placement::PlacementEdgeReferenceIterator;

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct TilePlacement<'a, 'b, S: Borrow<str>> {
    pub orientation: TileOrientation,
    pub tile: &'b Tile<'a, S>
}

impl<'a, 'b, S: Borrow<str> + Clone> Copy for TilePlacement<'a, 'b, S> {}

pub struct TilePlacementIterator<'a, 'b, S: Borrow<str>> {
    tile: &'b Tile<'a, S>,
    orientation_iter: TileOrientationIterator
}

impl<'a, 'b, S: Borrow<str>> IntoIterator for &'b Tile<'a, S> {
    type Item = TilePlacement<'a, 'b, S>;
    type IntoIter = TilePlacementIterator<'a, 'b, S>;
    fn into_iter(self) -> <Self as IntoIterator>::IntoIter {
        TilePlacementIterator {
            tile: self,
            orientation_iter: TileOrientationIterator::default()
        }
    }
}

impl<'a, 'b, S: Borrow<str>> Iterator for TilePlacementIterator<'a, 'b, S> {
    type Item = TilePlacement<'a, 'b, S>;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        self.orientation_iter.next().map(|orientation|{
            TilePlacement {
                tile: self.tile,
                orientation
            }
        })
    }
}

impl<'a, 'b, S: Borrow<str>> Display for TilePlacement<'a, 'b, S> {
    fn fmt<'c>(&'c self, formatter: &'c mut fmt::Formatter) -> fmt::Result {
        /* Fw -> Forward
         * Bw -> Backward
         * 
         * rotation
         * Fw,Top -> No copies
         * 12
         * 34
         * 
         * Bw,Top -> read rows backward: right to left, top down
         * 21
         * 43
         * 
         * Fw, Right -> read columns: top down, right to left
         * 24
         * 13
         * 
         * Bw, Right -> read columns: top down, left to right
         * 13
         * 24
         * 
         * Fw, Bottom -> read rows backward: right to left, bottom up
         * 43
         * 21
         * 
         * Bw, Bottom -> read rows: left to right, bottom up
         * 34
         * 12
         * 
         * Fw, Left -> read columns: bottom up, left to right
         * 31
         * 42
         * 
         * Bw, Left -> read columns: bottom up, right to left
         * 42
         * 31
         */
        let tile_body = self.tile.body_string;
        match self.orientation {
            TileOrientation{top_side: Side::Top, top_flipped: false} => { formatter.write_str(tile_body) },
            TileOrientation{top_side: Side::Top, top_flipped: true} => { // read rows backward: right to left, top down
                SquareFormat(formatter).write_lines_down(tile_body, write_line)
            },
            TileOrientation{top_side: Side::Right, top_flipped: false} => { // read columns: top down, right to left
                SquareFormat(formatter).write_columns_down(tile_body, write_right_char)
            },
            TileOrientation{top_side: Side::Right, top_flipped: true} => { // read columns: top down, left to right
                SquareFormat(formatter).write_columns_down(tile_body, write_left_char)
            },
            TileOrientation{top_side: Side::Bottom, top_flipped: false} => { // read rows backward: right to left, bottom up
                SquareFormat(formatter).write_lines_up(tile_body, write_line_backwards)
            },
            TileOrientation{top_side: Side::Bottom, top_flipped: true} => { // read rows: left to right, bottom up
                SquareFormat(formatter).write_lines_up(tile_body, write_line)
            },
            TileOrientation{top_side: Side::Left, top_flipped: false} => { // read columns: bottom up, left to right
                SquareFormat(formatter).write_columns_up(tile_body, write_left_char)
            },
            TileOrientation{top_side: Side::Left, top_flipped: true} => { // read columns: bottom up, right to left
                SquareFormat(formatter).write_columns_up(tile_body, write_right_char)
            }
        }
    }
}

impl<'a, 'b, S: Borrow<str>> Index<Side> for TilePlacement<'a, 'b, S> {
    type Output = EdgeBits;
    fn index(&self, index: Side) -> &Self::Output {
        /* Fw -> Forward
         * Bw -> Backward
         * 
         * rotation|^edge|edge orientation
         * Bw,Top -> 21 -> Top,Bw
         * 21
         * 43
         * 
         * Bw,Right -> 13 -> Left,Fw
         * 13
         * 24
         * 
         * Bw,Bottom -> 34 -> Bottom,Fw
         * 34
         * 12
         * 
         * Bw,Left -> 42 -> Right,Bw
         * 42
         * 31
         * 
         * Fw,Top -> 12 -> Top,Fw
         * 12
         * 34
         * 
         * Fw,Right -> 24 -> Right,Fw
         * 24
         * 13
         * 
         * Fw,Bottom -> 43 -> Bottom,Bw
         * 43
         * 21
         * 
         * Fw,Left -> 31 -> Left,Bw
         * 31
         * 42
         */
        let getting_tuple: (Side, bool);
        if self.orientation.top_flipped {
            getting_tuple = match index {
                Side::Top => (Side::Top, true),
                Side::Right => (Side::Left, false),
                Side::Bottom => (Side::Bottom, false),
                Side::Left => (Side::Right, true)
            };
        } else {
            getting_tuple = match index {
                Side::Top => (Side::Top, false),
                Side::Right => (Side::Right, false),
                Side::Bottom => (Side::Bottom, true),
                Side::Left => (Side::Left, true)
            };
        }
        let (get_side, get_backwards) = getting_tuple;
        self.tile.edges.index_edge(get_side, get_backwards)
    }
}

impl<'a, 'b, S: Borrow<str> + Clone> TilePlacement<'a, 'b, S> {
    pub fn get_edge_ref(&self, side: Side) -> EdgeReference<'a, 'b, S>{
        EdgeReference {
            side,
            placement: *self
        }
    }
}

impl<'a, 'b, S: Borrow<str>> TilePlacement<'a, 'b, S> {
    pub fn iter_edge_refs(&self) -> PlacementEdgeReferenceIterator<S> {
        PlacementEdgeReferenceIterator {
            side_iter: SideIterator::default(),
            this: &self
        }
    }
    pub fn get_edge_placement(&self, side: Side) -> EdgePlacement {
        EdgePlacement {
            side,
            bits: self[side]
        }
    }
    pub fn iter_edge_placements(&self) -> PlacementEdgePlacementIterator<S> {
        PlacementEdgePlacementIterator {
            side_iter: SideIterator::default(),
            this: &self
        }
    }
}