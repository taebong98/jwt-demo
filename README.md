# 프로젝트 개요
Java 기반 백엔드 REST API 구축 과제

# 요구사항 정리 및 구현 여부
1. 사용자가 삼쩜삼에 가입해야 한다.
2. 가입한 유저의 정보를 스크랩 하여 환급액이 있는지 조회한다.
3. 조회한 금액을 계산한 후 유저에게 실제 환급액을 알려준다.

### 회원 가입 요구사항
- [x] 엔드포인트: POST `/szs/signup`
- [x] 필수 파라메터에 대한 유효성 검증
  - 아이디, 패스워드, 이름, 주민등록번호
- [x] Identity 전략으로 PK 생성
- [ ] 패스워드와 주민등록번호는 암호화 된 상태로 저장한다.
- [x] 정해진 유저만 회원가입이 가능해야 한다. 
- [ ] 단위테스트 작성

### 구현 방법
**1. 정해진 유저만 회원가입이 가능해야 한다.**
```
홍길동, 860824-1655068
김둘리, 921108-1582816
마징가, 880601-2455116
베지터, 910411-1656116
손오공, 820326-2715702
```
`UserService`에서 회원가입을 처리하고 `AllowedUsers` 클래스가 허용된 사용자 목록을 관리하도록 하였습니다.
회원가입을 요청하는 유저가 허용된 목록에 있는지를 확인하여 회원가입을 허용하거나 거부합니다.

유저가 회원가입 할 수 있는지에 대한 검증은 `UserService.isValidUser(String name, String regNo)` 메서드에서 처리하도록 하였습니다.
stream API를 사용하여 허용된 사용자 목록(Collection)을 순회하면서 주어진 조건을 만족하는지 검사합니다.
해당 로직에 걸리는 시간 복잡도는 `O(list.size())` 이기 때문에, 리스트에서 관리되는 데이터가 많거나, 회원가입 요청(tps)이 증가한다면 병목이 발생할 수 있습니다.
