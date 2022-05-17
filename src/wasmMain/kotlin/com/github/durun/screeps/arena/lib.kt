package com.github.durun.screeps.arena

interface RoomPosition {
    val x: Int
    val y: Int

    fun getRangeTo(target: RoomPosition): Int {
        val dx = target.x - this.x
        val dy = target.y - this.y
        return dx * dx + dy * dy
    }
}

data class Vec2(
    override val x: Int,
    override val y: Int
) : RoomPosition


