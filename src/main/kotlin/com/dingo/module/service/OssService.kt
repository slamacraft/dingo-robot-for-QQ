package com.dingo.module.service

import com.dingo.common.util.MinioUtil
import com.dingo.module.entity.oss.OssEntity
import com.dingo.module.entity.oss.OssTable
import com.dingo.module.entity.oss.OssTable.getById
import com.google.protobuf.ServiceException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.TimeUnit

@Component
open class OssService {

    open fun get(id: Long): OssEntity = OssTable.getById(id) ?: throw RuntimeException("oss不存在")

    @Transactional
    open fun upload(file: MultipartFile, bucketName: String): OssEntity {
        val fileUrl = MinioUtil.upload(file, bucketName)

        return OssTable.insert(OssEntity {
            name = file.originalFilename
            this.bucketName = bucketName
            size = file.size
            url = fileUrl
        })
    }

    open fun preview(ossId: Long): String {
        val ossEntity = get(ossId)
        // 获取一个有效时间为1天的链接
        return MinioUtil.getUrl(ossEntity.bucketName, ossEntity.name) {
            expiry(1, TimeUnit.DAYS)
        }
    }

    @Transactional
    open fun removeById(ossId: Long) {
        val entity = OssTable.getById(ossId) ?: throw ServiceException("oss不存在")
        OssTable.removeById(ossId)
        MinioUtil.removeObject(entity.bucketName, entity.name)
    }

    @Transactional
    open fun removeByIds(ossIds: List<Long>) {
        val entityList = OssTable.listByIds(ossIds)
        OssTable.removeByIds(ossIds)
        entityList.forEach {
            MinioUtil.removeObject(it.bucketName, it.name)
        }
    }

}