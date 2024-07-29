package com.dingo.module.base

import com.dingo.module.oss.entity.OssTable.id
import com.dingo.common.util.underlineToCamelCase
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
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
            val value = entity.toGet(it.name.underlineToCamelCase())
            if (value != null) {
                values[it] = value
            } else if (it.name != "id") {
                val defaultValue = it.defaultValueFun?.invoke()
                values[it] = defaultValue
                entity.toSet(it.name.underlineToCamelCase(), defaultValue)
            }
        }
        execute(TransactionManager.current())
        entity.toSet("id", get(id).value)
        return entity
    }

}