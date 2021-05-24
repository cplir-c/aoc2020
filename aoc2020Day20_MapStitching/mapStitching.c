
#include <stdio.h>

#include "tileMaps.c"
#include "mapStitching.h"

#ifndef __MAP_STITCHING_C
#define __MAP_STITCHING_C 1
/* intSqrt binary searches for the square root of the given area */
static fu16 intSqrt(fu16 area) {
    fu16 upperBound = area >> 1;
    fu16 lowerBound = 2;
    while (upperBound - lowerBound > 1) {
        fu16 middleBound = (upperBound + lowerBound) >> 1;
        fu16 middleBoundArea = middleBound * middleBound;
        if (middleBoundArea > area) {
            upperBound = middleBound;
        } else if (middleBoundArea < area) {
            lowerBound = middleBound;
        } else { // middleBoundArea == area
            return middleBound;
        }
    }
    return lowerBound;
}

static fu64 findResult(fu16 mapSideSize, EdgeReferenceVector* placements) {
    fu16 mapSize = mapSideSize * mapSideSize;
    fu64 result = getTileIdFromPlacement(getIndexEdgeReferenceVector(placements, 0));
    result *= getTileIdFromPlacement(getIndexEdgeReferenceVector(placements, mapSideSize - 1));
    result *= getTileIdFromPlacement(getItemEdgeReferenceVector(placements));
    result *= getTileIdFromPlacement(getIndexEdgeReferenceVector(placements, mapSize - mapSideSize));
    return result;
}

