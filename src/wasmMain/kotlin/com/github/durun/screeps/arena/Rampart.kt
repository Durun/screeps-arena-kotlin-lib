package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getHeapInt32

class Rampart internal constructor(index: Int) : OwnedStructure(index) {
    companion object {
        fun getAll(): List<Rampart> {
            val size = TODO()
            return (0 until size).map { Rampart(getHeapInt32(it)) }
        }
    }
}