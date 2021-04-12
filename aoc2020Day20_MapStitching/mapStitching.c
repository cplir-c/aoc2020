
#include "tileMaps.c"
#include "mapStitching.h"
#include <stdio.h>

/* intSqrt binary searches for the square root of the given area */
fu16 intSqrt(fu16 area) {
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

fu64 findResult(fu16 mapSideSize, EdgeReferenceVector* placements) {
    fu16 mapSize = mapSideSize * mapSideSize;
    fu64 result = getTileIdFromPlacement(getIndexEdgeReferenceVector(placements, 0));
    result *= getTileIdFromPlacement(getIndexEdgeReferenceVector(placements, mapSideSize - 1));
    result *= getTileIdFromPlacement(getItemEdgeReferenceVector(placements));
    result *= getTileIdFromPlacement(getIndexEdgeReferenceVector(placements, mapSize - mapSideSize));
    return result;
}

fu64 pieceTogetherMap(TileVector* tiles, fu16ToEdgeReferenceVectorOpenHashMap* edges) {
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


    Tile* tile = getIndexTileVector(tiles, 0);
    for (fu16 tileIndex = 0; tileIndex < tileCount; ++tileIndex, ++tile) {
        Void v;
        if (addItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID), &v) == NULL) {
            continue;
        }
        fu16 tileEdge = (tile -> sides)[Right].forward;
        fu16ToEdgeReferenceVectorHashBucket* edgeRefBucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, &tileEdge);
        if (edgeRefBucket == NULL) {
            removeItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID));
            continue;
        }
        EdgeReferenceVector* edgeRefVec = &(edgeRefBucket -> value);

        fu16 edgeRefCount = edgeRefVec -> contentCount;
        EdgeReference* edgeRef = getIndexEdgeReferenceVector(edgeRefVec, 0);
        if (edgeRef == NULL) {
            removeItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID));
            continue;
        }
        for (fu16 edgeRefIndex = 0; edgeRefIndex < edgeRefCount; ++edgeRefIndex, ++edgeRef) {
            if ((edgeRef -> tile) != tile || (edgeRef -> backwards)) {
                continue;
            }
            // looked up the edge reference, so add it to placements
            if (!addItemEdgeReferenceVector(&placements, edgeRef)) {
                continue;
            }
            if (!addItemfu16Vector(&edgeReferenceIndices, &edgeRefIndex)) {
                removeItemEdgeReferenceVector(&placements);
                continue;
            }

            bool success = backtrackStitching(tileCount, mapSideSize, edges, &placements, &placedTileIDs, &edgeReferenceIndices);
            if (success) {
                destructfu16Vector(&edgeReferenceIndices);
                destructfu16ToVoidOpenHashMap(&placedTileIDs);
                fu64 result = findResult(mapSideSize, &placements);
                destructEdgeReferenceVector(&placements);
                return result;
            }

            removeItemEdgeReferenceVector(&placements);
        }

        removeItemfu16ToVoidOpenHashMap(&placedTileIDs, &(tile -> tileID));
    }

    fprintf(stderr, "failed to find a stitching solution for tile count %lu\n", tileCount);
    destructfu16Vector(&edgeReferenceIndices);
    destructfu16ToVoidOpenHashMap(&placedTileIDs);
    destructEdgeReferenceVector(&placements);
    return 0;
}

fu16* getEdgeInteger(Placement* placement, Side side) {
    return &(
        (
            (placement -> tile)
            -> sides
        )[ ((placement -> side) + side) & 3 ]
        .forward
    ) + (placement -> backwards);
}

char* getEdgeString(Placement* placement, Side side) {
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
fu16 getTileIdFromPlacement(Placement* placement) {
    return (placement -> tile) -> tileID;
}

bool backtrackStitching(fu16 tileCount, fu16 mapSideSize, fu16ToEdgeReferenceVectorOpenHashMap* edges, EdgeReferenceVector* placements, fu16ToVoidOpenHashMap* placedTileIDs, fu16Vector* edgeReferenceIndices) {
    // first, lookup the edge references for the next square
    // if there's a edge reference index for the next square, start after it and
    // filter by left side constraint and top side constraint
    // filter by placed tile IDs
    // if nothing remains, go back up a level
    do {
        fu16 placedTileCount = placements -> contentCount;
        fu16 edgeReferenceIndexCount = edgeReferenceIndices -> contentCount;
        if (edgeReferenceIndexCount > placedTileCount + 1) {
            if (trimToSizefu16Vector(edgeReferenceIndices, tileCount + 1)) {
                placedTileCount = tileCount + 1;
            } else {
                fprintf(stderr, "failed to trim edge reference index vector to size %ld", placedTileCount + 1);
                return 0;
            }
        }
        fu16 edgeIndex = 0;
        if (edgeReferenceIndexCount == placedTileCount + 1 && !popItemfu16Vector(edgeReferenceIndices, &edgeIndex)) {
            fprintf(stderr, "failed to pop old edge reference index from vector");
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
        } else if ((placedTileCount % mapSideSize) == 0) {
            // only the up side exists
            leftPlacement = NULL;
            upPlacement = getIndexEdgeReferenceVector(placements, placedTileCount - mapSideSize);
            unconstrainedBucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, getEdgeInteger(upPlacement, Bottom));
        } else {
            // both up and left sides exist
            leftPlacement = getItemEdgeReferenceVector(placements);
            upPlacement = getIndexEdgeReferenceVector(placements, placedTileCount - mapSideSize);
            unconstrainedBucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, getEdgeInteger(upPlacement, Bottom));
            filterLeft = true;
        }
        unconstrained = &(unconstrainedBucket -> value);
        }
        fu16 unconstrainedCount = unconstrained -> contentCount;
        bool continueDoLoop = false;
        for (; edgeIndex < unconstrainedCount; ++edgeIndex) {
            EdgeReference* edgeRef = &((unconstrained -> contents)[edgeIndex]);
            Tile* referencedTile = edgeRef -> tile;
            fu16 tileID = referencedTile -> tileID;
            // filter by placed tile IDs
            fu16ToVoidHashBucket* voidPtr = getItemfu16ToVoidOpenHashMap(placedTileIDs, &tileID);
            if (voidPtr != NULL) {
                // found this tileID placed already
                continue;
            }
            if (filterLeft && *getEdgeInteger(edgeRef, Left) != *getEdgeInteger(leftPlacement, Right)) {
                // filter by the left side
                continue;
            }
            // good place for left and up matching assertions
            if (!filterLeft && leftPlacement != NULL && *getEdgeInteger(edgeRef, Left) != *getEdgeInteger(leftPlacement, Right)) {
                fprintf(stderr, "lookup of side %lu on the left failed, crying silently\n", *getEdgeInteger(leftPlacement, Right));
                continue;
            }
            if (upPlacement != NULL && *getEdgeInteger(edgeRef, Top) != *getEdgeInteger(upPlacement, Bottom)) {
                fprintf(stderr, "lookup of side %lu on the upside failed, crying silently\n", *getEdgeInteger(upPlacement, Bottom));
                continue;
            }

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
