# [JIGUMULMI(지구멀미)](https://www.jigumulmi.com/)
[채식 정보 공유 서비스](https://developerjoseph.notion.site/dfea728380654c84b4b898b63e411707)
## 아키텍처
![jigumulmi architecture drawio](https://github.com/user-attachments/assets/bb18c5bb-19d7-4be8-b5d1-a0d43226c83a)
## 브랜치 전략
![gitflow drawio](https://github.com/user-attachments/assets/985708a6-f7dd-4d7b-be58-7db9d65e710c)
### 브랜치
> 브랜치 보호 전략으로 상위 브랜치에 커밋을 추가하기 위해서는 PR 제출 및 Status Check 필수
- main: 상용 배포 버전 코드 브랜치
- hotfix: main 기준 빠르게 수정이 필요한 작업을 처리하는 브랜치
- dev: 개발 배포 버전 코드 브랜치
- feat: 작업 중인 기능 별 브랜치
### CICD
> [Doppler](https://www.doppler.com/): 프로젝트 시크릿 데이터 관리
- dev
  - PR 제출하면 단위 테스트 실행
  - Push하면 개발 서버 배포
- main
  - Push하면 상용 서버 배포
## 팀원 소개
- 김학준: 백엔드
- 정성목: 프론트엔드
- 엄세희: 기획
- 신혜지: 마케팅
## 주요 기능
### 사용자 API
- 카카오 OAuth
- 장소 등록 신청
- 등록된 장소 조회
- 장소 리뷰 및 답글 작성
- 리뷰 이미지 업로드
### 어드민 API
- 유저 조회
- 장소 CRUD
## 기술 스택
- `Java21`
- `SpringBoot3, Spring Security, Springdoc`
- `JPA, QueryDSL`
- `JUnit5, Mockito`,
- `OpenSearch, Filebeat`
- `AWS`
  - `EC2, RDS MySQL`
  - `Route53, Cloudfront, S3`
- `Github Actions, Doppler`
## 기술 노트
[노션 페이지](https://developerjoseph.notion.site/6aec3d72c3d641c4a98ba4a55d069536?v=f9513eb4d2644809927f5ab0ca5236ff)
