#include "shortIntNames.h"
#include "charVector.h"

#ifndef __TILE_STRUCTURES_H
    #define __TILE_STRUCTURES_H

typedef struct {
} Void;

typedef struct {
    char* forwardString;
    char* backwardString;
    fu16 forward;
    fu16 backward;
} Edge;

typedef struct {
    fu16 tileID;
    char* unrotatedTile;
    Edge sides[4];
} Tile;

    #ifdef DEBUG
static char const* const SIDE_STRING[4] = {"Top", "Right", "Bottom", "Left"};
static char const* const FORBACKWARD_STRINGS[2] = {"forward", "backward"};
    #endif

typedef enum {
    Top, Right, Bottom, Left
} Side;

typedef struct {
    bool backwards;
    Side side;
    Tile* tile;
} EdgeReference;
    #ifdef DEBUG
usize printDebugEdgeReference(charVector* out, fu16 indentation, EdgeReference* element);
    #endif

typedef EdgeReference Placement;
#endif

