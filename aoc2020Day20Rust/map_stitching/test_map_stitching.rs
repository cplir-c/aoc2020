
use super::TileOrientation;
use std::default::Default;
use std::fmt::Write;

#[test]
fn example_test_correct_answer() {
    let example_result: u64 = super::find_corner_id_product::<Box<str>, _>(EXAMPLE_TILES_STRING_INPUT, Default::default())
        .expect("failed to find corner id product");
    assert!(example_result == EXAMPLE_CORRECT_CORNER_PRODUCT
        , "failed example test:\n    got {:#?}, but expected {:#?}"
        , example_result, EXAMPLE_CORRECT_CORNER_PRODUCT);
}

#[test]
fn example_test_good_assembly() {
    use super::ReturnAssembler;
    let return_assembler = ReturnAssembler::<Box<str>, _>::new(
        |map_width, tiles|{
            use super::lib_square::MapDisplay;
            let tile_body_length = tiles.get(0)
                .expect("failed to find a tile to peek at")
                .tile.body_string.len();
            let map_width = map_width as usize;
            let mut out = String::with_capacity(
                map_width * map_width * tile_body_length
            );
            write!(&mut out, "{}", MapDisplay(tiles))
                .expect("failed to allocate string");
            out
        }
    );
    let example_result = super::find_corner_id_product(EXAMPLE_TILES_STRING_INPUT, return_assembler)
        .expect("failed example assembly-display test");
    
    let mut out = String::with_capacity(example_result.len());
    for orientation in TileOrientation::default() {
        out.clear();
        orientation.format(&mut out, EXAMPLE_MAP_ARRANGEMENT)
            .expect("failed to write oriented example map");
        if out.as_str() == example_result.as_str() {
            return;
        }
    }
    panic!("failed to find a matching example map orientation");
    // TODO: expand the test checking to test all 8 board orientations
}

static EXAMPLE_CORRECT_CORNER_PRODUCT: u64 = 20899048083289;
static EXAMPLE_TILES_STRING_INPUT: &str =
 r#"Tile 2311:
..##.#..#.
##..#.....
#...##..#.
####.#...#
##.##.###.
##...#.###
.#.#.#..##
..#....#..
###...#.#.
..###..###

Tile 1951:
#.##...##.
#.####...#
.....#..##
#...######
.##.#....#
.###.#####
###.##.##.
.###....#.
..#.#..#.#
#...##.#..

Tile 1171:
####...##.
#..##.#..#
##.#..#.#.
.###.####.
..###.####
.##....##.
.#...####.
#.##.####.
####..#...
.....##...

Tile 1427:
###.##.#..
.#..#.##..
.#.##.#..#
#.#.#.##.#
....#...##
...##..##.
...#.#####
.#.####.#.
..#..###.#
..##.#..#.

Tile 1489:
##.#.#....
..##...#..
.##..##...
..#...#...
#####...#.
#..#.#.#.#
...#.#.#..
##.#...##.
..##.##.##
###.##.#..

Tile 2473:
#....####.
#..#.##...
#.##..#...
######.#.#
.#...#.#.#
.#########
.###.#..#.
########.#
##...##.#.
..###.#.#.

Tile 2971:
..#.#....#
#...###...
#.#.###...
##.##..#..
.#####..##
.#..####.#
#..#.#..#.
..####.###
..#.#.###.
...#.#.#.#

Tile 2729:
...#.#.#.#
####.#....
..#.#.....
....#..#.#
.##..##.#.
.#.####...
####.#.#..
##.####...
##..#.##..
#.##...##.

Tile 3079:
#.#.#####.
.#..######
..#.......
######....
####.#..#.
.#...#.##.
#.#####.##
..#.###...
..#.......
..#.###..."#;

static EXAMPLE_MAP_ARRANGEMENT: &str = r#"#...##.#.. ..###..### #.#.#####.
..#.#..#.# ###...#.#. .#..######
.###....#. ..#....#.. ..#.......
###.##.##. .#.#.#..## ######....
.###.##### ##...#.### ####.#..#.
.##.#....# ##.##.###. .#...#.##.
#...###### ####.#...# #.#####.##
.....#..## #...##..#. ..#.###...
#.####...# ##..#..... ..#.......
#.##...##. ..##.#..#. ..#.###...

#.##...##. ..##.#..#. ..#.###...
##..#.##.. ..#..###.# ##.##....#
##.####... .#.####.#. ..#.###..#
####.#.#.. ...#.##### ###.#..###
.#.####... ...##..##. .######.##
.##..##.#. ....#...## #.#.#.#...
....#..#.# #.#.#.##.# #.###.###.
..#.#..... .#.##.#..# #.###.##..
####.#.... .#..#.##.. .######...
...#.#.#.# ###.##.#.. .##...####

...#.#.#.# ###.##.#.. .##...####
..#.#.###. ..##.##.## #..#.##..#
..####.### ##.#...##. .#.#..#.##
#..#.#..#. ...#.#.#.. .####.###.
.#..####.# #..#.#.#.# ####.###..
.#####..## #####...#. .##....##.
##.##..#.. ..#...#... .####...#.
#.#.###... .##..##... .####.##.#
#...###... ..##...#.. ...#..####
..#.#....# ##.#.#.... ...##....."#;