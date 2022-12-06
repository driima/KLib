package com.driima.klib.util

class Rectangle(var center: Vector, var size: Vector) {
    var left: Double
        get() = center.x - size.x / 2
        set (value) {
            center.x = value + size.x / 2
        }

    var right: Double
        get() = center.x + size.x / 2
        set (value) {
            center.x = value - size.x / 2
        }

    var top: Double
        get() = center.y - size.y / 2
        set (value) {
            center.y = value + size.y / 2
        }

    var bottom: Double
        get() = center.y + size.y / 2
        set (value) {
            center.y = value - size.y / 2
        }

    var x: Double
        get() = center.x
        set(value) {
            center.x = value
        }

    var y: Double
        get() = center.y
        set(value) {
            center.y = value
        }

    var width: Double
        get() = size.x
        set(value) {
            size.x = value
        }

    var height: Double
        get() = size.y
        set(value) {
            size.y = value
        }

    fun limit(min: Vector, max: Vector): Boolean {
        val xChanged = limitX(min.x, max.x)
        val yChanged = limitY(min.y, max.y)

        return xChanged || yChanged
    }

    fun limitX(min: Double, max: Double): Boolean {
        var changed = false

        if (left < min) {
            left = min
            changed = true
        }

        if (right > max) {
            right = max
            changed = true
        }

        return changed
    }

    fun limitY(min: Double, max: Double): Boolean {
        var changed = false

        if (top < min) {
            top = min
            changed = true
        }

        if (bottom > max) {
            bottom = max
            changed = true
        }

        return changed
    }

    infix fun intersects(other: Rectangle): Boolean {
        return left < other.right && right > other.left && top < other.bottom && bottom > other.top
    }

    infix fun contains(other: Rectangle): Boolean {
        return left <= other.left && right >= other.right && top <= other.top && bottom >= other.bottom
    }

    infix fun contains(point: Vector): Boolean {
        return point.x in left..right && top <= point.y && bottom >= point.y
    }

    fun clone(): Rectangle {
        return Rectangle(center.clone(), size.clone())
    }
}