@file:Suppress("UNUSED_EXPRESSION")

package com.dingo.module.base

import com.dingo.common.util.underlineToCamelCase
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
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

    /**
     * 批量新增
     */
    open fun insert(entity: E): E = EntityInsertStatement(this, false).insert(entity)

    /**
     * 批量新增
     */
    open fun batchInsert(entityList: List<E>) {
        batchInsert(entityList) { entity ->
            val row = this
            columns.forEach { column ->
                entity.toGet(column.name.underlineToCamelCase())?.let {
                    row.serValue(column as Column<Any>, it)
                }
            }
        }
    }

    open fun batchUpdateById(entityList: List<E>) {
        entityList.forEach(this::updateById)
    }

    open fun updateById(entity: E) {
        update({ id eq entity.id() }) {
            entity.fields().forEach { (column, value) ->
                it.serValue(column, value)
            }
        }
    }

    open fun removeById(id: Long) {
        deleteWhere { this.id eq id }
    }

    open fun removeByIds(ids: Collection<Long>) {
        if (ids.isEmpty()) {
            return
        }
        deleteWhere { this.id inList ids }
    }

    private fun <E : Entity<E>> E.fields(): Map<Column<*>, Any?> {
        return columns.associateWith { this[it.name.underlineToCamelCase()] }
    }

    private fun <E : Entity<E>> E.id(): Long = this["id"] as Long

    open operator fun plusAssign(entity: E) {
        insert(entity)
    }

    open operator fun plusAssign(entity: Collection<E>) {
        EntityInsertStatement(this, false)
    }

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

/**
 * //////////////// 下面是一些为了抽象exposed接口的拓展函数 ////////////////
 */

/**
 * 设置插入值
 */
private fun <T, E : Any> InsertStatement<E>.serValue(it: Column<*>, value: T?) {
    if (value != null) {
        this[it as Column<T>] = value
    }
}

/**
 * 设置插入值
 */
private fun <T> UpdateStatement.serValue(it: Column<*>, value: T?) {
    if (value != null) {
        this[it as Column<T>] = value
    }
}