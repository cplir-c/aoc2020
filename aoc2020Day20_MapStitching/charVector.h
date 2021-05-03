
#ifndef __CHAR_VECTOR_H
    #define __CHAR_VECTOR_H 1
    #define E char
        #include "genericVector.h"
		#ifdef DEBUG
			usize printDebugcharElement(charVector* out, fu16 indentation, char* element);
		#endif
    #undef E
#endif
