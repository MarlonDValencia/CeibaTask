# BTG Pactual - Fondos de Inversión API

API REST para la gestión de fondos de inversión. Permite a los clientes suscribirse y cancelar fondos, consultar su historial de transacciones y recibir notificaciones.

## Stack

- Java 17 · Spring Boot 3.3.6 · MongoDB Atlas · Spring Security + JWT

## Requisitos

- JDK 17
- Maven (o usar el wrapper incluido `./mvnw`)
- Cuenta en MongoDB Atlas (o MongoDB local)

## Configuración

Definir las variables de entorno antes de correr:

```bash
export MONGODB_URI="mongodb+srv://<user>:<password>@cluster.mongodb.net/btg_fondos?retryWrites=true&w=majority"
export JWT_SECRET="base64"
```

## Correr la aplicación

```bash
./mvnw spring-boot:run
```

## Correr los tests

```bash
./mvnw test
```

## Endpoints

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | No | Registrar cliente |
| POST | `/api/auth/login` | No | Login, retorna JWT |
| GET | `/api/funds` | Si | Listar fondos disponibles |
| POST | `/api/funds/{id}/subscribe` | Si | Suscribirse a un fondo |
| POST | `/api/funds/{id}/unsubscribe` | Si | Cancelar suscripcion |
| GET | `/api/transactions` | Si | Historial de transacciones |
| GET | `/api/clients/me` | Si | Perfil y balance del cliente |

## Reglas de negocio

- Saldo inicial del cliente: **COP $500.000**
- Cada fondo tiene un monto mínimo de vinculación
- Al cancelar, el monto se devuelve al saldo
- Si no hay saldo suficiente: `"No tiene saldo disponible para vincularse al fondo <nombre>"`

## Fondos disponibles

| ID | Nombre | Mínimo | Categoría |
|----|--------|--------|-----------|
| 1 | FPV_BTG_PACTUAL_RECAUDADORA | $75.000 | FPV |
| 2 | FPV_BTG_PACTUAL_ECOPETROL | $125.000 | FPV |
| 3 | DEUDAPRIVADA | $50.000 | FIC |
| 4 | FDO-ACCIONES | $250.000 | FIC |
| 5 | FPV_BTG_PACTUAL_DINAMICA | $100.000 | FPV |

## Colección Postman

Importar `BTG_Fondos.postman_collection.json` en Postman para probar todos los flujos con tests automáticos.

## Parte 2 - SQL

La consulta SQL se encuentra en `sql/consulta.sql`.

## Infraestructura AWS

Despliegue en EC2 (Free Tier) con MongoDB Atlas:

- **EC2 t2.micro** — instancia gratuita corriendo la app con Java 17
- **MongoDB Atlas** — cluster gratuito (M0) en la nube
- **Security Group** — puerto 8080 abierto para tráfico HTTP

### Despliegue manual

```bash
# 1. Compilar el JAR
./mvnw clean package -DskipTests

# 2. Copiar el JAR a la instancia EC2
scp -i key.pem target/fondos-0.0.1-SNAPSHOT.jar ec2-user@<IP>:~/app.jar

# 3. Conectar por SSH y ejecutar
ssh -i key.pem ec2-user@<IP>
export MONGODB_URI="mongodb+srv://..."
export JWT_SECRET="..."
nohup java -jar app.jar &
```

La app queda disponible en `http://<IP-publica>:8080`.
