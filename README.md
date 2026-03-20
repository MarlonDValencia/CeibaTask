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

## API en produccion

- **Base URL:** http://18.222.189.172:8080
- **Swagger UI:** http://18.222.189.172:8080/swagger-ui/index.html

## Endpoints

| Metodo | Endpoint | Auth | Descripcion |
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

Despliegue en EC2 (Free Tier) con MongoDB Atlas. Template de CloudFormation en `infrastructure/template.yml`.

- **EC2 t3.micro** — instancia free tier corriendo la app con Java 17
- **MongoDB Atlas** — cluster gratuito (M0) en la nube
- **Security Group** — puertos 22 (SSH) y 8080 (HTTP) abiertos

## CI/CD

Pipeline automatizado con GitHub Actions (`.github/workflows/deploy.yml`):

1. **Tests** — ejecuta `./mvnw test`
2. **Validacion CloudFormation** — valida `infrastructure/template.yml`
3. **Build** — genera el JAR con `./mvnw clean package`
4. **Deploy** — copia el JAR al EC2 via SCP y reinicia la app

El pipeline se ejecuta automaticamente en cada push a `main`.
