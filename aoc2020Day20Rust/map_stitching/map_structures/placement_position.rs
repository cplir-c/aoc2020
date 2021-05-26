
use std::iter::FusedIterator;

use super::super::lib_square;

#[derive(Copy, Clone, Default, Eq, PartialEq)]
pub struct PlacementPosition {
    row: u16,
    col: u16
}

impl From<usize> for PlacementPosition {
    fn from(index: usize) -> Self {
        // save as divmod(index, 2)
        let mut x = lib_square::isqrt(index);
        let mut y = index - x * x;
        let m = x;
        if x <= y {
            x = 2 * x - y;
            y = m;
        }
        let x = x as u16;
        let y = y as u16;
        if m & 1 != 0 {
            PlacementPosition {
                row: x,
                col: y
            }
        } else {
            PlacementPosition {
                row: y,
                col: x
            }
        }
    }
}

impl PlacementPosition {
    pub fn flat_position(self, width: u16) -> usize {
        let width = width as usize;
        let row_pos = self.row as usize * width;
        self.col as usize + row_pos
    }
    fn pairing(self) -> usize {
        let x = self.col;
        let y = self.row;
        let m: usize = x.max(y).into();
        let d: usize = if m & 1 != 0 {x - y} else {y - x}.into();
        m * (m + 1) + d
    }
    pub fn up(self) -> Option<Self> {
        if self.row > 0 {
            Some(PlacementPosition{row: self.row - 1, ..self})
        } else {
            None
        }
    }
    pub fn left(self) -> Option<Self> {
        if self.col > 0 {
            Some(PlacementPosition{col: self.col - 1, ..self})
        } else {
            None
        }
    }
}

/// The shell enumeration of N X N where N = {0, 1, 2, ...}
/// https://oeis.org/A319514
pub struct PlacementPositionIterator {
    side_length: u16,
    previous: Option<PlacementPosition>,
    mode: Mode
}

#[derive(Eq, PartialEq, Copy, Clone)]
enum Mode {
    Left, Right, Up, Down
}

impl Default for Mode {
    fn default() -> Self {
        Mode::Left
    }
}

impl PlacementPositionIterator {
    pub fn new(side_length: u16) -> Self {
        PlacementPositionIterator {
            side_length,
            previous: None,
            mode: Mode::default()
        }
    }
    pub fn side_length(&self) -> u16 {
        self.side_length
    }
}

impl Default for PlacementPositionIterator {
    fn default() -> Self {
        Self::new(12)
    }
}

impl FusedIterator for PlacementPositionIterator {}
impl ExactSizeIterator for PlacementPositionIterator {
    fn len(&self) -> usize {
        let total_size = (self.side_length as usize).pow(2);
        match self.previous {
            None => total_size,
            Some(pos) => total_size - pos.pairing()
        }
    }
}
impl DoubleEndedIterator for PlacementPositionIterator {
    fn next_back(&mut self) -> Option<<Self as Iterator>::Item> {
        self.previous = self.previous.and_then(|pos|{
            let (x, y, mode) = (pos.col, pos.row, self.mode);
            
            let new_state = {
                if x == 0 && y == 0 {
                    self.mode = Mode::Left;
                    self.previous = None;
                    return None
                } else if x == 0 && mode == Mode::Left {
                    (x, y - 1, Mode::Right)
                } else if y == 0 && mode == Mode::Up {
                    (x - 1, y, Mode::Down)
                } else if x == y {
                    if mode == Mode::Right {
                        (x, y + 1, Mode::Up)
                    } else {
                        (x + 1, y, Mode::Left)
                    }
                } else {
                    match mode {
                        Mode::Left => (x + 1, y, mode),
                        Mode::Right => (x - 1, y, mode),
                        Mode::Up => (x, y + 1, mode),
                        Mode::Down => (x, y - 1, mode)
                    }
                }
            };
            
            self.mode = new_state.2;
            Some(PlacementPosition{
                row: new_state.1,
                col: new_state.0
            })
        });
        self.previous
    }
}
impl Iterator for PlacementPositionIterator {
    type Item = PlacementPosition;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        self.previous = match self.previous {
            None => Some(PlacementPosition::default()),
            Some(pos) => {
                let (x, y, mode) = (pos.col, pos.row, self.mode);
                let new_state = {
                    if x == 0 && mode == Mode::Left {
                        (x, y + 1, Mode::Right)
                    } else if y == 0 && mode == Mode::Up {
                        (x + 1, y, Mode::Down)
                    } else if x == y {
                        if mode == Mode::Right {
                            (x, y - 1, Mode::Up)
                        } else {
                            (x - 1, y, Mode::Left)
                        }
                    } else {
                        match mode {
                            Mode::Left => (x - 1, y, mode),
                            Mode::Right => (x + 1, y, mode),
                            Mode::Up => (x, y - 1, mode),
                            Mode::Down => (x, y + 1, mode)
                        }
                    }
                };
                if new_state.0 > self.side_length || new_state.1 > self.side_length {
                    return None;
                }
                
                self.mode = new_state.2;
                Some(PlacementPosition{
                    row: new_state.1,
                    col: new_state.0
                })
            }
        };
        self.previous
    }
}
