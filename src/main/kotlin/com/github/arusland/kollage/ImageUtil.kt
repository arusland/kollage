package com.github.arusland.kollage

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage

object ImageUtil {
    fun createImageByText(text: String, font: Font, color: Color): BufferedImage {
        val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        val draw = tmp.createGraphics() as Graphics2D
        draw.font = font
        val metrics = draw.fontMetrics
        val bounds = metrics.getStringBounds(text, draw)
        val image = BufferedImage(
            bounds.width.toInt(), (bounds.height + metrics.descent).toInt(),
            BufferedImage.TYPE_INT_RGB
        )
        val draw2 = image.createGraphics() as Graphics2D
        draw2.fillRect(0, 0, image.width, image.height)
        draw2.font = font
        draw2.color = color
        draw2.drawString(text, 0, bounds.height.toInt())
        return image
    }

    fun squareImage(originalImage: BufferedImage): BufferedImage {
        val width = originalImage.getWidth(null)
        val height = originalImage.getHeight(null)

        if (width == height) {
            return originalImage
        }

        return if (width > height) {
            val diff: Int = Math.max((width - height) / 2, 1)
            originalImage.getSubimage(diff, 0, height, height)
        } else {
            val diff: Int = Math.max((height - width) / 2, 1)
            originalImage.getSubimage(0, diff, width, width)
        }
    }

    fun resizeImage(originalImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics2D.dispose()
        return resizedImage
    }
}