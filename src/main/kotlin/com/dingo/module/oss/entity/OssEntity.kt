package com.dingo.module.oss.entity

import com.dingo.module.base.BaseEntity
import com.dingo.module.base.BaseTable
import com.dingo.module.base.Entity

interface OssEntity : Entity<OssEntity>, BaseEntity {
    companion object : Entity.Factory<OssEntity>()

    var name: String // 文件名称
    var bucketName: String // 桶名称
    var size: Long // 文件后缀
    var url: String // 文件公开地址（不一定公开）
    var delFlag: Boolean // 是否删除
}


object OssTable : BaseTable<OssEntity>("bot_oss") {
    val name = varchar("name", 128)
    val bucketName = varchar("bucket_name", 128)
    val size = long("size")
    val url = varchar("url", 512)
    val delFlag = bool("del_flag").clientDefault { false }
}