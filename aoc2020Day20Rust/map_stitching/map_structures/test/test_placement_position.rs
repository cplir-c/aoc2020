
use super::{PlacementPosition, PlacementPositionIterator};
use std::convert::TryInto;

fn good_forward_array() -> [PlacementPosition; 9] {
    [
        PlacementPosition {row: 0, col: 0},
        PlacementPosition {row: 1, col: 0},
        PlacementPosition {row: 1, col: 1},
        PlacementPosition {row: 0, col: 1},
        PlacementPosition {row: 0, col: 2},
        PlacementPosition {row: 1, col: 2},
        PlacementPosition {row: 2, col: 2},
        PlacementPosition {row: 2, col: 1},
        PlacementPosition {row: 2, col: 0}
    ]
}
fn good_backward_array() -> [PlacementPosition; 9] {
    [
        PlacementPosition {row: 2, col: 0},
        PlacementPosition {row: 2, col: 1},
        PlacementPosition {row: 2, col: 2},
        PlacementPosition {row: 1, col: 2},
        PlacementPosition {row: 0, col: 2},
        PlacementPosition {row: 0, col: 1},
        PlacementPosition {row: 1, col: 1},
        PlacementPosition {row: 1, col: 0},
        PlacementPosition {row: 0, col: 0}
    ]
}
#[test]
fn test_pairing() {
    for (i, p) in good_forward_array().iter().enumerate() {
        assert!(i == p.pairing() as usize, "failed to pair position {:?} to index {}", p, i);
    }
}
#[test]
fn test_unpairing() {
    for (i, p) in good_forward_array().iter().enumerate() {
        let i: u32 = i.try_into().expect("failed to convert unpairing index to u32");
        print!("converting i {} to position {:?}", i, p);
        let pos = PlacementPosition::from_paired(i);
        println!(", got {:?}", pos);
        //assert!(pos == *p, "failed to unpair index {} to position {:?}, got position {:?}", i, p, pos);
    }
}
#[test]
fn test_forward_iterator() {
    let it = PlacementPositionIterator::new(3);
    let good_result = good_forward_array();
    let mut good_it = good_result.iter();
    for test in it {
        let good = *good_it.next().expect("failed to end iteration");
        assert!(test == good, "test iter gave {:?}, but good iter gave {:?}", test, good);
    }
}
fn good_scan_array() -> [PlacementPosition; 9] {
    [
        PlacementPosition {row: 0, col: 0},
        PlacementPosition {row: 0, col: 1},
        PlacementPosition {row: 0, col: 2},
        PlacementPosition {row: 1, col: 0},
        PlacementPosition {row: 1, col: 1},
        PlacementPosition {row: 1, col: 2},
        PlacementPosition {row: 2, col: 0},
        PlacementPosition {row: 2, col: 1},
        PlacementPosition {row: 2, col: 2}
    ]
}
#[test]
fn test_flattening() {
    let scan_positions = good_scan_array();
    for (i, p) in scan_positions.iter().enumerate() {
        let i = i.try_into().expect("failed to convert scan index into u32");
        assert!(p.flat_position(3) == i, "failed to flatten position {:?} into {}", p, i);
    }
}
#[test]
fn test_unflattening() {
    let scan_positions = good_scan_array();
    for (i, p) in scan_positions.iter().enumerate() {
        let i = i.try_into().expect("failed to convert scan index into u32");
        assert!(PlacementPosition::from_flat(i, 3) == *p, "failed to unflatten {} into position {:?}", i, p);
    }
}
fn good_packing_array() -> [u32; 9] {
    [ //  row, col
        0x0000_0000,
        0x0001_0000,
        0x0001_0001,
        0x0000_0001,
        0x0000_0002,
        0x0001_0002,
        0x0002_0002,
        0x0002_0001,
        0x0002_0000
    ]
}
#[test]
fn test_packing() {
    let packing_array = good_packing_array();
    let forward_array = good_forward_array();
    for (packed, forward) in packing_array.iter().zip(forward_array) {
        assert!(forward.pack() == *packed, "failed to bitpack position {:?} to {:#010x}, got {:#010x}", forward, packed, forward.pack());
    }
}
#[test]
fn test_unpacking() {
    let packing_array = good_packing_array();
    let forward_array = good_forward_array();
    for (packed, forward) in packing_array.iter().zip(forward_array) {
        assert!(PlacementPosition::unpack(*packed) == forward, "failed to unpack packed position {:#010x} to {:?}", packed, forward);
    }
}
#[test]
fn test_up() {
    let good_result = [
        None, None, None,
        Some(PlacementPosition {row: 0, col: 0}), Some(PlacementPosition {row: 0, col: 1}), Some(PlacementPosition {row: 0, col: 2}),
        Some(PlacementPosition {row: 1, col: 0}), Some(PlacementPosition {row: 1, col: 1}), Some(PlacementPosition {row: 1, col: 2})
    ];
    for (up, forward) in good_result.iter().zip(good_scan_array()) {
        assert!(forward.up() == *up, "failed to find the placement up from {:?}, {:?}", forward, up);
    }
}
#[test]
fn test_left() {
    let good_result = [
        None, Some(PlacementPosition {row: 0, col: 0}), Some(PlacementPosition {row: 0, col: 1}),
        None, Some(PlacementPosition {row: 1, col: 0}), Some(PlacementPosition {row: 1, col: 1}),
        None, Some(PlacementPosition {row: 2, col: 0}), Some(PlacementPosition {row: 2, col: 1})
    ];
    for (left, forward) in good_result.iter().zip(good_scan_array()) {
        assert!(forward.left() == *left, "failed to find the placement left from {:?}, should be {:?}, got {:?}", forward, left, forward.left());
    }
}

#[test]
fn test_shell() {
	let good_shells = [0, 1, 1, 1, 2, 2, 2, 2, 2];
	for (position, shell) in good_forward_array().iter().zip(good_shells) {
		assert!(position.shell() == shell, "failed to");
	}
}

#[test]
fn test_iterator_length() {
    let mut it = PlacementPositionIterator::new(3);
    let mut i = 3 * 3;
    loop {
        eprintln!("it len {}, good len {}", it.len(), i);
        assert!(i == it.len(), "\nmismatched placement iterator length: {} but should be {}", it.len(), i);
        if it.next().is_none() {
            break;
        }
        i -= 1;
    }
}

#[test]
fn test_backward_iterator() {
    let mut it = PlacementPositionIterator::new(3);
    let good_result = good_backward_array();
    for _ in 0..9 {
        it.next().expect("found null placement");
    }
    assert!(it.next() == None);
    let mut good_it = good_result.iter();
    it.rfold((), |_unit, item|{
        let good_item = *good_it.next().unwrap();
        if item != good_item {
            panic!("item {:?} != good item {:?}", item, good_item);
        }
    });
}
