# 🏨 Professional Challenge API - Plataforma de Reservas

## 📋 Descripción del Proyecto

API REST desarrollada en **Java 21 y Spring Boot** para gestionar la información de una plataforma de reservas de alojamientos.

El backend permite administrar alojamientos de diferentes tipos, incluyendo hoteles, hostales, apartamentos, casas y glamping. La API proporciona los recursos necesarios para consultar, crear, actualizar y eliminar alojamientos, además de consultar las categorías y ciudades disponibles.

El backend es consumido por el frontend desarrollado en **React + Vite** y utiliza una base de datos **H2 en memoria** para el entorno de desarrollo.

## ✨ Funcionalidades Implementadas

### Gestión de Alojamientos

- ✅ **Crear alojamiento**: Permite registrar un alojamiento con nombre, descripción, categoría, ciudad, dirección e imágenes.
- ✅ **Listado de alojamientos**: Permite obtener todos los alojamientos registrados.
- ✅ **Consultar alojamiento por ID**: Permite obtener la información de un alojamiento específico.
- ✅ **Eliminar alojamiento**: Permite eliminar un alojamiento mediante su ID.
- ✅ **Validación de nombres duplicados**: El backend verifica que no exista otro producto con el mismo nombre, ignorando mayúsculas y minúsculas.
- ✅ **Validación de categoría**: Verifica que la categoría enviada exista en la base de datos.
- ✅ **Validación de ciudad**: Verifica que la ciudad enviada exista en la base de datos.

### Catálogos

- ✅ **Listado de categorías**: Endpoint para consultar las categorías disponibles.
- ✅ **Listado de ciudades**: Endpoint para consultar las ciudades disponibles.
- ✅ **Carga inicial de datos**: `data.sql` registra las categorías y ciudades iniciales.

### Gestión de Imágenes

- ✅ **Carga de imágenes**: Permite recibir archivos mediante `MultipartFile` y almacenarlos localmente en la carpeta `uploads/`.
- ✅ **URL de imagen**: Las imágenes almacenadas se exponen mediante la ruta `/uploads/**`.
- ✅ **Agregar imagen mediante URL**: Permite asociar una URL de imagen a un producto.
- ✅ **Eliminar imagen**: Permite eliminar una imagen asociada a un producto.
- ✅ **Ordenar imágenes**: Permite actualizar el orden de visualización de una imagen.

## 🏗️ Arquitectura del Proyecto

El proyecto utiliza una arquitectura por capas basada en la separación de responsabilidades:

```text
Controller → Service → Repository → Database
```

La estructura principal del proyecto es:

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── jc/
│   │           └── professional_challenge_api/
│   │               ├── config/
│   │               │   └── WebConfig.java
│   │               │
│   │               ├── controller/
│   │               │   ├── ProductController.java
│   │               │   ├── CategoryController.java
│   │               │   ├── CityController.java
│   │               │   └── dto/
│   │               │       └── ProductImageRequest.java
│   │               │
│   │               ├── entities/
│   │               │   ├── Product.java
│   │               │   ├── Category.java
│   │               │   ├── City.java
│   │               │   ├── Address.java
│   │               │   └── ProductImage.java
│   │               │
│   │               ├── repository/
│   │               │   ├── ProductRepository.java
│   │               │   ├── CategoryRepository.java
│   │               │   ├── CityRepository.java
│   │               │   └── AddressRepository.java
│   │               │
│   │               └── service/
│   │                   ├── ProductService.java
│   │                   ├── CategoryService.java
│   │                   └── CityService.java
│   │
│   └── resources/
│       ├── application.properties
│       └── data.sql
│
└── test/
    └── java/
        └── com/jc/professional_challenge_api/
            └── ProfessionalChallengeApiApplicationTests.java
