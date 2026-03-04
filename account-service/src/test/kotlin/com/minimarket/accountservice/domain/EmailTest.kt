package com.minimarket.accountservice.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class EmailTest {
    @Test
    fun `유효한 이메일 형식으로 Email 객체를 생성할 수 있다`() {
        // given
        val validEmails = listOf(
            "user@example.com",
            "test.user@example.com",
            "test_user@example.com",
            "test+user@example.com",
            "test-user@example.com",
            "user123@test-domain.co.kr",
            "a@b.co"
        )

        // when & then
        validEmails.forEach { emailValue ->
            val email = Email(emailValue)
            assertEquals(emailValue, email.value)
        }
    }

    @Test
    fun `빈 문자열로 Email 객체를 생성하면 예외가 발생한다`() {
        // when & then
        assertThrows<IllegalArgumentException> {
            Email("")
        }
    }

    @Test
    fun `@가 없는 이메일 형식은 예외가 발생한다`() {
        // when & then
        assertThrows<IllegalArgumentException> {
            Email("invalidemail.com")
        }
    }

    @Test
    fun `도메인이 없는 이메일 형식은 예외가 발생한다`() {
        // when & then
        assertThrows<IllegalArgumentException> {
            Email("user@")
        }
    }

    @Test
    fun `로컬파트가 없는 이메일 형식은 예외가 발생한다`() {
        // when & then
        assertThrows<IllegalArgumentException> {
            Email("@example.com")
        }
    }

    @Test
    fun `TLD가 없는 이메일 형식은 예외가 발생한다`() {
        // when & then
        assertThrows<IllegalArgumentException> {
            Email("user@domain")
        }
    }

    @Test
    fun `TLD가 1글자인 이메일 형식은 예외가 발생한다`() {
        // when & then
        assertThrows<IllegalArgumentException> {
            Email("user@domain.c")
        }
    }

    @Test
    fun `특수문자가 포함된 잘못된 이메일 형식은 예외가 발생한다`() {
        // when & then
        val invalidEmails = listOf(
            "user name@example.com",  // 공백
            "user@exam ple.com",       // 도메인에 공백
            "user#test@example.com",   // # 특수문자
            "user@exam#ple.com"        // 도메인에 # 특수문자
        )

        invalidEmails.forEach { invalidEmail ->
            assertThrows<IllegalArgumentException> {
                Email(invalidEmail)
            }
        }
    }
}