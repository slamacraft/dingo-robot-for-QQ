package com.dingo.module.oss.model

import com.dingo.module.oss.entity.OssRefEntity

class OssRefModel {
    var id: Long = 0
    var businessId: Long = 0
    var ossId: Long = 0
    lateinit var name: String
    lateinit var ossUrl: String
    var size: Long = 0

    companion object {
        operator fun invoke(initFun: OssRefModel.() -> Unit): OssRefModel {
            return OssRefModel().apply(initFun)
        }
    }
}

fun OssRefEntity.toModel(): OssRefModel {
    val entity = this
    return OssRefModel {
        id = entity.id
        businessId = entity.businessId
        ossId = entity.ossId
        name = entity.name
        ossUrl = entity.ossUrl
        size = entity.size
    }
}