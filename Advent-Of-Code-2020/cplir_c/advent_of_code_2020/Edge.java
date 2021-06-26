package cplir_c.advent_of_code_2020;

import cplir_c.advent_of_code_2020.Edge.EdgeOrientation;

public final class Edge {
    final String edgeStr;
    final short  edgeBits;
    final byte   edgeOrientation;
    /**
     * These objects are lazily initialized.
     */
    /***/
    Edge         reversed;
    Edge         translated;
    Edge         rotated;

    public Edge(String edgeStr, byte edgeOrientation) {
        this.edgeStr = edgeStr;
        this.edgeBits        = Short.parseShort(edgeStr.replace('#', '1').replace('.', '0'), 2);
        this.edgeOrientation = edgeOrientation;
        // The first point when the references are
        this.reversed        = this.translated = this.rotated = null;
    }
    public Edge(short edge, byte edgeOrientation) {
        this.edgeBits = edge;
        var edgeStringBuilder = new StringBuilder(Integer.toBinaryString(edge).replace('0', '.').replace('1', '#'));
        if (edgeStringBuilder.length() < 10) {
            edgeStringBuilder.reverse();
            while (edgeStringBuilder.length() < 10) {
                edgeStringBuilder.append('.');
            }
            edgeStringBuilder.reverse();
        }
        this.edgeStr = edgeStringBuilder.toString();
        if (Integer.numberOfLeadingZeros(edge) < (Integer.SIZE - 10)) {
            throw new AssertionError(this.edgeStr);
        }
        this.edgeOrientation = edgeOrientation;
        // The first point when the references are
        this.reversed        = this.translated = this.rotated = null;
    }
    /// Unsafe
    protected Edge(String edgeStr, short edge, byte edgeOrientation, Edge reversed, Edge translated, Edge rotated) {
        this.edgeStr         = edgeStr;
        this.edgeBits        = edge;
        this.edgeOrientation = edgeOrientation;
        this.reversed        = reversed;
        this.translated      = translated;
        this.rotated         = rotated;
    }

    static final class EdgeOrientation {
        private EdgeOrientation() {
            throw new UnsupportedOperationException();
        }
        static final byte     CLOCKWISE_RIGHT        = 0;
        static final byte     COUNTERCLOCKWISE_RIGHT = 1;
        static final byte     CLOCKWISE_UP           = 2;
        static final byte     COUNTERCLOCKWISE_UP    = 3;
        static final byte     CLOCKWISE_LEFT         = 4;
        static final byte     COUNTERCLOCKWISE_LEFT  = 5;
        static final byte     CLOCKWISE_DOWN         = 6;
        static final byte     COUNTERCLOCKWISE_DOWN  = 7;
        static final String[] EDGE_ORIENTATIONS      = new String[COUNTERCLOCKWISE_DOWN + 1];
    }

    static {
        var edgeOrientationFields = Edge.EdgeOrientation.class.getDeclaredFields();
        for (var i = edgeOrientationFields.length - 1; i >= 0; --i) {
            var field = edgeOrientationFields[i];
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
                Edge.EdgeOrientation.EDGE_ORIENTATIONS[value] = name.toLowerCase().replace('_', ' ');
            }
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(20);
        sb.append('[');
        sb.append(EdgeOrientation.EDGE_ORIENTATIONS[this.edgeOrientation]);
        sb.append(' ');
        sb.append(this.edgeStr);
        sb.append("]\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof Edge) {
            var other = (Edge) otherObj;
            return other.edgeOrientation == this.edgeOrientation && other.edgeBits == this.edgeBits;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.edgeBits << 16) | (this.edgeOrientation & 0xff);
    }

