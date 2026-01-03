# Franchises API – WebFlux Reactivo

## Descripción General

Este proyecto es una **API REST reactiva** desarrollada con **Spring Boot WebFlux** para gestionar franquicias, sus sucursales y los productos ofertados en cada sucursal.

La solución implementa **Arquitectura Hexagonal (Ports & Adapters)** utilizando el **Scaffold Clean Architecture de Bancolombia**, garantizando una correcta separación de responsabilidades, facilidad de pruebas y escalabilidad.

La API permite:
- Gestionar franquicias, sucursales y productos
- Modificar el stock de productos
- Consultar el producto con mayor stock por sucursal para una franquicia específica

---

## Arquitectura de Software

El proyecto sigue **Arquitectura Hexagonal**, estructurada de la siguiente manera:

domain
├── model
├── ports
│ ├── in (Casos de uso)
│ └── out (Contratos de persistencia)

## application
 
 ├── usecase
 
 └── service

## infrastructure
 
 ├── entrypoints (Controladores REST – WebFlux)
 
 ├── driven-adapters
 
 │  └── mongo (MongoDB Reactivo)
  
 └── config

## Stack Tecnológico

- **Java 21**
- **Spring Boot WebFlux**
- **MongoDB Reactivo**
- **Project Reactor (Mono / Flux)**
- **Lombok**
- **SLF4J / Logback**
- **JUnit 5, Mockito, StepVerifier**
- **Docker**

---
## Modelo de Dominio

Franchise

 └── Branch (Sucursal)

 └── Product


- **Franchise**: nombre
- **Branch**: nombre, franchiseId
- **Product**: nombre, stock, branchId

---

## Consideraciones Reactivas

- Los flujos reactivos se encadenan usando operadores como:
    - `map`
    - `flatMap`
    - `switchIfEmpty`
    - `zip`
- Las señales reactivas se gestionan mediante:
    - `doOnNext`
    - `doOnError`
    - `doOnSuccess`
- No se utilizan llamadas bloqueantes dentro del pipeline reactivo
- El manejo de errores es explícito y no bloqueante

---

## Endpoints Expuestos

### Franquicia
- `POST /franchises` → Crear franquicia
- `PUT /franchises/{id}` → Actualizar nombre de franquicia *(extra)*

### Sucursal
- `POST /franchises/{id}/branches` → Agregar sucursal a franquicia
- `PUT /branches/{id}` → Actualizar nombre de sucursal *(extra)*

### Producto
- `POST /branches/{id}/products` → Agregar producto a sucursal
- `DELETE /branches/{branchId}/products/{productId}` → Eliminar producto
- `PATCH /products/{id}/stock` → Modificar stock de producto
- `PUT /products/{id}` → Actualizar nombre de producto *(extra)*

### Consultas
- `GET /franchises/{id}/products/max-stock`  
  Retorna el producto con mayor stock **por sucursal** para una franquicia específica.

---

## Pruebas

- Se implementan pruebas unitarias para:
    - Casos de uso
    - Flujos reactivos
    - Escenarios de error
- Herramientas utilizadas:
    - JUnit 5
    - Mockito
    - Reactor `StepVerifier`
- Cobertura de código:
    - **Mínimo:** 60%
    - **Deseable:** 80%

---

## Decisiones de Diseño

 - MongoDB Reactivo fue seleccionado por:
 - Soporte reactivo nativo
 - Modelado sencillo de estructuras jerárquicas
 - Rapidez de implementación
 - Spring WebFlux permite:
 - I/O no bloqueante
 - Mejor escalabilidad bajo alta concurrencia
 -Scaffold Clean Architecture asegura:
 -Buenas prácticas arquitectónicas
 -Consistencia estructural

Código mantenible


---

## Ejecución con Docker

### Construir imagen
```bash
  docker build -t franchises-api .
```

 Ejecución Local

# Prerrequisitos

 Java 21

 MongoDB en ejecución (local o Docker)

## Levantar la aplicación
./gradlew bootRun