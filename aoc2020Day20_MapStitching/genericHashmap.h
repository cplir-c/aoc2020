
#include "shortIntNames.h"

#ifdef K
    #define _TokenPaste(pre, post) pre ## post
    #define TokenPaste(pre, post) _TokenPaste(pre, post)
    #define KTo TokenPaste(K, To)
    #define KToV TokenPaste(KTo, V)
    #define HashMap TokenPaste(KToV, OpenHashMap)
    #define hashMapMethod(name) TokenPaste(name, HashMap)

    #define Bucket TokenPaste(KToV, HashBucket)

    #define KKey TokenPaste(K, Key)
    #define hashKey TokenPaste(hash, KKey)
extern usize hashKey(K* key);

typedef struct {
    usize hash;
    K key;
    V value;
} Bucket;

typedef struct {
    double loadFactor;
    usize growFillLevel;
    usize shrinkFillLevel;
    usize contentCount;
    usize size;
    usize bucketMask;
    usize minimumSize;
    Bucket* contents;
} HashMap;

_Bool hashMapMethod(construct) (HashMap* map);
_Bool hashMapMethod(construct2) (HashMap* map, usize requiredSpots);
_Bool hashMapMethod(construct3) (HashMap* map, double loadFactor, usize requiredSpots);

_Bool hashMapMethod(destruct) (HashMap* map);

_Bool hashMapMethod(__growContents) (HashMap* map);
_Bool hashMapMethod(__shrinkContents) (HashMap* map);

Bucket* hashMapMethod(addItem) (HashMap* map, K* key, V* value);
Bucket* hashMapMethod(__addItem) (HashMap* map, K* key, V* value, usize keyHash);
_Bool hashMapMethod(removeItem) (HashMap* map, K* key);
_Bool hashMapMethod(__removeItem) (HashMap* map, K* key, usize keyHash);
Bucket* hashMapMethod(getItem) (HashMap* map, K* key);

usize nextHigherPowerOfTwo(double load);
usize spreadBits(usize in);

    #undef _TokenPaste
#    undef TokenPaste
//   undef K
//   undef V
#    undef KTo
#    undef KToV
#    undef HashMap
#    undef hashMapMethod

    #undef Bucket

#    undef KKey
#    undef hashKey

    #ifdef BUCKET_DESTRUCTION
        #undef BUCKET_DESTRUCTION
    #endif

#endif
