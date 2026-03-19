# Lab P4 - BluePrints Real Time (Monorepo)


> **Repositorio:** `DECSIS-ECI/Lab_P4_BluePrints_RealTime-Sokets`  
> **Front:** React + Vite (Canvas, CRUD, y selector de tecnología RT)  
> **Backends guía (elige uno o compáralos):**
> - **Socket.IO (Node.js):** https://github.com/DECSIS-ECI/example-backend-socketio-node-/blob/main/README.md
> - **STOMP (Spring Boot):** https://github.com/DECSIS-ECI/example-backend-stopm/tree/main

## 🎯 Objetivo del laboratorio
Implementar **colaboración en tiempo real** para el caso de BluePrints. El Front consume la API CRUD de la Parte 3 (o equivalente) y habilita tiempo real usando **Socket.IO** o **STOMP**, para que múltiples clientes dibujen el mismo plano de forma simultánea.

Al finalizar, el equipo debe:
1. Integrar el Front con su **API CRUD** (listar/crear/actualizar/eliminar planos, y total de puntos por autor).
2. Conectar el Front a un backend de **tiempo real** (Socket.IO **o** STOMP) siguiendo los repos guía.
3. Demostrar **colaboración en vivo** (dos pestañas navegando el mismo plano).

---

## 🧩 Alcance y criterios funcionales
- **CRUD** (REST):
  - `GET /api/blueprints?author=:author` → lista por autor (incluye total de puntos).
  - `GET /api/blueprints/:author/:name` → puntos del plano.
  - `POST /api/blueprints` → crear.
  - `PUT /api/blueprints/:author/:name` → actualizar.
  - `DELETE /api/blueprints/:author/:name` → eliminar.
- **Tiempo real (RT)** (elige uno):
  - **Socket.IO** (rooms): `join-room`, `draw-event` → broadcast `blueprint-update`.
  - **STOMP** (topics): `@MessageMapping("/draw")` → `convertAndSend(/topic/blueprints.{author}.{name})`.
- **UI**:
  - Canvas con **dibujo por clic** (incremental).
  - Panel del autor: **tabla** de planos y **total de puntos** (`reduce`).
  - Barra de acciones: **Create / Save/Update / Delete** y **selector de tecnología** (None / Socket.IO / STOMP).
- **DX/Calidad**: código limpio, manejo de errores, README de equipo.

---

## 🏗️ Arquitectura (visión rápida)

```
React (Vite)
 ├─ HTTP (REST CRUD + estado inicial) ───────────────> Tu API (P3 / propia)
 └─ Tiempo Real (elige uno):
     ├─ Socket.IO: join-room / draw-event ──────────> Socket.IO Server (Node)
     └─ STOMP: /app/draw -> /topic/blueprints.* ────> Spring WebSocket/STOMP
```

**Convenciones recomendadas**  
- **Plano como canal/sala**: `blueprints.{author}.{name}`  
- **Payload de punto**: `{ x, y }`

---

## 📦 Repos guía (clona/consulta)
- **Socket.IO (Node.js)**: https://github.com/DECSIS-ECI/example-backend-socketio-node-/blob/main/README.md  
  - *Uso típico en el cliente:* `io(VITE_IO_BASE, { transports: ['websocket'] })`, `join-room`, `draw-event`, `blueprint-update`.
- **STOMP (Spring Boot)**: https://github.com/DECSIS-ECI/example-backend-stopm/tree/main  
  - *Uso típico en el cliente:* `@stomp/stompjs` → `client.publish('/app/draw', body)`; suscripción a `/topic/blueprints.{author}.{name}`.

---

## ⚙️ Variables de entorno (Front)
Crea `.env.local` en la raíz del proyecto **Front**:
```bash
# REST (tu backend CRUD)
VITE_API_BASE=http://localhost:8080

# Tiempo real: apunta a uno u otro según el backend que uses
VITE_IO_BASE=http://localhost:3001     # si usas Socket.IO (Node)
VITE_STOMP_BASE=http://localhost:8080  # si usas STOMP (Spring)
```
En la UI, selecciona la tecnología en el **selector RT**.

---

## 🚀 Puesta en marcha

### 1) Backend RT (elige uno)

**Opción A — Socket.IO (Node.js)**  
Sigue el README del repo guía:  
https://github.com/DECSIS-ECI/example-backend-socketio-node-/blob/main/README.md
```bash
npm i
npm run dev
# expone: http://localhost:3001
# prueba rápida del estado inicial:
curl http://localhost:3001/api/blueprints/juan/plano-1
```

**Opción B — STOMP (Spring Boot)**  
Sigue el repo guía:  
https://github.com/DECSIS-ECI/example-backend-stopm/tree/main
```bash
./mvnw spring-boot:run
# expone: http://localhost:8080
# endpoint WS (ej.): /ws-blueprints
```

