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
        final Edge revTranslated;
        if (this.translated != null) {
            revTranslated = this.translated.reversed;
            if (revTranslated != null) {
                var indirectReversed = revTranslated.translated;
                if (indirectReversed != null) {
                    this.reversed = indirectReversed;
                } else {
                    indirectReversed = this.reversed = new Edge(
                        revTranslated.edgeStr, revTranslated.edgeBits, ((byte) (this.edgeOrientation ^ 1)), this, revTranslated,
                        null
                    );
                }
                return indirectReversed;
            }
        } else {
            revTranslated = null;
        }
        var reversedSb = new StringBuilder(this.edgeStr);
        reversedSb.reverse();
        var reversedEdge      = (short) (Integer.reverse(this.edgeBits) >>> (Integer.SIZE - 10));
        var reversedDirection = this.edgeOrientation;
        reversedDirection ^= 1;
        this.reversed      = new Edge(reversedSb.toString(), reversedEdge, reversedDirection, this, revTranslated, null);
        return this.reversed;
    }
    public Edge translate() {
        if (this.translated != null) {
            getTranslated();
        }
        return this.translated();
    }
    private void getTranslated() {
        final Edge revTranslated;
        if (this.reversed != null) {
            revTranslated = this.reversed.translated;
            if (revTranslated != null && revTranslated.reversed != null) {
                this.translated = revTranslated.reversed;
                return this.translated;
            }
        } else {
            revTranslated = null;
        }
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
        this.translated = new Edge(this.edgeStr, this.edgeBits, otherTranslatedOrientation, revTranslated, this, null);
    }
    public Edge rotate() {
        if (this.rotated != null) {
            getRotated();
        }
        return this.rotated;
    }
    private void getRotated() {
        /** // formatter:off
         *  4
         * 1 3 -> 3412
         *  2
         * // formatter:on
         * rotatedRotated == this.translated;
         */
        /** // formatter:off
         *  3
         * 4 2 -> 2341
         *  1
         * // formatter:on
         * */
        Edge rotatedTranslated = null;
        /** // formatter:off
         *  I
         * Z A -> AIZE
         *  E
         * // formatter:on
         * */
        Edge rotatedReversed = null;
        /** // formatter:off
         *  normal
         *   2
         *  3 1 -> 1234
         *   4
         *  rotated
         *   1
         *  2 4 -> 4123
         *   3
         * // formatter:on
         * */
        String rotatedString     = null;
        short  rotatedBits       = -1;
        byte   rotatedOrientatio = -1;
        if (this.translated != null) {
            // trans : rotate CW or CCW twice
            // trans : 3412
            var trans = this.translated;
            if (trans.translated != null && trans.translated != this) {
                var transTrans = trans.translated;
                trans.translated = this;
                if (transTrans.translated != null && this.translated == null) {
                    this.translated = transTrans.translated;
                }
                if (transTrans.reversed != null && this.reversed == null) {
                    this.reversed = transTrans.reversed;
                }
                if (transTrans.rotated != null) {
                    this.rotated = transTrans.rotated;
                    return this.rotated;
                }
            }
            if (trans.reversed != null) {
                // transrevved
                // _A
                // E_Z -> ZAEI
                // _I
                var transRev = trans.reversed;
                if (transRev.translated != this.reversed) {
                    var transRevTrans = transRev.translated;
                    var rev           = this.reversed;
                    if (transRevTrans == null) {
                        transRev.translated = rev;
                    }
                    if (transRevTrans.translated != null && rev.translated == null) {
                        rev.translated = transRevTrans.translated;
                    }
                    if (transRevTrans.reversed != this) {
                        var transRevTransRev = transRevTrans.reversed;
                        transRevTrans.reversed = this;
                        if (transRevTransRev.translated != null && this.translated == null) {
                            this.translated = transRevTransRev.translated;
                        }
                        if (transRevTransRev.reversed != null && this.reversed == null) {
                            rev = this.reversed = transRevTransRev.reversed;
                        }
                        if (transRevTransRev.rotated != null) {
                            this.rotated = transRevTransRev.rotated;
                            return this.rotated;
                        }
                    }
                    if (transRevTrans.rotated != null) {
                        rotatedReversed = transRevTrans.rotated;
                    }
                }
                if (transRev.reversed != trans) {
                    var transRevRev = transRev.reversed;
                    transRev.reversed = trans;
                    if (trans.reversed == null && transRevRev.reversed != null) {
                        trans.reversed = transRevRev.reversed;
                    }
                    if (trans.rotated == null && transRevRev.rotated != null) {
                        trans.rotated = transRevRev.rotated;
                    }
                    if (trans.translated != this) {
                        trans.translated = this;
                    }
                }
                if (transRev.rotated != null) {
                    // transrevrot
                    // _Z
                    // A_I -> IZAE
                    // _E
                    var transRevRot = transRev.rotated;
                    if (transRevRot.translated != null) {
                        // transrevrot
                        // _Z
                        // A_I -> IZAE
                        // _E
                        // transrevrottrans
                        // _E
                        // I_A
                        // _Z
                        var transRevRotTrans = transRevRot.translated;
                        rotatedReversed = transRevRotTrans;
                    }
                }
            }
            if (trans.rotated != null) {
                /** // formatter:off
                 * normal
                 *  2
                 * 3 1 -> 1234
                 *  4
                 * translated
                 *  4
                 * 1 3 -> 3412
                 *  2
                 * translated & rotated
                 *  3
                 * 4 2 -> 2341
                 *  1
                 */// formatter:on
                var transrotat = trans.rotated;
                if (transrotat.translated != null) {
                    /** // formatter:off
                     * translated & rotated
                     *  3
                     * 4 2 -> 2341
                     *  1
                     * translated & rotated & translated == rotated
                     *  1
                     * 2 4 -> 4123
                     *  3
                     *  // formatter:on
                     */
                    this.rotated = transrotat.translated;
                    return this.rotated;
                }
                if (transrotat.reversed != null) {
                    /** // formatter:off
                     * translated & rotated
                     *  3
                     * 4 2 -> 2341
                     *  1
                     * translated & rotated & reversed
                     *  E
                     * A Z -> ZEAI
                     *  I
                     *  // formatter:on
                     */
                    var transrotatrevs = transrotat.reversed;
                    if (transrotatrevs.translated != null) {
                        /** // formatter:off
                         * translated & rotated & reversed
                         *  E
                         * A Z -> ZEAI
                         *  I
                         * translated & rotated & reversed & translated
                         *  I
                         * Z A -> AIZE
                         *  E
                         * // formatter:on
                         */
                        rotatedReversed = transrotatrevs.translated;
                        if (rotatedReversed.reversed != null) {
                            /** // formatter:off
                             * translated & rotated & reversed
                             *  E
                             * A Z -> ZEAI
                             *  I
                             * translated & rotated & reversed & translated
                             *  I
                             * Z A -> AIZE
                             *  E
                             * // formatter:on
                             */
                            this.rotated = rotatedReversed.reversed;
                            if (this.rotated.reversed == null) {
                                this.rotated.reversed = rotatedReversed;
                            }
                            return this.rotated;
                        }
                    }
                }
            }
        }
        if (this.reversed != null) {
            var revved = this.reversed;
            if (revved.translated != null) {

            }
            if (revved.reversed != null) {

            }
        }
    }
}
