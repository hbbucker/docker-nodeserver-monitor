#!/bin/bash
# mvn compile package
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/docker-monitor-jvm .