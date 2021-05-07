
#include "shortIntNames.h"
#include "tileStructures.h"
#include "tileMaps.h"

#ifndef __TILE_PARSING_H
    #define __TILE_PARSING_H 1

struct MutEdge {
    char* forwardString;
    char* backwardString;
    fu16 forward;
    fu16 backward;
};
struct MutTile {
    fu16 tileID;
    char const* const unrotatedTile;
    struct MutEdge sides[4];
};

/* countTiles counts the tiles contained in a tilesString */
static fu16 countTiles(char tilesString[]);
/* findTiles puts in null characters in the tilesString
 * to snip the tilesString into individual tiles, and
 * puts them in an array of char pointers allocated using the count from countTiles
 */
static void findTiles(char tilesString[], char** tilesPointers);
/* buildTileList builds a TileVector of tile objects from an array of tile strings */
static bool buildTileList(fu16 tileCount, char** tilesPointers, TileVector* tiles, charVector* strings);
/* parseTileHeader parses the Tile ####:\n part of a tileString,
 * where #### is the 4 digit tileID, and puts the ID in the Tile*
 */
static char const* parseTileHeader(fu16* tileID, char const* tileStringPoint);
/* constructTileFromTileString builds a single tile object from a single tile string */
static bool constructTileFromTileString(char const* tileString, TileVector* tiles, charVector* strings);
/* constructIntEdgeFromCharEdge parses the forward and backward
 * edge strings in an Edge* into fu16 bit patterns, and
 * puts them back in the same Edge*
 */
static bool constructIntEdgeFromCharEdge(Edge* edge);
/* parseRowFromRowString parses a single edge string into its fu16 bit pattern */
static fu16 parseRowFromRowString(char const* rowString);

#endif
