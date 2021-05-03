
#include <stdio.h>

#ifdef DEBUG
#define __STDC_WANT_LIB_EXT1__ 1
#endif

#include <string.h>

#ifdef DEBUG
//undef __STDC_WANT_LIB_EXT1__
#include "shortIntNames.h"
#include "tileStructures.h"
#include "tileParsing.c"
#include "mapStitching.c"
#include "tileMaps.c"
#include "testing.h"
#include "tileStrings.c"
#include "buildTileStructures.c"

inline static fu8 min(fu8 a, usize b) {
    if (b < a) {
        return (fu8) b;
    }
    return a;
}

static bool runTests() {
    bool success = true;
    #define errored(message) {\
        fprintf(stderr, message "\n");\
        success = false;\
    }



    if (!testRowParsing()) {
        errored("failed to parse row from string");
    }
    if (!testEdgeParsing()) {
        errored("failed to parse Edge from string");
    }
    if (!testTileParsing()) {
        errored("failed to parse Tile from string");
    }
    if (!testTileCounting()) {
        errored("failed to count tiles in a tilesString");
    }
    if (!testTileFinding()) {
        errored("failed to make an index of tiles in a tilesString");
    }
    if (!testTileRotation()) {
        errored("failed to get rotated tile's edges");
    }

    if (!testDebugPrintAppending()) {
        errored("failed to append to debug print properly\n");
    }
    if (!testEdgeDebugPrinting()) {
        errored("failed to debug print edges properly\n");
    }
    if (!testTileDebugPrinting()) {
        errored("failed to debug print tiles properly\n");
    }
    if (!testEdgeReferenceDebugPrinting()) {
        errored("failed to debug print edge references properly\n");
    }
    if (!testVectorDebugPrinting()) {
        errored("failed to debug print vectors properly\n");
    }

    if (!testEdgeMapBuilding()) {
        errored("failed to build edge map");
    }
    if (!testTileSelfLookup()) {
        errored("failed to lookup tile by its own edge");
    }
    if (!testTileMatchLookup()) {
        errored("failed to lookup matching tile by edge");
    }
    #undef errored

    return success;
}

static void printBinaryInteger(fu16 integer) {
    usize const size = (sizeof(fu16) * 8) - (usize const) __builtin_clzl(integer) + 1;
    printf("string size %lu\n", size);
    char string[size];
    fu16 i = 0;
    while (integer != 0) {
        string[i] = '0' + ((char)(integer & 1));
        ++i;
        integer >>= 1;
    }
    if (i == 0) {
        string[0] = '0';
        ++i;
    }
    printf("ending i %lu\n", i);
    string[i] = '\0';
    printf("%s", string);
}

static bool testRowParsing() {
    char const rowString[] = "#.###...#.";
    fu16 row = parseRowFromRowString(rowString);
    fu16 answer =           0x2e2;
    if (row != answer) {
        printf("%lu row, %lu answer\n", row, answer);
        printBinaryInteger(row);
        printf("\nanswer:\n");
        printBinaryInteger(answer);
        return false;
    }
    return true;
}
static bool testEdgeParsing() {
    struct MutEdge parsed = {
        .forwardString = (char*) "#..#.##..#",
        .backwardString = (char*) "#..##.#..#",
        .forward = 0x259,
        .backward = 0x269
    };
    fu16 forwards = parsed.forward;
    fu16 backwards = parsed.backward;
    return constructIntEdgeFromCharEdge(&parsed)
        && parsed.forward == forwards
        && parsed.backward == backwards;
}

static char const tileString[] =
    // Test tile generated from the thue-morse sequence
    "Tile 1024:\n"
    "#..#.##..#\n"
    "#.#..#.##.\n"
    "#..##..#.#\n"
    "#..##.#..#\n"
    "#..#.##.#.\n"
    ".#.##..##.\n"
    "#..#.##.#.\n"
    ".##..#.##.\n"
    "#..#.##..#\n"
    "#.#..##..#";

