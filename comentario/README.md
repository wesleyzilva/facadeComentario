# Projeto Facade de Comentários

Este projeto é uma aplicação Spring Boot que atua como uma camada de **Facade (ou Middleware)**, intermediando a comunicação entre um cliente e um serviço de persistência de dados desacoplado.

## 🎯 Objetivo

O objetivo principal é demonstrar uma arquitetura de microsserviços simples, onde a lógica de negócio e a exposição de endpoints para o cliente são separadas da lógica de acesso e armazenamento de dados.

A aplicação **Facade**:
- **Não possui banco de dados próprio.**
- Recebe requisições HTTP de clientes.
- Repassa essas requisições para um serviço externo (a camada de persistência).
- Trata as respostas e as devolve ao cliente.

## 🏛️ Arquitetura Proposta

O sistema é dividido em três componentes principais:

1.  **Cliente (Ex: Frontend, Postman)**
    -   Interage **exclusivamente** com a API da camada Facade.
    -   Não tem conhecimento sobre como ou onde os dados são armazenados.

2.  **Camada Facade (Este Projeto)**
    -   Aplicação Spring Boot rodando em `http://localhost:8081`.
    -   Responsável por expor a API pública para os clientes.
    -   Atua como um proxy, traduzindo e encaminhando as requisições para a camada de persistência.
    -   **Endpoints Principais**:
        -   `GET /api/facadeComments`: Busca todos os comentários.
        -   `POST /api/facadeComments`: Cria um novo comentário.

3.  **Camada de Persistência (Serviço Externo)**
    -   Uma aplicação Spring Boot separada, rodando em `http://localhost:8082`.
    -   É a única camada que tem acesso direto ao banco de dados.
    -   Expõe uma API interna (`/api/persistComments`) para ser consumida **apenas** pela camada Facade.

4.  **Banco de Dados (PostgreSQL)**
    -   Onde os dados dos comentários são efetivamente armazenados.

```mermaid
graph TD
    A[Cliente] --> B(Facade Layer | :8081);
    B --> C(Persistence Layer | :8082);
    C --> D[(PostgreSQL DB)];
```

## ⚙️ Como a Camada de Persistência Funcionará (com PostgreSQL)

A camada de persistência (que não faz parte deste repositório) será responsável por toda a interação com o banco de dados. Sua implementação seguirá o padrão:

1.  **Dependências**: Incluirá `spring-boot-starter-data-jpa` e `postgresql`.
2.  **Configuração (`application.properties`)**: Conterá as credenciais de acesso ao banco de dados PostgreSQL.
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/sua_base
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    spring.jpa.hibernate.ddl-auto=update
    ```
3.  **Entidade (`@Entity`)**: Haverá uma classe `Comment` anotada com `@Entity` para mapear a tabela `comments` no banco de dados.
    ```java
    @Entity
    public class Comment {
        @Id @GeneratedValue
        private Long id;
        private String author;
        private String content;
        private Instant timestamp;
        // ... getters e setters
    }
    ```
4.  **Repositório (`@Repository`)**: Uma interface estendendo `JpaRepository<Comment, Long>` será usada para abstrair as operações de CRUD (Create, Read, Update, Delete).
5.  **Serviço e Controlador**: O controlador receberá as requisições da camada Facade e usará o serviço/repositório para executar as operações no banco de dados, como `repository.findAll()` ou `repository.save(comment)`.