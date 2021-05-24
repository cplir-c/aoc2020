
#include <stdio.h>

#ifdef DEBUG
#define __STDC_WANT_LIB_EXT1__ 1
#endif

#include <string.h>
#include <inttypes.h>
#include <stdbool.h>

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

static _Thread_local _Bool staticsInitialized = false;
static _Thread_local char const tileString[121] =
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
static _Thread_local Tile correctTile = {
    .tileID = 1024,
    .unrotatedTile = NULL,
    .sides = {
        [Top] = { /* same as edge parsing test edge*/
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
static _Thread_local Edge edgeParsingTestEdge;
#define EMPTY_BUCKET { .hash = SIZE_MAX, .key = UINT_FAST16_MAX, .value = { .size = 0, .contentCount = 0, .contents = NULL } }
static _Thread_local fu16ToEdgeReferenceVectorOpenHashMap correctEdgeMap = (fu16ToEdgeReferenceVectorOpenHashMap) {
    .loadFactor = 0.5,
    .growFillLevel = 16,
    .shrinkFillLevel = 0,
    .contentCount = 8,
    .size = 32,
    .bucketMask = 0x1f,
    .minimumSize = 32,
    .sizeTwoPower = 6,
    .contents = (fu16ToEdgeReferenceVectorHashBucket*) (fu16ToEdgeReferenceVectorHashBucket[32]) {
        [0] = EMPTY_BUCKET,
        [1] = EMPTY_BUCKET,
        [2] = EMPTY_BUCKET,
        [3] = EMPTY_BUCKET,
        [4] = EMPTY_BUCKET,
        [5] = EMPTY_BUCKET,
        [6] = EMPTY_BUCKET,
        [7] = EMPTY_BUCKET,
        [8] = EMPTY_BUCKET,
        [9] = EMPTY_BUCKET,
        [10] = EMPTY_BUCKET,
        [11] = {
            .hash = 781,
            .key = 781,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 1, // Right
                        .tile = NULL
                    }
                }
            },
        },
        [12] = EMPTY_BUCKET,
        [13] = EMPTY_BUCKET,
        [14] = EMPTY_BUCKET,
        [15] = EMPTY_BUCKET,
        [16] = EMPTY_BUCKET,
        [17] = EMPTY_BUCKET,
        [18] = EMPTY_BUCKET,
        [19] = EMPTY_BUCKET,
        [20] = {
            .hash = 617,
            .key = 617,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 0, // Top
                        .tile = NULL
                    }
                }
            },
        },
        [21] = EMPTY_BUCKET,
        [22] = {
            .hash = 613,
            .key = 613,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 2, // Bottom
                        .tile = NULL
                    }
                }
            },
        },
        [23] = {
            .hash = 863,
            .key = 863,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 3, // Left
                        .tile = NULL
                    }
                }
            },
        },
        [24] = {
            .hash = 1003,
            .key = 1003,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 3, // Left
                        .tile = NULL
                    }
                }
            },
        },
        [25] = EMPTY_BUCKET,
        [26] = EMPTY_BUCKET,
        [27] = EMPTY_BUCKET,
        [28] = {
            .hash = 601,
            .key = 601,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 0, // Top
                        .tile = NULL
                    }
                }
            },
        },
        [29] = {
            .hash = 707,
            .key = 707,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 1, // Right
                        .tile = NULL
                    }
                }
            },
        },
        [30] = EMPTY_BUCKET,
        [31] = {
            .hash = 665,
            .key = 665,
            .value = {
                .size = 4,
                .contentCount = 1,
                .contents = (EdgeReference[1]) {
                    [0] = {
                        .backwards = false, // forward
                        .side = 2, // Bottom
                        .tile = NULL
                    }
                }
            },
        },
    }
};
#undef EMPTY_BUCKET

static fu8 min(fu8 a, usize b) {
    if (b < a) {
        return (fu8) b;
    }
    return a;
}

