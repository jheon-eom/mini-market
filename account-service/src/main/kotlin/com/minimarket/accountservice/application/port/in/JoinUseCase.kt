package com.minimarket.accountservice.application.port.`in`

import com.minimarket.accountservice.application.dto.JoinCommand
import com.minimarket.accountservice.application.dto.JoinResult

interface JoinUseCase {
    fun join(command: JoinCommand): JoinResult
}