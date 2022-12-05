package com.driima.klib.util

import kotlin.math.min

class Bitset {
    private var bits = longArrayOf()

    constructor()

    constructor(size: Int) {
        checkCapacity(size)
    }

    constructor(from: Bitset) {
        this.bits = LongArray(from.bits.size)
        System.arraycopy(from.bits, 0, this.bits, 0, from.bits.size)
    }

    operator fun get(index: Int): Boolean {
        val word = index ushr 6
        return if (word >= bits.size) false else bits[word] and (1L shl (index and 0x3F)) != 0L
    }

    operator fun set(index: Int, value: Boolean) {
        val word = index ushr 6
        checkCapacity(word)
        if (value) {
            bits[word] = bits[word] or (1L shl (index and 0x3F))
        } else {
            bits[word] = bits[word] and (1L shl (index and 0x3F)).inv()
        }
    }

    fun getAndClear(index: Int): Boolean {
        val word = index ushr 6
        if (word >= bits.size) return false
        val oldBits = bits[word]
        bits[word] = bits[word] and (1L shl (index and 0x3F)).inv()
        return bits[word] != oldBits
    }

    fun getAndSet(index: Int): Boolean {
        val word = index ushr 6
        checkCapacity(word)
        val oldBits = bits[word]
        bits[word] = bits[word] or (1L shl (index and 0x3F))
        return bits[word] == oldBits
    }

    fun flip(index: Int) {
        val word = index ushr 6
        checkCapacity(word)
        bits[word] = bits[word] xor (1L shl (index and 0x3F))
    }

    fun clear() {
        bits = longArrayOf()
    }

    fun clear(index: Int) {
        val word = index ushr 6

        if (word < bits.size) {
            bits[word] = bits[word] and (1L shl (index and 0x3F)).inv()
        }
    }

    fun bitSize(): Int {
        return bits.size shl 6
    }

    fun length(): Int {
        val bits = bits

        for (word in bits.indices.reversed()) {
            val bitsAtWord = bits[word]
            if (bitsAtWord != 0L) {
                for (bit in 63 downTo 0) {
                    if (bitsAtWord and (1L shl (bit and 0x3F)) != 0L) {
                        return (word shl 6) + bit + 1
                    }
                }
            }
        }

        return 0
    }

    fun notEmpty(): Boolean {
        return !isEmpty()
    }

    fun isEmpty(): Boolean {
        val bits = bits
        val length = bits.size

        for (i in 0 until length) {
            if (bits[i] != 0L) {
                return false
            }
        }

        return true
    }

    fun nextSetBit(fromIndex: Int): Int {
        val bits = bits
        var word = fromIndex ushr 6
        val bitsLength = bits.size

        if (word >= bitsLength) return -1

        var bitsAtWord = bits[word]

        if (bitsAtWord != 0L) {
            for (i in (fromIndex and 0x3f)..63) {
                if (bitsAtWord and (1L shl (i and 0x3F)) != 0L) {
                    return (word shl 6) + i
                }
            }
        }

        word++

        while (word < bitsLength) {
            if (word != 0) {
                bitsAtWord = bits[word]
                if (bitsAtWord != 0L) {
                    for (i in 0..63) {
                        if (bitsAtWord and (1L shl (i and 0x3F)) != 0L) {
                            return (word shl 6) + i
                        }
                    }
                }
            }
            word++
        }

        return -1
    }

    fun nextClearBit(fromIndex: Int): Int {
        val bits = bits
        var word = fromIndex ushr 6
        val bitsLength = bits.size

        if (word >= bitsLength) return bits.size shl 6

        var bitsAtWord = bits[word]

        for (i in (fromIndex and 0x3f)..63) {
            if (bitsAtWord and (1L shl (i and 0x3F)) == 0L) {
                return (word shl 6) + i
            }
        }

        word++
        while (word < bitsLength) {
            if (word == 0) {
                return word shl 6
            }
            bitsAtWord = bits[word]
            for (i in 0..63) {
                if (bitsAtWord and (1L shl (i and 0x3F)) == 0L) {
                    return (word shl 6) + i
                }
            }
            word++
        }

        return bits.size shl 6
    }

    infix fun and(other: Bitset) {
        val commonWords: Int = min(bits.size, other.bits.size)
        var i = 0

        while (commonWords > i) {
            bits[i] = bits[i] and other.bits[i]
            i++
        }

        if (bits.size > commonWords) {
            var i = commonWords
            val s: Int = bits.size
            while (s > i) {
                bits[i] = 0L
                i++
            }
        }
    }

    infix fun andNot(other: Bitset) {
        var i = 0
        val j: Int = bits.size
        val k: Int = other.bits.size

        while (i < j && i < k) {
            bits[i] = bits[i] and other.bits[i].inv()
            i++
        }
    }

    infix fun or(other: Bitset) {
        return bitwiseOperateOn(other) { a, b -> a or b }
    }

    infix fun xor(other: Bitset) {
        return bitwiseOperateOn(other) { a, b -> a xor b }
    }

    infix fun intersects(other: Bitset): Boolean {
        val bits = bits
        val otherBits: LongArray = other.bits

        for (i in min(bits.size, otherBits.size) - 1 downTo 0) {
            if (bits[i] and otherBits[i] != 0L) {
                return true
            }
        }

        return false
    }

    fun containsAll(other: Bitset): Boolean {
        val bits = bits
        val otherBits: LongArray = other.bits
        val otherBitsLength = otherBits.size
        val bitsLength = bits.size

        for (i in bitsLength until otherBitsLength) {
            if (otherBits[i] != 0L) {
                return false
            }
        }

        for (i in Math.min(bitsLength, otherBitsLength) - 1 downTo 0) {
            if (bits[i] and otherBits[i] != otherBits[i]) {
                return false
            }
        }

        return true
    }

    fun containsAny(other: Bitset): Boolean {
        val bits = bits
        val otherBits: LongArray = other.bits
        val otherBitsLength = otherBits.size
        val bitsLength = bits.size

        for (i in bitsLength until otherBitsLength) {
            if (otherBits[i] != 0L) {
                return true
            }
        }

        for (i in min(bitsLength, otherBitsLength) - 1 downTo 0) {
            if (bits[i] and otherBits[i] != 0L) {
                return true
            }
        }

        return false
    }

    private fun bitwiseOperateOn(other: Bitset, function: (Long, Long) -> Long) {
        val commonWords: Int = min(bits.size, other.bits.size)
        var i = 0

        while (commonWords > i) {
            bits[i] = function(bits[i], other.bits[i])
            i++
        }

        if (commonWords < other.bits.size) {
            checkCapacity(other.bits.size)
            var i = commonWords
            val s: Int = other.bits.size
            while (s > i) {
                bits[i] = other.bits[i]
                i++
            }
        }
    }


    private fun checkCapacity(size: Int) {
        if (size >= bits.size) {
            val newBits = LongArray(size + 1)
            System.arraycopy(bits, 0, newBits, 0, bits.size)
            bits = newBits
        }
    }
}