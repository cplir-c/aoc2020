package cplir_c.advent_of_code_2020;

public final class Edge {
    final String edgeStr;
    final short  edgeBits;
    final byte   edgeOrientation;
    /**
     * These objects are lazily initialized.
     */
    /***/
    private Edge reversed;
    private Edge translated;
    private Edge rotated;

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
            var name  = field.getName();
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
            this.getReversed();
        }
        return this.reversed;
    }
    private void getReversed() {
        String reversedStr = null;
        short  reversedEdge;
        {
            final var reversedObj = this.findReversedBitOrderObj();
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
        var reversed = new Edge(reversedStr, reversedEdge, reversedDirection, this, null, null);
        this.distributeReversed(reversed);
    }
    private Edge findReversedBitOrderObj() {
        if (this.translated != null) {
            return this.translated;
        }
        {
            var rotReversed = this.findRotReversed();
            if (rotReversed != null) {
                return rotReversed;
            }
        }
        {
            var transRot = this.findTransRot();
            if (transRot != null) {
                return transRot;
            }
        }
        return null;
    }
    private void distributeReversed(Edge rev) {
        // set references to and from this.reversed
        // there should be one for translated and one for rotated

        // find the rotation of reversed
        var rotReversed = this.findRotReversed();
        // find the translation of reversed
        var rotRot = this.findRotRot();
        // find the rotates-to-reversed
        var transRot = this.findTransRot();
        if (rotReversed != null) {
            rev.rotated = rotReversed;
        }
        if (rotRot != null) {
            rotRot.translated = rev;
            rev.translated    = rotRot;
        }
        if (transRot != null) {
            transRot.rotated = rev;
        }
        this.reversed = rev;
    }
    public Edge translate() {
        if (this.translated == null) {
            this.getTranslated();
        }
        return this.translated;
    }
    private void getTranslated() {
        var translatedOrientation = this.edgeOrientation;
        translatedOrientation += 4;
        translatedOrientation ^= 1;
        var trans = new Edge(this.edgeStr, this.edgeBits, translatedOrientation, null, this, null);
        this.distributeTranslated(trans);
    }
    private void distributeTranslated(Edge trans) {
        // find rotation of translated
        var transRot = this.findTransRot();
        // find reverses to translated
        var rotRot = this.findRotRot();
        // find rotates-to-translated
        var rotReversed = this.findRotReversed();
        if (transRot != null) {
            trans.rotated = transRot;
        }
        if (rotRot != null) {
            trans.reversed  = rotRot;
            rotRot.reversed = trans;
        }
        if (rotReversed != null) {
            rotReversed.rotated = trans;
        }
        this.translated = trans;
    }
    public Edge rotate() {
        if (this.rotated == null) {
            this.getRotated();
        }
        return this.rotated;
    }
    private void getRotated() {
        Edge rotated;
        {
            var otherRotatedOrientation = (byte) ((this.edgeOrientation + 2) & 7);
            rotated = new Edge(this.edgeStr, this.edgeBits, otherRotatedOrientation, null, null, null);
        }
        this.distributeRotated(rotated);
        // look for a bridge object back to the ring
        if (rotated.rotated == null && rotated.reversed == null && rotated.translated == null) {
            this.constructBridgeFromRotated(rotated);
        }
    }
    /** create a bridge back to this from rotated */
    private void constructBridgeFromRotated(Edge rot) {
        // try to find an object for the other side of a single object bridge
        /*
         * rot --[rotate]---> rotrot ----[rotate]------> rotrotrot -[rotate]-> root
         * rot --[rotate]---> rotrot ----[reverse]-----> trans -[translate]--> root
         * rot --[rotate]---> rotrot ----[translate]---> reversed -[reverse]-> root
         * rot --[reverse]--> rotreversed --[rotate]---> trans -[translate]--> root
         * rot --[reverse]--> rotreversed -[translate]-> rotrotrot -[rotate]-> root
         * rot -[translate]-> transrot ----[reverse]---> rotrotRot -[rotate]-> root
         * rot -[translate]-> transrot ----[rotate]----> reversed -[reverse]-> root
         */
        // second half present, create the first link obj
        if (this.translated != null) {
            this.translated.getReversed();
            return;
        }
        if (this.reversed != null) {
            this.reversed.getTranslated();
            return;
        }
        // first half present, create the second link obj
        {
            var rotRot = this.findRotRot();
            if (rotRot != null) {
                rotRot.getTranslated();
                return;
            }
        }
        {
            var rotReversed = this.findRotReversed();
            if (rotReversed != null) {
                rotReversed.getTranslated();
                return;
            }
        }
        {
            var transRot = this.findTransRot();
            if (transRot != null) {
                transRot.getReversed();
                return;
            }
        }
        // neither present, create both
        this.reverse().getTranslated();
    }
    private void distributeRotated(Edge rotated) {
        // find reverses to rotated
        var rotReversed = this.findRotReversed();
        // find translates to rotated
        var transRot = this.findTransRot();
        // find rotates from rotated
        var rotRot = this.findRotRot();
        if (rotReversed != null) {
            rotReversed.reversed = rotated;
            rotated.reversed     = rotReversed;
        }
        if (transRot != null) {
            transRot.translated = rotated;
            rotated.translated  = transRot;
        }
        if (rotRot != null) {
            rotated.rotated = rotRot;
        }
        this.rotated = rotated;
    }
    /**
     * The entire lazy reference graph:
     * //formatter:off
     *
     *    [translate]---reversed------>[rotate]------>rotreversed---[translate]
     *         |         ^      \                     /   |             |
     *         |         |   [reverse]          [reverse] |             |
     *      rotrot       |        \                 /     |             |
     *                   |        root->[rotate]->rot     |         rotrotrot
     *                   |         ^               |      |
     *               [rotate]      |               v      |
     *                   ^      [rotate]        [rotate]  v
     *                   |         ^               |    [rotate]
     *                   |         |               v      |
     *                   |   rotrotrot<-[rotate]<-rotrot  |
     *        rot        |      /                    \    |       root
     *         |         |   [reverse]          [reverse] |         |
     *         |         |    /                        \  v         |
     *    [translate]---transrot<------[rotate]<-------trans---[translate]
     *
     *   undirected edges mean bidirectional references,
     *   directed edges mean one way references
     * //formatter:on
     */
    protected Edge findRotRot() {
        if (this.rotated != null) {
            return this.rotated.rotated;
        } else if (this.translated != null) {
            return this.translated.reversed;
        } else if (this.reversed != null) {
            return this.reversed.translated;
        } else {
            return null;
        }
    }
    protected Edge findTransRot() {
        if (this.rotated != null) {
            return this.rotated.translated;
        } else if (this.translated != null) {
            return this.translated.rotated;
        } else if (this.reversed != null) {
            if (this.reversed.rotated != null) {
                var rotReversed = this.reversed.rotated;
                // rotReversed.rotated is silly cause its the same as trans
                // rotReversed.reversed is silly cause it's the same as rot
                if (rotReversed.translated != null) {
                    return rotReversed.translated.reversed;
                }
            }
            if (this.reversed.translated != null) {
                var rotRot = this.reversed.translated;
                if (rotRot.rotated != null) {
                    return rotRot.rotated.reversed;
                }
                // rotRot.reversed is silly cause its the same as trans
                // rotRot.translated is silly cause its the same as reversed
            }
            // reversed.reversed is silly cause its the same as root
        }
        return null;
    }
    protected Edge findRotReversed() {
        if (this.rotated != null) {
            return this.rotated.reversed;
        } else if (this.reversed != null) {
            return this.reversed.rotated;
        } else if (this.translated != null) {
            if (this.translated.rotated != null) {
                var transRot = this.translated.rotated;
                // transRot.rotated is silly cause its the same as reversed
                if (transRot.reversed != null) {
                    return transRot.reversed.translated;
                }
                // transRot.translated is silly cause its the same as rot
            }
            if (this.translated.reversed != null) {
                var rotRot = this.translated.reversed;
                if (rotRot.rotated != null) {
                    return rotRot.rotated.translated;
                }
                // rotRot.reversed is silly cause its the same as trans
                // rotRot.translated is silly cause its the same as reversed
            }
        } else { /* fallthrough to return null */ }
        return null;
    }
    protected Edge findRotRotRot() {
        if (this.rotated != null) {
            if (this.rotated.rotated != null) {
                var rotRot = this.rotated.rotated;
                return rotRot.rotated;
            }
            if (this.rotated.translated != null) {
                var transRot = this.rotated.translated;
                return transRot.reversed;
            }
            if (this.rotated.reversed != null) {
                var rotReversed = this.rotated.reversed;
                // rotReversed.rotated is silly cause its the same as trans
                // rotReversed.reversed is silly cause its the same as rotated
                if (rotReversed.translated != null) {
                    return rotReversed.translated.reversed;
                }
            }
        }
        if (this.reversed != null) {
            if (this.reversed.rotated != null) {
                var rotReversed = this.reversed.rotated;
                // rotReversed.rotated is silly cause its the same as trans
                // rotReversed.reversed is silly cause its the same as rotated
                if (rotReversed.translated != null) {
                    return rotReversed.translated.reversed;
                }
            }
            // this.reversed.reversed is silly cause its the same as root
            if (this.reversed.translated != null) {
                var rotRot = this.reversed.translated;
                return rotRot.rotated;
            }
        }
        if (this.translated != null) {
            if (this.translated.rotated != null) {
                var transRot = this.translated.rotated;
                return transRot.reversed;
            }
            if (this.translated.reversed != null) {
                var rotRot = this.translated.reversed;
                return rotRot.rotated;
            }
            // this.translated.translated is silly cause its the same as root
        }
        return null;
    }
}
