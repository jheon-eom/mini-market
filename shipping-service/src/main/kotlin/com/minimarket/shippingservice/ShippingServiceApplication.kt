package com.minimarket.shippingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.minimarket.shippingservice", "com.minimart.common"])
class ShippingServiceApplication

fun main(args: Array<String>) {
    runApplication<ShippingServiceApplication>(*args)
}