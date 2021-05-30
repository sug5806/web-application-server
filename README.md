# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* InputStream은 byte기반이기 때문에 BufferedReader로 읽으려면 InputStreamReader로 변경해서 읽는다.
* Files.readAllBytes로 해당 경로에 있는 파일의 전체 데이터를 읽어서 byte array로 리턴해준다.

### 요구사항 2 - get 방식으로 회원가입
* 처음에 ?로 파싱하고 그 다음 &으로 파싱, 그다음 =으로 파싱을 총 3번한다.

### 요구사항 3 - post 방식으로 회원가입

* RequestBody는 헤더를 다읽고 난후 공백 이후에 content-length만큼 읽으면 된다.

### 요구사항 4 - redirect 방식으로 이동

* 302는 Location만 넘겨주면 되므로 body나 content-length같은 값을 주지 않아도 된다.

### 요구사항 5 - cookie
* 로그인이 성공하면 cookie에 값을 넣어 로그인성공을 알려준다.

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 