#!/bin/bash

# Mini-Market 전체 시스템 중지 스크립트

set -e

echo "========================================="
echo "Mini-Market 전체 시스템 중지"
echo "========================================="
echo ""

# 현재 디렉토리 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/.."

cd "$PROJECT_ROOT"

# 중지 순서: Service → Monitoring → Event → DB (역순)

echo "1️⃣  마이크로서비스 중지 중..."
docker-compose -f docker-compose.service.yml down

echo ""
echo "2️⃣  모니터링 스택 중지 중..."
docker-compose -f docker-compose.monitoring.yml down

echo ""
echo "3️⃣  이벤트 인프라 중지 중..."
docker-compose -f docker-compose.event.yml down

echo ""
echo "4️⃣  데이터베이스 및 캐시 중지 중..."
docker-compose -f docker-compose.db.yml down

echo ""
echo "========================================="
echo "✅ 모든 서비스가 중지되었습니다!"
echo "========================================="
echo ""
echo "💡 데이터 삭제: ./scripts/clean-all.sh"
echo ""