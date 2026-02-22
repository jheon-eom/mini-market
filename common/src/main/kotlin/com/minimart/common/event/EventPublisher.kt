package com.minimart.common.event

/**
 * 이벤트 발행 인터페이스
 */
interface EventPublisher {
    /**
     * 이벤트를 특정 토픽으로 발행
     * @param topic 이벤트를 발행할 토픽 이름
     * @param event 발행할 이벤트 객체
     */
    fun publish(topic: String, event: DomainEvent)

    /**
     * 이벤트를 특정 토픽과 키로 발행 (파티셔닝을 위한 키 지정)
     * @param topic 이벤트를 발행할 토픽 이름
     * @param key 파티셔닝 키
     * @param event 발행할 이벤트 객체
     */
    fun publish(topic: String, key: String, event: DomainEvent)
}