🅿️ PickingParking (피킹파킹)
AI 기반의 지정 좌석 주차 예약 및 부정 주차 탐지 시스템

"주차 구역 예약은 했지만, 막상 가보니 내 자리가 없다면?"

PickingParking은 단순히 주차장을 예약하는 것을 넘어, 영화관처럼 '특정 주차 면'을 지정 예약하고 AI 관제를 통해 해당 자리를 실시간으로 보호하는 솔루션입니다.

🔥 Key Features (주요 기능)
지정 좌석 예약제: 사용자가 원하는 특정 주차 위치(예: A-01)를 직접 선택하여 예약

실시간 AI 주차 관제: 주차 공간별 카메라가 입차 차량의 번호판을 실시간 인식

부정 주차 자동 탐지: 예약 정보와 대조하여 미예약 차량 침입 시 즉시 경고 부저(Alarm) 및 관리자 알림 송신

실시간 상태 대시보드: 주차 공간의 예약 및 점유 상태를 실시간 데이터로 관리

🏗️ Tech Stack (기술 스택)
Backend: Spring Boot, Spring Security, JPA (Hibernate)

Frontend: React Native

AI/Recognition: Python (FastAPI), Object Detection (License Plate Recognition)

Database: MySQL

Communication: Firebase Cloud Messaging (FCM), REST API

🔒 Security & Architecture (보안 및 아키텍처)
JWT 기반 인증: 사용자 및 관리자 세션 보안 강화

Secret Management: API 키 및 데이터베이스 접근 제어를 위한 환경 변수 분리 관리

Real-time Verification: 이미지 인식 결과와 DB 예약 정보를 실시간 교차 검증하는 로직 구현

📺 System Flow (시스템 흐름)
차량 진입: 각 주차 면에 설치된 AI 카메라가 차량 번호판 인식

데이터 검증: FastAPI 서버가 번호 추출 후 Spring Boot 백엔드로 전송

정상 입차: 예약 차량 일치 시 사용자에게 입차 알림 전송

부정 주차: 번호 불일치 시 즉시 관리자 어플 알람
