package com.github.durun.screeps.arena.jsinterop

import kotlinx.wasm.jsinterop.JsValue
import kotlinx.wasm.jsinterop.Arena

@SymbolName("toHeapUint8")
external public fun toHeapUint8(index: Int, byte: Byte): Unit

@SymbolName("getHeapUint8")
external public fun getHeapUint8(index: Int): Byte

@SymbolName("logHeapAsString")
external public fun logHeapAsString(index: Int, byteSize: Int): Unit

@SymbolName("toHeapInt32")
external fun toHeapInt32(index: Int, value: Int): Unit

@SymbolName("getHeapInt32")
external fun getHeapInt32(index: Int): Int

@SymbolName("arenaLength")
external public fun arenaLength(arena: Arena): Int

@SymbolName("getNumberFromArena")
external public fun getIntFromArena(arena: Arena, index: Int): Int

/**************************************** arenaInfo ****************************************/
@SymbolName("arenaInfoName")
external fun arenaInfoName(): Int

@SymbolName("arenaInfoLevel")
external fun arenaInfoLevel(): Int

@SymbolName("arenaInfoSeason")
external fun arenaInfoSeason(): Int

@SymbolName("arenaInfoTicksLimit")
external fun arenaInfoTicksLimit(): Int

@SymbolName("arenaInfoCpuTimeLimit")
external fun arenaInfoCpuTimeLimit(): Int

@SymbolName("arenaInfoCpuTimeLimitFirstTick")
external fun arenaInfoCpuTimeLimitFirstTick(): Int

/**************************************** game/utils ****************************************/
@SymbolName("getCreeps")
external fun getCreeps_heap(): Int

@SymbolName("getContainers")
external fun getContainers_heap(): Int

@SymbolName("getSources")
external fun getSources_heap(): Int

@SymbolName("getSpawns")
external fun getSpawns_heap(): Int

@SymbolName("getTowers")
external fun getTowers_heap(): Int

@SymbolName("getFlags")
external fun getFlags_heap(): Int

@SymbolName("createConstructionSite")
external fun createConstructionSite(x: Int, y: Int, type: Int): Int

/**************************************** GameObject ****************************************/
@SymbolName("getX")
external fun getX(objIndex: Int): Int

@SymbolName("getY")
external fun getY(objIndex: Int): Int

@SymbolName("getExists")
external fun getExists(objIndex: Int): Boolean

@SymbolName("getTicksToDecay")
external fun getTicksToDecay(objIndex: Int): Int

@SymbolName("getId")
external fun getId(objIndex: Int): Int

@SymbolName("findClosestByPath")
external fun findClosestByPath(
    objIndex: Int,
    costMatrixIndex: Int,
    plainCost: Int,
    swampCost: Int,
    flee: Boolean,
    maxOps: Int,
    maxCost: Int,
    heuristicWeight: Int,
    numOfPositions: Int
): Int

@SymbolName("findClosestByRange")
external fun findClosestByRange(objIndex: Int, numOfPositions: Int): Int

@SymbolName("findInRange")
external fun findInRange(objIndex: Int, numOfPositions: Int, range: Double): Int

@SymbolName("findPathTo")
external fun findPathTo(
    objIndex: Int,
    costMatrixIndex: Int, plainCost: Int, swampCost: Int, flee: Boolean, maxOps: Int, maxCost: Int, heuristicWeight: Int
): Int

/**************************************** Store ****************************************/
@SymbolName("getStore")
external fun getStore(objIndex: Int, resourceLength: Int): Int

@SymbolName("storeGetCapacity")
external fun storeGetCapacity(objIndex: Int, resourceLength: Int): Int

@SymbolName("storeGetFreeCapacity")
external fun storeGetFreeCapacity(objIndex: Int, resourceLength: Int): Int

@SymbolName("storeGetUsedCapacity")
external fun storeGetUsedCapacity(objIndex: Int, resourceLength: Int): Int

/**************************************** Creep ****************************************/
@SymbolName("creepMove")
external fun creepMove(creepIndex: Int, direction: Int): Int

