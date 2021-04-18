
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "shortIntNames.h"
#include "tileStructures.h"
#include "mapStitching.c"
//include "tileMaps.h"
#include "tileStrings.c"
#include "aoc2020Day20_MapStitching.h"

extern int main() {
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

static fu16 countTiles(char tilesString[]) {
    char* charRef = (char*) tilesString;

    char chr = *charRef;
    fu16 tileCount = 1;
    while (chr != '\0') {
        if (chr == '\n') {
            char* nextCharRef = charRef + 1;
            chr = *nextCharRef;
            if (chr == '\0') {
                break;
            } else if (chr == '\n') {
                if (*(nextCharRef + 1) == '\0') {
                    break;
                } else {
                    ++tileCount;
                    charRef = nextCharRef;
                }
            }
        }
        ++charRef;
        chr = *charRef;
    }
    return tileCount;
}

static void findTiles(char tilesString[], char** tilePointerRef) {
    char* charRef = (char*) tilesString;
    *tilePointerRef = charRef;
    ++tilePointerRef;
    ++charRef;
    char chr = *charRef;

    while (chr != '\0') {
        if (chr == '\n') {
            char* nextCharRef = charRef + 1;
            chr = *nextCharRef;
            if (chr == '\0') {
                break;
            } else if (chr == '\n') {
                *charRef = '\0';
                charRef = nextCharRef;
                nextCharRef++;
                *tilePointerRef = nextCharRef;
                tilePointerRef++;
            }
        }
        ++charRef;
        chr = *charRef;
    }
}

static bool buildTileList(fu16 tileCount, char** tilesPointers, TileVector* tiles, charVector* strings) {
    printf("tile count at list build time: %lu\n", tileCount);
    for (fu16 tileIndex = 0; tileIndex < tileCount; ++tileIndex) {
        char* tileString = tilesPointers[tileIndex];
        if (!constructTileFromTileString(tileString, tiles, strings)) {
            fprintf(stderr, "failed to add tile %lu to tile vector\n", tileIndex);
            return false;
        }
    }
    return true;
}

static char* parseTileHeader(Tile* tile, char* tileStringPoint) {
    char* tileString = tileStringPoint;
    if (tileStringPoint[0] != 'T' || tileStringPoint[1] != 'i'
        || tileStringPoint[2] != 'l' || tileStringPoint[3] != 'e' || tileStringPoint[4] != ' ') {
        fprintf(stderr, "failed to parse \"Tile\" of tile %s\n", tileString);
        return NULL;
    }
    tileStringPoint += 5; // skip "Tile "
    tile -> tileID = strtoul(tileStringPoint, &tileStringPoint, 10);
    if (tileStringPoint[0] != ':' || tileStringPoint[1] != '\n') {
        fprintf(stderr, "failed to parse \":\\n\" of tile %s\n", tileString);
        return NULL;
    }
    tileStringPoint += 2; // skip ":\n"
    return tileStringPoint;
}

static bool constructIntEdgeFromCharEdge(Edge* edge) {
    edge -> backward = parseRowFromRowString(edge -> backwardString);
    if (edge -> backward == UINT_FAST16_MAX) {
        fprintf(stderr, "failed to construct backward int edge %s\n", edge -> backwardString);
        return false;
    }

    edge -> forward = parseRowFromRowString(edge -> forwardString);
    if (edge -> forward == UINT_FAST16_MAX) {
        fprintf(stderr, "failed to construct forward int edge %s\n", edge -> forwardString);
        return false;
    }
    return true;
}

static fu16 parseRowFromRowString(char* rowString) {
    char* pointer = rowString;
    char pointed = '\0';
    pointed = *pointer;
    fu16 row = 0;
    for (; pointed != '\0'; ++pointer) {
        row <<= 1;
        row |= (pointed == '#');
        if (pointer == NULL) {
            fprintf(stderr, "failed to parse row from row string\n");
            return UINT_FAST16_MAX;
        }
        pointed = *pointer;
    }
    return row;
}

static bool constructTileFromTileString(char* tileStringPoint, TileVector* tiles, charVector* strings) {

    Tile tile;    // look starting at tileStringPoint, and put the parse stop in tileStringPoint, parsing as base 10
    memset(&tile, 0, sizeof(tile)); // zero out the tile
    tileStringPoint = parseTileHeader(&tile, tileStringPoint);

    tile.unrotatedTile = tileStringPoint;

    char* tileTopEnd = strchr(tileStringPoint, (int) '\n');
    isize rowLength = (usize) tileTopEnd;
    rowLength -= (usize) tileStringPoint;
    ++rowLength;
    #ifdef DEBUG
    printf("row length (probably 11) %ld %p %p\n", rowLength, (void*) tileTopEnd, (void*) tileStringPoint);
    #endif
    if (rowLength < 10 || rowLength > 11) {
        fprintf(stderr, "rowLength invalid, failing\n");
        fprintf(stderr, "pointed to %s\n", tileTopEnd);
        return false;
    }

    tile.sides[Top].forwardString = addBlockcharVector(strings, rowLength * 2);
    if (tile.sides[Top].forwardString == NULL) {
        fprintf(stderr, "failed to allocate block for top strings of tile id %lu\n", tile.tileID);
        return false;
    }
    memcpy(tile.sides[Top].forwardString, tileStringPoint, rowLength - 1);
    tileStringPoint += rowLength - 1;
    char nullChar = '\0';
    *(tile.sides[Top].forwardString + rowLength - 1) = '\0';

    tile.sides[Top].backwardString = tile.sides[Top].forwardString + rowLength;
    for (fu16 i = 0; i < rowLength - 1; --tileStringPoint, ++i) {
        tile.sides[Top].backwardString[i] = *tileStringPoint;
    }
    setItemcharVector(strings, &nullChar);
    if (!constructIntEdgeFromCharEdge(&tile.sides[Top])) {
        fprintf(stderr, "failed to construct top edge of tile id %lu\n", tile.tileID);
        return false;
    }

    tileStringPoint = tile.unrotatedTile;
    tile.sides[Left].forwardString = addBlockcharVector(strings, rowLength * 2);
    if (tile.sides[Left].forwardString == NULL) {
        fprintf(stderr, "failed to allocate block for left strings of tile id %lu\n", tile.tileID);
        return false;
    }
    for (fu16 i = 0; i < rowLength - 1; tileStringPoint += rowLength, ++i) {
        tile.sides[Left].forwardString[i] = *tileStringPoint;
    }
    *(tile.sides[Left].forwardString + rowLength - 1) = '\0';

    tileStringPoint -= rowLength;
    // catch the bottom side forward
    tile.sides[Bottom].forwardString = tileStringPoint;
    tile.sides[Left].backwardString = tile.sides[Left].forwardString + rowLength;
    for (fu16 i = 0; i < rowLength - 1; tileStringPoint -= rowLength, ++i) {
        tile.sides[Left].backwardString[i] = *tileStringPoint;
    }
    setItemcharVector(strings, &nullChar);
    if (!constructIntEdgeFromCharEdge(&tile.sides[Left])) {
        fprintf(stderr, "failed to construct left edge of tile id %lu\n", tile.tileID);
        return false;
    }

    tileStringPoint = tile.sides[Bottom].forwardString + rowLength - 2;
    if (*tileStringPoint == '\0') {
        printf("found null at bottom of tile\n");
        --tileStringPoint;
    }
    tile.sides[Bottom].backwardString = addBlockcharVector(strings, rowLength);
    if (tile.sides[Bottom].backwardString == NULL) {
        fprintf(stderr, "failed to add block for bottom backwards string on tile id %lu\n", tile.tileID);
        return false;
    }
    for (fu16 i = 0; i < rowLength - 1; --tileStringPoint, ++i) {
        tile.sides[Bottom].backwardString[i] = *tileStringPoint;
    }
    setItemcharVector(strings, &nullChar);
    if (!constructIntEdgeFromCharEdge(&tile.sides[Bottom])) {
        fprintf(stderr, "failed to construct bottom edge of tile id %lu\n", tile.tileID);
        return false;
    }

    tileStringPoint = tileTopEnd - 1;
    tile.sides[Right].forwardString = addBlockcharVector(strings, rowLength * 2);
    if (tile.sides[Right].forwardString == NULL) {
        fprintf(stderr, "failed to add block for right strings on tile ID %lu\n", tile.tileID);
        return false;
    }
    tile.sides[Right].backwardString = tile.sides[Right].forwardString + rowLength;
    for (fu16 i = 0, j = rowLength - 2; i < rowLength - 1; ++i, --j, tileStringPoint += rowLength) {
        char tileChar = *tileStringPoint;
        tile.sides[Right].forwardString[i] = tileChar;
        tile.sides[Right].backwardString[j] = tileChar;
    }
    tile.sides[Right].forwardString[rowLength - 1] = '\0';
    setItemcharVector(strings, &nullChar);
    if (!constructIntEdgeFromCharEdge(&tile.sides[Right])) {
        fprintf(stderr, "failed to construct right edge of tile id %lu\n", tile.tileID);
        return false;
    }

    if (addItemTileVector(tiles, &tile) == NULL) {
        fprintf(stderr, "failed to add tile %s to tiles vector\n", tile.unrotatedTile);
        return false;
    }
    return true;
}

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
        #ifdef DEBUG
        printf("edging tile id %lu %s\n", tile -> tileID, tile -> unrotatedTile);
        #endif
        Edge* edge = &((*tile).sides[Top]);
        fu16 edgeIndex = 0;
        for (; edgeIndex < 4; ++edgeIndex, ++edge) {
            #ifdef DEBUG
            printf("%p edge is %ld bytes from the %p tile of size %lu\n", (void*) edge, ((isize) tile) - ((isize) edge), (void*) tile, sizeof(Tile));
            #endif
            fu16 edgeInteger = edge -> backward;
            #ifdef DEBUG
            printf("index %lu edge integer %lu\n", edgeIndex, edgeInteger);
            #endif
            for (fu8 loop = 0; loop < 2; ++loop, edgeInteger = edge -> forward) {
                #ifdef DEBUG
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
