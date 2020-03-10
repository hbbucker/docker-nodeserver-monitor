#!/usr/bin/env bash
#######################
# Script filta os logs do Haproxy pela data do dia anteior
# e exibe a estatistica das requisicoes
#######################

# Valores padrão
dirlog=~/logs
input="${dirlog}/haproxy.log"
filtered_log="${dirlog}/filtered.log"
DATA=$(date -d"-1 days" +"%Y-%m-%d") #log do dia anterior

function usage() {
  echo "Syntax: parse-log.sh [OPTIONS]"
  echo "   -f Arquivo de log Haproxy"
  echo "   -d Data para analise. Ex.: 2020-03-09"
  echo "   -h help"
  exit -1
}

while getopts ":f:d:h:" opt; do
  case $opt in
    f) input=$OPTARG 
       ;;
    d) DATA=$OPTARG 
       ;;
    h) usage;;
    ?) usage;;
  esac
done

declare -A RESULT
grep -i "${DATA}" $input > $filtered_log
x=0

count=$(grep -c "" $filtered_log)
echo "Processando ${count} linhas!"
x=1
while IFS= read -r line
do
  IFS=' ' read -r -a array <<< "$line"

  PAGE=${array[16]}
  RESP="${array[8]}${PAGE}"
  
  if (( "${#array[@]}" == "18" )) ; then    
    if [[ -z ${RESULT[$RESP]}  ]]; then
       RESULT[$RESP]=0
    fi
     
    let RESULT[$RESP]=${RESULT[$RESP]}+1
  fi
done < $filtered_log

echo -e "Data da Analise: ${DATA}\n"
echo -e "Qtd\t\t Resposta"
echo -e "===\t\t ========"
for KEY in "${!RESULT[@]}"; do 
  echo -e "${RESULT[$KEY]}\t\t ${KEY/\// \/}" 
done |
sort -rn
