# MetaMapa

Sistema de código abierto para la recopilación, visibilización y mapeo colaborativo de información geográfica y temporal.

## 📋 Descripción

MetaMapa es un sistema diseñado para ONGs, universidades y organismos estatales que permite instalar y gestionar instancias particulares para sus comunidades. El sistema maximiza la disponibilidad y veracidad de la información, protegiendo la identidad de quienes lo visitan y suben información.

### Características Principales

- **Fuentes de Datos Múltiples**: Integración con fuentes estáticas (CSV), dinámicas (contribuciones colaborativas) y proxy (APIs externas)
- **Agregación Inteligente**: Servicio que combina datos de distintas fuentes generando una visión integrada
- **Algoritmos de Consenso**: Mecanismos para validar y generar consenso sobre hechos duplicados o similares
- **Navegación Curada**: Modos de navegación que permiten filtrar hechos según su nivel de consenso
- **Multimedia**: Soporte para imágenes, videos y audios asociados a hechos
- **Estadísticas**: Servicio dedicado para análisis y visualización de datos
- **Control de Acceso**: Sistema de roles y permisos (Administradores, Contribuyentes, Visualizadores)

## 🏗️ Arquitectura

El sistema está basado en una arquitectura de microservicios con los siguientes componentes:

### Módulos del Sistema

- **`frontend`**: Cliente liviano con Spring MVC y Thymeleaf (server-side rendering)
- **`agregador`**: Servicio principal de agregación de hechos y colecciones
- **`dinamica`**: Gestión de solicitudes dinámicas de contribuyentes
- **`estatica`**: Procesamiento de archivos CSV
- **`estadistica`**: Generación de estadísticas y análisis
- **`users`**: Servicio de autenticación y gestión de usuarios
- **`proxy_ong`**: Proxy para fuente ONG externa
- **`proxy_metamapa`**: Proxy para fuente MetaMapa externa

### Comunicación

- **Cliente → Servicios**: APIs REST (HTTP/JSON)
- **Servicios → Servicios**: HTTP/REST con WebClient (Spring WebFlux)
- **Base de datos**: MySQL Protocol (JDBC)

## 🛠️ Stack Tecnológico

### Backend
- **Lenguaje**: Java 17 (OpenJDK)
- **Framework**: Spring Boot 3.5.6
- **Build Tool**: Maven
- **Persistencia**: Spring Data JPA / Hibernate ORM
- **Base de datos**: MySQL
- **Seguridad**: Spring Security con JWT
- **Comunicación**: Spring WebFlux/WebClient
- **Validación**: Jakarta Validation API
- **Observabilidad**: Spring Boot Actuator

### Frontend
- **Framework**: Spring Boot 3.5.6 con Spring MVC
- **Motor de plantillas**: Thymeleaf
- **Cliente HTTP**: Spring WebFlux/WebClient
- **Seguridad**: Spring Security con autenticación basada en sesiones HTTP

### Procesamiento de Archivos
- **CSV**: OpenCSV 5.8
- **Multimedia**: Java NIO
- **Upload**: Spring Multipart

