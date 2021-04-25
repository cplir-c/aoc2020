#include "shortIntNames.h"
#include "tileMaps.h"

#ifndef __CHAR_VECTOR_UTILS_H
    #define __CHAR_VECTOR_UTILS_H 1

static bool appendChars(charVector* out, fu16 repetitions, char character);
static bool appendChar(charVector* out, char character);
static bool appendNullString(charVector* out, char* nullString);

static bool appendLine(charVector* out, fu16 indentation, char* nullString);
// line without the \n
static bool appendLie(charVector* out, fu16 indentation, char* nullString);

static bool appendSizeT(charVector* out, usize in);

#endif
