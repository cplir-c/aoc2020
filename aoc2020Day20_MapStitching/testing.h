#include <stdbool.h>

static void initializeCorrectTile();
static void initializedEdgeParsingTestEdge();
static void initializeCorrectEdgeMap();

static bool runTests();
static bool testRowParsing();
static bool testEdgeParsing();
static bool testTileParsing();
static bool testTileCounting();
static bool testTileFinding();
static bool testTileRotation();

static bool testDebugPrintAppending();
static bool testEdgeDebugPrinting();
static bool testTileDebugPrinting();
static bool testEdgeReferenceDebugPrinting();
static bool testVectorDebugPrinting();

static bool testEdgeMapBuilding();
static bool testTileSelfLookup();
static bool testTileMatchLookup();
