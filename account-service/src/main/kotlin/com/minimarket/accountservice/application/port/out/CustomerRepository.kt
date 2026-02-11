package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.domain.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer, Long> {
    fun findByEmail(email: String): Customer?
}