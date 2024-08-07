package com.dingo.module.service


import com.dingo.module.entity.oss.OssRefTable.id
import com.dingo.module.entity.punch.PunchEntity
import com.dingo.module.entity.punch.PunchTable
import com.google.protobuf.ServiceException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class PunchService {

    @Transactional
    open fun add(userId: Long, type: String) {
        PunchTable.insert(PunchEntity {
            this.userId = userId
            this.type = type
            this.complete = false
        })
    }

    open fun punch(userId: Long, ossId: Long) {
        val entity = getLastPunch(userId)

    }

    @Transactional
    open fun complete(userId: Long) {
        val entity = getLastPunch(userId)
        PunchTable.update({ id eq entity.id }) {
            it[complete] = true
        }
    }

    open fun getLastPunch(userId: Long) = PunchTable.selectAll()
        .where {
            PunchTable.userId eq userId and
                    PunchTable.complete.eq(false)
        }.firstOrNull()?.let {
            PunchTable.buildEntity(it)
        } ?: throw ServiceException("请先开始打卡")

}