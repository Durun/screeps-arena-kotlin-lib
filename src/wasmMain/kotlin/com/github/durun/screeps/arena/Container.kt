package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getContainers_heap
import com.github.durun.screeps.arena.jsinterop.getHeapInt32

class Container internal constructor(index: Int) : OwnedStructure(index) {
    companion object {
        fun getAll(): List<Container> {
            val size = getContainers_heap()
            return (0 until size).map { Container(getHeapInt32(it)) }
        }
    }

    val store: Store = Store(this.index)
}