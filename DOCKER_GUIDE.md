# Mini-Market Docker 실행 가이드

## 개요

이 문서는 Mini-Market 프로젝트를 Docker 컨테이너로 실행하는 방법을 설명합니다.

## 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                     Docker Network                       │
│                  mini-market-network                     │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Account    │  │   Catalog    │  │    Order     │ │
│  │   Service    │  │   Service    │  │   Service    │ │
│  │   :8080      │  │   :8081      │  │   :8082      │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘ │
│         │                 │                  │          │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐ │
│  │   Payment    │  │   Shipping   │  │    Kafka     │ │
│  │   Service    │  │   Service    │  │   :9092      │ │
│  │   :8083      │  │   :8084      │  │   :29092     │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘ │
│         │                 │                  │          │
│  ┌──────▼─────────────────▼─────────────────▼───────┐ │
│  │              MySQL Databases (5개)               │ │
│  │   Redis (account, catalog)                      │ │
│  └─────────────────────────────────────────────────┘ │
│                                                        │
│  ┌─────────────────────────────────────────────────┐ │
│  │  Monitoring Stack (별도)                         │ │
│  │  - Elasticsearch  - Kibana  - Zipkin           │ │
│  └─────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

## 사전 준비

### 1. 필수 소프트웨어

- Docker Engine 20.10 이상
- Docker Compose V2 이상
- 최소 8GB RAM 권장
- 최소 20GB 디스크 공간

### 2. 네트워크 생성

모든 컨테이너가 통신할 수 있도록 Docker 네트워크를 생성합니다:

```bash
docker network create mini-market-network
```

## 실행 방법

### 옵션 1: 전체 시스템 한 번에 실행 (권장)

모든 서비스와 인프라를 한 번에 실행:

```bash
# 전체 빌드 및 실행
docker-compose -f docker-compose.services.yml up --build -d

# 로그 확인
docker-compose -f docker-compose.services.yml logs -f

# 특정 서비스 로그만 확인
docker-compose -f docker-compose.services.yml logs -f account-service
```

### 옵션 2: 단계별 실행

#### Step 1: 인프라 서비스 먼저 실행

```bash
# Kafka, MySQL, Redis 실행
docker-compose -f docker-compose.services.yml up -d kafka kafka-ui \
  account-db account-redis \
  catalog-db catalog-redis \
  order-db payment-db shipping-db

# 상태 확인 (모든 서비스가 healthy 될 때까지 대기)
docker-compose -f docker-compose.services.yml ps
```

#### Step 2: 애플리케이션 서비스 실행

```bash
# 모든 마이크로서비스 빌드 및 실행
docker-compose -f docker-compose.services.yml up --build -d \
  account-service catalog-service order-service \
  payment-service shipping-service
```

#### Step 3: 모니터링 스택 실행 (선택사항)

```bash
docker-compose -f docker-compose.monitoring.yml up -d
```

### 옵션 3: 개별 서비스만 실행

특정 서비스만 실행하려면:

```bash
# Account 서비스와 의존성만 실행
docker-compose -f docker-compose.services.yml up -d \
  account-db account-redis kafka account-service

# Catalog 서비스와 의존성만 실행
docker-compose -f docker-compose.services.yml up -d \
  catalog-db catalog-redis kafka catalog-service
```

## 서비스 포트

### 애플리케이션 서비스
- **Account Service**: http://localhost:8080
- **Catalog Service**: http://localhost:8081
- **Order Service**: http://localhost:8082
- **Payment Service**: http://localhost:8083
- **Shipping Service**: http://localhost:8084

### 인프라 서비스
- **Kafka**: localhost:29092 (외부 접속)
- **Kafka UI**: http://localhost:8989
- **MySQL Databases**:
  - Account DB: localhost:3306
  - Catalog DB: localhost:3307
  - Order DB: localhost:3308
  - Payment DB: localhost:3309
  - Shipping DB: localhost:3310
- **Redis**:
  - Account Redis: localhost:6379
  - Catalog Redis: localhost:6380

### 모니터링 서비스
- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601
- **Zipkin**: http://localhost:9411

## 상태 확인

### 1. 컨테이너 상태 확인

```bash
# 모든 컨테이너 상태
docker-compose -f docker-compose.services.yml ps

# 특정 서비스 상태
docker ps | grep mini-market
```

### 2. Health Check 확인

각 서비스의 Health 엔드포인트:

```bash
# Account Service
curl http://localhost:8080/actuator/health

# Catalog Service
curl http://localhost:8081/actuator/health

# Order Service
curl http://localhost:8082/actuator/health

# Payment Service
curl http://localhost:8083/actuator/health

# Shipping Service
curl http://localhost:8084/actuator/health
```

