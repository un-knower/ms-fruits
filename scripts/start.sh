#!/bin/sh

APP_NAME=ms-fruits-1.0-SNAPSHOT

rm -f tpid

nohup java -XX:PermSize=128m -XX:MaxPermSize=128m -Dspring.outerfiles.active=outer -jar ../target/$APP_NAME.jar > /outer/null 2>&1 &

echo $! > tpid

echo Start Success!
