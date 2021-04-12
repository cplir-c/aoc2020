#include "tileMaps.h"

/* testStitching is for wrapping findCornerIDProduct in a test case */
bool testStitching(char tilesString[], fu64 expectedCornerIDProduct);
/* findCornerIDProduct is the
 * main entrypoint for stitching tiles
 * Here it processes the tile strings into tile objects and indexes them by edge
 */
fu64 findCornerIDProduct(char tilesString[]);
/* countTiles counts the tiles contained in a tilesString */
fu16 countTiles(char tilesString[]);
/* findTiles puts in null characters in the tilesString
 * to snip the tilesString into individual tiles, and
 * puts them in an array of char pointers allocated using the count from countTiles
 */
void findTiles(char tilesString[], char** tilesPointers);
/* buildTileList builds a TileVector of tile objects from an array of tile strings */
bool buildTileList(fu16 tileCount, char** tilesPointers, TileVector* tiles, charVector* strings);
/* parseTileHeader parses the Tile ####:\n part of a tileString,
 * where #### is the 4 digit tileID, and puts the ID in the Tile*
 */
char* parseTileHeader(Tile* tile, char* tileStringPoint);
/* constructTileFromTileString builds a single tile object from a single tile string */
bool constructTileFromTileString(char* tileString, TileVector* tiles, charVector* strings);
/* constructIntEdgeFromCharEdge parses the forward and backward
 * edge strings in an Edge* into fu16 bit patterns, and
 * puts them back in the same Edge*
 */
bool constructIntEdgeFromCharEdge(Edge* edge);
/* parseRowFromRowString parses a single edge string into its fu16 bit pattern */
fu16 parseRowFromRowString(char* rowString);
/* addEdgeToEdgeMap adds an EdgeReference to the EdgeReference MultiMap */
bool addEdgeToEdgeMap(fu16* edgeInt, EdgeReference* edgeRef, fu16ToEdgeReferenceVectorOpenHashMap* edges);
/* buildEdgeMap fills in the edge map with
 * the edge references of the tiles its given
 */
bool buildEdgeMap(TileVector* tiles, fu16ToEdgeReferenceVectorOpenHashMap* edges);
/* main is the entrypoint of this program */
int main(int argc, char **argv);
