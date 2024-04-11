@file:Suppress("UNUSED_EXPRESSION")

package com.dingo.module.base

import com.dingo.common.util.underlineToCamelCase
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.FieldSet
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure


open abstract class Table<E : Entity<E>>(tableName: String) : LongIdTable(tableName), TypeReference {
    private val referencedKotlinType: KType by lazy { findSuperclassTypeArgument(javaClass.kotlin) }

    fun FieldSet.getById(pid: Long): E? = selectAll()
        .where { id eq pid }
        .one()

    fun Query.one(): E? = firstOrNull()?.let {
        mapResultToEntity(it)
    }

    private fun mapResultToEntity(it: ResultRow): E {
        val entity = createEntity()
        columns.forEach { column ->
            val value = it[column]
            val fieldName = column.name.underlineToCamelCase()
            if (value is EntityID<*>) {
                entity.toSet(fieldName, value.value)
            } else {
                entity.toSet(fieldName, value)
            }
        }
        return entity
    }

    fun Query.list(): List<E> = map { mapResultToEntity(it) }

    open fun insert(entity: E): E = EntityInsertStatement(this, false).insert(entity)

    private fun createEntity(): E = Entity.create(referencedKotlinType.jvmErasure) as E
}

open abstract class BaseTable<E>(tableName: String) : Table<E>(tableName)
        where E : BaseEntity, E : Entity<E> {

    val createBy = long("create_by")
        .default(1114951452)

    val createTime = datetime("create_time")
        .default(LocalDateTime.now())

    val updateBy = long("update_by")
        .default(1114951452)

    val updateTime = datetime("update_time")
        .default(LocalDateTime.now())
}