# 프로젝트 개요
Java 기반 백엔드 REST API 구축 과제

# 요구사항 정리 및 구현 여부
1. 사용자가 삼쩜삼에 가입해야 한다.
2. 가입한 유저의 정보를 스크랩 하여 환급액이 있는지 조회한다.
3. 조회한 금액을 계산한 후 유저에게 실제 환급액을 알려준다.

### 회원 가입 요구사항
- [x] 엔드포인트: POST `/szs/signup`
- [ ] 필수 파라메터에 대한 유효성 검증
  - 아이디, 패스워드, 이름, 주민등록번호
- [ ] Identity 전략으로 PK 생성
- [ ] 패스워드와 주민등록번호는 암호화 된 상태로 저장한다.
- [ ] 정해진 유저만 회원가입이 가능해야 한다. 
- [ ] 단위테스트 작성