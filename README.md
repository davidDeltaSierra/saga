# Saga

Orquestrador de eventos baseado no padrão de projeto saga pattern.

## iniciando

Buildando o projeto ex:
- docker build -t daviddeltasierra/saga .

Rodando o projeto ex:
- docker run -d -p 8080:8080 -e spring.profiles.active=prd -e db.host=host.docker.internal:3306 -e db.username=root -e db.password=root -e rabbitmq.host=host.docker.internal daviddeltasierra/saga

## Variáveis de ambiente

- spring.profiles.active
- db.host
- db.username
- db.password
- server.port: default 8080
- rabbitmq.port: default 5672
- rabbitmq.host
- rabbitmq.password: default guest
- rabbitmq.username: default guest
- topic.name: default saga.topic
- queue.success: default saga.queue-success
- queue.fallback: default saga.queue-fallback