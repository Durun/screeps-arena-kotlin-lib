package com.github.durun.screeps.arena.api

import com.github.durun.screeps.arena.jsinterop.getExtensions_heap
import com.github.durun.screeps.arena.jsinterop.getHeapInt32

class Extension internal constructor(index: Int) : OwnedStructure(index) {
    companion object {
        fun getAll(): List<Extension> {
            val size = getExtensions_heap()
            return (0 until size).map { Extension(getHeapInt32(it)) }
        }
    }

    val store: Store = Store(this.index)
}