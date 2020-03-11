# Desafio

## Implementação

Foi adotada uma abordagem para gerenciamento dos Containers Docker, 
construída uma aplicação Java que interage e monitora os
containers pré definidos.

## Aplicação de Monitoramento

A aplicação verifica os containers, inicializa ou para
conforme o número de Núcleos de processamento da máquina
hospedeira.

A Aplicação roda inicialmente na porta 8080, possuindo alguns
Endpoints de informação e para iniciar o Restore de uma Imagem
do NodeServer.

### Endpoints

#### /docker/status
Lista todos os containers.

#### /docker/images
Lista todas as imagens.

#### /docker/node-status
Lista os containers NodeJS criados para o desafio

#### /docker/node-restore
Remove os containers NojeJS criados com a última imagem, 
remove a ultima imagem, faz com que a imagem anterior passe
ser a atual, e inicia novos containers com a imagem anterior, que 
passa ser a atual.

### Scripts de configuração

#### 1. compile.sh
Este script compila, empacota e cria a Imagem Docker da Aplicação de Monitoramento

#### 2. scripts/configurar-email.sh
Este script instala e faz o passo-a-passo para configurar um servidor SMTP para enviar
mensagens pelo sistema, deve ser executado como ROOT ou sudo

> Obs.: Está preparado para fazer a instalação e configuração em Linux Ubuntu/Debian, não
foi preparado nem testado para outras versões.

### Scripts de analise de Logs

#### 1. scripts/parse-log.sh
Efetua a analise do arquivo de log pré configurado do
HAProxy, tendo como saída a estatśticas das requisções
ao servidor.

#### 2. scripts/analisar-log.sh
Chama o script de **parse-log.sh** e encaminha um email
com o resultado do parse.

A variável **ENVIARPARA** deve ser alterado com o email
de quem irá receber esta analise por email.

Analisa as requisições do dia anterior por padrão, para 
analisar as requisições de uma data específica deve ser passado a data
como parâmetro, no seguinte formato: **yyyy-mm-dd**. 
Por exemplo:
```
./ analisar-log.sh 2020-03-10
```

### Script para Teste de Carga

#### 1. scripts/teste-de-carga.sh

Foi utilizada a ferramenta *wrk* para efetuar os testes, verifique a página do desenvolvedor
para instalar a ferramenta [Clique aqui](https://github.com/wg/wrk/wiki/Installing-Wrk-on-Linux).

São efetuados 6 Teste com os seguintes parâmetros:

```
Thread..: 4
Conexões: 100
Thread..: 4
Conexões: 1000

Thread..: 8
Conexões: 100
Thread..: 8
Conexões: 1000

Thread..: 16
Conexões: 100
Thread..: 16
Conexões: 1000
```

Cada teste grava o resultado em um respectivo arquivo:

```
test-4x100.log
test-4x1000.log
test-8x100.log
test-8x1000.log
test-16x100.log
test-16x1000.log
```

# Requisitos

Todos este projeto foi testado e desenvolvido para Rodar em uma máquina
hospedeira com Linux Ubuntu ou Debian.

