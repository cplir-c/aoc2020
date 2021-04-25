
#include <stdbool.h>
#include "shortIntNames.h"
#include "tileMaps.h"

#ifndef __BUILD_TILE_STRUCTURES_H
#define __BUILD_TILE_STRUCTURES_H 1

/* addEdgeToEdgeMap adds an EdgeReference to the EdgeReference MultiMap */
static bool addEdgeToEdgeMap(fu16* edgeInt, EdgeReference* edgeRef, fu16ToEdgeReferenceVectorOpenHashMap* edges);
/* buildEdgeMap fills in the edge map with
 * the edge references of the tiles its given
 */
static bool buildEdgeMap(TileVector* tiles, fu16ToEdgeReferenceVectorOpenHashMap* edges);
#endif
