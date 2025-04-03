# 🏡 Care Hub - 종합 케어 관리 플랫폼

---

Care Hub는 여러 보호자가 교대로 돌봄을 제공하는 현대 가정의 문제점을 해결하기 위한 종합 케어 관리 플랫폼입니다. 실시간 정보 공유와 체계적인 케어 기록 관리를 통해 보호자들 간의 소통을 원활히 하고 돌봄의 질을 향상시키는 것을 목적으로 합니다.

## 🔧 기술 스택

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logoColor=white)

</div>

## 🛠️ 개발 환경 설정

## 📋 구현 예정 기능

### 1️⃣ 1차 개발 (~6월)
- ✨ 사용자 인증 및 계정 관리
- 👤 케어 대상 프로필 관리
- 📝 일상 케어 활동 기록
- 🩺 건강 상태 기록
- 📊 데이터 시각화 및 분석

### 2️⃣ 2차 개발 (~12월)
- 👨‍👩‍👧‍👦 케어 대상 확장 (노인, 반려동물 등)
- 💓 건강 모니터링
- 🤖 AI 기반 케어 어시스턴트
- 🔔 실시간 알림 시스템

## 📓 개발 메모

이 섹션은 개발 과정에서 참고할 내용을 기록합니다.

### ⚙️ 환경 설정 관련

- **개발 환경**: `application-local.yml`은 git에 포함되지 않으므로 로컬에서 설정 필요
- **프로덕션 환경**: 추후 결정

### 🗄️ 데이터베이스 관련

- PostgreSQL 연결 설정은 `application-local.yml`에 지정
- 개발 초기에는 JPA `ddl-auto: update` 사용
- 배포 전 `schema.sql` 작성 예정

### 📝 색상 가이드

- 메인 색상: `#3AAA8F` (에메랄드 그린)
- 보조 색상: `#D4F0E8` (연한 민트)
- 액센트 색상: `#FE8269` (산호색)

---

<div align="center">
  <sub>Created with ❤️ by DDang_me</sub>
</div>