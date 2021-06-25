package cplir_c.advent_of_code_2020;

import java.util.Map;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.longs.AbstractLongList;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectSet;


public class Day24 {

    public static void main(String[] args) {
        countFlippedTiles(INPUT);
        countFlippedTiles(EXAMPLE);
        simulateTileExhibition(INPUT, 100);
        simulateTileExhibition(EXAMPLE, 100);
    }

    private static void simulateTileExhibition(String input, int days) {
        var              blackTileMaps = parseDirections(input);
        var              blackTileSet  = toLongSet(blackTileMaps);
        AbstractLongList updateList    = new LongArrayList(blackTileSet);
        AbstractLongSet  updateSet     = new LongOpenHashSet(blackTileSet);
        spreadUpdates(updateList, updateSet);
        simulateTiles(blackTileSet, updateList, updateSet, days);
        System.out.println(blackTileSet.size() + " tiles are black after " + days + " days.");
    }

    private static void simulateTiles(LongOpenHashSet blackTileSet, AbstractLongList updateList, AbstractLongSet updateSet, int days) {
        for (; days > 0; --days) {
            if (days > 90 || days % 10 == 0) {
                System.out.println((100 - days) + " " + blackTileSet.size());
            }
            var oldBlackTiles = blackTileSet.clone();
            var updateArray = updateList.toLongArray();
            updateList.clear();
            updateSet.clear();
            for (long position : updateArray) {
                var east      = (int) (position >>> Integer.SIZE);
                var northEast = (int) position;
                var neighborCount = countNeighbors(oldBlackTiles, east, northEast);
                if (oldBlackTiles.contains(position)) {
                    if (neighborCount <= 0 || neighborCount > 2) {
                        blackTileSet.remove(position);
                        addUpdate(updateList, updateSet, east, northEast);
                        updateNeighbors(updateList, updateSet, position);
                    } else {
                        // otherwise the black tile survives
                    }
                } else {
                    if (neighborCount == 2) {
                        blackTileSet.add(position);
                        addUpdate(updateList, updateSet, east, northEast);
                        updateNeighbors(updateList, updateSet, position);
                    } else {
                        // otherwise the white tile stays dead
                    }
                }
            }
        }
    }

    static byte countNeighbors(LongOpenHashSet oldBlackTiles, int east, int northEast) {
        var neighborCount = checkForNeighbor(oldBlackTiles, east + 1, northEast);
        neighborCount += checkForNeighbor(oldBlackTiles, east + 1, northEast - 1);
        neighborCount += checkForNeighbor(oldBlackTiles, east, northEast + 1);
        neighborCount += checkForNeighbor(oldBlackTiles, east - 1, northEast);
        neighborCount += checkForNeighbor(oldBlackTiles, east - 1, northEast + 1);
        neighborCount += checkForNeighbor(oldBlackTiles, east, northEast - 1);
        return neighborCount;
    }

    private static byte checkForNeighbor(LongOpenHashSet oldBlackTiles, int east, int northEast) {
        var position = (((long) east) << Integer.SIZE) | (northEast & 0xff_ff_ff_ffL);
        if (oldBlackTiles.contains(position)) {
            return 1;
        }
        return 0;
    }

    static void spreadUpdates(AbstractLongList updateList, AbstractLongSet updateSet) {
        for (long position : updateList.toLongArray()) {
            updateNeighbors(updateList, updateSet, position);
        }
    }

    static void updateNeighbors(AbstractLongList updateList, AbstractLongSet updateSet, long position) {
        var east      = (int) (position >>> Integer.SIZE);
        var northEast = (int) position;
        addUpdate(updateList, updateSet, east + 1, northEast);
        addUpdate(updateList, updateSet, east + 1, northEast - 1);
        addUpdate(updateList, updateSet, east, northEast + 1);
        addUpdate(updateList, updateSet, east - 1, northEast);
        addUpdate(updateList, updateSet, east - 1, northEast + 1);
        addUpdate(updateList, updateSet, east, northEast - 1);
    }

    private static void addUpdate(AbstractLongList updateList, AbstractLongSet updateSet, int east, int northEast) {
        var position = (((long) east) << Integer.SIZE) | (northEast & 0xff_ff_ff_ffL);
        if (updateSet.add(position)) {
            updateList.add(position);
        }
    }

    private static LongOpenHashSet toLongSet(ObjectSet<Object2IntMap<String>> tileMap) {
        var longSet = new LongOpenHashSet(tileMap.size());
        for (var path : tileMap) {
            var east      = path.getInt("e");
            var northEast = path.getInt("ne");
            var position  = (((long) east) << Integer.SIZE) | (northEast & 0xff_ff_ff_ffL);
            longSet.add(position);
        }
        return longSet;
    }

    static final Pattern                          LINE      = Pattern.compile("\\R+");
    static final Pattern                          DIRECTION = Pattern.compile("[ns]?e|[ns]?w");
    static final Object2ObjectMap<String, String> OPPOSITE_DIRECTION;
    static {
        OPPOSITE_DIRECTION = new Object2ObjectOpenHashMap<>();
        OPPOSITE_DIRECTION.put("nw", "se");
        OPPOSITE_DIRECTION.put("ne", "sw");
        OPPOSITE_DIRECTION.put("w", "e");
        for (Map.Entry<String, String> entry : new ObjectArrayList<>(OPPOSITE_DIRECTION.object2ObjectEntrySet())) {
            OPPOSITE_DIRECTION.put(entry.getValue(), entry.getKey());
        }
    }

