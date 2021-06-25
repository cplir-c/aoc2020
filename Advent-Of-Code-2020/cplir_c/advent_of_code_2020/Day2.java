package cplir_c.advent_of_code_2020;

public class Day2 {

    static final String INPUT = "4-5 l: rllllj\n" + "4-10 s: ssskssphrlpscsxrfsr\n" + "14-18 p: ppppppppppppppppppp\n"
            + "1-6 z: zzlzvmqbzzclrz\n" + "4-5 j: jhjjhxhjkxj\n" + "7-8 s: tsszsssrmsss\n" + "12-13 m: mmmmsmmmmmmqm\n"
            + "16-17 k: kkkkkkkzkkkkrkkmfkk\n" + "6-7 x: jxxwxpx\n" + "3-7 w: wwwwwwwwwwwwwwwwwwww\n"
            + "4-15 h: gcxfgbpbghdtrkhn\n" + "3-4 g: tprznvggnfgpgtmzsrmr\n" + "16-17 c: cccccccccccccccwccc\n"
            + "6-7 s: ssssssmsss\n" + "3-4 l: flll\n" + "4-5 v: mvvvc\n" + "17-19 j: lwjsfvkjgjjmsjrsjjr\n"
            + "2-20 k: xhfhfzvghbkbngzbqcck\n" + "2-3 s: rssnsxslshrdtk\n" + "12-17 b: hbvctbbxbkbbjhbvbw\n"
            + "8-9 j: qjxxtnwjqpjj\n" + "14-15 b: bbbbbbbbbbbbbbbb\n" + "14-19 l: llllllllllllllllllml\n"
            + "6-7 f: krgfxsffffsffrbf\n" + "8-9 f: fffffffffff\n" + "16-17 c: cczccwccccccccccz\n" + "8-10 s: tlstjjwsms\n"
            + "8-12 f: fffffffftftxff\n" + "5-16 z: vxjlgzxckzqgwjdw\n" + "8-15 t: lltqlhtptgtsvmw\n" + "5-9 h: dhsnnlthw\n"
            + "7-9 d: hgbdzzppdscxt\n" + "5-8 g: ggdmfgzm\n" + "2-14 w: wwwwwwwwwwwwwwwwww\n" + "9-16 q: qqlqqqqkqjqqqqrq\n"
            + "1-5 p: lpdmptz\n" + "1-3 l: llql\n" + "10-16 x: xxxxxxxxnxxxxxxcrxx\n" + "6-8 h: lhhhhqhh\n"
            + "4-15 z: zzszzzzzzmtzzzzrz\n" + "4-5 c: zvcsz\n" + "7-8 f: zcpftffqfwfffcdf\n" + "11-15 s: lsssssssgssfssrh\n"
            + "18-19 p: pppwpppppppppppppppp\n" + "3-5 g: gzgggqg\n" + "4-6 n: nnfnktnnndqt\n" + "1-10 l: llllllllldfll\n"
            + "9-17 x: xxxxxxxxrxxxxxxxhx\n" + "13-16 c: cccccncccxccgwckcc\n" + "3-4 k: kkhn\n" + "4-14 m: mmmdmmmmmmmmmmmm\n"
            + "4-16 t: stwqdgdhlzlzlwqtjmj\n" + "1-6 x: xpbxxj\n" + "9-10 h: mhhhhhhhhh\n" + "1-3 q: qqqqdqqq\n"
            + "1-3 b: bbbb\n" + "6-8 g: gggggpgmv\n" + "10-13 f: ffxzhfldffvmc\n" + "5-9 w: xwwwnwwfw\n" + "5-7 d: gtdhdzp\n"
            + "2-14 k: kkkkkkkkkkkkkkkkk\n" + "6-13 r: hzqgnrdqqxdwcshjr\n" + "5-10 d: ddczhrqjqd\n" + "1-10 b: qngplzdvrbsb\n"
            + "3-5 d: xrndch\n" + "2-6 s: sssssss\n" + "1-6 s: gsssssnssff\n" + "4-5 p: pnpxpgppsp\n" + "3-7 j: jcjnbvz\n"
            + "8-14 j: mrvbmlsdplscxznj\n" + "1-6 b: zbbbbbbbb\n" + "4-5 b: bbbbb\n" + "6-8 b: bbwbbbbbbbn\n"
            + "11-20 m: dlmcggnmlwmghngcqpxm\n" + "8-10 n: nfnncmwnnn\n" + "5-8 c: ccccjccc\n"
            + "18-20 k: qdckkkkdvknkkvvblhkk\n" + "1-15 l: gclfbjlvsxfgllq\n" + "14-16 v: vvvvcvvvvfknvvvvmvvv\n"
            + "4-7 t: rtdtttt\n" + "5-7 h: shhhwvb\n" + "4-5 x: xxsnvx\n" + "11-12 v: vqgqvdvrjvvdvvvvvvl\n"
            + "5-6 l: llllvwll\n" + "3-4 l: llhln\n" + "6-7 w: wwwwwwb\n" + "3-6 j: jjjjjjjfjjjjj\n" + "1-7 q: qqqqdqqqqq\n"
            + "4-14 q: qcqjqqqqqqqqqfqqqk\n" + "2-5 z: pzzfz\n" + "1-2 f: ffsflqqrgzfnmbw\n" + "1-4 p: qwgpwlbx\n"
            + "3-5 s: mzpcwhfswsz\n" + "6-7 w: dsmwcnwfww\n" + "2-5 h: qpkwhsmftnlvcgpq\n" + "10-11 m: mmmmmdmmmlbfmmmxmm\n"
            + "2-5 g: vgvgg\n" + "4-6 l: lglvltlll\n" + "4-7 t: ttttttmtttt\n" + "5-8 w: wwkfwwvhrvwwpx\n"
            + "4-5 k: nszkkkdrk\n" + "1-4 x: hbdg\n" + "6-9 p: pjppfwpjpj\n" + "4-5 h: hhrsqh\n" + "11-13 x: cfxfxrxxxxxrh\n"
            + "12-15 n: ctjxnzsbjmnnnfnjm\n" + "9-13 n: nnnndnntndzlgnnnnnnn\n" + "3-4 h: hhhws\n"
            + "4-15 f: kqgfffkvqtksrlwglt\n" + "10-15 d: dddddddddgddddbd\n" + "3-5 g: ggxgfhggg\n" + "4-8 b: bpjbpnvnzwbvfts\n"
            + "5-11 c: wccccmvccccc\n" + "17-20 h: hhshhhhhghhhhdhhvhhh\n" + "10-12 m: gmmmmmmmpbsmpmmmmhr\n"
            + "1-8 z: zzzzpzzxbh\n" + "4-10 c: kcxlxtckgxvc\n" + "8-10 f: ffffffffffffff\n" + "6-12 h: hhhhhjhhhhhhhhh\n"
            + "6-8 s: wsvwvfssh\n" + "3-6 j: xjjzjjzlztjrjjjt\n" + "2-3 g: gfvg\n" + "11-12 v: vvvvvvvvvvvv\n"
            + "2-4 l: lllltlll\n" + "4-9 q: qvqmhqdkd\n" + "4-5 l: lllxlj\n" + "5-6 d: dddbdddt\n" + "3-8 g: gpggggxzdggzxgh\n"
            + "2-7 s: nsrkvkwzpmv\n" + "11-13 v: vvvvvpvvvvvvpvvvvpvv\n" + "3-5 c: qccvjcc\n" + "12-13 d: ddddhddddddfld\n"
            + "3-4 q: dqqqqqqqqqq\n" + "12-14 k: kkkkkkkkkkqnkp\n" + "2-4 v: bvwvz\n" + "6-8 d: mxhdddddckdbkgddtsdd\n"
            + "3-5 p: ttpdppp\n" + "9-10 p: pnppvpkphp\n" + "2-5 q: qqxczq\n" + "1-5 x: xbgtwk\n" + "6-7 f: dwzfpfr\n"
            + "12-13 b: bbbbbbbzbbbbb\n" + "8-10 x: xxxxxmvcnphxfx\n" + "15-16 t: tttttttthmttkttdg\n"
            + "1-3 l: rsmgkpjxnpjnlrmdslsw\n" + "2-4 x: xhxsjxx\n" + "11-14 w: hwwbwlwhwgxmwwwwwz\n" + "3-4 j: pjjdr\n"
            + "1-8 s: sssssssssss\n" + "9-13 q: jqxcngqqqrxqwp\n" + "3-10 v: vjxhfzbfvv\n" + "14-15 w: lmxjqmwtpwsvjjw\n"
            + "8-9 c: tcmccdczs\n" + "4-5 w: wwwtw\n" + "5-10 f: ffffmfhffkpfffffffff\n" + "7-9 l: llgblxlxlwv\n"
            + "6-14 w: wghjmwtbvsvzkfcwwj\n" + "6-9 p: mqpnmlpcdppfp\n" + "5-7 b: bbbbbbbb\n" + "6-7 w: xwwwxcw\n"
            + "11-13 p: ppppppwppfppzszppwpp\n" + "3-5 z: rzmzmzzbzzczz\n" + "1-8 h: bhbxrhthphhbljknfvhh\n"
            + "11-12 q: qqqqqqqqqqwjqhq\n" + "2-17 r: zrmshszlckcrxtmsrtg\n" + "4-5 s: svztv\n" + "8-11 z: zzzzzzzqzzczz\n"
            + "3-4 h: hnzpc\n" + "13-14 q: qqqqqqqqqqqqqhqq\n" + "2-5 p: rppgpd\n" + "12-15 p: ppppppppppppppl\n"
            + "1-14 v: vvzvvvvvvvjszv\n" + "7-9 h: hxhnhhlhzdh\n" + "2-3 r: rrrr\n" + "5-9 m: mmmmdzmmwmmmm\n"
            + "4-7 f: zpvlfff\n" + "4-15 j: gpjjjcjpjjjjjpj\n" + "4-7 p: pdkgjhp\n" + "6-16 w: wwwwwwwwwwwwwwwww\n"
            + "9-10 q: hsdfqqqrlm\n" + "4-16 q: qqqqqqqqqqqqqqqjq\n" + "4-7 s: sssdtsr\n" + "15-16 p: bqphxhczjpmpwpqpd\n"
            + "7-8 g: wbjggggc\n" + "11-16 x: xxxxxxxxxxxxxxxmxx\n" + "16-17 w: wwwzwwwwwwwvxvxww\n" + "2-7 t: ttttttgttttt\n"
            + "6-14 q: mmkqxqljbkmfpjvgf\n" + "16-19 j: jfjjjsjjjjjjjjfjqjj\n" + "6-7 r: rrrrrrs\n"
            + "7-9 d: ddgndhdcvdlnddddsdt\n" + "2-3 m: mgpms\n" + "6-10 g: lqdxdtjmglvgn\n" + "6-7 q: qqqqqbqb\n"
            + "3-5 w: wwjtwwwwwwww\n" + "3-4 g: ggthg\n" + "6-9 k: dbhgrkcjllqkgh\n" + "14-16 c: ccccccccccwcclqccccc\n"
            + "2-8 f: wfpvntbqcjf\n" + "6-10 r: njrlljrfkl\n" + "1-4 h: hhtjhhfh\n" + "5-6 z: zgxwzjqz\n" + "6-7 b: xbcbsbk\n"
            + "3-4 p: prpq\n" + "1-4 s: dhsmk\n" + "2-6 f: pffqffrfjff\n" + "5-6 h: hhwbdzhhg\n" + "5-6 q: qqjqqqqqvq\n"
            + "5-8 k: kkkkkkkq\n" + "4-5 k: kkkkzk\n" + "8-9 s: ssdvssbss\n" + "11-13 q: qqqpqqqqqqrqq\n"
            + "2-13 w: wvcwwwwwcwvmwmwwwww\n" + "9-12 r: rrrrrprrgrrhrr\n" + "10-13 m: mmmmmmmmwfmmmmm\n"
            + "9-14 t: vgsrtrxzltkvbtt\n" + "2-3 j: sjjj\n" + "4-5 v: mvvfm\n" + "4-5 h: hhhrb\n" + "7-10 j: jjjdjvfcjjm\n"
            + "4-13 l: lldllllwlpgmllkbsbd\n" + "12-20 r: rwrrrrrrrfdrrqnrrhrr\n" + "9-11 s: smssssssssgss\n" + "4-5 b: dfmsp\n"
            + "2-3 d: dlsnxd\n" + "10-19 m: mmmmmmmmmfmfmmmmmmm\n" + "1-5 n: nnnnhnnnn\n" + "4-7 d: dfsddkw\n"
            + "1-7 q: dqzzqxqpqqqsqnqq\n" + "2-3 p: twrp\n" + "14-15 g: lrsggrgvgjjcvggfgcc\n"
            + "9-20 m: rsbmmdmpmjjfhjdmfpgl\n" + "4-5 v: vvvmv\n" + "7-9 t: tttttthttt\n" + "3-4 r: rrhjr\n"
            + "5-8 m: mmmxmmqmmmcssnmm\n" + "10-11 g: gbgmggggwhnggsgvgf\n" + "6-7 m: mcmpkmm\n" + "2-3 b: bzbb\n"
            + "5-14 w: wwwwbwwwwwwwwdsw\n" + "4-17 x: wctqhkgxmnhwlsvpx\n" + "6-9 x: jdkxxdxhx\n" + "4-6 k: hpkskbkgjkkckh\n"
            + "8-10 h: hhhhhhhphphhhsh\n" + "10-11 p: ppppjppppzjppppp\n" + "12-15 f: ffmphffwxwjzfsfbs\n" + "2-4 n: nnrn\n"
            + "5-7 b: zbrbmbrbbbbbbbbbsbb\n" + "4-9 q: qhpbmrldqpvfk\n" + "3-5 b: bbbcr\n" + "2-13 t: ztkrtfqmcdcwsx\n"
            + "1-2 r: rxjrnrrrrtprr\n" + "2-5 h: rhbdcbp\n" + "1-2 z: zzzz\n" + "2-5 x: xkgxxxqtghn\n"
            + "2-7 z: hzrtbqzbfgqzd\n" + "5-6 j: jjzjmj\n" + "9-14 q: hqtgqqsqqqqqqhnqvqqq\n" + "3-4 x: txmxcx\n"
            + "3-8 x: hszvqxxxzvgpjrtxtqk\n" + "4-10 p: plpzpppppp\n" + "5-6 v: dvvsbv\n" + "16-18 z: zzzzzzzzzzzzzpxzzz\n"
            + "1-18 t: tjtttcststttttvlttt\n" + "7-13 t: tbdttctqtxdfrtncntcl\n" + "9-10 r: rrrqrrrrrrm\n"
            + "3-13 n: fqgmfmtnxhnzn\n" + "3-8 l: scjrllgwmkkfgbr\n" + "6-13 t: tgttttzsftrrtqnqt\n" + "5-11 r: rdrqrprfrbrr\n"
            + "9-15 k: kkkkkkkkkknkkkkkhk\n" + "11-12 b: bbbbbbbbbbbb\n" + "1-4 n: nnnsn\n" + "6-7 t: xnttbttsnt\n"
            + "6-11 z: rzxzzzkwlnjzzrsswz\n" + "1-2 h: hrmfh\n" + "2-3 x: mxxx\n" + "10-12 k: jrqdmnxlsbkwgg\n"
            + "16-17 j: jjjjjjjjjjjjjjjjw\n" + "10-11 k: kkkkkkkhkgwkk\n" + "6-7 n: nnnnnrbn\n" + "9-11 q: qqqqqqqqqqq\n"
            + "1-4 c: cccq\n" + "13-15 p: pppppcpppkppppp\n" + "2-4 b: gbbbjdqdbz\n" + "13-17 p: pppxpbpppppphppppk\n"
            + "3-5 p: pplppp\n" + "4-5 q: qqqkpq\n" + "10-12 h: hhhhhhhhhhhmhhhhhhh\n" + "17-18 r: rxrrrrrrrrrrrrrrcq\n"
            + "4-6 p: ppppppnpp\n" + "3-10 r: rrrrrrrrrrr\n" + "2-3 b: qwrv\n" + "1-3 c: vfcgc\n" + "3-4 s: ppsrq\n"
            + "2-4 s: wpsvjxs\n" + "2-9 d: rjjdddcddjdd\n" + "10-11 w: hwwwnwwwwwsnslwpwwb\n" + "1-5 k: nfvnks\n"
            + "13-14 w: wwwwwwwwwwwwww\n" + "4-6 s: sssssks\n" + "2-6 m: mmmmmmmmmm\n" + "6-12 h: hhhhhhhhhhwvh\n"
            + "3-8 v: nvkvvvvrvv\n" + "9-14 x: xxxxxxxxxxxxxxxx\n" + "4-9 r: rrrxrmrkrrrvrr\n" + "2-6 x: qrpxzx\n"
            + "7-17 n: snnnnnzmvnsspnqlnznm\n" + "4-7 q: qqqqqqvqqnqqgq\n" + "7-8 v: vsvjvsvvvfvzv\n" + "8-12 d: kwzmmqfztrnd\n"
            + "2-4 n: nnjnh\n" + "5-6 f: zfwvfffvrfc\n" + "12-14 b: bcbbbztsbbbfbbbbbb\n" + "15-16 m: wmmmmzxmmmmmmmmhm\n"
            + "11-13 m: mwmdmmcnmmfmnmwmmm\n" + "1-3 n: znggn\n" + "8-10 x: xxxxxxpxxs\n" + "7-8 k: qkpspjkk\n"
            + "5-6 b: wjsbbbbmbbbq\n" + "9-13 x: xxxjxnrxhxlvpx\n" + "1-3 p: hpvp\n" + "5-6 z: qpsfzc\n"
            + "5-11 t: pdbtttftrtt\n" + "7-9 b: bbbbbbzbzb\n" + "6-13 g: ggggggggggggdg\n" + "8-14 h: hhhhhjhhqhhnhr\n"
            + "17-19 x: xxxxxxxxxzxxxxxxxxxx\n" + "5-7 s: sssssskx\n" + "5-8 p: phnjswphclpbtcwrdvp\n"
            + "2-14 b: jpvmgklvqjbclb\n" + "3-5 s: bkqmsvlfrhbtssmfrzlq\n" + "6-8 v: vvvvvwvv\n" + "7-8 c: mmlhlccr\n"
            + "11-15 f: hvvfvbpgwxfpxvfnb\n" + "5-7 n: pntnnsx\n" + "10-15 n: mlmrbrnknsncjddm\n" + "1-7 x: xzqswmkktxbvbcm\n"
            + "1-3 n: lndnn\n" + "7-8 d: dddmkddd\n" + "2-6 q: qggqqqzqq\n" + "3-4 z: zzszzz\n" + "14-16 r: nrrgprrxhrlrjxlr\n"
            + "2-14 p: pppppppppppppvp\n" + "7-10 x: trwjbxxwjqfhspxr\n" + "6-7 q: mqgpjwq\n" + "5-6 f: fffzgb\n"
            + "7-8 q: qqqlqtdqq\n" + "5-7 s: zsfkdss\n" + "2-4 h: thjwvg\n" + "2-8 f: zmrdffff\n" + "2-6 t: btrqbs\n"
            + "11-12 m: mmmmmmmmmmcm\n" + "7-10 w: wwwnwwbwwwwwwzwsw\n" + "4-6 c: khszwwltfcndcbd\n"
            + "12-16 v: svnxvvvrvvvvvvvvvvvv\n" + "2-9 d: dgddwddrnddscdchdd\n" + "6-8 p: dpmdzptwfcvkzbznwf\n"
            + "9-10 d: dddddddddvd\n" + "16-17 s: cqtsgqsbkjrldhlcs\n" + "3-14 w: dvrblqkwnnwwbww\n"
            + "13-18 n: nthqnkfgnnnsncnnnb\n" + "10-12 j: zlcjgjcpzjbj\n" + "10-11 r: rrrgjrrrrrfr\n"
            + "4-8 z: vnmzsbzrvcznhjhtn\n" + "10-11 r: rrrrrrrrrhqr\n" + "4-5 n: nhdnzd\n" + "10-14 z: nzzzzzzzzzzkzzzz\n"
            + "7-9 n: nzzwnnkqnn\n" + "3-4 h: hhhx\n" + "1-8 c: ccgqgmkc\n" + "4-5 w: wwdwrww\n" + "2-9 p: ncppppppfppszpw\n"
            + "12-18 n: nnnnnnnnnnncnnnnnnn\n" + "4-6 f: fhflfnv\n" + "16-17 x: xxxtvxxtjxxxsxxxx\n"
            + "12-14 h: zhsxhfhhhhhhvshpnvp\n" + "13-15 v: mvvnjgvvvvqnbvvvg\n" + "3-6 j: jgzwjpcjj\n" + "1-3 k: qcktkhnk\n"
            + "13-17 f: ffmfffffffffxffft\n" + "3-5 s: ssssq\n" + "8-16 m: zmzbmmmxmmcvphqc\n" + "4-5 l: llrsl\n"
            + "3-12 m: qmpfmmmmmbdjmmlmtpv\n" + "1-4 c: ccspqcj\n" + "3-4 l: qblll\n" + "12-16 h: mphhhhhhhhhhvhhwhkh\n"
            + "4-6 v: vvvrvbvnvv\n" + "12-13 x: gkxwxzwljjqxxxgwjgxx\n" + "1-5 z: zzzzzzzzz\n" + "1-14 q: ltqqqqqqqqqqqpqqqqq\n"
            + "8-10 g: ggmggjvghg\n" + "12-20 v: rttnvvvqvmvlnhsvvqtv\n" + "13-17 v: vvvvvvvvvvvndvvvhwvv\n"
            + "13-14 w: wrwwvwcwmwwwxwwwlwww\n" + "3-13 l: lltlllllllllp\n" + "7-14 d: bhcspvkdwdsmkdwnq\n" + "1-5 k: kkpkx\n"
            + "1-5 k: stkkk\n" + "1-10 n: ntbptczncn\n" + "5-8 m: rmffmrvcfzsqqmvvm\n" + "10-12 f: fffffffffsfff\n"
            + "3-4 g: kptg\n" + "4-9 f: fffffbsbf\n" + "1-2 v: dnvmd\n" + "7-16 f: ffffffdffffffhhffk\n"
            + "14-15 b: bzbbbbbpxbbblrp\n" + "11-15 c: chccccccccncccs\n" + "15-18 c: ccccccccccccccccctcc\n"
            + "17-18 v: vxvvvtcxvpvvpvmzxv\n" + "9-10 m: mmmmmmmmmmm\n" + "2-9 j: kjgfjfpkzwndlpk\n" + "3-5 r: jsqsr\n"
            + "8-9 v: dvvbvvvvp\n" + "8-9 r: rdrrrrrrrbjtmr\n" + "13-16 r: drddrrrmsrrdrjsr\n" + "2-5 f: fffffff\n"
            + "5-6 d: dddddjd\n" + "10-17 g: mgmhzjggdkxgskcsm\n" + "4-5 d: drddd\n" + "9-10 s: ssssssssst\n"
            + "3-5 m: nmmmgmmmmmmmmmm\n" + "2-3 b: vplpl\n" + "8-16 l: lllllblclllllllpl\n" + "12-13 w: twwwwwlwwwwfwwwwd\n"
            + "8-11 m: mmmmmmmsmmglm\n" + "10-16 j: jjbjwvjjjjrjdjfpj\n" + "12-13 n: nsnnnpnntncwnnn\n" + "2-4 f: vllf\n"
            + "1-9 f: pffffffffff\n" + "1-6 m: mmmmmmm\n" + "5-13 w: jxwwbwwrkwwwpwwn\n" + "10-12 q: qxtqqqqqqhgq\n"
            + "9-12 f: fffffvffmvffh\n" + "3-6 z: fjzjkz\n" + "2-8 k: pcnbqdtksnkm\n" + "8-13 l: lkxlllsnmlblclx\n"
            + "10-12 m: cwcnkcpdsnlm\n" + "7-9 g: gggnggghm\n" + "4-14 w: vxxwffpxrmpnwwqlr\n" + "5-7 m: wmfmmmm\n"
            + "7-10 m: mjmtmmjkmmmlg\n" + "9-16 l: cjzlllmllkfwlwcl\n" + "11-12 c: cccccccccccc\n" + "10-11 b: bmbfwmbbvxtvfb\n"
            + "7-8 v: xvvvdvvvv\n" + "5-6 l: llztlg\n" + "6-10 m: mmmmmzmmmmmmmm\n" + "3-5 b: tsbzvz\n" + "1-4 g: ggggg\n"
            + "5-9 b: bbbbtbbbbb\n" + "4-6 f: fffffvff\n" + "8-12 w: wwwwwwwnzwwpwwwwwww\n" + "2-3 m: mgmmmmmm\n"
            + "2-5 b: bbbbbbb\n" + "5-6 b: bbbbbg\n" + "3-5 f: fpfpl\n" + "6-7 b: bbbbrhbbbbfbl\n"
            + "16-18 x: xxxxxxxxxxxxxxxxxhxx\n" + "7-9 w: wprgxqlpw\n" + "5-9 k: qkxbkkkzp\n" + "2-7 b: bbbfbbkbl\n"
            + "18-19 l: lhlllllllllllllllll\n" + "4-6 z: zppzzs\n" + "3-11 j: nkcsvmhtklj\n" + "4-8 g: ggbrdwbf\n"
            + "10-14 s: qsssssssslsssssszss\n" + "3-4 z: zzzw\n" + "5-13 h: hhhhhthhhhhhr\n" + "11-12 m: pmmmmmmmcmmdmmgm\n"
            + "4-8 l: hclghxblsn\n" + "10-12 n: nnnnnnnnnhng\n" + "1-17 l: bxtlrzmsppwvvvwjp\n"
            + "5-12 d: xbsnsvbmdhddkwvdprw\n" + "18-19 c: lrphqjtncgftjfcjcctc\n" + "12-13 k: gqghmzhkhcskks\n"
            + "10-12 j: djjfvjjrjdjjjjp\n" + "3-6 c: cczcckcccccc\n" + "11-14 q: qrqzqqqqlqqqvt\n"
            + "16-18 k: kkkkkkkkkkkkkkkzkx\n" + "1-4 p: pptxfljp\n" + "2-7 p: pphdjqph\n" + "14-15 q: knfmcqptqwfnrqqqkm\n"
            + "2-3 k: zvlkn\n" + "1-6 s: ssqssxs\n" + "3-10 v: zbcbpfqrvsr\n" + "3-4 v: wcvlghhv\n"
            + "18-20 l: lllllllllllllllllbll\n" + "13-14 k: kkkkkkkkkkkktrk\n" + "4-11 c: lccmccccfccccjc\n"
            + "2-12 v: vbfdvsmtvkmvbvvvtvn\n" + "3-7 m: nmmcmmnm\n" + "13-18 k: qfqckkkkqfbtjkkkbp\n" + "8-9 k: klkkkkvkskspk\n"
            + "3-15 j: qnvxjqsgrpwjzkjzkp\n" + "1-4 s: qgss\n" + "16-18 w: wwwwwwwgwwwwwwwwwc\n" + "5-11 j: sjjjjjjjjnp\n"
            + "3-6 p: pppppmpppppppppppppp\n" + "5-12 r: rrmjskttjhmrs\n" + "4-5 f: znfkxthpffjj\n"
            + "1-19 z: zzzzzzzzzzzzzzzzzzbz\n" + "13-15 s: ssssssssssslpsss\n" + "4-11 c: whzhrcbhbccc\n"
            + "1-8 m: mmmmmmmfmmmmmmmmmmm\n" + "2-10 j: bjhbfqcjrjhwkk\n" + "15-20 k: bnqkkkmvqnfcklzdjgkk\n"
            + "7-9 r: hrdrrrrrprrrr\n" + "3-9 n: jrnbzzmnnxln\n" + "11-14 f: vfnpfrbzpjqffrjzf\n" + "5-6 f: ffsmpbl\n"
            + "2-7 z: sznjwfzs\n" + "7-8 t: ptttttttt\n" + "2-4 d: rsnd\n" + "3-4 r: cvbpvktkrr\n" + "2-3 j: jjjj\n"
            + "5-7 z: qnjwzxz\n" + "2-5 z: fcbmzbs\n" + "1-9 g: nhgggmggwggngf\n" + "7-10 l: xmjdbtmhrdjlltjcglxp\n"
            + "7-8 b: bbbbbbwjbbbbwb\n" + "2-4 h: mhfhnjl\n" + "5-14 k: hqkrkvkzkhrknzjphc\n" + "7-12 b: bbgbbbbnxtcgbbb\n"
            + "12-18 t: tmtxtkpmqtvmttwcfgn\n" + "2-16 b: lbbnbxfrbtjltvpnnbmn\n" + "1-5 c: gvcchczcckcccnc\n"
            + "10-15 d: kngtvjwdvdktvdq\n" + "8-13 g: ggggcggggggggnlggt\n" + "5-7 n: nnnnnnn\n" + "2-4 n: nnfnxl\n"
            + "4-5 h: fbmdghjth\n" + "14-18 q: qqqqqqqqqqqqqqqqqhqq\n" + "7-13 l: llgldxlxrkklbc\n"
            + "15-17 w: twwwxwqwwwwwwcfww\n" + "16-19 f: fffffmdrbfffwfffnnrf\n" + "2-14 m: lmhhfmsjrrtgrm\n"
            + "7-11 v: hrvtwbvbwqvb\n" + "4-7 r: rczrrrrr\n" + "12-13 h: hphhhmhhhhnqh\n" + "7-12 x: xrqdxpxhfqrtgxh\n"
            + "5-7 t: fdnrnktrgtkvckpxbth\n" + "3-4 v: kvvr\n" + "1-2 g: pggxggfwcwzqpdbr\n" + "9-14 t: ttttttttdtttttttt\n"
            + "3-4 v: rvvlzwxxmwvv\n" + "7-9 l: llllllflmllll\n" + "3-5 z: mzfrh\n" + "3-4 n: znmlnnwn\n"
            + "5-12 j: mjrhqkdtsjpmv\n" + "1-7 p: pppjntrrnzzdtv\n" + "3-4 k: prmx\n" + "3-5 c: ctccc\n"
            + "3-14 n: nnlxnnnsnnnnnxnnnnnn\n" + "2-5 m: mtmmmmm\n" + "2-3 j: wjjj\n" + "9-13 r: nbtrwwkrrhqpvqmgjrxf\n"
            + "2-13 r: lmfwxcxrcvxxrhd\n" + "11-15 h: hhhhhhhhhhhhhhvh\n" + "1-6 l: hplllslzlfl\n" + "2-13 v: xvvvctvxwcvmmwm\n"
            + "3-10 s: sxpssssssmkss\n" + "1-3 t: cltdrntxw\n" + "1-2 b: sndbw\n" + "1-5 r: prxrrrr\n" + "4-7 f: gffffgjf\n"
            + "8-10 k: nkkcrkkkkk\n" + "2-4 s: sssbml\n" + "6-9 f: pfffzfffwwfpfffk\n" + "5-7 l: mxlllvl\n"
            + "10-13 d: dddddddddcddkdddd\n" + "1-8 q: qqnqqqqqqqqq\n" + "2-6 v: nvzvfv\n" + "13-15 r: rrnrrrvrrncrrrhr\n"
            + "2-5 s: jhsss\n" + "10-15 v: vvvvvvvvvmvvvvvv\n" + "2-4 n: cnpvsknzhqwpc\n" + "2-8 z: ctxqmpzd\n"
            + "8-9 x: txbtbrzxxxvx\n" + "3-12 j: jjjjjjjjjjjjj\n" + "5-6 v: vqdsvv\n" + "4-5 c: ccclc\n"
            + "6-8 m: dptrqlwmfcbmskjw\n" + "4-14 s: sxssscssstsssssssbss\n" + "3-6 q: xhqwqfpprqm\n"
            + "14-15 f: ffffmfffffffffkbffff\n" + "3-12 f: msjmmfrgrlzvtss\n" + "4-15 c: mwwcfkcchhcpkhg\n"
            + "2-3 v: hvpnbcvxgqn\n" + "16-17 d: cddssdpzdjdfkddht\n" + "12-13 v: vnvvvvsvvvvvr\n" + "4-5 r: rrrrv\n"
            + "7-9 g: zgzvgfgtg\n" + "3-4 w: zwwh\n" + "16-17 h: gvhgvhhhhcgkkdqhhhp\n" + "10-11 b: bbbbbbbbrzzbbbb\n"
            + "13-14 v: vvvvvvvvvvvvbv\n" + "10-17 k: dhkczrpkckdbjthkkb\n" + "6-7 q: qqqqqdkqqqq\n"
            + "10-14 b: prnmgmmtjvjvfbnbbvg\n" + "8-13 w: wxwswrqbwmmwwgw\n" + "14-15 f: ffffffffffcffzff\n"
            + "3-8 l: llxltlll\n" + "3-18 b: brbpdcgjcmwqlvkncn\n" + "2-3 m: xpmmm\n" + "6-7 r: rqmcgkktsrrkrflgftg\n"
            + "3-15 s: wpcztpfsfpskfbsczm\n" + "1-4 c: cccccc\n" + "11-15 c: gcckqcgcccccncccccc\n"
            + "1-3 c: cscjpjfcdbpfpbrvv\n" + "18-19 x: xxzxmxjwxxxjhxxcdhf\n" + "6-8 j: jjspjsppjzqhvrgdvdmj\n"
            + "4-5 f: swffn\n" + "11-12 m: dmfmgmwmmhkw\n" + "15-18 m: mmmmmmmmmmmmmmmmmmm\n" + "7-8 l: llvlllllll\n"
            + "3-4 j: jwgflnp\n" + "1-7 v: dvvvvvvvvv\n" + "5-7 l: lwmfxbvx\n" + "3-10 f: flnqlvbfgff\n"
            + "2-12 s: stssbsssbssskstsfs\n" + "10-15 g: rjnsggtjgpqxshbg\n" + "7-8 j: jjjjjjjj\n" + "1-3 b: nbbbb\n"
            + "5-13 b: bbbbbbbbbbbbbbbb\n" + "14-17 h: hxhhhnhkhghhhhhhhhjz\n" + "9-10 b: wbbbbbbbvg\n"
            + "1-7 d: ddddwddddddddd\n" + "5-9 b: jgdkhbdbphbs\n" + "10-15 w: mwgqmhjzwwnvfwwwm\n"
            + "7-8 h: thhhhhhhhhhrfcphh\n" + "3-7 z: khczzxz\n" + "4-6 l: lllhlh\n" + "2-5 m: psmmm\n"
            + "4-6 x: lrchzhqxxrrxvr\n" + "13-16 q: qqqhjqqcqqqlkbqq\n" + "3-9 f: ffffpffffff\n" + "5-6 k: kkkkgkkk\n"
            + "3-4 v: vvll\n" + "5-9 x: cpbvxwxfhpxrxcxxnq\n" + "8-9 p: pxppvnlpp\n" + "1-2 l: llxl\n"
            + "13-14 h: vhhhmhhhhhvsmhhh\n" + "12-14 b: bbbbbbrbbbngbbbkbbb\n" + "10-11 r: rxmdszrtrrj\n" + "4-5 n: nnnnnn\n"
            + "2-4 x: xsnx\n" + "11-14 z: wqzpjzzzzpzzlczzsj\n" + "1-8 d: lddddddd\n" + "4-6 v: cvvkfvv\n"
            + "6-17 f: ffgxddfflfgfffxbf\n" + "1-7 x: bzvxhxr\n" + "4-16 w: wwwjwwwwwwwwwwwhww\n"
            + "15-17 l: lllllllllllllllll\n" + "1-19 v: vnbzvvjvfdvvvvvvvmt\n" + "2-5 t: tpttzs\n"
            + "12-16 f: ffffflfffffsffftfff\n" + "5-10 g: hbggtxxgvkfg\n" + "7-12 s: jssssgtjfcrsqsx\n" + "1-4 p: pppg\n"
            + "4-5 w: wrwbw\n" + "7-9 r: rxrqnxqrrtrfrrr\n" + "8-9 b: bbbbbbbbbbbbbbkb\n" + "5-7 d: ddddddddd\n"
            + "1-4 x: xxbfxxxjx\n" + "9-14 w: svwwnzjtqczwww\n" + "15-16 f: ffffffffffffffbff\n" + "8-9 h: hhhhhhhhh\n"
            + "11-12 g: ggggggggggtmg\n" + "15-16 g: gggggggggggggpxgggg\n" + "9-12 d: lctfgcddhddbfsxrpl\n"
            + "8-9 w: fszlzwzwwwdwzqf\n" + "10-16 v: vlvvvflvvmdvkvvvvvs\n" + "9-11 x: xxxbxxxxdkx\n" + "9-10 n: nnnnnnnnnt\n"
            + "10-15 b: zbwzkjvqbtbnbkbb\n" + "13-18 w: wwwgwwwwwwwwswwwwww\n" + "3-4 n: ndgg\n"
            + "14-17 k: nkmkdssxzkkkfkkkqflk\n" + "2-13 z: prmtcfxgldwcpr\n" + "10-15 f: xfffffzfwpffffhpffg\n"
            + "6-12 t: tttttttttttz\n" + "1-3 b: bbbbbb\n" + "2-4 q: qqqhqqq\n" + "4-8 w: lqzqwwzwpcwwwlr\n"
            + "15-16 f: fffgrffffffffvfc\n" + "12-13 p: pjmpppppppppv\n" + "6-14 h: zhgxlgshnfdhrl\n"
            + "17-19 d: qgwtdjcvzdpsphppdmds\n" + "15-16 f: xfffffffffffcrfj\n" + "5-6 x: xzxgxxxn\n"
            + "4-7 f: fljffdfbrxklbf\n" + "8-9 t: btnwxkrtt\n" + "9-10 g: bggsggggfgpgg\n" + "2-4 p: ggpsbgdx\n"
            + "2-6 c: cxscckkc\n" + "3-11 q: qplqztgmdjqbqvqg\n" + "5-9 g: ggggtgzggglgdg\n" + "8-16 c: ccccccccccccccccccc\n"
            + "1-5 d: dhdddddd\n" + "3-5 c: qjcdz\n" + "3-6 b: bwbbbbbhbbzbbbb\n" + "1-4 f: qfffff\n" + "9-11 l: tllxbrfzrzl\n"
            + "3-4 v: vvvv\n" + "3-7 z: dzzmpbvvw\n" + "11-16 r: rrrzrrrvrrzwrrrr\n" + "4-5 z: kzqvzzz\n"
            + "3-9 n: rtnnpmnnsnncnqdnnn\n" + "5-7 t: htmghwktpdttt\n" + "5-6 s: sssssss\n" + "1-4 r: crpwcrrrrr\n"
            + "4-9 t: tttjttttttt\n" + "2-3 t: mrgt\n" + "13-17 x: xxxxxxxxxrxxmxxgv\n" + "2-11 m: mmmmmmvmmqmmcljbkt\n"
            + "1-3 l: lwlg\n" + "5-7 z: zzzzzzkz\n" + "3-6 f: wdfwlk\n" + "4-6 v: hjfvcv\n" + "1-3 h: hhdhchjvh\n"
            + "3-12 m: hlkbjrmjbkps\n" + "5-9 t: ttthqdchttvjk\n" + "9-11 p: wpzjpbpppnpqwpk\n" + "4-5 v: lvvkwv\n"
            + "12-15 k: kzkkkkkkkkkbkkkmkkk\n" + "4-8 h: hnhfhwhslhhhhdhg\n" + "1-7 d: dddddddd\n"
            + "18-19 g: gggggggggggkggggggx\n" + "4-10 v: vvvbvvvvvnvvjv\n" + "5-9 v: lvtqgmvmvvlv\n" + "2-3 d: pdkrdzdlvdn\n"
            + "1-12 s: sssssssssschs\n" + "7-9 c: ccmkscwccwql\n" + "3-5 s: sksdr\n" + "6-7 p: pppptjgppp\n"
            + "13-14 m: mmmmmmmmmmmmmwm\n" + "3-5 j: jjdjfj\n" + "2-5 f: dbxfrv\n" + "1-11 w: wwzdqntxzwt\n"
            + "5-9 b: bbjwffgmbmxb\n" + "12-13 b: bnbfblrbznqbg\n" + "1-4 s: vlbsdkslvk\n" + "1-12 k: kkkkkkkkkskkkkkkks\n"
            + "3-4 f: ffcg\n" + "4-19 w: zlfvsqxthvvtmwrmrwj\n" + "6-13 r: bsnqgqrsrrjjnthdr\n" + "1-5 t: bctbqbtft\n"
            + "4-7 v: vvvvvvqvvv\n" + "2-5 n: jnqnn\n" + "2-4 r: rrrr\n" + "11-13 w: fbwmwjgvjlnpwvwmqf\n"
            + "12-13 v: vlvskvkjrlsdv\n" + "17-18 s: sssssssssssssssshsss\n" + "7-18 q: qtqhbqxkcqqxnqwlqdqq\n"
            + "5-9 q: jfqrdgtqpqs\n" + "7-13 j: jjjjjjmjjjjhgjjjjjjj\n" + "1-3 z: gzzzl\n" + "9-10 g: gbcgggnggg\n"
            + "1-2 d: ljdddddbdddddd\n" + "2-4 t: ntwf\n" + "8-13 p: ppphpppcpppppppppp\n" + "7-11 b: bbkfbbbbbbdbfb\n"
            + "5-7 z: dtgtzzzkxdp\n" + "2-3 w: vwtbc\n" + "11-12 s: bssvsssssssnsssbs\n" + "8-12 w: wqwpwcxwgpww\n"
            + "8-9 p: mqpcvlspwx\n" + "2-6 w: wwskwkwww\n" + "6-17 w: gbwwnwrdvnnpfxhgl\n" + "12-13 h: hhhhhhhhhhhht\n"
            + "14-15 b: bkbbbbxfrzbbtbbbb\n" + "1-11 j: jvjjjjjjjjgfj\n" + "1-11 k: tkkkkkkkkkpk\n"
            + "12-16 q: frqhkwxwjbqqqqqq\n" + "7-10 f: fjkvvhffrhfgfrfffl\n" + "14-15 n: kfnnnvsvnhqncqm\n" + "4-5 v: vvvhx\n"
            + "3-10 r: qwrscrfwgtntntzsvnhg\n" + "2-11 r: wfdmkrcbgzbrzs\n" + "4-12 p: smwpnnjvrpvzmpcfprw\n"
            + "5-11 k: kkkklkkfkckw\n" + "2-4 g: gbgq\n" + "4-6 d: pdvsvkddd\n" + "1-5 h: hxhhthhhbbhlshhwh\n"
            + "10-13 n: knnnnnnnnnnnnnnpnnnx\n" + "3-15 m: mmmmmmmsmmmmmmnmmm\n" + "3-6 d: bddvdd\n" + "11-12 p: pqppnmlpppvp\n"
            + "1-3 w: swwvdwqwwwc\n" + "11-15 d: dcxdddddwtldddddrpnd\n" + "5-10 m: mmmmxmmmmxmm\n" + "4-6 g: qgggtgggg\n"
            + "8-12 h: zpvzbbhsstnv\n" + "5-6 x: nxxxvxxx\n" + "2-8 h: hhpqhhhhzh\n" + "14-17 v: gzvvwvvvvvvvvnvmpv\n"
            + "2-14 h: hxhhhrhhhhhhhmhhhhh\n" + "11-13 g: sggggggggggvzvg\n" + "1-3 d: dtdd\n" + "1-11 g: gggggmgggggggggs\n"
            + "10-11 j: bbmwbjhgnjjjwp\n" + "5-12 g: grtgxgnpsgwvnkd\n" + "6-8 p: lpvspswp\n" + "6-9 q: gfqfhrqccljrr\n"
            + "12-13 g: lsscgbnmxpggzj\n" + "6-9 x: kjvxpzxfdxmwcx\n" + "2-4 c: gcnr\n" + "1-5 z: zzzzz\n"
            + "7-19 c: lcjvqcmldcvwgnpjcsc\n" + "1-3 p: pppmpg\n" + "11-17 v: vvvvvvvvvvvvvvvvsvvv\n" + "4-5 l: llwwl\n"
            + "2-6 n: nnnhnn\n" + "3-5 c: cgxcp\n" + "2-5 p: plpptpphp\n" + "4-7 b: bbbbbbw\n" + "3-12 r: rcrfzgzwxcbrxfpd\n"
            + "16-20 h: hzdhhjphnmmtrftftzsh\n" + "6-7 g: gnqbvgjfxt\n" + "4-5 d: ddddf\n" + "4-9 w: wwwwwwgwnwwwwwwwlww\n"
            + "11-13 q: qqqqjqfcqrsqzqqq\n" + "4-5 l: nltls\n" + "7-10 n: nnwnnnnnnnjndf\n" + "3-4 s: xsnqs\n"
            + "13-14 c: ccccccckzccccscc\n" + "5-6 z: zzzzzczz\n" + "3-5 j: vjjkjcc\n" + "5-6 x: xnsxvxxl\n"
            + "13-15 l: llllllllllllllh\n" + "7-8 b: jmbgbvcrb\n" + "1-4 g: gbgggg\n" + "9-11 z: zqdlwbzjtsz\n"
            + "1-4 x: xxxwbxx\n" + "8-10 w: wwlwwwwwww\n" + "4-5 z: zchzztz\n" + "1-2 m: ckjdmmm\n"
            + "17-18 j: mhjzljjjjnzjjjjblpj\n" + "3-9 b: bswsbvdld\n" + "3-9 m: fsmmcfvlmbztcxkqs\n"
            + "16-17 c: ccccccccncccccchc\n" + "7-14 z: zzzzzzzzzzczzzzzd\n" + "4-16 z: zzdwzzzkzzzzzzztzzzz\n"
            + "11-18 p: ppppppppqppppppppl\n" + "2-3 f: zfrbzsh\n" + "2-9 w: tllwlmbkwq\n" + "1-7 h: hhhhhhhhhb\n"
            + "11-19 p: hmglpprgflrqmpxjbpw\n" + "10-13 p: rppppppppdppfpp\n" + "1-4 g: xgkgkp\n" + "4-6 k: zkkkshvkk\n"
            + "2-4 n: kclktpqlmmpwmlw\n" + "8-9 b: qbbbbvbtqbbj\n" + "3-4 w: wwwj\n" + "1-8 w: wqwwwjww\n" + "6-7 v: vvvvvkvv\n"
            + "4-6 f: nrjwffh\n" + "4-5 h: fhjzl\n" + "5-6 b: bbbbfbbbbbbb\n" + "4-7 v: vvnddvvwcxrvvfkvhv\n"
            + "4-12 t: sbktxtktxlvcpltntc\n" + "11-13 x: xvrtdxxxxtrxfxvq\n" + "3-4 l: llllll\n" + "7-8 h: hhhhhhhgh\n"
            + "1-10 z: zzzzzbzzzzzfzz\n" + "3-7 w: wwwwwwww\n" + "18-19 d: ddddddddddddvdddddx\n" + "5-6 l: pllkmsplcllllblll\n"
            + "7-12 g: ggggggjgggggggk\n" + "10-11 p: prppbcpppzpppp\n" + "4-5 v: kjgfvrt\n" + "3-8 k: kvkkkzkkrk\n"
            + "6-13 p: cchphppnshjpgvh\n" + "3-6 j: jfhjjlj\n" + "7-11 v: vgvbjwvvrgcvvvcv\n" + "5-12 b: bbbbdbbbbbbmbb\n"
            + "2-4 b: cxbb\n" + "8-13 h: hhhhhhhhhhhhhhx\n" + "6-13 w: wcdnrrhwzzwpr\n" + "1-4 x: hxgp\n"
            + "9-17 t: xttgrkxptthzljhwl\n" + "10-11 w: sqhzqtswsww\n" + "2-3 b: cbrbbpbb\n" + "1-6 m: mcwmmlxn\n"
            + "9-11 b: qbcjjjstpslqzwbkbp\n" + "12-14 n: nnnbxnnzhfvqrclnj\n" + "6-12 z: zzzzzvzzzzzfz\n"
            + "17-18 j: jjjjjjjjjjjjjjjjdjj\n" + "1-3 k: mkbk\n" + "4-6 j: znrjhcjjlj\n" + "6-8 b: hmsbbkcwfvbbnb\n"
            + "2-8 w: wwwwwwwwf\n" + "4-6 v: pvvqrp\n" + "8-10 h: mbhzhhhhhkhhhhhhh\n" + "5-6 l: lllllmll\n"
            + "1-11 p: nppppppppppp\n" + "11-13 b: bbbbbbbbxbxbb\n" + "4-6 l: llljll\n" + "1-4 x: xxxxx\n" + "2-6 f: fffffm\n"
            + "8-13 k: rnqxkkjkmkkqskkkk\n" + "3-4 w: dqdsbb\n" + "6-7 c: cccvctzcczmcccb\n" + "10-13 k: kkkkkkklmzgkk\n"
            + "9-12 d: ddddddddcddddddd\n" + "7-9 v: frvvvgvftksnvpvw\n" + "13-15 d: djdddddddddddddddd\n"
            + "6-8 t: tttttftrt\n" + "5-6 w: wlwwwrww\n" + "5-8 m: mmmpnpnpcmj\n" + "2-5 p: kpkppp\n"
            + "10-11 d: xtlgxpfjdczm\n" + "6-7 v: vvnvrvvvvb\n" + "8-10 g: ggggggglvgggg\n" + "8-15 g: kngggxggggggggg\n"
            + "1-11 n: ntngqrhnnrxjnnnnwxnn\n" + "2-5 k: kckkbpkvxhkz\n" + "1-4 j: jtjjvjjjj\n" + "15-16 x: xxxxxxxxxxxxxxxdx\n"
            + "7-8 r: rrrrrzxp\n" + "5-13 m: zzrqmklcmhdmrzfz\n" + "6-14 h: hhhhhphhhhhhhhh\n" + "4-6 c: brchqcqwcwcthccc\n"
            + "16-17 v: zmvqhnjbhvvrbrpvv\n" + "9-12 p: pppppqvpkppsnppp\n" + "1-4 d: mddnd\n" + "3-4 s: sssj\n"
            + "2-3 d: kmbsdpqkhj\n" + "11-15 f: ffffftxbjdfffcnfcf\n" + "8-15 t: tgptpthtttqctbt\n" + "1-3 l: wfklr\n"
            + "14-16 m: trxpshgmzpwmsmcmzgwk\n" + "5-10 m: jdhjmlqrqmmpfqsj\n" + "3-10 t: tctnlmvvtt\n"
            + "9-11 c: vxzmqpcxgnc\n" + "4-5 z: fzszzbzhsfxcmh\n" + "1-3 d: dddddd\n" + "3-9 j: mhjkjljjjz\n"
            + "14-17 n: nnnnnnnnnnnnnnnnh\n" + "4-5 l: hjspb\n" + "4-5 z: bmjvtmbq\n" + "4-5 v: vgvvvvv\n"
            + "3-8 x: mgzfmbdxxgm\n" + "5-6 t: tdxxqj\n" + "3-10 m: mmlbmbrlmmmmb\n" + "12-13 b: bbbbbbbbbbbztbbbbb\n"
            + "4-7 d: dtbdbzdsnkgbvsswdhww\n" + "7-8 c: cckccccg\n" + "1-6 r: jzqrrrdrtsrlrrvmr\n" + "4-5 x: xxxqh\n"
            + "3-6 v: vvvvvcv\n" + "6-8 x: xbxxxxbxfgxcnxgb\n" + "10-11 n: jnbnnnnnnbnwdnnn\n" + "5-8 h: hhhhvhbh\n"
            + "3-8 x: xfgksplxx\n" + "4-9 x: xxxxxcxxhxxx\n" + "4-7 l: lsplglldx\n" + "12-15 d: xxcqmmwdddzttkdldrml\n"
            + "7-8 w: wwhnlwfw\n" + "4-5 t: nvtdt\n" + "3-4 q: qhqr\n" + "3-7 j: jjjjjjjjhj\n" + "8-10 x: xlxxpxxsxxxxdx\n"
            + "3-8 k: kflkcvvthxkkkczlh\n" + "6-7 v: kfvlgvn\n" + "16-17 f: ffffffffffffffffrf\n" + "6-7 z: zzczzhfvnz\n"
            + "1-4 f: fsfs\n" + "6-7 b: pqbsmfsv\n" + "2-3 n: nlbn\n" + "4-5 b: bpbdbbbbnsbbbxb\n" + "4-12 n: csgndnqnsjjvxn\n"
            + "13-16 r: rrrrrrzrrrrrqrrrrrr\n" + "13-15 t: ttttttttttttttqtt\n" + "1-8 q: qqqtqtql\n"
            + "6-10 t: ttttttttzwtttttt\n" + "4-10 v: vvvsvvvvvqvvvv\n" + "3-5 w: wwwtl\n" + "3-8 z: qgkszmzkp\n"
            + "4-6 w: wwwvwgww\n" + "13-15 v: nvvvvvvvvdzvvvr\n" + "10-11 v: gvmwdpgpvvb\n" + "4-11 g: gggjgggvmgggg\n"
            + "1-6 z: fzzzzzzzzz\n" + "6-9 r: dmrrhxrrbrr\n" + "1-2 t: tmttv\n" + "5-13 d: jxvctbwmkpbqd\n"
            + "6-8 w: wbwlhwdw\n" + "17-18 x: qdzkpnhbdxcxsfsxkx\n" + "3-5 w: wwwww\n" + "2-3 n: xnjnl\n" + "2-9 f: ffffxffff\n"
            + "5-6 g: sgggjzg\n" + "7-10 h: zlvnhhrhlz\n" + "10-11 h: hhhhhhrhhhhhh\n" + "6-7 k: kpvtkkkk\n"
            + "9-17 b: vbbwhjntdzhbbhmbbq\n" + "6-8 h: hlhhhhrdjncphc";

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part2() {
        var lines = INPUT.split("\n");
        var i     = 0;
        for (var line : lines) {
            if (testTobogganPassword(line)) {
                ++i;
            }
        }
        System.out.printf("%d passwords are toboggan valid.%n", i);
    }

