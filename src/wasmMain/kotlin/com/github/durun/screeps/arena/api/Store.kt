package com.github.durun.screeps.arena.api

import com.github.durun.screeps.arena.jsinterop.getStore
import com.github.durun.screeps.arena.jsinterop.storeGetCapacity
import com.github.durun.screeps.arena.jsinterop.storeGetFreeCapacity
import com.github.durun.screeps.arena.jsinterop.storeGetUsedCapacity


class Store internal constructor(private val objIndex: Int) {
    val energy: Int get() = getStore(this.objIndex, -1)
    operator fun get(resourceType: String): Int {
        val len = resourceType.toHeapUTF16(0)
        return getStore(this.objIndex, len)
    }

    fun getCapacity(resourceType: String? = null): Int {
        val len = if (resourceType == RESOURCE_ENERGY) -1
        else resourceType?.toHeapUTF16(0) ?: 0
        return storeGetCapacity(this.objIndex, len)
    }

    fun getFreeCapacity(resourceType: String? = null): Int {
        val len = if (resourceType == RESOURCE_ENERGY) -1
        else resourceType?.toHeapUTF16(0) ?: 0
        return storeGetFreeCapacity(this.objIndex, len)
    }
    fun getUsedCapacity(resourceType: String? = null): Int {
        val len = if (resourceType == RESOURCE_ENERGY) -1
        else resourceType?.toHeapUTF16(0) ?: 0
        return storeGetUsedCapacity(this.objIndex, len)
    }
}