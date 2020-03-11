#!/bin/bash

apt-get update
apt-get -y install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    software-properties-common \
    git \
    build-essential libssl-dev \
    maven
    
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo apt-key add -

add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/debian \
   $(lsb_release -cs) \
   stable"
   
apt-get update
apt-get -y install docker-ce docker-ce-cli containerd.io

if [[ ! -e /usr/local/bin/wrk ]]; then
  cd
  git clone https://github.com/wg/wrk.git wrk
  cd wrk
  make
  cp wrk /usr/local/bin
fi
clear
echo -e "\n\n\n"
echo "Aviso: não esqueça de adicionar o usuario não root"
echo "       criado na instalação ao Grupo docker, "
echo "       para que ele possa rodar os comandos Docker-cli"
echo "       Ex.: gpasswd -a usuario docker" 
echo "       * Neste ponto talvez seja necessário fazer o login novamente"
sleep 10

echo "clonando os projetos"
git clone https://github.com/hbbucker/simple-nodeserver.git
git clone https://github.com/hbbucker/docker-nodeserver-monitor.git

echo "criando as imagens"
cd simple-nodeserver
./build-docker.sh
./build-haproxy.sh
cd ../docker-nodeserver-monitor
./build-docker.sh

echo "Iniciando serviços"
simple-nodeserver/start-servers.sh
docker-monitor/start-server.sh

sleep 5

echo -e "\n\n\n"
echo "INSTRUÇÕES"
echo "=========="
echo "Para executar teste de carga: docker-monitor/scripts/teste-de-carga.sh"
echo "e veja os arquivos de log criado!"
echo ""

echo "Configurar o envio de email: "
echo " docker-monitor/scripts/configurar-email.sh"
echo " "

echo "Analisar o gerado pelo teste de carga, altere a data se necessário e informe o email que recebera o log"
echo "  docker-monitor/scripts/analisar-log.sh 2020-03-11 email@enviarlog.com.br"
echo -e "\n\n
