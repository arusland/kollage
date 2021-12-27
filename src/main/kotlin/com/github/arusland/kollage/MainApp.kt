package com.github.arusland.kollage

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object MainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        testMask2()
    }

    private fun testMask2() {
        val font = Font("damn_noisy_kids_rus", Font.PLAIN, 100)
        val image = createImageByText("СЩЁЦУЙ", font, Color.BLACK)

        ImageIO.write(image, "png", File("mask2.png"))
    }

    private fun squareImageTest() {
        val file = File("/home/ruslan/Downloads/file.jpg")
        val imageIn = ImageIO.read(file)
        val resized = squareImage(imageIn)

        ImageIO.write(resized, "png", File("squared.png"))
    }

    private fun resizeTest() {
        val file = File("/home/ruslan/Downloads/file.jpg")
        val imageIn = ImageIO.read(file)
        val width = imageIn.getWidth(null)
        val height = imageIn.getHeight(null)
        val resized = resizeImage(imageIn, width / 10, height / 10)

        ImageIO.write(resized, "png", File("resized.png"))
    }

    private fun getMask() {
        val width = 400
        val height = 300
        val out = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2d = out.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, width, height)
        val font = g2d.setFont("HondaC", 100, Font.PLAIN, Color.BLACK)
        val metrics = g2d.getFontMetrics(font)
        g2d.drawString("СЩЁЦУЙ", 1, metrics.height)
        g2d.color = Color.RED
        g2d.drawRect(1, 1, 50, metrics.height)

        val colorsMap = mutableMapOf<Int, Long>()
        val mask = mutableMapOf<String, Boolean>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = out.getRGB(x, y)
                colorsMap[color] = colorsMap.getOrDefault(color, 0) + 1
                if (color == Color.BLACK.rgb) {
                    mask["${x}.$y"] = true
                }
            }
        }

        colorsMap.forEach { color, count ->
            println("${int2RGB(color)}: $count")
        }
        println("mask size: ${mask.size}")

        ImageIO.write(out, "png", File("mask_result.png"))
    }

    private fun createImageByText(text: String, font: Font, color: Color): BufferedImage {
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


    private fun int2RGB(clr: Int): String {
        val red: Int = clr and 0x00ff0000 shr 16
        val green: Int = clr and 0x0000ff00 shr 8
        val blue: Int = clr and 0x000000ff

        return "$red,$green,$blue"
    }

    private fun textOnImage() {
        val file = File("/home/ruslan/Downloads/file.jpg")
        val imageIn = ImageIO.read(file)
        val width = imageIn.getWidth(null)
        val height = imageIn.getHeight(null)

        println("Image: ${width}x$height")

        val out = BufferedImage(width + 10, height + 10, BufferedImage.TYPE_INT_RGB)
        val g2d = out.createGraphics()

        g2d.drawImage(imageIn, 5, 5, null)

        GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
            .forEach { fontName ->
                println(fontName)
            }

        listOf(
            "Marvin",
            "Kashima RUS by Cop",
            "AA Magnum",
            "aZZ BB Tribute Cyr",
            "damn_noisy_kids_rus",
            "HondaC"
        ).forEachIndexed { index, fontName ->
            g2d.setFont(fontName, 100, Font.PLAIN, Color.BLUE)
            g2d.drawString("Привет", 30, (index + 1) * 100)
        }
        g2d.dispose()

        ImageIO.write(out, "png", File("result.png"))
    }

    private fun squareImage(originalImage: BufferedImage): BufferedImage {
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

    private fun resizeImage(originalImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics2D.dispose()
        return resizedImage
    }

    private fun Graphics2D.setFont(fontName: String, size: Int, fontStyle: Int, color: Color): Font {
        val font = Font(fontName, fontStyle, size)
        this.color = color
        this.font = font

        return font
    }
}
