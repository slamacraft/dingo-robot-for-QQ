package com.dingo.module.base

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaSetter
import kotlin.reflect.jvm.jvmErasure

open interface Entity<out E : Entity<E>> {

    fun toGet(name: String): Any?

    fun toSet(name: String, value: Any?)

    companion object {

        fun create(entityClass: KClass<*>): Entity<*> {
            if (!entityClass.isSubclassOf(Entity::class)) {
                throw IllegalArgumentException("实体类必须是Entity类型")
            }
            val handler = EntityImpl(entityClass)
            return Proxy.newProxyInstance(entityClass.java.classLoader, arrayOf(entityClass.java), handler) as Entity<*>
        }

    }

    open abstract class Factory<E : Entity<E>> : TypeReference {
        private val referencedKotlinType: KType by lazy { findSuperclassTypeArgument(javaClass.kotlin) }
        private fun createType(): E = create(referencedKotlinType.jvmErasure) as E

        operator fun invoke(): E {
            return createType()
        }

        operator fun invoke(initFun: E.() -> Unit): E {
            return invoke().apply(initFun)
        }
    }
}

open interface BaseEntity {
    var id: Long
    var createBy: Long
    var createTime: LocalDateTime
    var updateBy: Long
    var updateTime: LocalDateTime
}

class EntityImpl(
    private val entityClass:KClass<*>
) : InvocationHandler {
    private val valueMap = mutableMapOf<String, Any?>()

    override fun invoke(target: Any, method: Method, args: Array<out Any>?): Any? {
        return when (method.declaringClass.kotlin) {
            Any::class -> when (method.name) {
                "toString" -> toString()
                else -> throw IllegalArgumentException("不支持代理的方法${method.name}")
            }

            else -> when (method.name) {
                "toGet" -> toGet(args!![0] as String)
                "toSet" -> toSet(args!![0] as String, args[1]!!)
                else -> methodInvoke(method, args)
            }
        }
    }

    private fun methodInvoke(method: Method, args: Array<out Any>?): Any? {
        val (prop, isGetter) = method.kotlinProperty
            ?: throw IllegalStateException("bindTo只能绑定Entity属性")
        if (isGetter) {
            return valueMap[prop.name]
        } else {
            valueMap[prop.name] = args?.get(0)
        }
        return null
    }

    fun toGet(name: String): Any? = valueMap[name]

    fun toSet(name: String, value: Any?) {
        valueMap[name] = value
    }


    /**
     * Return the corresponding Kotlin property of this method if exists and a flag indicates whether
     * it's a getter (true) or setter (false).
     */
    private val Method.kotlinProperty: Pair<KProperty1<*, *>, Boolean>?
        get() {
            for (prop in declaringClass.kotlin.declaredMemberProperties) {
                if (prop.javaGetter == this) {
                    return Pair(prop, true)
                }
                if (prop is KMutableProperty<*> && prop.javaSetter == this) {
                    return Pair(prop, false)
                }
            }
            return null
        }

    override fun toString(): String {
        return buildString {
            append(entityClass.simpleName).append("(")

            var i = 0
            for ((name, value) in valueMap) {
                if (i++ > 0) {
                    append(", ")
                }

                append(name).append("=").append(value)
            }

            append(")")
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}