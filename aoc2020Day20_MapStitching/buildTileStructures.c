
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#ifndef __BUILD_TILE_STRUCTURES_C
#define __BUILD_TILE_STRUCTURES_C 1

#include "tileMaps.c"
#include "buildTileStructures.h"

static bool addEdgeToEdgeMap(fu16* edgeInt, EdgeReference* edgeRef, fu16ToEdgeReferenceVectorOpenHashMap* edges) {
    fu16ToEdgeReferenceVectorHashBucket* bucket = getItemfu16ToEdgeReferenceVectorOpenHashMap(edges, edgeInt);
    if (bucket == NULL) {
        EdgeReferenceVector vec;
        if (!constructEdgeReferenceVector(&vec)) {
            fprintf(stderr, "failed to construct edge reference vector\n");
            return false;
        }
        bucket = addItemfu16ToEdgeReferenceVectorOpenHashMap(edges, edgeInt, &vec);
        if (bucket == NULL) {
            fprintf(stderr, "failed to create add edge reference vector to edge map\n");
            return false;
        }
    }
    EdgeReferenceVector* vec = &(bucket -> value);
    if (!addItemEdgeReferenceVector(vec, edgeRef)) {
        fprintf(stderr, "failed to add edge reference to edge reference vector\n");
        return false;
    }
    return true;
}

static bool buildEdgeMap(TileVector* tiles, fu16ToEdgeReferenceVectorOpenHashMap* edges){
    if (tiles == NULL || edges == NULL) {
        fprintf(stderr, "invalid null pointer argument\n");
        return false;
    }
    usize contentCount = tiles -> contentCount;
    usize tileIndex = 0;
    Tile* tile = getIndexTileVector(tiles, tileIndex);
    if (tile == NULL) {
        fprintf(stderr, "null tile element at index %lu", tileIndex);
        return false;
    }
    for (; tileIndex < contentCount; ++tileIndex, ++tile) {
        if (getIndexTileVector(tiles, tileIndex) != tile) {
            fprintf(stderr, "failed to index tiles properly with index %lu\n", tileIndex);
            return false;
        }
        #ifdef VERBOSE_DEBUG
        printf("edging tile id %lu %s\n", tile -> tileID, tile -> unrotatedTile);
        #endif
        Edge const* edge = &((tile -> sides)[0]);
        fu16 edgeIndex = 0;
        for (; edgeIndex < SIDE_COUNT; ++edgeIndex, ++edge) {
            #ifdef VERBOSE_DEBUG
            printf("%p edge is %ld bytes from the %p tile of size %lu\n", (void*) edge, ((isize) edge) - ((isize) tile), (void*) tile, sizeof(Tile));
            #endif
            fu16 edgeInteger = edge -> backward;
            #ifdef VERBOSE_DEBUG
            printf("index %lu edge integer %lu\n", edgeIndex, edgeInteger);
            #endif
            for (fu8 loop = 0; loop < 2; ++loop, edgeInteger = edge -> forward) {
                #ifdef VERBOSE_DEBUG
                printf("backwards? %hhu\n", (u8) (loop > 1));
                #endif
                EdgeReference edgeRef;
                edgeRef.backwards = loop > 1;
                edgeRef.side = (Side) edgeIndex;
                edgeRef.tile = tile;
                if (!addEdgeToEdgeMap(&edgeInteger, &edgeRef, edges)) {
                    fprintf(stderr, "failed to add edge to edge map %s\n", (&(edge -> forwardString))[loop - 1]);
                    return false;
                }
            }
        }
    }
    return true;
}

#endif
