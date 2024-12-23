# [JIGUMULMI(지구멀미)](https://www.jigumulmi.com/)
'지구멀미'는 채식 정보 공유 서비스로, 지구의 빠른 변화 속도를 늦추기 위해 우리의 식생활부터 변화를 시작하자는 취지에서 개발되었습니다.

사용자가 환경에 더 가까워질 수 있는 방법을 찾고, 일상에서 지구를 생각하며 잠시 쉬어갈 수 있는 공간을 제공합니다. 또한, 동식물의 입장에서 생각해보거나 같은 관심사를 가진 사람들과 소통할 수 있는 기회를 제공합니다.

더 자세한 내용은 [지구멀미 소개 페이지](https://developerjoseph.notion.site/dfea728380654c84b4b898b63e411707)에서 확인하실 수 있습니다.

## 인프라 아키텍처
![jigumulmi architecture drawio](https://github.com/user-attachments/assets/bb18c5bb-19d7-4be8-b5d1-a0d43226c83a)

## 브랜치 전략
![gitflow drawio](https://github.com/user-attachments/assets/985708a6-f7dd-4d7b-be58-7db9d65e710c)

> 브랜치 보호 전략으로 상위 브랜치에 커밋을 추가하기 위해서는 PR 제출 및 Status Check 필수
- **main**: 상용 서비스에 배포되는 최종 브랜치
- **hotfix**: 상용 서비스에서 발견된 치명적인 버그나 즉시 해결이 필요한 이슈를 처리하는 브랜치
- **dev**: 개발 단계에서 테스트하고 다듬어진 변경 사항들이 집합된 브랜치
- **feat**: 신규 기능 개발을 위해 독립적으로 작업되는 브랜치

### CI/CD
> [Doppler](https://www.doppler.com/): 프로젝트 시크릿 데이터 관리
- dev 브랜치
  - PR 제출 -> 단위 테스트 실행
  - Push -> 개발 서버 배포
- main 브랜치
  - Push -> 상용 서버 배포

## 기술 스택
- `Java21`
- `SpringBoot3, Spring Security, Springdoc`
- `JPA, QueryDSL`
- `JUnit5, Mockito`
- `OpenSearch, Filebeat`
- `AWS`
  - `EC2, RDS MySQL`
  - `Route53, Cloudfront, S3`
- `Github Actions, Doppler`

## 기술 노트
> 트러블 슈팅 및 학습 기록

[노션 페이지](https://developerjoseph.notion.site/6aec3d72c3d641c4a98ba4a55d069536?v=f9513eb4d2644809927f5ab0ca5236ff)

## 주요 기능
### 일반 사용자
- 카카오 OAuth
- 장소 등록 신청
- 등록된 장소 조회
- 장소 리뷰 및 답글 작성
- 리뷰 이미지 업로드

### 지구멀미 관리자
- 사용자, 장소 등 비즈니스 자원을 관리 감독할 수 있는 ADMIN API

### 사장님(TODO...)
- 

## 개발 기간 및 구성 인원
2024.03.29 ~

마케팅1, 기획1, 프론트엔드1, 백엔드1
