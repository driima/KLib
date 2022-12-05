package com.driima.game

import com.driima.game.component.*
import com.driima.klib.Game
import com.driima.klib.engine.Component
import com.driima.klib.engine.Engine
import com.driima.klib.engine.Entity
import com.driima.klib.engine.EntitySystem
import com.driima.klib.util.Vector
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

class PongGame : Game(800, 600, 1) {

    val engine: Engine = Engine()

    var playerScore = 0
    var enemyScore = 0

    init {
        title = "Pong"

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(PositionComponent::class, VelocityComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val position = entity[PositionComponent::class]
                val velocity = entity[VelocityComponent::class]
                position.position += velocity.velocity
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(PositionComponent::class, SizeComponent::class, ColorComponent::class)) {
            override fun drawEntity(entity: Entity, graphics: Graphics2D, time: Long) {
                val position: Vector = entity[PositionComponent::class].position
                val size: Vector = entity[SizeComponent::class].size
                val color: Color = entity[ColorComponent::class].color
                graphics.color = color
                graphics.fillRect(position.x.toInt(), position.y.toInt(), size.x.toInt(), size.y.toInt())
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(PositionComponent::class, VelocityComponent::class, SizeComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val position = entity[PositionComponent::class]
                val velocity = entity[VelocityComponent::class]
                val size = entity[SizeComponent::class].size

                if (position.position.x < 0) {
                    position.position.x = 0.0
                    velocity.velocity.x *= -1
                } else if (position.position.x + size.x > width) {
                    position.position.x = width - size.x
                    velocity.velocity.x *= -1
                }

                if (position.position.y < 0) {
                    position.position.y = 0.0
                    velocity.velocity.y *= -1
                } else if (position.position.y + size.y > height) {
                    position.position.y = height.toDouble() - size.y
                    velocity.velocity.y *= -1
                }
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(PositionComponent::class, VelocityComponent::class, PlayerComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val position = entity[PositionComponent::class]
                val velocity = entity[VelocityComponent::class]

                velocity.velocity.y = (mousePosition.y - 50.0 - position.position.y) / 10.0

            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(PositionComponent::class, VelocityComponent::class, EnemyComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val position = entity[PositionComponent::class]
                val velocity = entity[VelocityComponent::class]

                val ball = engine.getEntitiesWith(BallComponent::class).firstOrNull()

                if (ball != null) {
                    val ballPosition = ball[PositionComponent::class]
                    velocity.velocity.y = (ballPosition.position.y - 50.0 - position.position.y) / 5.0
                }
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(PositionComponent::class, SizeComponent::class).exclude(BallComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val position = entity[PositionComponent::class]
                val size = entity[SizeComponent::class].size

                val ball = engine.getEntitiesWith(BallComponent::class).firstOrNull()

                if (ball != null) {
                    val ballPosition = ball[PositionComponent::class]
                    val ballSize = ball[SizeComponent::class].size

                    if (ballPosition.position.x + ballSize.x > position.position.x && ballPosition.position.x < position.position.x + size.x) {
                        if (ballPosition.position.y + ballSize.y > position.position.y && ballPosition.position.y < position.position.y + size.y) {
                            ball[VelocityComponent::class].velocity.x *= -1
                            ball[VelocityComponent::class].velocity *= 1.08
                        }
                    }
                }
            }
        })

        engine.addSystem(object : EntitySystem(engine, Component.Family.of(BallComponent::class)) {
            override fun updateEntity(entity: Entity, time: Long) {
                val position = entity[PositionComponent::class]
                val velocity = entity[VelocityComponent::class]
                val size = entity[SizeComponent::class].size

                if (position.position.x + velocity.velocity.x <= 0) {
                    playerScore++
                    position.position.x = width / 2.0 - 10
                    position.position.y = height / 2.0 - 10
                    velocity.velocity.x = -4.0
                    velocity.velocity.y = 4.0
                }


                if (position.position.x + size.x + velocity.velocity.x >= width) {
                    enemyScore++
                    position.position.x = width / 2.0 - 10
                    position.position.y = height / 2.0 - 10
                    velocity.velocity.x = 4.0
                    velocity.velocity.y = 4.0
                }
            }
        })

        var player = Entity()

        player += PlayerComponent()
        player += PositionComponent(Vector(width - 100.0, height / 2 - 50.0))
        player += VelocityComponent()
        player += SizeComponent(Vector(20.0, 100.0))
        player += ColorComponent(Color.BLUE)

        engine.addEntity(player)

        var enemy = Entity()

        enemy += EnemyComponent()
        enemy += PositionComponent(Vector(100.0, height / 2 - 50.0))
        enemy += VelocityComponent()
        enemy += SizeComponent(Vector(20.0, 100.0))
        enemy += ColorComponent(Color.RED)

        engine.addEntity(enemy)

        var ball = Entity()

        ball += BallComponent()
        ball += PositionComponent(Vector(width / 2.0 - 10, height / 2.0 - 10))
        ball += VelocityComponent(Vector(4.0, 4.0))
        ball += SizeComponent(Vector(20.0, 20.0))
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
        val textWidth = fontMetrics.stringWidth(enemyScore.toString())
        val textHeight = fontMetrics.height

        graphics.drawString(enemyScore.toString(), width / 2 - 50 - textWidth / 2, 50 + textHeight / 2)
        graphics.drawString(playerScore.toString(), width / 2 + 50 - textWidth / 2, 50 + textHeight / 2)

        graphics.color = Color.WHITE
        graphics.drawLine(width / 2, 0, width / 2, height)
    }
}