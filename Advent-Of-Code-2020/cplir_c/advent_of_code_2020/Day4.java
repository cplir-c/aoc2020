package cplir_c.advent_of_code_2020;

import java.util.HashMap;
import java.util.Map;

public class Day4 {
    static final int BYR = 1;
    static final int IYR = BYR << 1;
    static final int EYR = IYR << 1;
    static final int HGT = EYR << 1;
    static final int HCL = HGT << 1;
    static final int ECL = HCL << 1;
    static final int PID = ECL << 1;
    static final int CID = PID << 1;
    static final String                      PASSPORTS = "iyr:2015\n"
            + "hgt:59cm byr:2029 cid:219 pid:9381688753 eyr:1992 hcl:#b6652a\n" + "ecl:#7a0fa6\n" + "\n"
            + "ecl:blu iyr:2018 pid:943614755 cid:335\n" + "byr:1968\n" + "eyr:2026\n" + "\n"
            + "pid:067285985 hcl:#ceb3a1 cid:281\n" + "ecl:#07219a eyr:1944\n" + "iyr:2025\n" + "byr:2029 hgt:64cm\n" + "\n"
            + "hgt:185cm\n" + "ecl:gry cid:222\n" + "iyr:2016\n" + "hcl:#866857 byr:1970 pid:269105457 eyr:2026\n" + "\n"
            + "pid:260043570 hcl:#b6652a cid:275 byr:1990 ecl:brn\n" + "hgt:163cm iyr:2012\n" + "\n"
            + "hgt:181cm pid:604983466\n" + "iyr:1930 eyr:2039 byr:1950 ecl:#906548 hcl:#b6652a\n" + "\n"
            + "iyr:2025 eyr:1956 hcl:z pid:#1c42cc byr:2006\n" + "cid:327 hgt:141 ecl:#f2affc\n" + "\n"
            + "hgt:178cm byr:1939 pid:595705064 ecl:oth\n" + "iyr:2020 eyr:2026\n" + "hcl:#888785\n" + "\n"
            + "hgt:159cm iyr:2016\n" + "hcl:#efcc98 pid:139063139 byr:1980 ecl:brn\n" + "eyr:2020\n" + "\n"
            + "pid:646870519 hgt:179cm eyr:2022 iyr:2011 hcl:#602927\n" + "ecl:brn\n" + "byr:1997\n" + "\n"
            + "hgt:170cm hcl:#ceb3a1 iyr:2014 eyr:2023 ecl:oth pid:243067344 byr:1962\n" + "\n" + "hcl:#866857\n"
            + "ecl:oth pid:704529614\n" + "byr:1941 cid:94\n" + "eyr:2026 hgt:180cm\n" + "iyr:2010\n" + "\n" + "iyr:1924\n"
            + "pid:36196401\n" + "hgt:74cm eyr:1921\n" + "ecl:#3acf57 hcl:a4e4c0 byr:2024\n" + "cid:153\n" + "\n"
            + "pid:770262094 hcl:#866857\n" + "eyr:2020 hgt:151cm\n" + "ecl:blu\n" + "iyr:2012\n" + "byr:2002\n" + "cid:242\n"
            + "\n" + "pid:984364862 ecl:dne\n" + "iyr:2020\n" + "hgt:151 eyr:2023 cid:314 hcl:z byr:2012\n" + "\n"
            + "hgt:178cm iyr:2020 hcl:#6b5442 ecl:grn cid:323 eyr:2030 byr:1925 pid:285882039\n" + "\n"
            + "iyr:2019 pid:986123633\n" + "eyr:2024 byr:1990 hcl:#7d3b0c ecl:hzl hgt:192cm\n" + "\n" + "hgt:90\n"
            + "byr:2025 iyr:1933\n" + "ecl:dne eyr:2040 pid:8194347544\n" + "\n"
            + "hgt:163cm byr:1934 eyr:2026 ecl:amb hcl:#eec6fb cid:303 pid:721792159 iyr:2013\n" + "\n" + "iyr:2019\n"
            + "byr:1920 hcl:#a97842\n" + "cid:186 eyr:2020\n" + "ecl:oth\n" + "hgt:167cm pid:217112082\n" + "\n"
            + "pid:#55ce6b hcl:d30f6b eyr:2040 hgt:60cm ecl:dne iyr:1920\n" + "cid:107 byr:2029\n" + "\n"
            + "ecl:amb eyr:2024 pid:644304174 hcl:#6b5442 iyr:2018\n" + "byr:1935\n" + "hgt:182cm\n" + "\n"
            + "ecl:hzl pid:559383552\n" + "hcl:#ceb3a1 eyr:2024 hgt:161cm byr:1968 iyr:2010\n" + "\n" + "iyr:2018\n"
            + "hcl:43fafb\n" + "hgt:65cm eyr:2027\n" + "byr:1937 pid:#4bff3e ecl:grt\n" + "\n" + "eyr:2024\n"
            + "iyr:2014 cid:163 byr:1924 hcl:#18171d\n" + "hgt:166cm\n" + "\n" + "eyr:2026 pid:955203781\n"
            + "iyr:2016 cid:52 hgt:167cm\n" + "ecl:grn byr:1963\n" + "\n" + "pid:479898570 hgt:165cm eyr:2024 byr:1932\n"
            + "iyr:2010 ecl:grn\n" + "cid:88\n" + "hcl:#c0a76e\n" + "\n"
            + "cid:241 hgt:178cm ecl:blu pid:069760797 hcl:#623a2f byr:1925 eyr:2029 iyr:2019\n" + "\n" + "hgt:172cm eyr:2036\n"
            + "iyr:2016 pid:#98caec\n" + "ecl:dne hcl:z\n" + "\n"
            + "ecl:#510672 iyr:1938 byr:2018 hgt:172in hcl:z cid:339 eyr:2039\n" + "pid:#6c1216\n" + "\n" + "hcl:#efcc98\n"
            + "byr:1972 ecl:brn iyr:2011 pid:190911803 eyr:2025 hgt:171cm\n" + "\n" + "pid:0636917222 byr:2009 hgt:96\n"
            + "hcl:z\n" + "iyr:1997 ecl:hzl eyr:2026\n" + "\n" + "byr:1989 iyr:2011 pid:071588682 cid:155 ecl:grn\n"
            + "hcl:#ceb3a1 eyr:1955 hgt:170cm\n" + "\n" + "cid:266 hcl:#a97842 byr:1964 hgt:175cm\n" + "iyr:2017 ecl:brn\n"
            + "\n" + "pid:930133867 ecl:grn hcl:#733820 hgt:63in byr:1995\n" + "eyr:2021 iyr:2014\n" + "\n"
            + "eyr:2025 pid:284329794\n" + "ecl:blu hcl:#ceb3a1 iyr:2012\n" + "hgt:65in byr:1961\n" + "\n"
            + "iyr:2010 byr:1998\n" + "hgt:160cm\n" + "eyr:2029 hcl:#cfa07d\n" + "pid:253052921\n" + "ecl:amb cid:324\n" + "\n"
            + "pid:026835791 byr:1999 eyr:2022 hgt:162cm\n" + "hcl:#7d3b0c ecl:brn iyr:2014\n" + "\n"
            + "pid:672752198 eyr:2030 byr:1952 hgt:65in iyr:2016 ecl:amb\n" + "hcl:#cfa07d\n" + "\n" + "hgt:193in\n"
            + "byr:2019 hcl:z pid:#cbc08c iyr:1951 ecl:#3e9f2f eyr:2002\n" + "\n" + "ecl:utc pid:571477176\n"
            + "byr:2012 eyr:1929 cid:240\n" + "hgt:175in hcl:f4ef32\n" + "\n" + "cid:93 hcl:#a5db2a\n"
            + "pid:274721479 byr:1940 eyr:2022 ecl:gry\n" + "hgt:157cm iyr:2012\n" + "\n"
            + "pid:540858450 iyr:2014 cid:95 byr:1964\n" + "hgt:156cm hcl:#866857 ecl:brn eyr:2026\n" + "\n"
            + "pid:532626994 byr:1939 iyr:2017\n" + "ecl:blu eyr:2026\n" + "hcl:#fffffd hgt:184cm\n" + "\n"
            + "hgt:70 pid:404622083\n" + "iyr:2026\n" + "byr:2022 hcl:c1ba7f eyr:1979 ecl:lzr\n" + "\n" + "pid:931910908\n"
            + "cid:177 hcl:#6b5442\n" + "ecl:gry hgt:184cm\n" + "byr:1963 eyr:2020\n" + "iyr:2014\n" + "\n"
            + "iyr:2019 eyr:2022 hcl:#ceb3a1 hgt:191cm ecl:gry pid:954124659 cid:123 byr:1939\n" + "\n"
            + "pid:411032659 byr:1950\n" + "hgt:153cm eyr:2020 iyr:2014 ecl:hzl\n" + "\n"
            + "hgt:156cm eyr:2023 pid:29836124 byr:2017 hcl:56de83 ecl:zzz cid:179\n" + "iyr:2018\n" + "\n"
            + "hcl:#866857 iyr:2014 hgt:190cm byr:1998 pid:565524574 eyr:2020\n" + "\n"
            + "byr:1973 hcl:#888785 iyr:2016 eyr:2028 hgt:173cm ecl:blu\n" + "\n" + "byr:1987\n" + "pid:028825120 hcl:#7d3b0c\n"
            + "eyr:2023 hgt:190cm ecl:oth iyr:2014\n" + "\n" + "eyr:2036 pid:172661617\n" + "ecl:#ae607d byr:2017 hcl:z\n"
            + "hgt:82 cid:153\n" + "\n" + "pid:202888577 eyr:2028 iyr:2013\n" + "byr:1933\n"
            + "hgt:68in cid:151 hcl:#b6652a ecl:brn\n" + "\n" + "iyr:2020\n"
            + "ecl:amb eyr:2025 hcl:#a355be hgt:63in pid:146650894\n" + "\n" + "iyr:2016 hgt:192cm pid:531372965 hcl:#fffffd\n"
            + "ecl:blu eyr:2025\n" + "\n" + "eyr:2025 ecl:blu byr:1961 cid:224 iyr:2016 hcl:#6b5442 pid:368694418\n"
            + "hgt:169cm\n" + "\n" + "pid:43707504 iyr:1945\n" + "ecl:grt byr:2010\n" + "eyr:2026 cid:273\n"
            + "hgt:165in hcl:z\n" + "\n" + "hgt:159cm ecl:gry\n" + "hcl:#6b5442\n" + "eyr:2030 pid:915819272 iyr:2015\n" + "\n"
            + "pid:808392314 ecl:gry cid:285 hcl:#efcc98 byr:1923 hgt:161cm iyr:1941 eyr:2020\n" + "\n" + "iyr:2017\n"
            + "hgt:161cm\n" + "eyr:2025 hcl:#602927 ecl:oth pid:081917611 byr:1983\n" + "\n"
            + "eyr:2028 pid:831032131 ecl:brn iyr:2013 hcl:#341e13 cid:198 byr:1991 hgt:67in\n" + "\n"
            + "hgt:181cm cid:320 pid:032769757 ecl:grn hcl:#733820\n" + "eyr:2022 byr:1992\n" + "\n"
            + "iyr:2010 cid:128 hgt:171cm byr:1932 pid:923377839 ecl:brn\n" + "hcl:#18171d eyr:2020\n" + "\n"
            + "ecl:hzl iyr:2021 byr:2008 pid:569583509 hcl:f74823\n" + "hgt:188in\n" + "\n"
            + "iyr:2016 hcl:z eyr:2021 ecl:#24ceee pid:349492243 hgt:67cm\n" + "cid:144 byr:2010\n" + "\n" + "ecl:gry\n"
            + "byr:2029 hcl:3a0c30 hgt:163in eyr:1962\n" + "\n" + "byr:1927 hgt:180\n" + "cid:87\n" + "ecl:#7ea777\n"
            + "hcl:#623a2f iyr:2024 pid:597098940 eyr:2027\n" + "\n" + "cid:89 hgt:193cm hcl:#623a2f\n" + "iyr:2010 eyr:2026\n"
            + "pid:374988952 ecl:hzl byr:1973\n" + "\n" + "eyr:2023 iyr:2013 byr:1977\n"
            + "cid:329 pid:711256829 ecl:grn hgt:154cm\n" + "hcl:#866857\n" + "\n" + "pid:212535692 ecl:brn\n"
            + "hcl:#b6652a hgt:169cm eyr:2025 byr:1920 iyr:2019\n" + "\n" + "ecl:blu\n" + "byr:1962\n"
            + "hgt:157cm iyr:2020 eyr:2027 pid:451039029\n" + "hcl:#6b5442\n" + "\n"
            + "hgt:187cm pid:187808959 eyr:2026 iyr:2020\n" + "ecl:oth\n" + "byr:1956 hcl:#733820\n" + "\n"
            + "byr:1959 hgt:160cm ecl:blu hcl:#6b5442\n" + "cid:193 eyr:2026\n" + "iyr:2014\n" + "pid:812555315\n" + "\n"
            + "hgt:153cm iyr:2011\n" + "ecl:grn hcl:#ceb3a1\n" + "eyr:2026 byr:1966 pid:503356330\n" + "\n" + "ecl:#95d8a9\n"
            + "eyr:2024 pid:382174744\n" + "iyr:2025\n" + "hgt:152 hcl:#888785 byr:2012\n" + "\n" + "eyr:2028\n"
            + "iyr:2017 byr:1938\n" + "cid:279 hcl:#733820 ecl:amb pid:497365268 hgt:191cm\n" + "\n"
            + "cid:335 byr:1982 hgt:171cm iyr:2013\n" + "ecl:hzl eyr:2030\n" + "hcl:#efcc98 pid:018900639\n" + "\n"
            + "eyr:2029 hgt:175cm pid:530128340\n" + "hcl:#888785\n" + "ecl:gry\n" + "byr:1947 iyr:2019\n" + "\n"
            + "hgt:183cm\n" + "hcl:#6b5442 eyr:2023 ecl:grn\n" + "byr:1934\n" + "\n" + "hcl:f8ed45 cid:54 iyr:1997\n"
            + "hgt:69cm eyr:2037 ecl:gry\n" + "pid:184cm byr:2012\n" + "\n" + "ecl:grn hcl:#733820 byr:1928 pid:002528194\n"
            + "iyr:2014 eyr:2021 hgt:157cm\n" + "\n" + "hgt:163in\n" + "hcl:#c0946f byr:2018 eyr:2021\n"
            + "iyr:1955 ecl:#216920 pid:87155266\n" + "cid:298\n" + "\n"
            + "eyr:2026 byr:1945 cid:161 iyr:2017 hgt:170cm hcl:#fffffd ecl:hzl pid:649441221\n" + "\n" + "byr:1930\n"
            + "iyr:2014 pid:151910079 hcl:#18171d ecl:oth eyr:2029\n" + "hgt:169cm\n" + "\n"
            + "ecl:blu byr:1950 iyr:2010 cid:260 hcl:#cfa07d\n" + "hgt:167cm\n" + "pid:910685738 eyr:2021\n" + "\n"
            + "hgt:182cm byr:1993\n" + "eyr:2030 pid:073035999 hcl:#341e13\n" + "cid:117\n" + "\n" + "byr:1981\n"
            + "hcl:#866857\n" + "eyr:2028 iyr:2012 ecl:blu pid:620133246 hgt:157cm\n" + "\n" + "hgt:191cm\n"
            + "iyr:2010 pid:089995590 eyr:2023 ecl:amb byr:1986 hcl:#733820\n" + "\n" + "iyr:2019 ecl:gry\n"
            + "hgt:165cm pid:910093364 hcl:#efcc98 byr:1997\n" + "eyr:2028\n" + "cid:153\n" + "\n"
            + "hgt:83 hcl:174774 eyr:2032\n" + "ecl:xry iyr:2017 byr:1940\n" + "\n" + "byr:1943\n" + "pid:980352645\n"
            + "iyr:2015 hgt:66 eyr:2023 hcl:#b6652a ecl:oth\n" + "\n"
            + "ecl:amb byr:1980 hgt:164cm pid:775303596 hcl:#671bed iyr:2013 eyr:2030\n" + "\n"
            + "hgt:173cm byr:1947 eyr:1947 iyr:1940 ecl:gmt hcl:7e515c\n" + "\n" + "hcl:#b6652a\n" + "iyr:2012\n"
            + "eyr:2030 hgt:185cm ecl:grn\n" + "\n" + "ecl:amb byr:1940 hcl:#2943a5 iyr:2015\n" + "hgt:185cm pid:931660417\n"
            + "eyr:2021\n" + "\n" + "eyr:1957 hcl:#623a2f\n" + "ecl:grt hgt:62cm pid:#af106a iyr:2012\n" + "cid:59 byr:1985\n"
            + "\n" + "ecl:amb eyr:2025\n" + "pid:351412754 iyr:2014 byr:1941 hcl:#6b5442 hgt:174cm\n" + "\n"
            + "pid:5621200134 hcl:6ef9ba ecl:#ef68f5 eyr:1924\n" + "hgt:63cm cid:188 byr:2004\n" + "\n"
            + "hcl:#a97842 byr:1976 eyr:2020 hgt:171cm pid:041926354 iyr:2019\n" + "\n" + "cid:234\n"
            + "byr:2025 hcl:98619a pid:181cm eyr:1941\n" + "iyr:2021\n" + "hgt:167in ecl:#f5e651\n" + "\n"
            + "hgt:73cm eyr:2028 byr:1985 iyr:1949 hcl:z ecl:utc cid:207 pid:#ee9f95\n" + "\n"
            + "pid:179cm eyr:2030 hcl:b8e142\n" + "hgt:69cm\n" + "iyr:1933\n" + "byr:1934\n" + "ecl:grn\n" + "\n"
            + "iyr:2028 eyr:1954 hgt:111 cid:180 pid:183391861\n" + "byr:2030 hcl:1fb30f ecl:#0d0160\n" + "\n"
            + "ecl:#0b3b2d hgt:191cm byr:2023 pid:727024676 eyr:2025 hcl:#b6652a\n" + "\n" + "hgt:66in\n"
            + "byr:1923 eyr:2023 ecl:gry\n" + "pid:454789451 iyr:2013 hcl:#cfa07d\n" + "\n" + "eyr:2020\n" + "pid:339972685\n"
            + "ecl:amb\n" + "iyr:2017 byr:1926 hgt:154cm\n" + "hcl:#18171d\n" + "\n" + "ecl:oth cid:302\n" + "byr:1946\n"
            + "hcl:#ceb3a1\n" + "pid:622779476 eyr:2024 iyr:2012 hgt:158cm\n" + "\n" + "byr:2012\n"
            + "pid:748786877 hgt:135 iyr:2016 hcl:b6e962 ecl:gry eyr:2011\n" + "\n" + "byr:1997\n" + "hcl:#a97842\n"
            + "eyr:2022 pid:325672898 ecl:amb hgt:190cm iyr:2010\n" + "\n" + "cid:210 hcl:#c0946f byr:1957 eyr:2022\n"
            + "iyr:2020 pid:374646087 ecl:blu hgt:184cm\n" + "\n" + "eyr:2029 ecl:#353e0f\n" + "pid:#66ec82\n"
            + "byr:2023 hcl:10d9d8 cid:271\n" + "\n" + "pid:816485054\n" + "eyr:2019 ecl:grn\n"
            + "hcl:#efcc98 hgt:185cm iyr:2013\n" + "byr:2014\n" + "\n"
            + "hcl:#866857 iyr:2014 byr:1953 eyr:2022 ecl:blu hgt:166cm\n" + "\n" + "pid:162cm hgt:59cm iyr:1981\n"
            + "eyr:2025 byr:2009\n" + "ecl:gmt hcl:116742\n" + "\n" + "eyr:2028 hgt:67cm hcl:3d1f34 byr:1963 pid:62859332\n"
            + "ecl:dne\n" + "iyr:2023\n" + "\n" + "iyr:2013\n"
            + "pid:271450754 eyr:2016 hcl:e20882 cid:186 hgt:157in ecl:utc byr:2023\n" + "\n"
            + "pid:702200026 eyr:1968 ecl:gmt hcl:#888785 iyr:2018 hgt:193in byr:1943\n" + "\n"
            + "eyr:2025 byr:1989 ecl:amb hcl:#866857 cid:119\n" + "hgt:191cm\n" + "pid:556011434\n" + "\n"
            + "hgt:178cm iyr:2013\n" + "pid:928476807\n" + "ecl:amb hcl:#623a2f byr:1996 eyr:2026\n" + "\n" + "cid:222\n"
            + "pid:325218825 eyr:2021 byr:1983 hgt:155cm ecl:brn iyr:2011\n" + "hcl:#fffffd\n" + "\n"
            + "pid:949344785 ecl:grn eyr:2025 cid:182 byr:1974 hcl:#ceb3a1\n" + "iyr:2011\n" + "\n"
            + "cid:269 pid:669599426 hgt:176cm ecl:blu byr:1957\n" + "iyr:2015 hcl:#623a2f eyr:2025\n" + "\n"
            + "eyr:2023 hcl:#888785\n" + "pid:178525132 iyr:2018 hgt:186cm\n" + "\n" + "ecl:hzl\n" + "byr:1940 iyr:2013\n"
            + "hgt:185cm eyr:2028\n" + "hcl:#7c73a3\n" + "\n" + "hcl:z\n" + "byr:2001 cid:292 ecl:#d56bbd pid:93473192\n"
            + "iyr:2003 hgt:150\n" + "eyr:1922\n" + "\n" + "eyr:2021 pid:786485899\n" + "hgt:170cm hcl:#efcc98 byr:1955\n"
            + "iyr:2010 ecl:brn\n" + "\n" + "hcl:#733820 ecl:hzl hgt:157cm byr:1944 eyr:2027 pid:906803629 iyr:2015\n" + "\n"
            + "hgt:151cm ecl:blu iyr:2016\n" + "hcl:#02ffd7 byr:1995\n" + "pid:369315941 eyr:2026\n" + "\n"
            + "cid:330 ecl:#18e883 eyr:2038\n" + "hcl:z iyr:1929\n" + "hgt:193 pid:33765426\n" + "\n"
            + "pid:743094345 eyr:2027\n" + "iyr:1949 byr:1955\n" + "ecl:gry\n" + "hgt:160cm hcl:8dae67\n" + "\n"
            + "cid:167 hcl:#18171d\n" + "iyr:2016 pid:214065645 byr:1942 eyr:2030 hgt:183cm ecl:hzl\n" + "\n"
            + "ecl:brn hcl:#623a2f cid:171 byr:1971\n" + "iyr:2011 eyr:2028\n" + "pid:607344613\n" + "hgt:153cm\n" + "\n"
            + "byr:1921 pid:677007802 hcl:#341e13 ecl:brn iyr:2012 hgt:188cm eyr:2028\n" + "\n"
            + "hgt:162cm cid:319 hcl:z iyr:2025\n" + "byr:1989 eyr:1939 pid:67311222\n" + "ecl:utc\n" + "\n"
            + "iyr:2014 eyr:2025 hgt:171cm\n" + "cid:302 byr:1997\n" + "hcl:z\n" + "ecl:amb pid:101363367\n" + "\n"
            + "ecl:oth iyr:2010\n" + "cid:96 hgt:164cm hcl:4bc20a byr:1947\n" + "pid:166115442 eyr:2030\n" + "\n" + "byr:1964\n"
            + "hcl:#6b5442 hgt:156cm eyr:2022 pid:426807062 ecl:brn cid:321 iyr:2012\n" + "\n"
            + "byr:2012 hcl:#888785 cid:298 eyr:1920 ecl:zzz hgt:169cm pid:0660316558 iyr:2019\n" + "\n"
            + "hcl:579266 byr:1931 pid:#aa5fd0 ecl:gry eyr:2017 hgt:60 iyr:1965\n" + "\n" + "iyr:2011\n"
            + "pid:610896691 hcl:#733820\n" + "byr:1936\n" + "ecl:gry eyr:2021 hgt:161cm\n" + "\n"
            + "pid:443246791 iyr:2015 hgt:158cm hcl:#18171d\n" + "byr:1928 ecl:brn cid:207\n" + "\n"
            + "byr:1950 pid:644579904 hcl:#b6652a\n" + "eyr:2027 iyr:2017\n" + "ecl:brn hgt:171cm\n" + "\n"
            + "iyr:2011 byr:1960\n" + "eyr:2023\n" + "hgt:171cm ecl:hzl\n" + "pid:331465564 cid:205 hcl:#18171d\n" + "\n"
            + "hgt:61cm eyr:1987 ecl:#9f458c byr:2023 pid:162cm hcl:z iyr:1997\n" + "\n" + "hcl:59e376 pid:065607649\n"
            + "iyr:2020\n" + "byr:2010 ecl:blu\n" + "\n" + "pid:167cm byr:2022 hgt:150cm ecl:#06650a hcl:caa145 eyr:2032\n"
            + "iyr:2015\n" + "\n" + "byr:1932\n" + "hcl:#419d73\n" + "cid:203 iyr:2017\n" + "pid:105921085\n" + "ecl:gry\n"
            + "\n" + "pid:501585534 hcl:#418895\n" + "iyr:2018\n" + "hgt:157cm byr:1940 ecl:hzl eyr:2027\n" + "\n"
            + "cid:220 hgt:171cm hcl:#623a2f\n" + "ecl:gry\n" + "iyr:2017\n" + "pid:085309709 eyr:2024 byr:1932\n" + "\n"
            + "hcl:#733820 eyr:2028 cid:93\n" + "iyr:2017\n" + "byr:1974 hgt:163cm ecl:grn pid:630322998\n" + "\n"
            + "hcl:#602927 cid:97 hgt:166cm eyr:2025\n" + "ecl:hzl iyr:2016 byr:1964 pid:355325363\n" + "\n"
            + "iyr:2016 pid:402228657 hgt:174cm byr:1993\n" + "eyr:2020 hcl:#733820 ecl:grn\n" + "\n"
            + "iyr:2020 hgt:171cm ecl:amb\n" + "hcl:#c0946f\n" + "byr:1939\n" + "cid:316 pid:782384470 eyr:2030\n" + "\n"
            + "byr:1983 pid:839608616\n" + "eyr:2026\n" + "hcl:#ceb3a1 cid:242\n" + "hgt:192cm ecl:hzl\n" + "\n"
            + "pid:701022732 byr:1931 ecl:amb\n" + "hgt:70in hcl:#341e13 eyr:2030 iyr:2013\n" + "\n" + "eyr:2027\n"
            + "pid:740692321 byr:1940\n" + "hgt:179cm ecl:blu cid:153 iyr:2010\n" + "\n"
            + "iyr:2024 hcl:z ecl:zzz hgt:181in pid:#c38620 eyr:1976 cid:97\n" + "byr:2029\n" + "\n"
            + "byr:1999 ecl:lzr hcl:6f29a6 eyr:2023\n" + "iyr:2018 cid:209 pid:401606571 hgt:163cm\n" + "\n" + "ecl:amb\n"
            + "byr:1996 hgt:181cm iyr:2018 hcl:#6b5442 pid:022285219 eyr:2021\n" + "\n" + "cid:93 pid:807990476\n"
            + "hgt:61in eyr:2027 hcl:#cfa07d ecl:oth iyr:2017\n" + "\n" + "hcl:#7d3b0c pid:225151503 iyr:2013 cid:68\n"
            + "eyr:2029\n" + "ecl:brn hgt:64in byr:1959\n" + "\n" + "eyr:2028 hgt:172in\n"
            + "iyr:2014 byr:1950 pid:187cm hcl:z ecl:brn\n" + "\n" + "byr:1982\n"
            + "pid:978263388 eyr:2021 hgt:175cm iyr:2014 ecl:brn hcl:#a97842\n" + "\n" + "hgt:162cm\n" + "eyr:2025\n"
            + "pid:6533951177 byr:1993 iyr:2011 hcl:#c0946f ecl:hzl\n" + "\n" + "pid:182cm\n" + "iyr:2025 eyr:2035 hgt:59in\n"
            + "ecl:#799f29 hcl:z\n" + "byr:1920 cid:202\n" + "\n" + "hcl:#733820\n"
            + "eyr:2022 hgt:185cm byr:1989 pid:195276207\n" + "ecl:blu iyr:2017\n" + "\n" + "hcl:#7d3b0c\n"
            + "cid:257 ecl:gry\n" + "pid:123065639 byr:1951 iyr:2013\n" + "\n"
            + "eyr:2039 ecl:#a82e90 byr:1927 pid:719738468 hgt:73cm\n" + "\n" + "hcl:605223\n" + "hgt:162cm pid:50424035\n"
            + "ecl:oth cid:343 byr:2025 iyr:2023 eyr:2024\n" + "\n" + "hcl:699116 iyr:2001\n" + "eyr:2022\n" + "byr:2013\n"
            + "hgt:171cm pid:8900968325\n" + "\n" + "hcl:#efcc98 eyr:2029 ecl:grn pid:568953221\n" + "byr:1986\n"
            + "hgt:178cm\n" + "iyr:2020\n" + "\n" + "pid:452235579 byr:1932\n" + "ecl:grn\n" + "iyr:2010 hgt:189cm eyr:2028\n"
            + "hcl:#602927 cid:258\n" + "\n" + "ecl:xry iyr:2009 cid:334 pid:189cm\n" + "eyr:2032 byr:2005 hgt:172in hcl:z\n"
            + "\n" + "hgt:159cm hcl:z pid:166cm\n" + "ecl:oth eyr:2026 iyr:2020\n" + "\n"
            + "eyr:2023 ecl:blu byr:1935 iyr:2015\n" + "hcl:#866857 pid:542611829\n" + "hgt:168cm\n" + "\n" + "pid:#ec3d53\n"
            + "hcl:#ceb3a1\n" + "byr:1999 eyr:2024\n" + "hgt:188cm ecl:oth iyr:2018\n" + "\n" + "byr:2003 hgt:167\n"
            + "hcl:486800\n" + "ecl:#29bdd6 eyr:2037 cid:169 iyr:2010\n" + "\n" + "byr:1983\n" + "eyr:2026 ecl:gry\n"
            + "pid:203934984\n" + "hgt:181cm iyr:2020 hcl:#a97842 cid:184\n" + "\n" + "hgt:180cm\n"
            + "iyr:1934 eyr:2038 hcl:#a97842 ecl:brn byr:1942 pid:427001597\n" + "\n" + "hcl:#18171d byr:1988\n"
            + "cid:267 hgt:188cm\n" + "ecl:amb\n" + "eyr:2028 pid:696617232\n" + "\n" + "eyr:2024 hcl:#cfa07d\n"
            + "iyr:2013 pid:176cm hgt:189cm byr:1990\n" + "ecl:gry\n" + "\n"
            + "eyr:2025 iyr:2015 hgt:153cm hcl:#ceb3a1 ecl:grn pid:686467422 byr:1961 cid:282\n" + "\n"
            + "byr:1931 hgt:185cm ecl:oth\n" + "eyr:2022\n" + "pid:561083684 hcl:#efcc98\n" + "iyr:2012\n" + "\n"
            + "byr:1948 cid:327 hgt:151cm\n" + "iyr:2016 hcl:#733820 ecl:oth pid:341978822\n" + "\n" + "hcl:#ceb3a1\n"
            + "byr:1978 iyr:2020 hgt:172cm\n" + "eyr:2022 ecl:oth pid:093317990\n" + "\n" + "eyr:2029\n"
            + "pid:096891409 iyr:2018\n" + "hcl:#d82822 hgt:174cm ecl:hzl\n" + "byr:1988\n" + "\n"
            + "hgt:170cm iyr:2018 pid:588142771 eyr:2022 hcl:#733820\n" + "cid:273 byr:1940 ecl:#a608fe\n" + "\n"
            + "iyr:2029 eyr:1980 hcl:#341e13 byr:2027 ecl:grt\n" + "pid:443809337 hgt:180cm\n" + "cid:205\n" + "\n"
            + "ecl:#f89df0 hgt:144 hcl:2f26ab iyr:1982 pid:#3b43c1 eyr:2032 byr:2012\n" + "\n" + "ecl:hzl byr:1971\n"
            + "pid:030850749\n" + "hgt:170in\n" + "hcl:#ceb3a1 eyr:2023 iyr:2018\n" + "\n" + "byr:1940 iyr:2020\n"
            + "eyr:2026 pid:437820254\n" + "hgt:179cm ecl:gry\n" + "\n" + "byr:2028\n" + "eyr:1986 hcl:z\n"
            + "hgt:185in pid:773739744 ecl:dne iyr:2020\n" + "\n" + "hcl:#a97842\n" + "hgt:186cm cid:64 iyr:2016\n"
            + "byr:1947 eyr:2021\n" + "\n" + "byr:1988 hgt:160cm eyr:2023 hcl:#866857 pid:788805179 iyr:2022 ecl:amb\n" + "\n"
            + "hgt:164cm byr:1996 cid:338 hcl:#efcc98\n" + "eyr:2029 pid:208596014 ecl:blu\n" + "\n"
            + "pid:357680064 byr:1960 eyr:2029 ecl:gry hgt:192cm hcl:#c0946f\n" + "\n" + "ecl:#d32320\n"
            + "hgt:167in pid:19531341\n" + "hcl:z\n" + "cid:346 iyr:2024 byr:2006 eyr:2035\n" + "\n"
            + "pid:843729120 byr:1987 hgt:185cm eyr:2022\n" + "ecl:amb\n" + "iyr:2012 hcl:#c0946f\n" + "\n"
            + "eyr:2020 byr:1961 iyr:2011\n" + "hgt:162cm cid:54 pid:891397982 ecl:brn\n" + "\n"
            + "ecl:zzz byr:2019 iyr:2015 eyr:2028 hcl:43d56d\n" + "hgt:152cm\n" + "pid:182cm\n" + "\n"
            + "hcl:#18171d byr:1979 hgt:174cm\n" + "iyr:2013 cid:228 eyr:2022 ecl:amb pid:82422450\n" + "\n"
            + "cid:156 iyr:2017\n" + "byr:1924\n" + "hcl:#b6652a ecl:gry hgt:184cm eyr:2027 pid:451347151\n" + "\n"
            + "pid:850192502 hgt:65in\n" + "iyr:2011 hcl:#7d3b0c\n" + "eyr:2023 ecl:gry\n" + "\n"
            + "ecl:amb hgt:181cm iyr:2017 pid:233345009 byr:1934\n" + "hcl:#341e13\n" + "eyr:2024 cid:199\n" + "\n"
            + "eyr:2026 pid:#4cb480\n" + "iyr:1958 hgt:176cm ecl:dne hcl:z\n" + "\n"
            + "ecl:grn eyr:2027 hgt:178cm byr:1994 hcl:#341e13\n" + "iyr:2016 pid:790075315\n" + "\n" + "pid:140922484\n"
            + "byr:1958\n" + "eyr:2025\n" + "iyr:2019 ecl:brn hgt:157cm hcl:#623a2f\n" + "\n"
            + "pid:466785488 hgt:160cm hcl:#cfa07d\n" + "byr:1947\n" + "iyr:2010\n" + "cid:198 eyr:2020 ecl:hzl\n" + "\n"
            + "ecl:oth\n" + "eyr:2022 byr:1963\n" + "hcl:#fffffd iyr:2017\n" + "hgt:171cm pid:463249115\n" + "\n"
            + "hgt:73cm byr:1968\n" + "pid:470317690 ecl:blu\n" + "iyr:2015 hcl:#c0946f cid:54 eyr:2029\n" + "\n"
            + "hgt:162cm iyr:2014\n" + "byr:1951 hcl:#b6652a eyr:2029 ecl:blu\n" + "\n" + "ecl:oth\n"
            + "hgt:176cm hcl:#888785 byr:1963\n" + "iyr:2017 pid:453133253 eyr:2025\n" + "\n" + "hcl:#efcc98\n"
            + "eyr:2024 iyr:2020 cid:330 byr:1950 pid:937122408 ecl:gry hgt:162cm\n" + "\n" + "hgt:168cm\n" + "pid:745867335\n"
            + "cid:165 hcl:#c0946f iyr:2018 ecl:grt eyr:2030\n" + "byr:1932\n" + "\n" + "byr:1949 pid:116003343\n"
            + "hcl:#c0946f hgt:178cm eyr:2028 iyr:2020 cid:220\n" + "ecl:hzl\n" + "\n" + "iyr:2013\n"
            + "cid:314 pid:186cm hgt:74cm eyr:1973 ecl:hzl byr:2007\n" + "hcl:180e0c\n" + "\n" + "pid:486330019\n"
            + "byr:1999 ecl:oth hgt:154cm iyr:2019 eyr:2026\n" + "hcl:#efcc98\n" + "\n"
            + "eyr:2030 iyr:2018 hcl:#18171d byr:1950\n" + "pid:648616604 hgt:160cm ecl:gry\n" + "\n" + "hgt:173cm\n"
            + "ecl:oth byr:1993 eyr:2029 hcl:#fffffd iyr:2010 pid:317451887\n" + "\n" + "ecl:brn hgt:157cm\n"
            + "byr:1963 eyr:2023 pid:005387570 hcl:#866857 iyr:2012\n" + "\n"
            + "pid:419695212 eyr:2020 byr:1957 cid:198 iyr:2015 hcl:#888785 hgt:168cm ecl:amb\n" + "\n" + "ecl:amb\n"
            + "iyr:2017 eyr:2024 pid:039995171 hcl:#a97842\n" + "hgt:153cm byr:1983\n" + "\n"
            + "byr:1979 eyr:2021 iyr:2011 hgt:157cm ecl:blu pid:110855542 hcl:#c0946f\n" + "\n"
            + "ecl:blu pid:948753945 eyr:2029 iyr:2012 hcl:#ceb3a1\n" + "hgt:164cm byr:1988\n" + "\n" + "iyr:2010\n"
            + "eyr:2032 hcl:#fffffd pid:#175129 hgt:184cm\n" + "ecl:hzl byr:1985\n" + "\n"
            + "hgt:189cm ecl:blu byr:1936 eyr:2027 hcl:#733820\n" + "pid:728752361 iyr:2011\n" + "\n"
            + "hcl:#733820 ecl:blu eyr:2023 hgt:172cm iyr:2017\n" + "pid:013415387 byr:1947\n" + "\n"
            + "byr:2012 iyr:2017 pid:#424ae4\n" + "cid:172 hgt:166cm eyr:2022\n" + "hcl:b1319b ecl:#6635d8\n" + "\n"
            + "eyr:2030\n" + "iyr:1928 hgt:185cm ecl:brn pid:#ac5a90 byr:1984 hcl:ac8f43\n" + "\n" + "eyr:2027\n"
            + "ecl:amb iyr:2014 hcl:#fffffd\n" + "pid:838758900\n" + "hgt:177cm byr:1942\n" + "\n"
            + "cid:166 iyr:2020 ecl:lzr hgt:70cm eyr:2040 byr:2004 hcl:#733820\n" + "\n"
            + "eyr:2028 ecl:grn byr:2016 cid:61 iyr:2010\n" + "hcl:#cfa07d\n" + "hgt:155in\n" + "pid:9594283803\n" + "\n"
            + "ecl:gmt pid:984675198\n" + "byr:1997 hgt:128 eyr:2037 hcl:#b6652a cid:299\n" + "\n"
            + "iyr:2015 pid:733864914 eyr:2021 ecl:amb\n" + "byr:1971 cid:280\n" + "hgt:181cm hcl:#054593\n" + "\n"
            + "ecl:hzl hcl:#cfa07d eyr:2022 pid:832736421\n" + "byr:1958\n" + "iyr:2010\n" + "cid:274 hgt:152cm\n" + "\n"
            + "eyr:2020 hcl:#6b5442 cid:223 hgt:155cm byr:1989 ecl:oth\n" + "iyr:2011 pid:549182194\n" + "\n"
            + "iyr:2020 hcl:#cfa07d\n" + "eyr:2027 pid:093361240 byr:1941 cid:271 hgt:178cm ecl:brn\n" + "\n"
            + "ecl:blu cid:290 eyr:2027\n" + "hgt:192cm byr:1945 hcl:#7d3b0c iyr:2020 pid:910713369\n" + "\n"
            + "byr:1991 hcl:#ceb3a1 ecl:xry hgt:159cm pid:9496171384\n" + "eyr:2030 iyr:2016\n" + "\n"
            + "eyr:2020 pid:812617809 hcl:#7d3b0c\n" + "byr:1970 ecl:gmt\n" + "iyr:1971 hgt:157in\n" + "\n"
            + "pid:596027311 hcl:#866857 hgt:169cm byr:1945 eyr:2030 ecl:oth\n" + "iyr:2010\n" + "\n" + "hgt:176cm\n"
            + "pid:213213359 byr:2012 hcl:be7b13 eyr:1971 ecl:gmt iyr:2011\n" + "cid:64\n" + "\n"
            + "pid:27107946 ecl:utc hgt:66cm byr:1928 eyr:2040\n" + "cid:87\n" + "\n"
            + "byr:1959 ecl:blu hcl:4e023b pid:9017609497 eyr:2023 hgt:68 iyr:2029\n" + "\n"
            + "hgt:164cm eyr:2023 byr:2008 ecl:grn pid:420168481 hcl:#b6652a iyr:2012\n" + "\n" + "eyr:1977 byr:1934\n"
            + "ecl:brn cid:163\n" + "iyr:2018 pid:2863284754\n" + "hgt:150in hcl:#623a2f\n" + "\n"
            + "ecl:hzl eyr:2031 cid:145 hgt:186cm hcl:#cfa07d\n" + "byr:1941 iyr:2010 pid:722056139\n" + "\n"
            + "ecl:blu eyr:2027\n" + "hcl:#888785 iyr:2018 byr:1977 cid:278 hgt:156cm\n" + "\n" + "eyr:2039 hgt:82 byr:2007\n"
            + "hcl:z iyr:2021 ecl:dne cid:191\n" + "pid:#1cf69f\n" + "\n" + "pid:183cm cid:111\n" + "hgt:66cm\n" + "iyr:1950\n"
            + "eyr:1947 ecl:#016f6a\n" + "\n"
            + "ecl:hzl byr:1957 iyr:2015 hgt:186cm eyr:2029 hcl:#701e04 cid:149 pid:827898914\n" + "\n"
            + "cid:214 pid:785688542 hgt:189cm byr:1974 ecl:brn\n" + "hcl:#18171d\n" + "eyr:2030\n" + "\n" + "hcl:#866857\n"
            + "cid:241 ecl:grn pid:389488422 byr:1959 iyr:2015 hgt:67in\n" + "eyr:2027\n" + "\n"
            + "hcl:#6b5442 iyr:2011 hgt:193cm\n" + "eyr:2026 byr:1952\n" + "pid:033382338\n" + "ecl:grn\n" + "\n"
            + "iyr:2020 hgt:166cm byr:1927\n" + "eyr:2029 ecl:hzl\n" + "pid:927006613 hcl:#623a2f\n" + "\n"
            + "ecl:gry pid:640783974\n" + "hgt:71in byr:1945 iyr:2019 cid:268 hcl:#b6652a\n" + "eyr:2025\n" + "\n"
            + "hcl:#733820 hgt:163cm\n" + "pid:1285584293 byr:1967 ecl:oth\n" + "cid:309 iyr:2020 eyr:2031\n" + "\n"
            + "pid:910349085 iyr:2011 hcl:#623a2f byr:1956\n" + "eyr:2025 ecl:gry\n" + "hgt:182cm\n" + "\n"
            + "pid:018283044 hcl:#602927 hgt:153cm ecl:gry iyr:2020\n" + "eyr:2024\n" + "byr:1990\n" + "\n"
            + "hgt:184cm hcl:#866857 ecl:oth\n" + "eyr:2023 pid:405733635 cid:205\n" + "byr:1987 iyr:2012\n" + "\n"
            + "hgt:167cm\n" + "iyr:2015 ecl:brn\n" + "eyr:2025\n" + "hcl:#18171d cid:313 byr:1960\n" + "\n"
            + "hgt:165cm byr:1933\n" + "iyr:2014\n" + "cid:203\n" + "hcl:#1cdbb3\n" + "ecl:hzl eyr:2027 pid:747009469\n" + "\n"
            + "hgt:169cm ecl:gry iyr:2014\n" + "byr:1966 pid:621876532 hcl:#efcc98\n" + "\n"
            + "cid:342 eyr:2029 hcl:#a97842 byr:1970\n" + "ecl:oth\n" + "pid:137287449 hgt:180cm\n" + "iyr:2011\n" + "\n"
            + "hcl:#cfa07d byr:1985 hgt:183cm ecl:grn\n" + "iyr:2013 eyr:2022\n" + "\n" + "iyr:2023\n"
            + "pid:164cm hcl:z byr:1966\n" + "eyr:2021 ecl:utc\n" + "\n" + "hcl:#fffffd cid:60\n" + "byr:1973\n"
            + "pid:324648387\n" + "hgt:177cm eyr:2022 iyr:2010\n" + "ecl:oth\n" + "\n" + "pid:632056596 hcl:#efcc98\n"
            + "hgt:73in ecl:brn byr:1928 iyr:2017\n" + "eyr:2023\n" + "\n" + "cid:144 ecl:amb eyr:2035 byr:1943 hgt:180cm\n"
            + "iyr:2012\n" + "pid:155cm\n" + "\n" + "hcl:#6b5442\n" + "pid:927492391\n"
            + "eyr:2023 hgt:172cm byr:1958 cid:92 ecl:gry iyr:2019\n" + "\n" + "iyr:2020 cid:82\n" + "hgt:193in hcl:#b6652a\n"
            + "ecl:grn eyr:2034 byr:2026\n" + "\n" + "iyr:1922 hcl:245cb3 byr:2015\n" + "pid:151cm\n" + "eyr:2040\n"
            + "ecl:lzr cid:136 hgt:101\n" + "\n" + "byr:2025\n" + "eyr:2029\n" + "hgt:193in\n" + "cid:308\n"
            + "ecl:gry iyr:2028 pid:9335153289\n" + "hcl:z\n" + "\n" + "eyr:2030 hgt:163cm iyr:2014\n"
            + "pid:147768826 ecl:blu byr:1922 hcl:#ceb3a1 cid:169\n" + "\n"
            + "ecl:blu byr:2002 eyr:2028 pid:998185490 cid:165 iyr:2020\n" + "hgt:188cm hcl:#c0946f";
    private static final Map<String, Number> fieldMap  = new HashMap<>();
    static {
        fieldMap.put("BYR".toLowerCase(), BYR);
        fieldMap.put("IYR".toLowerCase(), IYR);
        fieldMap.put("EYR".toLowerCase(), EYR);
        fieldMap.put("HGT".toLowerCase(), HGT);
        fieldMap.put("HCL".toLowerCase(), HCL);
        fieldMap.put("ECL".toLowerCase(), ECL);
        fieldMap.put("PID".toLowerCase(), PID);
        fieldMap.put("CID".toLowerCase(), CID);
    }
    private static final String EXAMPLE = "ecl:gry pid:860033327 eyr:2020 hcl:#fffffd\n"
            + "byr:1937 iyr:2017 cid:147 hgt:183cm\n" + "\n" + "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884\n"
            + "hcl:#cfa07d byr:1929\n" + "\n" + "hcl:#ae17e1 iyr:2013\n" + "eyr:2024\n" + "ecl:brn pid:760753108 byr:1931\n"
            + "hgt:179cm\n" + "\n" + "hcl:#cfa07d eyr:2025 pid:166559648\n" + "iyr:2011 ecl:brn hgt:59in";
    private static final String INVALID = "eyr:1972 cid:100\n" + "hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926\n"
            + "\n" + "iyr:2019\n" + "hcl:#602927 eyr:1967 hgt:170cm\n" + "ecl:grn pid:012533040 byr:1946\n" + "\n"
            + "hcl:dab227 iyr:2012\n" + "ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277\n" + "\n"
            + "hgt:59cm ecl:zzz\n" + "eyr:2038 hcl:74454a iyr:2023\n" + "pid:3556412378 byr:2007";
    private static final String VALID   = "pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980\n" + "hcl:#623a2f\n" + "\n"
            + "eyr:2029 ecl:blu cid:129 byr:1989\n" + "iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm\n" + "\n" + "hcl:#888785\n"
            + "hgt:164cm byr:2001 iyr:2015 cid:88\n" + "pid:545766238 ecl:hzl\n" + "eyr:2022\n" + "\n"
            + "iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719";
    public static void main(String[] args) {
        System.out.println(countValid(PASSPORTS));
        System.out.println();
        System.out.println(countValid(EXAMPLE));
        System.out.println();
        System.out.println(countValid(VALID));
        System.out.println();
        System.out.println(countValid(INVALID));
    }

