package com.github.durun.screeps.arena.api

import com.github.durun.screeps.arena.jsinterop.*

class Color private constructor(val int: Int) {
    companion object {
        fun of(int: Int): Color {
            if (int in 0x000000..0xFFFFFF) {
                return Color(int)
            } else {
                throw IllegalArgumentException("Color out off range: $int")
            }
        }
    }
}

enum class LineStyle(val int: Int) {
    Solid(0),
    Dashed(1),
    Dotted(2),
}

data class Style(
    val fill: Color? = null,
    val opacity: Float? = null,
    val stroke: Color? = null,
    val strokeWidth: Float? = null,
    val lineStyle: LineStyle = LineStyle.Solid
)

enum class Aling(val int: Int) {
    Center(0),
    Left(1),
    Right(2)
}

data class TextStyle(
    val color: Color? = null,
    val font: String? = null,
    val stroke: Color? = null,
    val strokeWidth: Float? = null,
    val bgColor: Color? = null,
    val padding: Float? = null,
    val aling: Aling = Aling.Center,
    val opacity: Float? = null
)

class Visual private constructor(private val index: Int) {
    companion object {
        fun new(layer: Int, persistent: Boolean): Visual {
            val index = newVisual(layer, persistent)
            return Visual(index)
        }
    }

    fun clear(): Visual {
        visualClear(this.index)
        return this
    }

    fun circle(pos: RoomPosition, radius: Float? = null, style: Style? = null): Visual {
        visualCircle(
            this.index,
            pos.x, pos.y,
            radius ?: -1f,
            style?.fill?.int ?: -1,
            style?.opacity ?: -1f,
            style?.stroke?.int ?: -1,
            style?.strokeWidth ?: -1f,
            style?.lineStyle?.int ?: 0
        )
        return this
    }

    fun line(pos1: RoomPosition, pos2: RoomPosition, style: Style? = null): Visual {
        visualLine(
            this.index,
            pos1.x, pos1.y,
            pos2.x, pos2.y,
            style?.strokeWidth ?: -1f,
            style?.stroke?.int ?: -1,
            style?.opacity ?: -1f,
            style?.lineStyle?.int ?: 0
        )
        return this
    }

    fun poly(points: List<RoomPosition>, style: Style? = null): Visual {
        points.toHeap()
        visualPoly(
            this.index,
            points.size,
            style?.fill?.int ?: -1,
            style?.opacity ?: -1f,
            style?.stroke?.int ?: -1,
            style?.strokeWidth ?: -1f,
            style?.lineStyle?.int ?: 0
        )
        return this
    }

    fun rect(pos: RoomPosition, w: Int, h: Int, style: Style? = null): Visual {
        visualRect(
            this.index,
            pos.x, pos.y, w, h,
            style?.fill?.int ?: -1,
            style?.opacity ?: -1f,
            style?.stroke?.int ?: -1,
            style?.strokeWidth ?: -1f,
            style?.lineStyle?.int ?: 0
        )
        return this
    }

    fun text(text: String, pos: RoomPosition, style: TextStyle? = null): Visual {
        val textLength = text.toHeapUTF16(0)
        val fontLength = style?.font?.toHeapUTF16(textLength)
        visualText(
            this.index,
            pos.x, pos.y,
            textLength,
            style?.color?.int ?: -1,
            fontLength ?: -1,
            style?.stroke?.int ?: -1,
            style?.strokeWidth ?: -1f,
            style?.bgColor?.int ?: -1,
            style?.padding ?: -1f,
            style?.aling?.int ?: 0,
            style?.opacity ?: -1f
        )
        return this
    }
}