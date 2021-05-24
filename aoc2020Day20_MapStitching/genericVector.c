
#ifndef E
    //define E char
    #pragma message "tried to compile genericVector c without E defined"
#else

#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include "genericVector.h"

#define _TokenPaste(pre, post) pre ## post
#define TokenPaste(pre, post) _TokenPaste(pre, post)
#define Vector TokenPaste(E, Vector)
#define vectorMethod(name) TokenPaste(name, Vector)
#define _stringify(name) #name
#define stringify(name) _stringify(name)
#define EString stringify(E)

#ifdef DEBUG
    #include "charVectorUtils.c"
    #include "tileStructures.c"
#endif

bool vectorMethod(construct) (Vector* vector) {
    usize size = 4;
    vector -> size = 4;
    vector -> contentCount = 0;
    E* contents = aligned_alloc(_Alignof(E), sizeof(E) * size);
    vector -> contents = contents;
    if (contents == NULL) {
        fprintf(stderr, "failed to allocate default size " EString " vector\n");
        return false;
    }
    return true;
}
bool vectorMethod(construct2) (Vector* vector, usize requiredSpots) {
    vector -> size = requiredSpots;
    vector -> contentCount = 0;
    E* contents = aligned_alloc(_Alignof(E), sizeof(E) * requiredSpots);
    vector -> contents = contents;
    if (contents == NULL) {
        fprintf(stderr, "failed to allocate %ld element " EString " vector, or %ld bytes\n", requiredSpots, requiredSpots * sizeof(E));
        return false;
    }
    return true;
}

bool vectorMethod(destruct) (Vector* vector) {
#ifdef ELEMENT_DESTRUCTOR
    E* contents = vector -> contents;
    bool success = true;
    for (E* item = (vector -> contents) + ((vector -> contentCount) - 1); item >= contents; --item) {
        success &= ELEMENT_DESTRUCTOR(item);
    }
#endif
    free(vector -> contents);
    (vector -> size) = 0;
    (vector -> contentCount) = 0;
#ifdef ELEMENT_DESTRUCTOR
    return success;
#else
    return true;
#endif
}

E* vectorMethod(addItem) (Vector* vector, E* item) {
    usize contentCount = vector -> contentCount;
    if ((vector -> size) <= contentCount && !vectorMethod(__growContents)(vector)) {
        fprintf(stderr, "failed to grow " EString " vector while adding an element\n");
        return NULL;
    }
    E* target = (vector -> contents) + contentCount;
    *target = *item;
    vector -> contentCount = ++contentCount;
    return target;
}

bool vectorMethod(__growContents) (Vector* vector) {
    E* contents = vector -> contents;
    usize size = vector -> size;
    size <<= 1;
    contents = realloc(contents, size * sizeof(E));
    if (contents == NULL) {
        fprintf(stderr, "failed to grow " EString " vector to size %lu, or %lu bytes\n", size, size * sizeof(E));
        return false;
    }
    fprintf(stderr, "grew vector of type " EString "\n");
    vector -> contents = contents;
    vector -> size = size;
    return true;
}
bool vectorMethod(__shrinkContents) (Vector* vector) {
    Vector v = *vector; // fetch all the vector fields at once
    v.size >>= 1;
    if (v.size < v.contentCount) {
        v.size = v.contentCount;
    }
    v.contents = realloc(v.contents, v.size * sizeof(E));
    if (v.contents == NULL) {
        fprintf(stderr, "failed to shrink " EString " vector to size %lu, or %lu bytes\n", v.size, v.size * sizeof(E));
        return false;
    }
    vector -> size = v.size;
    vector -> contents = v.contents;
    // vector.contentCount == v.contentCount so don't set it
    return true;
}

