#!/usr/bin/env bash

# Ajustando permissão para execucao docker-in-docker
# obs. melhorar isto, não é a melhor solução
chk=$(ls -l /var/run/docker.sock | cut -d" " -f1 | grep -c "rwxrwxrwx")
if [[  $chk != 1 ]]; then
	echo "Será solicitada a senha de root para ajustes na configuração:"
	su -c "chmod 777 /var/run/docker.sock"
	if [[ $? != 0 ]]; then
	  sudo chmod 777 /var/run/docker.sock
	  if [[ $? != 0 ]]; then
		exit -1
	  fi
	fi
fi

docker run -dti --rm -p 8080:8080 \
       -v /usr/bin/docker:/usr/bin/docker \
       -v /var/run/docker.sock:/var/run/docker.sock:rw \
       --privileged \
       quarkus/docker-monitor-jvm
