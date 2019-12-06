package com.beheresoft.raspberryPi

import com.beheresoft.raspberryPi.util.FontModel

fun main() {
    val cm = FontModel(16, 16)
    val c = '十'
    cm.setSaveDisk("d:/")
            .setScanModel(FontModel.SCAN_MODEL.LEFT_TO_RIGHT)
            .preview(c)
    val ch = cm.conventOneChar(c)
    cm.display(ch, 16)
    cm.binaryChineseFont()
}