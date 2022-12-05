package com.driima.klib.engine

import com.driima.klib.util.Bitset
import kotlin.reflect.KClass

interface Component {

    class Type {

        val index = typeIndex++

        companion object {
            private val assignedComponentTypes: Map<KClass<out Component>, Type> = mutableMapOf()
            private var typeIndex = 0

            fun from(componentClass: KClass<out Component>): Type {
                var type = assignedComponentTypes[componentClass]

                if (type == null) {
                    type = Type()
                    (assignedComponentTypes as MutableMap<KClass<out Component>, Type>)[componentClass] = type
                }

                return type
            }

            fun indexOf(componentClass: KClass<out Component>): Int {
                return from(componentClass).index
            }

            fun getBitsFor(vararg componentTypes: KClass<out Component>?): Bitset {
                val bits = Bitset()
                val typesLength = componentTypes.size

                for (i in 0 until typesLength) {
                    bits[indexOf(componentTypes[i]!!)] = true
                }

                return bits
            }
        }
    }

    class Family {
        companion object {
            fun of(vararg componentTypes: KClass<out Component>?): Family {
                return Family(Type.getBitsFor(*componentTypes))
            }

            fun empty(): Family {
                return Family(Bitset())
            }
        }

        private val requiredBits: Bitset
        private var excludedBits: Bitset

        constructor(requiredBits: Bitset, excludedBits: Bitset) {
            this.requiredBits = requiredBits
            this.excludedBits = excludedBits
        }

        constructor(requiredBits: Bitset) {
            this.requiredBits = requiredBits
            this.excludedBits = Bitset()
        }

        fun exclude(vararg componentTypes: KClass<out Component>?): Family {
            val bits = Type.getBitsFor(*componentTypes)
            excludedBits = bits
            return this
        }

        fun matches(entity: Entity): Boolean {
            return entity.componentBits.containsAll(requiredBits) && !entity.componentBits.containsAny(excludedBits)
        }
    }

    class Mapper<T : Component> private constructor(componentClass: KClass<T>) {
        private val componentType: Type

        operator fun get(entity: Entity): T? {
            return entity[componentType]
        }

        fun has(entity: Entity): Boolean {
            return entity.has(componentType)
        }

        init {
            componentType = Type.from(componentClass)
        }

        companion object {
            fun <T : Component> getFor(componentClass: KClass<T>): Mapper<T> {
                return Mapper(componentClass)
            }
        }
    }
}