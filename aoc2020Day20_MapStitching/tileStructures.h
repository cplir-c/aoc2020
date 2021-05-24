#include "shortIntNames.h"

#ifndef __TILE_STRUCTURES_H
    #define __TILE_STRUCTURES_H 1
    #include "charVector.h"

typedef struct {
    _Bool $;
} Void;
    #ifdef DEBUG
usize printDebugVoid(charVector* out, fu16 indentation, Void* vuid);
    #endif

typedef struct {
    char const* forwardString;
    char const* backwardString;
    fu16 forward;
    fu16 backward;
} Edge;
    #ifdef DEBUG
usize printDebugEdge(charVector* out, fu16 indentation, Edge* edge);
    #endif

#define SIDE_COUNT 4
typedef struct {
    fu16 tileID;
    char const* unrotatedTile;
    Edge sides[SIDE_COUNT];
} Tile;
    #ifdef DEBUG
usize printDebugTile(charVector* out, fu16 indentation, Tile* tile);
    #endif

typedef enum {
    Top = 0,
    Right = 1,
    Bottom = 2,
    Left = 3
} Side;
    #ifdef DEBUG
usize printDebugSide(charVector* out, fu16 indentation, Side* side);
    #endif

typedef struct {
    bool backwards;
    Side side;
    Tile* tile;
} EdgeReference;
    #ifdef DEBUG
usize printDebugEdgeReference(charVector* out, fu16 indentation, EdgeReference* edgeReference);
    #endif

typedef EdgeReference Placement;
#endif

