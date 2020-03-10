#!/bin/bash
export GRAALVM_HOME=/home/bucker/Documentos/Aplicativos/graalvm/graalvm-latest
JAVA_HOME=${GRAALVM_HOME}

./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/docker-monitor-jvm .