### 2) Front (este repo)
```bash
npm i
npm run dev
# http://localhost:5173
```
En la interfaz: selecciona **Socket.IO** o **STOMP**, define `author` y `name`, abre **dos pestañas** y dibuja en el canvas (clics).

---

## 🔌 Protocolos de Tiempo Real (detalle mínimo)

### A) Socket.IO
- **Unirse a sala**
  ```js
  socket.emit('join-room', `blueprints.${author}.${name}`)
  ```
- **Enviar punto**
  ```js
  socket.emit('draw-event', { room, author, name, point: { x, y } })
  ```
- **Recibir actualización**
  ```js
  socket.on('blueprint-update', (upd) => { /* append points y repintar */ })
  ```

### B) STOMP
- **Publicar punto**
  ```js
  client.publish({ destination: '/app/draw', body: JSON.stringify({ author, name, point }) })
  ```
- **Suscribirse a tópico**
  ```js
  client.subscribe(`/topic/blueprints.${author}.${name}`, (msg) => { /* append points y repintar */ })
  ```

---

## 🧪 Casos de prueba mínimos
- **Estado inicial**: al seleccionar plano, el canvas carga puntos (`GET /api/blueprints/:author/:name`).  
- **Dibujo local**: clic en canvas agrega puntos y redibuja.  
- **RT multi-pestaña**: con 2 pestañas, los puntos se **replican** casi en tiempo real.  
- **CRUD**: Create/Save/Delete funcionan y refrescan la lista y el **Total** del autor.

---

## 📊 Entregables del equipo
1. Código del Front integrado con **CRUD** y **RT** (Socket.IO o STOMP).  
2. **Video corto** (≤ 90s) mostrando colaboración en vivo y operaciones CRUD.  
  se encuentra en la carpeta de docs
3. **README del equipo**: setup, endpoints usados, decisiones (rooms/tópicos), y (opcional) breve comparativa Socket.IO vs STOMP.

---

## 🧮 Rúbrica sugerida
- **Funcionalidad (40%)**: RT estable (join/broadcast), aislamiento por plano, CRUD operativo.  
- **Calidad técnica (30%)**: estructura limpia, manejo de errores, documentación clara.  
- **Observabilidad/DX (15%)**: logs útiles (conexión, eventos), health checks básicos.  
- **Análisis (15%)**: hallazgos (latencia/reconexión) y, si aplica, pros/cons Socket.IO vs STOMP.

---

## 🩺 Troubleshooting
- **Pantalla en blanco (Front)**: revisa consola; confirma `@vitejs/plugin-react` instalado y que `AppP4.jsx` esté en `src/`.  
- **No hay broadcast**: ambas pestañas deben hacer `join-room` al **mismo** plano (Socket.IO) o suscribirse al **mismo tópico** (STOMP).  
- **CORS**: en dev permite `http://localhost:5173`; en prod, **restringe orígenes**.  
- **Socket.IO no conecta**: fuerza transporte WebSocket `{ transports: ['websocket'] }`.  
- **STOMP no recibe**: verifica `brokerURL`/`webSocketFactory` y los prefijos `/app` y `/topic` en Spring.

---

## 🔐 Seguridad (mínimos)
- Validación de payloads (p. ej., zod/joi).  
- Restricción de orígenes en prod.  
- Opcional: **JWT** + autorización por plano/sala.

---

## 📄 Licencia
MIT (o la definida por el curso/equipo).


# Lab P4 - Solución 
 **Documento:**
 https://docs.google.com/document/d/1PxvjWKi7f80hp1fVLm8gVR7YhPoawfCOqp0ZoJShKoM/edit?usp=sharing

 
## 1. Resumen
Este repositorio implementa BluePrints colaborativo en tiempo real para el laboratorio.

Decision tecnica principal:
- Tecnologia RT elegida: STOMP sobre WebSocket con Spring Boot.

El front permite selector Socket.IO y STOMP para pruebas/comparacion, pero la implementacion oficial del laboratorio en este repo se soporta con STOMP.

## 2. Estructura del repositorio

```text
Lab_P4_MONOREPO_BluePrints_RealTime-Sokets/
├─ backend/   # Spring Boot (REST CRUD + STOMP + seguridad + observabilidad)
└─ frontend/  # React + Vite (Canvas, CRUD, cliente STOMP/Socket.IO, validaciones)
```

Archivos clave:
- Backend:
  - backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
  - backend/src/main/java/co/edu/eci/blueprints/rt/BlueprintRTController.java
  - backend/src/main/java/co/edu/eci/blueprints/rt/WebSocketConfig.java
  - backend/src/main/java/co/edu/eci/blueprints/security/CorsConfig.java
  - backend/src/main/java/co/edu/eci/blueprints/api/ValidationExceptionHandler.java
  - backend/src/main/java/co/edu/eci/blueprints/api/HealthController.java
  - backend/src/main/java/co/edu/eci/blueprints/rt/WebSocketEventsLogger.java
