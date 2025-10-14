# 🚗 FIAP CarStore - Veículo

---

O **FIAP CarStore - Veículo** é o microsserviço **principal e ponto de partida** do ecossistema CarStore.  
Ele é responsável por **gerenciar os veículos** cadastrados na plataforma e também por **provisionar toda a infraestrutura base** utilizada pelos demais módulos do sistema.

Ao ser executado, este serviço:
- **Inicializa o banco de dados PostgreSQL**, usado por outros microsserviços.
- **Cria a rede Docker compartilhada (`carstore-network`)**, permitindo comunicação entre os módulos.
- **Garante a base de dados** para o funcionamento do serviço de **Clientes** e **Vendas**.

Dessa forma, ele é a **porta de entrada** do projeto CarStore — nenhum outro microsserviço deve ser iniciado antes deste.

---

## 🧭 Visão geral do ecossistema CarStore

O projeto **CarStore** foi desenvolvido com uma arquitetura **modular e limpa (Clean Architecture)**, onde cada microsserviço é independente, mas colabora dentro de um mesmo domínio de negócio.

- **Veículo** → Responsável por gerenciar os dados de automóveis e pela infraestrutura base (rede e banco).
- **Clientes** → Gerencia as informações de usuários e se conecta ao mesmo banco PostgreSQL criado pelo módulo Veículo.
- **Vendas** → Centraliza as transações e integra com os outros dois serviços.

Com essa separação, o sistema é altamente **escalável, testável e fácil de manter**, além de seguir boas práticas de microsserviços.

---

## ✅ Pré-requisitos

- **Docker** e **Docker Compose** instalados.
- As portas **5432** (PostgreSQL) e **8082** (aplicação Veículo) devem estar livres.

---
## ☁️ Branches e Ambientes

| Branch | Ambiente | Descrição |
|--------|-----------|------------|
| `master` | **AWS Cloud** | Código utilizado para execução e deploy automático na AWS. |
| `release/docker` | **Local (Docker)** | Versão configurada para rodar em ambiente local via Docker Compose. |

---

## ▶️ Para rodar localmente(Branch release/docker)

> ⚙️ Este projeto sobe a aplicação **Veículo** junto com o **Postgres**.  
> Ele é responsável por criar o banco e a rede que serão utilizados pelos outros serviços, como **Clientes** e **Vendas**.

1. Clone o repositório e acesse a pasta do projeto:
```bash
git clone https://github.com/fabriciofsousa/FIAP-carStore-veiculo.git
git checkout release/docker
```

2. Baixe a imagem da aplicação **veículo** do Docker Hub:

```bash
docker pull fabriciofsousa/fiap-carstore-veiculo:latest
```

3. Suba os containers com o Docker Compose:

```bash
docker compose up -d
```

Isso irá criar:
- Um container **Postgres** chamado `postgres_carstore`
- Um container da aplicação **veículo**
- Uma rede externa chamada **carstore-network**

---

## 🌐 Rede compartilhada

A rede **carstore-network** é criada automaticamente pelo serviço de **Veículo**.  
Outros projetos — como `clientes` e `vendas` — devem usar essa mesma rede no `docker-compose.yml` para se comunicar entre si.

---

## 🧩 Arquitetura e Benefícios

O módulo **Veículo** segue os princípios da **Clean Architecture**, separando as responsabilidades em camadas:

- **Domain:** contém as regras de negócio de cadastro e gestão de veículos.
- **Infra:** lida com o banco PostgreSQL e comunicação com outros módulos.
- **Interface (Controller):** expõe os endpoints REST da aplicação.

Essa abordagem garante:
- **Facilidade para testar e manter** o código.
- **Flexibilidade tecnológica**, permitindo trocar banco ou framework sem afetar o domínio.
- **Organização e clareza**, facilitando o entendimento geral do ecossistema CarStore.

---

## 🔗 Endpoints

- Swagger Veículo: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

---

## 📦 Collection Postman

> [Fiap-Carstore.postman_collection.json](src%2Fmain%2Fresources%2FFiap-Carstore.postman_collection.json)
> 
> [workspace.postman_globals.json](src%2Fmain%2Fresources%2Fworkspace.postman_globals.json)

