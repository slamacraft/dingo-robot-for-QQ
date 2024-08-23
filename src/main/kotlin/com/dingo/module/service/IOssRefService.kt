package com.dingo.module.service

import com.dingo.common.enums.BusinessTypeEnum
import com.dingo.common.interfaces.service.IBusinessService
import com.dingo.module.entity.oss.OssEntity
import com.dingo.module.entity.oss.OssRefEntity
import com.dingo.module.entity.oss.OssRefTable
import com.dingo.module.entity.oss.OssTable
import com.dingo.module.model.OssRefVo
import com.dingo.module.model.toModel
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll

interface IOssRefService : IBusinessService {

    fun ossService(): OssService

    /**
     * 获取关联的文件
     */
    fun listOss(businessId: Long): List<OssRefEntity> = OssRefTable.selectAll()
        .where(eqBusinessType())
        .where { OssRefTable.businessId eq businessId }
        .map(OssRefTable::buildEntity)

    /**
     * 批量获取关联的文件
     */
    fun listOssBatch(businessIds: List<Long>): List<OssRefEntity> {
        if (businessIds.isEmpty()) {
            return listOf()
        }

        return OssRefTable.selectAll()
            .where(eqBusinessType())
            .where { OssRefTable.businessId inList businessIds }
            .map(OssRefTable::buildEntity)
    }

    /**
     * 获取关联的文件，并转换为vo
     */
    fun listOssVo(businessId: Long): List<OssRefVo> = listOss(businessId).map { it.toModel() }

    /**
     * 批量新增关联文件
     */
    fun addOssBatch(businessId: Long, ossId: List<Long>) {
        OssTable.selectAll()
            .where { OssTable.id inList ossId }
            .map(OssTable::buildEntity)
            .map { it.toRefEntity(businessId) }
            .let { OssRefTable.batchInsert(it) }
    }

    /**
     * 编辑关联文件
     */
    fun editOss(businessId: Long, ossIdList: List<Long>) {
        // 之前保存的依赖关系
        val existOssRefList = OssRefTable.selectAll()
            .where { (OssRefTable.id eq businessId) }
            .map(OssRefTable::buildEntity)


        // 删除旧的依赖关系
        OssRefTable.deleteWhere {
            (OssRefTable.businessId eq businessId) and eqBusinessType()
        }
        // 添加新的OssRef
        addOssBatch(businessId, ossIdList)

        // 过滤出要删除的oss
        val notEditOssIds = existOssRefList.filter { !ossIdList.contains(it.ossId) }
            .map { it.ossId }
        // 删除未关联的文件
        ossService().removeByIds(notEditOssIds)
    }

    fun removeOss(businessId: Long) {
        val ossList = listOss(businessId)

        // 删除旧的依赖关系
        OssRefTable.removeByIds(ossList.map { it.id })
        ossService().removeByIds(ossList.map { it.ossId })
    }

    fun eqBusinessType(): Op<Boolean> {
        return OssRefTable.businessType eq businessType().name
    }

    /**
     *  ==========================================
     *  ================ 拓展函数 =================
     *  =========================================
     */
    private fun OssEntity.toRefEntity(businessId: Long): OssRefEntity {
        val ossEntity = this
        return OssRefEntity {
            this.businessId = businessId
            this.businessType = businessType().name
            this.ossId = ossEntity.id
            this.name = ossEntity.name
            this.url = ossEntity.url
            this.size = ossEntity.size
        }
    }
}



