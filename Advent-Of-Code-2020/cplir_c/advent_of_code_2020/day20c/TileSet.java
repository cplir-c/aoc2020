package cplir_c.advent_of_code_2020.day20c;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;


public class TileSet<R extends RegisteredTile> extends AbstractObjectSet<R> implements ObjectSet<R> {
    protected final R[]    allTiles;
    protected final BitSet           containedTiles;

    public TileSet(R[] allTiles) {
        this.allTiles = allTiles;
        for (var obj : allTiles) {
            if (obj == null) {
                throw new AssertionError();
            }
        }
        ObjectArrays.unstableSort(this.allTiles, (a, b) -> Integer.compare(a.tileID, b.tileID));

        this.containedTiles = new BitSet(allTiles.length);
        this.containedTiles.set(0, allTiles.length);
    }
    @SuppressWarnings("unchecked")
    public TileSet(Set<R> toCopy) {
        if (toCopy instanceof TileSet<R> ts) {
            this.allTiles       = ts.allTiles;
            this.containedTiles = (BitSet) ts.containedTiles.clone();
        } else {
            this.allTiles       = (R[]) toCopy.toArray(new RegisteredTile[toCopy.size()]);
            ObjectArrays.unstableSort(this.allTiles, (a, b) -> Integer.compare(a.tileID, b.tileID));
            this.containedTiles = new BitSet(this.allTiles.length);
            this.containedTiles.set(0, this.allTiles.length);
        }
    }
    protected TileSet(R[] allTiles, BitSet containedTiles) {
        this.allTiles       = allTiles;
        this.containedTiles = containedTiles;
    }


