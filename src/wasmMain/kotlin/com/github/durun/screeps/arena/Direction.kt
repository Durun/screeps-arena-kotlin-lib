package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getDirection

val TOP = Direction.Top
val TOP_RIGHT = Direction.TopRight
val RIGHT = Direction.Right
val BOTTOM_RIGHT = Direction.BottomRight
val BOTTOM = Direction.Bottom
val BOTTOM_LEFT = Direction.BottomLeft
val LEFT = Direction.Left
val TOP_LEFT = Direction.TopLeft

enum class Direction(val int: Int) {
    Top(1),
    TopRight(2),
    Right(3),
    BottomRight(4),
    Bottom(5),
    BottomLeft(6),
    Left(7),
    TopLeft(8),
    ;

    companion object {
        fun of(dx: Int, dy: Int): Direction = when (getDirection(dx, dy)) {
            1 -> Top
            2 -> TopRight
            3 -> Right
            4 -> BottomRight
            5 -> Bottom
            6 -> BottomLeft
            7 -> Left
            8 -> TopLeft
            else -> throw IllegalStateException("getDirection($dx, $dy) returns ${getDirection(dx, dy)}")
        }
    }

    operator fun unaryMinus(): Direction = when (this) {
        Top -> Bottom
        TopRight -> BottomLeft
        Right -> Left
        BottomRight -> TopLeft
        Bottom -> Top
        BottomLeft -> TopRight
        Left -> Right
        TopLeft -> BottomRight
    }
}