bool vectorMethod(trimToContents) (Vector* vector) {
    usize newSize = vector -> contentCount;
    if ((vector -> size) <= newSize) {
        return true; // successfully changed nothing
    }
    //if (newSize < 2) {
    //    newSize = 2;
    //}
    newSize |= 2;
    E* contents = realloc(vector -> contents, newSize * sizeof(E));
    if (contents == NULL) {
        fprintf(stderr, "failed to shrink " EString " vector to its contents size of %lu elements\n", newSize);
        return false;
    }
    vector -> size = newSize;
    vector -> contents = contents;
    return true;
}
bool vectorMethod(preallocateSpace) (Vector* vector, usize itemCount) {
    usize newSize = (vector -> contentCount) + itemCount;
    if (newSize < (vector -> size)) {
        return true; // successfully preallocated space for items
    }
    E* contents = realloc(vector -> contents, newSize * sizeof(E));
    if (contents == NULL) {
        fprintf(stderr, "failed to grow " EString " vector to accomodate preallocating %lu elements, for a new size of %lu elements\n", itemCount, newSize);
        return false;
    }
    vector -> size = newSize;
    vector -> contents = contents;
    return true;
}
bool vectorMethod(trimToSize) (Vector* vector, usize newSize) {
    usize oldSize = vector -> size;
    //if (newSize < 2) {
    //    newSize = 2;
    //}
    newSize |= 2;
    if (newSize > oldSize) {
        return false;
    } else if (newSize == oldSize) {
        return true;
    }
    E* contents = vector -> contents;
    usize oldContentCount = vector -> contentCount;
#ifdef ELEMENT_DESTRUCTOR
    // FIXME
    if (oldContentCount > newSize) {
        for (E* item = contents + (oldContentCount - 1); item >= contents; --item){
            ELEMENT_DESTRUCTOR(item);
        }
    }
#endif
    contents = realloc(contents, newSize * sizeof(E));
    if (contents == NULL) {
        fprintf(stderr, "failed to trim " EString " vector to a size of %lu elements\n", newSize);
        return false;
    }
    vector -> size = newSize;
    vector -> contents = contents;
    if (oldContentCount > newSize) {
        vector -> contentCount = newSize;
    }
    return true;
}

#ifdef __STDC_NO_VLA__
    #if (__STDC_NO_VLA__ == 1)
        #define WideElement TokenPaste(E, Array);
        typedef E[*] WideElement;
        #define WideE WideElement
    #endif
#endif
#ifndef WideE
    #define WideE E*
#endif

// Block methods: end of this vector
WideE vectorMethod(addBlock) (Vector* vector, usize count) {
    if (!vectorMethod(preallocateSpace)(vector, count)) {
        fprintf(stderr, "failed to allocate block of %zu items in a " EString " vector, returning NULL\n", count);
        return NULL;
    }
    usize contentCount = vector -> contentCount;
    E* targetBlock = (vector -> contents) + contentCount;
    contentCount += count;
    vector -> contentCount = contentCount;
    return (WideE) targetBlock;
}
bool vectorMethod(removeBlock) (Vector* vector, usize count) {
    if (count > (vector -> contentCount)) {
        fprintf(stderr, "failed to remove block of %zu items from a " EString " vector with %zu items\n", count, vector -> contentCount);
        return false;
    }
    (vector -> contentCount) -= count;
    return true;
}
bool vectorMethod(popBlock) (Vector* vector, usize count, WideE out) {
    if (count > (vector -> contentCount)) {
        fprintf(stderr, "failed to pop off block of %zu items from a " EString " vector with %zu items\n", count, vector -> contentCount);
        return false;
    }
    WideE inside = (WideE) ((vector -> contents) + ((vector -> contentCount) - count));
    memcpy(out, inside, count * sizeof(E));
    (vector -> contentCount) -= count;
    return true;
}

