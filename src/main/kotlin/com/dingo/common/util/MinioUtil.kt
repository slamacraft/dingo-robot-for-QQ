package com.dingo.common.util

import cn.hutool.core.net.URLDecoder
import com.dingo.config.cfg.MinioCfg
import io.minio.BucketExistsArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MakeBucketArgs
import io.minio.PutObjectArgs
import io.minio.http.Method
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.*

object MinioUtil {

    fun getUrl(
        bucketName: String,
        fileName: String,
        argsModify: GetPresignedObjectUrlArgs.Builder.() -> Unit = {}
    ): String {
        val argsBuilder = GetPresignedObjectUrlArgs
            .builder()
            .bucket(bucketName)
            .`object`(fileName)
            .method(Method.GET)
        argsModify(argsBuilder)

        val url = MinioCfg.client.getPresignedObjectUrl(argsBuilder.build())
        return URLDecoder.decode(url, Charset.forName("UTF-8"));
    }

    fun upload(file: MultipartFile, bucketName: String = "public"): String {
        return uploadWithSize(bucketName, file.originalFilename, file.contentType, file.size, file.inputStream)
    }

    fun upload(
        bucketName: String = "public",
        fileName: String,
        contentType: String,
        inputStream: InputStream
    ): String {
        return uploadWithSize(fileName, bucketName, contentType, 67108864, inputStream)
    }

    fun uploadWithSize(
        bucketName: String = "public",
        fileName: String,
        contentType: String,
        size: Long,
        inputStream: InputStream
    ): String {
        val name = modifyFileName(fileName)
        inputStream.use {
            MinioCfg.client.putObject(
                PutObjectArgs.builder()
                    .bucket("bot").`object`(name)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build()
            )
        }
        return getUrl(bucketName, name)
    }

    fun uploadWithPart(
        bucketName: String = "public",
        fileName: String,
        contentType: String,
        partSize: Long,
        inputStream: InputStream
    ) {
        inputStream.use {
            MinioCfg.client.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName).`object`(modifyFileName(fileName))
                    .stream(inputStream, -1, partSize)
                    .contentType(contentType)
                    .build()
            )
        }
    }

    fun modifyFileName(name: String): String {
        val now = LocalDate.now()
        return "${now.year}/${now.month}/${now.dayOfMonth}/${UUID.randomUUID()}_${name}"
    }

    fun createBucket(name: String) {
        MinioCfg.client.makeBucket(
            MakeBucketArgs.builder()
                .bucket(name)
                .build()
        )
    }

    fun createBucketIfNotExist(name: String) {
        val bucketExist = bucketExist(name)
        if (bucketExist) {
            return
        }
        createBucket(name)
    }

    fun bucketExist(name: String): Boolean {
        return MinioCfg.client.bucketExists(
            BucketExistsArgs.builder()
                .bucket(name)
                .build()
        )
    }
}