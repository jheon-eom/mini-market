package com.minimarket.accountservice.application

import com.minimarket.accountservice.application.dto.CustomerJoinCommand
import com.minimarket.accountservice.application.dto.CustomerJoinResult
import com.minimarket.accountservice.application.port.`in`.CustomerJoinUseCase
import com.minimarket.accountservice.application.port.out.CustomerRepository
import com.minimarket.accountservice.domain.Customer
import com.minimarket.accountservice.domain.EmailDuplicatedException
import com.minimarket.accountservice.adapter.out.security.JwtTokenProvider
import com.minimarket.accountservice.application.port.out.AuthProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerJoinService(
    private val customerRepository: CustomerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authProvider: AuthProvider
): CustomerJoinUseCase {

    @Transactional
    override fun joinCustomer(command: CustomerJoinCommand): CustomerJoinResult {
        customerRepository.findByEmail(command.email)?.let { throw EmailDuplicatedException() }

        val customer = Customer(
            email = command.email,
            passwordHash = passwordEncoder.encode(command.password)!!
        ).let { customerRepository.save(it) }

        val authToken = authProvider.generate(
            customerId = customer.id!!,
            email = customer.email
        )

        return CustomerJoinResult(
            id = customer.id!!,
            accessToken = authToken.accessToken,
            refreshToken = authToken.refreshToken
        )
    }
}