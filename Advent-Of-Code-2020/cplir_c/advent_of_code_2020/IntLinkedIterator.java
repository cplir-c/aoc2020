package cplir_c.advent_of_code_2020;

import cplir_c.advent_of_code_2020.IntLinkedList.IntNode;

class IntLinkedIterator implements CopyableIntListIterator {
    IntNode       node;
    IntLinkedList self;

    public IntLinkedIterator(IntLinkedList self) {
        this.self = self;
        this.node = self.head;
    }

    public IntLinkedIterator(IntLinkedList self, IntNode node) {
        this.self = self;
        this.node = node;
    }

    @Override
    public int previousInt() {
        var value = this.node.value;
        this.node = this.node.prev;
        return value;
    }

    @Override
    public int nextInt() {
        return (this.node = this.node.next).value;
    }

    @Override
    public boolean hasNext() { return this.node != null; }

    @Override
    public boolean hasPrevious() { return this.node != null; }

    @Override
    public int nextIndex() { throw new UnsupportedOperationException(); }

    @Override
    public int previousIndex() { throw new UnsupportedOperationException(); }

    @Override
    public CopyableIntListIterator copy() { return new IntLinkedIterator(this.self, this.node); }

    @Override
    public void copyFrom(CopyableIntListIterator otherObj) {
        var other = (IntLinkedIterator) otherObj;
        this.self = other.self;
        this.node = other.node;
    }

    @Override
    public int add() {
        this.node.next.prev = this.node;
        this.node.prev.next = this.node;
        ++this.self.size;
        return this.node.value;
    }

    @Override
    public void add(int i) {
        var node = new IntNode();
        node.prev      = this.node.prev;
        node.next      = this.node;
        node.value     = i;
        this.node.prev = node;
        node.prev.next = node;
        this.node      = node;
        ++this.self.size;
    }

    @Override
    public void remove() {
        if (this.self.head == this.node) {
            this.self.head = this.node.prev;
        }
        --this.self.size;
        this.node.next.prev = this.node.prev;
        this.node.prev.next = this.node.next;
    }

    @Override
    public int add(CopyableIntListIterator iter) {
        var it     = (IntLinkedIterator) iter;
        var before = it.node.prev;
        var after  = it.node;
        before.next    = after.prev = this.node;
        this.node.prev = before;
        this.node.next = after;
        ++this.self.size;
        return this.node.value;
    }

}