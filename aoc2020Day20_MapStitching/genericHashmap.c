
#include <math.h>
#include <stdlib.h>
#include <stdio.h>

#include "genericHashmap.h"

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

    #ifdef KEY_DESTRUCTOR
        #ifndef BUCKET_DESTRUCTION
            #define BUCKET_DESTRUCTION true
        #endif
    #else
        #ifdef VALUE_DESTRUCTOR
            #ifndef BUCKET_DESTRUCTION
                #define BUCKET_DESTRUCTION true
            #endif
        #endif
    #endif

    #ifdef DEBUG
        #include "charVectorUtils.c"
    #endif

_Bool hashMapMethod(construct1) (HashMap* map) {
    return hashMapMethod(construct3) (map, 0.5, 5);
}

_Bool hashMapMethod(construct2) (HashMap* map, usize requiredSpots) {
    return hashMapMethod(construct3) (map, 0.5, requiredSpots);
}

_Bool hashMapMethod(construct3) (HashMap* map, double loadFactor, usize requiredSpots) {
    if (requiredSpots == 0) {
        requiredSpots = 5;
    }
    usize minSize = nextHigherPowerOfTwo((double)requiredSpots / loadFactor);
    map -> loadFactor = loadFactor;
    map -> shrinkFillLevel = 0;
    map -> minimumSize = (map -> size = minSize);
    Bucket* contents = aligned_alloc(_Alignof(Bucket), minSize * sizeof(Bucket));
    if (contents == NULL) {
        contents = calloc(minSize, sizeof(Bucket));
        if (contents == NULL) {
            fprintf(stderr, "unable to allocate %lu buckets, or %lu bytes", minSize, minSize * sizeof(Bucket));
            return false;
        }
    } else {
        // initialize contents;
        Bucket* noBucket = contents + minSize;
        for (Bucket* bucket = contents; bucket < noBucket; ++bucket) {
            bucket -> hash = SIZE_MAX;
        }
    }
    map -> contents = contents;
    map -> contentCount = 0;
    // implicit truncation / flooring on the next line
    map -> growFillLevel = (usize) (loadFactor * minSize);
    map -> bucketMask = minSize - 1;
    map -> sizeTwoPower = (sizeof(usize) * 8) - __builtin_clzl(minSize);
    return true;
}

_Bool hashMapMethod(destruct) (HashMap* map) {
#ifdef BUCKET_DESTRUCTION
    usize size = map -> size;
    usize contentCount = map -> contentCount;
    Bucket* contents = map -> contents;
    for (Bucket* targetBucket = contents + (size - 1); targetBucket >= contents && contentCount > 0; --targetBucket) {
        if (targetBucket -> hash != SIZE_MAX) {
#ifdef KEY_DESTRUCTOR
            KEY_DESTRUCTOR(&(targetBucket -> key));
#endif
#ifdef VALUE_DESTRUCTOR
            VALUE_DESTRUCTOR(&(targetBucket -> value));
#endif
            --contentCount;
        }
    }
    if (contentCount > 0) {
        fprintf(stderr, "failed to destroy all buckets, missed %lu of them, freeing anyway", contentCount);
        free(map -> contents);
        return false;
    }
#endif
    free(map -> contents);
    return true;
}

_Bool hashMapMethod(__growContents) (HashMap* map) {
    // expand the map's fill levels
    map -> growFillLevel <<= 1;
    map -> shrinkFillLevel <<= 1;
    // record the old size and expand the map's size
    usize oldSize = map -> size;
    usize size = (map -> size <<= 1);
    // expand the bucket mask
    usize bucketMask = map -> bucketMask;
    bucketMask <<= 1;
    bucketMask |= 1;
    map -> bucketMask = bucketMask;
    // access the contents pointer
    Bucket* contents = map -> contents;
    // try to expand the contents array
    contents = realloc(contents, size * sizeof(Bucket));
    if (contents == NULL) {
        // if it failed, revert the expanded fill levels, mask, and size
        map -> size = oldSize;
        (map -> bucketMask) >>= 1;
        map -> growFillLevel >>= 1;
        map -> shrinkFillLevel >>= 1;
        fprintf(stderr, "failed to realloc resized hash buckets");
        return false;
    }
    // if it succeeded, save the new contents pointer to the map
    map -> contents = contents;
    // initialize the newly allocated buckets to empty
    Bucket* targetBucket = contents + oldSize;
    for (usize i = oldSize; i < size; ++i, ++targetBucket) {
        targetBucket -> hash = SIZE_MAX;
    }
    // rehash the buckets
    targetBucket = contents;
    for (usize i = 0; i < oldSize; ++targetBucket, ++i) {
        usize bucketHash = targetBucket -> hash;
        if ((bucketHash & bucketMask) != i) {
            // copy the bucket to the stack
            K key = targetBucket -> key;
            V value = targetBucket -> value;
            // delete the bucket item
            targetBucket -> hash = SIZE_MAX;
            --(map -> contentCount);
            // re-add the bucket
            if (!hashMapMethod(__addItem)(map, &key, &value, bucketHash)) {
                fprintf(stderr, "rehashing bucket index %ld failed during expansion", i);
                targetBucket -> hash = bucketHash;
                ++(map -> contentCount);
                return false;
            }
        }
    }
    return true;
}

