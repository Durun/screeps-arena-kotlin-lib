package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getHeapInt32
import com.github.durun.screeps.arena.jsinterop.getResources_heap
import com.github.durun.screeps.arena.jsinterop.resourceAmount
import com.github.durun.screeps.arena.jsinterop.resourceType

class Resource(index: Int) : GameObject(index) {
    companion object {
        fun getAll(): List<Resource> {
            val size = getResources_heap()
            return (0 until size).map { Resource(getHeapInt32(it)) }
        }
    }

    val amount: Int get() = resourceAmount(this.index)
    val resourceType: String by lazy {
        val size = resourceType(this.index)
        fromHeapUTF8(0, size)
    }
}