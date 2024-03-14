#!/bin/bash

# Diretório que contém os arquivos
diretorio="./html"

# Iterar sobre todos os arquivos no diretório
for arquivo in "$diretorio"/*; do
    # Verificar se o arquivo é um arquivo regular
    if [ -f "$arquivo" ]; then
        # Remover a primeira aspa do arquivo e salvar em um arquivo temporário
        sed 's/^"//' "$arquivo" > "${arquivo}.temp"
        
        # Remover a última aspa do arquivo temporário e substituir o original
        sed 's/"$//' "${arquivo}.temp" > "$arquivo"
        
        # Remover o arquivo temporário
        rm "${arquivo}.temp"
    fi
done
