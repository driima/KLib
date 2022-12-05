package com.driima.klib.engine

import com.driima.klib.util.Bitset
import kotlin.reflect.KClass

class Entity {
    private val components = mutableMapOf<Int, Component>()
    val componentBits = Bitset()

    operator fun plus(component: Component): Entity {
        components[Component.Type.indexOf(component::class)] = component
        componentBits[Component.Type.indexOf(component::class)] = true
        return this
    }

    operator fun minus(component: Component): Entity {
        components.remove(Component.Type.indexOf(component::class))
        componentBits[Component.Type.indexOf(component::class)] = false
        return this
    }

    operator fun minus(componentClass: KClass<out Component>): Entity {
        components.remove(Component.Type.indexOf(componentClass))
        componentBits[Component.Type.indexOf(componentClass)] = false
        return this
    }

    operator fun <T : Component> get(componentClass: KClass<T>): T {
        return components[Component.Type.indexOf(componentClass)] as T
    }

    operator fun <T : Component?> get(componentType: Component.Type): T? {
        val componentTypeIndex: Int = componentType.index
        return if (componentTypeIndex < components.size) {
            components[componentType.index] as T?
        } else {
            null
        }
    }

    fun has(componentClass: KClass<out Component>): Boolean {
        return components.containsKey(Component.Type.indexOf(componentClass))
    }

    fun has(componentType: Component.Type): Boolean {
        return componentBits[componentType.index]
    }

    fun has(vararg componentClasses: KClass<out Component>): Boolean {
        val typesLength = componentClasses.size

        for (i in 0 until typesLength) {
            if (!has(componentClasses[i])) {
                return false
            }
        }

        return true
    }
}