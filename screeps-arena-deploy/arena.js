import {instantiateLinking, konan_dependencies} from "./kotlin.js";
import * as game from "game";
import {
    Creep,
    Source,
    StructureContainer,
    StructureExtension,
    StructureRampart,
    StructureSpawn,
    StructureTower,
    StructureWall
} from "game/prototypes";
import {ATTACK, CARRY, HEAL, MOVE, RANGED_ATTACK, RESOURCE_ENERGY, TOUGH, WORK} from "game/constants";
import {CostMatrix} from "game/path-finder";
import {Visual} from "game/visual";
import {createConstructionSite, getCpuTime, getTerrainAt, getTicks} from "game/utils";
// import {Flag} from "arena/prototypes";

const heapUint8 = new Uint8Array(131072);
const heapInt32 = new Int32Array(4096);

/**
 * @type {Array<GameObject|Creep|Flag|Source|ConstructionSite|Structure|OwnedStructure|StructureTower|StructureSpawn>}
 */
const gameObjects = [];
/**
 * @type {CostMatrix[]}
 */
const costMatrices = [];
/**
 * @type {Visual[]}
 */
const visuals = [];

/**
 * @param array{Array<number> | undefined}
 * @return {number}
 */
function allocateArena(array) {
    return konan_dependencies.env.Konan_js_allocateArena(array);
}

/**
 * @param {number}start
 * @param {number}byteSize
 * @return {string}
 */
function fromHeapUTF16(start, byteSize) {
    let string = '';
    for (let i = start; i < start + byteSize; i += 2) {
        string += String.fromCharCode(heapUint8[i] + heapUint8[i + 1] * 256);
    }
    return string;
}

/**
 * @param {string}str
 * @param {number}offset
 * @return {number} byte length
 */
function toHeapUTF16(str, offset) {
    for (let i = 0; i < str.length; i++) {
        const code = str.charCodeAt(i);
        const lower = code & 0xff;
        const upper = code >>> 8 & 0xff;
        heapUint8[offset + i * 2] = lower;
        heapUint8[offset + i * 2 + 1] = upper;
    }
    return str.length * 2;
}

/**
 * @param {string}str
 * @param {number}offset
 * @return {number} byte length
 */
function toHeapUTF8(str, offset) {
    /** @type number[] */
    const scalars = [];
    for (let i = 0; i < str.length; i++) {
        const code = str.charCodeAt(i);
        if (0xd800 <= code && code <= 0xdbff) {
            const code2 = str.charCodeAt(++i);
            scalars.push((code & 1023) + 64 << 10 | code2 & 1023);
        } else {
            scalars.push(code);
        }
    }
    let i = offset;
    scalars.forEach(c => {
        if (c <= 0x7f) { //1byte
            heapUint8[i++] = c;
        } else if (c <= 0x7ff) { //2byte
            heapUint8[i++] = 0xc0 | c >>> 6;
            heapUint8[i++] = 0x80 | c & 0xbf;
        } else if (c <= 0xffff) { //3byte
            heapUint8[i++] = 0xe0 | c >>> 12;
            heapUint8[i++] = 0x80 | c >>> 6 & 0xbf;
            heapUint8[i++] = 0x80 | c & 0xbf;
        } else if (c <= 0x10ffff) { //4byte
            heapUint8[i++] = 0xf0 | c >>> 18;
            heapUint8[i++] = 0x80 | c >>> 12 & 0xbf;
            heapUint8[i++] = 0x80 | c >>> 6 & 0xbf;
            heapUint8[i++] = 0x80 | c & 0xbf;
        }
    });
    return i - offset;
}

/**
 * @param {number}start
 * @param {number}length
 * @return {Int32Array}
 */
function fromHeapInt32(start, length) {
    return heapInt32.slice(start, start + length);
}

/**
 * @param {_Constructor<Creep|Flag|Source|StructureContainer|StructureTower|StructureTower|StructureSpawn>}prototype
 * @return {number}
 * heapInt32[0:return] : indices of objects
 */
