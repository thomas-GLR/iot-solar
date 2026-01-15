package org.exemple.iotsolarapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class IotSolarApiApplication

fun main(args: Array<String>) {
    runApplication<IotSolarApiApplication>(*args)
}
