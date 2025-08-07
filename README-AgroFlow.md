
# 🧑‍🌾 AgroFlow - Sistema de Microservicios para Agricultura Inteligente

**AgroFlow** es una solución distribuida desarrollada completamente en **Spring Boot** para la gestión eficiente de agricultores, cosechas, inventario de insumos y facturación en cooperativas agrícolas.  
El sistema está basado en una arquitectura de **microservicios independientes**, comunicados de manera asincrónica a través de **RabbitMQ**, y desplegados en un clúster local usando **Kubernetes con Minikube**.

Cada microservicio se conecta a su propia instancia de **PostgreSQL**, y todo el sistema está contenedorizado con **Docker**, permitiendo un despliegue reproducible y escalable.



---


# 🚀 Proceso de Despliegue de AgroFlow en Kubernetes


## 📋 Prerrequisitos

Antes de empezar, asegúrate de tener instalado y funcionando:

- Docker Desktop (activo)
- Minikube iniciado (`minikube start`)
- `kubectl` configurado correctamente
- Maven instalado
- Java 17

---

## 🧪 PRUEBAS LOCALES

Es **recomendable probar cada microservicio por separado** en local antes de contenedorizarlos.

### Orden sugerido para ejecución local:

1. Verifica conexión y persistencia en PostgreSQL para cada MS.
2. Ejecuta primero `ms-central` (porque publica el evento).
3. Luego, ejecuta `ms-inventario` (consumidor de `nueva_cosecha`).
4. Después, `ms-facturacion` (otro consumidor de `nueva_cosecha` y emisor de `PUT`).
5. Usa Postman o curl para enviar `POST /cosechas` y validar el flujo completo.

---

## ⚡ DESPLIEGUE AUTOMATIZADO

### 1. Navega al directorio raíz del proyecto:

```bash
cd "C:\Users\Desktop\Agricultores"
```

### 2. Ejecuta el script de despliegue:

```bash
k8s\deploy.bat
```

> Este script aplica todos los YAML necesarios: namespace, base de datos, RabbitMQ, microservicios e ingress.

---

### ⚠️ 3. ¿Error de imagen no encontrada en Minikube?

Si ves errores como "ImagePullBackOff" o "ErrImagePull", debes cargar las imágenes localmente:

```bash
minikube image load agroflow-ms-central:latest
minikube image load agroflow-ms-inventario:latest
minikube image load agroflow-ms-facturacion:latest
```

✔️ Esto **resuelve el 100% de los errores de imagen en Minikube**.

---

## ✅ VERIFICACIÓN

Para verificar que todos los pods estén ejecutándose correctamente:

```bash
kubectl get pods -n agroflow
```

Acceder al dashboard:

```bash
minikube dashboard
```

---


---

## 🧾 Justificación del uso del archivo `deploy.bat`

El archivo `deploy.bat` automatiza el proceso de despliegue en Kubernetes para facilitar la configuración del entorno de desarrollo.  
Su objetivo es aplicar en orden todos los manifiestos YAML necesarios para levantar correctamente el sistema AgroFlow en Minikube, incluyendo:

1. Creación del namespace `agroflow`
2. Despliegue de la base de datos PostgreSQL
3. Despliegue de RabbitMQ
4. Despliegue de los tres microservicios (central, inventario, facturación)
5. Configuración del Ingress Controller (si se desea acceder vía dominio local)

Este script evita errores manuales al aplicar los manifiestos por separado y garantiza que el entorno se levante siempre con los mismos parámetros, reduciendo el riesgo de inconsistencias.

Para ejecutarlo:

```bash
k8s\deploy.bat
```

Este archivo puede ser editado para agregar validaciones adicionales o pasos personalizados, según las necesidades del entorno o del equipo de desarrollo.

---
## 📂 ESTRUCTURA DE ARCHIVOS `k8s/`

| Archivo YAML                 | Propósito                             |
|-----------------------------|---------------------------------------|
| `00-namespace.yaml`         | Crea el namespace `agroflow`          |
| `01-postgresql.yaml`        | Despliega base de datos PostgreSQL    |
| `02-rabbitmq.yaml`          | Despliega RabbitMQ + Management       |
| `03-ms-central.yaml`        | Despliega el microservicio central    |
| `04-ms-inventario.yaml`     | Despliega el microservicio inventario |
| `05-ms-facturacion.yaml`    | Despliega el microservicio facturación|
| `06-ingress.yaml`           | Habilita acceso externo               |
| `deploy.bat`                | Script que ejecuta todo automáticamente|

---

## 🌐 ACCESO A SERVICIOS VÍA PORT FORWARDING

