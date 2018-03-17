#!/usr/bin/env bash
[[ ! -z "$DOCKER_USERNAME" ]] && docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD"
docker image build -t happyfat25/hermes:latest .
docker push happyfat25/hermes:latest