package lib

import com.github.durun.screeps.arena.jsinterop.*

class ConstructionSite<T : Structure> private constructor(index: Int) : GameObject(index) {
    companion object {
        fun createContainer(x: Int, y: Int): Pair<ConstructionSite<Container>?, Err?> {
            val (i, err) = create(x, y, 1)
            val site = i?.let { ConstructionSite<Container>(i) }
            return site to err
        }

        fun createSpawn(x: Int, y: Int): Pair<ConstructionSite<Spawn>?, Err?> {
            val (i, err) = create(x, y, 4)
            val site = i?.let { ConstructionSite<Spawn>(i) }
            return site to err
        }

        fun createTower(x: Int, y: Int): Pair<ConstructionSite<Tower>?, Err?> {
            val (i, err) = create(x, y, 5)
            val site = i?.let { ConstructionSite<Tower>(i) }
            return site to err
        }

        private fun create(x: Int, y: Int, type: Int): Pair<Int?, Err?> {
            val result = createConstructionSite(x, y, type)
            return if (0 <= result) result to null
            else null to Err.of(result)
        }

        private fun newStructure(type: String, index: Int): Structure {
            return when (type) {
                "StructureTower" -> Tower(index)
                "StructureContainer" -> Container(index)
                else -> TODO("newStructure: type=$type")
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