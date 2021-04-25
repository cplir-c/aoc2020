
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "shortIntNames.h"
#include "tileStructures.h"
#include "mapStitching.c"
//include "tileMaps.h"
#include "tileStrings.c"
#include "tileParsing.h"
#ifdef DEBUG
    #include "testing.c"
#endif
#include "aoc2020Day20_MapStitching.h"
#include "buildTileStructures.c"

extern int main() {
    #ifdef DEBUG
    if (!runTests()) {
        fprintf(stderr, "Something's broken; exiting badly.");
        return 2;
    }
    #endif
    if (!testStitching(exampleTilesString, 20899048083289)) {
        printf("failed example test\n");
    } else {
        printf("example test passed\n");
    }
    #ifdef PRODUCTION
    fu64 payloadCornerIDProduct = findCornerIDProduct(payloadTilesString);
    printf("Payload emitted %lu\n", payloadCornerIDProduct);
    #endif
    return 0;
}

static fu64 findCornerIDProduct(char tilesString[]) {
    fu16 tileCount = countTiles(tilesString);
    printf("tile count: %lu\n", tileCount);
    printf("malloc location: %lu\n", (usize) &malloc);

    char** tilesPointers = malloc(sizeof(char*) * tileCount);
    if (tilesPointers == NULL) {
        fprintf(stderr, "failed to allocate tilestring pointers space\n");
        return 0;
    }
    // measured, long string is 17569 bytes long
    tilesString = strndup(tilesString, 18000);
    // split up the single tiles string into tiles strings and record the locations
    findTiles(tilesString, tilesPointers);
    #ifdef DEBUG
    for (fu16 i = 0; i < tileCount; ++i) {
        printf("Tile #%lu:    %s\n\n", i, tilesPointers[i]);
    }
    #endif

    TileVector tiles;
    if (!construct2TileVector(&tiles, tileCount)) {
        fprintf(stderr, "failed to initialize tile vector\n");
        free(tilesString);
        free(tilesPointers);
        return 0;
    }

    charVector strings;
    if (!construct2charVector(&strings, tileCount * (/*all sides*/ 8 * 11) + 1)) {
        fprintf(stderr, "failed to initialize charvector\n");
        destructTileVector(&tiles);
        free(tilesString);
        free(tilesPointers);
        return 0;
    }
    if (!buildTileList(tileCount, tilesPointers, &tiles, &strings)) {
        fprintf(stderr, "failed to build tile list\n");
        destructcharVector(&strings);
        destructTileVector(&tiles);
        free(tilesString);
        free(tilesPointers);
        return 0;
    }

    fu16ToEdgeReferenceVectorOpenHashMap edgeMap;
    if (!construct3fu16ToEdgeReferenceVectorOpenHashMap(&edgeMap, 0.5f, ((usize) tileCount) * 8)) {
        fprintf(stderr, "failed to initialize edge map\n");
        destructcharVector(&strings);
        destructTileVector(&tiles);
        free(tilesString);
        free(tilesPointers);
        return 0;
    }
    if(!buildEdgeMap(&tiles, &edgeMap)) {
        fprintf(stderr, "failed to build edge map\n");
        destructfu16ToEdgeReferenceVectorOpenHashMap(&edgeMap);
        destructcharVector(&strings);
        destructTileVector(&tiles);
        free(tilesString);
        free(tilesPointers);
        return 0;
    }

    printf("built data structures, calculating\n");
    fu64 cornerIDProduct = pieceTogetherMap(&tiles, &edgeMap);

    destructfu16ToEdgeReferenceVectorOpenHashMap(&edgeMap);
    destructcharVector(&strings);
    destructTileVector(&tiles);
    free(tilesString);
    free(tilesPointers);
    return cornerIDProduct;
}

inline bool testStitching(char tilesString[], fu64 expectedCornerIDProduct) {
    return (bool) (findCornerIDProduct(tilesString) == expectedCornerIDProduct);
}
