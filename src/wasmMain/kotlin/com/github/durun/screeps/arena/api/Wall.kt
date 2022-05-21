package com.github.durun.screeps.arena.api

import com.github.durun.screeps.arena.jsinterop.getHeapInt32
import com.github.durun.screeps.arena.jsinterop.getWalls_heap

class Wall internal constructor(index: Int) : Structure(index) {
    companion object {
        fun getAll(): List<Wall> {
            val size = getWalls_heap()
            return (0 until size).map { Wall(getHeapInt32(it)) }
        }
    }
}