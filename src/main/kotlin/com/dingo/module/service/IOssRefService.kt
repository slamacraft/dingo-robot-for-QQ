package com.dingo.module.service

import com.dingo.common.enums.BusinessTypeEnum
import com.dingo.common.interfaces.service.IBusinessService
import com.dingo.module.entity.oss.OssEntity
import com.dingo.module.entity.oss.OssRefEntity
import com.dingo.module.entity.oss.OssRefTable
import com.dingo.module.entity.oss.OssTable
import com.dingo.module.entity.oss.OssTable.getById
import com.dingo.module.model.OssRefVo
import com.dingo.module.model.toModel
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
        OssTable.getById(ossId)
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

    fun editOss(businessId: Long, ossIdList: List<Long>) {
        val existOssRefList = OssRefTable.selectAll()
            .where { OssTable.id eq businessId }
            .map(OssRefTable::buildEntity)


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

