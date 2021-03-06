
use std::iter::FusedIterator;
use std::cmp::Ordering;

use super::super::lib_square;

/// represents a 2d array index
#[derive(Copy, Clone, Default, Eq, PartialEq, Debug)]
pub struct PlacementPosition {
    row: u16,
    col: u16
}

impl PlacementPosition {
    fn pairing(self) -> u32 {
        let x = self.col;
        let y = self.row;
        let m = x.max(y) as u32;
        let msq = m * (m + 1);
        let (x, y) = (x as i32, y as i32);
        let i = if m & 1 == 0 { // m is even
            y - x
        } else {
            x - y
        };
        //println!("u32 msq{}, i32 i{}", msq, i);
        let i = i + msq as i32;
        i as u32
    }
    fn from_paired(combined_index: u32) -> Self {
        // combined index:
		// 0 3 4 f
		// 1 2 5 e
		// 8 7 6 d
		// 9 a b c
        // shell layer:
		// 0 1 2 3
		// 1 1 2 3
		// 2 2 2 3
		// 3 3 3 3
        let shell_layer = lib_square::isqrt(combined_index);
        // inner area:
		// 0 1 4 9
		// 1 1 4 9
		// 4 4 4 9
		// 9 9 9 9
        let inner_area = shell_layer * shell_layer;
        // surface index:
		// 0 2 0 6
		// 0 1 1 5
		// 4 3 2 4
		// 0 1 2 3
        let surface_index = combined_index - inner_area;
        let shell_layer = shell_layer as u16;
        let surface_index = surface_index as u16;
        // (x y) should be:
		// (0 0) (1 0) (2 0) (3 0)
		// (0 1) (1 1) (2 1) (3 1)
		// (0 2) (1 2) (2 2) (3 2)
		// (0 3) (1 3) (2 3) (3 3)
        let (x, y): (u16, u16);
        // test for even layer:
		// evn odd evn odd
		// odd odd evn odd
		// evn evn evn odd
		// odd odd odd odd
        if (shell_layer & 1) == 0 {
            // even layers move clockwise
            // test for surface index before the corner:
			// bfor null bfor null
			// null null bfor null
			// aftr aftr bfor null
			// null null null null
            if surface_index <= shell_layer {
                // at or before the corner, going clockwise
				// so upper triangle
                // X:
                // 0 - 2 -
                // - - 2 -
				// - - 2 -
				// - - - -
                x = shell_layer;
				// Y:
                // 0 - 0 -
				// - - 1 -
				// - - 2 -
				// - - - -
                y = surface_index;
            } else {
				// after the corner, going clockwise
				// so lower triangle
                // X:
                // - - - -
				// - - - -
				// 0 1 - -
				// - - - -
				print!("\nshell layer {}, surface index {}, combined index {}\n", shell_layer, surface_index, combined_index);
                x = (shell_layer << 1) - surface_index;
                // Y:
				// - - - -
				// - - - -
				// 2 2 - -
				// - - - -
                y = shell_layer;
            }
        } else {
            // odd layers move counterclockwise
            // test for surface index before corner:
			// null aftr null aftr
			// bfor bfor null aftr
			// null null null aftr
			// bfor bfor bfor bfor
            if surface_index <= shell_layer{
                // before the corner, moving counterclockwise
				// so lower triangle
                // X:
				// - - - -
				// 0 1 - -
				// - - - -
				// 0 1 2 3
                x = surface_index;
                // Y:
				// - - - -
				// 1 1 - -
				// - - - -
				// 3 3 3 3
                y = shell_layer;
            } else {
                // after the corner, moving counterclockwise
				// so upper triangle
                // X:
				// - 1 - 3
				// - - - 3
				// - - - 3
				// - - - -
                x = shell_layer;
				// Y:
                // - 0 - 0
				// - - - 1
				// - - - 2
				// - - - -
                y = (shell_layer << 1) - surface_index;
            }
        }
        PlacementPosition{ row: y, col: x }
    }
    pub fn flat_position(self, width: u16) -> u32 {
        let width = width as u32;
        let row_pos = self.row as u32 * width;
        self.col as u32 + row_pos
    }
    fn from_flat(index: u32, side_length: u16) -> Self {
        PlacementPosition {
            row: (index as u32 / side_length as u32) as u16,
            col: (index as u32 % side_length as u32) as u16
        }
    }
    fn pack(self) -> u32 {
        ((self.row as u32) << 16) | self.col as u32
    }
    fn unpack(pack: u32) -> Self {
        PlacementPosition {
            row: (pack >> 16) as u16,
            col: (pack & 0xff_ff) as u16
        }
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
    pub fn shell(self) -> u16 {
        self.row.max(self.col)
    }
}

impl Ord for PlacementPosition {
    fn cmp(&self, other: &Self) -> Ordering {
        if self == other {
            return Ordering::Equal;
        }
        match self.shell().cmp(&other.shell()) {
            Ordering::Equal => {
                self.pairing().cmp(&other.pairing())
            },
            order => order
        }
    }
}
impl PartialOrd for PlacementPosition {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

/// The shell enumeration of N X N where N = {0, 1, 2, ...}
/// https://oeis.org/A319514
#[derive(Copy, Clone, Eq, PartialEq, Debug)]
pub struct PlacementPositionIterator {
    side_length: u16,
    previous: Option<PlacementPosition>,
    mode: Mode
}

#[derive(Eq, PartialEq, Copy, Clone, Debug)]
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
    pub fn current_position(&self) -> Option<PlacementPosition> {
        self.previous
    }
    pub fn peek_back(&self) -> Option<PlacementPosition> {
        self._peek_back().map(|state| {
            PlacementPosition {
                row: state.1, // Y
                col: state.0  // X
            }
        })
    }
    fn _peek_back(&self) -> Option<(u16, u16, Mode)> {
        self.previous.and_then(|pos|{
            let (x, y, mode) = (pos.col, pos.row, self.mode);
            
            let new_state = {
                if x == 0 && y == 0 {
                    return None;
                } else if x == 0 && mode == REV_LEFT {
                    (x, y - 1, REV_RIGHT)
                } else if y == 0 && mode == REV_UP {
                    (x - 1, y, REV_DOWN)
                } else if x == y {
                    if mode == REV_RIGHT {
                        (x, y - 1, REV_UP)
                    } else { // going REV_DOWN
                        (x - 1, y, REV_LEFT)
                    }
                } else {
                    match mode {
                        REV_LEFT => (x - 1, y, mode),
                        REV_RIGHT => (x + 1, y, mode),
                        REV_UP => (x, y - 1, mode),
                        REV_DOWN => (x, y + 1, mode)
                    }
                }
            };
            
            Some(new_state)
        })
    }
    fn peek(&self) -> Option<(u16, u16, Mode)> {
        match self.previous {
            None => {
                let pos = PlacementPosition::default();
                Some((pos.col, pos.row, Mode::default()))
            },
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
                if new_state.0 >= self.side_length || new_state.1 >= self.side_length {
                    None
                } else {
                    Some(new_state)
                }
            }
        }
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
            Some(pos) => total_size - pos.pairing() as usize - 1
        }
    }
}

const REV_LEFT: Mode = Mode::Right;
const REV_RIGHT: Mode = Mode::Left;
const REV_UP: Mode = Mode::Down;
const REV_DOWN: Mode = Mode::Up;

impl DoubleEndedIterator for PlacementPositionIterator {
    fn next_back(&mut self) -> Option<<Self as Iterator>::Item> {
        let old: Option<PlacementPosition> = self.previous;
        self.previous = self._peek_back().map(|new_state|{
            self.mode = new_state.2;
            PlacementPosition {
                row: new_state.1,
                col: new_state.0
            }
        });
        old
    }
}
impl Iterator for PlacementPositionIterator {
    type Item = PlacementPosition;
    fn next(&mut self) -> Option<<Self as Iterator>::Item> {
        self.previous = self.peek().map(|new_state|{
            self.mode = new_state.2;
            PlacementPosition {
                row: new_state.1,
                col: new_state.0
            }
        });
        self.previous
    }
}

#[cfg(test)]
#[path="./test/test_placement_position.rs"]
mod test_placement_position;

