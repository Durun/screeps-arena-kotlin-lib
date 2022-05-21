package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.*

class Creep internal constructor(index: Int) : GameObject(index) {
    companion object {
        fun getAll(): List<Creep> {
            val size = getCreeps_heap()
            return (0 until size).map { Creep(getHeapInt32(it)) }
        }
    }

    val body: List<BodyPart>
        get() {
            val size = creepBody_heap(this.index)
            return (0 until size).map {
                BodyPart(
                    BodyType.of(getHeapInt32(it * 2)),
                    getHeapInt32(it * 2 + 1)
                )
            }
        }
    val fatigue: Int get() = creepFatigue(this.index)
    val hits: Int get() = creepHits(this.index)
    val hitsMax: Int by lazy { creepHitsMax(this.index) }
    val my: Boolean by lazy { creepMy(this.index) }
    val store: Store = Store(this.index)

    fun attack(target: Creep): Err? = attackToAny(target)
    fun attack(target: Structure): Err? = attackToAny(target)
    private fun attackToAny(target: GameObject): Err? {
        val code = creepAttack(this.index, target.index)
        return Err.of(code)
    }

    fun build(target: ConstructionSite<*>): Err? {
        val code = creepBuild(this.index, target.index)
        return Err.of(code)
    }

    fun drop(resourceType: String = RESOURCE_ENERGY, amount: Int? = null): Err? {
        val code = creepDrop(
            this.index,
            if (resourceType == RESOURCE_ENERGY) -1 else resourceType.toHeapUTF16(0),
            amount ?: -1
        )
        return Err.of(code)
    }

    fun harvest(target: Source): Err? {
        val code = creepHarvest(this.index, target.index)
        return Err.of(code)
    }

    fun heal(target: Creep): Err? {
        val code = creepHeal(this.index, target.index)
        return Err.of(code)
    }

    fun move(direction: Direction): Err? {
        val code = creepMove(this.index, direction.int)
        return Err.of(code)
    }

    fun moveTo(target: GameObject): Err? {
        val code = creepMoveToTarget(this.index, target.index)
        return Err.of(code)
    }

    fun moveTo(target: RoomPosition): Err? {
        val code = creepMoveToPos(this.index, target.x, target.y)
        return Err.of(code)
    }

    fun pickup(target: Resource): Err? {
        val code = creepPickup(this.index, target.index)
        return Err.of(code)
    }

    fun pull(target: Creep): Err? {
        TODO()
    }

    fun rangedAttack(target: Creep): Err? = rangedAttackToAny(target)
    fun rangedAttack(target: Structure): Err? = rangedAttackToAny(target)
    private fun rangedAttackToAny(target: GameObject): Err? {
        val code = creepRangedAttack(this.index, target.index)
        return Err.of(code)
    }

    fun rangedHeal(target: Creep): Err? {
        TODO()
    }

    fun rangedMassAttack(): Err? {
        TODO()
    }

    private fun transferToAny(target: GameObject, resourceType: String = RESOURCE_ENERGY, amount: Int? = null): Err? {
        val len = if (resourceType == RESOURCE_ENERGY) -1
        else resourceType.toHeapUTF16(0)
        val code = creepTransfer(this.index, target.index, len, amount ?: -1)
        return Err.of(code)
    }

    fun transfer(target: Creep, resourceType: String = RESOURCE_ENERGY, amount: Int? = null): Err? =
        transferToAny(target, resourceType, amount)

    fun transfer(target: Structure, resourceType: String = RESOURCE_ENERGY, amount: Int? = null): Err? =
        transferToAny(target, resourceType, amount)

    private fun withdrawFromAny(target: GameObject, resourceType: String = RESOURCE_ENERGY, amount: Int? = null): Err? {
        val len = if (resourceType == RESOURCE_ENERGY) -1
        else resourceType.toHeapUTF16(0)
        val code = creepWithdraw(this.index, target.index, len, amount ?: -1)
        return Err.of(code)
    }

    fun withdraw(target: Creep, resourceType: String = RESOURCE_ENERGY, amount: Int? = null): Err? =
        withdrawFromAny(target, resourceType, amount)

    fun withdraw(target: Structure, resourceType: String = RESOURCE_ENERGY, amount: Int? = null): Err? =
        withdrawFromAny(target, resourceType, amount)
}