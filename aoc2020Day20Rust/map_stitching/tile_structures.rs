
use std::borrow::Borrow;
use std::collections::HashMap;
use std::convert::From;

mod sides;
pub use sides::Side;
pub use sides::SideIterator;

mod edges;
pub use edges::EdgeBits;

mod tile_edges;

mod tile_orientation;
pub use tile_orientation::TileOrientation;
pub use tile_orientation::TileOrientationIterator;

mod tile;
pub use tile::Tile;

mod tile_placement;
pub use tile_placement::TilePlacement;
pub use tile_placement::TilePlacementIterator;

mod edge_placement;
pub use edge_placement::EdgePlacement;
pub use edge_placement::EdgeReference;

pub type EdgeMap<'a, 'b, S> = HashMap<EdgePlacement, Vec<EdgeReference<'a, 'b, S>>>;

pub fn build_edge_map<'a, S: Borrow<str> + Clone>(placements: &'a[TilePlacement<'a, 'a, S>], edge_map: &mut EdgeMap<'a, 'a, S>) {
    for tile_placement in placements {
        for edge_reference in tile_placement.iter_edge_refs() {
            let edge_placement = EdgePlacement::from(&edge_reference);
            
            edge_map.entry(edge_placement)
                .or_default().push(edge_reference);
        }
    }
}

