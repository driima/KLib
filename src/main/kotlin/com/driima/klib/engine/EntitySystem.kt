package com.driima.klib.engine

import com.driima.klib.core.Renderable
import java.awt.Graphics2D

abstract class EntitySystem(private val engine: Engine, private val family: Component.Family = Component.Family.empty()) : Renderable {

    private var entities = engine.entities.filter { family.matches(it) }

    override fun update(time: Long) {
        entities = engine.entities.filter { family.matches(it) }
        entities.forEach { updateEntity(it, time) }
    }

    override fun draw(graphics: Graphics2D, time: Long) {
        entities.forEach { drawEntity(it, graphics, time) }
    }

    open fun updateEntity(entity: Entity, time: Long) {}
    open fun drawEntity(entity: Entity, graphics: Graphics2D, time: Long) {}
}