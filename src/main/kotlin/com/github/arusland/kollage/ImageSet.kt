package com.github.arusland.kollage

import org.slf4j.LoggerFactory
import java.awt.Image
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class ImageSet(private val images: Map<Int, List<Image>>) {
    private val random = Random(100)
    private val alreadyGotIndexes = mutableMapOf<Int, MutableSet<Int>>()

    fun nextImage(size: Int): Image {
        val list = images[size] ?: throw IllegalStateException("Unknown size: $size")
        val nextIndex = nextImageIndex(size, list.size)

        return list[nextIndex]
    }

    /**
     * Returns random index but not repeat previous indexes
     */
    private fun nextImageIndex(size: Int, listSize: Int): Int {
        val set = alreadyGotIndexes.getOrPut(size) { mutableSetOf() }

        if (set.size >= listSize) {
            // all indexes are got, reset set
            log.debug("Reset image index set for size: {}", size)
            set.clear()
        }

        while (true) {
            val nextIndex = Math.abs(random.nextInt()) % listSize

            if (!set.contains(nextIndex)) {
                set.add(nextIndex)
                return nextIndex
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ImageSet::class.java)!!
        private val dirCache: File = File("/tmp/kollage-cache")

        private fun File.isImage(): Boolean = isFile && (name.endsWith(".jpg") || name.endsWith(".jpeg"))

        fun fromDir(dir: File, sizes: List<Int>): ImageSet {
            if (!dirCache.exists()) {
                dirCache.mkdirs()
            }

            val images = mutableMapOf<Int, MutableList<Image>>()

            sizes.forEach { size -> images[size] = mutableListOf() }

            dir.listFiles()
                .filter { it.isImage() }
                .map {
                    val imgs = fileToImage(it, sizes)
                    sizes.forEachIndexed { index, size ->
                        images[size]!!.add(imgs[index])
                    }
                }

            return ImageSet(images)
        }

        private fun fileToImage(file: File, sizes: List<Int>): List<Image> {
            val squared = lazy {
                val origImage = ImageIO.read(file)
                ImageUtil.squareImage(origImage)
            }

            return sizes.map { size ->
                val cacheFile = File(dirCache, file.nameWithoutExtension + ".$size.cache.png")

                if (cacheFile.exists()) {
                    log.debug("Load from cache file $cacheFile")
                    ImageIO.read(cacheFile)
                } else {
                    val resized = ImageUtil.resizeImage(squared.value, size, size)
                    ImageIO.write(resized, "png", cacheFile)

                    log.debug("Saved cache image (${cacheFile.length()}) into $cacheFile")

                    resized
                }
            }
        }
    }
}
