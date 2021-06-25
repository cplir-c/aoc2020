package cplir_c.advent_of_code_2020;

import it.unimi.dsi.fastutil.shorts.ShortArrayFIFOQueue;


final class PrintableShortArrayFIFOQueue extends ShortArrayFIFOQueue implements Comparable<PrintableShortArrayFIFOQueue> {
    public PrintableShortArrayFIFOQueue(PrintableShortArrayFIFOQueue otherQueue) {
        this(otherQueue.array, otherQueue.start, otherQueue.length, otherQueue.end, otherQueue.size());
    }

    public PrintableShortArrayFIFOQueue() {}

    public PrintableShortArrayFIFOQueue(PrintableShortArrayFIFOQueue otherQueue, short length) {
        this(
            otherQueue.array, otherQueue.start, otherQueue.length,
            calculateEnd(length, otherQueue.start, otherQueue.length),
            length
        );
    }
    /**
     * applen = end - start;<br>
     * if (applen < 0) applen += this.length;<br>
     * return applen;<br>
     * applen + start = end;<br>
     * if (applen < 0) applen += this.length;<br>
     * return applen;<br>
     * <br>
     * int calculateEnd(size, start, arrayLength) {<br>
     * int end = size - start;<br>
     * end -= arrayLength;<br>
     * if (end < 0) end += arrayLength;<br>
     * return end;<br>
     * }<br>
     */
    private static int calculateEnd(int size, int start, int length) {
        var end = size + start;
        end -= length;
        if (end < 0) {
            end += length;
        }
        return end;
    }

    protected PrintableShortArrayFIFOQueue(short[] array, int start, int length, int end, int size) {
        super(size + 1);
        if (start > end) {
            System.arraycopy(array, start, this.array, 0, length - start);
            System.arraycopy(array, 0, this.array, length - start, end);
        } else {
            System.arraycopy(array, start, this.array, 0, end - start);
        }
        this.start  = 0;
        this.length = (this.end = size) + 1;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(this.size() * ShortArrayFIFOQueue.INITIAL_CAPACITY);
        if (this.isEmpty()) {
            return "[]";
        }
        sb.append('[');
        if (this.end > this.start) {
            for (var i = this.start; i < this.end; ++i) {
                sb.append(this.array[i]);
                sb.append(", ");
            }
        } else {
            for (var i = this.start; i < this.length; ++i) {
                sb.append(this.array[i]);
                sb.append(", ");
            }
            for (var i = 0; i < this.end; ++i) {
                sb.append(this.array[i]);
                sb.append(", ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(']');
        return sb.toString();
    }
    @Override
    public int compareTo(PrintableShortArrayFIFOQueue o) {
        var len  = this.size();
        var olen = o.size();
        var oar  = o.array;
        if (olen == len) {
            return this.compareWithEqualLength(o, oar, this.start, o.start);
        } else if (olen < len) {
            return 1;
        } else {
            return -1;
        }
    }
    @Override
    public int hashCode() { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other instanceof short[]) {
            return this.equalsArray((short[]) other);
        } else if (other instanceof PrintableShortArrayFIFOQueue) {
            return this.compareTo((PrintableShortArrayFIFOQueue) other) == 0;
        } else {
            return false;
        }
    }
    private boolean equalsArray(short[] oar) {
        if (oar.length != this.size()) {
            return false;
        }
        if (this.start > this.end) {
            // this is disjoint
            for (var i = this.start; i < this.length; ++i) {
                var n  = this.array[i];
                var on = oar[i];
                if (n != on) {
                    return false;
                }
            }
            for (var i = 0; i < this.end; ++i) {
                var n  = this.array[i];
                var on = oar[i];
                if (n != on) {
                    return false;
                }
            }
        } else {
            for (var i = this.start; i < this.end; ++i) {
                var n  = this.array[i];
                var on = oar[i];
                if (n != on) {
                    return false;
                }
            }
        }
        return true;
    }
    private int compareWithEqualLength(PrintableShortArrayFIFOQueue o, short[] oar, int start, int ostart) {
        if (ostart > o.end) {
            if (start > this.end) {
                // both disjoint
                return this.compareWithBothDisjoint(o, oar, start, ostart);
            } else {
                // just o is disjoint
                return this.compareWithODisjoint(o, oar, start, ostart);
            }
        } else if (start > this.end) {
            // just this is disjoint
            return this.compareWithThisDisjoint(o, oar, start, ostart);
        } else {
            // nothing's disjoint
            return this.compareWithBothJoined(o, oar, start, ostart);
        }
    }
    private int compareWithBothJoined(PrintableShortArrayFIFOQueue o, short[] oar, int start, int ostart) {
        for (int oi = ostart, i = start; i < this.end; ++i, ++oi) {
            var on = oar[oi];
            var n  = this.array[i];
            if (n != on) {
                if (n < on) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }
    private int compareWithThisDisjoint(PrintableShortArrayFIFOQueue o, short[] oar, int start, int ostart) {
        int oi;
        int i;
        for (oi = ostart, i = start; i < this.length; ++oi, ++i) {
            var on = oar[oi];
            var n  = this.array[i];
            if (n != on) {
                if (n < on) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        for (i = 0, oi++; oi < this.end; ++i, ++oi) {
            var on = oar[oi];
            var n  = this.array[i];
            if (n != on) {
                if (n < on) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }
    private int compareWithODisjoint(PrintableShortArrayFIFOQueue o, short[] oar, int start, int ostart) {
        int oi;
        int i;
        for (oi = ostart, i = start; oi < o.length; ++oi, ++i) {
            var on = oar[oi];
            var n  = this.array[i];
            if (n != on) {
                if (n < on) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        for (oi = 0, i++; i < this.end; ++i, ++oi) {
            var on = oar[oi];
            var n  = this.array[i];
            if (n != on) {
                if (n < on) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }
    private int compareWithBothDisjoint(PrintableShortArrayFIFOQueue o, short[] oar, int start, int ostart) {
        if (o.end > this.end) {
            // o has fewer elements in the first segment
            int oi;
            int i;
            for (oi = ostart, i = start; oi < o.length; ++oi, ++i) {
                var on = oar[oi];
                var n  = this.array[i];
                if (n != on) {
                    if (n < on) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            for (oi = 0, ++i; i < this.length; ++oi, ++i) {
                var on = oar[oi];
                var n  = this.array[i];
                if (n != on) {
                    if (n < on) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            for (i = 0, ++oi; oi < o.end; ++oi, ++i) {
                var on = oar[oi];
                var n  = this.array[i];
                if (n != on) {
                    if (n < on) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        } else {
            // this has fewer (or an equal number of) elements in the first segment
            int oi;
            int i;
            for (oi = ostart, i = start; i < this.length; ++oi, ++i) {
                var on = oar[oi];
                var n  = this.array[i];
                if (n != on) {
                    if (n < on) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            for (i = 0, ++oi; oi < o.length; ++oi, ++i) {
                var on = oar[oi];
                var n  = this.array[i];
                if (n != on) {
                    if (n < on) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            for (oi = 0, ++i; i < this.end; ++oi, ++i) {
                var on = oar[oi];
                var n  = this.array[i];
                if (n != on) {
                    if (n < on) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }
}
