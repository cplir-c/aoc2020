
#include "shortIntNames.h"
#include "tileMaps.c"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#ifndef DEBUG
    #error "Included charVectorUtils.c without setting debug macro"
#endif
#include "charVectorUtils.h"

#ifndef __CHAR_VECTOR_UTILS_C
    #define __CHAR_VECTOR_UTILS_C 1

static bool appendChars(charVector* out, fu16 repetitions, char character) {
    char* targetPosition = addBlockcharVector(out, repetitions);
    if (targetPosition == NULL) {
        fprintf(stderr, "failed to append %zu of '%c' to charVector %p\n", repetitions, character, (void*) out);
        return false;
    }
    memset(targetPosition, repetitions, character);
    return true;
}
static bool appendChar(charVector* out, char character) {
    char* put = addItemcharVector(out, &character);
    return put != NULL;
}
static bool appendNullString(charVector* out, char* nullString) {
    usize strLength = strlen(nullString);
    if (strLength == 0) {
        fprintf(stderr, "failed to add string at %p to charVector %p\n", (void*) nullString, (void*) out);
        return false;
    }
    --strLength; // forget the null
    char* targetPosition = addBlockcharVector(out, strLength);
    memcpy(targetPosition, nullString, strLength);
    return true;
}

static bool appendLine(charVector* out, fu16 indentation, char* nullString) {
    return appendChars(out, indentation, ' ')
        && appendNullString(out, nullString)
        && appendChar(out, '\n');
}
// line without the \n
static bool appendLie(charVector* out, fu16 indentation, char* nullString) {
    return appendChars(out, indentation, ' ')
        && appendNullString(out, nullString);
}

static bool appendSizeT(charVector* out, usize in) {
    char* dest = addBlockcharVector(out, sizeof(usize) * 2);
    usize written = 0;
    sprintf(dest, "%zn%zu", &written, in);
    removeBlockcharVector(out, sizeof(usize) * 2 - written);
}
#endif