```

## 🧩 Capas de la Arquitectura

### 1. **Entities**

Representan las entidades principales de la aplicación y su estructura en la base de datos.

- `Product`: representa un alojamiento.
- `Category`: representa el tipo de alojamiento.
- `City`: representa la ciudad del alojamiento.
- `Address`: contiene la dirección del alojamiento.
- `ProductImage`: representa una imagen asociada a un alojamiento.

### 2. **Repositories**

Utilizan **Spring Data JPA** para realizar las operaciones de persistencia.

- `ProductRepository`
- `CategoryRepository`
- `CityRepository`
- `AddressRepository`

`ProductRepository` incluye la validación:

```java
boolean existsByNameIgnoreCase(String name);
```

que permite comprobar si ya existe un alojamiento con el mismo nombre.

### 3. **Services**

Contienen la lógica de negocio de la aplicación.

#### `ProductService`

Es el servicio principal y se encarga de:

- Crear productos.
- Consultar productos.
- Actualizar productos.
- Eliminar productos.
- Validar nombres duplicados.
- Validar categorías.
- Validar ciudades.
- Subir imágenes.
- Agregar imágenes mediante URL.
- Eliminar imágenes.
- Actualizar el orden de las imágenes.

#### `CategoryService`

Gestiona la consulta de las categorías disponibles.

#### `CityService`

Gestiona la consulta de las ciudades disponibles.

### 4. **Controllers**

Exponen los endpoints REST que consume el frontend.

- `ProductController`
- `CategoryController`
- `CityController`

### 5. **DTO**

`ProductImageRequest` es un `record` utilizado para recibir la información de una imagen mediante URL y orden de visualización.

```java
public record ProductImageRequest(String url, Integer displayOrder) {}
```

### 6. **Config**

`WebConfig` contiene la configuración de:

- CORS para permitir peticiones desde el frontend.
- Recursos estáticos para servir las imágenes almacenadas en `uploads/`.

## 🛠️ Stack Tecnológico

- **Lenguaje**: Java 21
- **Framework**: Spring Boot 4.1.0
- **API**: REST
- **Persistencia**: Spring Data JPA
- **ORM**: Hibernate
- **Base de datos**: H2, en memoria para desarrollo
- **Build Tool**: Maven
- **Serialización JSON**: Jackson
- **Carga de archivos**: Spring Web MVC + `MultipartFile`
- **Testing**: JUnit 5 + Spring Boot Test
- **CORS**: Spring Web MVC

## 🔌 Endpoints de la API

### Productos

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/products` | Obtiene todos los alojamientos |
| `GET` | `/products/{id}` | Obtiene un alojamiento por ID |
| `POST` | `/products` | Crea un nuevo alojamiento |
| `PUT` | `/products/{id}` | Actualiza un alojamiento |
| `DELETE` | `/products/{id}` | Elimina un alojamiento |

### Categorías

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/categories` | Obtiene todas las categorías |

### Ciudades

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/cities` | Obtiene todas las ciudades |

### Imágenes

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/products/{productId}/image` | Asocia una imagen mediante URL |
| `POST` | `/products/{productId}/images/upload` | Sube una imagen mediante archivo |
| `DELETE` | `/products/{productId}/images/{imageId}` | Elimina una imagen |
| `PATCH` | `/products/{productId}/images/{imageId}/order` | Actualiza el orden de una imagen |

## 📦 Ejemplo de Creación de un Alojamiento

### Request

```http
POST /products
Content-Type: application/json
```

```json
{
  "name": "Hotel Nexo",
  "description": "Alojamiento ubicado cerca del centro.",
  "category": {
    "id": 1
  },
  "city": {
    "id": 1
  },
  "address": {
    "direction": "Carrera 10 #20-30"
  },
  "images": []
}
```

### Respuesta

El endpoint devuelve el alojamiento creado en formato JSON y utiliza el código HTTP:

```text
201 Created
```

Si el nombre ya existe, la API devuelve:

```text
409 Conflict
```

con un mensaje indicando que ya existe un producto con ese nombre.

## 🗄️ Base de Datos

El proyecto utiliza **H2 Database** como base de datos en memoria durante el desarrollo.

Configuración actual:

```properties
spring.datasource.url=jdbc:h2:mem:products
spring.datasource.username=sa
spring.datasource.password=sa
```

Hibernate genera las tablas automáticamente mediante:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

Esto significa que la estructura de la base de datos se crea al iniciar la aplicación y se elimina al detenerla.

### Datos Iniciales

El archivo:

```text
src/main/resources/data.sql
```

contiene los datos iniciales de:

- 5 categorías.
- 20 ciudades colombianas.

Las categorías actualmente configuradas son:

- Hotel
- Hostal
- Apartamento
- Casa
- Glamping

## 🖼️ Almacenamiento de Imágenes

Las imágenes subidas mediante archivo se almacenan localmente en:

```text
uploads/
```

La aplicación crea el directorio automáticamente cuando es necesario.

Los archivos se almacenan utilizando un nombre generado a partir del timestamp y el nombre original del archivo.

Ejemplo:

```text
uploads/1788919439368_images.jpeg
```

Las imágenes se sirven mediante:

```text
http://localhost:8080/uploads/{nombre-del-archivo}
```

La configuración de recursos estáticos se encuentra en `WebConfig`.

El tamaño máximo configurado para los archivos es:

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## 🔗 Conexión con el Frontend

El frontend desarrollado en React utiliza esta API como fuente de datos.

La URL base utilizada durante el desarrollo es:

```text
http://localhost:8080
```

El frontend realiza peticiones HTTP a los siguientes recursos:

```text
React
  ↓
