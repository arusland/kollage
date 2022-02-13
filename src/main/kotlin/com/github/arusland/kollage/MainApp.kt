package com.github.arusland.kollage

import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random


object MainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        testKollage()
    }

    private fun testKollage() {
        val scale = 50

        listOf(
            "damn_noisy_kids_rus",
            "Marvin",
            "Kashima RUS by Cop",
            "AA Magnum",
            "aZZ BB Tribute Cyr",
            "HondaC"
        ).subList(0, 1)
            .forEach { fontName ->
                renderCollage("ВЫХОДИ\nЗА МЕНЯ", scale, fontName)
            }
    }

    private fun renderCollage(text: String, scale: Int, fontName: String) {
        val font = Font(fontName, Font.PLAIN, 100)
        val maskImage = ImageUtil.createImageByText(text, font, Color.BLACK)
        val imageMask = ImageMask.fromImage(maskImage).findInners(listOf(2, 4, 6))
        val out = BufferedImage(maskImage.width * scale, maskImage.height * scale, BufferedImage.TYPE_INT_RGB)
        val g2d = out.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, out.width, out.height)
        renderBackground(g2d, out.width, out.height)

        val collage = CollageImage(out, scale)
        val sizes = imageMask.calcSizes.map { it * scale }
        val imageSet = ImageSet.fromDir(File("/home/ruslan/Downloads/Telega"), sizes)
        val border = BufferedImage(scale, scale, BufferedImage.TYPE_INT_RGB)
        val alreadyRendered = mutableSetOf<Coord>()

        for (x in 0 until imageMask.width) {
            for (y in 0 until imageMask.height) {
                val coord = Coord(x, y)

                if (imageMask.isBorder(coord)) {
                    collage.drawImage(x, y, border)
                } else {
                    val rect = imageMask.getRect(coord)

                    if (rect != null) {
                        val leftPoint = if (rect.isEmpty()) coord else rect.coord

                        if (!alreadyRendered.contains(leftPoint)) {
                            alreadyRendered.add(leftPoint)

                            if (!rect.isEmpty()) {
                                collage.drawImage(
                                    leftPoint.x,
                                    leftPoint.y,
                                    imageSet.nextImage(if (rect.isEmpty()) scale else rect.size * scale)
                                )
                            }
                        }
                    }
                }
            }
        }

        //ImageIO.write(out, "png", File("kollage_$fontName.png"))

        val resized = ImageUtil.resizeImage(out, out.width / 2, out.height / 2)
        ImageIO.write(resized, "png", File("kollage_${fontName}_small.png"))
    }

    private fun renderBackground(g2d: Graphics2D, width: Int, height: Int) {
        g2d.stroke = BasicStroke(5f)
        val colors = listOf(Color.GRAY, Color.BLUE, Color.GREEN)

        val random = Random(1444)

        for (i in 1..100) {
            g2d.color = colors.get(Math.abs(random.nextInt() % colors.size))

            g2d.drawLine(
                Math.abs(random.nextInt() % width),
                Math.abs(random.nextInt() % height),
                Math.abs(random.nextInt() % width),
                Math.abs(random.nextInt() % height)
            )
        }
    }

    private fun testCreateImageByText() {
        val font = Font("damn_noisy_kids_rus", Font.PLAIN, 100)
        val image = ImageUtil.createImageByText("СЩЁЦУЙ", font, Color.BLACK)

        ImageIO.write(image, "png", File("mask2.png"))
    }

    private fun squareImageTest() {
        val file = File("/home/ruslan/Downloads/file.jpg")
        val imageIn = ImageIO.read(file)
        val resized = ImageUtil.squareImage(imageIn)

        ImageIO.write(resized, "png", File("squared.png"))
    }

    private fun resizeTest() {
        val file = File("/home/ruslan/Downloads/file.jpg")
        val imageIn = ImageIO.read(file)
        val width = imageIn.getWidth(null)
        val height = imageIn.getHeight(null)
        val resized = ImageUtil.resizeImage(imageIn, width / 10, height / 10)

        ImageIO.write(resized, "png", File("resized.png"))
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

    private fun Graphics2D.setFont(fontName: String, size: Int, fontStyle: Int, color: Color): Font {
        val font = Font(fontName, fontStyle, size)
        this.color = color
        this.font = font

        return font
    }
}
