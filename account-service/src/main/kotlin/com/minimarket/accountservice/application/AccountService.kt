package com.minimarket.accountservice.application

import com.minimarket.accountservice.application.dto.AuthToken
import com.minimarket.accountservice.application.dto.JoinCommand
import com.minimarket.accountservice.application.dto.JoinResult
import com.minimarket.accountservice.application.dto.LoginCommand
import com.minimarket.accountservice.application.port.`in`.AccountUseCase
import com.minimarket.accountservice.application.port.out.UserWriter
import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.application.port.out.AuthProvider
import com.minimarket.accountservice.application.port.out.UserFinder
import com.minimarket.accountservice.domain.AccountApiException
import com.minimarket.accountservice.domain.ErrorCode.*
import com.minimarket.accountservice.domain.UserStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val userWriter: UserWriter,
    private val userFinder: UserFinder,
    private val passwordEncoder: PasswordEncoder,
    private val authProvider: AuthProvider
): AccountUseCase {
    @Transactional
    override fun join(command: JoinCommand): JoinResult {
        userFinder.findByEmail(command.email)
            ?.let { throw AccountApiException(EMAIL_DUPLICATED) }

        val user = User(
            email = command.email,
            passwordHash = passwordEncoder.encode(command.password)!!,
            role = command.role,
            status = UserStatus.ACTIVE
        ).let { userWriter.save(it) }

        val authToken = authProvider.generate(user)

        return JoinResult(
            id = user.id!!,
            accessToken = authToken.accessToken,
            refreshToken = authToken.refreshToken
        )
    }

    override fun login(command: LoginCommand): AuthToken {
        val user = userFinder.findByEmail(command.email)
            ?: throw AccountApiException(EMAIL_NOT_FOUND)

        if (!passwordEncoder.matches(
                command.password,
                user.passwordHash
        )) {
            throw AccountApiException(INVALID_PASSWORD)
        }

        return authProvider.generate(user)
    }
}