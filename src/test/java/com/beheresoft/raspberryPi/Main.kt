package com.beheresoft.raspberryPi

import com.beheresoft.raspberryPi.util.FontModel
import java.nio.charset.Charset

fun main() {
    val cm = FontModel(16, 16)
    cm.preview('你',"d:/")
return;
    val gb2312 = Charset.forName("GB2312")

    for (bh in 0xA1..0xF7) {
        for (bl in 0xA0..0xFE) {
            val s = String(byteArrayOf(bh.toByte(), bl.toByte()), gb2312)
            //cm.flush(s)
        }
    }
    cm.finish()
}