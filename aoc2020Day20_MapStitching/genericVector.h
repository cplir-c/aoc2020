
#ifdef E
#include "shortIntNames.h"

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

E* vectorMethod(addBlock) (Vector* vector, usize count);

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
#else
    #pragma message "tried to preprocess genericVector.h without E defined"
#endif

