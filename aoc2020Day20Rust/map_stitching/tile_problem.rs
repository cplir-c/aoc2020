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

type TilePlacementMap<'a, 'b, S> = PlacementMap<'b, TilePlacement<'a, S>>;

pub struct TileProblem<'a, 'b, S: Borrow<str>> {
    tiles: &'a [Tile<'a, S>],
    edge_map: EdgeMap<'a, S>,
    placed_tile_ids: HashSet<u16>,
    placements: RefCell<TilePlacementMap<'a, 'b, S>>,
    phantom: PhantomData<&'b TileCandidate<'a, 'b, S>>,
}

pub enum TileCandidate<'a, 'b, S: Borrow<str>> {
    RootTiles(Cell<&'b [Tile<'a, S>]>),
    LeafTiles(Cell<&'b [&'a TilePlacement<'a, S>]>),
}
impl<'a, 'b, S: Borrow<str>> TileCandidate<'a, 'b, S> {
    fn is_empty(&self) -> bool {
        match self {
            TileCandidate::RootTiles(tiles) => tiles.get().is_empty(),
            TileCandidate::LeafTiles(places) => places.get().as_ref().is_empty(),
        }
    }
    fn next_extension(&'_ self, edge_map: &'b EdgeMap<'a, S>) -> Option<&'b TilePlacement<'a, S>> {
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
    pub fn as_placements(&'b self) -> Option<Box<[TilePlacement<'a, S>]>> {
        self.placements.borrow_mut().as_box()
    }
}

impl<'a, 'b, S: Borrow<str>> BFSProblem<'b> for TileProblem<'a, 'b, S> {
    type Candidate = TileCandidate<'a, 'b, S>;
    fn root_candidate(&'b self) -> <Self as BFSProblem<'b>>::Candidate {
        let (first, tiles): (&'a Tile<S>, &'a [Tile<S>]) =
            self.tiles.split_first().expect("expected some tiles");
        // WLOG only consider an arbitrary orientation for each tile
        // rotating the root tile is equivalent to rotating the entire board
        
        let first: &'b EdgeReference<S> = {
            let edge_map: &'b EdgeMap<S> = &self.edge_map;
            super::lookup_tile(first, edge_map)
                .expect("failed to lookup the same tile in edge map")
        };
        let first: &'b TilePlacement<'a, S> = &first.placement;
        {
            let mut ref_placements = self.placements.borrow_mut();
            let placements: &'_ mut TilePlacementMap<'a, 'b, S> = ref_placements.borrow_mut();
            placements.push(first);
        }
        TileCandidate::<'a, 'b, S>::RootTiles(Cell::new(tiles))
    }
    fn is_impossible(&self, candidate: &Self::Candidate) -> bool {
        !self.is_solution(candidate) && candidate.is_empty()
    }
    fn is_solution(&self, _candidate: &Self::Candidate) -> bool {
        self.placements.borrow().len() == self.tiles.len()
    }
    fn first_extension(
        &self,
        _candidate: <Self as BFSProblem<'b>>::Candidate,
    ) -> Option<<Self as BFSProblem<'b>>::Candidate> {
        todo!()
    }
    fn next_extension(
        &'b self,
        candidate: <Self as BFSProblem<'b>>::Candidate,
    ) -> Option<<Self as BFSProblem<'b>>::Candidate> {
        let next: Option<&'b TilePlacement<'a, S>> = candidate.next_extension(&self.edge_map);
        next.map(|new_placement| {
            self.placements.borrow_mut().replace_last(new_placement);
            candidate
        })
    }
    fn vec_backtrack(&'b self) -> Option<Self::Candidate> {
        self.vec_backtrack_with_capacity(self.tiles.len())
    }
}