### Infraestructura
- **Contenedorización**: Docker
- **Orquestación**: Docker Compose
- **Reverse Proxy**: Nginx (con HTTPS/SSL)
- **Certificados SSL**: Certbot (Let's Encrypt)
- **Cloud**: AWS EC2 y AWS RDS (MySQL gestionado)

## 🎯 Conceptos Clave

### Hechos
Piezas de información registradas en el sistema con:
- Título, descripción, categoría
- Ubicación (latitud/longitud)
- Fecha del acontecimiento y fecha de carga
- Origen (carga manual, dataset, contribuyente)
- Contenido multimedia opcional (imágenes, videos, audios)

### Colecciones
Conjuntos de hechos organizados bajo un título y descripción, creados y gestionados por administradores. Son públicas y se actualizan automáticamente según criterios de pertenencia configurables.

### Fuentes de Datos
- **Estáticas**: Datasets conocidos (archivos CSV)
- **Dinámicas**: Contribuciones colaborativas de usuarios
- **Proxy**: Integraciones con otras instancias de MetaMapa u otros sistemas

### Algoritmos de Consenso
- **Múltiples menciones**: Al menos dos fuentes contienen el mismo hecho
- **Mayoría simple**: Al menos la mitad de las fuentes contienen el mismo hecho
- **Absoluta**: Todas las fuentes contienen el mismo hecho

## 🏛️ Principios de Diseño

El sistema aplica los principios SOLID y utiliza patrones de diseño:

### Principios SOLID
- **SRP**: Cada clase tiene una responsabilidad única
- **OCP**: Extensible sin modificar código existente
- **LSP**: Subclases reemplazables sin afectar funcionamiento
- **ISP**: Interfaces específicas y cohesivas
- **DIP**: Dependencias de abstracciones, no implementaciones

### Patrones de Diseño
- **Factory**: Creación de fuentes y solicitudes
- **Strategy**: Criterios de pertenencia y algoritmos de consenso
- **Repository**: Abstracción de acceso a datos
- **Adapter**: Integración con APIs externas
- **Template Method**: Algoritmos de consenso con lógica común

## 🔐 Seguridad

### Autenticación
- **Frontend**: Spring Security con sesiones HTTP
- **Backend**: JWT (JSON Web Tokens) stateless
- **Tokens**: Access token (15 min) y Refresh token (7 días)

### Control de Acceso
- **Roles**: ADMIN, CONTRIBUYENTE
- **Permisos**: Granulares por funcionalidad
- **Filtro de IP**: Control de acceso por origen (configurable)
- **Rate Limiting**: Implementado en Nginx (10 req/s general, 5 req/s APIs)

## 📊 Características Avanzadas

### Normalización de Datos
- Normalización de provincias y ubicaciones geográficas
- Unificación de categorías (case-insensitive)
- Geocodificación por coordenadas

### Detección de Spam
- Algoritmo TF-IDF para detección automática
- Detección de patrones (URLs, repeticiones, textos cortos)
- Rechazo automático de solicitudes spam

### Optimizaciones de Performance
- **Paginación**: A nivel de base de datos con Spring Data JPA
- **Cache**: Caffeine para datos de referencia y consultas frecuentes
- **Procesamiento Asíncrono**: CSV y fuentes externas en paralelo
- **Carga Asíncrona**: Estadísticas en panel admin

### Observabilidad
- **Spring Boot Actuator**: Health checks y métricas
- **Health Checks**: Configurados en Docker Compose para autorestart
- **Monitoreo**: Endpoints `/actuator/health` y `/actuator/info`

## 🚀 Despliegue

### Desarrollo
```bash
docker-compose up
```

### Producción
El sistema se despliega en AWS:
- **EC2**: Instancias con contenedores Docker
- **RDS**: MySQL gestionado (alta disponibilidad, backups automáticos)
- **Nginx**: Reverse proxy con HTTPS (Let's Encrypt)
- **Certbot**: Renovación automática de certificados SSL

### Configuración
Las variables de entorno permiten configurar:
- Conexiones a base de datos
- URLs de servicios
- Rutas de almacenamiento
- Configuración de seguridad

## 📁 Estructura del Proyecto

```
.
├── backend/
│   ├── agregador/          # Servicio de agregación
│   ├── dinamica/           # Fuentes dinámicas
│   ├── estatica/           # Procesamiento CSV
│   ├── estadistica/        # Estadísticas
│   ├── users/              # Autenticación
│   ├── proxy_ong/          # Proxy ONG
│   └── proxy_metamapa/     # Proxy MetaMapa
├── frontend/               # Cliente liviano
├── nginx/                  # Configuración reverse proxy
├── docker-compose.yml      # Orquestación desarrollo
└── docker-compose.prod.yml # Orquestación producción
```

## 📚 Documentación

Para más detalles sobre el diseño y justificaciones, consultar:
- `entregables/entrega-final`

## 📝 Licencia

Sistema de código abierto desarrollado como Trabajo Práctico Anual Integrador 2025.

---

**MetaMapa** - Sistema de gestión de mapeos colaborativos

