package com.driima.klib.engine

import com.driima.klib.core.Renderable
import java.awt.Graphics2D
import kotlin.reflect.KClass

open class Engine : Renderable {

    internal val entities = mutableListOf<Entity>()
    internal val systems = mutableListOf<EntitySystem>()

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun addSystem(system: EntitySystem) {
        systems.add(system)
    }

    override fun update(time: Long) {
        systems.forEach { it.update(time) }
    }

    override fun draw(graphics: Graphics2D, time: Long) {
        systems.forEach { it.draw(graphics, time) }
    }

    fun getEntitiesWith(vararg components: KClass<out Component>): List<Entity> {
        return entities.filter { it.has(*components) }
    }
}