static fu64 pieceTogetherMap(TileVector* tiles, fu16ToEdgeReferenceVectorOpenHashMap* edges) {
    fu16 tileCount = tiles -> contentCount;
    fu16 mapSideSize = intSqrt(tileCount);
    EdgeReferenceVector placements;
    if (!construct2EdgeReferenceVector(&placements, tileCount)) {
        fprintf(stderr, "failed to construct placement vector\n");
        return 0;
    }

    fu16ToVoidOpenHashMap placedTileIDs;
    if (!construct2fu16ToVoidOpenHashMap(&placedTileIDs, tileCount)) {
        fprintf(stderr, "failed to construct placed tile set\n");
        destructEdgeReferenceVector(&placements);
        return 0;
    }

    fu16Vector edgeReferenceIndices;
    if (!construct2fu16Vector(&edgeReferenceIndices, tileCount)) {
        fprintf(stderr, "failed to construct edge reference index vector\n");
        destructfu16ToVoidOpenHashMap(&placedTileIDs);
        destructEdgeReferenceVector(&placements);
        return 0;
    }

    #ifdef DEBUG
    printf("entering base tile placing loop\n");
    #endif
    Tile* tile = getIndexTileVector(tiles, 0);
    for (fu16 tileIndex = 0; tileIndex < tileCount; ++tileIndex, ++tile) {
        Void v;
        #ifdef DEBUG
        printf("\tattempting to place base tile ID %lu in the placedTileIDs map\n", tile -> tileID);
        #endif
        if (addItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID), &v) == NULL) {
            #ifdef DEBUG
            printf("\tfailed to place base tile ID %lu in the placedTileIDs map\n", tile -> tileID);
            #endif
            continue;
        }
        #ifdef DEBUG
        printf("\tsucceeded to place base tile ID %lu in the placedTileIDs map\n", tile -> tileID);
        printf("\tentering base tile rotation loop\n");
        #endif
        for (Side side = Top; side <= Left; ++side) {
            #ifdef DEBUG
            printf("\t\trotating base tile to face side number %u (%s) up\n", side, SIDE_STRING[side]);
            #endif
            fu16 tileEdge = (tile -> sides)[side].forward;
            fu16ToEdgeReferenceVectorHashBucket* edgeRefBucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, &tileEdge);
            if (edgeRefBucket == NULL) {
                #ifdef DEBUG
                printf("\t\tlooking up this tile's %s side failed to match *any* tile vectors\n", SIDE_STRING[side]);
                #endif
                removeItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID));
                continue;
            }
            EdgeReferenceVector* edgeRefVec = &(edgeRefBucket -> value);
            fu16 edgeRefCount = edgeRefVec -> contentCount;
            #ifdef DEBUG
            printf("\t\tfound an edge reference vector of %lu items\n", edgeRefCount);
            #endif
            EdgeReference* edgeRef = getIndexEdgeReferenceVector(edgeRefVec, 0);
            if (edgeRef == NULL) {
                #ifdef DEBUG
                printf("\t\tfailed to get first edge reference in a vector of %lu edge references somehow\n", edgeRefCount);
                #endif
                removeItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID));
                continue;
            }
            #ifdef DEBUG
            printf("\t\tgot the first edge reference as a pointer, entering edge reference filtering loop\n");
            #endif
            for (fu16 edgeRefIndex = 0; edgeRefIndex < edgeRefCount; ++edgeRefIndex, ++edgeRef) {
                #ifdef DEBUG
                printf("\t\t\tselected edge reference index %ld, checking tile pointer and for forwards orientation\n", edgeRefIndex);
                #endif
                if ((edgeRef -> tile) != tile || (edgeRef -> backwards)) {
                    #ifdef DEBUG
                    printf("\t\t\tskipping edge reference index %ld because %s\n", edgeRefIndex, (edgeRef -> tile) == tile? "tile did not match the selected base tile": "tile was backwards");
                    #endif
                    continue;
                }
                // looked up the edge reference, so add it to placements
                #ifdef DEBUG
                printf("\t\t\ttrying to add edge reference to placements vector\n");
                #endif
                if (!addItemEdgeReferenceVector(&placements, edgeRef)) {
                    #ifdef DEBUG
                    printf("\t\t\tfailed to add edge reference to placements vector, skipping\n");
                    #endif
                    continue;
                }
                #ifdef DEBUG
                printf("\t\t\ttrying to add edge reference index to edge reference index vector\n");
                #endif
                if (!addItemfu16Vector(&edgeReferenceIndices, &edgeRefIndex)) {
                    #ifdef DEBUG
                    printf("\t\t\tfailed to add edge reference index to edge reference index vector, skipping\n");
                    #endif
                    removeItemEdgeReferenceVector(&placements);
                    continue;
                }
                #ifdef DEBUG
                printf("\t\t\tentering backtracking loop!\n");
                #endif
                bool success = backtrackStitching(tileCount, mapSideSize, edges, &placements, &placedTileIDs, &edgeReferenceIndices);

                if (success) {
                    #ifdef DEBUG
                    printf("\t\t\tbacktracking loop succeeded!\n");
                    #endif
                    destructfu16Vector(&edgeReferenceIndices);
                    destructfu16ToVoidOpenHashMap(&placedTileIDs);
                    fu64 result = findResult(mapSideSize, &placements);
                    destructEdgeReferenceVector(&placements);
                    return result;
                }
                #ifdef DEBUG
                printf("\t\t\tbacktracking loop failed, removing placement\n");
                #endif
                removeItemEdgeReferenceVector(&placements);
            }
        }
        fprintf(stderr, "failed to find a stitching solution for tile ID %lu, removing it from the tile id map\n", tile -> tileID);
        removeItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID));
    }

    fprintf(stderr, "failed to find a stitching solution for tile count %lu\n", tileCount);
    destructfu16Vector(&edgeReferenceIndices);
    destructfu16ToVoidOpenHashMap(&placedTileIDs);
    destructEdgeReferenceVector(&placements);
    return 0;
}

inline static fu16* getEdgeInteger(Placement* placement, Side side) {
    return &(
        (
            (placement -> tile)
            -> sides
        )[ ((placement -> side) + side) & 3 ]
        .forward
    ) + (placement -> backwards);
}

inline static char* getEdgeString(Placement* placement, Side side) {
    return *(
        &(
            (
                (placement -> tile)
                -> sides
            )[ ((placement -> side) + side) & 3 ]
            .forwardString
        ) + (placement -> backwards)
    );
}
inline static fu16 getTileIdFromPlacement(Placement* placement) {
    return (placement -> tile) -> tileID;
}

