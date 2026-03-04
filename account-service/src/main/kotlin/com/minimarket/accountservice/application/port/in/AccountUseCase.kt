package com.minimarket.accountservice.application.port.`in`

import com.minimarket.accountservice.application.dto.AuthToken
import com.minimarket.accountservice.application.dto.JoinCommand
import com.minimarket.accountservice.application.dto.JoinResult
import com.minimarket.accountservice.application.dto.LoginCommand

interface AccountUseCase {
    fun join(command: JoinCommand): JoinResult

    fun login(command: LoginCommand): AuthToken
}