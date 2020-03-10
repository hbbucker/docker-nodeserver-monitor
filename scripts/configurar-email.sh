#!/bin/bash

LINUX=$(cat /etc/issue | cut -d" " -f1)
LINUX=${LINUX,,}
FILECONF="/etc/ssmtp/ssmtp.conf"
CHECK_MARK="\033[0;32m\xE2\x9C\x94\033[0m"
UNCHECK_MARK="\033[0;31m\u2717\033[0m"
SENHA=""

function ler_senha(){

  echo -n "  Senha do usuario: "
  read -s PASS_A
  echo " "
  echo -n "  Confirme a Senha: "
  read -s PASS_B
  
  if [[ $PASS_A != $PASS_B ]]; then
    echo -e "\n${UNCHECK_MARK} As senhas não conferem"
    ler_senha
  else
    SENHA=${PASS_A}
    echo -e "\n${CHECK_MARK} Senha OK"
  fi
}

function configure_ssmtp(){
  echo "Configurar sSMTP:"
  
  echo -n "  Conta de e-mail: "
  read CONTAEMAIL
  echo -e "\b\r${CHECK_MARK} ${CONTAEMAIL}"
  
  echo -n "  Servidor SMTP: "
  read SERVSMTP
  echo -e "\b\r${CHECK_MARK} ${SERVSMTP}"
  
  echo -e "  Usa TLS?\n\t1) Sim \n\t2) Não"
  read -s -n 1 TLSOPT
  if [[ $TLSOPT == 1 ]]; then
    TLSOPT="YES"
  else
    TLSOPT="NO"
  fi
  echo -e "${CHECK_MARK} $TLSOPT"
  
  echo -n "  Username para autenticação: "
  read USERNAMEAUTH
  echo -e "\r${CHECK_MARK} ${USERNAMEAUTH}"
  
  ler_senha
  
  echo -e "\n\nConfirma as Informações digitadas? "
  echo "  email......: $CONTAEMAIL"
  echo "  smtp server: $SERVSMTP"
  echo "  TLS........: $TLSOPT"
  echo "  Usuario....: $USERNAMEAUTH"
  echo "  Senha......: ********"
  echo -e -n "\t1) Sim\n\t2) Não "
  read -s -n1 OPT
  
  if [[ $OPT == 1 ]]; then
     echo -e "\nSalvar"
     cp ${FILECONF} ${FILECONF}.old
     cat /dev/null > ${FILECONF}
     
     echo "TLS_CA_File=/etc/ssl/certs/ca-certificates.crt" >> $FILECONF
     echo "root=${CONTAEMAIL}" >> $FILECONF
     echo "mailhub=${SERVSMTP}" >> $FILECONF
     echo "rewriteDomain= " >> $FILECONF
     echo "hostname=localhost" >> $FILECONF
     echo "UseSTARTTLS=${TLSOPT} " >> $FILECONF
     echo "AuthUser=${USERNAMEAUTH}" >> $FILECONF
     echo "AuthPass=${SENHA}" >> $FILECONF
     echo "FromLineOverride=YES" >> $FILECONF
     
     echo "desativar apps seguro para usar o gmail: https://myaccount.google.com/lesssecureapps"
   
  fi
}


if [[ ($LINUX == "ubuntu") || ($LINUX == "debian") ]]; then
   echo "Verificando pacote instalado"
   P=$(dpkg -l ssmtp 2>/dev/null | grep -c -e ^ii)
   
   if (( ! $P )); then
     echo -e "Instalar pacote?\n \t1) Sim \n\t2) Não"
     read -s -n 1 OPT
     
     if [[ $OPT == 1 ]]; then
       echo "Instalando..."
       apt-get update
       apt -y install ssmtp pki-ca
       echo "ssmtp instalado"
     fi
   
   else
     
     echo -e "${CHECK_MARK} sSMTP já está instaldo!"
     if [[ -e ${FILECONF} ]]; then
       echo "Já existe um arquivo de configuraçao. O que fazer?"
       echo -e "\t1) Nada \n\t2) Configurar"
       read -s -n 1 OPT
       if [[ $OPT == 1 ]]; then
          echo -e "${UNCHECK_MARK} Ok. Nada Feito!"
          exit 0
       fi 
       
       configure_ssmtp  
     fi 
   
   fi
   
   echo "OK"
   exit
fi


