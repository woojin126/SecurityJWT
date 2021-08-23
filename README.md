# SecurityJWT
#### 목표

 JWT 로그인을 Spring Securiy로 controll 해보기

### 기본 사양
- spring boot 2.4.5
- Spring Data JPA
- Oracle 11g XE
- h2Database

### Utils dependencies
- lombok
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- devtools
- junit-jupiter-api:5.7.0
- unit-jupiter-engine:5.7.2
- spring-boot-starter-mustache
- security:spring-security-test


### IDEL
- IntelliJ로 구축

### Version
## 1.0 구현

- 1.1.0 스프링 시큐리티 로 JWT 토큰 생성과 클라이언트에 응답해보기

### IntelliJ 콘솔 로그 한글 깨짐 해결 방법
- IntelliJ File Encodings 변경

1. 컨트롤 + 알트 + S
2. Editor > File Encodings 선택
3. 셋팅

- Global Encoding:UTF-8
- Project Encoding:UTF-8
- Default encoding for properties files:UTF-8

### lombok 설정
1. Setting
2. Annotation Processors
3. Enable annotaion processing 체크

### gradle 동작 방식 설정
1. setting
2. 검색창에 gradle
3. Build and run using , Run tests using : 모두 IntelliJ로 변경