function getGameObjects(prototype) {
    const objs = game.utils.getObjectsByPrototype(prototype);
    const start = gameObjects.length;
    gameObjects.push(...objs);
    objs.forEach((_, i) => {
        heapInt32[i] = start + i;
    });
    return objs.length;
}

/**
 * @param {0|1|2|3|4|5|6}int
 * @return {RANGED_ATTACK|ATTACK|WORK|CARRY|TOUGH|MOVE|HEAL}
 */
function intToBodyType(int) {
    switch (int) {
        case 0:
            return MOVE;
        case 1:
            return WORK;
        case 2:
            return CARRY;
        case 3:
            return ATTACK;
        case 4:
            return RANGED_ATTACK;
        case 5:
            return HEAL;
        case 6:
            return TOUGH;
        default:
            Error(`Illegal number of BodyType: ${int}`);
    }
}

/**
 * @param {RANGED_ATTACK|ATTACK|WORK|CARRY|TOUGH|MOVE|HEAL}str
 * @return {0|1|2|3|4|5|6}
 */
function bodyTypeToInt(str) {
    switch (str) {
        case MOVE:
            return 0;
        case WORK:
            return 1;
        case CARRY:
            return 2;
        case ATTACK:
            return 3;
        case RANGED_ATTACK:
            return 4;
        case HEAL:
            return 5;
        case TOUGH:
            return 6;
        default:
            Error(`Illegal string of BodyType: ${str}`);
    }
}

/**
 * @param {number}numOfPositions
 * @return {Array<{x: number, y: number}|GameObject>}
 */
function positionsFromHeap(numOfPositions) {
    /** @type {Array<{x: number, y: number}|GameObject>}*/
    const positions = [];
    for (let i = 0; i < numOfPositions; i++) {
        const x = heapInt32[i * 2];
        const y = heapInt32[i * 2 + 1];
        if (x !== -1) {
            positions.push({x: x, y: y});
        } else {
            positions.push(gameObjects[y]);
        }
    }
    return positions;
}

/**
 * @param {number}int (r<<<16 | g<<<8 | b)
 * @return {string}
 */
function intToColor(int) {
    const code = `000000${int.toString(16)}`;
    return `#${code.substring(code.length - 6)}`;
}

/**
 * @param {0|1|2}int
 * @return {"dashed"|"dotted"|undefined}
 */
function intToLineStyle(int) {
    switch (int) {
        case 0:
            return undefined;
        case 1:
            return "dashed";
        case 2:
            return "dotted";
        default:
            Error(`Illegal line style: ${int}`);
    }
}

/**
 *
 * @param {0|1|2}int
 * @return {"left"|"right"|undefined}
 */
function intToAling(int) {
    switch (int) {
        case 0:
            return undefined;
        case 1:
            return "left";
        case 2:
            return "right";
        default:
            Error(`Illegal aling: ${int}`);
    }
}

/**
 * @param {1|2|3|4|5|6}int
 * @return {StructureWallConstructor|StructureExtensionConstructor|StructureSpawnConstructor|StructureContainerConstructor|StructureTowerConstructor|StructureRampartConstructor}
 */
function intToPrototype(int) {
    switch (int) {
        case 1:
            return StructureContainer;
        case 2:
            return StructureExtension;
        case 3:
            return StructureRampart;
        case 4:
            return StructureSpawn;
        case 5:
            return StructureTower;
        case 6:
            return StructureWall;
        default:
            Error(`Illegal prototype: ${int}`);
    }
}