static Tile correctTile = {
    .tileID = 1024,
    .unrotatedTile = (char*)(tileString + 11),
    .sides = {
        [Top] = { // same as edge parsing test edge
            .forwardString = "#..#.##..#",
            .backwardString = "#..##.#..#",
            .forward = 0x259,
            .backward = 0x269
        },
        [Right] = {
            .forwardString = "#.##....##",
            .backwardString = "##....##.#",
            .forward = 0x2c3,
            .backward = 0x30d
        },
        [Bottom] = {
            .forwardString = "#.#..##..#",
            .backwardString = "#..##..#.#",
            .forward = 0x299,
            .backward = 0x265
        },
        [Left] = {
            .forwardString = "#####.#.##",
            .backwardString = "##.#.#####",
            .forward = 0x3eb,
            .backward = 0x35f
        }
    }
};

static bool testTileParsing() {
    TileVector tileVector;
    if (!construct2TileVector(&tileVector, 1)) {
        fprintf(stderr, "failed to construct tile vector in testing tile parsing\n");
        return false;
    }
    charVector strings; // 4 edges, each with 2 rows of 0 terminated 10 char strings
    if (!construct2charVector(&strings, 0x100)) {// 4 * 2 * 11)) {
        fprintf(stderr, "failed to construct char vector in testing tile parsing\n");
        destructTileVector(&tileVector);
        return false;
    }

    bool success = constructTileFromTileString(tileString, &tileVector, &strings);
    if (success) {
        Tile* parsedTile = tileVector.contents;
        if ((parsedTile -> tileID) != correctTile.tileID) {
            fprintf(stderr, "incorrect tileID: %ld but should be %ld\n", (parsedTile -> tileID), correctTile.tileID);
            success = false;
        }
        if (strncmp((parsedTile -> unrotatedTile), correctTile.unrotatedTile, 110) != 0) {
            fprintf(stderr, "incorrect unrotatedTile: was \"%s\" but should be \"%s\"\n", (parsedTile -> unrotatedTile), correctTile.unrotatedTile);
            success = false;
        }
        for (fu16 i = 0; i < 4; ++i) {
            Edge const* correctEdge = &(correctTile.sides[i]);
            Edge const* parsedEdge = &((parsedTile -> sides)[i]);
            printf("%p %p", (void*) correctEdge, (void*) parsedEdge);
            printf("%p %p", (void*) correctEdge -> forwardString, (void*) parsedEdge -> forwardString);

            if (strncmp((parsedEdge -> forwardString), correctEdge -> forwardString, 11) != 0) {
                fprintf(stderr, "incorrect forward string, side %s [%lu], was \"%s\" but should be \"%s\"\n", SIDE_STRING[i], i, parsedEdge -> forwardString, correctEdge -> forwardString);
                success = false;
            } else {
                // if the forward string is wrong the integer will be too
                if ((parsedEdge -> forward) != (correctEdge -> forward)) {
                    fprintf(stderr, "incorrect forward edge integer, side %s [%lu], was %lu but should be %lu\n", SIDE_STRING[i], i, parsedEdge -> forward, correctEdge -> forward);
                    success = false;
                }
            }
            if (strncmp((parsedEdge -> backwardString), correctEdge -> backwardString, 11) != 0) {
                fprintf(stderr, "incorrect backward string, side %s [%lu], was \"%s\" but should be \"%s\"\n", SIDE_STRING[i], i, parsedEdge -> backwardString, correctEdge -> backwardString);
                success = false;
            } else {
                // if the forward string is wrong the integer will be too
                if ((parsedEdge -> backward) != (correctEdge -> backward)) {
                    fprintf(stderr, "incorrect backward edge integer, side %s [%lu]\n, was %lu but should be %lu\n", SIDE_STRING[i], i, parsedEdge -> backward, correctEdge -> backward);
                    success = false;
                }
            }
        }
        if (!success) {
            fprintf(stderr, "example tile:\n%s\nparsed tile:\n%s\n", tileString, parsedTile -> unrotatedTile);
        }
    } else {
        fprintf(stderr, "reported as failed to construct tile from tile string while testing tile parsing\n");
        // success = false; // it's already false
    }

    destructcharVector(&strings);
    destructTileVector(&tileVector);
    return success;
}