@SymbolName("creepMoveToTarget")
external fun creepMoveToTarget(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepMoveToPos")
external fun creepMoveToPos(creepIndex: Int, x: Int, y: Int): Int

@SymbolName("creepMy")
external fun creepMy(creepIndex: Int): Boolean

@SymbolName("creepFatigue")
external fun creepFatigue(creepIndex: Int): Int

@SymbolName("creepHits")
external fun creepHits(creepIndex: Int): Int

@SymbolName("creepHitsMax")
external fun creepHitsMax(creepIndex: Int): Int

@SymbolName("creepAttack")
external fun creepAttack(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepRangedAttack")
external fun creepRangedAttack(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepBuild")
external fun creepBuild(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepDrop")
external fun creepDrop(creepIndex: Int, resourceLength: Int, amount: Int): Int

@SymbolName("creepHarvest")
external fun creepHarvest(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepHeal")
external fun creepHeal(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepPickup")
external fun creepPickup(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepPull")
external fun creepPull(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepRangedHeal")
external fun creepRangedHeal(creepIndex: Int, targetIndex: Int): Int

@SymbolName("creepRangedMassAttack")
external fun creepRangedMassAttack(creepIndex: Int): Int

@SymbolName("creepTransfer")
external fun creepTransfer(creepIndex: Int, targetIndex: Int, resourceLength: Int, amount: Int): Int

@SymbolName("creepWithdraw")
external fun creepWithdraw(creepIndex: Int, targetIndex: Int, resourceLength: Int, amount: Int): Int

@SymbolName("creepBody_heap")
external fun creepBody_heap(creepIndex: Int): Int

/**************************************** Source ****************************************/
@SymbolName("sourceEnergy")
external fun sourceEnergy(index: Int): Int

@SymbolName("sourceEnergyCapacity")
external fun sourceEnergyCapacity(index: Int): Int

/**************************************** ConstructionSite ****************************************/
@SymbolName("constructionSiteMy")
external fun constructionSiteMy(index: Int): Boolean

@SymbolName("constructionSiteProgress")
external fun constructionSiteProgress(index: Int): Int

@SymbolName("constructionSiteProgressTotal")
external fun constructionSiteProgressTotal(index: Int): Int

@SymbolName("constructionSiteStructure")
external fun constructionSiteStructure(index: Int): Int

@SymbolName("constructionSiteRemove")
external fun constructionSiteRemove(index: Int): Int

/**************************************** Structure ****************************************/
@SymbolName("structureHits")
external fun structureHits(index: Int): Int

@SymbolName("structureHitsMax")
external fun structureHitsMax(index: Int): Int

/**************************************** OwnedStructure ****************************************/
@SymbolName("ownedStructureMy")
external fun ownedStructureMy(index: Int): Boolean

/**************************************** Spawn ****************************************/
@SymbolName("spawnCreep")
external fun spawnCreep(index: Int, bodyLength: Int): Int

/**************************************** Tower ****************************************/
@SymbolName("towerCooldown")
external fun towerCooldown(index: Int): Int

@SymbolName("towerAttack")
external fun towerAttack(index: Int, targetIndex: Int): Int

@SymbolName("towerHeal")
external fun towerHeal(index: Int, targetIndex: Int): Int

/**************************************** CostMatrix ****************************************/
@SymbolName("newCostMatrix")
external fun newCostMatrix(): Int

@SymbolName("costMatrixSet")
external fun costMatrixSet(index: Int, x: Int, y: Int, cost: Int)

@SymbolName("costMatrixGet")
external fun costMatrixGet(index: Int, x: Int, y: Int): Int

@SymbolName("costMatrixClone")
external fun costMatrixClone(index: Int): Int

/**************************************** Visual ****************************************/
@SymbolName("newVisual")
external fun newVisual(layer: Int, persistent: Boolean): Int

@SymbolName("visualClear")
external fun visualClear(index: Int): Unit

@SymbolName("visualCircle")
external fun visualCircle(
    index: Int, x: Int, y: Int,
    radius: Float, fill: Int, opacity: Float, stroke: Int, strokeWidth: Float, lineStyle: Int
): Unit

@SymbolName("visualLine")
external fun visualLine(
    index: Int, x1: Int, y1: Int, x2: Int, y2: Int,
    width: Float, color: Int, opacity: Float, lineStyle: Int
): Unit

@SymbolName("visualPoly")
external fun visualPoly(
    index: Int, numPoints: Int,
    fill: Int, opacity: Float, stroke: Int, strokeWidth: Float, lineStyle: Int
): Unit

@SymbolName("visualRect")
external fun visualRect(
    index: Int, x: Int, y: Int, w: Int, h: Int,
    fill: Int, opacity: Float, stroke: Int, strokeWidth: Float, lineStyle: Int
): Unit

@SymbolName("visualText")
external fun visualText(
    index: Int,
    x: Int,
    y: Int,
    textLength: Int,
    color: Int,
    fontLength: Int,
    stroke: Int,
    strokeWidth: Float,
    bgColor: Int,
    padding: Float,
    aling: Int,
    opacity: Float
): Unit

/**************************************** Functions ****************************************/
@SymbolName("getCpuTime")
external fun getCpuTime(): Int

@SymbolName("getDirection")
external fun getDirection(x: Int, y: Int): Int

@SymbolName("getTerrainAt")
external fun getTerrainAt(x: Int, y: Int): Int

@SymbolName("getTicks")
external fun getTicks(): Int
