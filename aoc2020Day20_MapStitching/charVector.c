
#include "shortIntNames.h"

#ifndef __CHAR_VECTOR_C
    #define __CHAR_VECTOR_C 1
    #define E char
        #include "genericVector.h"
        #include "charVectorUtils.c"
usize printDebugcharElement(charVector* out, fu16 indentation, char* element) {
    appendChar(out, '\'');
    appendChar(out, *element);
    appendChar(out, '\'');
    return 3;
}
        #include "genericVector.c"
    #undef E
#endif
