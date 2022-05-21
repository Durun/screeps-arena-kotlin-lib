package com.github.durun.screeps.arena.utils

import com.github.durun.screeps.arena.api.BodyType
import com.github.durun.screeps.arena.api.Creep
import com.github.durun.screeps.arena.api.Spawn

fun Spawn.spawnCreepCatching(body: List<BodyType>): ScreepsResult<Creep> {
    val (creep, err) = spawnCreep(body)
    return ScreepsResult(creep, err)
}
