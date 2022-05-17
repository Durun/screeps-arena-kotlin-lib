package com.github.durun.screeps.arena

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
}