예상 응답:
```json
{
  "status": "UP"
}
```

### 3. 로그 확인

```bash
# 모든 서비스 로그 (실시간)
docker-compose -f docker-compose.services.yml logs -f

# 특정 서비스 로그
docker logs -f mini-market-account-service
docker logs -f mini-market-order-service

# 최근 100줄만 확인
docker logs --tail 100 mini-market-account-service

# 에러 로그만 확인
docker logs mini-market-account-service 2>&1 | grep ERROR
```

## 빌드 및 재시작

### 전체 재빌드

코드 변경 후 전체 재빌드:

```bash
# 컨테이너 중지 및 제거
docker-compose -f docker-compose.services.yml down

# 이미지 삭제 (선택사항)
docker-compose -f docker-compose.services.yml down --rmi all

# 재빌드 및 실행
docker-compose -f docker-compose.services.yml up --build -d
```

### 특정 서비스만 재빌드

```bash
# Account 서비스만 재빌드
docker-compose -f docker-compose.services.yml up --build -d account-service

# Order 서비스만 재빌드
docker-compose -f docker-compose.services.yml up --build -d order-service
```

### 빌드 캐시 없이 완전 재빌드

```bash
docker-compose -f docker-compose.services.yml build --no-cache account-service
docker-compose -f docker-compose.services.yml up -d account-service
```

## 종료 및 정리

### 서비스 중지

```bash
# 모든 서비스 중지 (데이터 보존)
docker-compose -f docker-compose.services.yml stop

# 모든 서비스 중지 및 컨테이너 제거 (데이터 보존)
docker-compose -f docker-compose.services.yml down
```

### 완전 정리 (데이터 포함)

```bash
# 컨테이너, 볼륨, 이미지 모두 제거
docker-compose -f docker-compose.services.yml down -v --rmi all

# 네트워크 제거
docker network rm mini-market-network
```

### 특정 서비스만 재시작

```bash
# Account 서비스만 재시작
docker-compose -f docker-compose.services.yml restart account-service

# 여러 서비스 재시작
docker-compose -f docker-compose.services.yml restart account-service order-service
```

## 개발 워크플로우

### 1. 로컬 개발 + Docker 인프라

로컬에서 코드 개발 시 인프라만 Docker로 실행:

```bash
# 인프라만 실행
docker-compose -f docker-compose.services.yml up -d \
  kafka kafka-ui \
  account-db account-redis \
  catalog-db catalog-redis \
  order-db payment-db shipping-db

# IntelliJ/IDE에서 서비스 실행
# application.yml의 localhost 설정 사용
```

### 2. 빠른 테스트를 위한 핫 리로드

코드 변경 후 빠른 재시작:

```bash
# 변경된 서비스만 빌드 (캐시 활용)
docker-compose -f docker-compose.services.yml build account-service

# 재시작
docker-compose -f docker-compose.services.yml up -d account-service

# 로그 확인
docker logs -f mini-market-account-service
```

### 3. 디버깅

컨테이너 내부 접속:

```bash
# 컨테이너 쉘 접속
docker exec -it mini-market-account-service sh

# 환경 변수 확인
docker exec mini-market-account-service env

# 실행 중인 프로세스 확인
docker exec mini-market-account-service ps aux
```

## 트러블슈팅

### 1. 컨테이너가 시작되지 않음

**증상**: 컨테이너가 계속 재시작됨

**해결**:
```bash
# 로그 확인
docker logs mini-market-account-service

# 일반적인 원인:
# - DB 연결 실패: DB가 healthy한지 확인
# - 포트 충돌: 이미 사용 중인 포트가 없는지 확인
# - 메모리 부족: Docker Desktop 메모리 설정 확인
```

### 2. 빌드 실패

**증상**: Gradle 빌드 중 오류

**해결**:
```bash
# Gradle 캐시 정리
./gradlew clean

# Docker 빌드 캐시 삭제
docker builder prune -a

# 재빌드
docker-compose -f docker-compose.services.yml build --no-cache account-service
```

### 3. 네트워크 연결 문제

**증상**: 서비스 간 통신 실패

**해결**:
```bash
# 네트워크 확인
docker network ls
docker network inspect mini-market-network

# 컨테이너가 올바른 네트워크에 있는지 확인
docker inspect mini-market-account-service | grep NetworkMode

# 네트워크 재생성
docker network rm mini-market-network
docker network create mini-market-network
```

