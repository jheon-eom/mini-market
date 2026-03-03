package com.minimarket.catalogtservice.application.`in`

import com.minimarket.catalogtservice.application.dto.InventoryRollbackCommand

interface InventoryRollbackUseCase {
    fun rollback(command: InventoryRollbackCommand)
}