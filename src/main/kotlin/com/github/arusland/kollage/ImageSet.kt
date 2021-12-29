package com.github.arusland.kollage

import org.slf4j.LoggerFactory
import java.awt.Image
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class ImageSet(private val images: List<Image>) {
    private val random = Random(42)

    fun nextImage(): Image = images[Math.abs(random.nextInt()) % images.size]

    companion object {
        private val log = LoggerFactory.getLogger(ImageSet::class.java)!!
        private val dirCache: File = File("/tmp/kollage-cache")

        private fun File.isImage(): Boolean = isFile && (name.endsWith(".jpg") || name.endsWith(".jpeg"))

        fun fromDir(dir: File, size: Int): ImageSet {
            if (!dirCache.exists()) {
                dirCache.mkdirs()
            }

            return ImageSet(dir.listFiles()
                .filter { it.isImage() }
                .map { fileToImage(it, size) })
        }

        private fun fileToImage(file: File, size: Int): Image {
            val cacheFile = File(dirCache, file.nameWithoutExtension + ".$size.cache.png")

            if (cacheFile.exists()) {
                log.debug("Load from cache file $cacheFile")
                return ImageIO.read(cacheFile)
            }

            val origImage = ImageIO.read(file)
            val squared = ImageUtil.squareImage(origImage)
            val resized = ImageUtil.resizeImage(squared, size, size)
            ImageIO.write(resized, "png", cacheFile)

            log.debug("Saved cache image (${cacheFile.length()}) into $cacheFile")

            return resized
        }
    }
}
