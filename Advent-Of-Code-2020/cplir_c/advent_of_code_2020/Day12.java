package cplir_c.advent_of_code_2020;

import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;


public class Day12 {

    public static void main(String[] args) {
        executeStringDirections(EXAMPLE);
        executeStringDirections(INPUT);
        executeStringWaypoints(EXAMPLE);
        executeStringWaypoints(INPUT);
    }

    private static void executeStringWaypoints(String inputString) {
        var instructions = compile(inputString);
        var coords       = executeWaypoint(instructions);
        System.out.println("distance: " + manhattanDistance(coords, 0L));
    }

    private static long executeWaypoint(int[] instructions) {
        var wayPointX = 10;
        var wayPointY = 1;
        var shipX = 0;
        var shipY = 0;
        for (int ins : instructions) {
            var opcode = ins & 0b111;
            ins >>= 3;
            switch(opcode) {
                case 0:
                    wayPointX += ins;
                    break;
                case 1:
                    wayPointY += ins;
                    break;
                case 2:
                    wayPointX -= ins;
                    break;
                case 3:
                    wayPointY -= ins;
                    break;

                case 6:
                    shipX += wayPointX * ins;
                    shipY += wayPointY * ins;
                    break;
                case 4:
                case 5:
                    var wayPointRot = getWayPointRotationFromOpcode(ins, opcode);
                    var returned = rotateWaypoint(wayPointX, wayPointY, wayPointRot);
                    wayPointY = (int) returned;
                    wayPointX = (int) (returned >> Integer.SIZE);
                    break;
                default :
                    throw new AssertionError(opcode);
            }
        }
        System.out.println("sx: " + shipX + " sy: " + shipY + " wx: " + wayPointX + " wy: " + wayPointY);
        return (((long) shipX) << Integer.SIZE) | (0xff_ff_ff_ffL & shipY);
    }

    private static int getWayPointRotationFromOpcode(int ins, int opcode) {
        int wayPointRot;
        if (opcode == 4) { // left
            wayPointRot = ins / 90;
        } else { // right
            wayPointRot = -ins / 90;
        }
        wayPointRot &= 0b11;
        return wayPointRot;
    }
    /***
     * equivalent to {@code
     *  switch (wayPointRot) {
     *      case 0:
     *          break;
     *      case 1:
     *          // reflect across y=x and then x=0
     *          wayPointX ^= wayPointY;
     *          // x:xy y:x
     *          wayPointY ^= wayPointX;
     *          // x:y y:x
     *          wayPointX ^= wayPointY;
     *          wayPointX = -wayPointX;
     *          break;
     *
     *      case 0b10:
     *          // reflect across y=0 and x=0
     *          wayPointY = -wayPointY;
     *          wayPointX = -wayPointX;
     *          break;
     *
     *      case 0b11:
     *          // reflect across y=x and y=0
     *          wayPointX ^= wayPointY;
     *          // x:xy y:x
     *          wayPointY ^= wayPointX;
     *          // x:y y:x
     *          wayPointX ^= wayPointY;
     *          wayPointY = -wayPointY;
     *          break;
     *
     *      default :
     *          throw new AssertionError();
     * }}
     */
    private static long rotateWaypoint(int wayPointX, int wayPointY, int wayPointRot) {
        if ((wayPointRot & 0b01) != 0) {
            // reflect across y=x
            // x:xy y:y
            wayPointX ^= wayPointY;
            // x:xy y:x
            wayPointY ^= wayPointX;
            // x:y y:x
            wayPointX ^= wayPointY;
        }
        if ((wayPointRot & 0b10) != 0) {
            // reflect across y=0
            wayPointY = -wayPointY;
        }
        if (wayPointRot == 1 || wayPointRot == 2) {
            // reflect across x=0
            wayPointX = -wayPointX;
        }
        return (((long) wayPointX) << Integer.SIZE) | (0xff_ff_ff_ffL & wayPointY);
    }

    private static void executeStringDirections(String inputString) {
        var instructions = compile(inputString);
        var coords       = execute(instructions);
        System.out.println("distance: " + manhattanDistance(coords, 0L));
    }

    private static int manhattanDistance(long coords, long p) {
        var coordsx = (int) (coords >>> Integer.SIZE);
        var coordsy = (int) coords;
        var px      = (int) (p >>> Integer.SIZE);
        var py      = (int) p;
        return abs(py - coordsy) + abs(px - coordsx);
    }

