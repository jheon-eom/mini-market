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
import com.minimarket.accountservice.application.port.out.UserStatusHistoryWriter
import com.minimarket.accountservice.domain.AccountApiException
import com.minimarket.accountservice.domain.Email
import com.minimarket.accountservice.domain.ErrorCode.*
import com.minimarket.accountservice.domain.UserStatus
import com.minimarket.accountservice.domain.UserStatusHistory
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val userWriter: UserWriter,
    private val userFinder: UserFinder,
    private val userStatusHistoryWriter: UserStatusHistoryWriter,
    private val passwordEncoder: PasswordEncoder,
    private val authProvider: AuthProvider,
    private val refreshTokenService: RefreshTokenService
) : AccountUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun join(command: JoinCommand): JoinResult {
        logger.info("[Account] 회원가입 시작 - email: ${command.email}, role: ${command.role}")

        userFinder.findByEmail(command.email)
            ?.let {
                logger.warn("[Account] 이메일 중복 - email: ${command.email}")
                throw AccountApiException(EMAIL_DUPLICATED)
            }

        val user = User(
            email = Email(command.email),
            passwordHash = passwordEncoder.encode(command.password)!!,
            role = command.role,
            status = UserStatus.ACTIVE
        ).let { userWriter.save(it) }

        UserStatusHistory(
            userId = user.id!!,
            status = UserStatus.ACTIVE,
            reason = "회원가입"
        ).let { userStatusHistoryWriter.save(it) }

        val token = authProvider.generate(user)

        saveRefreshToken(user, token.refreshToken)

        logger.info("[Account] 회원가입 완료 - userId: ${user.id.value}, email: ${command.email}")

        return JoinResult(
            id = user.id.value,
            accessToken = token.accessToken,
            refreshToken = token.refreshToken
        )
    }

    override fun login(command: LoginCommand): AuthToken {
        logger.info("[Account] 로그인 시도 - email: ${command.email}")

        val user = userFinder.findByEmail(command.email)
            ?: run {
                logger.warn("[Account] 존재하지 않는 이메일 - email: ${command.email}")
                throw AccountApiException(EMAIL_NOT_FOUND)
            }

        if (!passwordEncoder.matches(
                command.password,
                user.passwordHash
            )
        ) {
            logger.warn("[Account] 비밀번호 불일치 - email: ${command.email}")
            throw AccountApiException(INVALID_PASSWORD)
        }

        val token = authProvider.generate(user)
        saveRefreshToken(user, token.refreshToken)

        logger.info("[Account] 로그인 성공 - userId: ${user.id!!.value}, email: ${command.email}")

        return AuthToken(
            accessToken = token.accessToken,
            refreshToken = token.refreshToken
        )
    }

    private fun saveRefreshToken(user: User, refreshToken: String) {
        refreshTokenService.save(user, refreshToken)
    }
}