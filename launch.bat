set archiServerName=localhost
set archiServerPort=8896

set home=./

java -jar ./target/archicherryontop-1.0.0.jar ^
-Dlog4j.debug -Dspring.config.location=file:./application.properties