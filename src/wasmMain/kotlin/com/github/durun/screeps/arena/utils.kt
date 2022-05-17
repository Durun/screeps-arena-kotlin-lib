package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.getHeapUint8
import com.github.durun.screeps.arena.jsinterop.toHeapInt32
import com.github.durun.screeps.arena.jsinterop.toHeapUint8
import kotlinx.cinterop.getBytes
import kotlinx.cinterop.utf16

internal fun List<RoomPosition>.toHeap() {
    this.forEachIndexed { i, position ->
        if (position is GameObject) {
            toHeapInt32(i * 2, -1)
            toHeapInt32(i * 2 + 1, position.index)
        } else {
            toHeapInt32(i * 2, position.x)
            toHeapInt32(i * 2 + 1, position.y)
        }
    }
}

/**
 * @return byte size
 */
internal fun String.toHeapUTF16(offset: Int): Int {
    val bytes = this.utf16.getBytes()
    bytes.forEachIndexed { i, byte ->
        toHeapUint8(offset + i, byte)
    }
    return bytes.size
}

internal fun fromHeapUTF8(offset: Int, size: Int): String {
    val bytes = ByteArray(size) { getHeapUint8(it + offset) }
    return bytes.decodeToString()
}
