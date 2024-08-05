@file:Suppress("UNUSED_EXPRESSION")

package com.dingo.module.base

import com.dingo.common.util.underlineToCamelCase
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import java.time.LocalDateTime
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure


/**
 * 对exposed的Table进行扩展，假如一些类似于mybatis-plus的默认方法
 */
open abstract class Table<E : Entity<E>>(tableName: String) : LongIdTable(tableName), TypeReference {
    private val referencedKotlinType: KType by lazy { findSuperclassTypeArgument(javaClass.kotlin) }

    fun FieldSet.getById(pid: Long): E? = selectAll()
        .where { id eq pid }
        .firstOrNull()?.let {
            buildEntity(it)
        }

    fun buildEntity(resultRow: ResultRow): E {
        val entity = createEntity()
        columns.forEach { column ->
            val value = resultRow[column]
            val fieldName = column.name.underlineToCamelCase()
            if (value is EntityID<*>) {
                entity.toSet(fieldName, value.value)
            } else {
                entity.toSet(fieldName, value)
            }
        }
        return entity
    }

    open fun insert(entity: E): E = EntityInsertStatement(this, false).insert(entity)

    open fun batchInsert(entityList : List<E>){
        batchInsert(entityList){ entity->
            val row = this
            columns.forEach { column->
                entity.toGet(column.name.underlineToCamelCase())?.let {
                    row[column] = it
                }
            }
        }
    }

    open operator fun plusAssign(entity: E) {
        insert(entity)
    }

    open operator fun plusAssign(entity: Collection<E>) {
        EntityInsertStatement(this, false)
    }

    private fun createEntity(): E = Entity.create(referencedKotlinType.jvmErasure) as E

    private operator fun <T> BatchInsertStatement.set(it: Column<*>, value: T) {
        this[it] = value
    }
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