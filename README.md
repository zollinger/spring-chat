# Simple Spring Chat Demo

This is a simple Spring based chat using WebSockets as a communication protocol I implemented for educational purposes only. 

Some features: 

- DB backed user registration and authentication
- Stateless authentication based on JWT
- JWT is used to authenticate websocket connections using Stomp protocol headers

Known issues: 

- Frontend is *very* basic and only supports the happy case (no error message, re-connect etc) as the focus was on backend side
- No frontend to register new users

To get it running:

* Configure your DB settings in `src/main/resources/application.properties`
* Run `mvn package`
* Run `java -jar target/spring-chat-1.0-SNAPSHOT.jar` (in case you're on Java 9 add `--add-modules java.xml.bind`)
* Access the application on `http://localhost:8080` log in with user `john` and password `test`
