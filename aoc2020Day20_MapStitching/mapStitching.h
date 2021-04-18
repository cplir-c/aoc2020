#include "tileMaps.h"

#ifndef __MAP_STITCHING_H
#define __MAP_STITCHING_H 1
/* finds the integer square root of the given area */
static fu16 intSqrt(fu16 area);
/* finds the result from the final placement grid */
static fu64 findResult(fu16 mapSideSize, EdgeReferenceVector* placements);
/* pieceTogetherMap is the entrypoint for stitching a map together once
 * the tile objects are registered in the edge map.
 * It starts the stitching process by picking a tile and looking up its edge reference,
 * and then solving the stitching via backtracking
 */
extern fu64 pieceTogetherMap(TileVector* tiles, fu16ToEdgeReferenceVectorOpenHashMap* edges);

/* a little helper function to get sides of rotated placements */
static fu16* getEdgeInteger(Placement* placement, Side side);
/* the sibling of getEdgeInteger, to help with debugging */
static char* getEdgeString(Placement* placement, Side side);
/* a little helper function to get tile IDs from placements */
static fu16 getTileIdFromPlacement(Placement* placement);

/* backtrackStitching tries to solve an initial map state by stitching together tiles
 * and backtracking when it fails to find one
 */
static bool backtrackStitching(fu16 tileCount, fu16 mapSideSize, fu16ToEdgeReferenceVectorOpenHashMap* edges, EdgeReferenceVector* placements, fu16ToVoidOpenHashMap* placedTileIDs, fu16Vector* edgeReferenceIndices);
#endif
