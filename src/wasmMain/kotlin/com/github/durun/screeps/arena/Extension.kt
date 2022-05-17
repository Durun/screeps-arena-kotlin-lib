package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getHeapInt32

class Extension internal constructor(index: Int) : OwnedStructure(index) {
    companion object {
        fun getAll(): List<Extension> {
            val size = TODO()
            return (0 until size).map { Extension(getHeapInt32(it)) }
        }
    }

    val store: Store = Store(this.index)
}