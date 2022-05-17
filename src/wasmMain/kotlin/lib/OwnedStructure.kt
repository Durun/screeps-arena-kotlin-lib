package lib

import com.github.durun.screeps.arena.jsinterop.ownedStructureMy

abstract class OwnedStructure(index: Int) : Structure(index) {
    val my: Boolean by lazy { ownedStructureMy(this.index) }
}