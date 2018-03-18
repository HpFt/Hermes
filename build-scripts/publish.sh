#!/usr/bin/env bash
[[ ! -z "$DOCKER_USERNAME" ]] && docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD"
cp -rf ../build/libs/Hermes.jar build-scripts/app.jar
docker image build -t happyfat25/hermes:latest .
docker push happyfat25/hermes:latest