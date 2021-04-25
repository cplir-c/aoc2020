
//include "tileMaps.h"

#include "tileStructures.c"
#ifndef __TILE_MAPS_C
    #define __TILE_MAPS_C 1

    #ifndef __TILE_MAPS_H
        #define __TILE_MAPS_H 1
    #endif

    #include "charVector.c"
    #define E EdgeReference
    #ifdef DEBUG
        #define printDebugEdgeReferenceElement printDebugEdgeReference
    #endif
        #include "genericVector.c"
    #ifdef printDebugEdgeReferenceElement
        #undef printDebugEdgeReferenceElement
    #endif
    #undef E
    #define E Tile
    #define printDebugTileElement printDebugTile
        #include "genericVector.c"
    #undef printDebugTileElement
    #undef E
    #define E fu16
    #ifdef DEBUG
usize printDebugfu16Element(charVector* out, fu16 indentation, fu16* element) {
    appendSizeT(out, (usize) (*element));
}
    #endif
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
    appendSizeT(out, (usize) *key);
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
    #ifdef DEBUG
        #define printDebugVoidValue printDebugVoid
    #endif
        #include "genericHashmap.c"
    #ifdef DEBUG
        #undef printDebugVoidValue
    #endif
    #undef V
    #undef K
#endif