- Frontend:
  - frontend/src/App.jsx
  - frontend/src/lib/stompClient.js
  - frontend/src/lib/payloadSchemas.js

## 3. Como correr el proyecto

### 3.1 Prerrequisitos
- Java 21
- Maven
- Node.js + npm
- PostgreSQL

Base de datos esperada por defecto:
- DB: mi_basedatos
- User: admin
- Password: admin123
- Puerto: 5432

### 3.2 Backend

```bash
cd backend
mvn spring-boot:run
```

Backend disponible en:
- http://localhost:8080

Health checks:
- http://localhost:8080/api/health
- http://localhost:8080/actuator/health

### 3.3 Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend disponible en:
- http://localhost:5173

## 4. Implementacion de lo solicitado por el laboratorio

### 4.1 CRUD (REST)
Se implementaron los endpoints solicitados:
- GET /api/blueprints?author=:author
- GET /api/blueprints/:author/:name
- POST /api/blueprints
- PUT /api/blueprints/:author/:name
- DELETE /api/blueprints/:author/:name

Referencia:
- backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java

### 4.2 Tiempo real (STOMP elegido)
Implementacion STOMP:
- Publicacion desde cliente: /app/draw
- Broadcast a topico por plano: /topic/blueprints.{author}.{name}
- Endpoint WS: /ws-blueprints

Referencias:
- backend/src/main/java/co/edu/eci/blueprints/rt/WebSocketConfig.java
- backend/src/main/java/co/edu/eci/blueprints/rt/BlueprintRTController.java
- frontend/src/lib/stompClient.js
- frontend/src/App.jsx

### 4.3 UI funcional
Incluye:
- Canvas con dibujo incremental por clic.
- Panel de autor con lista y total de puntos.
- Acciones Create / Save / Delete.
- Selector de tecnologia RT.

Referencias:
- frontend/src/App.jsx
- frontend/src/AuthorPanel.jsx

## 5. Seguridad (minimos)

### 5.1 Validacion de payloads
Se implemento en dos niveles:
- Frontend (zod): valida author, name, points y rangos de coordenadas.
- Backend (Bean Validation): valida DTOs y requests en REST y RT.

Referencias:
- frontend/src/lib/payloadSchemas.js
- backend/src/main/java/co/edu/eci/blueprints/dto/PointDTO.java
- backend/src/main/java/co/edu/eci/blueprints/dto/DrawEvent.java
- backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
- backend/src/main/java/co/edu/eci/blueprints/api/ValidationExceptionHandler.java

### 5.2 Restriccion de origenes en produccion
Se implemento CORS configurable por propiedad:
- app.security.cors.allowed-origins

En dev queda localhost y en prod se define por variable de entorno.

Referencias:
- backend/src/main/java/co/edu/eci/blueprints/security/CorsConfig.java
- backend/src/main/java/co/edu/eci/blueprints/rt/WebSocketConfig.java
- backend/src/main/resources/application.properties
- backend/src/main/resources/application-prod.properties

## 6. Observabilidad y DX
Se agregaron:
- Logs de eventos WS: CONNECT, CONNECTED, SUBSCRIBE, DISCONNECT.
- Logs de eventos draw recibidos por backend.
- Logs de conexion/reconexion en cliente STOMP y Socket.IO.
- Medicion simple de latencia por punto en frontend.
- Health check basico propio + Actuator health.

Referencias:
- backend/src/main/java/co/edu/eci/blueprints/rt/WebSocketEventsLogger.java
- backend/src/main/java/co/edu/eci/blueprints/rt/BlueprintRTController.java
- backend/src/main/java/co/edu/eci/blueprints/api/HealthController.java
- frontend/src/lib/stompClient.js
- frontend/src/lib/socketIoClient.js
- frontend/src/App.jsx

## 7. Analisis de hallazgos
Resumen de los hallazgos observados en pruebas:
- RT funciona correctamente: se ven eventos draw y propagacion por topico de plano.
- Conexion WS estable: no se observaron cierres anormales de transporte.
- Se detecto reconexion/suscripcion frecuente al editar author/name en vivo (churn de sesiones).
- El health check reporta estado de servicio y base de datos correctamente.

Recomendacion de mejora (no bloqueante):
- Aplicar debounce o accion explicita de conectar para evitar reconectar en cada tecla.

## 8. Estado del item opcional
- JWT + autorizacion por plano/sala: no implementado (item opcional en la guia).

---

## 🚀 Ejecución con Docker Compose

Para levantar todos los servicios (frontend y backend) usando Docker Compose, ejecuta:

```
docker-compose up --build
```

Esto construirá y levantará los contenedores definidos en el archivo docker-compose.yml.


