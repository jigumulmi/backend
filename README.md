# [JIGUMULMI(지구멀미)](https://www.jigumulmi.com/)
'지구멀미'는 채식 정보 공유 서비스로, 지구의 빠른 변화 속도를 늦추기 위해 우리의 식생활부터 변화를 시작하자는 취지에서 개발되었습니다.

사용자가 환경에 더 가까워질 수 있는 방법을 찾고, 일상에서 지구를 생각하며 잠시 쉬어갈 수 있는 공간을 제공합니다. 또한, 동식물의 입장에서 생각해보거나 같은 관심사를 가진 사람들과 소통할 수 있는 기회를 제공합니다.

더 자세한 내용은 [지구멀미 소개 페이지](https://developerjoseph.notion.site/dfea728380654c84b4b898b63e411707)에서 확인하실 수 있습니다.

## 인프라 아키텍처
![jigumulmi architecture.png](..%2F..%2FDownloads%2Fjigumulmi%20architecture.png)

## 코드 아키텍처
> [Manager 계층 도입 배경](https://developerjoseph.notion.site/19c519c54f54803fbc94eecba5d173da?pvs=74)
- **Controller**: 클라이언트 요청 및 응답 핸들링
  - 요청 데이터 검증 및 적절한 형태로 서비스 계층에 전달
  - Swagger 문서화
- **Service**: 비즈니스 로직 수행
  - 서비스가 수행하고자 하는 "주 관심사"의 흐름을 이해하기 쉽게 표현하는 계층
- **Manager**: 비즈니스 로직의 세부 구현
  - 서비스 계층의 부품이 되어 여러 도메인의 비즈니스 로직에 재사용되는 계층
- **Repository**: Data Access Object (DAO)
  - 다양한 데이터 자원(주로 RDB)에 접근하여 처리하는 계층

## 브랜치 전략
![gitflow drawio](https://github.com/user-attachments/assets/985708a6-f7dd-4d7b-be58-7db9d65e710c)

> 브랜치 보호 전략으로 상위 브랜치에 커밋을 추가하기 위해서는 PR 제출 및 Status Check 필수
- **main**: 상용 서비스에 배포되는 최종 브랜치
- **hotfix**: 상용 서비스에서 발견된 치명적인 버그나 즉시 해결이 필요한 이슈를 처리하는 브랜치
- **dev**: 개발 단계에서 테스트하고 다듬어진 변경 사항들이 집합된 브랜치
- **feat**: 신규 기능 개발을 위해 독립적으로 작업되는 브랜치

### CI/CD
> [Doppler](https://www.doppler.com/): 프로젝트 시크릿 데이터 관리
- path 기반 워크플로우 트리거 조건 설정
- dev 브랜치
  - PR 제출 -> 단위 테스트 실행
  - Push -> 개발 환경의 백엔드 애플리케이션 혹은 모니터링 시스템 배포
- main 브랜치
  - Push -> 상용 환경의 백엔드 애플리케이션 혹은 모니터링 시스템 배포

## 개발 일지
> 트러블 슈팅 및 학습 기록
### [노션 페이지](https://developerjoseph.notion.site/6aec3d72c3d641c4a98ba4a55d069536?v=f9513eb4d2644809927f5ab0ca5236ff)

## 기술 스택
- `Java21, SpringBoot3, Spring Security, Springdoc`
- `JPA, QueryDSL`
- `JUnit5, Mockito`
- `Docker, FluentBit, Loki, Prometheus, Grafana`
- `AWS`
  - `EC2, Lambda, RDS MySQL, S3`
  - `Route53, Cloudfront`
- `Github Actions, Doppler`

## 주요 기능
### 일반 사용자
- 카카오 OAuth
- 비건 식당 큐레이팅 배너 목록 조회
- 배너별 장소 목록 조회
- 장소별 세부 정보 조회
- 장소별 리뷰 CRUD

### 지구멀미 관리자
- 사용자, 장소, 배너 등 비즈니스 자원을 관리 감독할 수 있는 ADMIN 기능

### 사장님(TODO...)
- 

## 개발 기간 및 구성 인원
2024.03.29 ~

마케팅1, 기획1, 프론트엔드1, 백엔드1
