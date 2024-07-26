package com.dingo.module.oss.entity

import com.dingo.module.base.BaseEntity
import com.dingo.module.base.BaseTable
import com.dingo.module.base.Entity

interface OssRefEntity : Entity<OssRefEntity>, BaseEntity {
    companion object : Entity.Factory<OssRefEntity>()
    var ossId: Long // 文件名称
    var businessId: Long // 文件后缀
    var businessType: String    // 业务类型
}

object OssRefTable : BaseTable<OssRefEntity>("bot_oss_ref") {
    val ossId = long("oss_id")
    val businessId = long("business_id")
    val businessType = varchar("business_type", 4)
}