static bool testTileCounting(){
    return countTiles(exampleTilesString) == 9;
}
static bool testTileFinding(){
    #ifdef __STDC_LIB_EXT1__
    #pragma message "Found bounds checked libc functions!"
    usize exampleLen = strnlen_s(exampleTilesString, 1100);
    #else
    usize exampleLen = strlen(exampleTilesString);
    #endif // pypy3 tells me it's 1097 chars
    if (exampleLen == 1100) {
        fprintf(stderr, "failed to find the length of the example tiles string\n");
        return false;
    }
    char* tileStringCopy = strndup(exampleTilesString, exampleLen);
    char* tileStringIndexes[9] = {NULL};
    findTiles(tileStringCopy, tileStringIndexes);
    free(tileStringCopy);
    // safe to deref indexes cause it's probably on the stack
    // also we only freed the backing array so just dont deref the pointers
    return tileStringIndexes[8] != NULL;
}

static bool testTileRotation() {
    // test getEdgeInteger and getEdgeString
    Edge const* const correctEdges[SIDE_COUNT][SIDE_COUNT] = {
        [Top] = { // Top up rotation
            [Top] = &correctTile.sides[Top],
            [Right] = &correctTile.sides[Right],
            [Bottom] = &correctTile.sides[Bottom],
            [Left] = &correctTile.sides[Left]
        }, [Right] = { // Right up rotation
            [Top] = &correctTile.sides[Right],
            [Right] = &correctTile.sides[Bottom],
            [Bottom] = &correctTile.sides[Left],
            [Left] = &correctTile.sides[Top]
        }, [Bottom] = { // Bottom up rotation
            [Top] = &correctTile.sides[Bottom],
            [Right] = &correctTile.sides[Left],
            [Bottom] = &correctTile.sides[Top],
            [Left] = &correctTile.sides[Right]
        }, [Left] = { // Left up rotation
            [Top] = &correctTile.sides[Left],
            [Right] = &correctTile.sides[Top],
            [Bottom] = &correctTile.sides[Right],
            [Left] = &correctTile.sides[Bottom]
        }
    };

    bool allCorrect = true;
    Placement placement = {
        .backwards = false,
        .side = Top,
        .tile = &correctTile
    };
    while (true) {
        while (true) {
            Side compareSide = Top;
            while (true){
                fu16 placedEdgeInteger = *getEdgeInteger(&placement, compareSide);
                fu16 correctEdgeInteger = *(&(correctEdges[placement.side][compareSide] -> forward) + placement.backwards);
                if (placedEdgeInteger != correctEdgeInteger) {
                    fprintf(stderr, "test tile placed facing %s's %s %s side integer %lu "
                        "didn't match the expected value of %lu\n",
                        SIDE_STRING[placement.side], SIDE_STRING[compareSide],
                        FORBACKWARD_STRINGS[placement.backwards],
                        placedEdgeInteger,
                        correctEdgeInteger);
                    allCorrect = false;
                }

                char const* const placedEdgeString = getEdgeString(&placement, compareSide);
                char const* const correctEdgeString = *(&(correctEdges[placement.side][compareSide] -> forwardString) + placement.backwards);
                fu8 ROW_LENGTH = 11;
                if (placedEdgeString != correctEdgeString && strncmp(placedEdgeString, correctEdgeString, ROW_LENGTH) != 0) {
                    fprintf(stderr, "test tile placed facing %s's %s %s side string %s "
                        "didn't match the expected value of %s\n",
                        SIDE_STRING[placement.side], SIDE_STRING[compareSide],
                        FORBACKWARD_STRINGS[placement.backwards],
                        placedEdgeString,
                        correctEdgeString);
                    allCorrect = false;
                }

                if (compareSide < Left) {
                    ++compareSide;
                } else {
                    break;
                }
            }

            if (!placement.backwards) {
                placement.backwards = true;
            } else {
                break;
            }
        }
        if (placement.side < Left) {
            ++placement.side;
        } else {
            break;
        }
    }
    return allCorrect;
}

