package com.github.arusland.kollage

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage

object ImageUtil {
    fun createImageByText(text: String, font: Font, color: Color): BufferedImage {
        val koef = 0.8
        val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        val draw = tmp.createGraphics() as Graphics2D
        draw.font = font
        val metrics = draw.fontMetrics
        val lineHeight = metrics.height
        val heightDelta = (lineHeight * (1 - koef)).toInt()
        val bounds = Rectangle()

        text.split('\n').forEach { line ->
            val rect = metrics.getStringBounds(line, draw)
            bounds.width = Math.max(bounds.width, rect.width.toInt())
            bounds.height = bounds.height + rect.height.toInt() - heightDelta
        }

        val image = BufferedImage(
            bounds.width, (bounds.height + metrics.descent),
            BufferedImage.TYPE_INT_RGB
        )
        val draw2 = image.createGraphics() as Graphics2D
        draw2.fillRect(0, 0, image.width, image.height)
        draw2.font = font
        draw2.color = color
        var y = lineHeight - heightDelta
        text.split('\n').forEach { line: String ->
            draw2.drawString(line, 0, y)
            y += (lineHeight * koef).toInt()
        }
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