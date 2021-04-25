
//include "tileMaps.h"

#include "tileStructures.h"
#ifndef __TILE_MAPS_C
    #define __TILE_MAPS_C 1

    #ifndef __TILE_MAPS_H
        #define __TILE_MAPS_H 1
    #endif

    #include "charVector.c"
    #define E EdgeReference
    #define printDebugEdgeReferenceElement printDebugEdgeReference
        #include "genericVector.c"
    #undef printDebugEdgeReferenceElement
    #undef E
    #define E Tile
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
    #ifdef DEBUG
usize printDebugfu16Key (charVector* out, fu16 indentation, fu16* key) {
    return appendSizeT(out, (usize) *key);
}
usize printDebugEdgeReferenceVectorValue (charVector* out, fu16 indentation, EdgeReferenceVector* value) {
    printDebugEdgeReferenceVector(out, indentation, value);
}
    #endif
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
