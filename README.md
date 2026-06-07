# 📚 GoldenLibrary

<div align="center">

# 📚 GoldenLibrary

**Gerenciador de biblioteca pessoal com autenticação JWT, MongoDB e foco em Qualidade de Software.**

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Database-darkgreen)
![JWT](https://img.shields.io/badge/JWT-Authentication-blue)
![Coverage](https://img.shields.io/badge/Coverage-80%25-brightgreen)
![License](https://img.shields.io/badge/License-Academic-lightgrey)

</div>

---

# 📖 Sobre o Projeto

O **GoldenLibrary** é uma aplicação completa para cadastro e gerenciamento de livros de uma biblioteca pessoal, composta por um backend desenvolvido com **Java/Spring Boot** e um frontend responsivo utilizando **HTML, CSS e JavaScript puro**.

Desenvolvido como projeto semestral da disciplina de **Qualidade de Software**, o sistema foi construído com foco em boas práticas de engenharia de software, segurança, testes automatizados e cobertura de código.

## 🎯 Objetivos do Projeto

* Arquitetura em camadas (MVC)
* Segurança baseada em JWT
* Isolamento de dados por usuário
* Testes automatizados sem uso de mocks
* Cobertura mínima de 80%
* Integração contínua com GitHub Actions
* Análise estática com SonarCloud

---

# 🛠️ Stack Tecnológica

## Backend

* Java 21
* Spring Boot 3.4.5
* Spring Security
* JWT (JSON Web Token)
* MongoDB
* Spring Data MongoDB
* Jakarta Bean Validation
* Swagger / OpenAPI 3

## Frontend

* HTML5
* CSS3
* JavaScript ES6+
* Fetch API
* LocalStorage

### Recursos do Frontend

* Renderização dinâmica de livros
* Busca em tempo real
* Filtros por status de leitura
* Capas personalizadas por URL
* Fallback automático para imagens inválidas

---

# ✨ Funcionalidades

| Funcionalidade           | Descrição                                          |
| ------------------------ | -------------------------------------------------- |
| 🔐 Cadastro de Usuários  | Registro de novos usuários com criptografia BCrypt |
| 🔑 Login JWT             | Autenticação segura baseada em token               |
| 📚 CRUD de Livros        | Cadastro, consulta, edição e remoção               |
| 🖼️ Capas Personalizadas | Exibição de capas via URL                          |
| 🔍 Busca em Tempo Real   | Pesquisa por título                                |
| 📖 Filtros de Leitura    | WANT_TO_READ, READING e READ                       |
| 👤 Isolamento de Dados   | Cada usuário acessa apenas seus livros             |
| ✅ Validações             | Validação automática de entradas                   |

---

# 🏗️ Arquitetura do Projeto

```text
src/
├── main/
│   ├── java/.../goldenlibrary_api/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── enums/
│   │   ├── security/
│   │   └── tokensjwt/
│   └── resources/
│       ├── static/
│       └── application.properties
│
└── test/
    └── java/.../goldenlibrary_api/
        ├── controller/
        ├── service/
        └── MongoIntegrationTest.java
```

### Organização das Camadas

| Camada     | Responsabilidade     |
| ---------- | -------------------- |
| Controller | Endpoints REST       |
| Service    | Regras de negócio    |
| Repository | Persistência MongoDB |
| Entity     | Modelos de dados     |
| Security   | JWT e autenticação   |
| Test       | Testes automatizados |

---

# 🚀 Como Executar Localmente

## Pré-requisitos

* JDK 21
* Maven 3.9+
* Docker Desktop
* MongoDB Local ou MongoDB Atlas

---

## 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/kaiofernandesdevs/goldenlibrary.git

cd goldenlibrary

git checkout dev
```

---

## 2️⃣ Configurar Variáveis de Ambiente

Crie um arquivo `.env` ou configure as variáveis no sistema:

```env
MONGO_URI=mongodb://localhost:27017
DB_NAME=goldenlibrary
JWT_SECRET=sua-chave-secreta-com-no-minimo-32-caracteres
```

---

## 3️⃣ Executar o Backend

### Linux / macOS

```bash
./mvnw spring-boot:run
```

### Windows

```bash
mvnw.cmd spring-boot:run
```

---

## 4️⃣ Acessar a Aplicação

### API

```text
http://localhost:8080
```

### Swagger

```text
http://localhost:8080/swagger-ui/index.html
```

### Frontend

```text
http://localhost:8080/login.html
```

---

# 📡 Endpoints da API

## 👤 Usuários

### Cadastro

```http
POST /user/signup
```

### Login

```http
POST /user/login
```

---

## 📚 Livros

> Todas as rotas exigem:

```http
Authorization: Bearer <TOKEN>
```

### Listar Livros

```http
GET /books
```

### Buscar por Título

```http
GET /books?title=harry
```

### Filtrar por Status

```http
GET /books/filter?status=READ
```

### Buscar por ID

```http
GET /books/{id}
```

### Criar Livro

```http
POST /books
```

### Atualizar Livro

```http
PUT /books/{id}
```

### Remover Livro

```http
DELETE /books/{id}
```

---

# 🔐 Fluxo de Autenticação

1. Usuário realiza login.
2. Senha é validada com BCrypt.
3. API gera um JWT.
4. Frontend salva o token no LocalStorage.
5. Requisições enviam:

```http
Authorization: Bearer <TOKEN>
```

6. O filtro JWT do Spring Security valida o token.
7. O usuário acessa apenas seus próprios livros.

---

# 🧪 Estratégia de Testes

O projeto segue uma regra importante:

> ❌ Não utilizar mocks

Todos os testes utilizam infraestrutura real através do Testcontainers.

## Executar Testes

```bash
./mvnw clean test
```

---

## Testes de Serviço

Validação de:

* Regras de negócio
* Criptografia de senhas
* Persistência de dados
* Isolamento de usuários

---

## Testes de Controller

Validação de:

* Endpoints REST
* Status HTTP
* Segurança
* Payloads

---

## Testes Parametrizados

Cobrem:

* E-mails válidos e inválidos
* Status de leitura
* Casos de borda

---

## Cobertura de Código

Gerar relatório:

```bash
./mvnw clean verify
```

Relatório:

```text
target/site/jacoco/index.html
```

Meta mínima:

```text
80%
```

---

# ⚙️ Integração Contínua

## GitHub Actions

Pipeline automatizada para:

* Build
* Testes
* Cobertura
* Validação de qualidade

Executada a cada:

* Push
* Pull Request

---

## SonarCloud

Análise estática de:

* Bugs
* Vulnerabilidades
* Code Smells
* Duplicação de código
* Cobertura de testes

O Quality Gate impede integrações que não atendam aos critérios definidos.

---

# 🐳 Docker & Testcontainers

Os testes utilizam containers reais do MongoDB através do Testcontainers.

Benefícios:

* Ambiente isolado
* Testes reproduzíveis
* Sem dependência de banco local
* Maior confiabilidade

---

# 👨‍💻 Desenvolvedores

**Kaio Fernandes**

* GitHub: https://github.com/kaiofernandesdevs

**Celso Alves**

*  Github: https://github.com/CelsoAlves935 *
  


---

# 🎓 Projeto Acadêmico

Desenvolvido como projeto semestral para a disciplina de **Qualidade de Software**, aplicando conceitos de:

* Testes Automatizados
* Arquitetura de Software
* Integração Contínua
* Segurança de Aplicações
* Cobertura de Código
* Boas Práticas de Desenvolvimento

---

<div align="center">

⭐ Se gostou do projeto, deixe uma estrela no repositório!

</div>
