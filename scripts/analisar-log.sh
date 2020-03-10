#/!bin/bash

ENVIARPARA="hbbucker@gmail.com"
DATA="2020-03-09"
CMDPARSE=./parse-log.sh

TEMPLATE="Cc: ${ENVIARPARA}
Subject: [LOGS] Analise diária de Logs ${DATA}
From: no-replay@domain.com
Content-Type: text/plain; charset='utf8'
\n"

PARSE=$(${CMDPARSE} -d ${DATA})

echo -e "$TEMPLATE $PARSE" > email.txt
ssmtp hbbucker@gmail.com < email.txt