bool backtrackStitching(fu16 tileCount, fu16 mapSideSize, fu16ToEdgeReferenceVectorOpenHashMap* edges, EdgeReferenceVector* placements, fu16ToVoidOpenHashMap* placedTileIDs, fu16Vector* edgeReferenceIndices) {
    // first, lookup the edge references for the next square
    // if there's a edge reference index for the next square, start after it and
    // filter by left side constraint and top side constraint
    // filter by placed tile IDs
    // if nothing remains, go back up a level
    #ifdef DEBUG
    printf("entering backtrack stitching loop\n");
    #endif
    do {
        fu16 placedTileCount = placements -> contentCount;
        fu16 edgeReferenceIndexCount = edgeReferenceIndices -> contentCount;
        #ifdef DEBUG
        printf("\tplaced tile count: %lu, edge ref index count: %lu\n", placedTileCount, edgeReferenceIndexCount);
        #endif
        if (edgeReferenceIndexCount > placedTileCount + 1) {
            if (trimToSizefu16Vector(edgeReferenceIndices, placedTileCount + 1)) {
                #ifdef DEBUG
                printf("\ttrimmed edge reference index vector successfully\n");
                #endif
                edgeReferenceIndexCount = placedTileCount + 1;
            } else {
                fprintf(stderr, "failed to trim edge reference index vector to size %ld\n", placedTileCount + 1);
                return 0;
            }
        }
        fu16 edgeIndex = 0;
        if (edgeReferenceIndexCount == placedTileCount + 1 && !popItemfu16Vector(edgeReferenceIndices, &edgeIndex)) {
            fprintf(stderr, "failed to pop old edge reference index from vector\n");
            return 0;
        }
        // lookup the base edge reference vector for the next square
        Placement* leftPlacement;
        Placement* upPlacement;
        bool filterLeft = false;
        EdgeReferenceVector* unconstrained;
        {
            fu16ToEdgeReferenceVectorHashBucket* unconstrainedBucket;
            if (placedTileCount < mapSideSize){
                // only the left side exists
                leftPlacement = getItemEdgeReferenceVector(placements);
                upPlacement = NULL;
                unconstrainedBucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, getEdgeInteger(leftPlacement, Right));
                #ifdef DEBUG
                printf("\tonly the left side is there\n");
                #endif
            } else if ((placedTileCount % mapSideSize) == 0) {
                // only the up side exists
                leftPlacement = NULL;
                upPlacement = getIndexEdgeReferenceVector(placements, placedTileCount - mapSideSize);
                unconstrainedBucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, getEdgeInteger(upPlacement, Bottom));
                #ifdef DEBUG
                printf("\tonly the up side is there\n");
                #endif
            } else {
                // both up and left sides exist
                leftPlacement = getItemEdgeReferenceVector(placements);
                upPlacement = getIndexEdgeReferenceVector(placements, placedTileCount - mapSideSize);
                unconstrainedBucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, getEdgeInteger(upPlacement, Bottom));
                filterLeft = true;
                #ifdef DEBUG
                printf("\tboth the up and left sides are there\n");
                #endif
            }
            unconstrained = &(unconstrainedBucket -> value);
        }
        fu16 unconstrainedCount = unconstrained -> contentCount;
        #ifdef DEBUG
        printf("\tunconstrained edge reference count: %lu\n", unconstrainedCount);
        #endif
        bool continueDoLoop = false;
        #ifdef DEBUG
        printf("\tentering edge reference filter loop\n");
        #endif
        for (; edgeIndex < unconstrainedCount; ++edgeIndex) {
            #ifdef DEBUG
            printf("\t\tCurrent edge reference index: %lu out of %lu\n", edgeIndex, unconstrainedCount);
            #endif
            EdgeReference* edgeRef = &((unconstrained -> contents)[edgeIndex]);
            Tile* referencedTile = edgeRef -> tile;
            fu16 tileID = referencedTile -> tileID;
            // filter by placed tile IDs
            fu16ToVoidHashBucket* voidPtr = getItemfu16ToVoidOpenHashMap(placedTileIDs, &tileID);
            if (voidPtr != NULL) {
                // found this tileID placed already
                #ifdef DEBUG
                printf("\t\tTile ID %lu was placed already.\n", tileID);
                #endif
                continue;
            }
            if (filterLeft && *getEdgeInteger(edgeRef, Left) != *getEdgeInteger(leftPlacement, Right)) {
                // filter by the left side
                #ifdef DEBUG
                printf("\t\tTile ID %lu does not match the tile on its left: %lu %lu.\n", tileID, *getEdgeInteger(leftPlacement, Right), *getEdgeInteger(edgeRef, Left));
                #endif
                continue;
            }
            // good place for left and up matching assertions
            if (!filterLeft && leftPlacement != NULL && *getEdgeInteger(edgeRef, Left) != *getEdgeInteger(leftPlacement, Right)) {
                fprintf(stderr, "lookup of side %lu on the left failed, crying silently\n", *getEdgeInteger(leftPlacement, Right));
                #ifdef DEBUG
                printf("\t\tTile ID %lu does not match lookup on its left: %lu %lu.\n", tileID, *getEdgeInteger(leftPlacement, Right), *getEdgeInteger(edgeRef, Left));
                #endif
                continue;
            }
            if (upPlacement != NULL && *getEdgeInteger(edgeRef, Top) != *getEdgeInteger(upPlacement, Bottom)) {
                fprintf(stderr, "lookup of side %lu on the upside failed, crying silently\n", *getEdgeInteger(upPlacement, Bottom));
                #ifdef DEBUG
                printf("\t\tTile ID %lu does not match lookup on its top: %lu %lu.\n", tileID, *getEdgeInteger(upPlacement, Bottom), *getEdgeInteger(edgeRef, Top));
                #endif
                continue;
            }


            #ifdef DEBUG
            printf("\t\tplaced tile succcessfully!");
            #endif
            // time to place this thing
            ++edgeIndex;
            if (!addItemfu16Vector(edgeReferenceIndices, &edgeIndex)) {
                fprintf(stderr, "failed to add edge reference index to edge reference index vector\n");
                return 0;
            }
            Void v;
            if (!addItemfu16ToVoidOpenHashMap(placedTileIDs, &tileID, &v)) {
                fprintf(stderr, "failed to add tile ID to placed tile IDs\n");
                return 0;
            }
            if (!addItemEdgeReferenceVector(placements, edgeRef)) {
                fprintf(stderr, "failed to add placement to placement vector\n");
                return 0;
            }
            if ((placements -> contentCount) == tileCount) {
                printf("found a solution to your %lu tiles problem\n", tileCount);
                return true;
            }

            continueDoLoop = true;
            break;
        }
        if (continueDoLoop) {
            continue;
        }
        // else we fell through the loop without finding a match, so pop the current data

        Placement* lastPlacement = getItemEdgeReferenceVector(placements);
        if (lastPlacement == NULL) {
            fprintf(stderr, "failed to get last placement\n");
            return 0;
        }
        Tile* lastPlacedTile = lastPlacement -> tile;
        fu16 lastPlacedTileID = lastPlacedTile -> tileID;
        if (!removeItemfu16ToVoidOpenHashMap(placedTileIDs, &lastPlacedTileID)) {
            fprintf(stderr, "failed to remove tile id %lu from placed tile IDs\n", lastPlacedTileID);
            return 0;
        }
        if (!trimToSizeEdgeReferenceVector(placements, placedTileCount - 1)) {
            fprintf(stderr, "failed to shrink placement vector to size %lu\n", placedTileCount - 1);
            return 0;
        }
        if (!removeItemfu16Vector(edgeReferenceIndices)) {
            fprintf(stderr, "failed to pop edge reference index from edge reference index vector\n");
            return 0;
        }
    } while ((placements -> contentCount) > 1);
    // failed to find a solution

    return 0;
}
#endif
