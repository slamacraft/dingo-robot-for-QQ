package com.dingo.module.base

import com.dingo.common.util.underlineToCamelCase
import com.dingo.module.oss.entity.OssEntity.Companion.findSuperclassTypeArgument
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
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

open interface BaseEntity {
    var id: Long
    var createBy: Long
    var createTime: LocalDateTime
    var updateBy: Long
    var updateTime: LocalDateTime
}


open interface Entity<out E : Entity<E>> {

    /**
     * 获取属性的方法，给代理对象使用的
     */
    fun toGet(name: String): Any?

    /**
     * 设置属性的方法，也是给代理对象使用的
     */
    fun toSet(name: String, value: Any?)


    companion object {
        /**
         * 为接口创建动态代理，其实现为[EntityImpl]
         */
        fun create(entityClass: KClass<*>): Entity<*> {
            if (!entityClass.isSubclassOf(Entity::class)) {
                throw IllegalArgumentException("实体类必须是Entity类型")
            }
            val handler = EntityImpl(entityClass)
            return Proxy.newProxyInstance(entityClass.java.classLoader, arrayOf(entityClass.java), handler) as Entity<*>
        }
    }

    /**
     * 创建接口代理实例的工厂方法，通过这个就可以实现像创建类的实例一样，创建[Entity]接口的实例
     */
    open abstract class Factory<E : Entity<E>> : TypeReference {
        private val referencedKotlinType: KType by lazy { findSuperclassTypeArgument(javaClass.kotlin) }
        private fun createType(): E = create(referencedKotlinType.jvmErasure) as E

        operator fun invoke(): E {
            return createType()
        }

        /**
         * 初始化方法，通过这个可以做到在创建代理对象后，给对象的属性设置一些值
         */
        operator fun invoke(initFun: E.() -> Unit): E {
            return invoke().apply(initFun)
        }
    }
}

/**
 * [Entity]接口的代理类
 *
 * 这个接口会也实现了equals和hashCode，并且会对代理对象的每个属性值进行比较
 */
class EntityImpl(
    private val entityClass: KClass<*>
) : InvocationHandler {
    /**
     * 这个map用来记录代理对象的属性值
     */
    private val valueMap = mutableMapOf<String, Any?>()

    /**
     * 当对被代理对象调用各种方法时，实际上是在执行本方法
     *
     * 本方法通过方法名称，来执行不同的代理方法
     */
    override fun invoke(target: Any, method: Method, args: Array<out Any>?): Any? {
        return when (method.declaringClass.kotlin) {
            // Any对象的方法
            Any::class -> when (method.name) {
                "toString" -> toString()
                "hashCode" -> hashCode()
                "equals" -> equals(args!![0])
                else -> throw IllegalArgumentException("不支持代理的方法${method.name}")
            }
            // getter,setter方法
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

    private fun toGet(name: String): Any? = valueMap[name]

    private fun toSet(name: String, value: Any?) {
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
        val fields = valueMap.map { "${it.key}=${it.value}" }
            .joinToString()
        return "${entityClass.simpleName}(${fields})"
    }

    /**
     * 将所有的key和value的hash值进行异或
     */
    override fun hashCode(): Int = valueMap.map {
        it.key.hashCode() xor it.value.hashCode()
    }.fold(0) { acc, i ->
        acc xor i
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EntityImpl) {
            return false
        } else if (other.valueMap.size != this.valueMap.size) {
            return false
        }
        for (key in this.valueMap.keys) {
            if (other.valueMap[key] != this.valueMap[key]) {
                return false
            }
        }
        return true
    }
}