    private static boolean testTobogganPassword(String line) {
        var halves      = line.split(": ");
        var ruleString  = halves[0];
        var password    = halves[1];
        var rule        = ruleString.split(" ");
        var countString = rule[0];
        var counts      = countString.split("-");
        var firstString = counts[0];
        var first       = Integer.parseInt(firstString);
        var lastString  = counts[1];
        var last        = Integer.parseInt(lastString);
        var letter      = rule[1].charAt(0);
        var a           = checkLetter(password, first, letter);
        var b           = checkLetter(password, last, letter);
        var result      = a != b;
        System.out.println("line: " + line + " validity: " + result);
        return result;
    }

    private static boolean checkLetter(String password, int position, char letter) {
        var result = position - 1 < password.length() && password.charAt(position - 1) == letter;
        String value;
        if (position - 1 >= password.length()) {
            value = "none";
        } else {
            value = Character.toString(password.charAt(position - 1));
        }
        System.out.println("password " + password + " position: " + position + " checked: " + letter + " found: " + value);
        return result;
    }

    private static void part1() {
        var lines = INPUT.split("\n");
        var i     = 0;
        for (var line : lines) {
            if (testPassword(line)) {
                ++i;
            }
        }
        System.out.printf("%d passwords are sled rental valid.%n", i);
    }

    private static boolean testPassword(String line) {
        var halves = line.split(": ");
        var ruleString     = halves[0];
        var password = halves[1];
        var rule        = ruleString.split(" ");
        var countString = rule[0];
        var counts      = countString.split("-");
        var minString   = counts[0];
        var min = Integer.parseInt(minString);
        var maxString   = counts[1];
        var max = Integer.parseInt(maxString);
        var letter      = rule[1].codePointAt(0);
        var count = countLetter(password, letter);
        return min <= count && count <= max;
    }

    private static int countLetter(String string, int letter) {
        var count = 0;
        var prev  = 0;
        for (prev = string.indexOf(letter); prev >= 0; prev = string.indexOf(letter, prev + 1)) {
            ++count;
        }
        System.out.println(string + " has " + count + " '" + ((char) letter) + "'s.");
        return count;
    }

}