_Bool hashMapMethod(__shrinkContents) (HashMap* map) {
    // shrink fill levels
    map -> growFillLevel >>= 1;
    map -> shrinkFillLevel >>= 1;
    // record the old size, and shrink the map size
    usize oldSize = map -> size;
    map -> size >>= 1;
    // record the old mask and shrink the map's mask
    usize oldBucketMask = map -> bucketMask;
    usize bucketMask = ((map -> bucketMask) = (oldBucketMask >> 1));

    // access the contents pointer
    Bucket* contents = map -> contents;
    // rehash the buckets
    Bucket* targetBucket = contents;
    for (usize i = 0; i < oldSize; ++targetBucket, ++i) {
        usize bucketHash = targetBucket -> hash;
        if ((bucketHash & bucketMask) != i) {
            // copy the bucket to the stack
            K key = targetBucket -> key;
            V value = targetBucket -> value;
            // delete the bucket item
            targetBucket -> hash = SIZE_MAX;
            --(map -> contentCount);
            // re-add the bucket
            if (!hashMapMethod(__addItem)(map, &key, &value, bucketHash)) {
                fprintf(stderr, "failed to rehash bucket index %ld during shrinking", i);
                targetBucket -> hash = bucketHash;
                ++(map -> contentCount);
            }
        }
    }
    // shrink the buffer
    contents = realloc(contents, (map -> size >>= 1) * sizeof(Bucket));
    if (contents == NULL) {
        map -> size = oldSize;
        fprintf(stderr, "failed to realloc shrunken hash buckets");
        return false;
    } else {
        map -> contents = contents;
        return true;
    }
}

Bucket* hashMapMethod(addItem) (HashMap* map, K* key, V* value) {
    register usize keyHash = hashKey(key);
    if (keyHash == SIZE_MAX) { --keyHash; }
    return hashMapMethod(__addItem)(map, key, value, keyHash);
}

Bucket* hashMapMethod(__addItem) (HashMap* map, K* key, V* value, usize keyHash) {
    usize bucketMask = map -> bucketMask;
    usize spreadKeyHash = spreadBits(keyHash, map -> sizeTwoPower);
    Bucket* contents = map -> contents;
    usize index = spreadKeyHash;// & bucketMask;
    Bucket* pointed = contents + index;
// don't check for hitting the insertion point again because the hashmap should resize before filling up completely
// instead, check if the pointed bucket is empty
    while (pointed -> hash != SIZE_MAX) {
        if (pointed -> hash == keyHash && pointed -> key == *key) {
            // found a bucket with the same key
    #ifdef VALUE_DESTRUCTOR
            VALUE_DESTRUCTOR(&(pointed -> value));
    #endif
            pointed -> value = *value;
            return pointed;
        }
        ++index;
        index &= bucketMask;
        pointed = contents + index;
    }
    // if we got here pointed points to an empty bucket, so fill it
    pointed -> hash = keyHash;
    pointed -> key = *key;
    pointed -> value = *value;
    // if the map is too full and resizing fails, log and return the bucket pointer
    if (++(map -> contentCount) >= map -> growFillLevel && !(hashMapMethod(__growContents) (map))) {
        if (map -> contentCount < map -> size) {
            fprintf(stderr, "Failed to resize hashmap %p contents of %lu buckets with %lu full, probably due to memory pressure.", (void*) map, map -> size, map -> contentCount);
            // fallthrough to return pointed bucket
        } else {
            // hashmap is too full, empty the bucket and return null
            pointed -> hash = 0;
            -- (map -> contentCount);
            return NULL;
        }
    }
    return pointed;
}

_Bool hashMapMethod(removeItem) (HashMap* map, K* key) {
    register usize keyHash = hashKey(key);
    if (keyHash == SIZE_MAX) { --keyHash; }
    return hashMapMethod(__removeItem) (map, key, keyHash);
}

    #ifndef SMART_CLUMP_DELETION
        #define SMART_CLUMP_DELETION 1
    #endif

