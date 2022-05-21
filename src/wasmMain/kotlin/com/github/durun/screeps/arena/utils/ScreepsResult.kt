package com.github.durun.screeps.arena.utils

import com.github.durun.screeps.arena.api.Err

open class ScreepsResult<out T>(protected val value: T?, protected val err: Err?) {
    val isSuccess: Boolean get() = (value != null && err == null)
    val isFailure: Boolean get() = (err != null)

    fun errOrNull(): Err? = err
    fun getOrNull(): T? {
        return if (isSuccess) value
        else null
    }

    fun onSuccess(action: (T) -> Unit): ScreepsResult<T> {
        if (value != null && err == null) action(value)
        return this
    }

    fun onFailure(action: (Err) -> Unit): ScreepsResult<T> {
        if (err != null) action(err)
        return this
    }

    fun on(expected: Err, action: (Err) -> Unit): ScreepsResult<T> {
        if (err == expected) action(err)
        return this
    }

    fun <R> map(transform: (T) -> R): ScreepsResult<R> {
        return if (err == null) ScreepsResult(transform(value!!), null)
        else ScreepsResult(null, err)
    }

    fun <R> fold(onSuccess: (T) -> R, onFailure: (Err) -> R): R {
        return if (err == null) onSuccess(value!!)
        else onFailure(err)
    }
}

fun <R, T : R> ScreepsResult<T>.getOrDefault(defaultValue: R): R {
    return if (this.isSuccess) getOrNull()!!
    else defaultValue
}

fun <R, T : R> ScreepsResult<T>.getOrElse(onFailure: (Err) -> R): R {
    return if (this.isSuccess) getOrNull()!!
    else onFailure(errOrNull()!!)
}

fun <R, T : R> ScreepsResult<T>.recover(onFailure: (Err) -> ScreepsResult<R>): ScreepsResult<R> {
    return if (this.isSuccess) this
    else onFailure(this.errOrNull()!!)
}
