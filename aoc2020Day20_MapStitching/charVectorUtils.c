
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "shortIntNames.h"
#include "charVector.c"

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
    memset(targetPosition, character, repetitions);
    return true;
}
static bool appendChar(charVector* out, char character) {
    char* put = addItemcharVector(out, &character);
    return put != NULL;
}
static bool appendNullString(charVector* out, char const* const nullString) {
    usize strLength = strlen(nullString);
    if (strLength == 0) {
        fprintf(stderr, "failed to add string at %p to charVector %p\n", (void const* const) nullString, (void*) out);
        return false;
    }
    char* targetPosition = addBlockcharVector(out, strLength);
    memcpy(targetPosition, nullString, strLength);
    return true;
}

static bool appendLine(charVector* out, fu16 indentation, char const* const nullString) {
    if (!appendChars(out, indentation, ' ')
        || !appendNullString(out, nullString)
        || !appendChar(out, '\n')) {
        fprintf(stderr, "failed to print line: charVector out %p, fu16 indentation %lu, char* nullString %p \"%s\"\n", (void*) out, indentation, (void*) nullString, nullString);
        return false;
    }
    return true;
}
// line without the \n
static bool appendLie(charVector* out, fu16 indentation, char const* const nullString) {
    return appendChars(out, indentation, ' ')
        && appendNullString(out, nullString);
}

static bool appendHexSizeT(charVector* out, usize in) {
    char* dest = addBlockcharVector(out, sizeof(usize) * 4 + 2);
    if (dest == NULL) {
        fprintf(stderr, "failed to append hex size_t %zx to output char vector@%p\n", in, (void*) out);
        return false;
    }
    long written = 0;
    sprintf(dest, "0x%zx%zn", in, &written);
    //--written; // delete the null byte
    return removeBlockcharVector(out, sizeof(usize) * 4 + 2 - ((usize)written));
}
static bool appendSizeT(charVector* out, usize in) {
    char* dest = addBlockcharVector(out, sizeof(usize) * 4);
    if (dest == NULL) {
        fprintf(stderr, "failed to append size_t %zu to output char vector@%p\n", in, (void*) out);
        return false;
    }
    long written = 0;
    sprintf(dest, "%zu%zn", in, &written);
    //--written; // delete the null byte
    return removeBlockcharVector(out, sizeof(usize) * 4 - ((usize)written));
}
static bool appendPointer(charVector* out, void* in) {
    char* dest = addBlockcharVector(out, sizeof(void*) * 4);
    if (dest == NULL) {
        fprintf(stderr, "failed to append pointer %p to output char vector@%p\n", in, (void*) out);
        return false;
    }
    long written = 0;
    sprintf(dest, "%p%zn", in, &written);
    //--written; // delete the null byte
    return removeBlockcharVector(out, sizeof(usize) * 4 - ((usize)written));
}
static bool appendDouble(charVector* out, double in) {
    char* dest = addBlockcharVector(out, sizeof(double) * 8);
    if (dest == NULL) {
        fprintf(stderr, "failed to append double %g to output char vector@%p\n", in, (void*) out);
        return false;
    }
    long written = 0;
    sprintf(dest, "%g%zn", in, &written);
    //--written; // delete the null byte
    return removeBlockcharVector(out, sizeof(double) * 8 - ((usize)written));
}
#endif
