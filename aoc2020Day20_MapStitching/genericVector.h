
#ifdef E
#include "shortIntNames.h"

    #ifndef __GENERIC_VECTOR_H
        #define __GENERIC_VECTOR_H 1

        #define _TokenPaste(pre, post) pre ## post
        #define TokenPaste(pre, post) _TokenPaste(pre, post)
        #define Vector TokenPaste(E, Vector)
        #define vectorMethod(name) TokenPaste(name, Vector)


typedef struct {
    usize size;
    usize contentCount;
    E* contents;
} Vector;


bool vectorMethod(construct) (Vector* vector);
bool vectorMethod(construct2) (Vector* vector, usize requiredSpots);

bool vectorMethod(destruct) (Vector* vector);

bool vectorMethod(__growContents) (Vector* vector);
bool vectorMethod(__shrinkContents) (Vector* vector);

bool vectorMethod(trimToContents) (Vector* vector);
bool vectorMethod(preallocateSpace) (Vector* vector, usize itemCount);
bool vectorMethod(trimToSize) (Vector* vector, usize newSize);

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
WideE vectorMethod(addBlock) (Vector* vector, usize count);
bool vectorMethod(removeBlock) (Vector* vector, usize count);
bool vectorMethod(popBlock) (Vector* vector, usize count, WideE out);
// Segment methods: index in this vector
WideE vectorMethod(addSegment) (Vector* vector, usize index, usize count);
bool vectorMethod(removeSegment) (Vector* vector, usize index, usize count);
bool vectorMethod(popSegment) (Vector* vector, usize index, usize count, WideE out);
// Array methods: array contained in the vector
WideE vectorMethod(addArray) (Vector* vector, usize count, WideE array);
bool vectorMethod(removeArray) (Vector* vector, usize count, WideE array);
bool vectorMethod(popArray) (Vector* vector, usize count, WideE array, WideE out);

        #undef WideE
        #ifdef WideElement
            #undef WideElement
        #endif

E* vectorMethod(addItem) (Vector* vector, E* item);
E* vectorMethod(addIndex) (Vector* vector, usize index, E* item);
bool vectorMethod(removeItem) (Vector* vector);
bool vectorMethod(removeIndex) (Vector* vector, usize index);
bool vectorMethod(popItem) (Vector* vector, E* itemOut);
bool vectorMethod(popIndex) (Vector* vector, usize index, E* itemOut);

E* vectorMethod(getItem) (Vector* vector);
E* vectorMethod(getIndex) (Vector* vector, usize index);
E* vectorMethod(setItem) (Vector* vector, E* item);
E* vectorMethod(setIndex) (Vector* vector, usize index, E* item);


        #undef _TokenPaste
        #undef TokenPaste
        #undef Vector
        #undef vectorMethod
    #endif
#else
    #pragma message "tried to preprocess genericVector.h without E defined"
#endif

