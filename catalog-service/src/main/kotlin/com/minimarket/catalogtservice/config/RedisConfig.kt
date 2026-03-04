package com.minimarket.catalogtservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory("localhost", 6380)
    }

    @Bean
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