#!/usr/bin/env bash

HOST=http://localhost:8090

# Veriricar seo wrk esta instalado
# https://github.com/wg/wrk/wiki/Installing-Wrk-on-Linux

# -t quantidade de Thread que serão lançadas para o teste
# -c quantidade de conexões por Thread

wrk -t 4 -c100 ${HOST} > test-4x100.log
wrk -t 4 -c1000 ${HOST} > test-4x1000.log

wrk -t 8 -c100 ${HOST} > test-8x100.log
wrk -t 8 -c1000 ${HOST} > test-8x1000.log

wrk -t 16 -c100 ${HOST} > test-16x100.log
wrk -t 16 -c1000 ${HOST} > test-16x1000.log
