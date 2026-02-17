package com.minimarket.accountservice.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.testcontainers.containers.GenericContainer

@TestConfiguration
class RedisTestConfig {
    @Bean
    fun redisContainer(): GenericContainer<*> =
        GenericContainer("redis:7-alpine").withExposedPorts(6379).apply { start() }

    @Bean
    fun redisConnectionFactory(redisContainer: GenericContainer<*>): LettuceConnectionFactory {
        val host = redisContainer.host
        val port = redisContainer.getMappedPort(6379)
        return LettuceConnectionFactory(host, port).apply { afterPropertiesSet() }
    }

    @Bean
    fun redisTemplate(cf: LettuceConnectionFactory): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>().apply {
            connectionFactory = cf
            afterPropertiesSet()
        }
}