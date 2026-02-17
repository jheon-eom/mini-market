package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.domain.UserStatusHistory

interface UserStatusHistoryWriter {
    fun save(userStatusHistory: UserStatusHistory): UserStatusHistory
}