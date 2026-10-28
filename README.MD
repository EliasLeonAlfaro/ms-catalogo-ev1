# ms-catalogo

Microservicio encargado de administrar productos y stock del sistema Cloud Native.

## Descripción

`ms-catalogo` administra la información de los productos y su inventario.

Cuando `ms-pedidos` publica un evento `PEDIDO_ACEPTADO`, este microservicio consume el mensaje desde RabbitMQ y descuenta el stock correspondiente.

La comunicación es asíncrona y desacoplada.

## Tecnologías

- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Supabase
- Spring AMQP
- RabbitMQ
- Lombok
- Docker
- GitHub Actions

## Puerto

```text
8082
```

## Responsabilidades

- Administrar productos.
- Consultar productos.
- Gestionar stock.
- Consumir eventos RabbitMQ.
- Actualizar stock al aceptar pedidos.
- Controlar eventos duplicados.
- Aplicar ACK y NACK manual.
- Enviar mensajes inválidos a DLQ.
- Registrar eventos procesados.

## RabbitMQ

### Exchange principal

```text
pedidos.events
```

Tipo:

```text
topic
```

### Cola

```text
catalogo.stock.queue
```

### Routing key

```text
pedido.aceptado
```

### Dead Letter Exchange

```text
pedidos.dlx
```

Tipo:

```text
direct
```

### Dead Letter Queue

```text
catalogo.stock.dlq
```

### Routing key DLQ

```text
catalogo.stock.dlq
```

## Consumidor

El consumidor principal es:

```text
StockEventListener
```

Utiliza:

```java
@RabbitListener
```

## Procesamiento

Al recibir un evento válido:

1. Deserializa el mensaje.
2. Valida el `eventId`.
3. Valida el tipo de evento.
4. Valida la versión.
5. Valida producto y cantidad.
6. Comprueba si el evento ya fue procesado.
7. Actualiza el stock.
8. Registra el evento procesado.
9. Realiza commit de base de datos.
10. Envía ACK.

## Flujo exitoso

```text
pedidos.events
      |
      | pedido.aceptado
      v
catalogo.stock.queue
      |
      v
StockEventListener
      |
      v
Actualización de stock
      |
      v
processed_events
      |
      v
COMMIT
      |
      v
ACK
```

## ACK manual

Los mensajes válidos son confirmados manualmente después de completar correctamente la transacción.

Ejemplo:

```text
Evento procesado correctamente
ACK enviado
```

## Manejo de errores

Los errores no recuperables utilizan:

```text
NACK
requeue = false
```

Ejemplos:

- JSON inválido.
- `eventId` inexistente.
- Tipo de evento incorrecto.
- Versión no soportada.
- Producto inexistente.
- Cantidad inválida.
- Stock insuficiente.

## Dead Letter Queue

```text
catalogo.stock.queue
      |
      | NACK
      v
pedidos.dlx
      |
      v
catalogo.stock.dlq
```

Ejemplo de log:

```text
Mensaje rechazado. Será enviado a DLQ.
```

## Retry

```yaml
retry:
  enabled: true
  initial-interval: 1000
  max-attempts: 3
  multiplier: 2
  max-interval: 5000
```

## Idempotencia

La tabla:

```text
processed_events
```

almacena eventos ya procesados.

Esto evita descontar stock dos veces si un mensaje es entregado nuevamente.

## Actualización atómica de stock

Conceptualmente:

```sql
UPDATE products
SET stock = stock - :cantidad
WHERE id = :productoId
AND stock >= :cantidad;
```

## Configuración RabbitMQ

Los nombres se encuentran centralizados en:

```text
RabbitNames
```

Los beans de mensajería se encuentran en:

```text
RabbitConfig
```

## Variables de entorno

```text
SERVER_PORT

DB_URL
DB_USERNAME
DB_PASSWORD

RABBITMQ_HOST
RABBITMQ_PORT
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
RABBITMQ_VHOST

APP_RABBITMQ_LISTENER_ENABLED
```

## Compilación

Windows:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Linux:

```bash
./mvnw clean package -DskipTests
```

## Docker

```bash
docker build -t ms-catalogo .
```

Ejemplo:

```bash
docker run -d \
  --name ms-catalogo \
  --restart unless-stopped \
  --network cloudnative-network \
  -p 8082:8082 \
  --env-file catalogo.env \
  ms-catalogo
```

## Verificación

```bash
docker logs ms-catalogo --since 5m | \
grep -E "Evento procesado|ACK enviado|Mensaje rechazado"
```

## Verificar colas

```bash
docker exec rabbitmq \
  rabbitmqctl list_queues \
  -p pedidos670 \
  name messages messages_ready messages_unacknowledged
```

## Despliegue

El microservicio se despliega mediante:

```text
GitHub Actions
→ Amazon EC2
→ Docker
```

La rama `deploy` activa el pipeline.