_Bool hashMapMethod(__removeItem) (HashMap* map, K* key, usize keyHash) {
    usize bucketMask = map -> bucketMask;
    usize spreadKeyHash = spreadBits(keyHash, map -> sizeTwoPower);
    Bucket* contents = map -> contents;
    usize index = spreadKeyHash;// & bucketMask;
    #if SMART_CLUMP_DELETION
    usize startIndex = index;
    #endif
    Bucket* pointed = contents + index;
    while (pointed -> hash != SIZE_MAX) {
        if (pointed -> hash == keyHash && pointed -> key == *key) {
            // found a bucket with the same key
            break;
        }
        ++index;
        index &= bucketMask;
        pointed = contents + index;
    }
    if (pointed -> hash == SIZE_MAX) {
        return 0;
    }
    // found a bucket with the same key
    pointed -> hash = SIZE_MAX;
    usize targetIndex = index;
    Bucket* targetBucket = pointed;
    if (--(map -> contentCount) >= (map -> shrinkFillLevel)) {
        // don't shrink
        ++index;
        index &= bucketMask;
        pointed = contents + index;
        if (pointed -> hash != SIZE_MAX) {
    #if SMART_CLUMP_DELETION
            // find the first bucket of this clump of full buckets
            do {
                startIndex += bucketMask; // go backwards one
                startIndex &= bucketMask;
            } while (pointed -> hash != SIZE_MAX);

            ++startIndex;
            startIndex &= bucketMask;
            // startIndex is now the index of the first filled bucket of the clump
    #endif
            fu8 sizeTwoPower = map -> sizeTwoPower;
            do { // } while (pointed -> hash != SIZE_MAX)
                keyHash = pointed -> hash;
                usize preferredIndex = spreadBits(keyHash, sizeTwoPower);
                if (preferredIndex == index) {
                    // skip this bucket that likes being where it is
                    // fall through to the loop increment
    #if SMART_CLUMP_DELETION
                } else if ((startIndex <= targetIndex // contiguous clump case
                        && (preferredIndex <= targetIndex && preferredIndex >= startIndex))
                    // wrapped clump case
                    || (preferredIndex <= targetIndex && preferredIndex >= startIndex)) {
                    // if the bucket lands in this clump, move it to the target bucket
                    *targetBucket = *pointed;
                    targetIndex = index;
                    targetBucket = pointed;
                    // set the bucket to empty
                    pointed -> hash = SIZE_MAX;
    #endif
                } else {
                    // otherwise the bucket shouldn't be here, so rehash the bucket the hard way

                    // pretend to delete the bucket item
                    pointed -> hash = SIZE_MAX;
                    --(map -> contentCount);
                    // this is sufficient because we already checked to see if the bucket belongs where the target bucket is,
                    // and are in the process of rehashing from a deletion, so no need to start another one
                    // otherwise, the add item could try to copy the pointed struct to itself if it was in its favorite spot,
    #if SMART_CLUMP_DELETION
                    // or scanning the clump could cause quadratic slowdown for each rehashed item
    #endif
                    // re-add the bucket
                    if (hashMapMethod(__addItem)(map, &(pointed -> key), &(pointed -> value), keyHash) == NULL) {
                        fprintf(stderr, "__removeItem failed to allocate space for the bucket it just removed, what?");
                        return false;
                    }
                }
                ++index;
                index &= bucketMask;
                pointed = contents + index;
            } while (pointed -> hash != SIZE_MAX);
        }
    } else {
        // the map should shrink, so pointless to rehash the deleted clump end separately
        return hashMapMethod(__shrinkContents) (map);
    }
    return 1;
}

#undef SMART_CLUMP_DELETION

Bucket* hashMapMethod(getItem) (HashMap* map, K* key) {
    // FIXME
    usize keyHash = spreadBits(hashKey(key), map -> sizeTwoPower);
    usize bucketMask = map -> bucketMask;
    Bucket* contents = map -> contents;
    usize index = keyHash & bucketMask;
    Bucket* pointed = contents + index;
// don't check for hitting the insertion point again because the hashmap should resize before filling up completely
// instead, check if the pointed bucket is empty
    while (pointed -> hash != SIZE_MAX) {
        if (pointed -> hash == keyHash && pointed -> key == *key) {
            // found a bucket with the same key
            return pointed;
        }
        ++index;
        index &= bucketMask;
        pointed = contents + index;
    }
    return NULL;
}

    #ifndef nextHigherPowerOfTwo_
    #define nextHigherPowerOfTwo_
static usize nextHigherPowerOfTwo(double load) {
    load = ceil(load);
    usize intLoad = (usize) load;
    // number of bits to enumerate intLoad
    unsigned char shift = (sizeof(usize) * 8) - __builtin_clzl(intLoad);
    usize twoLoad = (((usize)1) << shift);
    twoLoad <<= ((_Bool)(twoLoad < intLoad));
    return twoLoad;
}

