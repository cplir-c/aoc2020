
#include <stdio.h>

#include "shortIntNames.h"
#include "tileMaps.c"
#include "tileParsing.h"

#ifndef __TILE_PARSING_C
#define __TILE_PARSING_C 1

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

static char const* parseTileHeader(Tile* tile, char const* tileStringPoint) {
    char const* tileString = tileStringPoint;
    if (tileStringPoint[0] != 'T' || tileStringPoint[1] != 'i'
        || tileStringPoint[2] != 'l' || tileStringPoint[3] != 'e' || tileStringPoint[4] != ' ') {
        fprintf(stderr, "failed to parse \"Tile\" of tile %s\n", tileString);
        return NULL;
    }
    tileStringPoint += 5; // skip "Tile "

    // parse tileID
    tile -> tileID = strtoul(
        (char* restrict) tileStringPoint,
        (char** restrict) &tileStringPoint, 10
    );

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

static fu16 parseRowFromRowString(char const* rowString) {
    char const* pointer = rowString;
    char pointed = '\0';
    if (pointer == NULL) {
        fprintf(stderr, "failed to parse row from row string\n");
        return UINT_FAST16_MAX;
    }
    pointed = *pointer;
    fu16 row = 0;
    for (; pointed != '\0'; ++pointer, pointed = *pointer) {
        row <<= 1;
        row |= (pointed == '#')? 1: 0;
    }
    return row;
}

static bool constructTileFromTileString(char const* tileStringPoint, TileVector* tiles, charVector* strings) {
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
    tileStringPoint += rowLength - 2;
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

#endif
