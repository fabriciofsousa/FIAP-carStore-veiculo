# 🚗 FIAP CarStore - Veículo


---

O **FIAP CarStore - Veículo** é o microsserviço responsável pelo gerenciamento dos veículos cadastrados na plataforma CarStore.

Ele também é o responsável por inicializar o banco de dados PostgreSQL e configurar a rede compartilhada entre os serviços. Dessa forma, todos os demais microsserviços (como o de Clientes) podem se conectar ao mesmo banco de dados sem esforço adicional.

Esse serviço é o ponto de partida do ecossistema CarStore, garantindo que a infraestrutura esteja pronta para que os outros módulos possam funcionar corretamente.

---

## ✅ Pré-requisitos

- Docker e Docker Compose instalados.

---

## ▶️ Para rodar localmente

---
Este projeto sobe a aplicação **Veículo** junto com o **Postgres**.  
Ele é o responsável por criar o banco de dados e a rede que serão utilizados por outros serviços, como o **Clientes**.
---

1. Baixe a imagem da aplicação **veículo** do Docker Hub:

```bash
docker pull fabriciofsousa/fiap-carstore-veiculo:latest
```

2. Suba os containers com o Docker Compose:

```bash
docker compose up -d
```

Isso irá criar:
- Um container **Postgres** chamado `postgres_carstore`
- Um container da aplicação **veículo**

---

## 🌐 Rede compartilhada

Ao subir este projeto, será criada a rede **carstore-network**.  
Outros projetos (como o `clientes`) utilizarão essa rede para compartilhar o mesmo banco de dados.

---

## 🔗 Endpoints

- Swagger Veículo: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
