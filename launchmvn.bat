set archiServerName=localhost
set archiServerPort=8896

set home=./

mvn spring-boot:run ^
-Drun.jvmArguments="-Dspring.profiles.active=dev -Dlog4j.debug -Dlogging.config=file:%home%s/Log4j_custom.properties -Dspring.config.location=file:$home/application.properties" ^
-Dspring-boot.run.arguments=="--server.port=%archiServerPort% --server.address=%archiServerPort%"