package com.minimarket.accountservice.application

import com.minimarket.accountservice.adapter.out.security.JwtTokenProvider
import com.minimarket.accountservice.application.dto.JoinCommand
import com.minimarket.accountservice.application.port.out.UserRepository
import com.minimarket.accountservice.domain.AccountApiException
import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.domain.UserRole
import com.minimart.common.exception.TokenExpiredException
import com.minimart.common.security.JwtProperties
import com.minimart.common.security.JwtValidator
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@DataJpaTest
class AccountServiceTest {

    @Autowired private lateinit var userRepository: UserRepository

    @Autowired private lateinit var jwtValidator: JwtValidator

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    private val accountService: AccountService by lazy {
        AccountService(
            userRepository = userRepository,
            passwordEncoder = BCryptPasswordEncoder(),
            authProvider = JwtTokenProvider(
                JwtProperties(
                    secret = "jQbN2ymPMrJ2VmCPq2ZKgaFnJdsSFGCdmsqscTn14d8=",
                    accessTokenValidity = 1,
                    refreshTokenValidity = 10,
                )
            ),
        )
    }

    @Test
    @DisplayName("중복된 이메일로 가입 시도 시 실패한다")
    fun `duplicate email will fail to join`() {
        // given
        val duplicatedEmail = saveDuplicateUser()

        // when
        val command = JoinCommand(
            email = duplicatedEmail,
            password = "123456",
            role = UserRole.CUSTOMER
        )

        // then
        assertThrows(AccountApiException::class.java) {
            accountService.join(command)
        }
    }

    @Test
    @DisplayName("회원가입에 성공하면 id, accessToken, refreshToken을 반환한다.")
    fun `join successfully returns id, accessToken, refreshToken`() {
        // given
        val command = JoinCommand(
            email = "test@example.com",
            password = passwordEncoder.encode("123456")!!,
            role = UserRole.CUSTOMER
        )

        // when
        val result = accountService.join(command)

        // then
        assertThat(result.id).isNotNull
        assertThat(result.accessToken).isNotBlank
        assertThat(result.refreshToken).isNotBlank
    }

    @Test
    @DisplayName("accessToken의 유효기간이 지나면 TokenExpiredException 예외가 발생한다.")
    fun `expired accessToken will TokenExpiredException`() {
        // given
        val command = JoinCommand(
            email = "test@example.com",
            password = passwordEncoder.encode("123456")!!,
            role = UserRole.CUSTOMER
        )

        val result = accountService.join(command)

        // when
        Thread.sleep(2) // accessToken 유효기간보다 1초 더 기다림

        // then
        assertThrows(TokenExpiredException::class.java) {
            jwtValidator.validateToken(result.accessToken)
        }
    }

    private fun saveDuplicateUser(): String {
        val email = "test@example.com"

        val user = User(
            email = email,
            passwordHash = passwordEncoder.encode("password123")!!,
            role = UserRole.CUSTOMER
        )

        userRepository.save(user)

        return email
    }
}