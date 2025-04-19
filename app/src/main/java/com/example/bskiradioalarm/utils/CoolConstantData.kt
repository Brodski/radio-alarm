package com.example.bskiradioalarm.utils

import com.example.bskiradioalarm.models.Station

class CoolConstantData {


    companion object {

        var num = 0

        fun doIncrement(numBase: Int? = null): Long {
            if (numBase != null) {
                num = numBase
            }
            num += 10
            return num.toLong()
        }
        // http://www.classical.net/music/links/radio.php
        // Deep research
        // https://chatgpt.com/c/67c3fac8-1c80-8001-8708-707dbfa95d29
        //
        // https://www.wnyc.org/streams/

        public val stationLoadMoreList: List<Station> = listOf(
            Station("NPR News",
                "https://wamu.cdnstream1.com/wamu.mp3",
                doIncrement(1000)
            ),
            Station("KCCU Jazz (Lawton, OK)",
                "https://26233.live.streamtheworld.com/KCCUFM.mp3?uuid=zx6i8so1d",
                doIncrement()
            ),
            Station("KCBX Jazz (San Luis Obispo, CA) ",
                "https://kcbx-ice.streamguys1.com/kcbx-hi.mp3",
                doIncrement()
            ),
            Station("WQXR News (WNYC, New York)",
                "https://fm939.wnyc.org/wnycfm",
                doIncrement()
            ),
            Station("All Classical Radio (Portland,OR)",
                "https://allclassical.streamguys1.com/ac128kmp3",
                doIncrement()
            ),
            Station("Classical 89 (BYU radio)",
                "https://radio.byub.org/classical89/classical89_aac",
                doIncrement()
            ),
            Station("KBAQ Classical - (Phoenix, AZ)",
                "https://kbaq.streamguys1.com/kbaq_mp3_128",
                doIncrement()
            ),
            Station("WNYC New Standards. Swing(?)",
                "https://specialstream.wnyc.org/wnyc-special",
                doIncrement()
            ),
            Station("WNYC Holiday Channel",
                "https://stream.wqxr.org/qxr-special",
                doIncrement()
            ),
            Station("WNYC New Sound",
                "https://q2stream.wqxr.org/q2-web",
                doIncrement()
            ),
            Station("WNYC Operavore",
                "https://opera-stream.wqxr.org/operavore-web",
                doIncrement()
            ),
            Station("Swiss Classical Radio ",
                "https://stream.srg-ssr.ch/m/rsc_de/aacp_96",
                doIncrement()
            ),

//            Station("KCME Classical (Colorado Springs, CO)", "https://ice9.securenetsystems.net/KCME", doIncrement()),
//            Station("KNPR Classical (Las Vegas, Nevada)", "https://16603.live.streamtheworld.com/KCNVFMAAC.aac?uuid=zxdai816e", doIncrement()            ),
//            Station("KENW Classical (New Mexico)", "https://ice10.securenetsystems.net/KENWFM", doIncrement()),
//            Station("WNYC Classical (New York)", "https://stream.wqxr.org/wqxr", doIncrement()),
            )

        public val stationPreloadedList: List<Station> = listOf(
            Station("CPR Classical Music",
                "https://stream1.cprnetwork.org/cpr2_lo",
                RadioService.DEFAULT_RADIO_REF_ID,
                doIncrement(0)
            ),
            Station("KUVO Jazz",
                "https://kuvo.streamguys1.com/kuvo-mp3-128",
                doIncrement()
            ),
            Station("CPR News",
                "https://stream1.cprnetwork.org/cpr1_lo",
                doIncrement()
            )
        )



    }
}