
use std::borrow::Borrow;
use std::collections::HashMap;
use std::convert::From;

use super::EdgePlacement;
use super::EdgeReference;
use super::Tile;
use super::TilePlacement;

pub type EdgeMap<'a, 'b, S> = HashMap<EdgePlacement, Vec<EdgeReference<'a, 'b, S>>>;

pub fn build_edge_map<'a, S: Borrow<str> + Clone>
  ( tiles: &'a [Tile<'a, S>]
  , edge_map: &mut EdgeMap<'a, 'a, S>
  ) {
    let placements = tiles.iter().flat_map(|tile|{tile.into_iter()});
    for tile_placement in placements {
        for edge_reference in tile_placement.iter_edge_refs() {
            let edge_placement = EdgePlacement::from(&edge_reference);
            
            edge_map.entry(edge_placement)
                .or_default().push(edge_reference);
        }
    }
}

#[cfg(test)]
mod tests {
    use std::fmt;
    use std::fmt::Display;
    use std::fmt::Formatter;
    use std::io;
    use std::io::Write;
    use std::mem;
    use std::mem::MaybeUninit;
    use std::str;
    
    use super::super::tile_placement::TilePlacement;
    use super::super::edge_placement::EdgePlacement;
    use super::super::tile::Tile;
    use super::super::tile_orientation::TileOrientationIterator;
    
    use super::{EdgeMap, build_edge_map};
    
    struct TileRowDisplay(u16);
    impl Display for TileRowDisplay {
        fn fmt(&self, formatter: &mut Formatter<'_>) -> fmt::Result {
            let mut buf: [u8;16] = [0;16];
            let mut buf_ref = buf.as_mut();
            write!(&mut buf_ref, "{:b}", self.0).map_err(|_|fmt::Error)?;
            let buf_left = buf_ref.len();
            
            let good_buf = &mut buf[..16 - buf_left];
            for byte in good_buf.iter_mut() {
                *byte = if *byte == b'1' {
                    b'#'
                } else {
                    b'.'
                };
            }
            // trim the buffer to format-specified width, precision, or 16
            let cut_to_width = formatter.width()
                .unwrap_or_else(||{
                    formatter.precision()
                        .unwrap_or(16)
                });
            let good_buf = good_buf.rchunks(cut_to_width).next().unwrap_or(b"");
            // parse buf as &str
            let buf_str: &str = str::from_utf8(&good_buf).map_err(|_|fmt::Error)?;
            // put the buf str into the formatter
            formatter.pad(buf_str)
        }
    }
    
    fn write_test_tile(out: &mut impl Write, tile_edge_length: u16, tile_id: u16, mut row_function: impl FnMut(u16) -> u16) -> io::Result<()> {
        writeln!(out, "Tile {}:", tile_id)?;
        let mut left_buf = [b'.'; 16];
        let mut right_buf = [b'.'; 16];
        let left_str: &str = {
            let mut left_ref = &mut left_buf.as_mut();
            write!(&mut left_ref, "{:.>edge_length$.edge_length$}", TileRowDisplay(row_function(1)), edge_length=(tile_edge_length) as usize)?;
            let left_left = left_ref.len();
            let left_ref = &left_buf[..left_buf.len() - left_left];
            str::from_utf8(&left_ref).expect("failed to parse left buf")
        };
        let right_str: &str = {
            let mut right_ref = right_buf.as_mut();
            write!(&mut right_ref, "{:.>edge_length$.edge_length$}", TileRowDisplay(row_function(tile_edge_length - 2)), edge_length=(tile_edge_length) as usize)?;
            let right_left = right_ref.len();
            let right_ref = &right_buf[..right_buf.len() - right_left];
            str::from_utf8(&right_ref).expect("failed to parse right buf")
        };
        (0..tile_edge_length).zip(
            left_str.chars().zip(right_str.chars())
        ).try_fold((), |_unit, (row, (left, right))| {
            // write the tile row with a fill char of '.', and
            // a min precision and max width of tile_edge_length
            write!(out, "{}", left)?;
            write!(out, "{:.>edge_length$.edge_length$}", TileRowDisplay(row_function(row)),
                edge_length=(tile_edge_length - 2) as usize)?;
            writeln!(out, "{}", right)
        })
    }
    
