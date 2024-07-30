package com.dingo.module.oss.service

import com.dingo.common.interfaces.service.IBusinessService
import com.dingo.module.oss.entity.OssRefEntity
import com.dingo.module.oss.entity.OssRefTable
import com.dingo.module.oss.entity.OssRefTable.list
import com.dingo.module.oss.model.OssRefModel
import com.dingo.module.oss.model.toModel
import org.jetbrains.exposed.sql.selectAll

interface IOssRefService : IBusinessService {

    fun listOss(businessId: Long): List<OssRefEntity> = OssRefTable.selectAll()
        .where { OssRefTable.businessId eq businessId }
        .where { OssRefTable.businessType eq businessType.name }
        .list()

    fun listOssVo(businessId: Long):List<OssRefModel> = listOss(businessId).map{ it.toModel()}
}