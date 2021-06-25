package cplir_c.advent_of_code_2020;

import java.lang.reflect.Array;
import java.util.Collection;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;


final class PrintableIntArrayFIFOQueue extends IntArrayFIFOQueue {

    @Override
    public String toString() {
        var sb = new StringBuilder(this.size() * IntArrayFIFOQueue.INITIAL_CAPACITY);
        if (this.isEmpty()) {
            return "[]";
        }
        sb.append('[');
        if (this.end > this.start) {
            for (var i = this.start; i <= this.end; ++i) {
                sb.append(this.array[i]);
                sb.append(", ");
            }
        } else {
            for (var i = this.start; i < this.length; ++i) {
                sb.append(this.array[i]);
                sb.append(", ");
            }
            for (var i = 0; i <= this.end; ++i) {
                sb.append(this.array[i]);
                sb.append(", ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(']');
        return sb.toString();
    }


    @Override
    public boolean isEmpty() { return this.end == this.start; }


    public Integer[] toArray() {
        var size = this.size();
        var array = new Integer[size];
        var j     = 0;
        if (this.start <= this.end) {
            for (var i = this.start; i < this.end; ++i) {
                array[j] = Integer.valueOf(this.array[i]);
                ++j;
            }
        } else {
            for (var i = this.start; i < this.length; ++i) {
                array[j] = Integer.valueOf(this.array[i]);
                ++j;
            }
            for (var i = 0; i < this.end; ++i) {
                array[j] = Integer.valueOf(this.array[i]);
                ++j;
            }
        }
        return array;
    }

    @SuppressWarnings("unchecked")

    public <T> T[] toArray(T[] a) {
        var size = this.size();
        if (a.length < size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }
        var j = 0;
        if (this.start <= this.end) {
            for (var i = this.start; i < this.end; ++i) {
                a[j] = (T) Integer.valueOf(this.array[i]);
                ++j;
            }
        } else {
            for (var i = this.start; i < this.length; ++i) {
                a[j] = (T) Integer.valueOf(this.array[i]);
                ++j;
            }
            for (var i = 0; i < this.end; ++i) {
                a[j] = (T) Integer.valueOf(this.array[i]);
                ++j;
            }
        }
        return a;
    }


    public boolean containsAll(Collection<?> c) {
        for (var v : c) {
            if (!this.contains(((Number) v).intValue())) {
                return false;
            }
        }
        return true;
    }


    public boolean contains(int intValue) {
        if (this.start < this.end) {
            for (var i = this.start; i < this.end; ++i) {
                if (this.array[i] == intValue) {
                    return true;
                }
            }
        } else {
            for (var i = this.start; i < this.length; ++i) {
                if (this.array[i] == intValue) {
                    return true;
                }
            }
            for (var i = 0; i < this.end; ++i) {
                if (this.array[i] == intValue) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addAll(Collection<? extends Integer> c) {
        for (var v : c) {
            this.enqueue(((Number) v).intValue());
        }
        return !c.isEmpty();
    }


}
