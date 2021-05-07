#include "tileStructures.h"

#ifndef __TILE_STRUCTURES_C
    #define __TILE_STRUCTURES_C 1
    #ifdef DEBUG
    #include "charVector.c"
static char const* const SIDE_STRING[4] = {"Top", "Right", "Bottom", "Left"};
static char const* const FORBACKWARD_STRINGS[2] = {"forward", "backward"};

usize printDebugVoid(charVector* out, fu16 indentation, Void* vuid) {
    usize startSize = indentation;
    startSize = out -> contentCount;
    appendNullString(out, "Void@");
    appendPointer(out, (void*) vuid);
    return (out -> contentCount) - startSize;
}
usize printDebugEdge(charVector* out, fu16 indentation, Edge* edge) {
    usize startSize = out -> contentCount;
    appendNullString(out, "(Edge) {\n");

    appendLie(out, indentation + 4, ".forwardString = (char const*) \"");
    appendNullString(out, edge -> forwardString);
    appendNullString(out, "\",\n");

    appendLie(out, indentation + 4, ".backwardString = (char const*) \"");
    appendNullString(out, edge -> backwardString);
    appendNullString(out, "\",\n");

    appendLie(out, indentation + 4, ".forward = (fu16) ");
    appendHexSizeT(out, (usize) (edge -> forward));
    appendNullString(out, ",\n");

    appendLie(out, indentation + 4, ".backward = (fu16) ");
    appendHexSizeT(out, (usize) (edge -> backward));
    appendNullString(out, "\n");

    appendLie(out, indentation, "}");
    return (out -> contentCount) - startSize;
}
usize printDebugTile(charVector* out, fu16 indentation, Tile* tile) {
    usize startSize = out -> contentCount;
    appendLine(out, 0, "(Tile) {");

    appendLie(out, indentation + 4, ".tileID = (fu16) ");
    appendSizeT(out, (usize) (tile -> tileID));
    appendNullString(out, ",\n");

    appendLie(out, indentation + 4, ".unrotatedTile = ");
    if (tile -> unrotatedTile == NULL) {
        appendNullString(out, "NULL");
    } else {
        appendNullString(out, "(char const*)\n");

        appendLie(out, indentation + 8, "\"");
        for (char* chPtr = (char*) tile -> unrotatedTile, chr = *chPtr; chr != '\0'; ++chPtr, chr = *chPtr) {
            if (chr == '\n') {
                appendNullString(out, "\\n\"\n");

                appendLie(out, indentation + 8, "\"");
            } else {
                appendChar(out, chr);
            }
        }
        appendChar(out, '\"');
    }
    appendNullString(out, ",\n");

    appendLine(out, indentation + 4, ".sides = (Edge[4]) {");
    {
        Side side = Top;
        while (true) {
            Edge* edge = (Edge*) &((tile -> sides)[side]);
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
    return (out -> contentCount) - startSize;
}
usize printDebugSide(charVector* out, fu16 indentation, Side* side) {
    usize startSize = indentation;
    startSize = out -> contentCount;
    appendNullString(out, "(Side) ");
    appendNullString(out, SIDE_STRING[*side]);
    return (out -> contentCount) - startSize;
}
usize printDebugEdgeReference(charVector* out, fu16 indentation, EdgeReference* element) {
    usize startSize = out -> contentCount;
    appendNullString(out, "(EdgeReference) {\n");

    appendLie(out, indentation + 4, ".backwards = ");
    appendNullString(out, ((char*[2]) {"false", "true"})[element -> backwards]);
    appendChar(out, ',');
    appendNullString(out, " // ");
    appendNullString(out, FORBACKWARD_STRINGS[element -> backwards]);
    appendChar(out, '\n');

    appendLie(out, indentation + 4, ".side = ");
    appendSizeT(out, element -> side);
    appendChar(out, ',');
    appendNullString(out, " // ");
    appendNullString(out, SIDE_STRING[element -> side]);
    appendChar(out, '\n');

    if (element -> tile != NULL) {
        appendLie(out, indentation + 4, ".tile = ");
        printDebugTile(out, indentation + 4, element -> tile);
        appendChar(out, '\n');
    } else {
        appendLine(out, indentation + 4, ".tile = NULL");
    }

    appendLie(out, indentation, "}");
    return (out -> contentCount) - startSize;
}
    #endif
#endif