    private static void countFlippedTiles(String input) {
        var blackTiles = parseDirections(input);

        System.out.println("the directions told of " + blackTiles.size() + " black tiles:\n" + blackTiles);
    }

    private static ObjectSet<Object2IntMap<String>> parseDirections(String input) {
        var blackTiles = new Object2ObjectOpenHashMap<Object2IntMap<String>, String>();
        var lines      = LINE.split(input);
        var matcher    = DIRECTION.matcher(input);
        for (String line : lines) {
            matcher.reset(line);
            var path = new Object2IntOpenHashMap<String>(6);
            while (matcher.find()) {
                var matched = matcher.group();
                // System.out.print(matched + " ");
                var count = path.getInt(matched);
                ++count;
                path.put(matched, count);
            }
            simplifyPath(path);
            path.trim();
            // System.out.println();
            // System.out.println(path);
            if (blackTiles.remove(path) == null && blackTiles.put(path, line) != null) {
                throw new AssertionError(blackTiles + " " + path);
            }
        }
        blackTiles.trim();
        return blackTiles.keySet();
    }

    private static void simplifyPath(Object2IntOpenHashMap<String> path) {
        // remove one of the three axis
        // I picked the north west axis to remove
        {
            var nw   = path.removeInt("nw");
            var se   = path.removeInt("se");
            var nwse = nw - se;
            if (nwse < 0) {
                // southeast
                var sw = path.getInt("sw");
                var e  = path.getInt("e");
                sw -= nwse;
                e  -= nwse;
                path.put("sw", sw);
                path.put("e", e);
            } else if (nwse > 0) {
                // northwest
                var ne = path.getInt("ne");
                var w  = path.getInt("w");
                ne += nwse;
                w  += nwse;
                path.put("ne", ne);
                path.put("w", w);
            } else {
                // do nothing because they cancel
            }
        }
        // now cancel the remaining axis
        if (path.containsKey("sw")) {
            var sw = path.removeInt("sw");
            var ne = path.getInt("ne");
            if (ne != sw) {
                path.put("ne", ne - sw);
            } else {
                path.removeInt("ne");
            }
        }
        if (path.containsKey("w")) {
            var w = path.removeInt("w");
            var e = path.getInt("e");
            if (e != w) {
                path.put("e", e - w);
            } else {
                path.removeInt("e");
            }
        }
    }

