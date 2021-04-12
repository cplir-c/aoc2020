
//include "tileMaps.h"

#include "tileStructures.h"
#ifndef __TILE_MAPS_C
    #define __TILE_MAPS_C

    #ifndef __TILE_MAPS_H
        #define __TILE_MAPS_H
    #endif
    
    #define E EdgeReference
        #include "genericVector.c"
    #undef E
    #define E Tile
        #include "genericVector.c"
    #undef E
    #define E char
        #include "genericVector.c"
    #undef E
    #define E fu16
        #include "genericVector.c"
    #undef E

    #define K fu16
    #define V EdgeReferenceVector
    #define VALUE_DESTRUCTOR destructEdgeReferenceVector
usize hashfu16Key (fu16* key) {
    return (usize)((fu16) *key);
}
        #include "genericHashmap.c"
    #undef VALUE_DESTRUCTOR
    #undef V
    #undef K

    #define K fu16
    #define V Void
        #include "genericHashmap.c"
    #undef V
    #undef K
#endif
