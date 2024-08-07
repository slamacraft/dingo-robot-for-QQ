package com.dingo.module.base

import com.dingo.module.entity.oss.OssTable.id
import com.dingo.common.util.underlineToCamelCase
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager


open class EntityInsertStatement(
    table: LongIdTable,
    isIgnore: Boolean = false
) : InsertStatement<Int>(table, isIgnore) {

    /**
     * 在新增时执行的操作，也就是把id赋值给Entity，以及默认值
     */
    fun <E : Entity<E>> insert(entity: E): E {
        table.columns.forEach {
            it.setValue(values, entity, table)
        }
        execute(TransactionManager.current())
        entity.toSet("id", get(id).value)
        return entity
    }

}

fun <E : Entity<E>> Column<*>.setValue(values: MutableMap<Column<*>, Any?>, entity: E, table: Table) {
    val value = entity.toGet(name.underlineToCamelCase())
    if (value != null) {
        values[this] = value
    } else if (name != "id") {
        val defaultValue = defaultValueFun?.invoke()
        // 校验字段可空性，如果没有默认值，且字段为不可空，抛出异常
        require(defaultValue != null || columnType.nullable) {
            "${table.tableName}字段${name}不能为空"
        }
        values[this] = defaultValue
        entity.toSet(name.underlineToCamelCase(), defaultValue)
    }
}