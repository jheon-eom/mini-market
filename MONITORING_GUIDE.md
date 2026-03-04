# Mini-Market 모니터링 시스템 가이드

## 개요

이 문서는 Mini-Market 프로젝트의 ELK Stack + Spring Cloud Sleuth + Zipkin 기반 분산 로깅 및 모니터링 시스템 사용 가이드입니다.

## 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                각 마이크로서비스                               │
│  - Spring Cloud Sleuth (자동 Trace ID 생성)                  │
│  - Logback + Logstash Encoder (JSON 로그)                   │
│  - Zipkin Reporter (분산 추적 데이터 전송)                    │
└─────────────────────────────────────────────────────────────┘
                    ↓ (JSON Logs)        ↓ (Traces)
        ┌───────────────────────┬──────────────────┐
        ↓                       ↓                  ↓
┌──────────────┐    ┌──────────────────┐   ┌─────────────┐
│  Filebeat    │    │  Elasticsearch   │   │   Zipkin    │
│ (로그 수집)   │───→│   (로그 저장)     │←──│ (추적 저장) │
└──────────────┘    └──────────────────┘   └─────────────┘
                             ↓
                    ┌──────────────────┐
                    │     Kibana       │
                    │  (시각화/검색)    │
                    └──────────────────┘
```

## 주요 컴포넌트

### 1. Spring Cloud Sleuth
- **역할**: 자동 분산 추적 (Distributed Tracing)
- **기능**:
  - HTTP 요청마다 고유한 Trace ID 생성
  - 서비스 간 Trace ID 전파
  - Span ID로 각 작업 단위 추적

### 2. Zipkin
- **역할**: 분산 추적 시각화
- **URL**: http://localhost:9411
- **기능**:
  - 서비스 간 호출 관계 시각화
  - 응답 시간 분석
  - 병목 구간 식별

### 3. Elasticsearch
- **역할**: 로그 저장 및 검색 엔진
- **URL**: http://localhost:9200
- **기능**:
  - JSON 형식 로그 저장
  - 빠른 전문 검색
  - 인덱스 기반 로그 관리

### 4. Kibana
- **역할**: 로그 시각화 및 대시보드
- **URL**: http://localhost:5601
- **기능**:
  - 로그 검색 및 필터링
  - 실시간 대시보드
  - 커스텀 시각화

### 5. Filebeat
- **역할**: Docker 컨테이너 로그 수집
- **기능**:
  - 컨테이너 stdout/stderr 수집
  - JSON 로그 파싱
  - Elasticsearch로 전송

## 설치 및 실행

### 1. 네트워크 생성

```bash
docker network create mini-market-network
```

### 2. 모니터링 스택 실행

```bash
# 모니터링 인프라 시작
docker-compose -f docker-compose.monitoring.yml up -d

# 상태 확인
docker-compose -f docker-compose.monitoring.yml ps
```

**대기 시간**: Elasticsearch와 Kibana가 완전히 시작되려면 1-2분 정도 소요됩니다.

### 3. 마이크로서비스 실행

```bash
# 각 서비스를 개별적으로 실행
cd account-service && ./gradlew bootRun
cd catalog-service && ./gradlew bootRun
cd order-service && ./gradlew bootRun
cd payment-service && ./gradlew bootRun
cd shipping-service && ./gradlew bootRun
```

또는 Docker로 실행:

```bash
# 각 서비스의 docker-compose 실행
cd account-service && docker-compose up -d
cd catalog-service && docker-compose up -d
cd order-service && docker-compose up -d
cd payment-service && docker-compose up -d
cd shipping-service && docker-compose up -d
```

### 4. 서비스 상태 확인

```bash
# Elasticsearch 상태
curl http://localhost:9200/_cluster/health

# Kibana 상태
curl http://localhost:5601/api/status

