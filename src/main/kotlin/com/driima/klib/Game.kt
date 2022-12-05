package com.driima.klib

import com.driima.klib.core.Renderable
import java.awt.*
import java.awt.image.BufferStrategy
import java.awt.image.VolatileImage
import kotlin.math.max

open abstract class Game(internal val width: Int, internal val height: Int, private val scale: Int) : Renderable {
    private val config: GraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration

    private val frame = Frame()
    private val canvas = Canvas(config)
    private val background: VolatileImage = createVolatileImage(width, height, false)

    private var strategy: BufferStrategy? = null
    private var running = true
    private var backgroundGraphics = background.graphics as Graphics2D
    private var graphics: Graphics2D? = null

    internal var mousePosition = Point(0, 0)

    var title: String
        get() = frame.title
        set(value) {
            frame.title = value
        }

    init {
        frame.setSize(800, 600)
        frame.addWindowListener(object : java.awt.event.WindowAdapter() {
            override fun windowClosing(e: java.awt.event.WindowEvent) {
                running = false
            }
        })

        canvas.setSize(width * scale, height * scale)
        frame.add(canvas, 0)
        frame.pack()
        frame.setLocationRelativeTo(null)

        canvas.createBufferStrategy(2)

        do {
            strategy = canvas.bufferStrategy
        } while (strategy == null)


        frame.isVisible = true
    }

    fun start() {
        backgroundGraphics = background.graphics as Graphics2D

        val fpsWait = (1.0 / 60 * 1000).toLong()
        var time: Long = 0

        main@ while (running) {
            val renderStart = System.nanoTime()
            val localPos = canvas.mousePosition

            if (localPos != null) {
                mousePosition = Point(localPos.x / scale, localPos.y / scale)
            }

            update(time)
            do {
                val buffer = getBuffer()

                if (!running) break@main

                backgroundGraphics.color = Color.BLACK
                backgroundGraphics.fillRect(0, 0, width, height)

                draw(backgroundGraphics, time)

                if (scale != 1) {
                    buffer!!.drawImage(background, 0, 0, width * scale, height * scale, 0, 0, width, height, null)
                } else {
                    buffer!!.drawImage(background, 0, 0, null)
                }

                buffer.dispose()
            } while (!updateScreen())

            val renderTime = (System.nanoTime() - renderStart) / 1000000

            try {
                Thread.sleep(max(0, fpsWait - renderTime))
            } catch (e: InterruptedException) {
                Thread.interrupted()
                break
            }

            time += 1
        }

        frame.dispose()
    }

    private fun getBuffer(): Graphics2D? {
        if (graphics == null) {
            graphics = try {
                strategy!!.drawGraphics as Graphics2D
            } catch (e: IllegalStateException) {
                return null
            }
        }
        return graphics
    }

    private fun updateScreen(): Boolean {
        graphics!!.dispose()
        graphics = null
        return try {
            strategy!!.show()
            Toolkit.getDefaultToolkit().sync()
            !strategy!!.contentsLost()
        } catch (e: NullPointerException) {
            true
        } catch (e: IllegalStateException) {
            true
        }
    }

    private fun createVolatileImage(width: Int, height: Int, transparency: Boolean): VolatileImage {
        return if (transparency) {
            config.createCompatibleVolatileImage(width, height)
        } else {
            config.createCompatibleVolatileImage(width, height, Transparency.OPAQUE)
        }
    }
}