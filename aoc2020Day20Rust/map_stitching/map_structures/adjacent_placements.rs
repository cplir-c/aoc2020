
use super::placement_position::PlacementPosition;

pub struct AdjacentPlacements<'a, T> {
    up: Option<&'a T>,
    left: Option<&'a T>
}
impl<'a, T> AdjacentPlacements<'a, T> {
    pub fn new(up: Option<&'a T>, left: Option<&'a T>) -> Self {
        AdjacentPlacements {
            up, left
        }
    }
    pub fn from_slice(slice: &'a [Option<&'a T>], pos: PlacementPosition, side_length: u16) -> Self {
        let (slice, left): (&[Option<&T>], Option<&T>) = match pos.left(){
            Some(poss) => {
                let flatpos = poss.flat_position(side_length) as usize;
                let (slice, rest) = slice.split_at(flatpos);
                (slice, rest.first()
                    .and_then(|place_ref| *place_ref))
            },
            None => (slice, None)
        };
        let up: Option<&T> = pos.up().and_then(|poss|{
            let flatpos = poss.flat_position(side_length) as usize;
            slice.get(flatpos)
                .and_then(|place_ref| *place_ref)
        });
        
        AdjacentPlacements{up, left}
    }
    pub fn up(&self) -> Option<&'a T> {
        self.up
    }
    pub fn left(&self) -> Option<&'a T> {
        self.left
    }
}