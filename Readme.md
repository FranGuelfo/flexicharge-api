# 🚀 FlexiCharge API

¡Bienvenido a **FlexiCharge API**! Una solución robusta para la gestión de clientes y planes de suscripción, desarrollada con **Spring Boot 3.x** y enfocada en la excelencia técnica.

---

## 📊 Estado del Proyecto

Actualmente, el proyecto cuenta con los más altos estándares de calidad:

* ✅ **Cobertura de Código:** 100% (Verificado con JaCoCo & SonarQube).
* 🛡️ **Seguridad:** Implementada mediante JWT y Spring Security.
* 📖 **Documentación:** Disponible vía Swagger UI.

---

## 🛠️ Stack Tecnológico

| Tecnología | Uso |
| :--- | :--- |
| **Java 17** | Lenguaje principal |
| **Spring Boot 3** | Framework base |
| **MongoDB** | Base de datos NoSQL |
| **MapStruct** | Mapeo de DTOs |
| **JUnit 5 / Mockito** | Testing unitario y mocks |
| **SonarQube** | Análisis de calidad estático |

---

## 🏛️ Arquitectura

El proyecto sigue un patrón de **diseño por capas**, asegurando que cada componente tenga una única responsabilidad:

1.  **Controllers:** Gestión de peticiones REST.
2.  **Services:** Lógica de negocio e integraciones.
3.  **Repositories:** Persistencia en MongoDB.
4.  **Security:** Filtros de autenticación y manejo de tokens.

---

## 🔐 Endpoints Principales

### 🔑 Autenticación (`/api/auth`)
* `POST /register` - Registro de nuevos usuarios.
* `POST /login` - Obtención de token JWT.

### 👥 Clientes (`/api/customers`)
* `GET /` - Listado completo.
* `POST /` - Alta de cliente.
* `DELETE /{id}` - Baja de sistema.

### 💳 Planes (`/api/plans`)
* `GET /` - Consulta de planes activos.
* `POST /` - Creación de nuevas tarifas.

---

## 🧪 Testing y Calidad

Para asegurar la integridad del código, el proyecto mantiene una cobertura total.

### Ejecutar los tests:
bash

mvn clean verify
Análisis de SonarQube:

Bash

mvn sonar:sonar \
  -Dsonar.projectKey=flexicharge \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=TU_TOKEN


### ⚙️ Configuración Local

Clonación: git clone https://github.com/FranGuelfo/flexicharge-api.git

Base de Datos: Asegúrate de tener MongoDB corriendo en el puerto 27017.

Arranque: Ejecuta mvn spring-boot:run desde la terminal.

Swagger: Accede a http://localhost:8080/swagger-ui.html.

### 👤 Autor

Desarrollado con ❤️ por Fran Guelfo.