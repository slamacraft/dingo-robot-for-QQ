package com.dingo.module.oss.service

import com.dingo.channel.model.OssAddReq
import com.dingo.module.oss.entity.OssEntity
import com.dingo.module.oss.entity.OssTable
import com.dingo.module.oss.entity.OssTable.getById
import com.dingo.common.util.MinioUtil
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Component
open class OssService {

    @Transactional
    open fun get(id: Long): OssEntity = OssTable.getById(id) ?: throw RuntimeException("oss不存在")

    @Transactional
    open fun upload(file: MultipartFile, bucketName: String): OssEntity {
        val fileUrl = MinioUtil.upload(file, bucketName)
        return OssTable.insert(OssEntity {
            name = file.originalFilename
            size = file.size
            url = fileUrl
        })
    }

    @Transactional
    open fun add(req: OssAddReq) = OssTable.insert(OssEntity {
        name = req.name
        size = req.size
        url = req.url
    })

}