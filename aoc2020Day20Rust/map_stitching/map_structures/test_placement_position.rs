
use super::{PlacementPosition, PlacementPositionIterator};

#[test]
fn test_iterator() {
    let it = PlacementPositionIterator::new(3);
    let good_result = [
        PlacementPosition {row: 0, col: 0},
        PlacementPosition {row: 1, col: 0},
        PlacementPosition {row: 1, col: 1},
        PlacementPosition {row: 0, col: 1},
        PlacementPosition {row: 0, col: 2},
        PlacementPosition {row: 1, col: 2},
        PlacementPosition {row: 2, col: 2},
        PlacementPosition {row: 2, col: 1},
        PlacementPosition {row: 2, col: 0}
    ];
    let mut good_it = good_result.iter();
    for a in it {
        let b = *good_it.next().expect("failed to end iteration");
        if a != b {
            panic!("a {:?} != b {:?}", a, b);
        }
    }
}

#[test]
fn test_backward_iterator() {
    let mut it = PlacementPositionIterator::new(3);
    let good_result = [
        PlacementPosition {row: 2, col: 0},
        PlacementPosition {row: 2, col: 1},
        PlacementPosition {row: 2, col: 2},
        PlacementPosition {row: 1, col: 2},
        PlacementPosition {row: 0, col: 2},
        PlacementPosition {row: 0, col: 1},
        PlacementPosition {row: 1, col: 1},
        PlacementPosition {row: 1, col: 0},
        PlacementPosition {row: 0, col: 0}
    ];
    for _ in 0..9 {
        it.next().expect("found null placement");
    }
    assert!(it.next() == None);
    let mut good_it = good_result.iter();
    it.rfold((), |_unit, item|{
        let good_item = *good_it.next().unwrap();
        if item != good_item {
            print!("item {:?} != good item {:?}", item, good_item);
            panic!();
        }
    });
}
