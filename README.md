# Mini-Market - 이벤트 기반 마이크로서비스 전자상거래 플랫폼

## 개요

Mini-Market는 Kotlin과 Spring Boot로 구축된 이벤트 기반 마이크로서비스 아키텍처의 전자상거래 플랫폼입니다.

## 아키텍처

### 마이크로서비스
- **Account Service** (8080): 사용자 계정 및 인증 관리
- **Catalog Service** (8081): 상품 및 재고 관리
- **Order Service** (8082): 주문 처리 및 오케스트레이션
- **Payment Service** (8083): 결제 처리
- **Shipping Service** (8084): 배송 관리

### 기술 스택
- **언어**: Kotlin 2.2.21
- **프레임워크**: Spring Boot 4.0.2
- **데이터베이스**: MySQL 8.0 (서비스별 독립 DB)
- **캐시**: Redis (Account, Catalog)
- **메시징**: Apache Kafka 7.6.0
- **분산 추적**: Spring Cloud Sleuth + Zipkin
- **로깅**: ELK Stack (Elasticsearch, Logstash/Filebeat, Kibana)
- **보안**: JWT (JJWT)

## 빠른 시작

### 사전 준비
- Docker Engine 20.10+
- Docker Compose V2+
- 최소 8GB RAM

### 1. 전체 시스템 한 번에 실행 (권장)

```bash
# 통합 스크립트 사용
./scripts/start-all.sh
```

이 스크립트는 다음 순서로 자동 실행합니다:
1. 데이터베이스 및 캐시 (MySQL, Redis)
2. 이벤트 인프라 (Kafka)
3. 마이크로서비스 (5개 서비스)
4. 모니터링 스택 (선택사항)

### 2. 개별 레이어 실행

필요한 레이어만 선택적으로 실행할 수 있습니다:

```bash
# 1. 데이터베이스 & 캐시만
docker-compose -f docker-compose.db.yml up -d

# 2. 이벤트 인프라만
docker-compose -f docker-compose.event.yml up -d

# 3. 마이크로서비스만
docker-compose -f docker-compose.service.yml up --build -d

# 4. 모니터링 스택만
docker-compose -f docker-compose.monitoring.yml up -d
```

### 3. 시스템 중지

```bash
# 전체 중지
./scripts/stop-all.sh

# 또는 개별 중지
docker-compose -f docker-compose.service.yml down
docker-compose -f docker-compose.event.yml down
docker-compose -f docker-compose.db.yml down
docker-compose -f docker-compose.monitoring.yml down
```

### 3. 접속 URL

**애플리케이션**
- Account Service: http://localhost:8080
- Catalog Service: http://localhost:8081
- Order Service: http://localhost:8082
- Payment Service: http://localhost:8083
- Shipping Service: http://localhost:8084

**인프라**
- Kafka UI: http://localhost:8989
- Zipkin (분산 추적): http://localhost:9411
- Kibana (로그): http://localhost:5601
- Elasticsearch: http://localhost:9200

## 이벤트 흐름

주문 생성부터 완료까지의 이벤트 체인:

```
1. ORDER_CREATED (Order Service)
   ↓
2. INVENTORY_RESERVED (Catalog Service)
   ↓
3. ORDER_RESERVED (Order Service)
   ↓
4. PAYMENT_PROCESSED (Payment Service)
   ↓
5. SHIPPING_CREATED (Shipping Service)
   ↓
6. ORDER_SHIPPED (Order Service) ✓
```

실패 시 보상 트랜잭션:
- INVENTORY_FAILED → ORDER_FAILED
- PAYMENT_FAILED → ORDER_FAILED

## 주요 기능

### 1. 분산 추적 (Distributed Tracing)
- 단일 Trace ID로 전체 마이크로서비스 체인 추적
- Zipkin UI에서 시각화
- 병목 구간 식별

### 2. 중앙 집중식 로깅
- JSON 형식 구조화 로그
- Elasticsearch 저장
- Kibana 대시보드

### 3. 이벤트 기반 아키텍처
- Kafka를 통한 비동기 통신
- Saga 패턴으로 분산 트랜잭션 처리
- Outbox 패턴으로 안정적 이벤트 발행

### 4. 서비스별 독립 데이터베이스
- Database per Service 패턴
- 각 서비스의 데이터 자율성 보장

## 개발 가이드

### 로컬 개발 환경

```bash
# 인프라만 Docker로 실행 (DB, Redis, Kafka)
docker-compose -f docker-compose.db.yml up -d
docker-compose -f docker-compose.event.yml up -d

# IDE에서 각 서비스 실행
# application.yml의 localhost 설정 사용
```

