use std::borrow::Borrow;
use std::borrow::BorrowMut;
use std::cell::Cell;
use std::cell::RefCell;
use std::cell::RefMut;
use std::collections::HashSet;
use std::marker::PhantomData;

use super::backtracking::BFSProblem;
use super::tile_structures;
use super::EdgeMap;
use super::EdgeReference;
use super::PlacementMap;
use super::Tile;
use super::TilePlacement;

mod borrow_owning;

type TilePlacementMap<'a, S> = PlacementMap<'a, TilePlacement<'a, 'a, S>>;

pub struct TileProblem<'a, 'b, S: Borrow<str>> {
    tiles: &'a [Tile<'a, S>],
    edge_map: EdgeMap<'a, 'a, S>,
    placed_tile_ids: HashSet<u16>,
    placements: RefCell<TilePlacementMap<'a, S>>,
    phantom: PhantomData<&'b TileCandidate<'a, 'b, S>>,
}

enum TileCandidate<'a, 'b, S: Borrow<str>> {
    RootTiles(Cell<&'b [Tile<'a, S>]>),
    LeafTiles(Cell<&'b [&'a TilePlacement<'a, 'a, S>]>),
}
impl<'a, 'b, S: Borrow<str>> TileCandidate<'a, 'b, S> {
    fn is_empty(&self) -> bool {
        match self {
            TileCandidate::RootTiles(tiles) => tiles.get().is_empty(),
            TileCandidate::LeafTiles(places) => places.get().as_ref().is_empty(),
        }
    }
    fn next_extension(&'_ self, edge_map: &'b EdgeMap<S>) -> Option<&'b TilePlacement<'b, 'b, S>> {
        match self {
            TileCandidate::RootTiles(tiles) => tiles.get().split_last().and_then(|(last, rest)| {
                tiles.set(rest);
                tile_structures::lookup_tile(&last, edge_map).map(|edge_ref| &edge_ref.placement)
            }),
            TileCandidate::LeafTiles(_ref_clone) => {
                todo!()
            }
        }
    }
}
impl<'a, 'b, S: Borrow<str>> Clone for TileCandidate<'a, 'b, S> {
    fn clone(&self) -> Self {
        match self {
            TileCandidate::RootTiles(tiles) => TileCandidate::RootTiles(Cell::new(tiles.get())),
            TileCandidate::LeafTiles(places) => TileCandidate::LeafTiles(Cell::new(places.get())),
        }
    }
}

impl<'a, 'b, S: Borrow<str>> TileProblem<'a, 'b, S> {
    pub fn new(tiles: &'a [Tile<'a, S>], side_length: u16) -> TileProblem<'a, 'b, S> {
        let area: usize = (side_length as usize).pow(2);
        let mut edge_map = EdgeMap::with_capacity(area);
        tile_structures::build_edge_map(tiles, &mut edge_map);
        let placed_tile_ids = HashSet::with_capacity(area);
        let placements = RefCell::new(TilePlacementMap::new(side_length));
        TileProblem {
            tiles,
            edge_map,
            placed_tile_ids,
            placements,
            phantom: PhantomData,
        }
    }
    pub fn into_placements(self) -> Option<Box<[&'a TilePlacement<'a, 'a, S>]>> {
        self.placements.into_inner().into_slice()
    }
}

impl<'a, 'b, S: Borrow<str>> BFSProblem<'a> for TileProblem<'a, 'b, S> {
    type Candidate = TileCandidate<'a, 'b, S>;
    fn root_candidate(&'a self) -> <Self as BFSProblem>::Candidate {
        let (first, tiles): (&Tile<S>, &[Tile<S>]) =
            self.tiles.split_first().expect("expected some tiles");
        // WLOG only consider an arbitrary orientation for each tile
        // rotating the root tile is equivalent to rotating the entire board
        
        let first: &'a EdgeReference<S> = {
            let edge_map: &'a EdgeMap<S> = &self.edge_map;
            super::lookup_tile(first, edge_map)
                .expect("failed to lookup the same tile in edge map")
        };
        let first: &TilePlacement<S> = &first.placement;
        {
            let mut ref_placements: RefMut<'_, PlacementMap<'a, _>> = self.placements.borrow_mut();
            let placements: &'_ mut PlacementMap<'a, _> = ref_placements.borrow_mut();
            PlacementMap::<'a, _>::push(placements, first);
        }
        TileCandidate::RootTiles(Cell::new(tiles))
    }
    fn is_impossible(&self, candidate: &Self::Candidate) -> bool {
        !self.is_solution(candidate) && candidate.is_empty()
    }
    fn is_solution(&self, _candidate: &Self::Candidate) -> bool {
        self.placements.borrow().len() == self.tiles.len()
    }
    fn first_extension(
        &self,
        _candidate: <Self as BFSProblem<'a>>::Candidate,
    ) -> Option<<Self as BFSProblem<'a>>::Candidate> {
        todo!()
    }
    fn next_extension(
        &'a self,
        candidate: <Self as BFSProblem<'a>>::Candidate,
    ) -> Option<<Self as BFSProblem<'a>>::Candidate> {
        let next: Option<&TilePlacement<S>> = candidate.next_extension(&self.edge_map);
        next.map(|new_placement| {
            self.placements.borrow_mut().replace_last(new_placement);
            candidate
        })
    }
    fn vec_backtrack(&'a self) -> Option<Self::Candidate> {
        self.vec_backtrack_with_capacity(self.tiles.len())
    }
}