    protected static final String EXAMPLE = "sesenwnenenewseeswwswswwnenewsewsw\n" + "neeenesenwnwwswnenewnwwsewnenwseswesw\n"
            + "seswneswswsenwwnwse\n" + "nwnwneseeswswnenewneswwnewseswneseene\n" + "swweswneswnenwsewnwneneseenw\n"
            + "eesenwseswswnenwswnwnwsewwnwsene\n" + "sewnenenenesenwsewnenwwwse\n" + "wenwwweseeeweswwwnwwe\n"
            + "wsweesenenewnwwnwsenewsenwwsesesenwne\n" + "neeswseenwwswnwswswnw\n" + "nenwswwsewswnenenewsenwsenwnesesenew\n"
            + "enewnwewneswsewnwswenweswnenwsenwsw\n" + "sweneswneswneneenwnewenewwneswswnese\n"
            + "swwesenesewenwneswnwwneseswwne\n" + "enesenwswwswneneswsenwnewswseenwsese\n"
            + "wnwnesenesenenwwnenwsewesewsesesew\n" + "nenewswnwewswnenesenwnesewesw\n"
            + "eneswnwswnwsenenwnwnwwseeswneewsenese\n" + "neswnwewnwnwseenwseesewsenwsweewe\n" + "wseweeenwnesenwwwswnew";
    protected static final String INPUT   = "seesweseseeeeeeeeeenweeee\n" + "wswneseseseswseswseseseseswsesesesesese\n"
            + "senwnesenenesenwseseswnwnwwnewnenwnew\n" + "neseeseseseseseseseseseenwseseeseswe\n" + "neswnweweeeeeeweese\n"
            + "nenenwnenwnenenenenenenenenenenesenwne\n" + "sesesesesenesesesesenwseseseseseewee\n"
            + "eeeeesesweeweseeeeneeeeenw\n" + "neswwnesenenenenenwenenenenesenesenenwne\n"
            + "wseseeseenwsesesesesesesesesesesesese\n" + "newneeneeseenweenenee\n" + "eeeneeeeneneneneneswneeneenewenw\n"
            + "nwewwnwwwwnwwwwwswnwwnwwewnww\n" + "wnwswswwsewnenwnwnweneswnwseenenwnwwnw\n"
            + "enwnwseswnenenwnwnwnwnwnenwnwnenwnwnwnwnw\n" + "swsesenewnwsenewseneswswswseseswswsesw\n"
            + "seseswseseneeenwwseeseseswse\n" + "sweeeeneneweeeeeeeneeseee\n" + "newwwswwnwsewwsewsww\n"
            + "wwswsweneswewseswswswswnwnesweswnesw\n" + "neneneneeenewnwnewsweeneeseeneese\n"
            + "swwwswswsewseseswseswenesewseseene\n" + "nweeesewwwsweseee\n" + "wwnwwnwnesenwsenwnwsenwwnwwnwnwswsenene\n"
            + "esesenwseswnwwseneeseenwswswswswsesese\n" + "wswswswswswnwswnweswnwswnwswswseswswese\n"
            + "swswnwneswswswswswswsweswswswswnewswswsw\n" + "seneseeswswnwswseswseseseseswswsenesew\n"
            + "swseseseseseseseeswsesesesenwsesese\n" + "seswswseseswswenenwswseswseseswnwsewnwse\n"
            + "seswsesesesesesesewseneseneswseseseswsese\n" + "sesenewnenenewneneneneenenenenenenwnene\n"
            + "wnenwenwneswsewsesenwwewwswwswe\n" + "swwseswswswswneseswseseswneswswsesesewse\n"
            + "nesenenenwneneeswnewneneswnenenenenenene\n" + "esesewseeneseneseweenwnwneseeswewe\n"
            + "nwsewwnwnwnwnwnesesenww\n" + "nwnwnwnenewnenwnwnwnwnwsenwwnwnwnwenwnw\n" + "seenenewneeneneneneneneeewneneew\n"
            + "wseswwswswswswswswswnwswswwwneswewww\n" + "wsenwwnwseenwwwwwwwwwnwwwww\n" + "eenweeeeeeweswsweeene\n"
            + "newnwseseeeseeewenesewweseeee\n" + "neeseseeeswseeneswweeeseeseeee\n" + "seseeeseeseeeseeseeesesenwseesw\n"
            + "nweswseseeenenesesweesewsesesenw\n" + "seswnwnwneseneneswwnenenwewnesesesenwnw\n"
            + "nwneeeneneneneenenewsweneeeeswne\n" + "neswsenwwnwneswnwneswswneswenwnenwnw\n"
            + "swnwnenwwnewswwnwnwswwnwewwwnwnw\n" + "nenenwnenenenewneneneneseneneweenww\n"
            + "seneneneneneeneswnenenweewnenene\n" + "eeesenweweneeeseese\n" + "eneneeesewnenewenewnewseseneee\n"
            + "nenenewsenenenwwnenwneseneenenenwnesee\n" + "wswsewwwwwwnwwneswwswwwewwwsw\n"
            + "wswswswwswwwswwwewswswwseswnew\n" + "seenwwseseseseesesenwsesesesesesesewne\n"
            + "wswsenwwesewnwnwwnenesenwwseewnenww\n" + "senwwsenwswseseswswnwswneswswswswswsenwe\n"
            + "swneseseeseeeeesewnweeeeesee\n" + "seeseeweeseseseeeseeseseeenee\n" + "eseswneseseseseswwsesesenesesewse\n"
            + "eeneeewneeeeeenweeeeseenese\n" + "eeseswenwnwesewnwnwese\n" + "seswswwswswswswswswswswswswenwseseswsw\n"
            + "enwwnwewwnwswneswnwwnwnww\n" + "nwwnwenwnwwnwwnwnweenwnwwsenwnwnwnw\n" + "senwnesewwwnwwswsesesenwsenwwenwnwne\n"
            + "eswswswswwneseswseswswswseeswnesesewsww\n" + "wwwwwwwnwnwwwwwnesenwnwsenwwww\n" + "nwnwwenewnwnenenwnwnenwsenw\n"
            + "wwwwwnwwwwsewwsewnewwnwewww\n" + "wwnwwwnwwwwwwweswwwwwww\n" + "nwwwwwwnwwwswnwwwesewnwnwwew\n"
            + "wseweswswswswneswswsw\n" + "nwneswneenenenwwnweneneneneneeesesene\n" + "nwwwnwnwnwsenenwwnwnwswnwwwnenwnwse\n"
            + "ewwsenwwnwenwenwewwneswwsenwwsw\n" + "esesenwswswseseswsenesesewswseseseswse\n"
            + "nenwnwnenwnwneswnenwsweneseenenenenenwnw\n" + "sewwewwwwwwwwswwwwnewwsw\n"
            + "swswswswswswswswswswswswswswswneswww\n" + "swseseswnwewwswseeswnesweswseswwswswsw\n"
            + "wnwneseswenweneswswweneeseneenenw\n" + "wswseseenwsesenwseseewnesesesesesese\n"
            + "eseseewwnwsweneswnwswswwswsesewswesw\n" + "neeneneeswneswswwenwwwseneneneene\n"
            + "neneswnwsenwswnenwnenwnwnwswnw\n" + "neeenweeeneeneeesweeneeeene\n" + "nweeeeeeeeneeeneweenweeswsesw\n"
            + "swwwnwswweswnwwwewnewwwseswwwsw\n" + "seseseseseswsesesesesenese\n" + "eswneswneewswnwswswswewnweeswwse\n"
            + "seswseswswsenwneswnwseswswswseswnwnesesesw\n" + "eeweeswneenweeeneneeeneswseene\n"
            + "nenwnwswnwnwenwnwenenwwnwswnenwnenwnwnw\n" + "enwseeneneeeeweswneneenewnee\n" + "enewsewwwwneewwwswswwnwwew\n"
            + "eesweenwnwnwseeesenwseswneseseseswe\n" + "weseneseeseewnwwswnwneswswsenesewnw\n"
            + "nwnwsenwnewwwnwsenwnwnwwnww\n" + "nwnewnesweneneneneneswnenenwnenwenwenwnw\n"
            + "eswswwwswswnwswswswswwswenewswsww\n" + "neeneenenenwswnewwsweswswneneenene\n"
            + "nwseswenwnwsenwenwwswesewsesweesw\n" + "nwnweeswswnwnwswewnwnenwnwswenenew\n"
            + "swseenweswnesenwswwnwnwwnwnenwswsenenw\n" + "eeeeeneseesewesenwnweneeneenee\n"
            + "seseseseenweseeseseeswseseeseseenese\n" + "senwwwswswswswswwswswwswswenwswwswsw\n"
            + "eeneeneneeeneneneeeesenewneeneew\n" + "wnwnwwnwswsweeseswswswswswwwwswe\n"
            + "sesesesenewseeseseseseseseesesesewse\n" + "nenwnwenwnwneswnwnenwenww\n" + "nwswswswsewwneswswwwswneenewswwwwsw\n"
            + "eswenwsweeswswnwnwenwewewswnene\n" + "neesweneseeeeeneeeseeeeswnwesw\n" + "seseeneeweeweeeeseeeewee\n"
            + "nwnwnwnwnwnwnwnwnwnwnenwsenwnwnwnwnw\n" + "nenwnwwnwnwnenenwnenwnenenenwseswnenwnwnwse\n"
            + "wwnesenwneneeneeseneewswnwneneeese\n" + "senwnwnenenwneswnwnenweswweweneseswse\n"
            + "nwnwnwwnwnwswnwswnwwnenwnwnwwenenwwnw\n" + "eswnenenewnenenenesenenenwseewnenwnwsw\n"
            + "wsenenwwwwnwnwwwnwwnwnwwnwnwww\n" + "seswseswsenwswswseseswseneswsese\n"
            + "swswswswswswswneswwswnwseseswswswswswsesw\n" + "enwneswswswswnenenenenenenenwneenenene\n"
            + "wsesewswwwwwwwnwnwwwswwweww\n" + "enwwwsewwwsewwnwnwnwwwnenwww\n" + "eeeeeneswsweeeeeeeeenwee\n"
            + "nwwnewnwwswewnwnwnwnwsenwwnwsenwse\n" + "nwnwsewwwnwwnwseenwswswenwenenwnwwnw\n"
            + "seswswswswseswswseswswswneneswswswswswswsww\n" + "wswnwseswewwwswwwswswswwwswwww\n" + "wwnwwnwwwwsenwswenwe\n"
            + "seseseseswseseswnesesw\n" + "swswswswswseswneewnwswswswswsw\n" + "seswneesewseneewswseswwnewsenesesewnw\n"
            + "nwswwenwnwswneneesenewnwswnenw\n" + "seeswswswswswswwswswswswwneenwswswe\n"
            + "wswwseswnenwwseswneneeswsesweseene\n" + "eseseeneewseseseewseseeseeseesese\n"
            + "senwnwnwnwswswwneneenwnwnenwsenenwswnese\n" + "wnewswnwesewwneswsewswwsewwswnw\n"
            + "sewnwnwseseeewnwesenwwswnwwwsenwww\n" + "swenenwswnwnenwseswnwnwnenwnwnenwnwnesew\n"
            + "wswenewwwwnwnwenesweswweswseswsw\n" + "eseseeeswwnesesesesesewnweenweseswne\n"
            + "nwnwnwnwnwnwenwnwnwnwwnwnwnwnwwswew\n" + "eenwweneeeeeneneeeesweeesee\n" + "newsewwwwwwwwwwswwwnewwwew\n"
            + "wsewnewwwwewswswnwwnwwwneww\n" + "nenenwneseneenwsewneneswneewwnwnwnw\n" + "nenenenenenenenenwswnwseneneewnwne\n"
            + "newnenwnwneneenenenwnenw\n" + "swseseseneseswswswswswnwswswswwseswswseese\n" + "sewwwnewnwwwwenewwnwwwswwwnwse\n"
            + "weswseseseseeseseseseswseseseesewwsw\n" + "swesenenesweseeswneseeswnwseesesee\n"
            + "wswwneneseeeneeeseseseneswsenwswwne\n" + "wenewseswsenwnenenwnewnewsesenwnew\n"
            + "swswsweswswswswswswswswswnwswswwswswe\n" + "seenwseswswenwwsesenwseneswenw\n"
            + "nwswswswswswseswswnwseneswsesweswwsesw\n" + "nwseswseswsenwnesesesweenewnwswwswse\n"
            + "swswswseswsewnweswsesesesee\n" + "eeneenesenenwseeneneneneneneneswnenw\n"
            + "swweswsewswnwswwswwswswswswswswswsw\n" + "swswseswseswswswswswsene\n" + "enewswwsesesewnweeneeswwswesene\n"
            + "seeeeeseweseenweseseseseesenese\n" + "swwswnwneswnesenwnenesenesene\n" + "seeseneswwswewnwseeeeesenenwwnene\n"
            + "nesesenwswswnwswnwnewnwwsenenewe\n" + "neewneeseeeweseeswenwseseeeee\n" + "nenesewneseneneneewwnenenenwwe\n"
            + "swnenwnenwnwnwnwnenwnwnenwnwnenwswnwnwe\n" + "wswneswseewseneswswenwseswnwswneswwsw\n"
            + "neneeeeneeneneenweseneeeneneneew\n" + "seneswwwwwsesenwwnewnenwenwnwew\n" + "enwwnwnwnwwwnwwwwsenw\n"
            + "wnewwwwnewwsewwwwsewwsweww\n" + "nwwwnwsenwnwnwwwwnwwwnwewwnwww\n" + "swswwswswseseswwswwswneswnwswswwwswe\n"
            + "nwenenesweswneenesenwenw\n" + "eenwenesweeneweneeweneseeswsee\n" + "enwneneneeswnwnewneseeeeswnwewee\n"
            + "neneewnenewwnenenesenenesenene\n" + "nwnenwnwenwnwsenwnwnwnenwnewnenwseewnw\n"
            + "ewwsewwswewwnwneeseneswwsweewne\n" + "wwnwwnenwswnwwnwnwnwnwwnw\n" + "nwnwswnwnwnweswenwnwswnwnwenwwnwnenw\n"
            + "swwnewnwwnwswseswwsweewewwwswwsw\n" + "seswswswsewswseseswswneswswswwseneswwne\n"
            + "seseesesesenesesesweseseswesewnwenwe\n" + "swenenwnwnenwneneneneswnenenenwnenwnw\n"
            + "nesewnewsewnewenwswsenwneeneswnwswsee\n" + "nenwsenwnenwseseswnenenwwnwnwnwswnenene\n" + "seswwwwswwswnwwsw\n"
            + "eeeesweeseseneseeeenenwsesesew\n" + "nwseseseswseswseseseseseeseweseneseswne\n"
            + "neneseswenewswenwswswneseswnwwswnwene\n" + "nwnwnwwwewwnwnwnwnewnwwswnwwwsww\n"
            + "swwwwswwswswsewneswwswswswswwww\n" + "wwswnwwswswswnwswswswswswwwswwwese\n"
            + "nwnwnwwnwswnwnwnwnwwnwnweenwnwnwnwnwnwnw\n" + "nenwnenenenwnwnwnenenwswnwnwenwnwnwne\n"
            + "seneneneneneneneneenenenweesweneenenwne\n" + "senwenwenwsenwnwnwwnewnenwnwsenenenw\n"
            + "esenweesesesenwsenesweneeesewswnw\n" + "neneenewneeeneeneneneneneneneeswnee\n"
            + "senwwwneseseseneesesewseseseseeesew\n" + "nwwnenwnwnwwnwsewesewwwnwnwwsww\n" + "seseseseseswsesenese\n"
            + "neseenenwweswnwnewwsenenwnwwnenenw\n" + "nwwwwwwwwwnwnwwwsesewnwwwnw\n" + "nweeeeeeeneeswesewsesewneese\n"
            + "nwnwenwwseeneeswswwnwnenenesenenwnwswne\n" + "swsesenwesesenweeeeeseseseesweesese\n"
            + "swswswwsenwswwsweswswswswwwswswswswsw\n" + "swswnwseseseseswseeseseesenwneseseese\n"
            + "nwnwwsewnwnwwnwnwnwnwwwwnw\n" + "nwswswwswswsweswswswwswwswsewswwnw\n" + "swnwwseewswnwwswswwsw\n"
            + "nwwnweweseswsewneneswnwswsesenenenwsw\n" + "nwneeswneneeeswwswnenweneneneneene\n"
            + "nwnwnenwnenenewnwenesenwnwnenenwnwnew\n" + "seseseseseswwseseseseneswsesesw\n"
            + "nenenwneneewnenenwnenwswnwwsenwnwenew\n" + "eneneneseneeneneswseenwnenewswswenenwnw\n"
            + "newwswwswwseswenwwwnwnewsenwneew\n" + "nwwwnwswnwnwwsenwnwnenwswnwenwnwnwenw\n"
            + "neeneeneseseeneeeneewewewewe\n" + "swsweseswseneswsenesenenwswene\n" + "wswneeswseseneeesenweseeesesesesesee\n"
            + "nenwnenwnwnwnwnwneeswnenwnwnwnwsw\n" + "swswneswnwnweswnwswwswnweswsweswee\n"
            + "nenenenwnenwnwnenenenwswnwnwnwnenenwsee\n" + "wswseseneswwwswwswnwswswswswswswwswsw\n"
            + "newnwsesesesewseeneseesesenesewswnw\n" + "senwnwwwswnwnwnenwnwnwwnwnwnwnwnwwnenwnw\n"
            + "nenwnenenenwnwnwnewenwnwsenwnwnenwnww\n" + "nwnwwwwwwnwwsewnenwwwsewsewww\n" + "nwnwwnwwwnwwswwnwwwenwnw\n"
            + "swwnwswnwnwnwnenwewseswnwswwneneww\n" + "sewseseseeeseewnwneeweesenesese\n"
            + "swswwneswnwswswswswwswneswswsewswwwse\n" + "neswswwseswswswseeswswneswwswneseswsw\n"
            + "swseswseseeswsesesesesenewsewseswseswswse\n" + "seeenweeeseeeeenwsesenwnwseee\n"
            + "esesewseswswswswseswseswswnwnwseswsene\n" + "wwwnwwwnwwwnwnwnwnwnenwnwsesenwnwwnw\n"
            + "swswsewswseswneswswswseeseswswseswswsw\n" + "newseeseeeseseeneeeeseeeewe\n"
            + "neswneswwenwesweneenenesenenweeesw\n" + "neeeneneneneeeeweneneneeeswnene\n"
            + "eseseswseseseseseneseeneseswenwwsese\n" + "neneneneneeneneenwneneneneeneneswswenw\n"
            + "senwnewnweneeneswseneswwenenewnene\n" + "neseeewneenesesewsenwswsenwswsenese\n"
            + "nwswnenwnwnwwenwsesenwwsewnwenese\n" + "swswwswneswwswswwswswswwenwswwwsw\n"
            + "nwneneneneneeneneswneeswnenenenenenenenene\n" + "neseswswswseseneswseseswswseswswswswsenwse\n"
            + "neswnenenesweswswnenenwneneenwsenwnene\n" + "swseseseswswwswswwweneswneesewwne\n"
            + "enwnwnwnwnwnwnwwenwswnwwnwwnwenwnwnw\n" + "wwwswwswwwwwswweswwswwwee\n"
            + "seneeeneeneneneswneswnwenwnenwnwswswse\n" + "nwnwnwnwnwnwwnwnwnwnenwnenwenwnwnwswnwnw\n"
            + "wwseneneseneswnwneeneneswne\n" + "nwwenewswnwsewswswewnwnwnenwwnwwwe\n"
            + "sesesesenwseswsesenesesesesesesesesesese\n" + "swenweseeeeesesenweseeseneseswsee\n"
            + "swenwswseneswwseswenwneswswwswnwswe\n" + "wswnwwwwwwwwwnwwewswwwwwwe\n" + "eseseweneeseeeswseeeseeeseenese\n"
            + "swneswnesweeewnweswnwswnenwswswswsww\n" + "nwnwnwseswnwewnwnw\n" + "neneswnweeeeneeeneneewneneneenene\n"
            + "eseseweseswsenwswesesenwsesewesenewsw\n" + "sesesesesewnenwwswsesesweswsesewee\n"
            + "swsewswseeswnwswseswswswswswswswswswesw\n" + "sewnwnwswnwwnwnwnwneswnwnenwnwnwewnwnw\n"
            + "wnwswnenenenenwneneenene\n" + "nwwneswswneenwnwenwnwnenwnwnenenenwnenw\n"
            + "newswswwsweenwnesenwnwsenesweeswsw\n" + "nwnwnwnwnwnwnwnwnwnwsenwnwnwnwsenwnwnwnw\n"
            + "nwnwwnwseenwnwwnwnwnwnwwnwnwnwwww\n" + "wwnwseeeneenweswswwenenesewsesenw\n"
            + "swswsesesenwseeswswswsesenwswseseseseswsw\n" + "wwswwwnesenewwwwswwwwnwneww\n"
            + "seseseswnwswswswswseswswswseneswnwsesw\n" + "weswwswwwewwenwswwwwwnwwnwww\n" + "eeeeeneweeeneeeeewseewee\n"
            + "seseseswneseseseneneneseseseswwsewsesw\n" + "neneneneneneneneneeeneneneswneewseenw\n"
            + "wnenwnwwswwnwnwewwwwnwnwswwwww\n" + "swenwnwnwnenwnwnenwnenwnwnenwnwnwnwnwne\n"
            + "eeenesweeeenweneeneweseeeswne\n" + "ewnweeeswwseneneene\n" + "neneswneneneneneneswnwenenenwnw\n"
            + "sewwwwenwnenwwwwwsewwwwnwse\n" + "wwwsesesewwnesenenwnewnwwsewwsewne\n" + "swswwwnewwswwswwswnewwwwwswsww\n"
            + "seseneweweswneneswseeeeswwenwnwne\n" + "eseseseesesweswseseseseeseneeeenw\n"
            + "neneneeeenewnenenenenwneswneneneneenene\n" + "nwewewwwsenewswsewswsenenewswwsene\n"
            + "neneneswnenwnwneneeeneneneeneswnenene\n" + "enenenwnwnwnwnwswnenwnwnwnwnenenenenwnw\n"
            + "wnewswwwswswswwswswswswswwww\n" + "swswswswneswswsweswnwswswswswswneseswsw\n" + "eeeeeeeeenweeeesw\n"
            + "seseswseseswsesesesesewsesene\n" + "wwwwnewswnwsenwwwwsewnewwwww\n" + "newsenenenenesenwenwneneneneenewnenesw\n"
            + "swswswswswswswswnweswswnesenwswswswswsesw\n" + "newnenenenenenenwesenenewswswnesene\n"
            + "wswnwnwnwnwseswenwseeenwnw\n" + "eewseseseeseseseneseeeneewsewe\n" + "nenenenenenenesenwenwenewnenenenwnenesw\n"
            + "wsenwsewnwwwenweseswnwnenewnwswwnw\n" + "seswenwswswswswswnwswswswswswswswswsesw\n"
            + "nwwnwwnwwnwnwwwswnwnwwenw\n" + "neenenenenenwenwwnenwnwnenewnenenene\n" + "eeeeneeneenwnewseeeneeeseene\n"
            + "esenwwnwsenwnwnwnwnwnwnenwnenwnwnwnwswnw\n" + "eeeseeseeeseeseeseeesesewsesenew\n"
            + "wweeswnewwnwsewnwwwwwwseewsw\n" + "seswwwwewwnwewswwswenwwwnene\n" + "eseeeseseseewseseeseeeesenesese\n"
            + "eenwesenwweeneswwnewneeeesenee\n" + "eeeneeneneswseneenenenenw\n" + "seseseswswseswseswesenwweswneswseswse\n"
            + "nwsewneenenwnenenenenenwnenenenewnenene\n" + "nwnenwnwnwnwnenwnwnwswswswnwnwnwnwnwnwe\n"
            + "neswnwnwnwenewwwnewwnwswswesenwwne\n" + "nwsewnwwnwnwwsenenenenwnwswwnwswnwnww\n"
            + "eeeeeeweeeseeseneeeenweene\n" + "seswswwswwwwwwswneswswswwnwswwwsw\n"
            + "nwnwnwnwwnwnwnwnwnenwnweswnwnwnwnwnwne\n" + "swweswswswswswwswswswswswwswwnweswwsw\n"
            + "nesweewnwswnenwnwnenenwnenwseeswnene\n" + "neseseseeeeeeswsesewseeneeeesese\n"
            + "swnwswseswneswswswswswswswswswwswseswsese\n" + "swwwenwnwnwwnwsenwwnwnewnwwwwnwnww\n"
            + "senwseseseesesesesesesesesese\n" + "senwnwnwnesewnweenwwnenenwswswnwswnenwnw\n"
            + "enenweenwnwweseesweeseseeesw\n" + "swseswwswenwwswsweswswswsweswweswsw\n"
            + "nenwnenenenewseswnesenwnwwneweeneese\n" + "esesesesesewseneseseseseseseswsesesese\n"
            + "wewnwnewsewwwwwswswwwwseenwew\n" + "nweeenwswsenwnewwewwnwswnwwnwnww\n"
            + "sewnwwnwwnwnesenewnenesenwnwseeenwne\n" + "enenewseeeeeneeeenene\n" + "eeeeeneeneeesenweeeeneeeesw\n"
            + "neeswwseewwneewswswnenwwswwwww\n" + "wswwswwwseswswwswswneww\n" + "nwnwnwwnenenesesesenwnwwwnwsewnwnwwnw\n"
            + "swneneenwswenwnwweswwwwnweesewse\n" + "nwnwnwnenwnwnwnenwnwnwnwnenwnwwnwesenenw\n"
            + "wswneswswwswswwneewwnwnwweseewne\n" + "swnenwsenwnwnenwnesenwnwnwnwnwnwnwnwnwnw\n"
            + "esweswsweeseeseswenwseenwenwnwnwwne\n" + "neswseeeeswsesenw\n" + "swnwswnwsesweswseswsweseswswswswswswsww\n"
            + "neswneweneneenwenweeswswnwweee\n" + "enwswnwenwnewswwnwnwnwnwesewnwnwesw\n"
            + "swswseswswswseseseseseseseswswswseweswne\n" + "eneesenweeeeeneeeenwneeswnenee\n"
            + "neneneneneenenwnenwnenenewsenenenenewne\n" + "neneseeswneneeneenenewneneenenenenw\n"
            + "seeswseseswseseeeenwenwseseeesenwee\n" + "nwnwswwnwneenwnwswsesenwnwseseeeswnw\n"
            + "nwesewnenwewseeneneeeewneeswenee\n" + "swseswseswswewenwswswneswswswnwnwseswse\n"
            + "eenweesesweeeseeweeseeeseeee\n" + "nenenenwnwnenenenewneneeswnenenenenenenw\n" + "sweesesenweseeeseeseeee\n"
            + "nwsenewenwnwnenenenenw\n" + "eneeweseneeeeeeeeeesweseese\n" + "nwnwnwwnwnenwnwwnwnwnwnwnwwsesewwnwnw\n"
            + "neswswswswseswswswswweswwswswsw\n" + "nwnenwewsenwnenwnwnwnwnwwnwnenwnwnwnwnwnw\n"
            + "seneesewsenesesesesesesesesesewsewsese\n" + "nenenweeneneenewwseeeenewseswnenene\n"
            + "nesesenwseeseseewneseseseewswseee\n" + "swwswswswswswwwwsweswswnwsww\n"
            + "sesewseseseseseesesesesewseesesesesese\n" + "swnwswwswwwsewseswwwneswwswwsww\n"
            + "seswswnewswswswswswswseswseeseseseseswsw\n" + "eeenwesesenwseseesesewweeseesee\n"
            + "swswswswswswswwswneswswseseswewswswsw\n" + "senwwnwnwnwnwnenwnwnwnwnwnwnwsenwnwwswenw\n"
            + "nweesweeeneeneeeneneeeewnenene\n" + "enwwwnwnwwnwnwwnwwnwnesenwsww\n" + "swseseneeeweesenwseeenewesewnenew\n"
            + "neesesesewseseesesese\n" + "esesenwseseswseesesesesesesesese\n" + "swswswswswneseseswswseseswswsenwswswsesww\n"
            + "ewwsesesewesenwnenw\n" + "wswwnwwnwwnenwnwnwsenwwwewnwnwnwnwnw\n" + "neseseeseweseseseseseesewseseesee\n"
            + "eeeneesenwesewnwsweweeeeenesw\n" + "neneeneeneeneeeeeeneewesenesw\n" + "eeneneeneneeneewneneseneneneenenwe\n"
            + "wnwnenenwnwnwsenenwnwnwnwnwneseewnwnw\n" + "sweswswswswswswwswnwswswswswsw\n" + "swneswwswwwswwswwwswswwswsw\n"
            + "wneewswneswswswseswwwnwwwwswswsenw\n" + "wneseneeeeneeeeeeeewneesww\n" + "swneseenesesweswseseneseseeenwsesese\n"
            + "nenwnwnesenenwnenwnwsenenwnenenwnwnenenwnw\n" + "eneneswneneneeeneene\n"
            + "neenewnenwnwnenenenwneswnenwnenenwnwnenw\n" + "wwwewwwwwwwsewwwnwwwwww\n" + "eeeeeewsesweeeeeeeenenwesw\n"
            + "wnwswwsewwswnesenesenewwwswwwesww\n" + "nesenwwwswwwsew\n" + "wnwnwneswneswnwnwnwnwnwnenwsenwnwnwesenw\n"
            + "nwneseenwwnenwnenenewnw\n" + "eseseeeesweeneeseeweeeseeese\n" + "swswseswseseseseseseswsesenewswswsenesesw\n"
            + "nwenenenenwneenenesweeeseseneneswnenww\n" + "wwsenesenwnenenweneneswnenwsenwwswe\n"
            + "nenenenesenenenenenenwwnenesweeneenenw\n" + "neswsenewnenenenewneeneseneenewnwneesw\n"
            + "swsweswswseswseswswswswseswsweswww\n" + "wnewwewwseswwwwwwwsenenwwsew\n" + "seswswnewswwwnewwweswwswwswswwsw\n"
            + "wnesweenenenwnwesewsweweeeswnw\n" + "wnwnwwswnwnenwwwnwnwwnwenwnwwnwnwnw\n"
            + "neswswseswwswswnwwsewseswnewwswswswsw\n" + "neesenwwseneswnwwese\n" + "wnwnwnwswnwwswnewwnwnewnwnwswnwenww\n"
            + "nwnenwswnwnenesweneneswnwneeswswnwnee\n" + "sesesenwseseseseseseswseseesesesesesewse\n"
            + "wwwwswewwwwnewwseswswswwswww\n" + "nenenwneswnwnenwnwnenenenw\n" + "eswwwnwnenwwwwwewswwwswnwwnew\n"
            + "wnwswwsweswswnwnwswseweswswswsweswww\n" + "swswswswswneswswswneswswswswswswswseswsww\n"
            + "wnwnwnwwwnwnwsenwewnwsenewnwnewsw\n" + "seesesesenwseswseswswsesenwseseswseswse\n"
            + "eneswsewesenwwneswwswseswswswwswswsw\n" + "seswnwenwsesewnwesesesesesweenenwe\n"
            + "eswseeweeswwsenwesenweeewnwe\n" + "seswnwnwewnwnwnwswnwenwnw\n" + "swwnwwswwwswwswsewswwwwwweswne\n"
            + "enwnwnwnewnwnwwnewwswwnwswsenewswsw\n" + "nenwnenenwnwnwnenewenenwewenwwswse\n"
            + "swewswwwswnewnwswnwseswwwswwwswsw\n" + "eeeeeseeewswswswnenwnweeneneene\n"
            + "nwswswswswswewseswswenwseswsweseswswne\n" + "neeeswneneneneneeneneneswnenwneeene\n"
            + "neenenenwnenenesweneseneeesewneseew\n" + "nwenenenenenwnesenesewnenenenenenewne\n"
            + "weseswwneesenewwsenwnwwsweswwsw\n" + "nwnwswwnewnwnwnwwwsenwwwswwneww\n"
            + "nwnwsenwnenenwnenwneenwnwswnewnwnenw\n" + "nenwneseswnwswwswwwnweseesesewnwsene\n"
            + "swswswswswswwsweswwswswwnwswswneswse\n" + "seswsenwsesweseseseseswseseseswseswswse\n"
            + "seswseseseseseseseseeseseswsenwswsesese\n" + "wwnenwwnwnwwwnwwwwnwnwwwwwse\n" + "seesesewsesesenwesese\n"
            + "swnenwwnwswsesweswnwnenesewswewwse\n" + "seseseswsewwwesesenwsesesweneswenw\n"
            + "senwnwnwwnwnwwnwnwswesenwnwwnwwnwe\n" + "wnwwwwwwwwnwwewwwwwwew\n" + "neeeneeenesenweeeeneneneeee\n"
            + "swwwseeeneenweeneeeenwseswnwswse\n" + "wwnwwswswswewseswswwswwswwnwwe\n" + "wswneeeeneewnwneneseseseeewwew\n"
            + "seseeseeswswseesewsenwesesenwse\n" + "swseneseseseseswswwesesenwswswseseswnese\n" + "wwnewwnwwswwnwwwnw\n"
            + "nwneswnwswswswswswseewswswswswswswesw\n" + "seeneseneseseweswwwsesenwwneseesee\n"
            + "wnwsenesesesesesesesenwseseseswsesese\n" + "sewwwwewswwwwwsewwwnwnw\n" + "wswswsewneeswswswswswswwswwswwwswsww\n"
            + "eeenenwwneneneneneweweneewneswnee\n" + "nwwnwsenwwwswwwwnwnwwnwnwnwnwnewnw\n"
            + "swswswswseswswswneswswswseswswswswswswnwsw\n" + "sewnenenenwnenenenenenenenenenenesenwnene\n"
            + "swnweswnwnwsenwnenenwswnwswweeeswenw\n" + "wnenenwneenwseswnwwswnwswwwswwnwne";
}
