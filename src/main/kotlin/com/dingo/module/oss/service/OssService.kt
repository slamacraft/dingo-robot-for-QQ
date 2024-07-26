package com.dingo.module.oss.service

import com.dingo.common.util.MinioUtil
import com.dingo.module.oss.entity.OssEntity
import com.dingo.module.oss.entity.OssTable
import com.dingo.module.oss.entity.OssTable.getById
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
        return MinioUtil.getUrl(ossEntity.bucketName, ossEntity.url) {
            expiry(1, TimeUnit.DAYS)
        }
    }

}