# Zipkin 상태
curl http://localhost:9411/health
```

## Kibana 설정

### 1. 인덱스 패턴 생성

1. Kibana 접속: http://localhost:5601
2. 좌측 메뉴에서 **Management** → **Stack Management** 선택
3. **Kibana** → **Index Patterns** 선택
4. **Create index pattern** 클릭
5. Index pattern: `mini-market-logs-*` 입력
6. Time field: `@timestamp` 선택
7. **Create index pattern** 클릭

### 2. Discover에서 로그 확인

1. 좌측 메뉴에서 **Discover** 선택
2. 상단에서 시간 범위 설정 (예: Last 15 minutes)
3. 검색 바에서 KQL 쿼리 사용

## 로그 검색 예시

### Trace ID로 전체 이벤트 체인 조회

```kql
trace_id: "abc123def456"
```

### 특정 서비스의 에러 로그만 조회

```kql
service_name: "order-service" AND level: "ERROR"
```

### 특정 이벤트 타입 조회

```kql
event: "ORDER_CREATED"
```

### 주문 ID로 관련 로그 조회

```kql
orderId: "123456"
```

### 시간 범위 + 서비스 + 레벨

```kql
service_name: "payment-service" AND level: ("ERROR" OR "WARN") AND @timestamp >= "2024-03-04T10:00:00"
```

## Zipkin 사용법

### 1. 트레이스 조회

1. Zipkin 접속: http://localhost:9411
2. **Find a trace** 화면에서 조건 설정:
   - Service Name: 서비스 선택 (예: order-service)
   - Time Range: 시간 범위 선택
3. **Run Query** 클릭

### 2. 트레이스 상세 분석

- 각 Span 클릭 시 상세 정보 확인
- 서비스 간 호출 시간 확인
- 병목 구간 식별

### 3. Dependency Graph

- 상단 메뉴에서 **Dependencies** 선택
- 서비스 간 의존성 그래프 확인

## 이벤트 흐름 추적 예시

### 주문 생성부터 완료까지 추적

1. **주문 생성 API 호출**
   ```bash
   curl -X POST http://localhost:8082/api/orders \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -d '{
       "orderLines": [
         {"productId": 1, "quantity": 2}
       ],
       "shippingInfo": {
         "receiverName": "홍길동",
         "address": "서울시 강남구",
         "detailAddress": "101호"
       }
     }'
   ```

2. **응답에서 Trace ID 확인**
   - 응답 헤더 또는 로그에서 Trace ID 확인

3. **Kibana에서 Trace ID로 검색**
   ```kql
   trace_id: "YOUR_TRACE_ID"
   ```

4. **이벤트 타임라인 확인**
   ```
   [10:00:00.100] order-service: ORDER_CREATED (orderId=123)
   [10:00:00.250] catalog-service: Reserving inventory
   [10:00:00.400] catalog-service: INVENTORY_RESERVED
   [10:00:00.500] order-service: ORDER_RESERVED
   [10:00:00.650] payment-service: Creating payment
   [10:00:01.000] payment-service: PAYMENT_PROCESSED
   [10:00:01.100] order-service: Order status updated to PAID
   [10:00:01.200] shipping-service: SHIPPING_CREATED
   [10:00:01.300] order-service: Order status updated to SHIPPED
   ```

5. **Zipkin에서 시각화 확인**
   - http://localhost:9411에서 Trace ID 검색
   - 서비스 간 호출 흐름 및 소요 시간 확인

## 대시보드 구성 예시

### 1. 서비스별 로그 볼륨 대시보드

**시각화 1: 서비스별 로그 카운트 (Pie Chart)**
- Aggregation: Count
- Bucket: Terms on `service_name.keyword`

**시각화 2: 시간별 로그 트렌드 (Line Chart)**
- Y-axis: Count
- X-axis: Date Histogram on `@timestamp`
- Split Series: Terms on `service_name.keyword`

**시각화 3: 로그 레벨 분포 (Bar Chart)**
- Y-axis: Count
- X-axis: Terms on `level.keyword`

### 2. 에러 모니터링 대시보드

**시각화 1: 최근 에러 로그 (Table)**
- Filters: `level: "ERROR"`
- Columns: `@timestamp`, `service_name`, `message`, `trace_id`

**시각화 2: 에러 발생률 (Metric)**
- Filter: `level: "ERROR"`
- Metric: Count
- Time Range: Last 1 hour

**시각화 3: 서비스별 에러 카운트 (Bar Chart)**
- Filter: `level: "ERROR"`
- Y-axis: Count
- X-axis: Terms on `service_name.keyword`

### 3. 이벤트 흐름 대시보드

**시각화 1: 이벤트 타입 분포 (Pie Chart)**
- Bucket: Terms on `event.keyword`

**시각화 2: 이벤트 타임라인 (Table)**
- Columns: `@timestamp`, `service_name`, `event`, `trace_id`, `message`
- Sort: `@timestamp` descending

## 코드에서 구조화된 로깅 사용

### 1. 기본 로깅

```kotlin
import com.minimart.common.logging.StructuredLogger
import com.minimart.common.logging.LogLevel
import org.slf4j.LoggerFactory

@Service
class OrderService(
    private val structuredLogger: StructuredLogger
) {
    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    fun createOrder(request: CreateOrderRequest): Order {
        structuredLogger.logEvent(
            logger = logger,
            level = LogLevel.INFO,
            eventName = "ORDER_CREATION_STARTED",
            message = "Starting order creation",
            additionalFields = mapOf(
                "userId" to request.userId,
                "itemCount" to request.orderLines.size,
                "totalAmount" to request.totalAmount
            )
        )

        // 주문 생성 로직...

        structuredLogger.logEvent(
            logger = logger,
            level = LogLevel.INFO,
            eventName = "ORDER_CREATION_COMPLETED",
            message = "Order created successfully",
            additionalFields = mapOf(
                "orderId" to order.id,
                "userId" to request.userId
            )
        )

        return order
    }
}
```

### 2. 에러 로깅

```kotlin
try {
    // 비즈니스 로직
} catch (e: Exception) {
    structuredLogger.logEventWithThrowable(
        logger = logger,
        level = LogLevel.ERROR,
        eventName = "ORDER_CREATION_FAILED",
        message = "Failed to create order",
        throwable = e,
        additionalFields = mapOf(
            "userId" to request.userId,
            "reason" to e.message
        )
    )
    throw e
}
```

### 3. Trace Context 사용

```kotlin
import com.minimart.common.logging.TraceContextHolder
import com.minimart.common.logging.withTraceId

