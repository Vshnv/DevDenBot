package me.bristermitten.devdenbot.leaderboard

import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.Comparator

open class Leaderboard<T> (private val comparator: Comparator<T>) {

    private val indices = ConcurrentHashMap<T, Int>()
    private val entries = Vector<T>()

    fun addAll(entries: Collection<T>) {
        this.entries.addAll(entries)
        this.entries.sortWith(comparator)
        indices.clear()
        entries.forEachIndexed { i, it -> indices[it] = i }
    }
    fun getEntryCount(): Int = indices.size

    fun getEntry(position: Int): T {
        return entries[position]
    }

    fun getPosition(entry: T): Int? {
        return indices[entry]
    }

    @Synchronized
    private fun add(entry: T) {
        if (!indices.containsKey(entry)) {
            indices[entry] = entries.size
            entries.add(entry)
        }
    }

    @Synchronized
    fun update(entry: T) {
        if (!indices.containsKey(entry)) {
            add(entry)
        }

        var index = getPosition(entry) ?: throw IllegalStateException("User was not added correctly")
        while (index > 0 && comparator.compare(entry, entries[index - 1]) > 0) {
            val tmp = entries[index - 1]
            entries[index - 1] = entry
            entries[index] = tmp
            indices[entry] = index - 1
            indices[tmp] = index
            index--
        }
        while (index < entries.size - 1 && comparator.compare(entry, entries[index + 1]) < 0) {
            val tmp = entries[index + 1]
            entries[index + 1] = entry
            entries[index] = tmp
            indices[entry] = index + 1
            indices[tmp] = index
            index++
        }
    }


}
