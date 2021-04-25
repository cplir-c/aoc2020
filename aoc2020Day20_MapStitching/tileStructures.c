#include "tileStructures.h"
#include "charVector.c"

#ifndef __TILE_STRUCTURES_C
    #define __TILE_STRUCTURES_C 1
    #ifdef DEBUG
usize printDebugEdgeReference(charVector* out, fu16 indentation, EdgeReference* element) {
    appendNullString(out, "(EdgeReference) {\n");

    appendLie(out, indentation + 4, ".backwards = ");
    appendChar(out, '0' + (element -> backwards));
    appendNullString(out, "// ");
    appendNullString(out, FORBACKWARD_STRINGS[element -> backwards]);
    appendNullString(out, ",\n");

    appendLie(out, indentation + 4, ".side = ");
    appendSizeT(out, element -> side);
    appendNullString(out, "// ");
    appendNullString(out, SIDE_STRING[element -> side]);
    appendNullString(out, ",\n");

    if (element -> tile != NULL) {
        appendLie(out, indentation + 4, ".tile = ");
        printDebugTile(out, indentation + 4, element -> tile);
        appendChar(out, '\n');
    } else {
        appendLine(out, indentation + 4, ".tile = NULL");
    }

    appendLie(out, indentation, "}");
}
    #endif
#endif
