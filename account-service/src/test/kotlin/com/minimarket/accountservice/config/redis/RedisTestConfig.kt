package com.minimarket.accountservice.config.redis

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
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

    @Bean( "testRedisTemplate")
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val serializer = GenericJacksonJsonRedisSerializer.builder()
            .enableUnsafeDefaultTyping()
            .build()

        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)

            keySerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()

            valueSerializer = serializer
            hashValueSerializer = serializer

            afterPropertiesSet()
        }
    }
}