package com.liang.map.util

open class SingletonHolder<out T : Any>(private val creator: () -> T) {
    private var instance: T? = null

    fun getInstance(): T =
        instance ?: synchronized(this) {
            instance ?: creator().also {
                instance = it
            }
        }
}

open class SingletonHolderParameterOne<out T : Any, in A>(private val creator: (A) -> T) {

    private var instance: T? = null

    fun getInstance(argA: A): T =
        instance ?: synchronized(this) {
            instance ?: creator(argA).also {
                instance = it
            }
        }
}

open class SingletonHolderParameterTwo<out T : Any, in A, in B>(private val creator: (A, B) -> T) {

    private var instance: T? = null

    fun getInstance(argA: A, argB: B): T =
        instance ?: synchronized(this) {
            instance ?: creator(argA, argB).also {
                instance = it
            }
        }
}