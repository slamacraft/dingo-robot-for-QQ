package com.dingo.channel.controller

import com.dingo.module.entity.oss.OssEntity
import com.dingo.module.service.OssService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/oss")
open class OssController {
    @Autowired
    lateinit var ossService: OssService

    @GetMapping("/{id}")
    open fun get(@PathVariable id: Long): OssEntity = ossService.get(id)

    @PostMapping("/upload")
    open fun upload(
        @RequestPart @RequestParam("file") file: MultipartFile
    ): OssEntity = ossService.upload(file, "bot")

    @GetMapping("/preview/{id}")
    open fun preview(@PathVariable id: String): String {
        return ossService.preview(id.toLong())
    }

}