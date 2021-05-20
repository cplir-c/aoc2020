
use std::borrow::Borrow;
use std::collections::HashSet;
mod tile_structures;
use tile_structures::*;
mod lib_square;
use lib_square::*;

fn gather_errors<T>(accum: Result<Vec<T>, String>, item: Result<T, String>) -> Result<Vec<T>, String> {
    match (item, accum) {
        (Ok(itm), Ok(mut vec)) => {vec.push(itm); Ok(vec)},
        (Ok(_), Err(_errs)) => Err(_errs),
        (Err(err), Ok(_)) => Err(err),
        (Err(err), Err(mut errs)) => {
            errs.push('\n');
            errs += &err;
            Err(errs)
        }
    }
}

pub fn find_corner_ID_product(tiles_string: &'static str) -> Result<u64, String> {
    let tile_count = tiles_string.split("\n\n").count();
    println!("tile count: {}", tile_count);
    // collect tile strings
    let tiles_strings: Vec<&'static str> = {
        let mut tiles_strings: Vec<&str> = Vec::with_capacity(tile_count);
        tiles_strings.extend(tiles_string.split("\n\n"));
        tiles_strings
    };
    // parse tiles
    type BoxStr = Box<str>;
    let tiles_result: Result<Vec<Tile<BoxStr>>, String> = tiles_strings.iter()
        .map(|string| {Tile::parse(*string)})
        .fold(Result::Ok(Vec::with_capacity(tile_count)), gather_errors);
    // return errors if tile parsing failed
    let tiles: Vec<Tile<BoxStr>> = match tiles_result {
        Ok(tiles) => tiles,
        Err(errs) => { return Err(format!("Errors while parsing tiles:\n{}", errs)); }
    };
    if tiles[0].into_iter().take(10).count() != 8 {
        println!("{}", TileOrientationIterator::default()
                .take(10)
                .map(|orientation|{
                    format!("{:#?},\n", orientation)
                }).collect::<String>());
        return Err("broken tile placement iterator".to_string());
    }
    let all_placements: Vec<TilePlacement<BoxStr>> = tiles.iter().flat_map(|tile|{tile.into_iter()}).collect();
    if all_placements.is_empty() {
        return Err("Error: empty All Placements vector".to_string())
    }
    let edge_map = {
        let mut edge_map = EdgeMap::with_capacity(8 * tile_count);
        build_edge_map(&all_placements, &mut edge_map);
        edge_map
    };
    if all_placements.is_empty() {
        return Err("Error: empty All Placements vector after edge map built".to_string());
    }
    
    println!("build data structures, calculating tile map...");
    let corner_ID_product = piece_together_map(&tiles, &edge_map);
    match corner_ID_product {
        Some(product) => Ok(product),
        None => Err(format!("failed to build map with edges {:?}...\nplacement map:\n{}", edge_map, MapDisplay(all_placements.as_slice())))
    }
}

fn get_corner_product<S: Borrow<str>>(map_edge_length: usize, placements: &[TilePlacement<S>]) -> u64 {
    println!("map placements:");
    for placement in placements {
        println!("{}\n", placement);
    }
    print!("Map pieced together:\n{}", MapDisplay(placements));
    let tile_count = map_edge_length * map_edge_length;
    let mut result: u64 = placements[0].tile.tile_ID as u64;
    result *= placements[map_edge_length - 1].tile.tile_ID as u64;
    result *= placements[tile_count - 1].tile.tile_ID as u64;
    result *= placements[tile_count - map_edge_length].tile.tile_ID as u64;
    result
}

fn piece_together_map<'a, S: Borrow<str> + Clone>(tiles: &[Tile<'a, S>], edge_map: &'a EdgeMap<'a, 'a, S>) -> Option<u64> {
    let tile_count: usize = tiles.len();
    println!("tile count {:?}", tile_count);
    let edge_length = isqrt(tile_count);
    print!("tile count: {}, map side length: {}", tile_count, edge_length);
    let mut placements = Vec::<TilePlacement<S>>::with_capacity(tile_count);
    let mut placed_tile_IDs = HashSet::<EdgeBits>::with_capacity(tile_count);
    let mut edge_reference_slices = Vec::<&[EdgeReference<S>]>::with_capacity(tile_count);
    
    edge_reference_slices.push(&[]);
    for tile in tiles {
        placed_tile_IDs.insert(tile.tile_ID);
        for placement in tile {
            placements.push(placement);
            
            let success: bool = backtrack_stitching(tile_count, edge_length, edge_map,
                &mut placements, &mut placed_tile_IDs, &mut edge_reference_slices);
            if success {
                return Some(get_corner_product(edge_length, placements.as_slice()));
            }
            
            placements.pop();
        }
        placed_tile_IDs.remove(&tile.tile_ID);
    }
    None
}

