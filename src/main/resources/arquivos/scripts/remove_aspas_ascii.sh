#!/bin/bash

# Diretório que contém os arquivos
diretorio="./html"

# Iterar sobre todos os arquivos no diretório
for arquivo in "$diretorio"/*; do
    # Verificar se o arquivo é um arquivo regular
    if [ -f "$arquivo" ]; then
        # Substituir todas as ocorrências de \" por " usando sed e salvar no mesmo arquivo
        sed -i 's/\\\"/"/g' "$arquivo"
    fi
done
