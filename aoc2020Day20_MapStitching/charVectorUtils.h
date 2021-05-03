#include "shortIntNames.h"
#include "charVector.h"

#ifndef __CHAR_VECTOR_UTILS_H
    #define __CHAR_VECTOR_UTILS_H 1

static bool appendChars(charVector* out, fu16 repetitions, char character);
static bool appendChar(charVector* out, char character);
static bool appendNullString(charVector* out, char const* const nullString);

static bool appendLine(charVector* out, fu16 indentation, char const* const nullString);
// line without the \n
static bool appendLie(charVector* out, fu16 indentation, char const* const nullString);

static bool appendHexSizeT(charVector* out, usize in);
static bool appendSizeT(charVector* out, usize in);
static bool appendPointer(charVector* out, void* const in);
static bool appendDouble(charVector* out, double in);
#endif