productService.js
  ↓
HTTP Request
  ↓
Spring Boot
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
H2 Database
```

La configuración de CORS permite peticiones desde:

```text
http://localhost:5173
```

## 🏛️ Modelo de Datos

Las principales relaciones entre las entidades son:

```text
                 ┌──────────────┐
                 │   Category   │
                 └──────┬───────┘
                        │
                        │ N:1
                        │
┌─────────────┐    ┌────▼─────┐
│   Address   │◄───│  Product │
└─────────────┘ 1:1└────┬─────┘
                        │
                        │ N:1
                        │
                 ┌──────▼──────┐
                 │     City     │
                 └──────────────┘

                        │
                        │ 1:N
                        │
                 ┌──────▼───────┐
                 │ ProductImage │
                 └──────────────┘
```

### Relaciones principales

- `Product → Category`: muchos productos pueden pertenecer a una categoría.
- `Product → City`: muchos productos pueden pertenecer a una ciudad.
- `Product → Address`: un producto tiene una dirección asociada.
- `Product → ProductImage`: un producto puede tener múltiples imágenes.

## 🧠 Lógica de Negocio

La creación de un producto realiza diferentes validaciones antes de guardarlo.

### Validación de nombre

Se verifica que no exista otro producto con el mismo nombre:

```java
if (productRepository.existsByNameIgnoreCase(product.getName())) {
    throw new IllegalStateException(
        "Ya existe un producto con el nombre: " + product.getName()
    );
}
```

Si el nombre ya está registrado, el controlador responde con:

```text
409 Conflict
```

### Validación de categoría y ciudad

Antes de guardar el producto, el servicio consulta la categoría y ciudad mediante sus respectivos repositorios.

Si alguna no existe, se lanza una `EntityNotFoundException`.

Esto evita asociar un producto con registros inexistentes.

## 📦 Instalación y Configuración

### Prerrequisitos

- Java 21
- Maven
- Git

No es necesario instalar H2 externamente, ya que la base de datos se ejecuta dentro de la aplicación.

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd professional-challenge-api
```

### 2. Ejecutar el proyecto

Con Maven Wrapper:

#### macOS / Linux

```bash
./mvnw spring-boot:run
```

#### Windows

```bash
mvnw.cmd spring-boot:run
```

También es posible ejecutar la aplicación desde un IDE compatible con Spring Boot.

### 3. Acceder a la API

Una vez iniciada la aplicación:

```text
http://localhost:8080
```

Ejemplo:

```text
http://localhost:8080/products
```

## 🧪 Testing

El proyecto incluye pruebas automatizadas con **JUnit 5 y Spring Boot Test**.

Las pruebas pueden ejecutarse mediante Maven:

```bash
./mvnw test
```

En Windows:

```bash
mvnw.cmd test
```

## 🎯 Funcionalidades por Implementar

- ⏳ Autenticación y autorización de usuarios.
- ⏳ Gestión de clientes.
- ⏳ Gestión de inventario.
- ⏳ Persistencia en una base de datos para producción.
- ⏳ Mejoras en la gestión y eliminación física de archivos de imágenes.

## 🎨 Decisiones de Diseño

### 1. Arquitectura por capas

Se utiliza la separación:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

**Razón:** mantener separadas las responsabilidades y facilitar el mantenimiento y evolución del proyecto.

### 2. Spring Data JPA

Se utiliza Spring Data JPA para simplificar las operaciones de persistencia y las consultas básicas.

Ejemplos:

```java
productRepository.findById(id);
productRepository.findAll();
productRepository.save(product);
productRepository.deleteById(id);
```

### 3. H2 para desarrollo

H2 permite ejecutar el proyecto sin depender de una instalación externa de base de datos durante el desarrollo.

### 4. DTO para imágenes

`ProductImageRequest` permite recibir únicamente la información necesaria para asociar una imagen mediante URL, evitando utilizar directamente la entidad para este caso específico.

### 5. Almacenamiento local de imágenes

Las imágenes subidas mediante archivo se almacenan inicialmente en el directorio `uploads/`.

Esta solución permite trabajar con imágenes reales durante el desarrollo y facilita posteriormente migrar el almacenamiento a un servicio externo.

## 🔄 Flujo de Datos

```text
Cliente / Frontend
        ↓
HTTP Request
        ↓
Controller
        ↓
Service
        ↓
Repository
        ↓
H2 Database
        ↓
Repository
        ↓
Service
        ↓
Controller
        ↓
JSON Response
        ↓
Cliente / Frontend
```

## 👨‍💻 Autor

Este proyecto fue creado por:

**Jesús Ceballos.**
