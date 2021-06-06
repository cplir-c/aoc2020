
use std::borrow::Borrow;
use std::borrow::Cow;
use std::cell::RefCell;
use std::collections::HashSet;
use std::marker::PhantomData;

use super::placement_map::PlacementMap;
use super::super::tile_structures;
use super::super::tile_structures::EdgeMap;
use super::super::tile_structures::Tile;
use super::super::tile_structures::TilePlacement;
use super::super::backtracking::BFSProblem;

type TilePlacementMap<'a, S> = PlacementMap<'a, TilePlacement<'a, 'a, S>>;

struct TileProblem<'a, 'b, S: Borrow<str>> {
    tiles: &'a [Tile<'a, S>],
    edge_map: EdgeMap<'a, 'a, S>,
    placed_tile_ids: HashSet<u16>,
    placements: RefCell<TilePlacementMap<'a, S>>,
    phantom: PhantomData<TileCandidate<'a, 'b, S>>
}

enum BorrowClone<'a, T: Borrow<B>, B: ?Sized> {
    Borrowed(&'a B),
    Owned(T)
}
impl<T: Borrow<B>, B: ?Sized> Clone for BorrowClone<'_, T, B> {
    fn clone(&self) -> Self {
        Self::Borrowed(self.borrow())
    }
}
impl<T: Borrow<B>, B: ?Sized> Borrow<B> for BorrowClone<'_, T, B> {
    fn borrow(&self) -> &B {
        match self {
            Self::Borrowed(bor) => bor,
            Self::Owned(o) => o.borrow(),
        }
    }
}

enum TileCandidate<'a, 'b, S: Borrow<str>> {
    RootTiles(&'b [Tile<'a, S>]),
    LeafTiles(BorrowClone<'b, Box<[&'a TilePlacement<'a, 'a, S>]>, [&'a TilePlacement<'a, 'a, S>]>)
}

impl<'a, 'b, S: Borrow<str>> Clone for TileCandidate<'a, 'b, S> {
    fn clone(&self) -> Self {
        match self {
            TileCandidate::RootTiles(tiles) => TileCandidate::RootTiles(tiles),
            TileCandidate::LeafTiles(borrow_clone) => TileCandidate::LeafTiles({
                borrow_clone.clone()
            })
        }
    }
}

impl<'a, 'b, S: Borrow<str> + Clone> TileProblem<'a, 'b, S> {
    fn new(tiles: &'a [Tile<'a, S>], side_length: u16) -> TileProblem<'a, 'b, S> {
        let area: usize = (side_length as usize).pow(2);
        let mut edge_map = EdgeMap::with_capacity(area);
        tile_structures::build_edge_map(tiles, &mut edge_map);
        let placed_tile_ids = HashSet::with_capacity(area);
        let placements = RefCell::new(TilePlacementMap::new(side_length));
        
        TileProblem {
            tiles, edge_map,
            placed_tile_ids,
            placements,
            phantom: PhantomData
        }
    }
}

impl<'a, 'b, S: 'a + Borrow<str>> BFSProblem<'a> for TileProblem<'a, 'b, S> {
    type Candidate=TileCandidate<'a, 'b, S>;
    
    fn root_candidate(&'a self) -> <Self as BFSProblem>::Candidate {
        let (first, tiles) = self.tiles.split_first().expect("expected some tiles");
        TileCandidate::RootTiles(tiles)
    }
    fn first_extension(&'a self, candidate: <Self as BFSProblem>::Candidate) -> Option<<Self as BFSProblem>::Candidate> {
        
    }
    fn next_extension(&'a self, candidate: <Self as BFSProblem>::Candidate) -> Option<<Self as BFSProblem>::Candidate> {
        
    }
}