    private static int countValid(String passportsString) {
        var passports = passportsString.split("\n\n");
        var foundPassports = 0;
        for (String passportString : passports) {
            var fields = passportString.split("[ \n\t]");
            System.out.println("pstr: " + passportString);
            var foundFields = 0;
            for (String field : fields) {
                foundFields = verifyField(field, foundFields);
            }
            System.out.println("found fields: " + Integer.toBinaryString(foundFields));
            // don't require CID
            foundFields &= ~CID;
            if (foundFields == 127) {
                ++foundPassports;
                System.out.println("valid passports: " + foundPassports);
            }
        }
        System.out.println("valid passports: " + foundPassports);
        return foundPassports;
    }

    private static int verifyField(String fieldString, int foundFields) {
        var field      = fieldString.split(":");
        var fieldName  = field[0];
        var fieldValue = field[1];
        var fieldInt   = fieldMap.get(fieldName).intValue();
        System.out.print("fstr: " + fieldString + " fNam: " + fieldName + " fVal: " + fieldValue + " fInt: " + fieldInt);
        if ((foundFields & fieldInt) != 0 || !checkField(fieldInt, fieldValue)) {
            System.out.println(" invalid");
            return foundFields;
        } else {
            System.out.println(" valid");
            return foundFields | fieldInt;
        }

    }

    private static boolean checkField(int fieldInt, String fieldValue) {
        switch (fieldInt) {
            case BYR:
                return checkInt(fieldValue, 1920, 2002);

            case IYR:
                return checkInt(fieldValue, 2010, 2020);

            case EYR:
                return checkInt(fieldValue, 2020, 2030);

            case HGT:
                final var measurement = fieldValue.substring(0, fieldValue.length() - 2);
                if (fieldValue.endsWith("in")) {
                    return checkInt(measurement, 59, 76);
                } else if (fieldValue.endsWith("cm")) {
                    return checkInt(measurement, 150, 193);
                } else {
                    return false;
                }
            case HCL:
                return fieldValue.matches("#[0-9a-f]{6}");

            case ECL:
                return fieldValue.matches("amb|blu|brn|gry|grn|hzl|oth");

            case PID:
                return fieldValue.matches("[0-9]{9}");

            case CID:
                return fieldValue.matches("[0-9]{3}");

            default :
                return false;
        }
    }

    private static boolean checkInt(String measurement, int min, int max) {
        final int units;
        return measurement.matches("[0-9]+") && (units = Integer.parseInt(measurement)) >= min && units <= max;
    }

}
