package com.contact_manager

import android.content.Context
import android.support.v4.content.ContextCompat

/**
 * Created by Mayur on 10/10/17.
 */

inline fun <T, R : Any> Iterable<T>.firstResult(predicate: (T) -> R?): R? {
    this.mapNotNull { predicate(it) }
            .forEach { return it }
    return null
}

fun Context.color(res: Int): Int = ContextCompat.getColor(this, res)

inline fun <T> MutableList<T>.mapInPlace(mutator: (T) -> T) {
    val iterate = this.listIterator()
    while (iterate.hasNext()) {
        val oldValue = iterate.next()
        val newValue = mutator(oldValue)
        if (newValue !== oldValue) {
            iterate.set(newValue)
        }
    }
}


inline fun <T> MutableList<T>.mapAtPlace(mutator: (T) -> T): List<T> {
    val iterate = this.listIterator()
    while (iterate.hasNext()) {
        val oldValue = iterate.next()
        val newValue = mutator(oldValue)
        if (newValue !== oldValue) {
            iterate.set(newValue)
        }
    }

    return this.toList()
}


inline fun <T> MutableList<T>.mapInPlace(mutator: (T) -> Boolean, newValue: T, block: (Int) -> Unit = {}) {
    val iterate = this.listIterator()
    var i = 0
    var finalPos: Int? = null
    while (iterate.hasNext()) {
        if (mutator(iterate.next())) {
            iterate.set(newValue)
            finalPos = i
        }
        i++
    }

    if (finalPos != null) {
        block(finalPos)
    }

}

inline fun <T> MutableList<T>.rmIf(mutator: (T) -> Boolean, block: (Int) -> Unit = {}) {
    val iterate = this.listIterator()
    var i = 0
    var finalPos: Int? = null
    while (iterate.hasNext()) {
        if (mutator(iterate.next())) {
            iterate.remove()
            finalPos = i
        }
        i++
    }

    if (finalPos != null) {
        block(finalPos)
    }

}

public fun <T> Iterable<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this.toList())
}

/*fun Spinner.setSelected(value: Any) {
    this.setSelection((this.adapter as ArrayAdapter<Any>).getPosition(value))
}*/

fun IntArray.containsOnly(num: Int): Boolean = filter { it == num }.isNotEmpty()
/*
fun Spinner.getItem(value: Any):Any {
    (this.adapter as ArrayAdapter<Any>).getPosition(value)
}*/

fun String.asStringNumbers(): String {
    return this.replace("[a-zA-Z]".toRegex(), "").replace("\\.".toRegex(), "").replace("\\/".toRegex(), "").replace("\\-".toRegex(), "").replace("\\;".toRegex(), "").replace("\\,".toRegex(), "").replace("\\)".toRegex(), "").replace("\\(".toRegex(), "").replace("\\s+".toRegex(), "")
}
