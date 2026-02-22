package com.minimart.common.event

/**
 * 이벤트 리스너 마커 인터페이스
 * 이벤트를 처리하는 클래스는 이 인터페이스를 구현하고
 * @KafkaListener 어노테이션을 사용하여 특정 토픽을 구독합니다
 */
interface EventListener<T: DomainEvent> {
    /**
     * 이벤트 처리
     * @param event 수신한 이벤트
     */
    fun handleEvent(event: T)
}