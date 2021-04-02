package com.github.remering.krutheye

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("com.github.remering.krutheye.mapper")
class KruthEyeApplication

fun main(args: Array<String>) {
    runApplication<KruthEyeApplication>(*args)
}
