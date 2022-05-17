package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.structureHits
import com.github.durun.screeps.arena.jsinterop.structureHitsMax

abstract class Structure(index: Int): GameObject(index) {
    val hits: Int get() = structureHits(this.index)
    val hitsMax: Int get() = structureHitsMax(this.index)
}