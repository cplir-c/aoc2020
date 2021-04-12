#include "shortIntNames.h"

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

typedef enum {
    Top, Right, Bottom, Left
} Side;

typedef struct {
    bool backwards;
    Side side;
    Tile* tile;
} EdgeReference;

typedef EdgeReference Placement;
#endif

