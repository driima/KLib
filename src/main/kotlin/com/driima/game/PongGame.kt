package com.driima.game

import com.driima.game.component.*
import com.driima.klib.Game
import com.driima.klib.engine.Component
import com.driima.klib.engine.Engine
import com.driima.klib.engine.Entity
import com.driima.klib.engine.EntitySystem
import com.driima.klib.util.Rectangle
import com.driima.klib.util.Vector
import java.awt.*

class PongGame : Game(800, 600, 1) {

    val engine: Engine = Engine()

    var playerScore = 0
    var enemyScore = 0

    init {
        title = "Pong"

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(BoundsComponent::class, VelocityComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val bounds = entity[BoundsComponent::class].bounds
                val velocity = entity[VelocityComponent::class]
                bounds.center += velocity.velocity

                if (bounds.limitX(0.0, width.toDouble())) {
                    velocity.velocity.x *= -1.0
                }

                if (bounds.limitY(0.0, height.toDouble())) {
                    velocity.velocity.y *= -1.0
                }
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(BoundsComponent::class, ColorComponent::class)) {
            override fun drawEntity(entity: Entity, graphics: Graphics2D, time: Long) {
                val bounds = entity[BoundsComponent::class].bounds
                val color = entity[ColorComponent::class].color
                graphics.color = color
                graphics.fillRect(bounds.left.toInt(), bounds.top.toInt(), bounds.width.toInt(), bounds.height.toInt())
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(PlayerComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val bounds = entity[BoundsComponent::class].bounds
                val velocity = entity[VelocityComponent::class]

                velocity.velocity.y = (mousePosition.y - bounds.y) / 10.0
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(EnemyComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val bounds = entity[BoundsComponent::class].bounds
                val velocity = entity[VelocityComponent::class].velocity

                val ball = engine.getEntitiesWith(BallComponent::class).firstOrNull()

                if (ball != null) {
                    val ballBounds = ball[BoundsComponent::class].bounds

                    val distance = ballBounds.center.distance(bounds.center)
                    val speed = 1.0 + (distance / 10.0)
                    velocity.y = (ballBounds.y - bounds.y) / speed
                }
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(BoundsComponent::class).exclude(BallComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val bounds = entity[BoundsComponent::class].bounds

                val ball = engine.getEntitiesWith(BallComponent::class).firstOrNull()

                if (ball != null) {
                    val ballBounds = ball[BoundsComponent::class].bounds

                    if (ballBounds intersects bounds) {
                        val velocity = ball[VelocityComponent::class]

                        if (entity.has(PlayerComponent::class)) {
                            ballBounds.right = bounds.left - velocity.velocity.x
                        } else {
                            ballBounds.left = bounds.right + velocity.velocity.x
                        }

                        Toolkit.getDefaultToolkit().beep()

                        velocity.velocity.x *= -1
                        velocity.velocity *= 1.04
                        velocity.velocity.y += (Math.random() - 0.5)
                    }
                }
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(BallComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val bounds = entity[BoundsComponent::class].bounds
                val velocity = entity[VelocityComponent::class]

                if (bounds.left + velocity.velocity.x <= 0) {
                    playerScore++
                    bounds.x = width / 2.0
                    bounds.y = height / 2.0
                    velocity.velocity.x = -4.0
                    velocity.velocity.y = if (Math.random() < 0.5) 4.0 else -4.0
                }


                if (bounds.right + velocity.velocity.x >= width) {
                    enemyScore++
                    bounds.x = width / 2.0
                    bounds.y = height / 2.0
                    velocity.velocity.x = 4.0
                    velocity.velocity.y = if (Math.random() < 0.5) 4.0 else -4.0
                }
            }
        })

        var player = Entity()

        player += PlayerComponent()
        player += BoundsComponent(Rectangle(Vector(width - 100.0, height / 2.0), Vector(20.0, 100.0)))
        player += VelocityComponent()
        player += ColorComponent(Color.BLUE)

        engine.addEntity(player)

        var enemy = Entity()

        enemy += EnemyComponent()
        enemy += BoundsComponent(Rectangle(Vector(100.0, height / 2.0), Vector(20.0, 100.0)))
        enemy += VelocityComponent()
        enemy += ColorComponent(Color.RED)

        engine.addEntity(enemy)

        var ball = Entity()

        ball += BallComponent()
        ball += BoundsComponent(Rectangle(Vector(width / 2.0, height / 2.0), Vector(20.0, 20.0)))
        ball += VelocityComponent(Vector(4.0, 4.0))
        ball += ColorComponent(Color.WHITE)

        engine.addEntity(ball)
    }

    override fun update(time: Long) {
        engine.update(time)
    }

    override fun draw(graphics: Graphics2D, time: Long) {
        engine.draw(graphics, time)

        graphics.color = Color.WHITE
        graphics.font = Font("Arial", Font.BOLD, 50)

        val fontMetrics = graphics.fontMetrics
        val playerTextWidth = fontMetrics.stringWidth(playerScore.toString())
        val enemyTextWidth = fontMetrics.stringWidth(enemyScore.toString())
        val textHeight = fontMetrics.height

        graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)

        graphics.drawString(playerScore.toString(), width / 2 + 80 - playerTextWidth / 2, 50 + textHeight / 2)
        graphics.drawString(enemyScore.toString(), width / 2 - 80 - enemyTextWidth / 2, 50 + textHeight / 2)

        graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f)
        graphics.color = Color.WHITE
        graphics.stroke = BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, floatArrayOf(10.0f), 0.0f)
        graphics.drawLine(width / 2, 0, width / 2, height)
        graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
    }
}