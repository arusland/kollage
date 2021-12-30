package com.github.arusland.kollage

data class Coord(val x: Int, val y: Int) {
    fun left() = Coord(x - 1, y)

    fun right() = Coord(x + 1, y)

    fun top() = Coord(x, y - 1)

    fun bottom() = Coord(x, y + 1)
}

data class CoordRect(val coord: Coord, val size: Int) {
    fun isEmpty() = this === Empty

    companion object {
        val Empty = CoordRect(Coord(0, 0), 0)
    }
}
