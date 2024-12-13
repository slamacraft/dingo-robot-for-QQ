package com.dingo

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties
@ImportAutoConfiguration(ExposedAutoConfiguration::class)
open class RobotApplication

fun main(args: Array<String>) {
    "asdasd"
    "asdasdadsad"
    val springApplication = SpringApplication(RobotApplication::class.java)
    springApplication.run(*args)
}
