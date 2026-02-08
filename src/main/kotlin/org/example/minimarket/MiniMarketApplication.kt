package org.example.minimarket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MiniMarketApplication

fun main(args: Array<String>) {
    runApplication<MiniMarketApplication>(*args)
}
