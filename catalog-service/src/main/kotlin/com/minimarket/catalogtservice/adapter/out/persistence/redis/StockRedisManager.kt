package com.minimarket.catalogtservice.adapter.out.persistence.redis

import com.minimarket.catalogtservice.application.out.StockManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component

@Component
class StockRedisManager(
    private val redisTemplate: RedisTemplate<String, Any>
): StockManager {
    companion object {
        private const val STOCK_KEY_PREFIX = "product:stock:"

        // Lua 스크립트: 재고 감소 및 남은 재고 반환
        // KEYS[1]: 재고 키
        // ARGV[1]: 감소할 수량
        // 반환값: 남은 재고 수량, -1(재고 부족)
        private val DECREASE_STOCK_SCRIPT = """
            local stock = redis.call('GET', KEYS[1])
            if not stock then
                return -1
            end

            local currentStock = tonumber(stock)
            local quantity = tonumber(ARGV[1])

            if currentStock < quantity then
                return -1
            end

            local remainingStock = currentStock - quantity
            redis.call('SET', KEYS[1], remainingStock)
            return remainingStock
        """.trimIndent()

        // Lua 스크립트: 재고 초기화 (키가 없을 때만)
        // KEYS[1]: 재고 키
        // ARGV[1]: 설정할 재고 수량
        // 반환값: 1(성공), 0(이미 존재)
        private val INIT_STOCK_SCRIPT = """
            local exists = redis.call('EXISTS', KEYS[1])
            if exists == 1 then
                return 0
            end

            redis.call('SET', KEYS[1], ARGV[1])
            return 1
        """.trimIndent()

        // Lua 스크립트: 재고 증가
        // KEYS[1]: 재고 키
        // ARGV[1]: 증가할 수량
        // 반환값: 증가 후 재고 수량
        private val INCREASE_STOCK_SCRIPT = """
            local stock = redis.call('GET', KEYS[1])
            if not stock then
                return -1
            end

            local currentStock = tonumber(stock)
            local quantity = tonumber(ARGV[1])
            local newStock = currentStock + quantity

            redis.call('SET', KEYS[1], newStock)
            return newStock
        """.trimIndent()
    }

    override fun check(productId: Long): Boolean {
        val key = STOCK_KEY_PREFIX + productId
        val stock = redisTemplate.opsForValue().get(key)
        return stock != null
    }

    override fun init(productId: Long, stock: Int) {
        val key = STOCK_KEY_PREFIX + productId

        redisTemplate.execute(
            RedisScript.of(INIT_STOCK_SCRIPT, Long::class.java),
            listOf(key),
            stock.toString()
        )
    }

    override fun decrease(productId: Long, quantity: Int): Int {
        val key = STOCK_KEY_PREFIX + productId

        val result = redisTemplate.execute(
            RedisScript.of(DECREASE_STOCK_SCRIPT, Long::class.java),
            listOf(key),
            quantity.toString()
        )

        return result?.toInt() ?: -1
    }

    override fun add(productId: Long, quantity: Int) {
        val key = STOCK_KEY_PREFIX + productId

        redisTemplate.execute(
            RedisScript.of(INCREASE_STOCK_SCRIPT, Long::class.java),
            listOf(key),
            quantity.toString()
        )
    }
}