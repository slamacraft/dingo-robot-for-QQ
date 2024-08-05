package com.dingo.module.oss.model

import com.dingo.module.oss.entity.OssRefEntity

class OssRefVo {
    var id: Long = 0
    var businessId: Long = 0
    var ossId: Long = 0
    lateinit var name: String
    lateinit var ossUrl: String
    var size: Long = 0

    companion object {
        operator fun invoke(initFun: OssRefVo.() -> Unit): OssRefVo {
            return OssRefVo().apply(initFun)
        }
    }
}

fun OssRefEntity.toModel(): OssRefVo {
    val entity = this
    return OssRefVo {
        id = entity.id
        businessId = entity.businessId
        ossId = entity.ossId
        name = entity.name
        ossUrl = entity.url
        size = entity.size
    }
}