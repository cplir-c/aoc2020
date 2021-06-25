package cplir_c.advent_of_code_2020;

import it.unimi.dsi.fastutil.ints.AbstractIntList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;


public class IntLinkedList extends AbstractIntList implements IntPriorityQueue {
    protected static class IntNode {
        int     value;
        IntNode prev;
        IntNode next;

        IntNode() {
            this.value = 0;
            this.prev  = this.next = this;
        }
    }

    protected int     size;
    protected IntNode head;

    public IntLinkedList() { this.size = 0; }

    @Override
    public int getInt(int index) {
        index %= this.size;
        if (index < 0) {
            throw new IndexOutOfBoundsException(index);
        }
        return this.seekRelativeTo(this.head, index).value;
    }

    protected IntNode seekRelativeTo(IntNode node, int index) {
        if (index == 0) {
            return node;
        }
        final var half = this.size >> 1;
        if (index > half) {
            for (var i = this.size; i > index; --i) {
                node = node.prev;
            }
        } else {
            for (var i = 0; i <= index; ++i) {
                node = node.next;
            }
        }
        return node;
    }
    @Override
    public CopyableIntListIterator listIterator() { return new IntLinkedIterator(this); }
    /**
     * This implementation is based on moving linked list nodes.
     */
    @Override
    public CopyableIntListIterator listIterator(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(index);
        }
        index %= this.size;
        return new IntLinkedIterator(this, this.seekRelativeTo(this.head, index));
    }

    @Override
    public int size() { return this.size; }

    @Override
    public void enqueue(int x) { this.add(x);
    }

    @Override
    public int dequeueInt() {
        if (this.head.prev == this.head) {
            var node = this.head;
            this.head = null;
            return node.value;
        } else {
            var prev = this.head.prev;
            prev.prev.next = this.head;
            this.head.prev = prev.prev;
            prev.prev      = prev.next = prev;
            return prev.value;
        }
    }

    @Override
    public int firstInt() { return this.head.prev.value;
    }

    @Override
    public IntComparator comparator() { return IntComparators.NATURAL_COMPARATOR;
    }

    public void reverse() {
        var head = this.head;
        if (head == null) {
            return;
        }
        var current = head;
        do {
            var swap = current.prev;
            current.prev = current.next;
            current      = (current.next = swap);
        } while (current != head);
    }
    @Override
    public boolean add(final int i) {
        var node = new IntNode();
        node.value = i;
        if (this.head == null) {
            this.head = node;
        } else {
            var last = this.head.prev;
            node.next      = this.head;
            node.prev      = last;
            last.next      = node;
            this.head.prev = node;
            this.head      = node;
        }
        ++this.size;
        return true;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(this.size * 20);
        sb.append('[');
        var node = this.head;
        if (node == null) {
            sb.append(']');
            return sb.toString();
        }
        for (var i = this.size; i > 0; --i) {
            sb.append('(');
            sb.append(node.prev.value);
            sb.append(' ');
            sb.append(node.value);
            sb.append(' ');
            node = node.next;
            sb.append(node.value);
            sb.append("), ");
        }
        if (this.size > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(']');
        return sb.toString();
    }
}
