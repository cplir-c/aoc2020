package cplir_c.advent_of_code_2020;

import it.unimi.dsi.fastutil.ints.IntListIterator;

public interface CopyableIntListIterator extends IntListIterator {
    /**
     * @return A new copy of this iterator.
     */
    CopyableIntListIterator copy();
    void copyFrom(CopyableIntListIterator other);
    int add();
    int add(CopyableIntListIterator insertionPosition);
}