    private static int abs(int i) {
        if (i < 0) {
            return -i;
        }
        return i;
    }

    private static long execute(int[] instructions) {
        var shipX   = 0;
        var shipY   = 0;
        byte shipRot = 0;
        for (int ins : instructions) {
            var opcode = ins & 0b111;
            ins >>= 3;
            if (opcode >= 6) {
                // forward is translated to an absolute instruction
                opcode = shipRot;
            } else if (opcode >= 4) {
                if (opcode == 4) {
                    // rotate Left (+ shipRot)
                    shipRot += ins / 90;
                } else {
                    // rotate Right (- shipRot)
                    shipRot -= ins / 90;
                }
                shipRot &= 0b11;
            }
            if (opcode < 4) {
                switch (opcode) {
                    case 0: // E
                        shipX += ins;
                        break;
                    case 1: // N
                        shipY += ins;
                        break;
                    case 2: // W
                        shipX -= ins;
                        break;
                    case 3: // S
                        shipY -= ins;
                        break;

                    default :
                        throw new AssertionError("" + opcode);
                }
            }
        }
        System.out.println("x: " + shipX + " y: " + shipY + " r: " + shipRot);
        return (((long) shipX) << Integer.SIZE) | (0xff_ff_ff_ffL & shipY);
    }

    private static final Pattern SPACE_SPLIT = Pattern.compile("\\s+");
    static final String          OPCHARS     = "ENWSLRF";
    private static int[] compile(String inputString) {
        var inputLines = SPACE_SPLIT.split(inputString);
        IntList instructions = new IntArrayList(inputLines.length);
        for (String line : inputLines) {
            if (line.isEmpty() || line.isBlank()) {
                continue;
            }
            var opChar = line.charAt(0);
            line = line.substring(1);
            var argument = Integer.parseInt(line);
            final int opcode;
            instructions.add((argument << 3) | getOpcode(opChar));
        }
        return instructions.toArray(new int[instructions.size()]);
    }

    void prettyPrintInstructions(int[] instrs) {
        var s = new StringBuilder(instrs.length * 4);
        for (int ins : instrs) {
            var opcode = ins & 0b111;
            ins >>= 3;
            s.append(Day12.getOpChar(opcode));
            s.append(ins);
            s.append('\n');
        }
        if (instrs.length > 0) {
            s.setLength(s.length() - 1);
        }
        System.out.println(s);
    }

    private static char getOpChar(int opcode) { return OPCHARS.charAt(opcode); }

    private static int getOpcode(char opChar) {
        switch (opChar) {
            case 'E':
                return 0;
            case 'N':
                return 1;

            case 'W':
                return 2;
            case 'S':
                return 3;

            case 'L':
                return 4;
            case 'R':
                return 5;

            case 'F':
                return 6;

            default :
                throw new AssertionError(opChar);
        }
    }

