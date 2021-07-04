
use super::PlacementMap;
use super::SquareMapFullError;

#[test]
fn test_placement_map_construction() {
    for i in 0..=16 {
        let map = PlacementMap::<u8>::new(i);
        assert!(map.placements.len() == i.pow(2).into(),
            "map placements size was {} but should be {}", map.placements.len(), i.pow(2));
        for (i, optn) in map.placements.iter().enumerate() {
            assert!(optn.is_none(),
                "element {} of map placements was {:?}", i, optn);
        }
        assert!(map.peek_cache.is_none());
        assert!(map.positions.len() == i.pow(2).into(),
            "map positions len {}, target len {}", map.positions.len(), i * i);
    }
}
#[test]
fn test_placement_map_side_length(){
    for i in 0..=4 {
        let side_len = 1 << i;
        let map = PlacementMap::<usize>::new(side_len);
        assert!(map.side_length() == side_len, "wrong side length");
    }
}
#[test]
fn test_placement_map_capacity(){
    for i in 0..=4 {
        let side_len = 1 << i;
        let map = PlacementMap::<usize>::new(side_len);
        let capacity = (side_len as usize) << i;
        assert!(map.capacity() == capacity, "wrong total length");
    }
}
#[test]
fn test_clear_placements() {
    let mut map = PlacementMap::<u8>::new(3);
    let ints = (0..9).collect::<Vec<u8>>();
    for j in ints.iter() {
        for i in ints[..*j as usize].iter() {
            map.push(i).expect("failed to push int");
        }
        map.clear();
        assert!(map.len() == 0);
    }
}

#[test]
fn test_placement_map_insertion() -> Result<(), SquareMapFullError> {
    let mut map = PlacementMap::<u8>::new(3);
    let ints = (1..=9).collect::<Vec<u8>>();
    for uint in ints.iter() {
        map.push(uint)?;
    }
    let shaped_ints: &[u8; 9] = &[
        1, 4, 5,
        2, 3, 6,
        9, 8, 7
    ];
    for (uint, good_int) in map.placements.iter().zip(shaped_ints) {
        assert!(uint == &Some(good_int), "strr {:?}, good str {}", uint, good_int);
    }
    Ok(())
}

#[test]
fn test_placement_map_removal() {
    let mut map = PlacementMap::<u8>::new(3);
    let ints = (1..=9).collect::<Vec<u8>>();
    for i in ints.iter() {
        map.push(i).expect("failed to push int");
    }
    for i in (1..=9).rev() {
        assert!(map.pop() == Some(&i));
    }
    for option in map.placements.iter() {
        assert!(option.is_none());
    }
}

#[test]
fn test_placement_len() {
    let mut map = PlacementMap::<u8>::new(3);
    let ints = (1..=9).collect::<Vec<u8>>();
    for i in ints.iter() {
        map.push(i).expect("failed to push int");
        println!("{}, {} vs {}", map.len() == (*i).into(), map.len(), i);
        assert!(map.len() == (*i).into());
    }
    for i in (1..=9).rev() {
        println!("{}, {} vs {}", map.len() == i.into()
            && map.pop() == Some(&i), map.len(), i);
    }
    assert!(map.len() == 0);
}