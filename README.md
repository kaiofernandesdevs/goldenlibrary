# 📚 GoldenLibrary

<div align="center">
**Gerenciador de biblioteca pessoal com autenticação JWT, MongoDB e foco em qualidade de software.**

</div>

---

# 📖 Sobre o Projeto

O **GoldenLibrary** é uma aplicação completa para cadastro e gerenciamento de livros de uma biblioteca pessoal.

O projeto foi desenvolvido com foco em:

* Arquitetura MVC
* Boas práticas de desenvolvimento
* Qualidade e testabilidade
* Cobertura mínima de 80%
* Integração contínua (CI)
* Segurança com JWT
* Persistência em MongoDB

Cada usuário possui acesso apenas aos próprios livros, garantindo isolamento de dados e gerenciamento seguro da sessão através de autenticação JWT.

---

# 🛠 Stack Tecnológica

## Backend

* Java 21
* Spring Boot 3.4.5
* Spring Security
* JWT Authentication
* MongoDB
* Spring Data MongoDB
* Swagger / SpringDoc OpenAPI

## Frontend

* Interface Web Responsiva
* Gerenciamento de sessão via JWT
* UX focada em navegação simples e intuitiva

## Qualidade & Testes

* JUnit 5
* Testcontainers
* JaCoCo
* SonarCloud
* MockMvc
* Testes parametrizados
* Testes de integração
* Testes caixa branca
* Testes caixa preta

## DevOps

* GitHub Actions
* Docker
* CI Pipeline

---

# ✨ Funcionalidades

| Funcionalidade            | Descrição                                          |
| ------------------------- | -------------------------------------------------- |
| 🔐 Cadastro de Usuários   | Cadastro com senha criptografada utilizando BCrypt |
| 🔑 Login JWT              | Autenticação segura com geração de token JWT       |
| 📚 CRUD de Livros         | Criar, listar, atualizar e remover livros          |
| 🔍 Busca por Título       | Busca parcial e case-insensitive                   |
| 📖 Filtro por Status      | WANT_TO_READ, READING e READ                       |
| 👤 Isolamento por Usuário | Cada usuário acessa apenas os próprios livros      |
| ✅ Validação de Dados      | Bean Validation com mensagens de erro apropriadas  |

---

# 🏗 Arquitetura do Projeto

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
│       └── application.properties
│
└── test/
    ├── controller/
    ├── service/
    └── MongoIntegrationTest.java
```

---

# 🚀 Como Executar Localmente

## Pré-requisitos

* Java 21+
* Maven 3.9+
* Docker Desktop
* MongoDB local ou MongoDB Atlas

---

## 1. Clonar o repositório

```bash
git clone https://github.com/kaiofernandesdevs/goldenlibrary.git
cd goldenlibrary
git checkout dev
```

---

## 2. Configurar variáveis de ambiente

Crie um arquivo `.env`:

```env
MONGO_URI=mongodb://localhost:27017
DB_NAME=goldenlibrary
JWT_SECRET=sua-chave-secreta-com-no-minimo-32-caracteres
```

---

## 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# 📡 Endpoints da API

## Usuários

| Método | Endpoint       | Descrição                    | Autenticação |
| ------ | -------------- | ---------------------------- | ------------ |
| POST   | `/user/signup` | Cadastro de usuário          | ✅            |
| POST   | `/user/login`  | Login e geração do token JWT | ✅            |

---

## Livros

| Método | Endpoint                        | Descrição                | Autenticação |
| ------ | ------------------------------- | ------------------------ | ------------ |
| GET    | `/books`                        | Listar livros do usuário | ✅            |
| GET    | `/books?title={title}`          | Buscar livros por título | ✅            |
| GET    | `/books/filter?status={status}` | Filtrar por status       | ✅            |
| GET    | `/books/{id}`                   | Buscar livro por ID      | ✅            |
| POST   | `/books`                        | Criar livro              | ✅            |
| PUT    | `/books/{id}`                   | Atualizar livro          | ✅            |
| DELETE | `/books/{id}`                   | Remover livro            | ✅            |

---

# 🔐 Gerenciamento de Sessão

O gerenciamento de sessão é realizado através de autenticação JWT.

Fluxo:

1. Usuário realiza login
2. API retorna um token JWT
3. O frontend armazena o token
4. As próximas requisições enviam:

```text
Authorization: Bearer <token>
```

5. O filtro JWT valida o token e libera acesso às rotas protegidas

---

# 🧪 Estratégia de Testes

> ⚠️ O uso de mocks é proibido conforme os requisitos do projeto.

Todos os testes utilizam infraestrutura real via Testcontainers.

---

## Tipos de Testes

| Tipo             | Classe               | Objetivo                              |
| ---------------- | -------------------- | ------------------------------------- |
| Integração       | `BookServiceTest`    | Validar regras de negócio de livros   |
| Integração       | `UserServiceTest`    | Validar regras de negócio de usuários |
| Controller / API | `BookControllerTest` | Validar endpoints de livros           |
| Controller / API | `UserControllerTest` | Validar autenticação e cadastro       |
| Parametrizados   | `BookServiceTest`    | Testar enum ReadingStatus             |
| Parametrizados   | `UserServiceTest`    | Testar formatos de e-mail             |

---

## Cobertura

```text
✅ Cobertura mínima exigida: 80%
✅ Requisitos funcionais rastreados no RTM.md
```

Relatório JaCoCo:

```text
target/site/jacoco/index.html
```

---

# ⚙️ Integração Contínua — GitHub Actions

O pipeline CI executa automaticamente:

1. Build do projeto
2. Execução dos testes
3. Verificação de cobertura
4. Análise estática via SonarCloud

Arquivo:

```text
.github/workflows/ci.yml
```

---

# 🔍 Qualidade de Código — SonarCloud

O projeto possui integração com SonarCloud para:

* análise estática
* verificação de bugs
* code smells
* cobertura
* duplicação de código

Dashboard:

[https://sonarcloud.io/project/overview?id=kaiofernandesdevs_goldenlibrary](https://sonarcloud.io/project/overview?id=kaiofernandesdevs_goldenlibrary)

---

# 📄 RTM — Matriz de Rastreabilidade

O arquivo `RTM.md` contém:

* Requisitos funcionais
* Mapeamento requisito → testes
* Cenários de teste
* Diagramas UML de sequência
* Evidência de rastreabilidade completa

---

# 📋 Requisitos Funcionais

| ID     | Requisito              | Status |
| ------ | ---------------------- | ------ |
| RF-001 | Cadastro de usuário    | ✅      |
| RF-002 | Login com JWT          | ✅      |
| RF-003 | Proteção de rotas      | ✅      |
| RF-004 | CRUD de livros         | ✅      |
| RF-005 | Busca por título       | ✅      |
| RF-006 | Filtro por status      | ✅      |
| RF-007 | Isolamento por usuário | ✅      |
| RF-008 | Validação de dados     | ✅      |

---

# 📁 Documentação

| Arquivo     | Descrição                         |
| ----------- | --------------------------------- |
| `README.md` | Documentação principal do projeto |
| `RTM.md`    | Matriz de rastreabilidade         |
| Swagger     | Documentação interativa da API    |
| JaCoCo      | Relatório de cobertura            |
| SonarCloud  | Relatório de qualidade            |

---

# 👨‍💻 Autores

Desenvolvido como projeto semestral para a disciplina de Qualidade de Software.
</div>