// Segment methods: index in this vector
WideE vectorMethod(addSegment) (Vector* vector, usize index, usize count) {
    usize contentCount = vector -> contentCount;
    if (index + count > contentCount) {
        fprintf(stderr, "failed to allocate items at index %zu in a %zu element " EString " vector\n", index + count, contentCount);
        return NULL;
    }
    if (!vectorMethod(preallocateSpace)(vector, count)) {
        fprintf(stderr, "failed to allocate space for %zu more items in this " EString " vector of %zu items.\n", count, contentCount);
        return NULL;
    }
    E* source = (vector -> contents) + index;
    usize itemsToMove = contentCount - index;
    usize newContentCount = contentCount + count;
    E* destination = (vector -> contents) + (newContentCount - itemsToMove);
    memmove(destination, source, itemsToMove * sizeof(E));
    vector -> contentCount = newContentCount;
    return (WideE) source;
}
bool vectorMethod(removeSegment) (Vector* vector, usize index, usize count) {
    usize contentCount = vector -> contentCount;
    usize firstUndeletedIndex = index + count;
    if (firstUndeletedIndex > contentCount) {
        fprintf(stderr, "failed to remove item at position %zu from a " EString " vector of %zu items.\n", firstUndeletedIndex, contentCount);
        return false;
    }
    usize newContentCount = contentCount - count;
    usize itemsToMove = contentCount - firstUndeletedIndex;
    E* contents = vector -> contents;
    E* source = contents + firstUndeletedIndex;
    E* destination = contents + index;
#ifdef ELEMENT_DESTRUCTOR
    for (E* item = destination; item < source; ++item) {
        ELEMENT_DESTRUCTOR(item);
    }
#endif
    memmove(destination, source, itemsToMove * sizeof(E));
    vector -> contentCount = newContentCount;
    return true;
}
bool vectorMethod(popSegment) (Vector* vector, usize index, usize count, WideE out) {
    usize contentCount = vector -> contentCount;
    usize firstUndeletedIndex = index + count;
    if (firstUndeletedIndex > contentCount) {
        fprintf(stderr, "failed to remove item at position %zu from a " EString " vector of %zu items.\n", firstUndeletedIndex, contentCount);
        return false;
    }
    usize newContentCount = contentCount - count;
    usize itemsToMove = contentCount - firstUndeletedIndex;
    E* contents = vector -> contents;
    E* source = contents + firstUndeletedIndex;
    E* destination = contents + index;
    memcpy(out, destination, count * sizeof(E));
    memmove(destination, source, itemsToMove * sizeof(E));
    vector -> contentCount = newContentCount;
    return true;
}

// Array methods: array contained in the vector
WideE vectorMethod(addArray) (Vector* vector, usize count, WideE array) {
    E* eArray = (E*) array;
    E* contents = vector -> contents;
    usize contentCount = vector -> contentCount;
    if (eArray < contents || eArray + count >= contents + contentCount) {
        fprintf(stderr, "failed to add %zu item array at index %td in a " EString " vector of %zu items.\n", count, ((isize) eArray) - ((isize)contents), contentCount);
        return NULL;
    }
    if (!vectorMethod(preallocateSpace)(vector, count)) {
        fprintf(stderr, "failed to allocate space for %zu more items in a vector of %zu " EString " elements", count, contentCount);
        return NULL;
    }
    usize index = (usize) (eArray - contents) / _Alignof(E);
    E* source = (vector -> contents) + index;
#ifdef DEBUG
    if (source != eArray) {
        fprintf(stderr, "ALIGNMENT DIVISION ASSERTION FAILED, failing\n");
        abort();
    }
#endif
    usize newContentCount = contentCount + count;
    usize itemsToMove = contentCount - index;
    E* destination = contents + (newContentCount - itemsToMove);
    memmove(destination, source, itemsToMove * sizeof(E));
    vector -> contentCount = newContentCount;
    return (WideE) source;
}
bool vectorMethod(removeArray) (Vector* vector, usize count, WideE array) {
    E* eArray = (E*) array;
    E* contents = vector -> contents;
    usize contentCount = vector -> contentCount;
    if (eArray < contents || eArray + count >= contents + contentCount) {
        fprintf(stderr, "failed to remove %zu item array at index %td in a " EString " vector of %zu items.\n", count, ((isize) eArray) - ((isize)contents), contentCount);
        return false;
    }

    usize index = (usize) (eArray - contents) / _Alignof(E);
    E* destination = contents + index;
#ifdef DEBUG
    if (destination != eArray) {
        fprintf(stderr, "ALIGNMENT DIVISION ASSERTION FAILED, failing\n");
        abort();
    }
#endif
    usize newContentCount = contentCount - count;
    usize firstUndeletedIndex = index + count;
    usize itemsToMove = contentCount - firstUndeletedIndex;
    E* source = contents + firstUndeletedIndex;
 // E* destination = contents + index; // already above
#ifdef ELEMENT_DESTRUCTOR
    for (E* item = destination; item < source; ++item) {
        ELEMENT_DESTRUCTOR(item);
    }
#endif
    memmove(destination, source, itemsToMove * sizeof(E));
    vector -> contentCount = newContentCount;
    return true;
}
bool vectorMethod(popArray) (Vector* vector, usize count, WideE array, WideE out) {
    E* eArray = (E*) array;
    E* contents = vector -> contents;
    usize contentCount = vector -> contentCount;
    if (eArray < contents || eArray + count >= contents + contentCount) {
        fprintf(stderr, "failed to remove %zu item array at index %td in a " EString " vector of %zu items.\n", count, ((isize) eArray) - ((isize)contents), contentCount);
        return false;
    }

    usize index = (usize) (eArray - contents) / _Alignof(E);
    E* destination = contents + index;
#ifdef DEBUG
    if (destination != eArray) {
        fprintf(stderr, "ALIGNMENT DIVISION ASSERTION FAILED, failing\n");
        abort();
    }
#endif
    usize newContentCount = contentCount - count;
    usize firstUndeletedIndex = index + count;
    usize itemsToMove = contentCount - firstUndeletedIndex;
    E* source = contents + firstUndeletedIndex;
 // E* destination = contents + index; // already above
    memcpy(out, destination, count * sizeof(E));
    memmove(destination, source, itemsToMove * sizeof(E));
    vector -> contentCount = newContentCount;
    return true;
}