static bool testDebugPrintAppending(){
    bool success = true;
    charVector printSpace = {
        .size = 100,
        .contentCount = 0,
        .contents = (char[128]) {0}
    };

    //printf("entering char appending test loop\n");
    for (fu8 i = 1; i < 128; ++i) {
        appendChar(&printSpace, (char) i);
        appendChar(&printSpace, '\0');

        if (printSpace.contents[0] != i || printSpace.contents[1] != 0 || printSpace.contentCount != 2) {
            fprintf(stderr, "failed to append characters properly\n");
            success = false;
            break;
        }
        printSpace.contentCount = 0;
    }

    //printf("testing appending 13 Xs\n");
    appendChars(&printSpace, 13, 'X');
    appendChar(&printSpace, '\0');
    if (printSpace.contentCount == 14) {
        for (fu8 i = 0; i < 13; ++i) {
            if (printSpace.contents[i] != 'X') {
                fprintf(stderr, "mismatched character in repetition appending debug print test\n");
                success = false;
                break;
            }
        }
        if (printSpace.contents[13] != '\0') {
            fprintf(stderr, "missing null byte after repetition appending debug print test\n");
            success = false;
        }
    } else {
        fprintf(stderr, "wrong character count after repetition appending debug print test\n");
        success = false;
    }
    printSpace.contentCount = 0;

    if (!appendDouble(&printSpace, 55.55)) {
        fprintf(stderr, "failed to append double 55.55 to debug print space char vector\n");
        success = false;
    } else {
        appendChar(&printSpace, '\0');
        if (printSpace.contentCount != 6 || strncmp(printSpace.contents, "55.55", 6) != 0) {
            fprintf(stderr, "debug appended 55.55 wrong: got (char[%zu]) \"%s\" but was supposed to be (char[6]) \"55.55\"\n", printSpace.contentCount, printSpace.contents);
            success = false;
        }
    }
    printSpace.contentCount = 0;

    appendHexSizeT(&printSpace, 0x124810204080);
    appendChar(&printSpace, '\0');
    if (printSpace.contentCount != 15 || strncmp(printSpace.contents, "0x124810204080", 15) != 0) {
        fprintf(stderr, "debug appended 0x124810204080 wrong: was %zu long but should be 15: \"%s\"\n", printSpace.contentCount, printSpace.contents);
        success = false;
    }
    printSpace.contentCount = 0;
                                  //12345678901234567890123
    char const lieTestString[23] = "Testing lie appending!";
    appendLie(&printSpace, 4, lieTestString);
    appendChar(&printSpace, '\0');
    if (printSpace.contentCount != 23) {
        fprintf(stderr, "failed to append lie, mismatching character count: was %zu but should be 23\n", printSpace.contentCount);
        success = false;
    } else if(strncmp(printSpace.contents, lieTestString, 23) != 0) {
        fprintf(stderr, "failed to append lie, mismatching strings: got \"%s\" but should have been \"%s\"", printSpace.contents, lieTestString);
        success = false;
    }
    printSpace.contentCount = 0;

    //appendLine(&printSpace, )

    return success;
}
static bool testEdgeDebugPrinting(){
    return false;
}
static bool testTileDebugPrinting(){
    return false;
}
static bool testEdgeReferenceDebugPrinting(){
    return false;
}
static bool testVectorDebugPrinting(){
    return false;
}

