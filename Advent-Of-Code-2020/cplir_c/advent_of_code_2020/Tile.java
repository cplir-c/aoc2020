package cplir_c.advent_of_code_2020;

import java.util.Objects;


/**
 * edgeOrientation bit 0: counterclockwise? <br>
 * 0-1: right <br>
 * 2-3: top <br>
 * 4-5: left <br>
 * 6-7: bottom <br>
 * orientations bit index bit 0: up/down reflection? <br>
 * bit index bit 1: left/right reflection? <br>
 * bit index bit 2: y=x reflection? <br>
 * example rotation equivalence using quadrants:<br>
 * it starts as:<br>
 * 2 1<br>
 * 3 4<br>
 * <br>
 * but we want:<br>
 * 1 4<br>
 * 2 3<br>
 * <br>
 * start by reflecting over y=x:<br>
 * 4 1<br>
 * 3 2<br>
 * <br>
 * then reflect left/right:<br>
 * 1 4<br>
 * 2 3<br>
 */
public class Tile implements Cloneable {
    static final class Orientation {
        static final byte NORMAL                    = 0b000;
        static final byte UPSIDE_DOWN_AND_BACKWARDS = 0b001;
        static final byte BACKWARDS                 = 0b010;
        static final byte UPSIDE_DOWN               = 0b011;
        static final byte RIGHT_BACKWARDS           = 0b100;
        static final byte RIGHT                     = 0b101;
        static final byte LEFT                      = 0b110;
        static final byte LEFT_BACKWARDS            = 0b111;
        static final String[] ORIENTATIONS              = new String[LEFT_BACKWARDS + 1];
    }
    static {
        var orientationFields     = Orientation.class.getDeclaredFields();
        for (var i = orientationFields.length - 1; i >= 0; --i) {
            var field = orientationFields[i];
            {
                var name = field.getName();
                int value;
                try {
                    var obj = field.get(null);
                    if (obj instanceof Number) {
                        value = ((Number) obj).intValue();
                    } else {
                        continue;
                    }
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
                Orientation.ORIENTATIONS[value] = name.toLowerCase().replace('_', ' ');
            }
        }
    }
    // clockwise bit order
    short        edge1; // right
    short        edge2; // top
    short        edge3; // left
    short        edge4; // bottom
    int          hash;
    short        id;
    private byte orientation;

    private Tile() {}
    protected Tile(Tile tile) {
        this.edge1       = tile.edge1;
        this.edge2       = tile.edge2;
        this.edge3       = tile.edge3;
        this.edge4       = tile.edge4;
        this.hash        = tile.hash;
        this.id          = tile.id;
        this.orientation = tile.orientation;
    }
    static final short reverseShort(short s) { return (short) (Integer.reverse(s) >>> 22); }
    public static Tile fromStringArray(String[] tileLines, int start, int end, short id) {
        if (end - start != 10) {
            throw new AssertionError(end - start);
        }

        var tile  = new Tile();
        var lines = new short[10];
        for (var i = start; i < end; ++i) {
            var lineString = tileLines[i];
            var line       = Short.parseShort(lineString, 2);
            lines[i - start] = line;
        }
        tile.edge2 = reverseShort(lines[0]);
        tile.edge4 = lines[9];
        short left  = 0; // high bit is uppermost bit
        short right = 0; // low bit is uppermost bit
        for (var i = 9; i >= 0; --i) {
            var line = lines[i];
            left  |= ((line & (1 << 9)) >> i);
            right |= ((line & 1) << i);
        }
        tile.edge1 = right;
        tile.edge3 = left;
        tile.canonicalizeHash();
        tile.id = id;
        return tile;
    }
    @Override
    public int hashCode() { return this.hash; }
    private void canonicalizeHash() {
        var hash    = this.edge1 ^ this.edge2 ^ this.edge3 ^ this.edge4;
        var hash2   = this.edge1 ^ reverseShort(this.edge2) ^ this.edge3 ^ reverseShort(this.edge4);
        var hash3   = reverseShort(this.edge1) ^ this.edge2 ^ reverseShort(this.edge3) ^ this.edge4;
        int hash4   = reverseShort((short) hash);
        var minHash = Math.min(Math.min(hash, hash2), Math.min(hash3, hash4));
        if (minHash == hash) {
            // no need to flip
        } else if (minHash == hash2) {
            this.flipLR();
        } else if (minHash == hash3) {
            this.flipUD();
        } else if (minHash == hash4) {
            this.flipXY();
        }
        this.orientation = 0;
        hash             = minHash;
    }
    void flipXY() { // y=x flip
        this.orientation ^= 0b100;
        var swap = reverseShort(this.edge1);
        this.edge1  = reverseShort(this.edge2);
        this.edge2  = swap;
        swap        = reverseShort(this.edge4);
        this.edge4  = reverseShort(this.edge3);
        this.edge3  = swap;
        this.hash  ^= 0b100 << 10;
    }
    void flipUD() { // U/D flip
        this.orientation ^= 0b10;
        var swap = reverseShort(this.edge4);
        this.edge4  = reverseShort(this.edge2);
        this.edge2  = swap;
        this.edge1  = reverseShort(this.edge1);
        this.edge3  = reverseShort(this.edge3);
        this.hash  ^= 0b10 << 10;
    }
    void flipLR() { // L/R flip
        this.orientation ^= 1;
        var swap = reverseShort(this.edge1);
        this.edge1  = reverseShort(this.edge3);
        this.edge3  = swap;
        this.edge2  = reverseShort(this.edge2);
        this.edge4  = reverseShort(this.edge4);
        this.hash  ^= 1 << 10;
    }
    void rotateClockwise() {
        this.flipXY();
        this.flipLR();
    }
    /**
     * orientation bit 0: up/down reflection? <br>
     * bit 1: left/right reflection? <br>
     * bit 2: y=x reflection? <br>
     */
    public byte getOrientation() { return this.orientation; }

    public short getEdge1() { return this.edge1; }
    public short getEdge2() { return this.edge2; }
    public short getEdge3() { return this.edge3; }
    public short getEdge4() { return this.edge4; }
    public short getId() { return this.id; }
    @Override
    public boolean equals(Object otherObj) {
        if (this.hash == Objects.hashCode(otherObj) && otherObj instanceof Tile) {
            var other = (Tile) otherObj;
            if (other.edge1 == this.edge1 && other.edge2 == this.edge2 && other.edge3 == this.edge3 && other.edge4 == this.edge4
                && this.id == other.id
            /*
             * || other.edge2 == this.edge1 && other.edge3 == this.edge2 && other.edge4 == this.edge3
             * && other.edge1 == this.edge4
             * || other.edge3 == this.edge1 && other.edge4 == this.edge2 && other.edge1 == this.edge3
             * && other.edge2 == this.edge4
             * || other.edge4 == this.edge1 && other.edge1 == this.edge2 && other.edge2 == this.edge3
             * && other.edge3 == this.edge4
             */) {
                return true;
            } /*
               * var rev1 = reverseShort(this.edge1);
               * var rev2 = reverseShort(this.edge2);
               * var rev3 = reverseShort(this.edge3);
               * var rev4 = reverseShort(this.edge4);
               * // the hash canonicalization should make this unnecessary, right?
               * if (other.edge1 == rev3 && other.edge2 == rev2 && other.edge3 == rev1 && other.edge4 == rev4
               * || other.edge2 == rev3 && other.edge3 == rev2 && other.edge4 == rev1 && other.edge1 == rev4
               * || other.edge3 == rev3 && other.edge4 == rev2 && other.edge1 == rev1 && other.edge2 == rev4
               * || other.edge4 == rev3 && other.edge1 == rev2 && other.edge2 == rev1 && other.edge3 == rev4
               * || other.edge1 == rev1 && other.edge2 == rev4 && other.edge3 == rev3 && other.edge4 == rev2
               * || other.edge2 == rev1 && other.edge3 == rev4 && other.edge4 == rev3 && other.edge1 == rev2
               * || other.edge3 == rev1 && other.edge4 == rev4 && other.edge1 == rev3 && other.edge2 == rev2
               * || other.edge4 == rev1 && other.edge1 == rev4 && other.edge2 == rev3 && other.edge3 == rev2
               * || other.edge1 == rev3 && other.edge2 == rev4 && other.edge3 == rev1 && other.edge4 == rev2
               * || other.edge2 == rev3 && other.edge3 == rev4 && other.edge4 == rev1 && other.edge1 == rev2
               * || other.edge3 == rev3 && other.edge4 == rev4 && other.edge1 == rev1 && other.edge2 == rev2
               * || other.edge4 == rev3 && other.edge1 == rev4 && other.edge2 == rev1 && other.edge3 == rev2) {
               * throw new AssertionError("I thought the hashcode was better than this.");
               * }
               */
        }
        return false;
    }

    static boolean pretty = false;

    static CharSequence showEdge(short edge) {
        if (pretty) {
            var sb = new StringBuilder(Integer.toBinaryString(edge));
            for (var i = sb.length() - 1; i >= 0; --i) {
                var c = sb.charAt(i);
                if (c == '1') {
                    sb.setCharAt(i, '#');
                } else if (c == '0') {
                    sb.setCharAt(i, '.');
                } else {
                    // the character is fine how it is, so leave it alone
                }
            }
            while (sb.length() < 10) {
                sb.insert(0, '.');
            }
            return sb;
        } else {
            return Integer.toString(edge);
        }
    }
    @Override
    public String toString() {
        var sb = new StringBuilder(24);
        sb.append("Tile{edges=[");
        sb.append(showEdge(this.edge1));
        sb.append(',');
        sb.append(showEdge(this.edge2));
        sb.append(',');
        sb.append(showEdge(this.edge3));
        sb.append(',');
        sb.append(showEdge(this.edge4));
        sb.append("], hash=");
        sb.append(Integer.toHexString(this.hash));
        sb.append(", oriented:");
        sb.append(Orientation.ORIENTATIONS[this.orientation]);
        sb.append('}');
        return sb.toString();
    }
    @Override
    public Tile clone() {
        try {
            return (Tile) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Tile(this);
        }
    }
}