    #[test]
    fn test_tile_orientations() {
        let mut tile_buf = Vec::<u8>::new();
        const TILE_EDGE_SIZE: u16 = 10;
        write_test_tile(&mut tile_buf, TILE_EDGE_SIZE, 1024, |row|{
            24 * TILE_EDGE_SIZE + row
        }).expect("failed to write test tile");
        let tile_str = str::from_utf8(&tile_buf).expect("failed to print valid utf8 tile");
        let tile: Tile<Box<str>> = Tile::parse(tile_str).expect("failed to parse generated tile");
        
        if tile.into_iter().take(10).count() != 8 {
            eprintln!("{}", TileOrientationIterator::default()
                    .take(10)
                    .map(|orientation|{
                        format!("{:#?},\n", orientation)
                    }).collect::<String>());
            panic!("broken tile placement iterator");
        }
    }
    
    
    #[test]
    fn test_self_lookup() {
        const TILE_EDGE_SIZE_U16: u16 = 4;
        const TILE_COUNT_U16: u16 = 9;
        const TILE_EDGE_SIZE: usize = TILE_EDGE_SIZE_U16 as usize;
        const TILE_COUNT: usize = TILE_COUNT_U16 as usize;
        const TILE_BUF_LEN: usize = TILE_COUNT * (
            "Tile 00000000:".len() + TILE_EDGE_SIZE * (TILE_EDGE_SIZE + 1)
        ) + 2 * (TILE_COUNT - 1);
        let mut tiles_buf: [u8; TILE_BUF_LEN] = [0; TILE_BUF_LEN];
        let tiles_string = {
            let mut buf_ref = tiles_buf.as_mut();
            for i in 0..TILE_COUNT_U16 {
                let tile_id: u16 = 1000 + i;
                write_test_tile(&mut buf_ref, TILE_EDGE_SIZE_U16, tile_id, |row|{
                    (tile_id % 1000) * TILE_EDGE_SIZE_U16 + row
                }).unwrap_or_else(|_|panic!("Failed to write test tile {} ", tile_id));
            }
            
            let buf_left = buf_ref.len();
            let good_buf = &tiles_buf.as_ref()[..tiles_buf.len() - buf_left];
            str::from_utf8(good_buf)
                .expect("failed to stringify tiles string buffer")
        };
        // https://doc.rust-lang.org/nightly/std/mem/union.MaybeUninit.html#initializing-an-array-element-by-element
        let tiles: [Tile<Box<str>>; TILE_COUNT] = {
            let mut tiles: [MaybeUninit<Tile<Box<str>>>; TILE_COUNT] = unsafe {
                MaybeUninit::uninit().assume_init()
            };
            for (tile, tile_str) in tiles.iter_mut().zip(tiles_string.split("\n\n")) {
                *tile = MaybeUninit::new(Tile::parse(tile_str).expect("failed to parse test tile"));
            }
            unsafe {
                mem::transmute::<_, [Tile<Box<str>>; TILE_COUNT]>(tiles)
            }
        };
        
        let placements: [TilePlacement<Box<str>>; TILE_COUNT * 8] = {
            let mut placements: [MaybeUninit<TilePlacement<_>>; TILE_COUNT * 8] = unsafe {
                MaybeUninit::uninit().assume_init()
            };
            for (chunk, tile) in placements.chunks_exact_mut(8).zip(tiles.iter()) {
                for (place, ment) in chunk.iter_mut().zip(tile.into_iter()) {
                    *place = MaybeUninit::new(ment);
                }
            }
            unsafe {
                mem::transmute::<_, [TilePlacement<_>; TILE_COUNT * 8]>(placements)
            }
        };
        
        let mut edge_map = EdgeMap::with_capacity(4 * TILE_COUNT * 8);
        build_edge_map(&tiles[..], &mut edge_map);
        
        for placement in placements.iter() {
            for goal_ref in placement.iter_edge_refs() {
                let goal_tile_id = goal_ref.placement.tile.tile_id;
                let goal_placement = EdgePlacement::from(goal_ref);
                let edge_refs = edge_map.get(&goal_placement).expect("failed to get edge ref vector by edge placement");
                let mut found_goal = false;
                for edge_ref in edge_refs {
                    if edge_ref.placement.tile.tile_id != goal_tile_id {
                        continue;
                    }
                    
                    if goal_placement.bits != edge_ref.placement[edge_ref.side]
                    || goal_placement.side != edge_ref.side {
                        panic!("invalid edge reference in edge map vector");
                    }
                    
                    if edge_ref == &goal_ref {
                        found_goal = true;
                        break;
                    }
                }
                if !found_goal {
                    panic!("failed to find edge ref\n{:?} in edge map\n", goal_ref);
                }
            }
        }
    }
}