package com.github.durun.screeps.arena.api

import com.github.durun.screeps.arena.jsinterop.getHeapInt32
import com.github.durun.screeps.arena.jsinterop.getSpawns_heap
import com.github.durun.screeps.arena.jsinterop.spawnCreep
import com.github.durun.screeps.arena.jsinterop.toHeapInt32

class Spawn internal constructor(index: Int) : OwnedStructure(index) {
    companion object {
        fun getAll(): List<Spawn> {
            val size = getSpawns_heap()
            return (0 until size).map { Spawn(getHeapInt32(it)) }
        }
    }

    val store: Store = Store(this.index)

    fun spawnCreep(body: List<BodyType>): Pair<Creep?, Err?> {
        body.forEachIndexed { i, bodyType -> toHeapInt32(i, bodyType.int) }
        val result = spawnCreep(this.index, body.size)
        return if (0 <= result) Creep(result) to null
        else null to Err.of(result)
    }
}