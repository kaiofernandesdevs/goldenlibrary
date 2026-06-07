# 📚 GoldenLibrary

<div align="center">
**Gerenciador de biblioteca pessoal com autenticação JWT, MongoDB e foco estrito em qualidade de software.**
---

# 📖 Sobre o Projeto

O **GoldenLibrary** é uma aplicação completa para cadastro e gerenciamento de livros de uma biblioteca pessoal, dividida num ecossistema robusto com backend em Java/Spring Boot e um frontend responsivo em Vanilla JavaScript e CSS moderno.

Desenvolvido como projeto prático semestral para a disciplina de **Qualidade de Software**, o projeto foca em:
* **Arquitetura em Camadas:** Divisão clara entre Controller, Service, Repository e Entity (padrão MVC).
* **Segurança Avançada:** Isolamento total de dados por utilizador baseado no ID contido no token JWT.
* **Testabilidade Estrita:** Proibição do uso de dublês de teste (*Mocks*) em benefício de infraestrutura real em containers.
* **Garantia de Cobertura:** Cobertura mínima de código de 80% auditada via JaCoCo e SonarCloud.

---

# 🛠 Stack Tecnológica

* **Backend:** Java 21, Spring Boot 3.4.5, Spring Security, JWT (Json Web Token), MongoDB, Spring Data MongoDB, Jakarta Bean Validation e Swagger/OpenAPI 3.
* **Frontend:** HTML5, CSS3 Moderno, Vanilla JavaScript (ES6+ assíncrono com `fetch`) e armazenamento local via `localStorage`. Incorpora renderização dinâmica de capas com fallback visual automático (`📚`) para URLs inválidas.
* **Qualidade & Testes:** JUnit 5, Testcontainers (instâncias reais de MongoDB em Docker), MockMvc, JaCoCo e SonarCloud.
* **DevOps & CI:** GitHub Actions e Docker.

---

# ✨ Funcionalidades

| Funcionalidade            | Descrição                                                                          |
| ------------------------- | ---------------------------------------------------------------------------------- |
| 🔐 Cadastro de Usuários   | Criação de contas com criptografia de senha via BCrypt (Spring Security).          |
| 🔑 Login via JWT          | Autenticação segura gerando tokens Bearer com tempo de expiração definido.         |
| 📚 Gestão de Acervo (CRUD)| Criar, ler, atualizar e remover livros de forma individualizada.                    |
| 🖼️ Capas Personalizadas    | Suporte à inserção e exibição de capas dos livros via URL dinâmica.                |
| 🔍 Busca em Tempo Real    | Barra de pesquisa por título com filtragem case-insensitive.                       |
| 📖 Filtros de Leitura     | Segmentação rápida do acervo por status (`WANT_TO_READ`, `READING` e `READ`).       |
| 👤 Isolamento Multitenant | Garantia estrita de que um utilizador autenticado apenas acede aos seus próprios livros. |
| ✅ Validação Restritiva   | Captura automatizada de campos vazios ou inválidos com feedbacks em tempo real.    |

---

# 🏗 Arquitetura do Projeto

