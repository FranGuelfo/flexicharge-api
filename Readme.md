🚀 FlexiCharge API
FlexiCharge API es una solución robusta para la gestión de clientes y planes de suscripción. Diseñada con un enfoque en la calidad del código, la seguridad mediante JWT y la escalabilidad, esta API sirve como el motor central para sistemas de facturación o membresías.

🛠️ Tecnologías Utilizadas
Backend: Java 17 con Spring Boot 3.x.

Seguridad: Spring Security + JWT (JSON Web Tokens).

Base de Datos: MongoDB (NoSQL) para una persistencia flexible.

Mapeo de Datos: MapStruct para una conversión eficiente entre Entidades y DTOs.

Documentación: Swagger / OpenAPI 3.

Calidad de Código: SonarQube & JaCoCo (Cobertura del 100%).

Testing: JUnit 5, Mockito y MockMvc.

🏛️ Arquitectura del Proyecto
El proyecto sigue una arquitectura limpia dividida por capas para facilitar el mantenimiento y el testing unitario:

Controller: Endpoints REST que gestionan las peticiones HTTP.

Service: Lógica de negocio pura, aislada de la infraestructura.

Repository: Capa de acceso a datos con Spring Data MongoDB.

Model/DTO: Objetos de transferencia de datos y entidades de persistencia.

Security: Configuración de filtros, proveedores de autenticación y manejo de JWT.

🔐 Endpoints Principales
Autenticación (/api/auth)
POST /register: Registro de nuevos usuarios.

POST /login: Autenticación y obtención del token JWT.

Clientes (/api/customers)
GET /: Listar todos los clientes.

GET /search?email=...: Buscar cliente por email.

POST /: Crear un nuevo cliente.

DELETE /{id}: Eliminar un cliente.

Planes (/api/plans)
GET /: Listar planes disponibles.

POST /: Crear un nuevo plan de suscripción.

DELETE /{id}: Eliminar un plan.

🧪 Calidad y Testing
Este proyecto destaca por su alto estándar de calidad. Actualmente cuenta con una cobertura de código del 100%, verificada mediante SonarQube.

Ejecutar Tests Localmente
Para ejecutar la suite de pruebas y generar el informe de cobertura:

Bash
mvn clean verify
Análisis con SonarQube
Si tienes una instancia de SonarQube corriendo localmente:

Bash
mvn sonar:sonar \
-Dsonar.projectKey=flexicharge \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=TU_TOKEN_AQUI
⚙️ Instalación y Configuración
Clonar el repositorio:

Bash
git clone https://github.com/FranGuelfo/flexicharge-api.git
cd flexicharge-api
Configurar variables de entorno:
Asegúrate de tener instalado MongoDB y configurar el archivo application.properties con tus credenciales.

Ejecutar la aplicación:

Bash
mvn spring-boot:run
Acceder a la documentación:
Una vez arrancado, visita http://localhost:8080/swagger-ui.html.

🔜 Próximos Pasos (Roadmap)
[ ] Integración Continua (CI) con Jenkins.

[ ] Despliegue automatizado en contenedores Docker.

[ ] Implementación de notificaciones vía Email tras el registro.

👤 Autor
Fran Guelfo - GitHub