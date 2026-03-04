# Order Service Docker 실행

이 서비스의 개별 docker-compose.yml 파일은 더 이상 사용하지 않습니다.

## 실행 방법

루트 디렉토리에서 통합 docker-compose 파일을 사용하세요:

```bash
# 프로젝트 루트로 이동
cd ..

# 전체 시스템 실행
docker-compose -f docker-compose.services.yml up -d

# Order 서비스만 실행
docker-compose -f docker-compose.services.yml up -d order-db order-service

# 로그 확인
docker-compose -f docker-compose.services.yml logs -f order-service
```

자세한 내용은 루트 디렉토리의 `DOCKER_GUIDE.md`를 참고하세요.