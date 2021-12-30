package com.github.arusland.kollage

import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.image.BufferedImage

class ImageMask(
    private val maskMap: Map<Coord, CoordRect>,
    val width: Int,
    val height: Int,
    private val borders: Set<Coord> = emptySet()
) {
    fun isSet(x: Int, y: Int) = isSet(Coord(x, y))

    fun isSet(coord: Coord): Boolean {
        if (!(coord.x in 0 until width && coord.y in 0 until height)) {
            return false
        }

        return maskMap[coord] != null
    }

    fun isBorder(x: Int, y: Int): Boolean = isBorder(Coord(x, y))

    fun isBorder(coord: Coord): Boolean = borders.contains(coord)

    /**
     * Creates new ImageMask with calculated borders
     */
    fun calcBorders(): ImageMask {
        val borders = mutableSetOf<Coord>()

        maskMap.keys.forEach { coord ->
            if (!isSet(coord.left()) || !isSet(coord.top()) || !isSet(coord.right()) || !isSet(coord.bottom())) {
                borders.add(coord)
            }
        }

        return ImageMask(maskMap, width, height, borders)
    }

    /**
     *  Creates new ImageMask with calculated inner squares
     */
    fun findInners(sizes: List<Int>): ImageMask {
        val mask = if (borders.isNotEmpty()) this else calcBorders()
        var sizeIndex = -1
        val newMaskMap = maskMap.toMutableMap()

        maskMap.keys.forEach { coord ->
            val nextSize = sizes[++sizeIndex % sizes.size]
            val nextRect = CoordRect(coord, nextSize)

            if (tryPlaceRect(nextRect, newMaskMap)) {
                TODO()
            }
        }

        return ImageMask(newMaskMap, width, height, borders)
    }

    fun tryPlaceRect(rect: CoordRect, maskMap: MutableMap<Coord, CoordRect>): Boolean {
        val right = rect.coord.x + rect.size - 1
        val bottom = rect.coord.y + rect.size - 1
        val coords = mutableListOf<Coord>()

        for (x in rect.coord.x..right) {
            for (y in rect.coord.y..bottom) {
                val coord = Coord(x, y)

                if (isBorder(coord)) {
                    return false
                }

                val localRect = maskMap[coord] ?: return false

                if (localRect.isEmpty()) {
                    coords.add(coord)
                    continue
                }

                return false
            }
        }

        coords.forEach { coord ->
            maskMap[coord] = rect
        }

        return true
    }


    companion object {
        private val log = LoggerFactory.getLogger(ImageMask::class.java)!!

        fun fromImage(image: BufferedImage, maskColor: Color = Color.BLACK): ImageMask {
            val rgbColor = maskColor.rgb
            val mask = mutableMapOf<Coord, CoordRect>()
            val width = image.getWidth(null)
            val height = image.getHeight(null)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = image.getRGB(x, y)
                    if (color == rgbColor) {
                        mask[Coord(x, y)] = CoordRect.Empty
                    }
                }
            }

            log.debug("Create image mask ${width}x${height} with size: ${mask.size}")

            return ImageMask(mask, width, height)
        }
    }
}
