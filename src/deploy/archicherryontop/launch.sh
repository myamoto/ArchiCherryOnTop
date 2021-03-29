#!/bin/sh
#

archiServerName=localhost
archiServerPort=9999
oauthPublicKeyUrl=https://my.oauth.bearer.server/publicKeys/


home=$(`pwd`)

java \
-Dlog4j.debug \
-Dlogging.config=file:$home/Log4j_custom.properties \
-Dlog4j.configuration=file:$home/Log4j_custom.properties \
-Dserver.address=$archiServerName \
-Dserver.port=$archiServerPort \
-Dspring.config.location=file:$home/application.properties \
-Dorg.toolup.secu.oauth.jwt.oidc.oauthPublicKeyUrl=$oauthPublicKeyUrl \
-jar  $home/archicherryontop.jar
