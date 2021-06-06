

use std::borrow::Borrow;
use std::default::Default;
use std::fmt;
use std::fmt::Debug;
use std::ops::Index;


use crate::map_stitching::tile_structures::tile_edges::TileEdges;
use crate::map_stitching::tile_structures::edges::Edge;
use crate::map_stitching::tile_structures::sides::Side;
use crate::map_stitching::tile_structures::edges::EdgeBits;

#[derive(Default, PartialEq, Eq)]
pub struct Tile<'a, S: Borrow<str>> {
    pub tile_id: u16,
    pub body_string: &'a str,
    pub edges: TileEdges<'a, S>
}

impl<'a, S: Borrow<str>> Index<Side> for Tile<'a, S> {
    type Output = EdgeBits;
    fn index(&self, index: Side) -> &Self::Output {
        &self.edges[index]
    }
}

impl<'a, S: Borrow<str> + Default + From<String>> Tile<'a, S> {
    pub fn parse(mut string: &'a str) -> std::result::Result<Self, String> {
        if !string.starts_with("Tile ") {
            return Err(format!("Error parsing tile: did not start with \"Tile \":\n{}", string));
        }
        string = &string["Tile ".len()..];
        let mut tile: Tile<'a, S> = Tile::default();
        string = match string.split_once(":\n") {
            None => { return Err(format!("Error parsing tile: did not find \":\\n\":\n{}", string)); },
            Some((line, rest)) => {
                tile.tile_id = match line.parse::<u16>() {
                    Err(err) => { return Err(format!("Error parsing tile: could not parse tileID from {}\ngot an error of {}", line, err)) },
                    Ok(id) => id
                };
                rest
            }
        };
        tile.body_string = string;
        
        {
            let other_lines = match string.split_once('\n') {
                None => { return Err(format!("Error parsing tile: did not find second newline:\n{}", string)); },
                Some((line, rest)) => {
                    tile.edges.top = Edge::from(line);
                    rest
                }
            };
            match other_lines.lines().last() {
                None => { return Err(format!("Error parsing tile: no lines found somehow:\n{}", string)); },
                Some(line) => {
                    tile.edges.bottom = Edge::from(line);
                }
            };
        };
        let edge_length = tile.edges.top.forward.string.len();
        let mut left_string = String::with_capacity(edge_length);
        let mut right_string = String::with_capacity(edge_length);
        
        for line in string.lines() {
            let mut chars = line.chars();
            let first_char = chars.next();
            let last_char = chars.next_back();
            match first_char {
                Some(chr) => left_string.push(chr),
                None => { return Err(format!("Error parsing tile: no initial character found on line: {}", line)); }
            }
            match last_char {
                Some(chr) => right_string.push(chr),
                None => { return Err(format!("Error parsing tile: no final character found on line: {}", line)); }
            }
        }
        
        tile.edges.left = Edge::from(S::from(left_string));
        tile.edges.right = Edge::from(S::from(right_string));
        
        Ok(tile)
    }
}

impl<'a, S: Borrow<str>> Debug for Tile<'a, S> {
    fn fmt(&self, out: &mut fmt::Formatter) -> fmt::Result {
        write!(out, "tile ID {}", self.tile_id)
    }
}

#[cfg(test)]
mod tests {
    use super::Tile;
    use super::super::edges::{Edge, DirectedEdge};
    use super::super::tile_edges::TileEdges;//45678901
    static TEST_TILE_STR: &str = r#"Tile 1024:
#..#.##..#
#.#..#.##.
#..##..#.#
#..##.#..#
#..#.##.#.
.#.##..##.
#..#.##.#.
.##..#.##.
#..#.##..#
#.#..##..#"#;
    use std::borrow::Cow;
    static TEST_TILE: Tile<MyStr> = Tile {
        tile_id: 1024,
        body_string: r#"#..#.##..#
#.#..#.##.
#..##..#.#
#..##.#..#
#..#.##.#.
.#.##..##.
#..#.##.#.
.##..#.##.
#..#.##..#
#.#..##..#"#,
        edges: TileEdges {
            top: Edge { /* same as edge parsing test edge*/
                forward: DirectedEdge {
                    string: ("#..#.##..#"),
                    bits: 0x259
                },
                backward: DirectedEdge {
                    string: MyStr(Cow::Borrowed("#..##.#..#")),
                    bits: 0x269
                }
            },
            right: Edge {
                forward: DirectedEdge {
                    string: MyStr(Cow::Borrowed("#.##....##")),
                    bits: 0x2c3
                },
                backward: DirectedEdge {
                    string: MyStr(Cow::Borrowed("##....##.#")),
                    bits: 0x30d
                }
            },
            bottom: Edge {
                forward: DirectedEdge {
                    string: ("#.#..##..#"),
                    bits: 0x299
                },
                backward: DirectedEdge {
                    string: MyStr(Cow::Borrowed("#..##..#.#")),
                    bits: 0x265
                }
            },
            left: Edge {
                forward: DirectedEdge {
                    string: MyStr(Cow::Borrowed("#####.#.##")),
                    bits: 0x3eb
                },
                backward: DirectedEdge {
                    string: MyStr(Cow::Borrowed("##.#.#####")),
                    bits: 0x35f
                }
            }
        }
    };
    
    #[derive(PartialEq, Eq, Default, Debug)]
    struct MyStr<'a>(Cow<'a, str>);
    
    impl<'a> std::borrow::Borrow<str> for MyStr<'a> {
        fn borrow(&self) -> &str { 
            &self.0
        }
    }
    
    impl<'a> From<String> for MyStr<'a> {
        fn from(s: String) -> MyStr<'a> {
            MyStr(Cow::Owned(s))
        }
    }
    
    #[test]
    fn test_tile_parsing() {
        let parsed_tile: Tile<MyStr> = Tile::parse(TEST_TILE_STR).expect("failed to parse tile");
        if TEST_TILE != parsed_tile{
            panic!("Failed to parse tile:\n tile string in: \"{}\",\n parsed tile: {:#?},\n, correct tile: {:#?}\n", TEST_TILE_STR, parsed_tile, TEST_TILE);
        }
    }
}


