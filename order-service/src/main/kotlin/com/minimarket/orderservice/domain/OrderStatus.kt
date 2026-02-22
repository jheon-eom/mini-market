package com.minimarket.orderservice.domain

enum class OrderStatus {
    PENDING, // 주문이 생성되고 결제가 완료되지 않은 상태
    PAID,    // 결제가 완료된 상태
    SHIPPED, // 주문이 배송 중인 상태
    DELIVERED, // 주문이 배송 완료된 상태
    CONFIRMED, // 주문이 구매자에 의해 확인된 상태
    CANCELLED, // 주문이 취소된 상태
}