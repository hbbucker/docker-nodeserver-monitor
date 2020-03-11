#/!bin/bash

ENVIARPARA="hbbucker@gmail.com"
DATA=$(date -d"-1 days" +"%Y-%m-%d") #log do dia anterior
CMDPARSE=./parse-log.sh

## Verifica parametro de data
if [[ -n $@ ]]; then
  for param in "$@"; do
    eHData=$(echo "$param" | grep -c -E "[0-9]{4}\-[0-9]{2}\-[0-9]{2}")
    eHEmail=$(echo "$param" | grep -c -E "\.*@")
    if [[ $eHData == "1" ]]; then
      DATA=$param
    fi
    if [[ $eHEmail == "1" ]]; then
      EMAIL=$param
    fi
  done
fi 

if [[ -z $EMAIL ]]; then
  echo "Informe o parametro com email para continuar.!"
  exit -1
fi


TEMPLATE="Cc: ${ENVIARPARA}
Subject: [LOGS] Analise diária de Logs ${DATA}
From: no-replay@domain.com
Content-Type: text/plain; charset='utf8'
\n"

PARSE=$(${CMDPARSE} -d ${DATA})

echo -e "$TEMPLATE $PARSE" > email.txt
ssmtp $EMAIL < email.txt
