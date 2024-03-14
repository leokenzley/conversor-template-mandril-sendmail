# Conversor de template mailchimp para handlebars
## Features

- Busca todos os templates do diretório /src/main/resources/email-templates
- Verifica quais os arquivos ainda estão sendo utilizados
- Converte as marcações do mailchimp para handlebars
- Cria o novo template no diretório /src/main/resources/arquivos/processados
- Export documents as Markdown, HTML and PDF


## 1. Separar os arquivos que ainda são utilizados
```sh
curl --location 'http://localhost:8080/api/processa-arquivo/separa-arquivos-extraidos'
```

## 2. Processar os arquivos
```sh
curl --location 'http://localhost:8080/api/processa-arquivo/separa-arquivos-extraidos'
```

