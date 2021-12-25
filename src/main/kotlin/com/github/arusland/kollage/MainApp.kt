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
            "Kashima RUS by Cop",
            "Marvin",
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

    private fun Graphics2D.setFont(fontName: String, size: Int, fontStyle: Int, color: Color) {
        val font = Font(fontName, fontStyle, size)
        this.color = color
        this.font = font
    }
}
