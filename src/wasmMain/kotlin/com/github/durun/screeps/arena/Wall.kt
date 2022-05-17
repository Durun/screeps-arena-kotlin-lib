package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getHeapInt32

class Wall internal constructor(index: Int) : Structure(index) {
    companion object {
        fun getAll(): List<Wall> {
            val size = TODO()
            return (0 until size).map { Wall(getHeapInt32(it)) }
        }
    }
}