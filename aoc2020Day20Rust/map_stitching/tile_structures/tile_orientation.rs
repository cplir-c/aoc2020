
use super::Side;
use super::SideIterator;

#[derive(Debug, Default, Copy, Clone, PartialEq, Eq)]
pub struct TileOrientation {
    pub top_side: Side,
    pub top_flipped: bool
}

use std::fmt;
use std::fmt::Write;

use super::super::lib_square::SquareFormat;
use super::super::lib_square::write_line;
use super::super::lib_square::write_line_backwards;
use super::super::lib_square::write_left_char;
use super::super::lib_square::write_right_char;

impl TileOrientation {
    pub fn format(self, out: &mut impl Write, string: &str) -> fmt::Result {
         match self {
            TileOrientation{top_side: Side::Top, top_flipped: false} => { out.write_str(string) },
            TileOrientation{top_side: Side::Top, top_flipped: true} => { // read rows backward: right to left, top down
                SquareFormat(out).write_lines_down(string, write_line)
            },
            TileOrientation{top_side: Side::Right, top_flipped: false} => { // read columns: top down, right to left
                SquareFormat(out).write_columns_down(string, write_right_char)
            },
            TileOrientation{top_side: Side::Right, top_flipped: true} => { // read columns: top down, left to right
                SquareFormat(out).write_columns_down(string, write_left_char)
            },
            TileOrientation{top_side: Side::Bottom, top_flipped: false} => { // read rows backward: right to left, bottom up
                SquareFormat(out).write_lines_up(string, write_line_backwards)
            },
            TileOrientation{top_side: Side::Bottom, top_flipped: true} => { // read rows: left to right, bottom up
                SquareFormat(out).write_lines_up(string, write_line)
            },
            TileOrientation{top_side: Side::Left, top_flipped: false} => { // read columns: bottom up, left to right
                SquareFormat(out).write_columns_up(string, write_left_char)
            },
            TileOrientation{top_side: Side::Left, top_flipped: true} => { // read columns: bottom up, right to left
                SquareFormat(out).write_columns_up(string, write_right_char)
            }
        }
    }
}

#[derive(Debug, Copy, Clone)]
pub struct TileOrientationIterator (Option<TileOrientation>);

impl Default for TileOrientationIterator {
    fn default() -> Self {
        TileOrientationIterator(Some(TileOrientation::default()))
    }
}

impl IntoIterator for TileOrientation {
    type Item = TileOrientation;
    type IntoIter = TileOrientationIterator;
    fn into_iter(self) -> TileOrientationIterator {
        TileOrientationIterator (Some(self))
    }
}

impl Iterator for TileOrientationIterator {
    type Item = TileOrientation;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        let current = self.0;
        self.0 = self.0.and_then(|orientation| match orientation {
            // flip side if not flipped
            TileOrientation{top_side: side, top_flipped: false}
                => Some(TileOrientation{top_side: side, top_flipped: true}),
            // Turn the tile more if possible, otherwise put None
            TileOrientation{top_side: side, top_flipped: true}
                => SideIterator(Some(side)).next_side().map(|option_side|{
                    TileOrientation {top_side: option_side, top_flipped: false}
                })
        });
        current
    }
}