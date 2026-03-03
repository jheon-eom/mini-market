package com.minimart.common.event.kafka

object EventTopic {
    const val ORDER_CREATED = "order.created" // 주문 생성

    const val INVENTORY_RESERVED = "inventory.reserved" // 재고 예약

    const val INVENTORY_FAILED = "inventory.failed" // 재고 예약 실패

    const val ORDER_RESERVED = "order.reserved" // 주문 예약 완료

    const val ORDER_FAILED = "order.failed" // 주문 처리 실패

    const val PAYMENT_PROCESSED = "payment.processed" // 결제 처리 완료

    const val PAYMENT_FAILED = "payment.failed" // 결제 처리 실패

    const val SHIPPING_CREATED = "shipping.created" // 배송 생성

    const val SHIPPING_FAILED = "shipping.failed" // 배송 생성 실패
}