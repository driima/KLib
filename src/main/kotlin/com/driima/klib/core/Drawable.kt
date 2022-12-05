package com.driima.klib.core

import java.awt.Graphics2D

interface Drawable {
    fun draw(graphics: Graphics2D, time: Long)
}