/*
 * Found using pypy3:
 * >>>> def spread_bits(num):
 * ....     twoPower = 4
 * ....     num ^= num >> (64 - twoPower)
 * ....     num = ((11400714819323198485 * num) & 0xffff_ffff_ffff_ffff) >> (64 - twoPower)
 * ....     if num == 0:
 * ....         num += 1
 * ....     return num
 * ....
 * >>>> [spread_bits(n) for n in [0x259, 0x269, 0x2c3, 0x30d, 0x299, 0x265, 0x3eb, 0x35f]]
 * [7, 5, 15, 10, 15, 13, 14, 5]
 */
#define EMPTY_BUCKET {.hash = 0, .key = 0, .value = {.size = 0, .contentCount = 0, .contents = NULL}}
static fu16ToEdgeReferenceVectorOpenHashMap correctEdgeMap = {
    .loadFactor = 0.5,
    .size = 2 * 8, //nextHigherPowerOfTwo(8 / 0.5),
    .minimumSize = 2 * 8,
    .shrinkFillLevel = 0,
    .growFillLevel = 8,
    .contentCount = 8,
    .bucketMask = 15,
    .sizeTwoPower = 4,
    .contents = (fu16ToEdgeReferenceVectorHashBucket*) (fu16ToEdgeReferenceVectorHashBucket[16]) {
        [0] = {
            .hash = 15,
            .key = 0x299,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    [0] = {
                        .backwards = false,
                        .side = Bottom,
                        .tile = &correctTile
                    }
                }
            }
        },
        [1] = EMPTY_BUCKET,
        [2] = EMPTY_BUCKET,
        [3] = EMPTY_BUCKET,
        [4] = EMPTY_BUCKET,
        [5] = {
            .hash = 5,
            .key = 0x269,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    [0] = {
                        .backwards = true,
                        .side = Top,
                        .tile = &correctTile
                    }
                }
            }
        },
        [6] = {
            .hash = 5,
            .key = 0x35f,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    [0] = {
                        .backwards = true,
                        .side = Left,
                        .tile = &correctTile
                    }
                }
            }
        },
        [7] = {
            .hash = 7,
            .key = 0x259,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    [0] = {
                        .backwards = false,
                        .side = Top,
                        .tile = &correctTile
                    }
                }
            }
        },
        [8] = EMPTY_BUCKET,
        [9] = EMPTY_BUCKET,
        [10] = {
            .hash = 10,
            .key = 0x30d,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    [0] = {
                        .backwards = true,
                        .side = Right,
                        .tile = &correctTile
                    }
                }
            }
        },
        [11] = EMPTY_BUCKET,
        [12] = EMPTY_BUCKET,
        [13] = {
            .hash = 13,
            .key = 0x265,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    [0] = {
                        .backwards = true,
                        .side = Bottom,
                        .tile = &correctTile
                    }
                }
            }
        },
        [14] = {
            .hash = 14,
            .key = 0x3eb,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    // 0x3eb
                    [0] = {
                        .backwards = false,
                        .side = Left,
                        .tile = &correctTile
                    }
                }
            }
        },
        [15] = {
            .hash = 15,
            .key = 0x299,
            .value = {
                .size = 1,
                .contentCount = 1,
                .contents = (EdgeReference*) (EdgeReference[1]) {
                    [0] = {
                        .backwards = false,
                        .side = Bottom,
                        .tile = &correctTile
                    }
                }
            }
        }
    }
};
#undef EMPTY_BUCKET

