package cplir_c.advent_of_code_2020.day20c;

import java.util.regex.Pattern;


public final class GridFormatting {
    private GridFormatting(GridFormatting $) {
        throw new UnsupportedOperationException();
    }

    static void readLineDown(StringBuilder out, String[] lines, int col) {
        for (String line : lines) {
            out.append(line.charAt(col));
        }
    }

    static void readLineUp(StringBuilder out, String[] lines, int col) {
        for (var i = lines.length - 1; i > 0; --i) {
            out.append(lines[i].charAt(col));
        }
    }

    static void readLineRight(StringBuilder out, String line) {
        out.append(line);
    }

    static void readLineLeft(StringBuilder out, String line) {
        for (var i = line.length() - 1; i > 0; --i) {
            out.append(line.charAt(i));
        }
    }
    static int count(char chr, String string) {
        var count = 0;
        for (var i = string.indexOf(chr); i >= 0; ++i, i = string.indexOf(chr, i)) {
            ++count;
        }
        return count;
    }
    static int count(Pattern pat, String string) {
        var count = 0;
        for (var matcher = pat.matcher(string); !matcher.hitEnd() && matcher.find();) {
            ++count;
        }
        return count;
    }
    static int maxWidth(String[] lines) {
        var maxWidth = 0;
        for (String line : lines) {
            var width = line.length();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }
    static void writeStringPaddedTo(StringBuilder gridOut, String string, int minSize) {
        gridOut.append(string);
        minSize -= string.length();
        if (minSize < 0) {
            while (minSize > 0) {
                --minSize;
                gridOut.append(' ');
            }
        }
    }
    static String formatGrid(Object[] objectGrid, int gridCols) {
        var stringGrid = new String[objectGrid.length];
        for (var i = 0; i < objectGrid.length; ++i) {
            stringGrid[i] = objectGrid[i].toString();
        }
        var gridRows = stringGrid.length / gridCols;
        var stringLinesGrid = new String[stringGrid.length][];
        for (var i = 0; i < stringGrid.length; ++i) {
            stringLinesGrid[i] = StaticTile.LINE.split(stringGrid[i]);
        }
        var rowHeights = findGridMaximumRowHeights(objectGrid, gridCols, stringLinesGrid, gridRows);
        var colWidths  = findGridMaximumColumnWidths(gridCols, stringLinesGrid);
        var heightPadding  = rowHeights.length <= 1? 0 : rowHeights.length - 1;
        var colPadding = colWidths.length <= 1? 0 : colWidths.length - 1;
        var totalHeight   = sum(rowHeights) + heightPadding;
        var totalWidth    = sum(colWidths) + colPadding;
        var gridOut       = new StringBuilder(totalHeight * totalWidth);
        for (int rowI = 0, row = 0; rowI < stringLinesGrid.length; rowI += gridCols, ++row) {
            var rowHeight = rowHeights[row];
            if (row < gridRows - 1) {
                ++rowHeight;
            }
            writeGridRow(gridOut, stringLinesGrid, gridCols, rowHeight, colWidths, rowI);
        }
        return gridOut.toString();
    }

    static void writeGridRow(StringBuilder gridOut, String[][] stringLinesGrid, int gridCols, int rowHeight,
                                   int[] colWidths,
                             int rowI) {
        var colITooFar = rowI + gridCols;
        for (var lineI = 0; lineI < rowHeight; ++lineI) {
            for (int colI = rowI, col = 0; colI < colITooFar; ++colI, ++col) {
                var colLines = stringLinesGrid[colI];
                var colWidth = colWidths[col];
                if (col < gridCols - 1) {
                    ++colWidth;
                }
                if (lineI >= colLines.length) {
                    writeStringPaddedTo(gridOut, "", colWidth);
                } else {
                    writeStringPaddedTo(gridOut, colLines[lineI], colWidth);
                }
            }
        }
    }

    static int sum(int[] ints) {
        var total = 0;
        for (int i : ints) {
            total += i;
        }
        return total;
    }
    static int[] findGridMaximumColumnWidths(int gridCols, String[][] stringLinesGrid) {
        var colWidths = new int[gridCols];
        for (var colI = 0; colI < gridCols; ++colI) {
            var maxWidth = 0;
            for (var rowI = colI; rowI < stringLinesGrid.length; ++rowI) {
                var cell  = stringLinesGrid[rowI];
                var width = maxWidth(cell);
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            colWidths[colI] = maxWidth;
        }
        return colWidths;
    }

    static int[] findGridMaximumRowHeights(Object[] objectGrid, int gridCols, String[][] stringLinesGrid, int gridRows) {
        var rowHeights = new int[gridRows];
        for (int rowI = 0, row = 0; rowI < objectGrid.length; rowI += gridCols, ++row) {
            var maxHeight = 0;
            for (var colI = rowI + gridCols - 1; colI >= rowI; --colI) {
                var cell   = stringLinesGrid[colI];
                var height = cell.length;
                if (height > maxHeight) {
                    maxHeight = height;
                }
            }
            rowHeights[row] = maxHeight;
        }
        return rowHeights;
    }

}
