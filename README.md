# JWT Authentication API

## Descrição do programa
Este projeto é uma API de autenticação e autorização baseada em **JWT (JSON Web Token)**, desenvolvida com **Spring Boot**. Ele fornece endpoints para login, registro de usuários, renovação de tokens e acesso a recursos protegidos com base em permissões de usuário. 🚀

## Explicação do funcionamento
A API utiliza **JWT** para autenticação e autorização. O fluxo principal é:
1. O usuário faz login e recebe um **Access Token** e um **Refresh Token**.
2. O **Access Token** é usado para acessar recursos protegidos.
3. Quando o **Access Token** expira, o **Refresh Token** pode ser usado para obter um novo **Access Token**.
4. A API também suporta registro de novos usuários e controle de acesso baseado em roles (e.g., `USER`, `ADMIN`).

### Principais Endpoints
1. **Login**: Gera tokens de acesso e renovação.
2. **Renovação de Token**: Gera um novo token de acesso usando o refresh token.
3. **Registro de Usuário**: Permite registrar novos usuários.
4. **Acesso Protegido**: Endpoints que requerem autenticação e roles específicas.

## Instruções para executar o código

### Dependências necessárias
Certifique-se de ter as seguintes ferramentas instaladas:
- **Java 17** ou superior
- **Maven** para gerenciamento de dependências
- **PostgreSQL** como banco de dados

### Configuração do ambiente
1. Configure as variáveis de ambiente para o banco de dados:
    - `DATABASE_URL`: URL do banco de dados (e.g., `jdbc:postgresql://localhost:5432/jwtsecurity`)
    - `DATABASE_USERNAME`: Nome de usuário do banco de dados
    - `DATABASE_PASSWORD`: Senha do banco de dados
2. Configure o arquivo `application.yml` para ajustar as configurações do JWT e do servidor.

### Passos para execução
1. **Clone o repositório**:
```bash
git clone https://github.com/seu-usuario/jwt-authentication-api.git
cd jwt-authentication-api
## Instale as dependências:
```bash
mvn clean install
```

## Execute a aplicação:
```bash
mvn spring-boot:run
```

A API estará disponível em: [http://localhost:8080](http://localhost:8080).

## Exemplos de entrada e saída

### 1. Login
**Endpoint**: `POST /api/login`

**Requisição**:
```json
{
 "username": "admin",
 "password": "admin123"
}
```

**Resposta**:
```json
{
 "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Renovação de Token
**Endpoint**: `POST /api/refreshToken`

**Requisição**:
```json
{
 "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Resposta**:
```json
{
 "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 
}
```