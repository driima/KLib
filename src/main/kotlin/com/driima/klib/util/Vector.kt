package com.driima.klib.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector(var x: Double = 0.0, var y: Double = 0.0) {

    operator fun plus(other: Vector): Vector {
        return Vector(x + other.x, y + other.y)
    }

    operator fun minus(other: Vector): Vector {
        return Vector(x - other.x, y - other.y)
    }

    operator fun times(other: Vector): Vector {
        return Vector(x * other.x, y * other.y)
    }

    operator fun times(scalar: Double): Vector {
        return Vector(x * scalar, y * scalar)
    }

    operator fun timesAssign(other: Vector) {
        x *= other.x
        y *= other.y
    }

    operator fun div(other: Vector): Vector {
        return Vector(x / other.x, y / other.y)
    }

    operator fun div(scalar: Double): Vector {
        return Vector(x / scalar, y / scalar)
    }

    operator fun divAssign(other: Vector) {
        x /= other.x
        y /= other.y
    }

    fun length(): Double {
        return sqrt(x * x + y * y)
    }

    fun normalize(): Vector {
        val length = length()
        return Vector(x / length, y / length)
    }

    fun distance(other: Vector): Double {
        return sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))
    }

    fun distanceSquared(other: Vector): Double {
        return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)
    }

    fun angle(): Double {
        return Math.toDegrees(atan2(y, x))
    }

    fun angle(other: Vector): Double {
        return Math.toDegrees(atan2(other.y - y, other.x - x))
    }

    fun rotate(angle: Double): Vector {
        val radians = Math.toRadians(angle)
        val cos = cos(radians)
        val sin = sin(radians)
        return Vector(x * cos - y * sin, x * sin + y * cos)
    }

    fun rotateAround(angle: Double, point: Vector): Vector {
        val radians = Math.toRadians(angle)
        val cos = cos(radians)
        val sin = sin(radians)
        return Vector((x - point.x) * cos - (y - point.y) * sin + point.x, (x - point.x) * sin + (y - point.y) * cos + point.y)
    }

    fun dot(other: Vector): Double {
        return x * other.x + y * other.y
    }

    fun cross(other: Vector): Double {
        return x * other.y - y * other.x
    }

    fun lerp(other: Vector, alpha: Double): Vector {
        return Vector(x + (other.x - x) * alpha, y + (other.y - y) * alpha)
    }

    fun clone(): Vector {
        return Vector(x, y)
    }
}