### 4. DB 연결 실패

**증상**: `Communications link failure`

**해결**:
```bash
# DB 컨테이너 상태 확인
docker-compose -f docker-compose.services.yml ps account-db

# DB가 healthy가 될 때까지 대기
watch -n 1 'docker-compose -f docker-compose.services.yml ps'

# DB 로그 확인
docker logs mini-market-account-db

# DB에 직접 연결 테스트
docker exec -it mini-market-account-db mysql -u account_user -p
```

### 5. 포트 충돌

**증상**: `port is already allocated`

**해결**:
```bash
# 포트 사용 확인
lsof -i :8080
netstat -an | grep 8080

# 사용 중인 프로세스 종료 또는 포트 변경
# docker-compose.services.yml에서 포트 매핑 변경
```

### 6. 디스크 공간 부족

**증상**: `no space left on device`

**해결**:
```bash
# 사용하지 않는 이미지/컨테이너/볼륨 정리
docker system prune -a --volumes

# 디스크 사용량 확인
docker system df
```

## 성능 최적화

### 1. 빌드 시간 단축

`.dockerignore` 파일 생성:

```
# Build outputs
build/
.gradle/
*.class

# IDE
.idea/
.vscode/

# Git
.git/

# Logs
*.log
logs/
```

### 2. 메모리 설정

각 서비스의 메모리를 최적화:

```yaml
# docker-compose.services.yml
services:
  account-service:
    environment:
      - JAVA_OPTS=-Xms256m -Xmx512m  # 필요에 따라 조정
```

### 3. 멀티스테이지 빌드 캐시 활용

Gradle dependencies를 먼저 다운로드하여 캐시 활용:

```dockerfile
# Dockerfile (이미 적용됨)
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon  # 의존성만 먼저 다운로드
COPY src ./src
RUN gradle build -x test --no-daemon  # 빠른 빌드
```

## 프로덕션 배포 시 고려사항

### 1. 환경 변수 관리

민감한 정보는 `.env` 파일로 관리:

```bash
# .env 파일 생성
cat > .env << EOF
MYSQL_ROOT_PASSWORD=secure_password_here
JWT_SECRET=your_jwt_secret_here
REDIS_PASSWORD=redis_password_here
EOF

# docker-compose에서 참조
docker-compose -f docker-compose.services.yml --env-file .env up -d
```

### 2. 리소스 제한

```yaml
services:
  account-service:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 512M
        reservations:
          cpus: '0.5'
          memory: 256M
```

### 3. 헬스체크 타임아웃 조정

프로덕션에서는 더 긴 타임아웃 설정:

```yaml
healthcheck:
  interval: 60s
  timeout: 10s
  retries: 5
  start_period: 120s
```

## 모니터링

### 1. 컨테이너 리소스 사용량

```bash
# 실시간 리소스 모니터링
docker stats

# 특정 컨테이너만
docker stats mini-market-account-service
```

### 2. 로그 집계

Filebeat가 자동으로 수집하지만, 수동 확인도 가능:

```bash
# JSON 로그 확인
docker logs mini-market-account-service 2>&1 | jq .

# Trace ID로 필터링
docker logs mini-market-account-service 2>&1 | grep "traceId"
```

### 3. 분산 추적

Zipkin UI에서 확인:
- http://localhost:9411

## 추가 명령어

### Docker Compose 유용한 명령어

```bash
# 실행 중인 서비스 목록
docker-compose -f docker-compose.services.yml ps

# 특정 서비스 스케일링 (여러 인스턴스 실행)
docker-compose -f docker-compose.services.yml up -d --scale account-service=3

# 설정 검증
docker-compose -f docker-compose.services.yml config

# 리소스 정보
docker-compose -f docker-compose.services.yml top
```

### 백업 및 복구

```bash
# 볼륨 백업
docker run --rm -v mini-market_account-db-data:/data \
  -v $(pwd):/backup alpine tar czf /backup/account-db-backup.tar.gz /data

# 볼륨 복구
docker run --rm -v mini-market_account-db-data:/data \
  -v $(pwd):/backup alpine tar xzf /backup/account-db-backup.tar.gz -C /
```

## 참고 자료

- [Docker Compose 문서](https://docs.docker.com/compose/)
- [Spring Boot Docker 가이드](https://spring.io/guides/topicals/spring-boot-docker)
- [MONITORING_GUIDE.md](./MONITORING_GUIDE.md) - 모니터링 시스템 가이드

## 문의 및 지원

Docker 실행 관련 문의사항은 프로젝트 이슈 트래커를 통해 문의해주세요.