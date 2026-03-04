#!/bin/bash

# Mini-Market 전체 시스템 정리 스크립트
# 컨테이너, 볼륨, 이미지 모두 제거

set -e

echo "========================================="
echo "⚠️  Mini-Market 전체 시스템 정리"
echo "========================================="
echo ""
echo "경고: 이 작업은 다음을 삭제합니다:"
echo "  - 모든 컨테이너"
echo "  - 모든 볼륨 (데이터베이스 데이터 포함)"
echo "  - 모든 빌드된 이미지"
echo "  - 네트워크"
echo ""

read -p "정말로 계속하시겠습니까? (yes 입력): " -r
echo

if [[ ! $REPLY == "yes" ]]; then
    echo "취소되었습니다."
    exit 0
fi

# 현재 디렉토리 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/.."

cd "$PROJECT_ROOT"

echo "1️⃣  컨테이너, 볼륨, 이미지 제거 중..."
docker-compose -f docker-compose.service.yml down -v --rmi all
docker-compose -f docker-compose.monitoring.yml down -v --rmi all
docker-compose -f docker-compose.event.yml down -v --rmi all
docker-compose -f docker-compose.db.yml down -v --rmi all

echo ""
echo "2️⃣  네트워크 제거 중..."
docker network rm mini-market-network 2>/dev/null || echo "   네트워크가 이미 제거되었습니다."

echo ""
echo "3️⃣  사용하지 않는 Docker 리소스 정리 중..."
docker system prune -f

echo ""
echo "========================================="
echo "✅ 모든 리소스가 정리되었습니다!"
echo "========================================="
echo ""
echo "💡 다시 시작: ./scripts/start-all.sh"
echo ""