/// Backtrack stitching
/// ------------------
/// This function takes the precalculated tile data
/// and the currently single placed tile data and tries to find
/// a solution to edge matching by backtracking 
/// 
fn backtrack_stitching<'a, 'b, S: Borrow<str> + Clone>(tile_count: usize, map_side_length: usize, edge_map: &'a EdgeMap<'a, 'a, S>,
                      placements: &'b mut Vec<TilePlacement<'a, 'a, S>>, placed_tile_IDs: &'b mut HashSet<EdgeBits>,
                      edge_reference_slices: &'b mut Vec<&'a [EdgeReference<'a, 'a, S>]>) -> bool {
    loop {
        println!("stack depth: {}", placements.len());
        // this block truncates the edge reference slices stack
        // if it's more than one longer than the placements stack
        let placed_tile_count = placements.len();
        let mut edge_reference_slice_count = edge_reference_slices.len();
        if edge_reference_slice_count > placed_tile_count {
            edge_reference_slices.truncate(placed_tile_count + 1);
            edge_reference_slice_count = edge_reference_slices.len();
        }
        
        // This block fetches the adjancent tile edges
        let left_placement: Option<&TilePlacement<S>>;
        let up_placement: Option<&TilePlacement<S>>;
        if placed_tile_count < map_side_length {
            left_placement = placements.last();
            up_placement = None;
        } else if placed_tile_count % map_side_length == 0 {
            left_placement = None;
            up_placement = placements.get(placed_tile_count - map_side_length);
        } else {
            left_placement = placements.last();
            up_placement = placements.get(placed_tile_count - map_side_length);
        };
        let left_bits: Option<EdgePlacement> = left_placement.map(|left| {left.get_edge_placement(Side::Right)});
        let up_bits: Option<EdgePlacement> = up_placement.map(|up| {up.get_edge_placement(Side::Bottom)});
        
        
        // this block... kills the currently active slice?
        // that's not right...
        // it should take it and use it instead of immediately killing it
        // FIXME
        // TODO
        let (source_slice, filter_left) = if edge_reference_slice_count == placed_tile_count + 1 {
            (match edge_reference_slices.pop() {
                Some(last_edge_reference_slice) => last_edge_reference_slice,
                None => {
                    eprint!("Tried to pop from empty edge slice vec, shouldn't happen");
                    return false;
                }
            }, up_bits.is_some() && left_bits.is_some())
        } else {
            // This block fetches the  vector of single-edge matching tile placements
            match (up_bits, left_bits) {
                (Some(up_place), Some(_left_place)) => {
                    let vec: &Vec<EdgeReference<S>> = &edge_map[&up_place];
                    (vec.as_slice(), true)
                },
                (Some(up_place), None) => {
                    let vec: &Vec<EdgeReference<S>> = &edge_map[&up_place];
                    (vec.as_slice(), false)
                },
                (None, Some(left_place)) => {
                    let vec: &Vec<EdgeReference<S>> = &edge_map[&left_place];
                    (vec.as_slice(), false)
                },
                (None, None) => { return false; }
            }
        };
        
        let possible_placeable_placement_index: Option<usize> = source_slice.iter().position(|placement| {
            !placed_tile_IDs.contains(&placement.placement.tile.tile_ID)
         && (!filter_left || Some(EdgePlacement::from(placement)) == left_bits)
         // assertions of edge map soundness
         && (filter_left || left_bits.map_or(true, |left_bits|{
                let right_bits = placement.placement.get_edge_placement(Side::Left);
                left_bits == right_bits || {
                    eprintln!("left {:?},\n right {:?}", left_bits, right_bits);
                    panic!();
                }
            }))
         && (up_bits.map_or(true, |upbits|{ upbits == placement.placement.get_edge_placement(Side::Top) || panic!()}))
        });
        
        let stack_change_success: bool = match possible_placeable_placement_index {
            None => {
                // failed to find a match, try to pop the current data
                let last_placement: &TilePlacement<S> = match placements.last() {
                    Some(placement) => placement,
                    None => { return false; } // no data to pop, return failure
                };
                let last_placed_tile: &Tile<S> = last_placement.tile;
                let removed: bool =
                    placed_tile_IDs.remove(&last_placed_tile.tile_ID)
                 && placements.pop().is_some()
                 && edge_reference_slices.pop().is_some();
                removed
            },
            Some(placement_index) => {
                // add the current data
                let good_slice: &[EdgeReference<S>] = &source_slice[placement_index + 1..];
                edge_reference_slices.push(good_slice);
                let good_placement: &EdgeReference<S> = &source_slice[placement_index];
                
                placements.push(good_placement.placement);
                let inserted: bool = placed_tile_IDs.insert(good_placement.placement.tile.tile_ID);
                // check if this solved it
                if placements.len() == tile_count {
                    return true;
                }
                inserted
            }
        };
        
        if !stack_change_success {
            return false;
        }
    }
}