Si no estás utilizando Ingress Controller o prefieres trabajar por separado, puedes acceder a los microservicios desde localhost usando `kubectl port-forward`. Abre 4 terminales y ejecuta:

```bash
kubectl port-forward service/ms-central-svc 8081:8081 -n agroflow
kubectl port-forward service/ms-inventario-svc 8082:8082 -n agroflow
kubectl port-forward service/ms-facturacion-svc 8083:8083 -n agroflow
kubectl port-forward service/rabbitmq-svc 15672:15672 -n agroflow
```

URLs activas:

- ms-central: http://localhost:8081
- ms-inventario: http://localhost:8082
- ms-facturacion: http://localhost:8083
- RabbitMQ UI: http://localhost:15672 (usuario: `admin`, clave: `admin123`)

---

## 🚪 ACCESO VÍA INGRESS

Si tienes un `Ingress Controller` configurado, también puedes acceder con un solo túnel al punto de entrada:

```bash
minikube tunnel
kubectl get ingress -n agroflow
```

Ejemplos de acceso (depende de tu configuración):

- http://ms-central.local
- http://ms-inventario.local
- http://ms-facturacion.local

---

## 🧪 PRUEBAS DEL FLUJO

### 1. Crear agricultor

```http
POST /agricultores
```

```json
{
  "nombre": "Juan Carlos",
  "apellidos": "Pérez González",
  "cedula": "101230456",
  "telefono": "+506 8888-1234",
  "email": "juan.perez@email.com",
  "direccion": "Cartago, Costa Rica"
}
```

### 2. Registrar cosecha

```http
POST /cosechas
```

```json
{
  "agricultorId": "uuid",
  "producto": "Arroz Oro",
  "toneladas": 10.5,
  "ubicacion": "Finca El Dorado, Lote 5"
}
```

### 3. Verificar inventario

```http
GET /inventario/insumos
```

### 4. Ver factura generada

```http
GET /facturas/cosecha/{id-cosecha}
```

### 5. Marcar factura como pagada

```http
PUT /facturas/{id}/pagar
```

---

## 🗂️ Archivos de prueba

- `AGRICULTORES.postman_collection.json` → colección con todos los endpoints organizados por módulo

---


---

## 🧪 PRUEBAS COMPLETAS CON POSTMAN

La colección `AGRICULTORES.postman_collection.json` contiene todos los endpoints de los 4 microservicios. A continuación, se describen **todas las pruebas organizadas por módulo**.

---

### 🌿 AGRICULTORES (http://localhost:8081)

#### 1. Crear Agricultor

**POST** `/agricultores`
```json
{
  "nombre": "Juan Carlos",
  "apellidos": "Pérez González",
  "cedula": "101230456",
  "telefono": "+506 8888-1234",
  "email": "juan.perez@email.com",
  "direccion": "Cartago, Costa Rica"
}
```

#### 2. Obtener todos los agricultores

**GET** `/agricultores`

#### 3. Obtener agricultor por ID

**GET** `/agricultores/{id}`

---

### 🌾 COSECHAS (http://localhost:8081)

#### 4. Registrar nueva cosecha

**POST** `/cosechas`
```json
{
  "agricultorId": "uuid-obtenido",
  "producto": "Arroz Oro",
  "toneladas": 10.5,
  "ubicacion": "Finca El Dorado, Lote 5"
}
```

#### 5. Obtener todas las cosechas

**GET** `/cosechas`

#### 6. Obtener cosecha por ID

**GET** `/cosechas/{id}`

#### 7. Cambiar estado de cosecha

**PUT** `/cosechas/{id}/estado`
```json
{
  "estado": "FACTURADA",
  "facturaId": "uuid-de-factura"
}
```

---

### 📦 INVENTARIO (http://localhost:8082)

#### 8. Ver inventario completo

**GET** `/inventario/insumos`

#### 9. Ver insumos con stock bajo

**GET** `/inventario/insumos/stock-bajo`

---

### 💰 FACTURACIÓN (http://localhost:8083)

#### 10. Obtener todas las facturas

**GET** `/facturas`

#### 11. Ver factura por ID

**GET** `/facturas/{id}`

#### 12. Obtener factura de una cosecha específica

**GET** `/facturas/cosecha/{id-cosecha}`

#### 13. Ver facturas pendientes

**GET** `/facturas/pendientes`

#### 14. Marcar factura como pagada

**PUT** `/facturas/{id}/pagar`

---

## 📂 Archivo de prueba

- Archivo: `AGRICULTORES.postman_collection.json`
- Contiene todas las pruebas organizadas y listas para importar a Postman.

---
