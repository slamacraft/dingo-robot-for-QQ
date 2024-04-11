package com.dingo.module.base

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

open interface TypeReference {
    fun findSuperclassTypeArgument(cls: Class<*>): Type {
        val genericSuperclass = cls.genericSuperclass

        if (genericSuperclass is Class<*>) {
            if (genericSuperclass != TypeReference::class.java) {
                // Try to climb up the hierarchy until meet something useful.
                return findSuperclassTypeArgument(genericSuperclass.superclass)
            } else {
                throw IllegalStateException("Could not find the referenced type of class $javaClass")
            }
        }

        return (genericSuperclass as ParameterizedType).actualTypeArguments[0]
    }

    fun findSuperclassTypeArgument(cls: KClass<*>): KType {
        val supertype = cls.supertypes.first { !it.jvmErasure.java.isInterface }

        if (supertype.arguments.isEmpty()) {
            if (supertype.jvmErasure != TypeReference::class) {
                // Try to climb up the hierarchy until meet something useful.
                return findSuperclassTypeArgument(supertype.jvmErasure)
            } else {
                throw IllegalStateException("Could not find the referenced type of class $javaClass")
            }
        }

        return supertype.arguments[0].type!!
    }
}