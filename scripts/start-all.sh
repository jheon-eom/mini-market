#!/bin/bash

# Mini-Market 전체 시스템 시작 스크립트
# 실행 순서: DB → Event → Service → Monitoring

set -e

echo "========================================="
echo "Mini-Market 전체 시스템 시작"
echo "========================================="
echo ""

# 현재 디렉토리 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/.."

cd "$PROJECT_ROOT"

# 1. Database & Cache 시작
echo "1️⃣  데이터베이스 및 캐시 시작 중..."
docker-compose -f docker-compose.db.yml up -d

echo "   ⏳ DB가 준비될 때까지 대기 중..."
sleep 20

# 2. Event Infrastructure 시작
echo ""
echo "2️⃣  이벤트 인프라 (Kafka) 시작 중..."
docker-compose -f docker-compose.event.yml up -d

echo "   ⏳ Kafka가 준비될 때까지 대기 중..."
sleep 15

# 3. Microservices 시작
echo ""
echo "3️⃣  마이크로서비스 빌드 및 시작 중..."
docker-compose -f docker-compose.service.yml up --build -d

echo "   ⏳ 서비스가 시작될 때까지 대기 중..."
sleep 30

# 4. Monitoring Stack 시작 (선택사항)
read -p "4️⃣  모니터링 스택을 시작하시겠습니까? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "   모니터링 스택 시작 중..."
    docker-compose -f docker-compose.monitoring.yml up -d
    echo "   ⏳ 모니터링 스택이 준비될 때까지 대기 중..."
    sleep 20
fi

echo ""
echo "========================================="
echo "✅ 모든 서비스가 시작되었습니다!"
echo "========================================="
echo ""

# 상태 확인
echo "📊 실행 중인 컨테이너:"
echo ""
docker ps --filter "name=mini-market" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "========================================="
echo "🌐 접속 URL"
echo "========================================="
echo ""
echo "【 애플리케이션 서비스 】"
echo "  Account Service:  http://localhost:8080"
echo "  Catalog Service:  http://localhost:8081"
echo "  Order Service:    http://localhost:8082"
echo "  Payment Service:  http://localhost:8083"
echo "  Shipping Service: http://localhost:8084"
echo ""
echo "【 인프라 서비스 】"
echo "  Kafka UI:         http://localhost:8989"
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
echo "【 모니터링 서비스 】"
echo "  Kibana:           http://localhost:5601"
echo "  Zipkin:           http://localhost:9411"
echo "  Elasticsearch:    http://localhost:9200"
echo ""
fi
echo "========================================="
echo ""
echo "💡 로그 확인: docker-compose -f docker-compose.service.yml logs -f"
echo "💡 중지: ./scripts/stop-all.sh"
echo ""