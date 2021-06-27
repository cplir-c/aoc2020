use std::borrow::Borrow;
use std::borrow::BorrowMut;
use std::cell::Cell;
use std::cell::RefCell;
use std::collections::HashSet;
use std::marker::PhantomData;

use super::backtracking::BFSProblem;
use super::DebugWrapper;
use super::EdgeMap;
use super::EdgeReference;
use super::EdgePlacement;
use super::PlacementMap;
use super::Side;
use super::Tile;
use super::tile_structures;
use super::TilePlacement;

mod borrow_owning;

type TilePlacementMap<'a, 'b, S> = PlacementMap<'b, TilePlacement<'a, S>>;
#[derive(Debug)]
pub struct TileProblem<'a, 'b, S: Borrow<str>> {
    tiles: &'a [Tile<'a, S>],
    edge_map: EdgeMap<'a, S>,
    placed_tile_ids: RefCell<HashSet<u16>>,
    placements: RefCell<TilePlacementMap<'a, 'b, S>>,
    phantom: PhantomData<&'b TileCandidate<'a, 'b, S>>,
}
#[derive(Debug)]
pub enum TileCandidate<'a, 'b, S: Borrow<str>> {
    RootTiles(Cell<&'b [Tile<'a, S>]>),
    LeafTiles(Cell<&'b [EdgeReference<'a, S>]>),
}
impl<'a, 'b, S: Borrow<str>> TileCandidate<'a, 'b, S> {
    fn is_empty(&self) -> bool {
        match self {
            TileCandidate::RootTiles(tiles) => tiles.get().is_empty(),
            TileCandidate::LeafTiles(places) => places.get().as_ref().is_empty(),
        }
    }
    fn next_extension(&'_ self, problem: &'b TileProblem<'a, 'b, S>) -> Option<&'b TilePlacement<'a, S>> { 
        match self {
            TileCandidate::RootTiles(tiles) => tiles.get().split_last().and_then(|(last, rest)| {
                tiles.set(rest);
                let new_root = tile_structures::lookup_tile(&last, &problem.edge_map).map(|edge_ref| &edge_ref.placement);
                if let Some(root) = new_root {
                    problem.placements.borrow_mut().replace_last(root);
                }
                new_root
            }),
            TileCandidate::LeafTiles(tile_placements) => {
                let mut placements_slice = tile_placements.get();
                let (left_bits, up_bits) = {
                    let placements_ref = problem.placements.borrow();
                    if let Some(position) = placements_ref.last_position() {
                        let nearby = placements_ref.get_adjacents(position);
                        (nearby.left().map(|l| l[Side::Right]),
                           nearby.up().map(|u| u[Side::Bottom]))
                    } else {
                        return None;
                    }
                };
                loop {
                    let placement = if let Some((first, slice_rest)) = placements_slice.split_first() {
                        placements_slice = slice_rest;
                        &first.placement
                    } else {
                        tile_placements.set(&[]);
                        return None;
                    };
                    // filter by adjacent tiles
                    if let Some(l_bits) = left_bits {
                        if l_bits != placement[Side::Left] {
                            continue;
                        }
                    }
                    if let Some(u_bits) = up_bits {
                        if u_bits != placement[Side::Top] {
                            continue;
                        }
                    }
                    // filter by unplaced tile ids
                    let tile_id = placement.tile.tile_id;
                    if problem.placed_tile_ids.borrow().contains(&tile_id) {
                        continue;
                    }
                    // place tile
                    // replace old placed tile
                    let opt_old_placement = problem.placements.borrow_mut().replace_last(placement);
                    { // remove old placed tile's tile id
                        let mut mut_tile_ids = problem.placed_tile_ids.borrow_mut();
                        if let Some(old_placement) = opt_old_placement {
                            mut_tile_ids.remove(&old_placement.tile.tile_id);
                        }
                        // add new placed tile's tile id
                        assert!(mut_tile_ids.insert(tile_id));
                    }
                    tile_placements.set(placements_slice);
                    return Some(placement);
                }
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
        let placed_tile_ids = RefCell::new(HashSet::with_capacity(area));
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
            placements.push(first).expect("failed to push placement, placements full when inserting base tile");
        }
        TileCandidate::<'a, 'b, S>::RootTiles(Cell::new(tiles))
    }
    fn is_impossible(&self, candidate: &Self::Candidate) -> bool {
        !self.is_solution(candidate) && candidate.is_empty()
    }
    fn is_solution(&self, candidate: &Self::Candidate) -> bool {
        
        println!("tile problem state: {:#?}, candidate: {:?}", unsafe {
            std::mem::transmute::<&TileProblem<S>, &TileProblem<DebugWrapper<S>>>(self)
        }, unsafe {
            std::mem::transmute::<&TileCandidate<S>, &TileCandidate<DebugWrapper<S>>>(candidate)
        });
        self.placements.borrow().len() == self.tiles.len()
    }
    fn first_extension(
        &'b self,
        _candidate: <Self as BFSProblem<'b>>::Candidate,
    ) -> Option<<Self as BFSProblem<'b>>::Candidate> {
        let (left_bits, up_bits) = {
            let opt_pos = {self.placements.borrow_mut().peek_pos()};
            if let Some(position) = opt_pos {
                let placements_ref = self.placements.borrow();
                let nearby = placements_ref.get_adjacents(position);
                (nearby.left().map(|l| l[Side::Right]),
                   nearby.up().map(|u| u[Side::Bottom]))
            } else {
                return None;
            }
        };
        let mut placements_slice = {
            let edge_placement = if let Some(left) = left_bits {
                EdgePlacement {
                    side: Side::Left,
                    bits: left
                }
            } else if let Some(up) = up_bits {
                EdgePlacement {
                    side: Side::Top,
                    bits: up
                }
            } else {
                panic!("failed to get nearby placed tiles on finding next layer")
            };
            if let Some(placement_vec) = self.edge_map.get(&edge_placement) {
                placement_vec.as_slice()
            } else {
                return None;
            }
        };
        loop {
            let placement = if let Some((first, slice_rest)) = placements_slice.split_first() {
                placements_slice = slice_rest;
                &first.placement
            } else {
                return None;
            };
            // filter by adjacent tiles
            if let Some(l_bits) = left_bits {
                if l_bits != placement[Side::Left] {
                    continue;
                }
            }
            if let Some(u_bits) = up_bits {
                if u_bits != placement[Side::Top] {
                    continue;
                }
            }
            // filter by unplaced tile ids
            let tile_id = placement.tile.tile_id;
            if self.placed_tile_ids.borrow().contains(&tile_id) {
                continue;
            }
            // place tile
            { // remove old placed tile's tile id
                let mut mut_tile_ids = self.placed_tile_ids.borrow_mut();
                // add new placed tile's tile id
                assert!(mut_tile_ids.insert(tile_id));
            }
            return Some(TileCandidate::LeafTiles(Cell::new(placements_slice)));
        }
    }
    fn next_extension(
        &'b self,
        candidate: <Self as BFSProblem<'b>>::Candidate,
    ) -> Option<<Self as BFSProblem<'b>>::Candidate> {
        let next: Option<&'b TilePlacement<'a, S>> = candidate.next_extension(&self);
        next.map(|_new_placement| {
            candidate
        })
    }
    fn vec_backtrack(&'b self) -> Option<Self::Candidate> {
        self.vec_backtrack_with_capacity(self.tiles.len())
    }
    fn remove_extension(&'b self, _candidate: <Self as BFSProblem<'b>>::Candidate) {
        // only called when there's something to remove
        let placement = self.placements.borrow_mut().pop().unwrap();
        assert!(self.placed_tile_ids.borrow_mut().remove(&placement.tile.tile_id), "removing tile id: {}, tile ids: {:#?}", placement.tile.tile_id, self.placed_tile_ids.borrow());
    }
}
