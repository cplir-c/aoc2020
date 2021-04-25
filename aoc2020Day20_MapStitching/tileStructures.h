#include "shortIntNames.h"
#include "charVector.h"

#ifndef __TILE_STRUCTURES_H
    #define __TILE_STRUCTURES_H 1

typedef struct {
} Void;
    #ifdef DEBUG
usize printDebugVoid(charVector* out, fu16 indentation, Void* vuid);
    #endif

typedef struct {
    char* forwardString;
    char* backwardString;
    fu16 forward;
    fu16 backward;
} Edge;
    #ifdef DEBUG
usize printDebugEdge(charVector* out, fu16 indentation, Edge* edge);
    #endif

typedef struct {
    fu16 tileID;
    char* unrotatedTile;
    Edge sides[4];
} Tile;
    #ifdef DEBUG
usize printDebugTile(charVector* out, fu16 indentation, Tile* tile);
    #endif

typedef enum {
    Top, Right, Bottom, Left
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