    static final String EXAMPLE = "F10\n" + "N3\n" + "F7\n" + "R90\n" + "F11";
    static final String INPUT   = "R180\n" + "E1\n" + "N1\n" + "R90\n" + "E4\n" + "F84\n" + "W3\n" + "F19\n" + "E5\n" + "N1\n"
            + "W5\n" + "W4\n" + "R90\n" + "S1\n" + "F70\n" + "R90\n" + "W2\n" + "S3\n" + "L180\n" + "E2\n" + "R90\n" + "E1\n"
            + "R90\n" + "N2\n" + "E3\n" + "F55\n" + "W3\n" + "R90\n" + "S2\n" + "E4\n" + "L90\n" + "F33\n" + "W4\n" + "S3\n"
            + "F11\n" + "N4\n" + "W4\n" + "F88\n" + "W3\n" + "F62\n" + "L90\n" + "S3\n" + "W3\n" + "N1\n" + "E3\n" + "E2\n"
            + "N1\n" + "E3\n" + "S3\n" + "E5\n" + "S4\n" + "W5\n" + "L90\n" + "W5\n" + "R90\n" + "E4\n" + "F36\n" + "N4\n"
            + "E4\n" + "E5\n" + "F47\n" + "R90\n" + "N3\n" + "E3\n" + "L180\n" + "S5\n" + "R90\n" + "S5\n" + "W2\n" + "S3\n"
            + "F43\n" + "W2\n" + "R180\n" + "W2\n" + "S2\n" + "L180\n" + "E2\n" + "F49\n" + "L90\n" + "F77\n" + "S5\n" + "E1\n"
            + "S2\n" + "F39\n" + "L180\n" + "F12\n" + "W1\n" + "L90\n" + "F60\n" + "S2\n" + "E3\n" + "N1\n" + "E5\n" + "R90\n"
            + "E1\n" + "N4\n" + "W4\n" + "S4\n" + "E2\n" + "L90\n" + "F100\n" + "E3\n" + "F58\n" + "S2\n" + "E4\n" + "F83\n"
            + "W3\n" + "N1\n" + "R90\n" + "F99\n" + "W5\n" + "W4\n" + "F19\n" + "N3\n" + "W1\n" + "W5\n" + "F70\n" + "R180\n"
            + "R90\n" + "F58\n" + "E5\n" + "N3\n" + "R90\n" + "N4\n" + "F86\n" + "N4\n" + "F70\n" + "L90\n" + "F36\n" + "R90\n"
            + "S4\n" + "R180\n" + "N5\n" + "F81\n" + "W5\n" + "R90\n" + "E5\n" + "L90\n" + "F73\n" + "S5\n" + "E3\n" + "N1\n"
            + "F27\n" + "E2\n" + "N3\n" + "L180\n" + "W2\n" + "F24\n" + "N2\n" + "L90\n" + "F69\n" + "N2\n" + "E3\n" + "F49\n"
            + "L90\n" + "S4\n" + "W5\n" + "F18\n" + "R180\n" + "F62\n" + "S2\n" + "F65\n" + "E3\n" + "F100\n" + "N1\n" + "F96\n"
            + "W1\n" + "F75\n" + "L180\n" + "W2\n" + "S2\n" + "F93\n" + "E5\n" + "S3\n" + "R90\n" + "S2\n" + "F34\n" + "R90\n"
            + "F83\n" + "S1\n" + "W1\n" + "F58\n" + "L180\n" + "N4\n" + "E1\n" + "R90\n" + "W2\n" + "F93\n" + "S3\n" + "F20\n"
            + "R90\n" + "N3\n" + "E3\n" + "N3\n" + "L90\n" + "F8\n" + "S3\n" + "F18\n" + "S3\n" + "N2\n" + "F6\n" + "L270\n"
            + "F70\n" + "W5\n" + "S3\n" + "F54\n" + "E2\n" + "F75\n" + "E3\n" + "R90\n" + "N2\n" + "W3\n" + "L90\n" + "E4\n"
            + "F58\n" + "N5\n" + "F97\n" + "W2\n" + "L90\n" + "W5\n" + "S2\n" + "W1\n" + "S5\n" + "F85\n" + "N1\n" + "E1\n"
            + "N4\n" + "E4\n" + "L90\n" + "E1\n" + "R90\n" + "S5\n" + "L180\n" + "R270\n" + "N1\n" + "L90\n" + "E4\n" + "N3\n"
            + "F45\n" + "N2\n" + "F68\n" + "R90\n" + "F36\n" + "N5\n" + "F82\n" + "S5\n" + "E4\n" + "R180\n" + "S2\n" + "L180\n"
            + "N3\n" + "R270\n" + "W5\n" + "F70\n" + "L90\n" + "W5\n" + "F80\n" + "W4\n" + "N2\n" + "R90\n" + "S3\n" + "W1\n"
            + "F23\n" + "N1\n" + "W1\n" + "N4\n" + "F70\n" + "S3\n" + "L180\n" + "F57\n" + "R90\n" + "L90\n" + "F55\n" + "L90\n"
            + "N4\n" + "F87\n" + "L90\n" + "F1\n" + "L270\n" + "F17\n" + "N5\n" + "R180\n" + "F84\n" + "R90\n" + "W5\n" + "F7\n"
            + "W1\n" + "S1\n" + "E3\n" + "F46\n" + "S5\n" + "E2\n" + "F23\n" + "R90\n" + "E4\n" + "W2\n" + "F96\n" + "E5\n"
            + "L90\n" + "F65\n" + "F3\n" + "S5\n" + "E5\n" + "N3\n" + "W4\n" + "L90\n" + "S2\n" + "F57\n" + "E1\n" + "R90\n"
            + "F68\n" + "E3\n" + "L90\n" + "W1\n" + "F29\n" + "N5\n" + "W5\n" + "N1\n" + "F95\n" + "N1\n" + "L90\n" + "F31\n"
            + "S5\n" + "L180\n" + "N2\n" + "W5\n" + "R90\n" + "F27\n" + "E1\n" + "R90\n" + "E3\n" + "S5\n" + "F10\n" + "R90\n"
            + "N4\n" + "E2\n" + "F25\n" + "S4\n" + "E5\n" + "F51\n" + "N3\n" + "W2\n" + "L90\n" + "S3\n" + "L180\n" + "F17\n"
            + "E4\n" + "F93\n" + "E3\n" + "L90\n" + "F41\n" + "L90\n" + "S5\n" + "L90\n" + "W5\n" + "N1\n" + "F81\n" + "L90\n"
            + "E4\n" + "W2\n" + "R90\n" + "W1\n" + "S5\n" + "R90\n" + "F39\n" + "W3\n" + "R90\n" + "N5\n" + "E1\n" + "L90\n"
            + "F82\n" + "S3\n" + "R90\n" + "W4\n" + "F66\n" + "F4\n" + "L90\n" + "F77\n" + "R90\n" + "E1\n" + "L90\n" + "F53\n"
            + "S4\n" + "F35\n" + "W1\n" + "F64\n" + "R90\n" + "F9\n" + "S1\n" + "E1\n" + "L90\n" + "W4\n" + "R90\n" + "S2\n"
            + "W5\n" + "R90\n" + "S4\n" + "L90\n" + "N3\n" + "F8\n" + "L180\n" + "N5\n" + "E5\n" + "N4\n" + "F35\n" + "N5\n"
            + "W1\n" + "N1\n" + "E5\n" + "F15\n" + "R180\n" + "F92\n" + "W3\n" + "L90\n" + "F4\n" + "L90\n" + "E1\n" + "S3\n"
            + "W3\n" + "R90\n" + "F37\n" + "N5\n" + "F19\n" + "S2\n" + "F98\n" + "L90\n" + "F24\n" + "W3\n" + "F68\n" + "N5\n"
            + "R90\n" + "W3\n" + "L90\n" + "W3\n" + "L90\n" + "S1\n" + "L90\n" + "S4\n" + "W3\n" + "F56\n" + "N4\n" + "R90\n"
            + "E3\n" + "W1\n" + "L90\n" + "E4\n" + "N3\n" + "R180\n" + "E1\n" + "S1\n" + "W2\n" + "R90\n" + "N3\n" + "F82\n"
            + "N2\n" + "F37\n" + "S3\n" + "L180\n" + "E2\n" + "L180\n" + "F6\n" + "N2\n" + "F96\n" + "E2\n" + "R180\n" + "E2\n"
            + "W3\n" + "R90\n" + "E2\n" + "S5\n" + "S1\n" + "F23\n" + "R90\n" + "W5\n" + "F75\n" + "S1\n" + "L90\n" + "S3\n"
            + "E1\n" + "F83\n" + "W4\n" + "L180\n" + "W5\n" + "L90\n" + "N1\n" + "E1\n" + "S2\n" + "F17\n" + "L90\n" + "S2\n"
            + "F53\n" + "R90\n" + "S3\n" + "N3\n" + "W1\n" + "N4\n" + "L180\n" + "L90\n" + "E3\n" + "F9\n" + "S5\n" + "F24\n"
            + "W3\n" + "E5\n" + "N2\n" + "F73\n" + "N1\n" + "F28\n" + "N2\n" + "W4\n" + "N3\n" + "F53\n" + "E5\n" + "F47\n"
            + "W2\n" + "F60\n" + "L90\n" + "E2\n" + "F19\n" + "S1\n" + "F63\n" + "W5\n" + "F100\n" + "N3\n" + "L180\n" + "F83\n"
            + "N4\n" + "W5\n" + "F37\n" + "S1\n" + "F50\n" + "E1\n" + "N2\n" + "W3\n" + "R90\n" + "F85\n" + "S4\n" + "F72\n"
            + "N4\n" + "L90\n" + "F48\n" + "R90\n" + "F99\n" + "R90\n" + "F58\n" + "W3\n" + "W4\n" + "F64\n" + "E1\n" + "R90\n"
            + "F74\n" + "L90\n" + "F23\n" + "N3\n" + "N3\n" + "E1\n" + "S1\n" + "W5\n" + "L180\n" + "F98\n" + "L90\n" + "F36\n"
            + "W4\n" + "S2\n" + "W3\n" + "F9\n" + "F72\n" + "W5\n" + "F78\n" + "N2\n" + "F65\n" + "S3\n" + "F47\n" + "S5\n"
            + "R90\n" + "F68\n" + "L180\n" + "W2\n" + "F7\n" + "E2\n" + "E3\n" + "S4\n" + "R90\n" + "N2\n" + "L180\n" + "W2\n"
            + "R180\n" + "E4\n" + "R90\n" + "W3\n" + "L90\n" + "E4\n" + "F54\n" + "L180\n" + "E2\n" + "F6\n" + "W5\n" + "F82\n"
            + "E4\n" + "R90\n" + "E4\n" + "F25\n" + "N2\n" + "R270\n" + "N4\n" + "F18\n" + "N5\n" + "R90\n" + "S3\n" + "R90\n"
            + "F38\n" + "R90\n" + "F97\n" + "W4\n" + "F85\n" + "S4\n" + "F56\n" + "E4\n" + "S1\n" + "F40\n" + "W3\n" + "F52\n"
            + "L90\n" + "F76\n" + "N4\n" + "F15\n" + "S2\n" + "F22\n" + "S5\n" + "L180\n" + "F91\n" + "L180\n" + "F8\n"
            + "L90\n" + "E4\n" + "N4\n" + "F67\n" + "L90\n" + "S3\n" + "R180\n" + "R90\n" + "N4\n" + "F71\n" + "W3\n" + "F34\n"
            + "E2\n" + "N1\n" + "F43\n" + "W5\n" + "L180\n" + "N5\n" + "W2\n" + "F42\n" + "R90\n" + "W3\n" + "F39\n" + "E1\n"
            + "S2\n" + "L180\n" + "N5\n" + "E3\n" + "N5\n" + "F28\n" + "E1\n" + "R90\n" + "S3\n" + "F40\n" + "L90\n" + "S2\n"
            + "S2\n" + "L90\n" + "W5\n" + "L90\n" + "F93\n" + "R180\n" + "W4\n" + "S4\n" + "W4\n" + "F100\n" + "S3\n" + "R90\n"
            + "E2\n" + "L180\n" + "W1\n" + "E3\n" + "S5\n" + "L90\n" + "F87\n" + "N1\n" + "R90\n" + "F3\n" + "R90\n" + "E5\n"
            + "R90\n" + "S3\n" + "F45\n" + "L90\n" + "S2\n" + "F42\n" + "R90\n" + "F95\n" + "L90\n" + "E1\n" + "N3\n" + "R90\n"
            + "F73\n" + "S3\n" + "E1\n" + "L90\n" + "S2\n" + "E3\n" + "L90\n" + "L270\n" + "F38\n" + "S5\n" + "R90\n" + "F42\n"
            + "L90\n" + "N1\n" + "F7\n" + "S3\n" + "F65\n" + "N2\n" + "F42\n" + "L180\n" + "W5\n" + "S4\n" + "E4\n" + "F65\n"
            + "S4\n" + "E5\n" + "F51\n" + "E4\n" + "R180\n" + "F70\n" + "R90\n" + "F28\n" + "N5\n" + "W5\n" + "N1\n" + "F96\n"
            + "L90\n" + "W4\n" + "S3\n" + "W3\n" + "F89\n" + "W1\n" + "L90\n" + "F75\n" + "L270\n" + "S3\n" + "R90\n" + "L90\n"
            + "F7\n" + "E2\n" + "F24\n" + "R180\n" + "S2\n" + "L180\n" + "F48\n" + "R90\n" + "F37\n" + "W2\n" + "R90\n" + "W4\n"
            + "L90\n" + "W3\n" + "F81\n" + "E4\n" + "N2\n" + "F39\n" + "E4\n" + "N1\n" + "W1\n" + "L90\n" + "F59";
}
