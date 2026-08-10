# Inventory Service

Manages product stock, available stock, and reserved stock. It provides a direct stock-update endpoint and reacts to product and order events. Inventory changes are written to an outbox table for CDC publication.

## Technology

- Java 17 and Spring Boot 3.5
- Spring Web and Spring Data JPA
- MySQL
- Apache Kafka
- Debezium outbox CDC

## Local dependencies

| Dependency | Default address | Purpose |
| --- | --- | --- |
| MySQL | `localhost:3307/db_inventory` | Inventory and `outbox_inventory` tables |
| Kafka | `localhost:9092` | Product/order events |
| Kafka Connect | `http://localhost:8083` | Publishes outbox table changes |

The service uses MySQL credentials `root`/`root` in `application.yaml`. Create `db_inventory` before startup or provide equivalent Spring datasource overrides.

The included `docker-compose.yaml` starts Debezium Connect only; it expects reachable Kafka and MySQL containers. The gateway compose file contains the broader local infrastructure stack.

## Run locally

```powershell
mvn spring-boot:run
```

The service listens on port `8088`.

Run tests with:

```powershell
mvn test
```

## HTTP API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/v1/inventory/update` | Replace the stock value for a product |

Example request:

```json
{
  "productId": "product-123",
  "new_stock": 25
}
```

```powershell
curl.exe -X POST http://localhost:8088/api/v1/inventory/update `
  -H "Content-Type: application/json" `
  -d '{"productId":"product-123","new_stock":25}'
```

## Messaging

| Direction | Topic | Purpose |
| --- | --- | --- |
| Consumes | `inventory-new-product` | Create inventory for a newly created product |
| Consumes | `outbox.db_order.outbox_order` | Reserve, release, or finalize stock from order state changes |
| Publishes through CDC | `outbox.db_inventory.outbox_inventory` | Make updated available stock visible to product service |

Register the checked-in connector after Kafka Connect is healthy:

```powershell
curl.exe -X POST http://localhost:8083/connectors `
  -H "Content-Type: application/json" `
  --data-binary "@outbox_inventory_connector.json"
```

Review the connector's database host and credentials before registering it; they must match the MySQL instance used by this service.
