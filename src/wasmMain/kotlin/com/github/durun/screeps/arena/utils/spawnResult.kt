package com.github.durun.screeps.arena.utils

import com.github.durun.screeps.arena.BodyType
import com.github.durun.screeps.arena.Creep
import com.github.durun.screeps.arena.Spawn

fun Spawn.spawnCreepCatching(body: List<BodyType>): ScreepsResult<Creep> {
    val (creep, err) = spawnCreep(body)
    return ScreepsResult(creep, err)
}