static usize spreadBits(usize in, fu8 twoPower) {
    in ^= in >> ((sizeof(usize) * 8) - twoPower);
    // the long constant here is a decimal expansion of the golden ratio
    in = (((usize) 11400714819323198485u) * in) >> ((sizeof(usize) * 8) - twoPower);
    return in;
}
    #endif

    #ifdef DEBUG

        #define _stringify(name) #name
        #define stringify(name) _stringify(name)
        #define KToVString stringify(KToV)
        #define BucketString stringify(Bucket)
        #define HashMapString stringify(HashMap)
        #define KString stringify(K)
        #define VString stringify(V)

        #define printDebugKey TokenPaste(printDebug, KKey)

        #define VValue TokenPaste(V, Value)
        #define printDebugValue TokenPaste(printDebug, VValue)

extern usize printDebugKey(charVector* out, fu16 indentation, K* key);
extern usize printDebugValue(charVector* out, fu16 indentation, V* value);

static usize hashMapMethod(printDebug)(HashMap* map, fu16 indentation, charVector* out) {
    usize hashMapMethod(printDebugHeader) () {
        usize beginIndex = out -> contentCount;
        appendLine(out, indentation, "%zn(" HashMapString ") {");
        appendLine(out, indentation + 4, ".loadFactor = %f,");
        appendLine(out, indentation + 4, ".growFillLevel = %zu,");
        appendLine(out, indentation + 4, ".shinkFillLevel = %zu,");
        appendLine(out, indentation + 4, ".contentCount = %zu,");
        appendLine(out, indentation + 4, ".size = %lu,");
        appendLine(out, indentation + 4, ".bucketMask = 0x%zx,");
        appendLine(out, indentation + 4, ".minimumSize = %zu,");
        appendLine(out, indentation + 4, ".sizeTwoPower = %hhu,");
        appendChar(out, '\0');
        usize formatSize = (out -> contentCount) - beginIndex;
        char* destBlock = addBlockcharVector(out, 6 * formatSize);
        // copy the format string to the end of the char vector
        char* formatBlock = memcpy(destBlock + formatSize * 5, (out -> contents) + beginIndex, formatSize);
        destBlock = (out -> contents) + beginIndex;
        // format the hashmap header into place
        usize written = 0;
        sprintf(destBlock, formatBlock, &written,
            map -> loadFactor,
            map -> growFillLevel,
            map -> shrinkFillLevel,
            map -> contentCount,
            map -> size,
            map -> bucketMask,
            map -> minimumSize,
            map -> sizeTwoPower);
        // remove the unused space
        removeBlockcharVector(out, 7 * formatSize - written);
        return written;
    }
    hashMapMethod(printDebugHeader)();

    usize hashMapMethod(printDebugContentsArray)() {
        usize contentCount = map -> contentCount;
        appendLie(out, indentation + 4, ".contents = (" BucketString "*) (" BucketString "[");
        appendSizeT(out, contentCount);
        appendNullString(out, "]) {\n");

        for (usize i = 0; i < contentCount; ++i){
            appendLie(out, indentation + 8, "[");
            appendSizeT(out, i);
            appendNullString(out, "] = (" BucketString ") ");

            Bucket* bucket = (map -> contents) + i;
            if (bucket -> hash == SIZE_MAX) {
                appendNullString(out, "EMPTY_BUCKET,\n");
            } else {
                appendNullString(out, "{\n");

                appendLie(out, indentation + 12, ".hash = ");
                appendSizeT(out, bucket -> hash);
                appendNullString(out, ",\n");

                appendLie(out, indentation + 12, ".key = (" KString ") ");
                printDebugKey(out, indentation + 12, &(bucket -> key));
                appendNullString(out, ",\n");

                appendLie(out, indentation + 12, ".value = (" VString ") ");
                printDebugValue(out, indentation + 12, &(bucket -> value));
                appendNullString(out, ",\n");

                appendLine(out, indentation + 8, "},");
            }
        }

        appendLine(out, indentation + 4, "}");
    }
    hashMapMethod(printDebugContentsArray)();
    appendLine(out, indentation, "};");
}

        #undef printDebugValue
        #undef VValue
        #undef printDebugKey

        #undef VString
        #undef KString
        #undef HashMapString
        #undef BucketString
        #undef KToVString
        #undef stringify
        #undef stringify_

    #endif

    #undef _TokenPaste
    #    undef TokenPaste
    //   undef K
    //   undef V
    #    undef KTo
    #    undef KToV
    #    undef HashMap
    #    undef hashMapMethod

    #undef Bucket

    #undef KKey
    #undef hashKey

    #ifdef BUCKET_DESTRUCTION
        #undef BUCKET_DESTRUCTION
    #endif
#endif

