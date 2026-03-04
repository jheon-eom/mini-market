#!/bin/bash

# 특정 마이크로서비스만 재시작하는 스크립트

set -e

if [ -z "$1" ]; then
    echo "사용법: ./restart-service.sh <service-name>"
    echo ""
    echo "사용 가능한 서비스:"
    echo "  - account"
    echo "  - catalog"
    echo "  - order"
    echo "  - payment"
    echo "  - shipping"
    exit 1
fi

SERVICE=$1

# 현재 디렉토리 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/.."

cd "$PROJECT_ROOT"

echo "========================================="
echo "${SERVICE}-service 재시작"
echo "========================================="
echo ""

echo "1️⃣  ${SERVICE}-service 빌드 중..."
docker-compose -f docker-compose.service.yml build ${SERVICE}-service

echo ""
echo "2️⃣  ${SERVICE}-service 재시작 중..."
docker-compose -f docker-compose.service.yml up -d ${SERVICE}-service

echo ""
echo "3️⃣  로그 확인 (Ctrl+C로 종료)..."
sleep 2
docker-compose -f docker-compose.service.yml logs -f ${SERVICE}-service