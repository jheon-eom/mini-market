package com.minimarket.accountservice.application

import com.minimarket.accountservice.application.dto.JoinCommand
import com.minimarket.accountservice.application.dto.JoinResult
import com.minimarket.accountservice.application.port.`in`.JoinUseCase
import com.minimarket.accountservice.application.port.out.UserRepository
import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.domain.EmailDuplicatedException
import com.minimarket.accountservice.application.port.out.AuthProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JoinService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authProvider: AuthProvider
): JoinUseCase {

    @Transactional
    override fun join(command: JoinCommand): JoinResult {
        userRepository.findByEmail(command.email)?.let { throw EmailDuplicatedException() }

        val user = User(
            email = command.email,
            passwordHash = passwordEncoder.encode(command.password)!!
        ).let { userRepository.save(it) }

        val authToken = authProvider.generate(
            userId = user.id!!,
            email = user.email,
            role = user.role.name
        )

        return JoinResult(
            id = user.id!!,
            accessToken = authToken.accessToken,
            refreshToken = authToken.refreshToken
        )
    }
}