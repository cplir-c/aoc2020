
use std::borrow::Borrow;
use std::fmt;


mod tile_structures;
use tile_structures::*;
mod lib_square;
use lib_square::*;
mod map_structures;
use map_structures::*;
mod backtracking;
use backtracking::*;
mod tile_problem;
use tile_problem::*;
mod wrappers;
pub use wrappers::DebugStrWrapper;


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
    fun: fn(u16, &[TilePlacement<S>]) -> R
}

impl<S: Borrow<str>, R> ReturnAssembler<S, R> {
    pub fn new(fun: fn(u16, &[TilePlacement<S>]) -> R) -> ReturnAssembler<S, R> {
        ReturnAssembler{fun}
    }
}



fn get_corner_product<S: Borrow<str>>(map_edge_length: u16, placements: &[TilePlacement<S>]) -> u64 {
    println!("map placements:");
    for placement in placements {
        println!("{}\n", placement);
    }
    println!("Map pieced together:\n{}", MapDisplay(unsafe {
        // safe because DebugWrapper<S> has the same representation as S
        std::mem::transmute::<&[TilePlacement<S>], &[TilePlacement<DebugStrWrapper<S>>]>(placements)
    }));
    let map_edge_length = map_edge_length as usize;
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
impl<'a, S: Borrow<str>, R> Borrow<fn(u16, &[TilePlacement<S>]) -> R> for ReturnAssembler<S, R> {
    fn borrow(&self) -> &fn(u16, &[TilePlacement<S>]) -> R {
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
    
    println!("build data structures, calculating tile map...");
    let corner_id_product = piece_together_map(&tiles, return_assembler);
    match corner_id_product {
        Some(product) => Ok(product),
        None => Err("failed to build map".to_string())
    }
}

fn assemble_return<S: Borrow<str>, R>
  ( edge_length: u16
  , placed: &[TilePlacement<S>]
  , return_assembler: ReturnAssembler<S, R>
  ) -> R {
    println!("placement dimensions: {} long, {} across", placed.len(), edge_length);
    {
        let fun: &fn(u16, &[TilePlacement<S>]) -> R = return_assembler.borrow();
        fun(edge_length, placed)
    }
}

fn piece_together_map<'a, S: Borrow<str>, R>
  ( all_tiles: &'a [Tile<'a, S>]
  , return_assembler: ReturnAssembler<S, R>
  ) -> Option<R> {
    let tile_count: usize = all_tiles.len();
    println!("tile count {:?}", tile_count);
    let edge_length = isqrt(tile_count) as u16;
    println!("tile count: {}, map side length: {}", tile_count, edge_length);
    
    
    let tile_placements: Option<Box<[TilePlacement<'a, S>]>> = {
        let tile_problem: TileProblem<S> = TileProblem::new(all_tiles, edge_length);
        tile_problem.vec_backtrack()?;
        tile_problem.as_placements()
    };
    Some(
        tile_placements
            .map(|placements|{
                assemble_return(edge_length, &placements, return_assembler)
            })
            .expect("failed to collect tiles into placeements, what a letdown")
    )
}



/// Backtrack stitching
/// ------------------
/// This function takes the precalculated tile data
/// and the currently placed single tile data and tries to find
/// a solution to edge matching by backtracking
/// 


#[cfg(test)]
mod test_map_stitching;
