use super::AdjacentPlacements;
use super::PlacementPositionIterator;

#[test]
fn test_new_adjacent_placements() {
    let sometimes = [None, Some(0u8), Some(1u8)];
    for up in sometimes.iter() {
        for left in sometimes.iter() {
            let ab = AdjacentPlacements::new(up.as_ref(), left.as_ref());
            assert!(ab.up() == up.as_ref(), "up does not match");
            assert!(ab.left() == left.as_ref(), "left does not match");
        }
    }
}

#[test]
fn test_from_slice() {
    let nums = [0u8, 1, 2, 3, 4, 5, 6, 7, 8];
    let slice: [Option<&u8>; 9] = {
        let mut slice = [Some(&0); 9];
        for (out, i) in slice.iter_mut().zip(nums.iter()) {
            *out = Some(i)
        }
        slice
    };
    let ups = [
        None,     None,     None,
        slice[0], slice[1], slice[2],
        slice[3], slice[4], slice[5]
    ];
    let lefts = [
        None, slice[0], slice[1],
        None, slice[3], slice[4],
        None, slice[6], slice[7]
    ];
    let positer = PlacementPositionIterator::new(3);
    for pos in positer {
        let posindex = pos.flat_position(3) as usize;
        let up = &ups[posindex];
        let left = lefts[posindex];
        let adjacents = AdjacentPlacements::from_slice(&slice, pos, 3);
        assert!(adjacents.up() == *up, "up sliced wrong");
        assert!(adjacents.left() == left, "left sliced wrong");
    }
}

