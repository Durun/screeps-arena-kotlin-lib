package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getHeapInt32
import com.github.durun.screeps.arena.jsinterop.getRamparts_heap

class Rampart internal constructor(index: Int) : OwnedStructure(index) {
    companion object {
        fun getAll(): List<Rampart> {
            val size = getRamparts_heap()
            return (0 until size).map { Rampart(getHeapInt32(it)) }
        }
    }
}