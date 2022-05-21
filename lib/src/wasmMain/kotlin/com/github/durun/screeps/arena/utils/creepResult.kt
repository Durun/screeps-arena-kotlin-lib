package com.github.durun.screeps.arena.utils

import com.github.durun.screeps.arena.api.*

fun Creep.attackCatching(target: Creep): ScreepsResult<Nothing> {
    return ScreepsResult(null, attack(target))
}

fun Creep.attackCatching(target: Structure): ScreepsResult<Nothing> {
    return ScreepsResult(null, attack(target))
}

fun Creep.buildCatching(target: ConstructionSite<*>): ScreepsResult<Nothing> {
    return ScreepsResult(null, build(target))
}

fun Creep.dropCatching(resourceType: String = RESOURCE_ENERGY, amount: Int? = null): ScreepsResult<Nothing> {
    return ScreepsResult(null, drop(resourceType, amount))
}

fun Creep.harvestCatching(target: Source): ScreepsResult<Nothing> {
    return ScreepsResult(null, harvest(target))
}

fun Creep.healCatching(target: Creep): ScreepsResult<Nothing> {
    return ScreepsResult(null, heal(target))
}

fun Creep.moveCatching(direction: Direction): ScreepsResult<Nothing> {
    return ScreepsResult(null, move(direction))
}

fun Creep.moveToCatching(target: GameObject): ScreepsResult<Nothing> {
    return ScreepsResult(null, moveTo(target))
}

fun Creep.moveToCatching(target: RoomPosition): ScreepsResult<Nothing> {
    return ScreepsResult(null, moveTo(target))
}

fun Creep.pickupCatching(target: Resource): ScreepsResult<Nothing> {
    return ScreepsResult(null, pickup(target))
}

fun Creep.pullCatching(target: Creep): ScreepsResult<Nothing> {
    return ScreepsResult(null, pull(target))
}

fun Creep.rangedAttackCatching(target: Creep): ScreepsResult<Nothing> {
    return ScreepsResult(null, rangedAttack(target))
}

fun Creep.rangedAttackCatching(target: Structure): ScreepsResult<Nothing> {
    return ScreepsResult(null, rangedAttack(target))
}

fun Creep.rangedHealCatching(target: Creep): ScreepsResult<Nothing> {
    return ScreepsResult(null, rangedHeal(target))
}

fun Creep.rangedMassAttackCatching(): ScreepsResult<Nothing> {
    return ScreepsResult(null, rangedMassAttack())
}

fun Creep.transferCatching(
    target: Structure,
    resourceType: String = RESOURCE_ENERGY,
    amount: Int? = null
): ScreepsResult<Nothing> {
    return ScreepsResult(null, transfer(target, resourceType, amount))
}

fun Creep.withdrawCatching(target: Creep): ScreepsResult<Nothing> {
    return ScreepsResult(null, withdraw(target))
}

fun Creep.withdrawCatching(target: Structure): ScreepsResult<Nothing> {
    return ScreepsResult(null, withdraw(target))
}