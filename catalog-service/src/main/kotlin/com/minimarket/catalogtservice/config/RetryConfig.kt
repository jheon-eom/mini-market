package com.minimarket.catalogtservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

/**
 * Spring Retry 설정
 */
@Configuration
@EnableRetry
class RetryConfig