#undef WideE
#ifdef WideElement
    #undef WideElement
#endif

E* vectorMethod(addIndex) (Vector* vector, usize index, E* item) {
    usize size = (vector -> size);
    usize contentCount = (vector -> contentCount);
    if (index > contentCount) {
        fprintf(stderr, "index out of bounds to insert " EString " item: %lu\n", index);
    }
    if (contentCount >= size && !vectorMethod(__growContents)(vector)) {
        fprintf(stderr, "failed to grow " EString " vector to add an item\n");
        return NULL;
    }
    E* indexPointer = (vector -> contents) + index;
    memmove(indexPointer + 1, indexPointer, (contentCount - index) * sizeof(E));
    (vector -> contentCount) = contentCount + 1;
    return indexPointer;
}
bool vectorMethod(removeItem) (Vector* vector) {
    usize newContentCount = vector -> contentCount;
    if (newContentCount == 0) {
        fprintf(stderr, "failed to remove last item of empty " EString " vector\n");
        return false;
    }
    --newContentCount;
#ifdef ELEMENT_DESTRUCTOR
    register E* target = (vector -> contents) + newContentCount;
    ELEMENT_DESTRUCTOR(target);
#endif
    vector -> contentCount = newContentCount;
    return true;
}
bool vectorMethod(removeIndex) (Vector* vector, usize index) {
    usize contentCount = vector -> contentCount;
    // bounds check
    if (index >= contentCount) {
        fprintf(stderr, EString " vector of item count %ld does not contain an elment at index %ld", contentCount, index);
        return false;
    }

    E* contents = vector -> contents;
    E* target = (contents + index);
#ifdef ELEMENT_DESTRUCTOR
    ELEMENT_DESTRUCTOR(target);
#endif
    if (index < contentCount - 1) {
        memmove(target, target + 1, (contentCount - index) * sizeof(E));
    }
    vector -> contentCount = --contentCount;
    return true;
}
bool vectorMethod(popItem) (Vector* vector, E* itemOut) {
    usize contentCount = vector -> contentCount;
    if (contentCount == 0) {
        fprintf(stderr, "failed to pop item from an empty " EString " vector.\n");
        return false;
    }
#ifdef ELEMENT_DESTRUCTOR
    ELEMENT_DESTRUCTOR(itemOut);
#endif
    vector -> contentCount = --contentCount;
    E* target = (vector -> contents) + contentCount;
    *itemOut = *target;
    return true;
}
bool vectorMethod(popIndex) (Vector* vector, usize index, E* itemOut) {
    usize contentCount = vector -> contentCount;
    if (index >= contentCount) {
        fprintf(stderr, "failed to pop item from index %ld of a " EString " vector with %ld items\n", index, contentCount);
        return false;
    }

#ifdef ELEMENT_DESTRUCTOR
    ELEMENT_DESTRUCTOR(itemOut);
#endif
    E* contents = vector -> contents;
    E* target = contents + index;

    if (index < contentCount - 1) {
        memmove(target, target + 1, (contentCount - index) * sizeof(E));
    }
    vector -> contentCount = --contentCount;
    return true;
}