@Service
class OrderService(
    private val traceContextHolder: TraceContextHolder
) {
    fun processOrder(orderId: String) {
        traceContextHolder.withTraceId {
            // 이 블록 내의 모든 로그는 동일한 Trace ID를 공유
            logger.info("Processing order: $orderId")

            // 다른 서비스 호출...

            logger.info("Order processed: $orderId")
        }
    }
}
```

### 4. 이벤트 발행 시 Trace ID 전파

```kotlin
@Service
class OrderCreateService(
    private val traceContextHolder: TraceContextHolder,
    private val eventPublisher: EventPublisher
) {
    fun createOrder(request: CreateOrderRequest): Order {
        val traceId = traceContextHolder.getCurrentTraceId()

        // 주문 생성...

        eventPublisher.publish(
            EventTopic.ORDER_CREATED,
            OrderCreated(
                orderId = order.id,
                buyerId = request.buyerId,
                totalAmount = order.totalAmount,
                orderLines = orderLines,
                traceId = traceId  // Trace ID 전파
            )
        )

        return order
    }
}
```

### 5. 이벤트 수신 시 Trace ID 설정

```kotlin
@Component
class OrderEventListener(
    private val traceContextHolder: TraceContextHolder
) {
    @KafkaListener(topics = [EventTopic.ORDER_CREATED])
    fun handleOrderCreated(event: OrderCreated) {
        // Trace ID 설정
        event.traceId?.let { traceContextHolder.setTraceId(it) }

        try {
            logger.info("Received ORDER_CREATED event: ${event.orderId}")

            // 비즈니스 로직...

        } finally {
            traceContextHolder.clearTraceId()
        }
    }
}
```

## 프로덕션 설정 권장사항

### 1. Sleuth 샘플링 비율 조정

```yaml
# application-prod.yml
spring:
  sleuth:
    sampler:
      probability: 0.1  # 10%만 추적 (성능 최적화)
```

### 2. 로그 레벨 조정

```yaml
# application-prod.yml
logging:
  level:
    root: INFO
    com.minimart: INFO
    org.hibernate: WARN
```

### 3. Logback 프로파일 사용

로그백 설정에서 `prod` 프로파일을 사용하면 JSON 로그와 파일 로깅이 활성화됩니다:

```bash
java -jar app.jar --spring.profiles.active=prod
```

### 4. Elasticsearch 인덱스 관리

```bash
# 오래된 인덱스 삭제 (30일 이상)
curl -X DELETE "localhost:9200/mini-market-logs-2024.01.*"

# 인덱스 목록 확인
curl "localhost:9200/_cat/indices/mini-market-logs-*?v"
```

## 트러블슈팅

### Elasticsearch 연결 실패

```bash
# Elasticsearch 로그 확인
docker logs mini-market-elasticsearch

# 포트 확인
netstat -an | grep 9200
```

### Zipkin에 트레이스가 표시되지 않음

1. 서비스에서 Zipkin 연결 확인:
   ```bash
   curl http://localhost:9411/api/v2/services
   ```

2. application.yml에서 Zipkin URL 확인:
   ```yaml
   spring:
     zipkin:
       base-url: http://localhost:9411
   ```

### Kibana에서 로그가 보이지 않음

1. Filebeat 로그 확인:
   ```bash
   docker logs mini-market-filebeat
   ```

2. Elasticsearch에 인덱스가 생성되었는지 확인:
   ```bash
   curl "localhost:9200/_cat/indices?v"
   ```

3. Kibana 인덱스 패턴 재생성

### 로그가 JSON 형식으로 출력되지 않음

1. Logback 설정 파일 위치 확인:
   ```
   common/src/main/resources/logback-spring.xml
   ```

2. 프로파일 확인:
   ```bash
   # local/dev 프로파일: 사람이 읽기 쉬운 형식
   # prod 또는 기본: JSON 형식
   ```

## 성능 고려사항

### 1. 로그 볼륨 관리

- 불필요한 DEBUG 로그 제거
- 대용량 데이터는 로그에 포함하지 않음
- 로그 레벨을 적절히 설정

### 2. Trace 샘플링

- 프로덕션에서는 샘플링 비율을 낮춤 (0.1 = 10%)
- 중요한 엔드포인트만 항상 추적

### 3. Elasticsearch 리소스

- 메모리: 최소 2GB (프로덕션: 4GB 이상)
- 디스크: 로그 보관 정책에 따라 조정
- 샤드 수 최적화

## 추가 리소스

- [Spring Cloud Sleuth 문서](https://spring.io/projects/spring-cloud-sleuth)
- [Zipkin 문서](https://zipkin.io/)
- [Elasticsearch 가이드](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Kibana 사용자 가이드](https://www.elastic.co/guide/en/kibana/current/index.html)
- [Filebeat 문서](https://www.elastic.co/guide/en/beats/filebeat/current/index.html)

## 문의 및 지원

모니터링 시스템 관련 문의사항은 프로젝트 이슈 트래커를 통해 문의해주세요.