export const dependencies = {
    /**
     * @param {number}index
     * @param {number}byte
     */
    toHeapUint8: function (index, byte) {
        heapUint8[index] = byte;
    },
    /**
     * @param {number}index
     * @return {number}
     */
    getHeapUint8: function (index) {
        return heapUint8[index];
    },
    /**
     * @param {number}index
     * @param {number}i32
     */
    toHeapInt32: function (index, i32) {
        heapInt32[index] = i32;
    },
    /**
     *
     * @param {number}index
     * @return {number}
     */
    getHeapInt32: function (index) {
        return heapInt32[index];
    },
    /**
     * @param {number}arenaIndex
     * @return {number}
     */
    arenaLength: function (arenaIndex) {
        const arena = konan_dependencies.env.arenas.get(arenaIndex);
        return arena.length;
    },
    /**
     * @param {number}arenaIndex
     * @param {number}objectIndex
     * @return {number}
     */
    getNumberFromArena: function (arenaIndex, objectIndex) {
        const arena = konan_dependencies.env.arenas.get(arenaIndex);
        return arena[objectIndex];
    },
    /**************************************** game/utils ****************************************/
    /**
     * @return {number}
     * heapInt32[0:return] : indices of creeps
     */
    getCreeps: function () {
        return getGameObjects(Creep);
    },
    /**
     * @return {number}
     * heapInt32[0:return] : indices of containers
     */
    getContainers: function () {
        return getGameObjects(StructureContainer);
    },
    /**
     * @return {number}
     * heapInt32[0:return] : indices of sources
     */
    getSources: function () {
        return getGameObjects(Source);
    },
    /**
     * @return {number}
     * heapInt32[0:return] : indices of spawns
     */
    getSpawns: function () {
        return getGameObjects(StructureSpawn);
    },
    /**
     * @return {number}
     * heapInt32[0:return] : indices of towers
     */
    getTowers: function () {
        return getGameObjects(StructureTower);
    },
    /**
     * @return {number}
     * heapInt32[0:return] : indices of flags
     */
    getFlags: function () {
        return getGameObjects(Flag);
    },
    /**
     * @param {number}x
     * @param {number}y
     * @param {number}type
     * @see {intToPrototype}
     * @return {ERR_INVALID_ARGS|ERR_INVALID_TARGET|ERR_FULL|number}
     */
    createConstructionSite: function (x, y, type) {
        const result = createConstructionSite(x, y, intToPrototype(type));
        if (result.object) {
            gameObjects.push(result.object);
            return gameObjects.length - 1;
        } else {
            return result.error;
        }
    },
    /**************************************** GameObject ****************************************/
    /**
     * @param {number}objIndex
     * @return {number}
     */
    getX: function (objIndex) {
        return gameObjects[objIndex].x;
    },
    /**
     * @param {number}objIndex
     * @return {number}
     */
    getY: function (objIndex) {
        return gameObjects[objIndex].y;
    },
    /**
     * @param {number}objIndex
     * @return {boolean}
     */
    getExists: function (objIndex) {
        return gameObjects[objIndex].exists;
    },
    /**
     * @param {number}objIndex
     * @return {number} byte length (UTF8)
     */
    getId: function (objIndex) {
        const id = gameObjects[objIndex].id;
        return toHeapUTF8(id, 0);
    },
    /**
     * @param {number}objIndex
     * @return {number}
     */
    getTicksToDecay: function (objIndex) {
        const value = gameObjects[objIndex].ticksToDecay;
        if (value !== undefined) return value;
        else return -1;
    },
    /**
     * @param {number}objIndex
     * @param {number|-1}costMatrixIndex
     * @param {number|-1}plainCost
     * @param {number|-1}swampCost
     * @param {boolean}flee (default=false)
     * @param {number|-1}maxOps
     * @param {number|-1}maxCost
     * @param {number|-1}heuristicWeight
     * @param {number}numOfPositions
     * heapInt32 = [x, y, x, y, ...] or [-1, targetIndex, -1, targetIndex, ...]
     * @return {number|-1} index (in positions) of GameObject|RoomPosition, or -1 when not found
     */
    findClosestByPath: function (
        objIndex,
        costMatrixIndex, plainCost, swampCost, flee, maxOps, maxCost, heuristicWeight, // opts
        numOfPositions,
    ) {
        const opts = {
            costMatrix: (0 <= costMatrixIndex) ? costMatrices[costMatrixIndex] : undefined,
            plainCost: (plainCost !== -1) ? plainCost : undefined,
            swampCost: (swampCost !== -1) ? swampCost : undefined,
            flee: flee,
            maxOps: (0 <= maxOps) ? maxOps : undefined,
            maxCost: (maxCost !== -1) ? maxCost : undefined,
            heuristicWeight: (heuristicWeight !== -1) ? heuristicWeight : undefined
        };
        const positions = positionsFromHeap(numOfPositions);
        const result = gameObjects[objIndex].findClosestByPath(positions, opts);
        if (result === null) {
            return -1;
        } else if (result.id !== undefined) { // result: GameObject
            return positions.findIndex(v => v.id === result.id);
        } else { // result: {x: number, y: number}
            return positions.findIndex(v => (v.x === result.x && v.y === result.y));
        }
    },
    /**
     * @param {number}objIndex
     * @param {number}numOfPositions
     * heapInt32 = [x, y, x, y, ...] or [-1, targetIndex, -1, targetIndex, ...]
     * @return {number|-1} index (in positions) of GameObject|RoomPosition, or -1 when not found
     */
    findClosestByRange: function (objIndex, numOfPositions) {
        const positions = positionsFromHeap(numOfPositions);
        const result = gameObjects[objIndex].findClosestByRange(positions);
        if (result === null) {
            return -1;
        } else if (result.id !== undefined) { // result: GameObject
            return positions.findIndex(v => v.id === result.id);
        } else { // result: {x: number, y: number}
            return positions.findIndex(v => (v.x === result.x && v.y === result.y));
        }
    },
    /**
     * @param {number}objIndex
     * @param {number}numOfPositions
     * @param {number}range
     * @return {number} length of heapInt32,
     * heapInt32 = indices (in positions) of GameObject|RoomPosition
     */
    findInRange: function (objIndex, numOfPositions, range) {
        const positions = positionsFromHeap(numOfPositions);
        const results = gameObjects[objIndex].findInRange(positions, range);
        results.forEach((r, i) => {
            if (r.id !== undefined) { // r: GameObject
                heapInt32[i] = positions.findIndex(v => v.id === r.id);
            } else { // r: {x: number, y: number}
                heapInt32[i] = positions.findIndex(v => (v.x === r.x && v.y === r.y));
            }
        });
        return results.length;
    },
    /**
     * @param {number}objIndex
     * @param {number|-1}costMatrixIndex
     * @param {number|-1}plainCost
     * @param {number|-1}swampCost
     * @param {boolean}flee (default=false)
     * @param {number|-1}maxOps
     * @param {number|-1}maxCost
     * @param {number|-1}heuristicWeight
     * @return {number} num of PathStep,
     * heapInt32 = [x, y, x, y, ...]
     */
    findPathTo: function (
        objIndex,
        costMatrixIndex, plainCost, swampCost, flee, maxOps, maxCost, heuristicWeight // opts
    ) {
        const opts = {
            costMatrix: (0 <= costMatrixIndex) ? costMatrices[costMatrixIndex] : undefined,
            plainCost: (plainCost !== -1) ? plainCost : undefined,
            swampCost: (swampCost !== -1) ? swampCost : undefined,
            flee: flee,
            maxOps: (0 <= maxOps) ? maxOps : undefined,
            maxCost: (maxCost !== -1) ? maxCost : undefined,
            heuristicWeight: (heuristicWeight !== -1) ? heuristicWeight : undefined
        };
        const position = positionsFromHeap(1)[0];
        const steps = gameObjects[objIndex].findPathTo(position, opts);
        steps.forEach((step, i) => {
            heapInt32[i * 2] = step.x;
            heapInt32[i * 2 + 1] = step.y;
        });
        return steps.length;
    },
    /**************************************** Store ****************************************/
    /**
     * @param {number}objIndex
     * @param {number|-1}resourceLength
     * when positive: heapUint8 = resource (UTF16),
     * -1=RESOURCE_ENERGY
     * @return {number}
     */
    getStore: function (objIndex, resourceLength) {
        const resource = (0 <= resourceLength) ? fromHeapUTF16(0, resourceLength) : RESOURCE_ENERGY;
        return gameObjects[objIndex].store[resource];
    },
    /**
     * @param {number}objIndex
     * @param {number|0|-1}resourceLength
     * when positive: heapUint8 = resource (UTF16),
     *  0=undefined,
     * -1=RESOURCE_ENERGY
     * @return {number}
     */
    storeGetCapacity: function (objIndex, resourceLength) {
        const resource = (0 < resourceLength) ? fromHeapUTF16(0, resourceLength)
            : (resourceLength === -1) ? RESOURCE_ENERGY : undefined;
        return gameObjects[objIndex].store.getCapacity(resource);
    },
    /**
     * @param {number}objIndex
     * @param {number|0|-1}resourceLength
     * when positive: heapUint8 = resource (UTF16),
     *  0=undefined,
     * -1=RESOURCE_ENERGY
     * @return {number}
     */
    storeGetFreeCapacity: function (objIndex, resourceLength) {
        const resource = (0 < resourceLength) ? fromHeapUTF16(0, resourceLength)
            : (resourceLength === -1) ? RESOURCE_ENERGY : undefined;
        return gameObjects[objIndex].store.getFreeCapacity(resource);
    },
    /**
     * @param {number}objIndex
     * @param {number|0|-1}resourceLength
     * when positive: heapUint8 = resource (UTF16),
     *  0=undefined,
     * -1=RESOURCE_ENERGY
     * @return {number}
     */
    storeGetUsedCapacity: function (objIndex, resourceLength) {
        const resource = (0 < resourceLength) ? fromHeapUTF16(0, resourceLength)
            : (resourceLength === -1) ? RESOURCE_ENERGY : undefined;
        return gameObjects[objIndex].store.getUsedCapacity(resource);
    },
    /**************************************** Creep ****************************************/
    /**
     * @param {number}creepIndex
     * @param {DirectionConstant}direction
     * @return {CreepMoveReturnCode}
     */
    creepMove: function (creepIndex, direction) {
        return gameObjects[creepIndex].move(direction);
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @return {CreepMoveReturnCode | ERR_NO_PATH | ERR_INVALID_TARGET}
     */
    creepMoveToTarget: function (creepIndex, targetIndex) {
        const creep = gameObjects[creepIndex];
        const target = gameObjects[targetIndex];
        return creep.moveTo(target);
    },
    /**
     * @param {number}creepIndex
     * @param {number}x
     * @param {number}y
     * @return {CreepMoveReturnCode | ERR_NO_PATH | ERR_INVALID_TARGET}
     */
    creepMoveToPos: function (creepIndex, x, y) {
        return gameObjects[creepIndex].moveTo({x: x, y: y});
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @return {CreepActionReturnCode}
     */
    creepAttack: function (creepIndex, targetIndex) {
        const creep = gameObjects[creepIndex];
        const target = gameObjects[targetIndex];
        return creep.attack(target);
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @return {CreepActionReturnCode}
     */
    creepRangedAttack: function (creepIndex, targetIndex) {
        const creep = gameObjects[creepIndex];
        const target = gameObjects[targetIndex];
        return creep.rangedAttack(target);
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @return {CreepActionReturnCode | ERR_NOT_ENOUGH_RESOURCES}
     */
    creepBuild: function (creepIndex, targetIndex) {
        /** @type {ConstructionSite} */
        const target = gameObjects[targetIndex];
        return gameObjects[creepIndex].build(target);
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @return {CreepActionReturnCode | ERR_NOT_FOUND | ERR_NOT_ENOUGH_RESOURCES}
     */
    creepHarvest: function (creepIndex, targetIndex) {
        /** @type {Source} */
        const target = gameObjects[targetIndex];
        return gameObjects[creepIndex].harvest(target);
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @return {CreepActionReturnCode}
     */
    creepHeal: function (creepIndex, targetIndex) {
        const creep = gameObjects[creepIndex];
        const target = gameObjects[targetIndex];
        return creep.heal(target);
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @param {number|-1}resourceLength
     * when positive: heapUint8 = resource (UTF16),
     * -1=RESOURCE_ENERGY
     * @param {number|-1}amount
     * @return {ScreepsReturnCode}
     */
    creepTransfer: function (creepIndex, targetIndex, resourceLength, amount) {
        const resource = (0 <= resourceLength) ? fromHeapUTF16(0, resourceLength) : RESOURCE_ENERGY;
        /** @type{Creep|Structure} */
        const target = gameObjects[targetIndex];
        return gameObjects[creepIndex].transfer(target, resource, (0 <= amount) ? amount : undefined);
    },
    /**
     * @param {number}creepIndex
     * @param {number}targetIndex
     * @param {number|-1}resourceLength
     * when positive: heapUint8 = resource (UTF16),
     * -1=RESOURCE_ENERGY
     * @param {number|-1}amount
     * @return {ScreepsReturnCode}
     */
    creepWithdraw: function (creepIndex, targetIndex, resourceLength, amount) {
        const resource = (0 <= resourceLength) ? fromHeapUTF16(0, resourceLength) : RESOURCE_ENERGY;
        /** @type{Creep|Structure} */
        const target = gameObjects[targetIndex];
        return gameObjects[creepIndex].withdraw(target, resource, (0 <= amount) ? amount : undefined);
    },
    /**
     * @param {number}creepIndex
     * @return {boolean}
     */
    creepMy: function (creepIndex) {
        const creep = gameObjects[creepIndex];
        return creep.my;
    },
    /**
     * @param {number}creepIndex
     * @return {number}
     */
    creepFatigue: function (creepIndex) {
        return gameObjects[creepIndex].fatigue;
    },
    /**
     * @param {number}creepIndex
     * @return {number}
     */
    creepHits: function (creepIndex) {
        const creep = gameObjects[creepIndex];
        return creep.hits;
    },
    /**
     * @param {number}creepIndex
     * @return {number}
     */
    creepHitsMax: function (creepIndex) {
        const creep = gameObjects[creepIndex];
        return creep.hitsMax;
    },
    /**
     * @param {number}creepIndex
     * @return {number}
     * for i in (0 until return)
     *   heapInt32[i*2  ] = bodyType
     *   heapInt32[i*2+1] = hits
     */
    creepBody_heap: function (creepIndex) {
        /** @type {Creep} */
        const creep = gameObjects[creepIndex]
        creep.body.forEach((body, i) => {
            heapInt32[i * 2] = bodyTypeToInt(body.type);
            heapInt32[i * 2 + 1] = body.hits;
        });
        return creep.body.length;
    },
    /**************************************** Source ****************************************/
    sourceEnergy: function (index) {
        return gameObjects[index].energy;
    },
    sourceEnergyCapacity: function (index) {
        return gameObjects[index].energyCapacity;
    },
    /**************************************** ConstructionSite ****************************************/
    /**
     * @param {number}index
     * @return {boolean}
     */
    constructionSiteMy: function (index) {
        return gameObjects[index].my;
    },
    /**
     * @param {number}index
     * @return {number}
     */
    constructionSiteProgress: function (index) {
        return gameObjects[index].progress;
    },
    /**
     * @param {number}index
     * @return {number|-1} index of structure or -1,
     * heapInt32[0] = size
     * heapUint8[0:size] = structurePrototypeName (UTF8)
     */
    constructionSiteProgressTotal: function (index) {
        return gameObjects[index].progressTotal;
    },
    constructionSiteStructure: function (index) {
        /** @type ConstructionSite */
        const site = gameObjects[index];
        /** @type Structure */
        const structure = site.structure;
        if (structure) {
            heapInt32[0] = toHeapUTF8(site.structurePrototypeName, 0);
            gameObjects.push(structure);
            return gameObjects.length - 1;
        } else {
            return -1;
        }
    },
    /**
     * @param {number}index
     * @return {number}
     */
    constructionSiteRemove: function (index) {
        return gameObjects[index].remove()
    },
    /**************************************** Structure ****************************************/
    /**
     * @param {number}index
     * @return {number}
     */
    structureHits: function (index) {
        return gameObjects[index].hits;
    },
    /**
     * @param {number}index
     * @return {number}
     */
    structureHitsMax: function (index) {
        return gameObjects[index].hitsMax;
    },
    /**************************************** OwnedStructure ****************************************/
    /**
     * @param {number}index
     * @return {boolean}
     */
    ownedStructureMy: function (index) {
        return gameObjects[index].my;
    },
    /**************************************** Spawn ****************************************/
    /**
     * @param {number}index
     * @param {number}bodyLength
     * heapInt32 = bodies {Array<0|1|2|3|4|5|6>}
     * @return {number}
     * when positive: index of spawn,
     * when negative: error code
     */
    spawnCreep: function (index, bodyLength) {
        /** @type {BodyPartConstant[]} */
        const body = [];
        for (let i = 0; i < bodyLength; i++) {
            body.push(intToBodyType(heapInt32[i]));
        }
        const result = gameObjects[index].spawnCreep(body);
        if (result.object) {
            gameObjects.push(result.object);
            return gameObjects.length - 1;
        } else {
            return result.error;
        }
    },
    /**************************************** Tower ****************************************/
    /**
     * @param {number}index
     * @return {number}
     */
    towerCooldown: function (index) {
        return gameObjects[index].cooldown;
    },
    /**
     * @param {number}index
     * @param {number}targetIndex
     * @return {CreepActionReturnCode}
     */
    towerAttack: function (index, targetIndex) {
        /** @type {Creep|Structure} */
        const target = gameObjects[targetIndex];
        return gameObjects[index].attack(target);
    },
    /**
     * @param {number}index
     * @param {number}targetIndex
     * @return {CreepActionReturnCode}
     */
    towerHeal: function (index, targetIndex) {
        /** @type {Creep|Structure} */
        const target = gameObjects[targetIndex];
        return gameObjects[index].heal(target);
    },
    /**************************************** CostMatrix ****************************************/
    /**
     * @return {number}
     */
    newCostMatrix: function () {
        const matrix = new CostMatrix();
        costMatrices.push(matrix);
        return costMatrices.length - 1;
    },
    /**
     * @param {number}index
     * @param {number}x
     * @param {number}y
     * @param {number}cost
     */
    costMatrixSet: function (index, x, y, cost) {
        costMatrices[index].set(x, y, cost);
    },
    /**
     * @param {number}index
     * @param {number}x
     * @param {number}y
     * @return {number}
     */
    costMatrixGet: function (index, x, y) {
        return costMatrices[index].get(x, y);
    },
    /**
     * @param {number}index
     * @return {number}
     */
    costMatrixClone: function (index) {
        const newMatrix = costMatrices[index].clone();
        costMatrices.push(newMatrix);
        return costMatrices.length - 1;
    },
    /**************************************** Visual ****************************************/
    /**
     * @param {number}layer
     * @param {boolean}persistent
     * @return {number} index of Visual
     */
    newVisual: function (layer, persistent) {
        visuals.push(new Visual(layer, !!persistent));
        return visuals.length - 1;
    },
    /**
     * @param {number}index
     */
    visualClear: function (index) {
        visuals[index].clear();
    },
    /**
     * @param {number}index
     * @param {number}x
     * @param {number}y
     * @param {number|-1}radius
     * @param {number|-1}fill color
     * @param {number|-1}opacity
     * @param {number|-1}stroke color
     * @param {number|-1}strokeWidth
     * @param {0|1|2}lineStyle 0=undefined(default), 1=dashed, 2=dotted
     */
    visualCircle: function (index, x, y, radius, fill, opacity, stroke, strokeWidth, lineStyle) {
        const style = {
            radius: (0 <= radius) ? radius : undefined,
            fill: (fill !== -1) ? intToColor(fill) : undefined,
            opacity: (0 <= opacity) ? opacity : undefined,
            stroke: (stroke !== -1) ? intToColor(stroke) : undefined,
            strokeWidth: (0 <= strokeWidth) ? strokeWidth : undefined,
            lineStyle: intToLineStyle(lineStyle),
        };
        visuals[index].circle({x: x, y: y}, style);
    },
    /**
     * @param {number}index
     * @param {number}x1
     * @param {number}y1
     * @param {number}x2
     * @param {number}y2
     * @param {number|-1}width
     * @param {number|-1}color
     * @param {number|-1}opacity
     * @param {0|1|2}lineStyle
     */
    visualLine: function (index, x1, y1, x2, y2, width, color, opacity, lineStyle) {
        const style = {
            width: (0 <= width) ? width : undefined,
            color: (color !== -1) ? intToColor(color) : undefined,
            opacity: (0 <= opacity) ? opacity : undefined,
            lineStyle: intToLineStyle(lineStyle),
        };
        visuals[index].line({x: x1, y: y1}, {x: x2, y: y2}, style);
    },
    /**
     * @param {number}index
     * @param {number}numPoints
     * heapInt32 = [x, y, x, y, ...]
     * @param {number|-1}fill
     * @param {number|-1}opacity
     * @param {number|-1}stroke
     * @param {number|-1}strokeWidth
     * @param {0|1|2}lineStyle
     */
    visualPoly: function (index, numPoints, fill, opacity, stroke, strokeWidth, lineStyle) {
        const style = {
            fill: (fill !== -1) ? intToColor(fill) : undefined,
            opacity: (0 <= opacity) ? opacity : undefined,
            stroke: (stroke !== -1) ? intToColor(stroke) : undefined,
            strokeWidth: (0 <= strokeWidth) ? strokeWidth : undefined,
            lineStyle: intToLineStyle(lineStyle),
        };
        const points = positionsFromHeap(numPoints);
        visuals[index].poly(points, style);
    },
    /**
     * @param {number}index
     * @param {number}x
     * @param {number}y
     * @param {number}w
     * @param {number}h
     * @param {number|-1}fill
     * @param {number|-1}opacity
     * @param {number|-1}stroke
     * @param {number|-1}strokeWidth
     * @param {0|1|2}lineStyle
     */
    visualRect: function (index, x, y, w, h, fill, opacity, stroke, strokeWidth, lineStyle) {
        const style = {
            fill: (fill !== -1) ? intToColor(fill) : undefined,
            opacity: (0 <= opacity) ? opacity : undefined,
            stroke: (stroke !== -1) ? intToColor(stroke) : undefined,
            strokeWidth: (0 <= strokeWidth) ? strokeWidth : undefined,
            lineStyle: intToLineStyle(lineStyle),
        };
        visuals[index].rect({x: x, y: y}, w, h, style);
    },
    /**
     * @param {number}index
     * @param {number}x
     * @param {number}y
     * @param {number}textLength length of text string
     * @param {number|-1}color
     * @param {number|-1}fontLength length of font option stirng
     * @param {number|-1}stroke
     * @param {number|-1}strokeWidth
     * @param {number|-1}bgColor
     * @param {number|-1}padding
     * @param {0|1|2}aling 0=center (default), 1=left, 2=right
     * @param {number|-1}opacity
     */
    visualText: function (index, x, y, textLength, color, fontLength, stroke, strokeWidth, bgColor, padding, aling, opacity) {
        const text = fromHeapUTF16(0, textLength);
        const style = {
            color: (color !== -1) ? intToColor(color) : undefined,
            font: (fontLength !== -1) ? fromHeapUTF16(textLength, fontLength) : undefined,
            stroke: (stroke !== -1) ? intToColor(stroke) : undefined,
            strokeWidth: (0 <= strokeWidth) ? strokeWidth : undefined,
            backgroundColor: (bgColor !== -1) ? intToColor(bgColor) : undefined,
            backgroundPadding: (0 <= padding) ? padding : undefined,
            aling: intToAling(aling),
            opacity: (0 <= opacity) ? opacity : undefined,
        };
        visuals[index].text(text, {x: x, y: y}, style);
    },
    /**************************************** Functions ****************************************/
    getCpuTime: getCpuTime,
    /**
     * @param {number} x
     * @param {number} y
     * @return {0|TERRAIN_WALL|TERRAIN_SWAMP}
     */
    getTerrainAt: function (x, y) {
        return getTerrainAt({x: x, y: y});
    },
    getTicks: getTicks
}

/**
 * @param {BufferSource} wasmBin
 * @return {Promise<WebAssembly.WebAssemblyInstantiatedSource>}
 */
export function instantiate(wasmBin) {
    return instantiateLinking(wasmBin, dependencies, ["hoge"]);
}

export default {instantiate}