    public Edge reverse() {
        if (this.reversed == null) {
            getReversed();
        }
        return this.reversed;
    }
    private void getReversed() {
        String reversedStr = null;
        byte reversedEdge;
        {
            Edge reversedObj;
            if (this.translated != null && this.translated.reversed != null) {
                reversedObj = this.translated.reversed;
            } else if (this.translated != null && this.translated.rotated != null && this.translated.rotated.reversed != null) {
                reversedObj = this.translated.rotated.reversed;
            } else if (this.rotated != null && this.rotated.reversed != null) {
                reversedObj = this.rotated.reversed;
            } else if (this.rotated != null && this.rotated.translated != null && this.rotated.translated.reversed != null) {
                reversedObj = this.rotated.translated.reversed;
            } else {
                reversedObj = null;
            }
            if (reversedObj != null) {
                reversedStr = reversedObj.edgeStr;
                reversedEdge = reversedObj.edgeBits;
            } else {
                var reversedSb = new StringBuilder(this.edgeStr);
                reversedSb.reverse();
                reversedStr  = reversedSb.toString();
                reversedEdge = (short) (Integer.reverse(this.edgeBits) >>> (Integer.SIZE - 10));
            }
        }
        var reversedDirection = this.edgeOrientation;
        reversedDirection ^= 1;
        this.reversed      = new Edge(reversedStr, reversedEdge, reversedDirection, this, null, null);
        this.distributeReversed();
    }
    private void distributeReversed() {
        // set references to and from this.reversed
        // there should be one for translated and one for rotated
        // try to find rotated source
        do {
            // (node + 4) ^ 1
            if (this.translated != null) {
                // what am I thinking, I need to get (node + 6) ^ 1 not node + 6
                // (node + 6) ^ 1 and node + 6
                if (this.translated.rotated != null && this.translated.rotated.reversed != null) {
                    this.translated.rotated.reversed.rotated = this.reversed;
                    break;
                    // node + 4 and node + 6
                } else if (this.translated.reversed != null && this.translated.reversed.rotated != null) {
                    this.translated.reversed.rotated.rotated = this.reversed;
                    break;
                } else { /* intentionally empty */ }
            }
            // node + 2
            if (this.rotated != null) {
                // (node + 6) ^ 1 and node + 6
                if (this.rotated.translated != null && this.rotated.translated.reversed != null) {
                    this.rotated.translated.reversed.rotated = this.reversed;
                    // (node + 2) ^ 1 and node + 6
                } else if (this.rotated.reversed != null && this.rotated.reversed.translated != null) {
                    this.rotated.reversed.translated.rotated = this.reversed;
                } else { /* intentionally empty */ }
            }
        } while (false);
        // try to find the translated source
        {
            Edge translationSource;
            // (node + 4) ^ 1 and node + 4
            if (this.translated != null && this.translated.reversed != null) {
                translationSource = this.translated.reversed;
            // node + 2 and node + 4
            } else if (this.rotated != null && this.rotated.rotated != null) {
                translationSource = this.rotated.rotated;
            } else {
                translationSource = null;
            }
            if (translationSource != null) {
                this.reversed.translated     = translationSource;
                translationSource.translated = this.reversed;
            }
        }
        // try to find the rotation target
        do {
            // node + 2
            if (this.rotated != null) {
                // (node + 2) ^ 1
                if (this.rotated.reversed != null) {
                    this.reversed.rotated = this.rotated.reversed;
                    break;
                    // (node + 6) ^ 1
                } else if (this.rotated.translated != null) {
                    // node ^ 1 and node
                    if (this.rotated.translated.rotated != null && this.rotated.translated.rotated.rotated != null) {
                        this.reversed.rotated = this.rotated.translated.rotated.rotated;
                        break;
                    }
                }
            // (node + 4) ^ 1
            } else if (this.translated != null) {
                // node + 4 and node + 6 and (node + 2) ^ 1
                if (this.translated.reversed != null && this.translated.reversed.rotated != null
                    && this.translated.reversed.rotated.translated != null) {
                    this.reversed.rotated = this.translated.reversed.rotated.translated;
                }
            } else { /* intentionally empty */ }
        } while (false);
    }
    public Edge translate() {
        if (this.translated != null) {
            getTranslated();
        }
        return this.translated();
    }
    private void getTranslated() {
        var translatedOrientation      = switch (this.edgeOrientation) {
            case EdgeOrientation.CLOCKWISE_RIGHT -> EdgeOrientation.COUNTERCLOCKWISE_LEFT;
            case EdgeOrientation.COUNTERCLOCKWISE_RIGHT -> EdgeOrientation.CLOCKWISE_LEFT;
            case EdgeOrientation.CLOCKWISE_UP -> EdgeOrientation.COUNTERCLOCKWISE_DOWN;
            case EdgeOrientation.COUNTERCLOCKWISE_UP -> EdgeOrientation.CLOCKWISE_DOWN;
            case EdgeOrientation.CLOCKWISE_LEFT -> EdgeOrientation.COUNTERCLOCKWISE_RIGHT;
            case EdgeOrientation.COUNTERCLOCKWISE_LEFT -> EdgeOrientation.CLOCKWISE_RIGHT;
            case EdgeOrientation.CLOCKWISE_DOWN -> EdgeOrientation.COUNTERCLOCKWISE_UP;
            case EdgeOrientation.COUNTERCLOCKWISE_DOWN -> EdgeOrientation.CLOCKWISE_UP;
            default -> throw new AssertionError(this.edgeOrientation);
        };
        var otherTranslatedOrientation = (byte) (((this.edgeOrientation + 4) ^ 1) & 7);
        if (translatedOrientation != otherTranslatedOrientation) {
            throw new AssertionError();
        }
        this.translated = new Edge(this.edgeStr, this.edgeBits, otherTranslatedOrientation, null, this, null);
        this.distributeTranslated();
    }
    public Edge rotate() {
        if (this.rotated != null) {
            getRotated();
        }
        return this.rotated;
    }
    private void getRotated() {
        var otherRotatedOrientation = (byte) ((this.edgeOrientation + 2) & 7);
        this.rotated = new Edge(this.edgeStr, this.edgeBits, otherRotatedOrientation, null, null, null);
        // look for a bridge object back to the ring
        if (this.translated != null) {
            this.rotated.rotated = this.translated;
        } else if (this.reversed != null && this.reversed.rotated != null) {
            this.rotated.reversed = this.reversed.rotated;
        } else {
            // if there is no bridge object, make one
            getTranslated();
            this.rotated.rotated = this.translated;
        }
        this.distributeRotated();
    }
}