    @Override
    public boolean add(R tile) {
        var tileID = tile.tileID;
        if (tile.equals(this.allTiles[tileID])) {
            if (this.containedTiles.get(tileID)) {
                return false;
            } else {
                this.containedTiles.set(tileID);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends R> tiles) {
        if (tiles instanceof TileSet<? extends R> ts) {
            if (ts.allTiles != this.allTiles && !Arrays.equals(ts.allTiles, this.allTiles)) {
                return false;
            }
            this.containedTiles.or(ts.containedTiles);
            return true;
        } else {
            var changed = false;
            for (R tile : tiles) {
                changed |= this.add(tile);
            }
            return changed;
        }
    }

    @Override
    public void clear() {
        this.containedTiles.clear();
    }

    @Override
    public boolean contains(Object tile) {
        return tile instanceof RegisteredTile rt && this.allTiles[rt.tileID].equals(rt) && this.containedTiles.get(rt.tileID);
    }

    @Override
    public boolean containsAll(Collection<?> tiles) {
        if (tiles.size() > this.size()) {
            return false;
        }
        if (tiles instanceof TileSet<?> ts) {
            if (ts.allTiles != this.allTiles && !Arrays.equals(ts.allTiles, this.allTiles)) {
                return this.regularContainsAll(tiles);
            }
            return this.sameAllTilesContainsAll(ts);
        }
        return this.regularContainsAll(tiles);
    }
    private boolean sameAllTilesContainsAll(TileSet<? extends RegisteredTile> ts) {
        if (!ts.containedTiles.intersects(this.containedTiles)) {
            return false;
        }
        var tsSetIndex   = ts.containedTiles.nextSetBit(0);
        var tsClearIndex = ts.containedTiles.nextClearBit(tsSetIndex + 1);
        for (int setIndex = this.containedTiles.nextSetBit(0), clearIndex = this.containedTiles.nextClearBit(setIndex + 1);
             clearIndex >= 0 && tsClearIndex >= 0; setIndex = this.containedTiles.nextSetBit(clearIndex + 1), clearIndex
                 = this.containedTiles.nextSetBit(setIndex + 1)) {
            if (tsSetIndex < setIndex || tsClearIndex > clearIndex) {
                return false;
            }
            while (tsSetIndex >= setIndex && tsClearIndex <= clearIndex) {
                tsSetIndex   = ts.containedTiles.nextSetBit(tsClearIndex + 1);
                tsClearIndex = ts.containedTiles.nextClearBit(tsSetIndex + 1);
                if (tsSetIndex < 0 || tsClearIndex < 0) {
                    return true;
                }
            }
        }
        return true;
    }

    private boolean regularContainsAll(Collection<?> tiles) {
        for (Object tile : tiles) {
            if (!this.contains(tile)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean isEmpty() { return this.containedTiles.isEmpty(); }

    @Override
    public boolean remove(Object tile) {
        if (tile instanceof RegisteredTile rt && this.allTiles[rt.tileID].equals(rt)) {
            if (!this.containedTiles.get(rt.tileID)) {
                return false;
            }
            this.containedTiles.clear(rt.tileID);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> tiles) {
        if (tiles instanceof TileSet<?> ts) {
            if (ts.allTiles != this.allTiles && !Arrays.equals(ts.allTiles, this.allTiles)) {
                return this.regularRemoveAll(tiles);
            }
            return this.sameAllTilesRemoveAll(ts);
        }
        return this.regularRemoveAll(tiles);
    }
    private boolean sameAllTilesRemoveAll(TileSet<?> ts) {
        if (!ts.containedTiles.intersects(this.containedTiles)) {
            return false;
        }
        var bitIndex    = 0;
        var bitEndIndex = -1;
        var changed     = false;
        while (true) {
            bitIndex    = ts.containedTiles.nextSetBit(bitEndIndex + 1);
            bitEndIndex = ts.containedTiles.nextClearBit(bitIndex + 1);
            if (bitIndex < 0 || bitEndIndex < 0) {
                break;
            }

            var innerChanged = false;
            for (var bitI = bitIndex; bitI < bitEndIndex; ++bitI) {
                innerChanged |= this.containedTiles.get(bitI);
            }
            if (innerChanged) {
                this.containedTiles.clear(bitIndex, bitEndIndex);
                changed = true;
            }
        }
        return changed;
    }

    private boolean regularRemoveAll(Collection<?> tiles) {
        var changed = false;
        for (Object tile : tiles) {
            changed |= this.remove(tile);
        }
        return changed;
    }
    @Override
    public boolean retainAll(Collection<?> tiles) {
        if (tiles instanceof TileSet<?> ts) {
            if (ts.allTiles != this.allTiles && !Arrays.equals(ts.allTiles, this.allTiles)) {
                return this.regularRetainAll(tiles);
            }
            return this.sameAllTilesRetainAll(ts);
        }
        return this.regularRetainAll(tiles);
    }
    private boolean sameAllTilesRetainAll(TileSet<?> ts) {
        if (!ts.containedTiles.intersects(this.containedTiles)) {
            if (this.isEmpty()) {
                return false;
            }
            this.containedTiles.clear();
            return true;
        }
        var bitIndex    = 0;
        var bitEndIndex = -1;
        var changed     = false;
        while (bitIndex >= 0) {
            bitIndex    = ts.containedTiles.nextClearBit(bitEndIndex + 1);
            bitEndIndex = ts.containedTiles.nextSetBit(bitIndex + 1);
            if (bitEndIndex == -1) {
                if (bitIndex < this.containedTiles.length()) {
                    bitEndIndex = this.containedTiles.length();
                } else {
                    break;
                }
            }
            var innerChanged = false;
            for (var bitI = bitIndex; bitI < bitEndIndex; ++bitI) {
                innerChanged |= this.containedTiles.get(bitI);
            }
            if (innerChanged) {
                this.containedTiles.clear(bitIndex, bitEndIndex);
                changed = true;
            }
        }
        return changed;
    }

    private boolean regularRetainAll(Collection<?> tiles) {
        if (tiles.size() > this.size() && tiles instanceof Set<?>) {
            var changed = false;
            for (var bitIndex = this.containedTiles.nextSetBit(0); bitIndex >= 0;
                 bitIndex = this.containedTiles.nextSetBit(bitIndex + 1)) {
                var tile = this.allTiles[bitIndex];
                if (!tiles.contains(tile)) {
                    this.containedTiles.clear(bitIndex);
                    changed = true;
                }
            }
            return changed;
        } else {
            var oldContained = (BitSet) this.containedTiles.clone();
            this.clear();
            for (Object tile : tiles) {
                if (tile instanceof RegisteredTile rt && this.allTiles[rt.tileID].equals(rt) && oldContained.get(rt.tileID)) {
                    this.containedTiles.set(rt.tileID);
                }
            }
            return !oldContained.equals(this.containedTiles);
        }
    }
    @Override
    public int size() {
        return this.containedTiles.cardinality();
    }

    @Override
    public Object[] toArray() {
        var length   = this.size();
        var tiles    = new Object[length];
        var bitIndex = -1;
        for (var tilesIndex = 0; tilesIndex < length; ++tilesIndex) {
            bitIndex = this.containedTiles.nextSetBit(bitIndex + 1);
            tiles[tilesIndex] = this.allTiles[bitIndex];
        }
        return tiles;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] tiles) {
        var neededLength = this.size();
        if (tiles.length < neededLength) {
            tiles = (T[]) Array.newInstance(tiles.getClass().componentType(), neededLength);
        }
        var bitIndex = -1;
        for (var tilesIndex = 0; tilesIndex < neededLength; ++tilesIndex) {
            bitIndex = this.containedTiles.nextSetBit(bitIndex + 1);
            tiles[tilesIndex] = (T) this.allTiles[bitIndex];
        }
        return tiles;
    }

    @Override
    public ObjectIterator<R> iterator() {
        return new SkippingTileSetIterator();
    }

    private class SkippingTileSetIterator implements ObjectIterator<R> {
        int currentPosition = -1;
        int nextPosition    = this.fetchNextPosition();

        @Override
        public boolean hasNext() {
            return this.nextPosition >= 0;
        }

        private int fetchNextPosition() {
            return TileSet.this.containedTiles.nextSetBit(this.currentPosition + 1);
        }

        @Override
        public R next() {
            this.currentPosition = this.nextPosition;
            var tiles = TileSet.this.allTiles;
            if (this.currentPosition >= tiles.length) {
                throw new NoSuchElementException();
            }
            this.nextPosition = this.fetchNextPosition();
            return tiles[this.currentPosition];
        }

        private int fetchNextClear() {
            return TileSet.this.containedTiles.nextClearBit(this.currentPosition + 1);
        }
        @Override
        public int skip(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Elements to skip cannot be negative: " + n);
            }
            var skipped = 0;
            for (var nextClear = this.fetchNextClear(); nextClear >= 0; nextClear = this.fetchNextClear()) {
                // if the skip point is in this group, return the skip point
                final var skipToEstimate = this.currentPosition + n + 1;
                if (skipToEstimate < nextClear) {
                    this.currentPosition = skipToEstimate;
                    this.fetchNextPosition();
                    return skipped + n;
                }
                // skip the rest of this group
                final var toSkip = nextClear - this.currentPosition - 1;
                n                    -= toSkip;
                skipped              += toSkip;
                this.currentPosition  = TileSet.this.containedTiles.nextSetBit(nextClear + 1);
            }
            this.nextPosition    = -1;
            return skipped;
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(this.size() * 8 + 2);
        sb.append('{');
        for (var bitIndex = this.containedTiles.nextSetBit(0); bitIndex >= 0;
             bitIndex = this.containedTiles.nextSetBit(bitIndex + 1)) {
            var objString = this.allTiles[bitIndex].toString();
            sb.append(objString);
            sb.append(',');
            if (objString.contains("\n")) {
                sb.append('\n');
            }
        }
        sb.append('}');
        return sb.toString();
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof TileSet && other.getClass() == TileSet.class) {
            var ts = (TileSet<?>) other;
            return this.hashCode() == other.hashCode() && this.allTiles == ts.allTiles
                && this.containedTiles.equals(ts.containedTiles);
        } else {
            return super.equals(other);
        }
    }

    @Override
    public int hashCode() {
        return this.containedTiles.hashCode() ^ System.identityHashCode(this.allTiles);
    }
}
