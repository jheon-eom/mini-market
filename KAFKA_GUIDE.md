# Kafka 이벤트 시스템 사용 가이드

## 개요

이 프로젝트는 Kafka를 사용한 이벤트 기반 마이크로서비스 아키텍처를 구현합니다.
모든 서비스는 공통 이벤트 인터페이스를 통해 이벤트를 발행하고 수신할 수 있습니다.

## Kafka 실행

### 1. Kafka 시작 (KRaft 모드 - Zookeeper 불필요)

```bash
docker-compose up -d
```

### 2. Kafka UI 접속

브라우저에서 http://localhost:8989 접속하여 Kafka UI를 통해 토픽, 메시지 등을 확인할 수 있습니다.

### 3. Kafka 종료

```bash
docker-compose down
```

## 이벤트 발행 예시

### 1. 이벤트 클래스 정의

```kotlin
package com.minimart.order.event

import com.minimart.common.event.DomainEvent
import java.time.LocalDateTime
import java.util.UUID

data class OrderCreatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
    override val eventType: String = "ORDER_CREATED",
    val orderId: Long,
    val userId: Long,
    val totalAmount: Long,
    val items: List<OrderItem>
) : DomainEvent

data class OrderItem(
    val productId: Long,
    val quantity: Int,
    val price: Long
)
```

### 2. 서비스에서 이벤트 발행

```kotlin
package com.minimart.order.service

import com.minimart.common.event.EventPublisher
import com.minimart.common.kafka.KafkaTopics
import com.minimart.order.event.OrderCreatedEvent
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val eventPublisher: EventPublisher
) {
    fun createOrder(userId: Long, items: List<OrderItem>): Order {
        // 주문 생성 로직
        val order = orderRepository.save(...)

        // 이벤트 발행
        val event = OrderCreatedEvent(
            orderId = order.id,
            userId = userId,
            totalAmount = order.totalAmount,
            items = items
        )

        eventPublisher.publish(KafkaTopics.ORDER_CREATED, event)

        return order
    }
}
```

## 이벤트 수신 예시

### 1. 이벤트 리스너 구현

```kotlin
package com.minimart.payment.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimart.common.event.EventListener
import com.minimart.common.kafka.KafkaTopics
import com.minimart.order.event.OrderCreatedEvent
import com.minimart.payment.service.PaymentService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class OrderCreatedEventListener(
    private val paymentService: PaymentService,
    private val objectMapper: ObjectMapper
) : EventListener<OrderCreatedEvent> {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [KafkaTopics.ORDER_CREATED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(message: String, acknowledgment: Acknowledgment) {
        try {
            val event = objectMapper.readValue(message, OrderCreatedEvent::class.java)
            handleEvent(event)
            acknowledgment.acknowledge()
        } catch (e: Exception) {
            logger.error("이벤트 처리 실패: {}", message, e)
            // 에러 처리 로직 (재시도, DLQ 전송 등)
        }
    }

    override fun handleEvent(event: OrderCreatedEvent) {
        logger.info("주문 생성 이벤트 수신 - OrderId: {}, UserId: {}", event.orderId, event.userId)

        // 결제 요청 생성
        paymentService.createPaymentRequest(
            orderId = event.orderId,
            userId = event.userId,
            amount = event.totalAmount
        )
    }
}
```

## 정의된 Kafka 토픽

모든 토픽은 `KafkaTopics` 객체에 상수로 정의되어 있습니다:

### Order 관련
- `order.created` - 주문 생성
- `order.confirmed` - 주문 확정
- `order.cancelled` - 주문 취소
- `order.completed` - 주문 완료

### Payment 관련
- `payment.requested` - 결제 요청
- `payment.completed` - 결제 완료
- `payment.failed` - 결제 실패

### Shipping 관련
- `shipping.requested` - 배송 요청
- `shipping.in-transit` - 배송 중
- `shipping.delivered` - 배송 완료
- `shipping.failed` - 배송 실패

### Catalog 관련
- `product.created` - 상품 생성
- `product.updated` - 상품 수정
- `product.deleted` - 상품 삭제
- `stock.updated` - 재고 변경

### Account 관련
- `user.registered` - 사용자 등록
- `user.updated` - 사용자 정보 수정

## 서비스별 Kafka 설정

각 서비스의 `application.yml`에 다음 설정이 추가되어 있습니다:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:29092
    consumer:
      group-id: {service-name}-group
      auto-offset-reset: earliest
      enable-auto-commit: false
    producer:
      acks: all
      retries: 3
```

## 포트 정보

- Kafka (내부): 9092
- Kafka (외부/로컬): 29092
- Kafka Controller: 9093
- Kafka UI: 8989

## 주의사항

1. **수동 커밋 모드**: 이벤트 처리 성공 후 명시적으로 `acknowledgment.acknowledge()` 호출 필요
2. **에러 처리**: 이벤트 처리 실패 시 적절한 에러 처리 로직 구현 필요
3. **멱등성**: 동일한 이벤트가 여러 번 전달될 수 있으므로 멱등성 고려 필요
4. **순서 보장**: 같은 키를 가진 메시지는 순서가 보장됨 (파티션 내에서)

## 트러블슈팅

### Kafka 연결 실패
```bash
# Kafka 컨테이너 상태 확인
docker ps

# Kafka 로그 확인
docker logs mini-market-kafka
```

### 토픽이 생성되지 않음
Kafka는 `auto.create.topics.enable=true` 설정으로 인해 메시지 발행 시 자동으로 토픽을 생성합니다.
수동으로 토픽을 생성하려면:

```bash
docker exec -it mini-market-kafka kafka-topics \
  --create \
  --topic order.created \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```