

use std::ops::AddAssign;

#[derive(Debug, Eq, PartialEq, Copy, Clone, Hash)]
pub enum Side {
    Top, Right, Bottom, Left
}

#[derive(Debug, Copy, Clone)]
pub struct SideIterator(pub Option<Side>);

impl SideIterator {
    pub fn next_side(self) -> Option<Side> {
        Some(match self.0 {
            None => Side::Top,
            Some(Side::Top) => Side::Right,
            Some(Side::Right) => Side::Bottom,
            Some(Side::Bottom) => Side::Left,
            Some(Side::Left) => { return None; }
        })
    }
}

impl Default for SideIterator {
    fn default() -> SideIterator {
        SideIterator(None)
    }
}

impl IntoIterator for Side {
    type Item = Side;
    type IntoIter = SideIterator;
    fn into_iter(self) -> Self::IntoIter {
        SideIterator(None)
    }
}

impl Iterator for SideIterator {
    type Item = Side;
    fn next(&mut self) -> Option<Self::Item> {
        self.0 = self.next_side();
        self.0
    }
}

impl Default for Side {
    fn default() -> Side {
        Side::Top
    }
}

impl AddAssign<Side> for Side {
    fn add_assign(&mut self, side: Side) {
        self.add_assign(i8::from(side));
    }
}

impl AddAssign<i8> for Side {
    fn add_assign(&mut self, int: i8) {
        *self = Side::from(int.wrapping_add(i8::from(*self)));
    }
}

impl From<i8> for Side {
    fn from(int: i8) -> Side {
        match int & 0b11 {
            0 => Side::Top,
            1 => Side::Right,
            2 => Side::Bottom,
            _ => Side::Left
        }
    }
}

impl From<Side> for i8 {
    fn from(side: Side) -> i8 {
        match side {
            Side::Top => 0,
            Side::Right => 1,
            Side::Bottom => 2,
            Side::Left => 3
        }
    }
}