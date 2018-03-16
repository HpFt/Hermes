#!/usr/bin/env bash
gradle -b ../build.gradle build
cp ../build/libs/Hermes.jar app.jar
docker image build -t happyfat25/hermes:latest .
docker push happyfat25/hermes:latest