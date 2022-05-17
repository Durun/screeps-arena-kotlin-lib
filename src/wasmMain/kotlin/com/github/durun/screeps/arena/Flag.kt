package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getFlags_heap
import com.github.durun.screeps.arena.jsinterop.getHeapInt32

class Flag private constructor(index: Int) : GameObject(index) {
    companion object {
        fun getAll(): List<Flag> {
            val size = getFlags_heap()
            return (0 until size).map { Flag(getHeapInt32(it)) }
        }
    }
}