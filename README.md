# Observador de editais do IMD

## Sobre
Este projeto permite que pessoas recebam notificações sobre novos editais do Instituto Metrópole Digital por email.

## Como rodar o projeto
### Requisitos
- Docker e npm

### Rodando o banco de dados
Vá para a pasta backend e digite o comando `docker compose up`

### Rodando o backend
Para esta etapa, você precisa ter o banco de dados rodando e ter declarado as variáveis de ambiente abaixo.
Substitua os valores vazios por valores válidos.

```sh
export FRONTEND_URL=http://localhost:5173 # URL que o frontend irá rodar
export MAIL_USERNAME= # Email do google que irá enviar os emails da aplicação
export MAIL_PASSWORD= # Senha de aplicativo da conta do google (veja como obter uma abaixo)
export DB_USERNAME=postgres # Usuário do BD
export DB_PASSWORD=password # Senha do usuário do BD
export DB_URL=jdbc:postgresql://localhost:5432/notice_tracker # URL de conexão do DB
```

Depois de ter feito isso, na pasta backend, execute o comando `./mvnw spring-boot:run`

#### Obtendo a senha de aplicativo da conta Google

Para obter a senha de aplicativo, siga as instruções deste [link](https://support.google.com/accounts/answer/185833?hl=pt-BR)

### Rodando o frontend
Crie um arquivo `.env` contendo apenas `VITE_API_URL=http://localhost:8080`.
Depois, basta instalar as dependências com `npm install` e rodar o comando `npm run dev`


## Contribuindo
Você pode contribuir para que a API notifique sobre editais de outros sites.

Basta implementar a interface `INoticeTracker` com a sua lógica para recuperar os novos editais do site. Depois, basta injetar sua implementação na classe `GetNewNoticesCron`
