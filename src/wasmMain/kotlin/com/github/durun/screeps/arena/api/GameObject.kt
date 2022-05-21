package com.github.durun.screeps.arena.api

import com.github.durun.screeps.arena.jsinterop.*

abstract class GameObject(
    val index: Int
) : RoomPosition {
    val exists: Boolean get() = getExists(this.index)
    val ticksToDecay: Int? get() = getTicksToDecay(this.index).let { if (0 <= it) it else null }
    override val x: Int get() = getX(this.index)
    override val y: Int get() = getY(this.index)
    val id: String by lazy {
        val size = getId(this.index)
        ByteArray(size) { getHeapUint8(it) }
            .decodeToString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameObject) return false
        return (this.id == other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun <T : RoomPosition> findClosestByPath(
        targets: List<T>,
        costMatrix: CostMatrix? = null,
        plainCost: Int? = null,
        swampCost: Int? = null,
        flee: Boolean = false,
        maxOps: Int? = null,
        maxCost: Int? = null,
        heuristicWeight: Int? = null
    ): T? {
        targets.toHeap()
        val result = findClosestByPath(
            this.index,
            costMatrix?.index ?: -1,
            plainCost ?: -1,
            swampCost ?: -1,
            flee,
            maxOps ?: -1,
            maxCost ?: -1,
            heuristicWeight ?: -1,
            targets.size
        )
        return if (0 <= result) targets[result]
        else null
    }

    fun <T : RoomPosition> findClosestByRange(targets: List<T>): T? {
        targets.toHeap()
        val result = findClosestByRange(this.index, targets.size)
        return if (0 <= result) targets[result]
        else null
    }

    fun <T : RoomPosition> findInRange(targets: List<T>, range: Double): List<T> {
        targets.toHeap()
        val resultSize = findInRange(this.index, targets.size, range)
        return (0 until resultSize).map { targets[getHeapInt32(it)] }
    }

    fun findPathTo(
        target: RoomPosition,
        costMatrix: CostMatrix? = null,
        plainCost: Int? = null,
        swampCost: Int? = null,
        flee: Boolean = false,
        maxOps: Int? = null,
        maxCost: Int? = null,
        heuristicWeight: Int? = null
    ): List<Vec2> {
        listOf(target).toHeap()
        val numSteps = findPathTo(
            this.index,
            costMatrix?.index ?: -1,
            plainCost ?: -1,
            swampCost ?: -1,
            flee,
            maxOps ?: -1,
            maxCost ?: -1,
            heuristicWeight ?: -1
        )
        return (0 until numSteps).map {
            Vec2(getHeapInt32(it * 2), getHeapInt32(it * 2 + 1))
        }
    }
}