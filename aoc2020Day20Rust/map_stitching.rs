
use std::borrow::Borrow;
use std::collections::HashSet;
use std::fmt;
use std::fmt::Debug;

mod tile_structures;
use tile_structures::*;
mod lib_square;
use lib_square::*;
mod map_structures;
use map_structures::*;
mod backtracking;
use backtracking::*;

fn gather_errors<T>(accum: Result<Vec<T>, String>, item: Result<T, String>) -> Result<Vec<T>, String> {
    match (item, accum) {
        (Ok(itm), Ok(mut vec)) => {vec.push(itm); Ok(vec)},
        (Ok(_), Err(errs)) => Err(errs),
        (Err(err), Ok(_)) => Err(err),
        (Err(err), Err(mut errs)) => {
            errs.push('\n');
            errs.push_str(&err);
            Err(errs)
        }
    }
}

#[repr(transparent)]
pub struct ReturnAssembler<S: Borrow<str>, R>{
    fun: fn(usize, &[&TilePlacement<S>]) -> R
}

impl<S: Borrow<str>, R> ReturnAssembler<S, R> {
    pub fn new(fun: fn(usize, &[&TilePlacement<S>]) -> R) -> ReturnAssembler<S, R> {
        ReturnAssembler{fun}
    }
}

#[repr(transparent)]
struct DebugWrapper<S: Borrow<str>>{
    s: S
}
impl<S: Borrow<str>> Debug for DebugWrapper<S> {
    fn fmt(&self, out: &mut fmt::Formatter) -> fmt::Result {
        out.write_str(self.borrow())
    }
}
impl<S: Borrow<str>> Borrow<str> for DebugWrapper<S> {
    fn borrow(&self) -> &str {
        self.s.borrow()
    }
}

fn get_corner_product<S: Borrow<str>>(map_edge_length: usize, placements: &[&TilePlacement<S>]) -> u64 {
    println!("map placements:");
    for placement in placements {
        println!("{}\n", placement);
    }
    println!("Map pieced together:\n{}", MapDisplay(unsafe {
        // safe because DebugWrapper<S> has the same representation as S
        std::mem::transmute::<&[&TilePlacement<S>], &[&TilePlacement<DebugWrapper<S>>]>(placements)
    }));
    let tile_count = map_edge_length * map_edge_length;
    let mut result: u64 = placements[0].tile.tile_id as u64;
    result *= placements[map_edge_length - 1].tile.tile_id as u64;
    result *= placements[tile_count - 1].tile.tile_id as u64;
    result *= placements[tile_count - map_edge_length].tile.tile_id as u64;
    result
}

impl<S: Borrow<str>> Default for ReturnAssembler<S, u64> {
    fn default() -> Self {
        Self::new(get_corner_product)
    }
}
impl<'a, S: Borrow<str>, R> Borrow<fn(usize, &[&TilePlacement<S>]) -> R> for ReturnAssembler<S, R> {
    fn borrow(&self) -> &fn(usize, &[&TilePlacement<S>]) -> R {
        &self.fun
    }
}

pub fn find_corner_id_product<S, R>(
    tiles_string: &'static str,
    return_assembler: ReturnAssembler<S, R>
  ) -> Result<R, String>
  where S: Borrow<str> + Clone + Default + From<String> + fmt::Debug {
    // parse tiles
    let tiles_result: Result<Vec<Tile<S>>, String> = tiles_string.split("\n\n")
        .map(|string| {Tile::<S>::parse(string)})
        .fold(Result::Ok(Vec::new()), gather_errors);
    // return errors if tile parsing failed
    let tiles: Vec<Tile<S>> = match tiles_result {
        Ok(tiles) => tiles,
        Err(errs) => { return Err(format!("Errors while parsing tiles:\n{}", errs)); }
    };

    let tile_count = tiles.len();
    println!("tile count: {}", tile_count);
    
    let edge_map = {
        let mut edge_map = EdgeMap::with_capacity(8 * tile_count);
        build_edge_map(&tiles[..], &mut edge_map);
        edge_map
    };
    
    println!("build data structures, calculating tile map...");
    let corner_id_product = piece_together_map(&tiles, &edge_map, return_assembler);
    match corner_id_product {
        Some(product) => Ok(product),
        None => Err(format!("failed to build map with edges {:?}...", edge_map))
    }
}

fn assemble_return<'a, S: Borrow<str>, R>
  ( edge_length: usize
  , placements: PlacementMap<TilePlacement<'a, 'a, S>>
  , return_assembler: ReturnAssembler<S, R>
  ) -> R {
    println!("placement dimensions: {} long, {} across", placements.capacity(), placements.side_length());
    let placed = placements.into_slice().unwrap();
    {
        let fun: &fn(usize, &[&TilePlacement<S>]) -> R = return_assembler.borrow();
        fun(edge_length, &placed[..])
    }
}