```text
src/
├── main/
│   ├── java/.../goldenlibrary_api/
│   │   ├── controller/      # Endpoints REST e validação HTTP
│   │   ├── service/         # Regras de negócio e isolamento de utilizadores
│   │   ├── repository/      # Interfaces de persistência (MongoRepository)
│   │   ├── entity/          # Documentos do MongoDB (User, Book)
│   │   ├── enums/           # Constantes de domínio (ReadingStatus)
│   │   ├── security/        # Configurações do Spring Security e filtros
│   │   └── tokensjwt/       # Geração e validação do token JWT
│   └── resources/
│       ├── static/          # Frontend integrado (home.html, cadastro.html, editar.html, login.html)
│       └── application.properties  # Configurações do Spring Boot
└── test/
    └── java/.../goldenlibrary_api/
        ├── controller/      # Testes de API caixa-preta baseados em requisições MockMvc
        ├── service/         # Testes caixa-branca focados nas regras de negócio com banco real
        └── MongoIntegrationTest.java # Configuração base do Testcontainers para MongoDB

🚀 Como Executar Localmente
Pré-requisitos

    Java Development Kit (JDK) 21

    Maven 3.9+ (ou utilização do wrapper ./mvnw incluso)

    Docker Desktop ativo (obrigatório para a execução dos testes)

    MongoDB ativo localmente (porta 27017) ou string de conexão do MongoDB Atlas.

Passo a Passo
1. Clonar o repositório

git clone [https://github.com/kaiofernandesdevs/goldenlibrary.git](https://github.com/kaiofernandesdevs/goldenlibrary.git)
cd goldenlibrary
git checkout dev

2. Configurar Variáveis de Ambiente

Configure as variáveis no seu ambiente ou num ficheiro .env na raiz do projeto:

NGO_URI=mongodb://localhost:27017
DB_NAME=goldenlibrary
JWT_SECRET=sua-chave-secreta-com-no-minimo-32-caracteres-e-totalmente-segura

3. Executar o Backend

# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run

A API estará disponível em http://localhost:8080. O Swagger pode ser acedido em http://localhost:8080/swagger-ui/index.html.
4. Acessar o Frontend

Como os ficheiros estáticos estão mapeados na pasta resources/static, basta abrir o navegador e aceder diretamente a:

http://localhost:8080/login.html

📡 Endpoints da API

Todas as rotas de /books requerem o cabeçalho Authorization: Bearer <TOKEN_JWT>.

    POST /user/signup: Regista um novo utilizador.

    POST /user/login: Autentica e devolve o token JWT.

    GET /books: Lista todos os livros do utilizador autenticado.

    GET /books?title={termo}: Procura livros por correspondência parcial no título.

    GET /books/filter?status={STATUS}: Filtra por status de leitura.

    GET /books/{id}: Obtém detalhes de um livro específico.

    POST /books: Adiciona um novo livro (vinculando automaticamente o ID do utilizador).

    PUT /books/{id}: Atualiza os dados e a URL da capa do livro.

    DELETE /books/{id}: Remove permanentemente um livro.

🔐 Gerenciamento de Sessão

    O utilizador faz login em /user/login, a API valida a senha com BCrypt e retorna o JWT.

    O frontend armazena o token no localStorage sob a chave token.

    Em cada requisição assíncrona, o script extrai o token e injeta-o no cabeçalho HTTP:

Authorization: Bearer <seu_token_jwt_aqui>

O filtro do Spring Security decodifica o token, extrai o contexto do utilizador e garante o isolamento multitenant dos dados.

🧪 Estratégia de Testes Automatizados

Seguindo os requisitos estritos do projeto, o uso de Mocks é proibido. Toda a validação ocorre em ambiente real através do Testcontainers, que sobe uma instância efémera do MongoDB em Docker para cada execução.
Executar a Suíte de Testes
Bash

./mvnw clean test

    Testes Caixa-Branca (Service Layer): Validação de regras de negócio, consistência de dados, criptografia de senhas e integridade do isolamento de registos entre utilizadores diferentes.

    Testes Caixa-Preta (Controller Layer): Utilização de MockMvc para simular requisições HTTP completas, validando os filtros de segurança, retornos de códigos HTTP (201, 403, 404) e validações de payloads.

    Testes Parametrizados: Testes em massa para validação de múltiplos formatos de e-mails e transições do enum ReadingStatus.

O relatório de cobertura detalhado do JaCoCo é gerado em: target/site/jacoco/index.html.
⚙️ Integração Contínua (CI) & Qualidade

    GitHub Actions (ci.yml): Executa automaticamente o build, inicializa os containers Docker do Testcontainers, roda a suite de testes e valida a integridade do código a cada push ou pull request.

    SonarCloud: Atua como um Quality Gate na Cloud. Analisa o código estaticamente à procura de bugs, Code Smells, vulnerabilidades de segurança e garante o bloqueio de integrações caso a cobertura de código fique abaixo do patamar mínimo de 80%.

Desenvolvido como projeto semestral para a disciplina de Qualidade de Software.
