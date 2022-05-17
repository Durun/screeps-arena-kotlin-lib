package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.*

class Tower internal constructor(index: Int): OwnedStructure(index) {
    companion object {
        fun getAll(): List<Tower> {
            val size = getTowers_heap()
            return (0 until size).map { Tower(getHeapInt32(it)) }
        }
    }

    val cooldown: Int get() = towerCooldown(this.index)
    val store: Store = Store(this.index)

    fun attack(target: Creep): Err? {
        val result = towerAttack(this.index, target.index)
        return Err.of(result)
    }

    fun attack(target: Structure): Err? {
        val result = towerAttack(this.index, target.index)
        return Err.of(result)
    }

    fun heal(target: Creep): Err? {
        val result = towerHeal(this.index, target.index)
        return Err.of(result)
    }
}