fn piece_together_map<'a, S: Borrow<str> + Clone, R>
  ( all_tiles: &[Tile<'a, S>]
  , edge_map: &'a EdgeMap<'a, 'a, S>
  , return_assembler: ReturnAssembler<S, R>
  ) -> Option<R> {
    let tile_count: usize = all_tiles.len();
    println!("tile count {:?}", tile_count);
    let edge_length = isqrt(tile_count);
    print!("tile count: {}, map side length: {}", tile_count, edge_length);
    let mut placements = PlacementMap::<TilePlacement<'a, 'a, S>>::new(edge_length as u16);
    let mut placed_tile_ids = HashSet::<EdgeBits>::with_capacity(tile_count);
    let mut edge_reference_slices = Vec::<&[EdgeReference<S>]>::with_capacity(tile_count);
    
    edge_reference_slices.push(&[]);
    for tile in all_tiles {
        placed_tile_ids.insert(tile.tile_id);
        // WLOG only consider the first orientation for each tile
        // rotating the root tile is equivalent to rotating the entire board
        let placement: &TilePlacement<S> = {
            let placement = tile.into_iter().next().unwrap();
            let edge_placement = placement.get_edge_placement(Side::Top);
            let place_ref_vec: &Vec<EdgeReference<S>> = edge_map.get(&edge_placement).unwrap();
            let mut good_placement: Option<&TilePlacement<S>> = None;
            for edge_ref in place_ref_vec.iter() {
                let possible_placement = &edge_ref.placement;
                if possible_placement.tile.tile_id == placement.tile.tile_id {
                    good_placement = Some(possible_placement);
                    break;
                }
            }
            good_placement.expect("failed to self-lookup edge reference")
        };
        if placements.push(&placement).is_err() {
            return Some(assemble_return(edge_length, placements, return_assembler));
        }
        
        let success: bool = {
            let borrowed_tile_ids = &mut placed_tile_ids;
            backtrack_stitching(edge_map, &mut placements, borrowed_tile_ids,
                &mut edge_reference_slices)
        };
        if success {
            return Some(assemble_return(edge_length, placements, return_assembler));
        }
        
        placements.pop();
        placed_tile_ids.remove(&tile.tile_id);
    }
    None
}



/// Backtrack stitching
/// ------------------
/// This function takes the precalculated tile data
/// and the currently placed single tile data and tries to find
/// a solution to edge matching by backtracking
/// 
fn backtrack_stitching<'a, 'b, 'c, S>(
    edge_map: &'a EdgeMap<'a, 'a, S>,
    placements: &'c mut PlacementMap<'b, TilePlacement<'a, 'b, S>>,
    placed_tile_ids: &'c mut HashSet<u16>,
    edge_reference_slices: &'c mut Vec<&'a [EdgeReference<'a, 'a, S>]>
  ) -> bool
  where S: Borrow<str> + Clone {
    loop {
        // this block truncates the edge reference slices stack
        // if it's more than one longer than the placements stack
        let placed_tile_count = placements.len();
        println!("stack depth: {}", placed_tile_count);
        let mut edge_reference_slice_count = edge_reference_slices.len();
        if edge_reference_slice_count > placed_tile_count {
            edge_reference_slices.truncate(placed_tile_count + 1);
            edge_reference_slice_count = edge_reference_slices.len();
        }
        
        let pos = match placements.peek_pos() {
            None => {
                // This solved it!
                return true;
            },
            Some(pos) => pos
        };
        
        // This block fetches the adjancent tile edges
        let adjacents = placements.get_adjacents(pos);
        let left_bits: Option<EdgePlacement> = adjacents.left().map(|left| {left.get_edge_placement(Side::Right)});
        let up_bits: Option<EdgePlacement> = adjacents.up().map(|up| {up.get_edge_placement(Side::Bottom)});
        
        let (source_slice, filter_left) = if edge_reference_slice_count == placed_tile_count + 1 {
            (edge_reference_slices.pop().expect("Tried to pop from empty edge slice vec, shouldn't happen")
            , up_bits.is_some() && left_bits.is_some())
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
            !placed_tile_ids.contains(&placement.placement.tile.tile_id)
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
                let last_placement: &TilePlacement<S> = match placements.pop() {
                    Some(placement) => placement,
                    None => { return false; } // no data to pop, return failure
                };
                let last_placed_tile: &Tile<S> = last_placement.tile;
                let removed: bool =
                    placed_tile_ids.remove(&last_placed_tile.tile_id)
                 && edge_reference_slices.pop().is_some();
                removed
            },
            Some(placement_index) => {
                // add the current data
                let good_slice: &[EdgeReference<S>] = &source_slice[placement_index + 1..];
                edge_reference_slices.push(good_slice);
                let good_placement: &EdgeReference<S> = &source_slice[placement_index];
                
                if placements.push(&good_placement.placement).is_err() {
                    return true;
                }
                let inserted: bool = placed_tile_ids.insert(good_placement.placement.tile.tile_id);
                inserted
            }
        };
        
        if !stack_change_success {
            return false;
        }
    }
}

#[cfg(test)]
mod test_map_stitching;
