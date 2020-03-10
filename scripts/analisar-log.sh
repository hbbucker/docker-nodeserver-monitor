#/!bin/bash

ENVIARPARA="hbbucker@gmail.com"
DATA=$(date -d"-1 days" +"%Y-%m-%d") #log do dia anterior
CMDPARSE=./parse-log.sh

## Verifica parametro de data
if [[ -n $1 ]]; then
  DATA=$1
fi 

TEMPLATE="Cc: ${ENVIARPARA}
Subject: [LOGS] Analise diária de Logs ${DATA}
From: no-replay@domain.com
Content-Type: text/plain; charset='utf8'
\n"

PARSE=$(${CMDPARSE} -d ${DATA})

echo -e "$TEMPLATE $PARSE" > email.txt
ssmtp hbbucker@gmail.com < email.txt
