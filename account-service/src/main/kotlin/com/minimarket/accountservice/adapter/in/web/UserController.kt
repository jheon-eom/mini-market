package com.minimarket.accountservice.adapter.`in`.web

import com.minimarket.accountservice.application.port.out.UserRepository
import com.minimart.common.security.CurrentUserId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userRepository: UserRepository,
) {

    // TODO: 임시 테스트용, 로그인 먼저 구현
    @GetMapping
    fun getInfo(@CurrentUserId currentUserId: Long): String {
        return userRepository.findById(currentUserId).toString()
    }
}