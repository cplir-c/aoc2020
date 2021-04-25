#include "tileStructures.h"
#include "charVector.c"

#ifndef __TILE_STRUCTURES_C
    #define __TILE_STRUCTURES_C 1
    #ifdef DEBUG
static char const* const SIDE_STRING[4] = {"Top", "Right", "Bottom", "Left"};
static char const* const FORBACKWARD_STRINGS[2] = {"forward", "backward"};

usize printDebugVoid(charVector* out, fu16 indentation, Void* vuid) {
    appendNullString(out, "Void@");
    appendPointer(out, (void*) vuid);
}
usize printDebugEdge(charVector* out, fu16 indentation, Edge* edge) {
    appendNullString(out, "(Edge) {\n");

    appendLie(out, indentation + 4, ".forwardString = (char*) \"");
    appendNullString(out, edge -> forwardString);
    appendNullString(out, "\",\n");

    appendLie(out, indentation + 4, ".backwardString = (char*) \"");
    appendNullString(out, edge -> backwardString);
    appendNullString(out, "\",\n");

    appendLie(out, indentation + 4, ".forward = (fu16) ");
    appendHexSizeT(out, (usize) (edge -> forward));
    appendNullString(out, ",\n");

    appendLie(out, indentation + 4, ".backward = (fu16) ");
    appendHexSizeT(out, (usize) (edge -> backward));
    appendNullString(out, "\n");

    appendLie(out, indentation, "}");
}
usize printDebugTile(charVector* out, fu16 indentation, Tile* tile) {
    appendLine(out, 0, "(Tile) {\n");

    appendLie(out, indentation + 4, ".tileID = (fu16) ");
    appendSizeT(out, (usize) (tile -> tileID));
    appendNullString(out, ",\n");

    appendLie(out, indentation + 4, ".unrotatedTile = ");
    if (tile -> unrotatedTile == NULL) {
        appendNullString(out, "NULL");
    } else {
        appendNullString(out, "(char*)\n");

        appendLie(out, indentation + 8, "\"");
        for (char *chPtr = tile -> unrotatedTile, chr = *chPtr; chr != '\0'; ++chPtr, chr = *chPtr) {
            if (chr == '\n') {
                appendNullString(out, "\\n\"\n");

                appendLie(out, indentation + 8, "\"");
            } else {
                appendChar(out, chr);
            }
        }
        appendNullString(out, "\"\n");
    }

    appendLie(out, indentation + 4, ".sides = (Edge[4]) {");
    {
        Side side = Top;
        while (true) {
            Edge* edge = &((tile -> sides)[side]);
            appendLie(out, indentation + 8, "[");
            appendNullString(out, SIDE_STRING[side]);
            appendNullString(out, "] = ");
            printDebugEdge(out, indentation + 8, edge);
            if (side < Left) {
                appendNullString(out, ",\n");
                ++side;
            } else {
                appendChar(out, '\n');
                break;
            }
        }
    }
    appendLine(out, indentation + 4, "}");

    appendLie(out, indentation, "}");
}
usize printDebugSide(charVector* out, fu16 indentation, Side* side) {
    appendNullString(out, "(Side) ");
    appendNullString(out, SIDE_STRING[*side]);
}
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
