package com.github.arusland.kollage

import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage

class CollageImage(
    val image: BufferedImage,
    val scale: Int
) {
    val width: Int = image.width / scale
    val height: Int = image.height / scale
    private val gfx: Graphics2D = image.createGraphics()

    fun drawImage(x: Int, y: Int, image: Image) {
        gfx.drawImage(image, toVirtX(x), toVirtY(y), null)
    }

    private fun toVirtX(x: Int) = x * scale

    private fun toVirtY(y: Int) = y * scale
}
