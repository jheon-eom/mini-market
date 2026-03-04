package com.minimarket.orderservice.adapter.out.persistence.jpa.entity

import com.minimarket.orderservice.domain.BuyerId
import com.minimarket.orderservice.domain.Order
import com.minimarket.orderservice.domain.OrderId
import com.minimarket.orderservice.domain.OrderLine
import com.minimarket.orderservice.domain.OrderStatus
import com.minimarket.orderservice.domain.ShippingInfo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "`order`")
class OrderEntity(
    @Id
    @Column(nullable = false, updatable = false, unique = true)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    val id: UUID? = null,

    @Column(nullable = false, updatable = false)
    val buyerId: Long,

    @Column(nullable = false)
    var amount: BigDecimal,

    @Column(nullable = false)
    var status: String,

    @Column(nullable = false, updatable = false)
    val orderedAt: LocalDateTime,
): BaseEntity() {
    fun toDomainWithoutOrderLines(): Order {
        return Order(
            id = OrderId(id.toString()),
            buyerId = BuyerId(buyerId),
            lines = emptyList(),
            amount = amount,
            status = OrderStatus.valueOf(status),
            orderedAt = orderedAt,
            shippingInfo = ShippingInfo(
                receiverName = "",
                address = "",
                detailAddress = ""
            )
        )
    }
    fun toDomain(orderLines: List<OrderLine>, shippingInfo: ShippingInfoEntity): Order {
        return Order(
            id = OrderId(id.toString()),
            buyerId = BuyerId(buyerId),
            lines = orderLines,
            amount = amount,
            status = OrderStatus.valueOf(status),
            orderedAt = orderedAt,
            shippingInfo = ShippingInfo(
                receiverName = shippingInfo.receiverName,
                address = shippingInfo.address,
                detailAddress = shippingInfo.detailAddress
            )
        )
    }
}