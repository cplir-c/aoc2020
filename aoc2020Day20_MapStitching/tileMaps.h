
#include "tileStructures.h"
#ifndef __TILE_MAPS_H
    #define __TILE_MAPS_H 1

    #include "charVector.h"
    #define E EdgeReference
        #include "genericVector.h"
    #undef E
    #define E Tile
        #include "genericVector.h"
    #undef E
    #define E fu16
        #include "genericVector.h"
    #undef E

    #define K fu16
    #define V EdgeReferenceVector
    #define VALUE_DESTRUCTOR destructEdgeReferenceVector
usize hashfu16Key (fu16* key);
        #include "genericHashmap.h"
    #undef VALUE_DESTRUCTOR
    #undef V
    #undef K

    #define K fu16
    #define V Void
        #include "genericHashmap.h"
    #undef V
    #undef K
#endif
