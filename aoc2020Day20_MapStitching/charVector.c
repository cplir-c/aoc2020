
#include "shortIntNames.h"
#include "charVectorUtils.h"

#ifndef __CHAR_VECTOR_C
    #define __CHAR_VECTOR_C 1
    #include "charVectorUtils.c"
    #define E char
        #include "genericVector.h"
usize printDebugcharElement(charVector* out, fu16 indentation, char* element) {
	usize startSize = indentation;
	startSize = out->contentCount;
    appendChar(out, '\'');
    appendChar(out, *element);
    appendChar(out, '\'');
    return out -> contentCount - startSize;
}
        #include "genericVector.c"
    #undef E
#endif
