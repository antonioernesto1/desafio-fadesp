# API de Pagamentos

Este projeto implementa uma API REST para recebimento e gerenciamento de pagamentos de débitos de pessoas físicas e jurídicas.

A aplicação permite:
- receber um pagamento com status inicial `PENDENTE`
- atualizar o status conforme as regras de negócio
- listar pagamentos com filtros
- realizar exclusão lógica, mudando o status para `INATIVO` quando permitido

## Resumo do projeto

A API foi desenvolvida com foco em simplicidade, separação de responsabilidades e clareza de regras de domínio.

A regra central está no domínio de `Pagamento`, onde ficam as validações de transição de status e de inativação.
No restante da aplicação, as camadas estão organizadas em:
- `controller`: endpoints HTTP
- `service`: orquestração de casos de uso
- `repository`: persistência e filtros com Specifications
- `dto`: contratos de entrada e saída
- `exception`: tratamento centralizado de erros
- `validation`: validações customizadas

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Bean Validation (Jakarta Validation)
- H2 Database
- Lombok
- Springdoc OpenAPI (Swagger UI)
- JUnit 5 e Mockito

## Como executar

### Pre-requisitos

- Java 17 instalado
- Variável `JAVA_HOME` configurada

### Subir a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

A API sobe, por padrão, em `http://localhost:8080`.

## Documentacao da API (Swagger/OpenAPI)

A documentacao interativa da API esta disponivel via Swagger UI.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

Esses caminhos foram configurados em `src/main/resources/application.properties` pelas propriedades:
- `springdoc.swagger-ui.path=/swagger-ui.html`
- `springdoc.api-docs.path=/api-docs`

## Banco de dados e console H2

Configuração em `src/main/resources/application.properties` com H2 em memória.

Console H2:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:pagamentosdb`
- User: `sa`
- Password: (vazio)

## Testes

O projeto possui testes unitários para regras de domínio e serviço.

Executar testes no Windows:

```powershell
.\mvnw.cmd test
```

Executar testes no Linux/macOS:

```bash
./mvnw test
```

Observação: se o comando falhar com erro de ambiente, verifique a configuração do `JAVA_HOME`.

## Endpoints principais

Base: `/pagamentos`

- `POST /pagamentos`: cria um pagamento
- `PATCH /pagamentos/{id}/status`: altera status do pagamento
- `DELETE /pagamentos/{id}`: inativa pagamento (exclusão lógica)
- `GET /pagamentos`: lista pagamentos com filtros opcionais (`codigoDebito`, `cpfCnpj`, `status`)