### 특정 서비스만 재시작

```bash
# 편리한 스크립트 사용
./scripts/restart-service.sh account

# 또는 직접 명령
docker-compose -f docker-compose.service.yml build account-service
docker-compose -f docker-compose.service.yml up -d account-service

# 로그 확인
docker logs -f mini-market-account-service
```

## 문서

- **[DOCKER_GUIDE.md](./DOCKER_GUIDE.md)**: Docker 실행 및 관리 상세 가이드
- **[MONITORING_GUIDE.md](./MONITORING_GUIDE.md)**: 모니터링 시스템 사용 가이드
- **[KAFKA_GUIDE.md](./KAFKA_GUIDE.md)**: Kafka 이벤트 가이드

## 프로젝트 구조

```
mini-market/
├── account-service/          # 계정 서비스
├── catalog-service/          # 카탈로그 서비스
├── order-service/            # 주문 서비스
├── payment-service/          # 결제 서비스
├── shipping-service/         # 배송 서비스
├── common/                   # 공통 모듈
│   ├── event/               # 이벤트 DTO
│   ├── logging/             # 로깅 유틸
│   └── security/            # 보안 설정
├── monitoring/
│   └── filebeat/            # Filebeat 설정
├── scripts/                  # 실행 스크립트
│   ├── start-all.sh         # 전체 시스템 시작
│   ├── stop-all.sh          # 전체 시스템 중지
│   ├── clean-all.sh         # 전체 정리
│   └── restart-service.sh   # 서비스 재시작
├── docker-compose.db.yml         # 데이터베이스 & 캐시
├── docker-compose.event.yml      # 이벤트 인프라 (Kafka)
├── docker-compose.service.yml    # 마이크로서비스
└── docker-compose.monitoring.yml # 모니터링 스택
```

### Docker Compose 파일 구성

| 파일 | 용도 | 포함 서비스 |
|------|------|------------|
| `docker-compose.db.yml` | 데이터베이스 & 캐시 | MySQL (5개), Redis (2개) |
| `docker-compose.event.yml` | 이벤트 인프라 | Kafka, Kafka UI |
| `docker-compose.service.yml` | 마이크로서비스 | 5개 Spring Boot 서비스 |
| `docker-compose.monitoring.yml` | 모니터링 | ELK Stack, Zipkin |

## API 테스트 예시

### 1. 회원가입

```bash
curl -X POST http://localhost:8080/api/accounts/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "홍길동"
  }'
```

### 2. 로그인

```bash
curl -X POST http://localhost:8080/api/accounts/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 3. 주문 생성

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "orderLines": [
      {
        "productId": 1,
        "quantity": 2
      }
    ],
    "shippingInfo": {
      "receiverName": "홍길동",
      "address": "서울시 강남구",
      "detailAddress": "101호"
    }
  }'
```

### 4. Trace 확인

주문 생성 후:
1. Zipkin: http://localhost:9411 에서 Trace 검색
2. Kibana: http://localhost:5601 에서 로그 검색

## 트러블슈팅

### 컨테이너 시작 실패

```bash
# 로그 확인
docker logs mini-market-account-service

# DB 상태 확인
docker-compose -f docker-compose.services.yml ps

# 재시작
docker-compose -f docker-compose.services.yml restart account-service
```

### 네트워크 문제

```bash
# 네트워크 확인
docker network inspect mini-market-network

# 재생성
docker-compose -f docker-compose.services.yml down
docker network rm mini-market-network
docker network create mini-market-network
docker-compose -f docker-compose.services.yml up -d
```

### 전체 정리

```bash
# 편리한 스크립트 사용 (권장)
./scripts/clean-all.sh

# 또는 개별 정리
docker-compose -f docker-compose.service.yml down -v --rmi all
docker-compose -f docker-compose.monitoring.yml down -v --rmi all
docker-compose -f docker-compose.event.yml down -v --rmi all
docker-compose -f docker-compose.db.yml down -v --rmi all
```

## 성능 고려사항

### 리소스 할당
- **Account Service**: 256MB-512MB
- **Catalog Service**: 256MB-512MB (Redis 캐싱)
- **Order Service**: 256MB-512MB
- **Payment Service**: 256MB-512MB
- **Shipping Service**: 256MB-512MB
- **MySQL (각)**: 최소 512MB
- **Kafka**: 최소 1GB
- **Elasticsearch**: 최소 2GB

### 프로덕션 최적화
1. Sleuth 샘플링 비율 조정: `probability: 0.1`
2. 로그 레벨 조정: `INFO` 이상
3. DB 커넥션 풀 최적화
4. Redis 메모리 정책 설정
5. Kafka 파티션 수 증가
