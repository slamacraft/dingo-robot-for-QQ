package com.dingo.module.oss.service

import com.dingo.common.enums.BusinessTypeEnum
import com.dingo.common.interfaces.service.IBusinessService
import com.dingo.module.oss.entity.OssEntity
import com.dingo.module.oss.entity.OssRefEntity
import com.dingo.module.oss.entity.OssRefTable
import com.dingo.module.oss.entity.OssTable
import com.dingo.module.oss.entity.OssTable.getById
import com.dingo.module.oss.model.OssRefVo
import com.dingo.module.oss.model.toModel
import com.google.protobuf.ServiceException
import org.jetbrains.exposed.sql.selectAll

interface IOssRefService : IBusinessService {

    fun listOss(businessId: Long): List<OssRefEntity> = OssRefTable.selectAll()
        .where { OssRefTable.businessId eq businessId }
        .where { OssRefTable.businessType eq businessType().name }
        .map(OssRefTable::buildEntity)

    fun listOssBatch(businessIds: List<Long>): List<OssRefEntity> {
        if (businessIds.isEmpty()) {
            return listOf()
        }

        return OssRefTable.selectAll()
            .where { OssRefTable.businessId inList businessIds }
            .where { OssRefTable.businessType eq businessType().name }
            .map(OssRefTable::buildEntity)
    }

    fun listOssVo(businessId: Long): List<OssRefVo> = listOss(businessId).map { it.toModel() }

    fun addOss(businessId: Long, ossId: Long) {

        val ossEntity = OssTable.getById(ossId)
            ?.toRefEntity(businessId, businessType())
            ?.let { OssRefTable.insert(it) }
            ?: throw ServiceException("oss不存在")
    }

    fun addOssBatch(businessId: Long, ossId: List<Long>) {
        OssTable.selectAll()
            .where { OssTable.id inList ossId }
            .map(OssTable::buildEntity)
            .map { it.toRefEntity(businessId, businessType()) }
            .let { OssRefTable.batchInsert(it) }
    }
}

fun OssEntity.toRefEntity(businessId: Long, businessType: BusinessTypeEnum): OssRefEntity {
    val ossEntity = this
    return OssRefEntity {
        this.businessId = businessId
        this.businessType = businessType.name
        this.ossId = ossEntity.id
        this.name = ossEntity.name
        this.url = ossEntity.url
        this.size = ossEntity.size
    }
}

