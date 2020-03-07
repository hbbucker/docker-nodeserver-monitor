#!/bin/bash

export GRAALVM_HOME=/home/bucker/Documentos/Aplicativos/graalvm/graalvm-latest
JAVA_HOME=${GRAALVM_HOME}

mvn compile quarkus:dev -Ddebug