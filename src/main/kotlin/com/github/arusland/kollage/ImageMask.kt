package com.github.arusland.kollage

import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.image.BufferedImage

class ImageMask(
    private val maskMap: Map<String, Boolean>,
    val width: Int,
    val height: Int
) {
    fun isSet(x: Int, y: Int): Boolean {
        if (!(x in 0 until width && y in 0 until height)) {
            return false
        }

        return maskMap["${x}.$y"] ?: false
    }

    companion object {
        private val log = LoggerFactory.getLogger(ImageMask::class.java)!!

        fun fromImage(image: BufferedImage, maskColor: Color = Color.BLACK): ImageMask {
            val rgbColor = maskColor.rgb
            val mask = mutableMapOf<String, Boolean>()
            val width = image.getWidth(null)
            val height = image.getHeight(null)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = image.getRGB(x, y)
                    if (color == rgbColor) {
                        mask["${x}.$y"] = true
                    }
                }
            }

            log.debug("Create image mask ${width}x${height} with size: ${mask.size}")

            return ImageMask(mask, width, height)
        }
    }
}