static bool testEdgeMapBuilding() {
    TileVector justCorrectTile = {
        .size = 1,
        .contentCount = 1,
        .contents = &correctTile
    }; // make a vector that refers to just the correctTile

    fu16ToEdgeReferenceVectorOpenHashMap edges;
    construct2fu16ToEdgeReferenceVectorOpenHashMap(&edges, 8);

    bool success;
    success = buildEdgeMap(&justCorrectTile, &edges);
    if (!success) {
        fprintf(stderr, "failed to build edge map\n");
        destructfu16ToEdgeReferenceVectorOpenHashMap(&edges);
        // don't destruct the statically allocated correctTile by destructing the justCorrectTile vector
        return success;
    }

    // compare edge maps
    double LOAD_FACTOR_PRECISION = 0.001;
    if (edges.loadFactor >= correctEdgeMap.loadFactor + LOAD_FACTOR_PRECISION || edges.loadFactor <= correctEdgeMap.loadFactor - LOAD_FACTOR_PRECISION) {
        fprintf(stderr, "mismatched load factor %lf, expected load factor %lf\n", edges.loadFactor, correctEdgeMap.loadFactor);
        success = false;
    }

    if (edges.growFillLevel != correctEdgeMap.growFillLevel) {
        fprintf(stderr, "mismatched grow fill level %lu, expected grow fill level %lu\n", edges.growFillLevel, correctEdgeMap.growFillLevel);
        success = false;
    }

    if (edges.shrinkFillLevel != correctEdgeMap.shrinkFillLevel) {
        fprintf(stderr, "mismatched shrink fill level %lu, expected shrink fill level %lu\n", edges.shrinkFillLevel, correctEdgeMap.shrinkFillLevel);
        success = false;
    }

    if (edges.contentCount != correctEdgeMap.contentCount) {
        fprintf(stderr, "mismatched item count %lu, expected item count %lu\n", edges.contentCount, correctEdgeMap.contentCount);
        success = false;
    }

    if (edges.contentCount != correctEdgeMap.contentCount) {
        fprintf(stderr, "mismatched content count %lu, expected content count %lu\n", edges.contentCount, correctEdgeMap.contentCount);
        success = false;
    }

    if (edges.bucketMask != correctEdgeMap.bucketMask) {
        fprintf(stderr, "mismatched bucket mask %lu, expected bucket mask %lu\n", edges.bucketMask, correctEdgeMap.bucketMask);
        success = false;
    }

    if (edges.minimumSize != correctEdgeMap.minimumSize) {
        fprintf(stderr, "mismatched minimum size %lu, expected minimum size %lu\n", edges.minimumSize, correctEdgeMap.minimumSize);
        success = false;
    }

    if (edges.sizeTwoPower != correctEdgeMap.sizeTwoPower) {
        fprintf(stderr, "mismatched minimum size %hhu, expected minimum size %hhu\n", edges.sizeTwoPower, correctEdgeMap.sizeTwoPower);
        success = false;
    }

    unsigned char bucketLimit;
    if (edges.size != correctEdgeMap.size) {
        fprintf(stderr, "mismatched map size %lu, expected map size %lu\n", edges.size, correctEdgeMap.size);
        bucketLimit = min(16, edges.size);
    } else {
        bucketLimit = 16;
    }

    if (edges.contents == NULL || correctEdgeMap.contents == NULL) {
        fprintf(stderr, "null contents of an edge map\n");
        destructfu16ToEdgeReferenceVectorOpenHashMap(&edges);
        return false;
    }

    if (edges.contents != correctEdgeMap.contents) {
        fu16ToEdgeReferenceVectorHashBucket* correctMapBucket = (correctEdgeMap.contents);
        fu16ToEdgeReferenceVectorHashBucket* edgeMapBucket = (edges.contents);

        for (unsigned char i = 0; i < bucketLimit; ++i) {
            if (correctMapBucket -> hash != edgeMapBucket -> hash) {
                fprintf(stderr, "mismatched hashes at index %hhu in edge maps: found %lu, expected %lu\n", i, edgeMapBucket -> hash, correctMapBucket -> hash);
                success = false;
                if (edgeMapBucket -> hash == SIZE_MAX) {
                    fprintf(stderr, "edge map bucket hash was 0; skipping these buckets\n");
                    ++correctMapBucket;
                    ++edgeMapBucket;
                    continue;
                }
            }

            if (correctMapBucket -> key != edgeMapBucket -> key) {
                fprintf(stderr, "mismatched keys at index %hhu in edge maps: found %lu, expected %lu\n", i, edgeMapBucket -> key, correctMapBucket -> key);
                success = false;
            }

            if (&(correctMapBucket -> value) != &(edgeMapBucket -> value)) {
                // check the vector
                EdgeReferenceVector* correctMapVector = &(correctMapBucket -> value);
                EdgeReferenceVector* edgeMapVector = &(edgeMapBucket -> value);
                if (correctMapVector -> size != edgeMapVector -> size) {
                    fprintf(stderr, "mismatched vector size in bucket %hhu in edge maps: found %lu, expected %lu\n", i, edgeMapVector -> size, correctMapVector -> size);
                    success = false;
                }

                unsigned char vectorLimit;
                if (correctMapVector -> contentCount != edgeMapVector -> contentCount) {
                    fprintf(stderr, "mismatched vector fill size in bucket %hhu in edge maps: found %lu items, expected %lu instead\n", i, edgeMapVector -> contentCount, correctMapVector -> contentCount);
                    success = false;
                    vectorLimit = min(16, edgeMapVector -> contentCount);
                } else {
                    vectorLimit = 16;
                }

                if (correctMapVector -> contents == NULL || edgeMapVector -> contents == NULL) {
                    fprintf(stderr, "found a null edge map vector contents, skipping\n");
                    ++correctMapBucket;
                    ++edgeMapBucket;
                    continue;
                }
                if (correctMapVector -> contents != edgeMapVector -> contents) {
                    // inspect the contents of the vectors

                    EdgeReference* correctMVContents = correctMapVector -> contents;
                    EdgeReference* edgeMVContents = edgeMapVector -> contents;

                    for (unsigned char j = 0; j < vectorLimit; ++j) {
                        if (correctMVContents -> backwards != edgeMVContents -> backwards) {
                            fprintf(stderr, "mismatched reversal of vector element %hhu in bucket %hhu in edge maps: backwards was %s, but was supposed to be %s\n", j, i, FORBACKWARD_STRINGS[edgeMVContents -> backwards], FORBACKWARD_STRINGS[correctMVContents -> backwards]);
                            success = false;
                        }

                        if (correctMVContents -> side != edgeMVContents -> side) {
                            fprintf(stderr, "mismatched side of vector element %hhu in bucket %hhu in edge maps: got %s but expected %s\n", j, i, SIDE_STRING[edgeMVContents -> side], SIDE_STRING[correctMVContents -> side]);
                            success = false;
                        }

                        if (correctMVContents -> tile != edgeMVContents -> tile) {
                            fprintf(stderr, "mismatched tile pointers of vector element %hhu in bucket %hhu in edge maps: expected correctTile at %p but got %p\n", j, i, (void*) &(correctMVContents -> tile), (void*) (edgeMVContents -> tile));
                            success = false;
                        }

                        ++correctMVContents;
                        ++edgeMVContents;
                    }
                }
            }
            ++correctMapBucket;
            ++edgeMapBucket;
        }
    }

    if (!success) {
        charVector debugMap;
        if (!construct2charVector(&debugMap, 1024)) {
            fprintf(stderr, "failed to allocate 1024 size charVector for debug map printing\n");
            destructfu16ToEdgeReferenceVectorOpenHashMap(&edges);
            return false;
        }
        printDebugfu16ToEdgeReferenceVectorOpenHashMap(&debugMap, 0, &edges);
        for (usize i = 0; i < debugMap.contentCount; ++i) {
            if (debugMap.contents[i] == '\0') {
                fprintf(stderr, "found null byte in debug map dump at index %zu\n", i);
                debugMap.contents[i] = 'X';
            }
        }
        appendChar(&debugMap, '\0');
        printf("printing debug map of size %zu\n", debugMap.contentCount);
        printf("fu16ToEdgeReferenceVectorOpenHashMap correctMap = ");
        puts(debugMap.contents);
        destructcharVector(&debugMap);
    }
    destructfu16ToEdgeReferenceVectorOpenHashMap(&edges);
    // don't destruct the statically allocated correctTile by destructing the justCorrectTile vector
    return success;
}

static bool testTileSelfLookup() {
    return false;
}
static bool testTileMatchLookup() {
    return false;
}
#endif