static bool runTests() {
    if (!staticsInitialized) {
        initializedEdgeParsingTestEdge();
        initializeCorrectTile();
        initializeCorrectEdgeMap();
        staticsInitialized = true;
    }
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
    fu16 answer = 0x2e2;
    if (row != answer) {
        printf("%lu row, %lu answer\n", row, answer);
        printBinaryInteger(row);
        printf("\nanswer:\n");
        printBinaryInteger(answer);
        return false;
    }
    return true;
}
static void initializedEdgeParsingTestEdge() {
    edgeParsingTestEdge = correctTile.sides[Top];
}
static bool testEdgeParsing() {
    Edge parsed = edgeParsingTestEdge;
    return constructIntEdgeFromCharEdge(&parsed)
        && parsed.forward == edgeParsingTestEdge.forward
        && parsed.backward == edgeParsingTestEdge.backward;
}

static void initializeCorrectTile() {
    correctTile.unrotatedTile = (char*)(tileString + 11);
}

static bool testTileParsing() {
    TileVector tileVector;
    if (!construct2TileVector(&tileVector, 1)) {
        fprintf(stderr, "failed to construct tile vector in testing tile parsing\n");
        return false;
    }
    charVector strings; // 4 edges, each with 2 rows of 0 terminated 10 char strings
    if (!construct2charVector(&strings, 4 * 2 * 11 + 1)) {// 4 * 2 * 11)) {
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
            //printf("%p %p", (void*) correctEdge, (void*) parsedEdge);
            //printf("%p %p", (void*) correctEdge -> forwardString, (void*) parsedEdge -> forwardString);

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
                if (placement.tile != NULL) {
                    {
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
                    } {
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
                    }
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

    {
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
    } {
        //printf("testing appending 13 Xs\n");
        fu8 const CHAR_COUNT = 13;
        appendChars(&printSpace, CHAR_COUNT, 'X');
        appendChar(&printSpace, '\0');
        if (printSpace.contentCount == CHAR_COUNT + 1) {
            for (fu8 i = 0; i < CHAR_COUNT; ++i) {
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
    } {
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
    }
    {
        appendHexSizeT(&printSpace, 0x124810204080);
        appendChar(&printSpace, '\0');
        if (printSpace.contentCount != 15 || strncmp(printSpace.contents, "0x124810204080", 15) != 0) {
            fprintf(stderr, "debug appended 0x124810204080 wrong: was %zu long but should be 15: \"%s\"\n", printSpace.contentCount, printSpace.contents);
            success = false;
        }
        printSpace.contentCount = 0;
    } {
        #define LIE_TEST_STRLEN 23                 //12345678901234567890123
        char const lieTestString[LIE_TEST_STRLEN] = "Testing lie appending!";
        appendLie(&printSpace, 4, lieTestString);
        appendChar(&printSpace, '\0');
        char const lieCorrectString[LIE_TEST_STRLEN + 4] = "    Testing lie appending!";
        if (printSpace.contentCount != LIE_TEST_STRLEN + 4) {
            fprintf(stderr, "failed to append lie, mismatching character count: was %zu but should be %hhu\n", printSpace.contentCount, LIE_TEST_STRLEN + 4);
            success = false;
        } else if(strncmp(printSpace.contents, lieCorrectString, LIE_TEST_STRLEN + 4) != 0) {
            fprintf(stderr, "failed to append lie, mismatching strings: got \"%s\" but should have been \"%s\"", printSpace.contents, lieTestString);
            success = false;
        }
        printSpace.contentCount = 0;
        #undef LIE_TEST_STRLEN
    } {
        #define LINE_TEST_STRLEN 24                 // 123456789012345678901234
        char const lineTestString[LINE_TEST_STRLEN] = "Testing line appending!";
        appendLine(&printSpace, 3, lineTestString);
        appendChar(&printSpace, '\0');
        #define LINE_ANSWER_STRLEN (LINE_TEST_STRLEN + 4)
        char const lineCorrectString[LINE_ANSWER_STRLEN] = "   Testing line appending!\n";
        if (printSpace.contentCount != LINE_ANSWER_STRLEN) {
            fprintf(stderr, "failed to append line, mismatching character count: was %zu but should be %hhu\n", printSpace.contentCount, LINE_ANSWER_STRLEN);
            success = false;
        } else if(strncmp(printSpace.contents, lineCorrectString, LINE_ANSWER_STRLEN) != 0) {
            fprintf(stderr, "failed to append line, mismatching strings: got \"%s\" but should have been \"%s\"", printSpace.contents, lineTestString);
            success = false;
        }
        printSpace.contentCount = 0;
        #undef LINE_ANSWER_STRLEN
        #undef LINE_TEST_STRLEN
    } {
        #define NULL_TEST_STRLEN 24                 // 123456789012345678901234
        char const nullTestString[NULL_TEST_STRLEN] = "Testing nULL apPENDing!";
        appendNullString(&printSpace, nullTestString);
        appendChar(&printSpace, '\0');
        if (printSpace.contentCount != NULL_TEST_STRLEN) {
            fprintf(stderr, "failed to append null string, mismatching character count: was %zu but should be %hhu\n", printSpace.contentCount, NULL_TEST_STRLEN);
            success = false;
        } else if(strncmp(printSpace.contents, nullTestString, NULL_TEST_STRLEN) != 0) {
            fprintf(stderr, "failed to append null string, mismatching strings: got \"%s\" but should have been \"%s\"", printSpace.contents, nullTestString);
            success = false;
        }
        printSpace.contentCount = 0;
        #undef NULL_TEST_STRLEN
    } {
        char pointerTestString[64] = {0};
        sprintf(pointerTestString, "%p", printSpace.contents);
        fu8 const POINTER_STRLEN = strlen(pointerTestString);
        appendPointer(&printSpace, printSpace.contents);
        appendChar(&printSpace, '\0');
        if (printSpace.contentCount != POINTER_STRLEN + 1) {
            fprintf(stderr, "failed to append pointer, mismatching character count: was %zu but should be %hhu\n", printSpace.contentCount, POINTER_STRLEN);
            success = false;
        } else if(strncmp(printSpace.contents, pointerTestString, POINTER_STRLEN + 1) != 0) {
            fprintf(stderr, "failed to append pointer, mismatching strings: got \"%s\" but should have been \"%s\"", printSpace.contents, pointerTestString);
            success = false;
        }
        printSpace.contentCount = 0;
    } {                       // 1234567890123
        appendSizeT(&printSpace, 792147035435);
        appendChar(&printSpace, '\0');
        if (printSpace.contentCount != 13 || strncmp(printSpace.contents, "792147035435", 13) != 0) {
            fprintf(stderr, "debug appended 792147035435 wrong: was %zu long but should be 13: \"%s\"\n", printSpace.contentCount, printSpace.contents);
            success = false;
        }
    }
    return success;
}
static bool testEdgeDebugPrinting(){
    charVector printSpace = { .size = 0, .contentCount = 0, .contents = NULL };
    if (!construct2charVector(&printSpace, 128)) {
        fprintf(stderr, "failed to allocate print space for testing edge debug printing\n");
        return false;
    }
    char const edgePrintingString[] = "(Edge) {\n"
    "    .forwardString = (char*) \"#..#.##..#\",\n"
    "    .backwardString = (char*) \"#..##.#..#\",\n"
    "    .forward = (fu16) 0x259,\n"
    "    .backward = (fu16) 0x269\n"
    "}";            // pypy3 says 155, 156 would be with the '\0'
    fu8 edgePrintingLength = 156;
    printDebugEdge(&printSpace, 0, &edgeParsingTestEdge);
    appendChar(&printSpace, '\0');
    if (printSpace.contentCount != edgePrintingLength || strncmp(printSpace.contents, edgePrintingString, edgePrintingLength) != 0) {
        fprintf(stderr, "failed to debug print edge properly, got (char[%zu])\"%s\", but should have been (char[%hhu])\"%s\".\n", printSpace.contentCount, printSpace.contents, edgePrintingLength, edgePrintingString);
        destructcharVector(&printSpace);
        return false;
    }
    destructcharVector(&printSpace);
    return true;
}
static bool testTileDebugPrinting(){
    char const correctPrintedTile[] =
        "(Tile) {\n"
        "    .tileID = (fu16) 1024,\n"
        "    .unrotatedTile = (char const*)\n"
        "        \"#..#.##..#\\n\"\n"
        "        \"#.#..#.##.\\n\"\n"
        "        \"#..##..#.#\\n\"\n"
        "        \"#..##.#..#\\n\"\n"
        "        \"#..#.##.#.\\n\"\n"
        "        \".#.##..##.\\n\"\n"
        "        \"#..#.##.#.\\n\"\n"
        "        \".##..#.##.\\n\"\n"
        "        \"#..#.##..#\\n\"\n"
        "        \"#.#..##..#\",\n"
        "    .sides = (Edge[4]) {\n"
        "        [Top] = (Edge) {\n"
        "            .forwardString = (char const*) \"#..#.##..#\",\n"
        "            .backwardString = (char const*) \"#..##.#..#\",\n"
        "            .forward = (fu16) 0x259,\n"
        "            .backward = (fu16) 0x269\n"
        "        },\n"
        "        [Right] = (Edge) {\n"
        "            .forwardString = (char const*) \"#.##....##\",\n"
        "            .backwardString = (char const*) \"##....##.#\",\n"
        "            .forward = (fu16) 0x2c3,\n"
        "            .backward = (fu16) 0x30d\n"
        "        },\n"
        "        [Bottom] = (Edge) {\n"
        "            .forwardString = (char const*) \"#.#..##..#\",\n"
        "            .backwardString = (char const*) \"#..##..#.#\",\n"
        "            .forward = (fu16) 0x299,\n"
        "            .backward = (fu16) 0x265\n"
        "        },\n"
        "        [Left] = (Edge) {\n"
        "            .forwardString = (char const*) \"#####.#.##\",\n"
        "            .backwardString = (char const*) \"##.#.#####\",\n"
        "            .forward = (fu16) 0x3eb,\n"
        "            .backward = (fu16) 0x35f\n"
        "        }\n"
        "    }\n"
        "}";
    charVector printSpace = {0};        // from python3, literal'd by: b = '"' + a.replace('"', r'\"').replace('\n', '\\n"\n"') + '"'
    if (!construct2charVector(&printSpace, 1378)) {
        fprintf(stderr, "failed to allocate print space for tile debug print testing\n");
        return false;
    }
    bool success = true;
    printDebugTile(&printSpace, 0, &correctTile);
    appendChar(&printSpace, '\0');
    if (strncmp(printSpace.contents, correctPrintedTile, 1378) != 0) {
        fprintf(stderr, "failed to correctly debug print tile: got \"%s\" but should have been \"%s\".", printSpace.contents, correctPrintedTile);
        success = false;
    }

    destructcharVector(&printSpace);
    return success;
}
static bool testEdgeReferenceDebugPrinting(){

    return false;
}
static bool testVectorDebugPrinting(){
    return false;
}

static void initializeCorrectEdgeMap() {
    usize i = correctEdgeMap.size - 1;
    if (correctEdgeMap.contents == NULL) {
        fprintf(stderr, "failed to initialize correct edge map");
        return;
    }
    do {
        fu16ToEdgeReferenceVectorHashBucket* bucket = &(correctEdgeMap.contents[i]);
        if (bucket -> hash != SIZE_MAX) {
            EdgeReferenceVector* vector = &(bucket -> value);
            for (usize j = 0; j < (vector -> contentCount); ++j){
                EdgeReference* ref = (vector -> contents) + j;
                ref -> tile = &correctTile;
            }
        }
    } while (i-- >= 1);

}
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
        bucketLimit = min(correctEdgeMap.size, edges.size);
    } else {
        bucketLimit = correctEdgeMap.size;
    }

    if (edges.contents == NULL || correctEdgeMap.contents == NULL) {
        fprintf(stderr, "null contents of an edge map\n");
        success = false;
    } else if (edges.contents != correctEdgeMap.contents) {
        fu16ToEdgeReferenceVectorHashBucket* correctMapBucket = (correctEdgeMap.contents);
        fu16ToEdgeReferenceVectorHashBucket* edgeMapBucket = (edges.contents);

        for (unsigned char i = 0; i < bucketLimit; ++i) {
            if (correctMapBucket -> hash != edgeMapBucket -> hash) {
                fprintf(stderr, "mismatched hashes at index %hhu in edge maps: found %lu, expected %lu\n", i, edgeMapBucket -> hash, correctMapBucket -> hash);
                success = false;
            }
            if (edgeMapBucket -> hash == SIZE_MAX) {
                fprintf(stderr, "edge map bucket hash was SIZE_MAX; skipping bucket %zu\n", i);
                ++correctMapBucket;
                ++edgeMapBucket;
                continue;
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
                    vectorLimit = min(correctEdgeMap.size, edgeMapVector -> size);
                } else {
                    vectorLimit = correctEdgeMap.size;
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

                    for (unsigned char j = 0; j < vectorLimit; ++j, ++correctMVContents, ++edgeMVContents) {

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
