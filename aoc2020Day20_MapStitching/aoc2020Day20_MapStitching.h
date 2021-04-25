#include "tileMaps.h"

/* main is the entrypoint of this program */
extern int main();

/* testStitching is for wrapping findCornerIDProduct in a test case */
static bool testStitching(char tilesString[], fu64 expectedCornerIDProduct);
/* findCornerIDProduct is the
 * main entrypoint for stitching tiles
 * Here it processes the tile strings into tile objects and indexes them by edge
 */
static fu64 findCornerIDProduct(char tilesString[]);

