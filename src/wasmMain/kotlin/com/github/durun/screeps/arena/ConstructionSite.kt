package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.*

class ConstructionSite<T : Structure> private constructor(index: Int) : GameObject(index) {
    companion object {
        private const val CONTAINER = 1
        private const val EXTENSION = 2
        private const val RAMPART = 3
        private const val SPAWN = 4
        private const val TOWER = 5
        private const val WALL = 6
        fun createContainer(x: Int, y: Int): Pair<ConstructionSite<Container>?, Err?> = create(x, y, CONTAINER)
        fun createExtension(x: Int, y: Int): Pair<ConstructionSite<Extension>?, Err?> = create(x, y, EXTENSION)
        fun createRampart(x: Int, y: Int): Pair<ConstructionSite<Rampart>?, Err?> = create(x, y, RAMPART)
        fun createSpawn(x: Int, y: Int): Pair<ConstructionSite<Spawn>?, Err?> = create(x, y, SPAWN)
        fun createTower(x: Int, y: Int): Pair<ConstructionSite<Tower>?, Err?> = create(x, y, TOWER)
        fun createWall(x: Int, y: Int): Pair<ConstructionSite<Wall>?, Err?> = create(x, y, WALL)
        private fun <T : Structure> create(x: Int, y: Int, type: Int): Pair<ConstructionSite<T>?, Err?> {
            val result = createConstructionSite(x, y, type)
            val (i, err) = if (0 <= result) result to null
            else null to Err.of(result)
            val site = i?.let { ConstructionSite<T>(i) }
            return site to err
        }

        private fun newStructure(type: String, index: Int): Structure {
            return when (type) {
                "StructureContainer" -> Container(index)
                "StructureExtension" -> Extension(index)
                "StructureRampart" -> Rampart(index)
                "StructureSpawn" -> Spawn(index)
                "StructureTower" -> Tower(index)
                "StructureWall" -> Wall(index)
                else -> throw IllegalArgumentException("newStructure(): type=$type")
            }
        }
    }

    val my: Boolean get() = constructionSiteMy(this.index)
    val progress: Int get() = constructionSiteProgress(this.index)
    val progressTotal: Int get() = constructionSiteProgressTotal(this.index)
    val structure: T?
        get() {
            val result = constructionSiteStructure(this.index)
            return if (result < 0) null
            else {
                val typeName = fromHeapUTF8(0, getHeapInt32(0))
                @Suppress("UNCHECKED_CAST")
                newStructure(typeName, result) as T
            }
        }

    fun remove(): Err? {
        val code = constructionSiteRemove(this.index)
        return Err.of(code)
    }
}
