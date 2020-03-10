#!/usr/bin/env bash

docker run -i --rm -p 8080:8080 \
       -v /usr/bin/docker:/usr/bin/docker \
       -v /var/run/docker.sock:/var/run/docker.sock:rw \
       quarkus/docker-monitor-jvm