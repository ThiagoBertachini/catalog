# Catalog

## Descrição

O projeto **Catalog** é uma API para gerenciar um catálogo de produtos com categorias. As funcionalidades incluem listar, criar e atualizar produtos e categorias. A API possui autenticação JWT para segurança.

## Requisitos

- Java 17
- Spring Boot 2.7.3
- Maven 4.0.0
- H2 Database
- PostgreSQL

## Dependências Principais

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-security-oauth2-autoconfigure`
- `spring-boot-starter-test` (para testes)
- `h2` (runtime)
- `postgresql` (runtime)

## Configuração do Projeto

### Banco de Dados

O projeto utiliza o banco de dados H2 para testes e PostgreSQL para runtime. A configuração do banco de dados é feita no arquivo `application.properties`.

### Autenticação

A API usa autenticação JWT para segurança. O arquivo de configuração `ResourceServerConfig` define as regras de autorização para diferentes rotas.

## Estrutura da API

### Categorias

- `GET /api/categories` - Listar todas as categorias
- `POST /api/categories` - Criar uma nova categoria
- `PUT /api/categories/{id}` - Atualizar uma categoria existente

### Produtos

- `GET /api/products` - Listar todos os produtos
- `POST /api/products` - Criar um novo produto
- `PUT /api/products/{id}` - Atualizar um produto existente

### Usuários

- `GET /api/users` - Listar todos os usuários (Apenas para administradores)
- `POST /api/users` - Criar um novo usuário (Apenas para administradores)
- `PUT /api/users/{id}` - Atualizar um usuário existente (Apenas para administradores)

## Executando o Projeto

Para executar o projeto localmente:

```bash
mvn spring-boot:run
