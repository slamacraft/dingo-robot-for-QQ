package com.dingo.module.entity.oss

import com.dingo.module.base.BaseEntity
import com.dingo.module.base.BaseTable
import com.dingo.module.base.Entity

interface OssRefEntity : Entity<OssRefEntity>, BaseEntity {
    companion object : Entity.Factory<OssRefEntity>()

    var ossId: Long // 文件名称
    var name: String // 文件名称
    var size: Long   // 文件大小
    var url: String  // 文件路径
    var businessId: Long // 文件后缀
    var businessType: String    // 业务类型
}

object OssRefTable : BaseTable<OssRefEntity>("bot_oss_ref") {
    val ossId = long("oss_id")
    val name = varchar("name", 128)
    val size = long("size")
    val oss = varchar("url", 512)
    val businessId = long("business_id")
    val businessType = varchar("business_type", 4)
}