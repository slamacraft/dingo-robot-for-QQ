package com.dingo.module.entity.punch

import com.dingo.module.base.BaseEntity
import com.dingo.module.base.BaseTable
import com.dingo.module.base.Entity

interface PunchEntity : Entity<PunchEntity>, BaseEntity {
    companion object : Entity.Factory<PunchEntity>()

    var userId: Long
    var type: String // 打卡类型
    var content: String // 打卡内容（用户自己决定）
    var complete: Boolean   // 是否打卡完成
}

object PunchTable : BaseTable<PunchEntity>("bot_punch") {
    val userId = long("user_id")
    val type = varchar("type", 128)
    val content = text("content")
    val complete = bool("complete")
}