E* vectorMethod(getItem) (Vector* vector) {
    usize contentCount = vector -> contentCount;
    if (contentCount == 0) {
        fprintf(stderr, "Failed to get the last element of this " EString " vector, returning NULL.\n");
        return NULL;
    }
    return (vector -> contents) + (contentCount - 1);
}
E* vectorMethod(getIndex) (Vector* vector, usize index) {
    usize contentCount = vector -> contentCount;
    if (index >= contentCount) {
        fprintf(stderr, "Failed to get element %ld of this " EString " vector, returning NULL\n", index);
        return NULL;
    }
    return (vector -> contents) + index;
}
E* vectorMethod(setItem) (Vector* vector, E* item) {
    usize contentCount = vector -> contentCount;
    if (contentCount == 0) {
        fprintf(stderr, "Failed to set the end of an empty " EString " vector, returning NULL.\n");
        return NULL;
    }
    E* target = (vector -> contents) + (contentCount - 1);
#ifdef ELEMENT_DESTRUCTOR
    ELEMENT_DESTRUCTOR(target);
#endif
    *target = *item;
    return target;
}
E* vectorMethod(setIndex) (Vector* vector, usize index, E* item) {
    usize contentCount = vector -> contentCount;
    if (index >= contentCount) {
        fprintf(stderr, "Tried to set index %ld of a %ld element " EString " vector. Returning null.\n", index, contentCount);
        return NULL;
    }
    E* target = (vector -> contents) + index;
#ifdef ELEMENT_DESTRUCTOR
    ELEMENT_DESTRUCTOR(target);
#endif
    *target = *item;
    return target;
}

#ifdef DEBUG
    #define VecString stringify(Vector)
    #define EElement TokenPaste(E, Element)
    #define printDebugElement TokenPaste(printDebug, EElement)
extern usize printDebugElement(charVector* out, fu16 indentation, E* element);
// char vector should work as long as it's the first defined vector
static usize vectorMethod(printDebug) (charVector* out, fu16 indentation, Vector* vector) {
    usize oldPrintPosition = out -> contentCount;
    if (indentation == 0) {
        appendLine(out, 0, VecString "");
    } else {
        appendNullString(out, "(" VecString ") {\n");
    }

    appendLie(out, indentation + 4, ".size = ");
    appendSizeT(out, vector -> size);
    appendNullString(out, ",\n");

    usize contentCount = vector -> contentCount;
    appendLie(out, indentation + 4, ".contentCount = ");
    appendSizeT(out, contentCount);
    appendNullString(out, ",\n");

    if (contentCount == 0) {
        appendLine(out, indentation + 4, ".contents = {}");
    } else if (vector -> contents != NULL) {
        appendLie(out, indentation + 4, ".contents = {");

        for (usize i = 0; i < contentCount; ++i) {
            appendLie(out, indentation + 8, "[");
            appendSizeT(out, i);
            appendNullString(out, "] = ");
            printDebugElement(out, indentation + 8, (vector -> contents) + i);
            if (i + 1 < contentCount) {
                appendNullString(out, ",\n");
            } else {
                appendChar(out, '\n');
            }
        }

        appendLine(out, indentation + 4, "}");
    } else {
        appendLine(out, indentation + 4, ".contents = NULL");
    }

    appendLie(out, indentation, "}");
    return (out -> contentCount) - oldPrintPosition;
}
    #undef printDebugElement
    #undef EElement
    #undef VecString
#endif

#undef _TokenPaste
#undef TokenPaste
#undef Vector
#undef vectorMethod
#undef _stringify
#undef stringify
#undef EString
#undef assignItem

#ifdef __GENERIC_VECTOR_H
    #undef __GENERIC_VECTOR_H
#endif

#endif // E guard
