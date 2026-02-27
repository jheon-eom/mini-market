package com.minimarket.orderservice.application

import com.minimarket.orderservice.adapter.`in`.event.OrderReserveFailedCommand
import com.minimarket.orderservice.adapter.`in`.event.OrderReserveSuccessCommand
import com.minimarket.orderservice.adapter.out.persistence.jpa.EventOutBoxJpaFinder
import com.minimarket.orderservice.adapter.out.persistence.jpa.EventOutBoxJpaWriter
import com.minimarket.orderservice.adapter.out.persistence.jpa.EventOutBoxRepository
import com.minimarket.orderservice.adapter.out.persistence.jpa.OrderJpaFinder
import com.minimarket.orderservice.adapter.out.persistence.jpa.OrderJpaWriter
import com.minimarket.orderservice.adapter.out.persistence.jpa.OrderLineRepository
import com.minimarket.orderservice.adapter.out.persistence.jpa.OrderRepository
import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderEntity
import com.minimarket.orderservice.domain.OrderStatus
import com.minimart.common.event.kafka.EventTopic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class OrderStatusUpdateServiceTest {
    @Autowired private lateinit var orderRepository: OrderRepository
    @Autowired private lateinit var orderLineRepository: OrderLineRepository
    @Autowired private lateinit var eventOutBoxRepository: EventOutBoxRepository
    @Autowired private lateinit var applicationEventPublisher: ApplicationEventPublisher

    private lateinit var orderStatusUpdateService: OrderStatusUpdateService

    @BeforeEach
    fun setUp() {
        cleanUp()
        initDependencies()
    }

    @Test
    fun `예약 성공 이벤트 수신 시 주문 상태를 예약 완료로 변경하고 아웃박스를 저장한다`() {
        // given
        val order = createOrder(status = OrderStatus.PENDING)
        val command = OrderReserveSuccessCommand(
            eventId = "event-001",
            eventType = EventTopic.INVENTORY_RESERVED,
            orderId = order.id.toString()
        )

        // when
        orderStatusUpdateService.updateToReserved(command)

        // then
        val updatedOrder = orderRepository.findById(order.id!!).get()
        assertEquals(OrderStatus.RESERVED.name, updatedOrder.status, "주문 상태가 RESERVED로 변경되어야 합니다")

        val outBoxes = eventOutBoxRepository.findAll()
        assertEquals(1, outBoxes.size, "아웃박스가 1개 저장되어야 합니다")
        assertEquals(EventTopic.INVENTORY_RESERVED, outBoxes.first().eventType)
    }

    @Test
    fun `예약 성공 이벤트 수신 시 아웃박스 저장과 주문 상태 변경이 원자성을 보장한다`() {
        // given
        val order = createOrder(status = OrderStatus.PENDING)
        val command = OrderReserveSuccessCommand(
            eventId = "event-002",
            eventType = EventTopic.INVENTORY_RESERVED,
            orderId = order.id.toString()
        )

        // when
        orderStatusUpdateService.updateToReserved(command)

        // then
        val updatedOrder = orderRepository.findById(order.id!!).get()
        val outBoxes = eventOutBoxRepository.findAll()

        assertEquals(OrderStatus.RESERVED.name, updatedOrder.status)
        assertEquals(1, outBoxes.size, "아웃박스가 1개 저장되어야 합니다")
        assertEquals(order.id.toString(), outBoxes.first().relationId)
        assertEquals(EventTopic.INVENTORY_RESERVED, outBoxes.first().eventType)
    }

    @Test
    fun `예약 실패 이벤트 수신 시 주문 상태를 예약 실패로 변경한다`() {
        // given
        val order = createOrder(status = OrderStatus.PENDING)
        val command = OrderReserveFailedCommand(
            eventId = "event-004",
            eventType = EventTopic.INVENTORY_FAILED,
            orderId = order.id.toString()
        )

        // when
        orderStatusUpdateService.updateToReserveFail(command)

        // then
        val updatedOrder = orderRepository.findById(order.id!!).get()
        assertEquals(OrderStatus.RESERVE_FAILED.name, updatedOrder.status, "주문 상태가 RESERVE_FAILED로 변경되어야 합니다")
    }

    @Test
    fun `이미 처리된 이벤트 ID인 경우 예약 성공 처리를 건너뛴다`() {
        // given
        val order = createOrder(status = OrderStatus.PENDING)
        val command = OrderReserveSuccessCommand(
            eventId = "event-005",
            eventType = EventTopic.INVENTORY_RESERVED,
            orderId = order.id.toString()
        )

        // 첫 번째 처리
        orderStatusUpdateService.updateToReserved(command)

        // when - 동일한 이벤트 ID로 재시도
        orderStatusUpdateService.updateToReserved(command)

        // then
        val outBoxes = eventOutBoxRepository.findAll()
        assertEquals(1, outBoxes.size, "중복 처리되지 않고 아웃박스가 1개만 저장되어야 합니다")
    }

    @Test
    fun `이미 처리된 이벤트 ID인 경우 예약 실패 처리를 건너뛴다`() {
        // given
        val order = createOrder(status = OrderStatus.PENDING)
        val command = OrderReserveFailedCommand(
            eventId = "event-006",
            eventType = EventTopic.INVENTORY_FAILED,
            orderId = order.id.toString()
        )

        // 첫 번째 처리
        orderStatusUpdateService.updateToReserveFail(command)
        val firstStatus = orderRepository.findById(order.id!!).get().status

        // when - 동일한 이벤트 ID로 재시도
        orderStatusUpdateService.updateToReserveFail(command)

        // then
        val secondStatus = orderRepository.findById(order.id!!).get().status
        assertEquals(firstStatus, secondStatus, "중복 처리되지 않아야 합니다")
    }

    // === Setup 헬퍼 메서드 ===
    private fun initDependencies() {
        val orderFinder = OrderJpaFinder(orderRepository)
        val orderWriter = OrderJpaWriter(orderRepository, orderLineRepository)
        val eventOutBoxWriter = EventOutBoxJpaWriter(eventOutBoxRepository)
        val eventOutBoxReader = EventOutBoxJpaFinder(eventOutBoxRepository)

        orderStatusUpdateService = OrderStatusUpdateService(
            orderFinder = orderFinder,
            orderWriter = orderWriter,
            eventOutBoxWriter = eventOutBoxWriter,
            eventOutBoxReader = eventOutBoxReader,
            applicationEventPublisher = applicationEventPublisher
        )
    }

    private fun cleanUp() {
        eventOutBoxRepository.deleteAll()
        orderLineRepository.deleteAll()
        orderRepository.deleteAll()
    }

    // === 테스트 데이터 생성 헬퍼 메서드 ===
    private fun createOrder(status: OrderStatus): OrderEntity {
        return orderRepository.save(
            OrderEntity(
                buyerId = 1L,
                amount = BigDecimal("10000"),
                status = status.name,
                orderedAt = LocalDateTime.now()
            )
        )
    }

}