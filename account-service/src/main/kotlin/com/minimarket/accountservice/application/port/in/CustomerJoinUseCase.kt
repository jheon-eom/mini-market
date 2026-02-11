package com.minimarket.accountservice.application.port.`in`

import com.minimarket.accountservice.application.dto.CustomerJoinCommand
import com.minimarket.accountservice.application.dto.CustomerJoinResult

interface CustomerJoinUseCase {
    fun joinCustomer(command: CustomerJoinCommand): CustomerJoinResult
}