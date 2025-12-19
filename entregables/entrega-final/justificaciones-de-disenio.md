## MetaMapa – Justificaciones de Diseño

---

## 1. Resumen de Tecnologías por Capa

### 1.1. Front-end (Cliente Liviano)

**Tecnologías utilizadas:**
- **Framework:** Spring Boot 3.5.6 (Java 17)
- **Arquitectura:** MVC con Spring MVC
- **Motor de plantillas:** Thymeleaf (server-side rendering)
- **Maquetado:** HTML5 y CSS
- **Cliente HTTP:** Spring WebFlux/WebClient (para consumir APIs REST)
- **Validación:** Jakarta Validation API
- **Build:** Maven

**Estructura:**
- **Controllers:** `@Controller` (Spring MVC) - Manejan peticiones HTTP y renderizan vistas
- **Vistas:** Templates Thymeleaf (`.html` en `templates/`) - Renderizado dinámico en servidor
- **Servicios:** Clases `@Service` que consumen APIs REST del backend
- **Estilos:** CSS en `static/css/`

✅ Cliente Liviano con arquitectura MVC  
✅ Server-side rendering con Thymeleaf  
✅ HTML5 y CSS para maquetado  
✅ Motor de plantillas (Thymeleaf)  
✅ Desacoplado de lógica de negocio (consume APIs REST)

---

### 1.2. Back-end (Servicios)

**Tecnologías utilizadas:**
- **Framework:** Spring Boot 3.5.5 (Java 17)
- **Build:** Maven
- **Base de datos:** MySQL (conector `mysql-connector-j`)
- **Persistencia:** Spring Data JPA / Hibernate ORM
- **APIs REST:** `@RestController` con `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.
- **Comunicación entre servicios:** Spring WebFlux/WebClient (HTTP/REST)
- **Validación:** Jakarta Validation
- **Inyección de dependencias:** Spring Framework

**Módulos del sistema:**
- `agregador`: Servicio principal de agregación de hechos y colecciones
- `dinamica`: Gestión de solicitudes dinámicas de contribuyentes
- `estatica`: Procesamiento de archivos CSV
- `estadistica`: Generación de estadísticas
- `proxy_ong`: Proxy para fuente ONG externa
- `proxy_metamapa`: Proxy para fuente MetaMapa externa

✅ Java/OpenJDK 17  
✅ Maven  
✅ Spring Boot
✅ MySQL

---

### 1.3. Integración

**Protocolos utilizados:**
- **Cliente → Servicios:** APIs REST (HTTP/JSON)
- **Servicios → Servicios:** HTTP/REST con WebClient (Spring WebFlux)

✅ APIs REST para integración Cliente-Servicio  
✅ Protocolo HTTP/REST para comunicación entre servicios

---

## 2. Principios SOLID y Cualidades de Diseño

### 2.1. Single Responsibility Principle (SRP)

Cada clase tiene una responsabilidad única y bien definida:

- **`Hecho`**: Representa un hecho y encapsula su lógica de negocio (validaciones, transiciones de estado)
- **`Coleccion`**: Gestiona colecciones y aplica criterios de pertenencia y algoritmos de consenso
- **`Criterio`** (y sus implementaciones): Encapsulan lógicas de filtrado específicas
- **`AAlgoritmoConsenso`** (y sus implementaciones): Definen estrategias de consenso
- **Services**: Cada servicio (`HechosService`, `ColeccionesService`, etc.) tiene una responsabilidad específica
- **Controllers**: Solo manejan peticiones HTTP y delegan a servicios
- **Repositories**: Abstraen el acceso a datos

**Beneficios:**
- **Mantenibilidad**: Cambios en una responsabilidad no afectan otras
- **Testabilidad**: Fácil de testear unidades con responsabilidades claras
- **Reusabilidad**: Componentes con responsabilidades únicas son más reutilizables

---

### 2.2. Open/Closed Principle (OCP)

El sistema permite agregar nuevas funcionalidades sin modificar código existente:

**Ejemplos:**
- **Nuevos criterios de pertenencia**: Se implementa la interfaz `Criterio` sin modificar `Coleccion`
- **Nuevos algoritmos de consenso**: Se extiende `AAlgoritmoConsenso` sin modificar `Coleccion`
- **Nuevas fuentes de hechos**: Se extiende `AFuente` o se agrega configuración en `FuenteFactory`
- **Nuevos tipos de contenido multimedia**: Se agregan valores al enum `TipoContenido`

**Beneficios:**
- **Extensibilidad**: Fácil agregar nuevas funcionalidades
- **Mantenibilidad**: No se rompe código existente al agregar nuevas características
- **Estabilidad**: El código base permanece estable

---

### 2.3. Liskov Substitution Principle (LSP)

Las subclases pueden reemplazar a sus clases padre sin afectar el funcionamiento:

**Ejemplos:**
- **`FuenteCron` y `FuenteTiempoReal`**: Pueden reemplazar `AFuente` sin afectar `FuentesService`
- **`CriterioFechas`, `CriterioLugar`, etc.**: Pueden reemplazar `Criterio` sin afectar `Coleccion`
- **`MayoriaSimple`, `Absoluta`, `MultiplesMenciones`**: Pueden reemplazar `AAlgoritmoConsenso` sin afectar `Coleccion`

**Beneficios:**
- **Polimorfismo**: Permite tratar diferentes implementaciones de forma uniforme
- **Flexibilidad**: Fácil intercambiar implementaciones
- **Testabilidad**: Se pueden usar mocks/stubs que implementen las mismas interfaces

---

### 2.4. Interface Segregation Principle (ISP)

Las interfaces son específicas y no fuerzan a implementar métodos innecesarios:

**Ejemplos:**
- **`Criterio`**: Solo define `cumple(Hecho)`, método esencial para el filtrado
- **`DetectorDeSpam`**: Solo define `esSpam(String)`, método esencial para detección
- **`Validacion`**: Define métodos específicos para validación
- **`AFuente`**: Define métodos específicos para obtener hechos y colecciones

**Beneficios:**
- **Cohesión**: Interfaces pequeñas y cohesivas
- **Desacoplamiento**: Clientes no dependen de métodos que no usan
- **Claridad**: Interfaces claras y específicas

---

### 2.5. Dependency Inversion Principle (DIP)

Las clases de alto nivel dependen de abstracciones, no de implementaciones concretas:

**Ejemplos:**
- **`Coleccion`**: Depende de `Criterio` (interfaz) y `AAlgoritmoConsenso` (clase abstracta), no de implementaciones concretas
- **`SolicitudEliminacionFactory`**: Depende de `DetectorDeSpam` (interfaz) y `Validacion` (interfaz)
- **`FuentesService`**: Depende de `AFuente` (clase abstracta), no de `FuenteCron` o `FuenteTiempoReal`
- **Services**: Dependen de Repositories (interfaces), no de implementaciones JPA concretas
- **Controllers**: Dependen de Services (interfaces implícitas), no de implementaciones concretas

**Beneficios:**
- **Desacoplamiento**: Reducción del acoplamiento entre módulos
- **Flexibilidad**: Fácil cambiar implementaciones sin afectar dependientes
- **Testabilidad**: Fácil inyectar mocks en lugar de implementaciones reales

---

## 3. Patrones de Diseño Aplicados

### 3.1. Factory 

**Implementación:**
- **`FuenteFactory`**: Crea instancias de `AFuente` según configuración (`CRON` o `TIEMPO_REAL`)
- **`SolicitudEliminacionFactory`**: Crea instancias de `SolicitudEliminacion` con validaciones y detección de spam

**Justificación (SOLID y Cualidades):**
- **SRP**: Encapsula la lógica de creación de objetos complejos
- **OCP**: Permite agregar nuevos tipos de fuentes sin modificar el código que las usa
- **DIP**: Los clientes dependen de `AFuente` (abstracción), no de implementaciones concretas
- **Mantenibilidad**: Centraliza la lógica de creación, facilitando cambios futuros
- **Extensibilidad**: Fácil agregar nuevos tipos sin modificar código existente

**Código de ejemplo:**
```java
@Component
public class FuenteFactory {
  public AFuente crearFuente(FuenteConfig config) {
    WebClient webClient = WebClient.builder().baseUrl(config.getUrl()).build();
    return switch (config.getTipo()) {
      case CRON -> new FuenteCron(idAsignado, nombre, webClient);
      case TIEMPO_REAL -> new FuenteTiempoReal(idAsignado, nombre, webClient);
    };
  }
}
```

---

### 3.2. Strategy 

**Implementación:**
- **`Criterio`** (interfaz) con implementaciones: `CriterioFechas`, `CriterioLugar`, `CriterioFuente`, `CriterioIgualdad`, `CriterioOrigen`
- **`AAlgoritmoConsenso`** (clase abstracta) con implementaciones: `MayoriaSimple`, `Absoluta`, `MultiplesMenciones`

**Justificación (SOLID y Cualidades):**
- **OCP**: Permite agregar nuevos criterios o algoritmos sin modificar `Coleccion`
- **DIP**: `Coleccion` depende de abstracciones (`Criterio`, `AAlgoritmoConsenso`), no de implementaciones
- **LSP**: Cualquier implementación de `Criterio` puede reemplazar a otra sin afectar el comportamiento
- **Flexibilidad**: Permite cambiar estrategias en tiempo de ejecución
- **Mantenibilidad**: Cada estrategia está encapsulada en su propia clase

**Código de ejemplo:**
```java
public interface Criterio {
  boolean cumple(Hecho hecho);
}

public class Coleccion {
  public List<Hecho> aplicarCriteriosDePertenencia(List<Hecho> hechos) {
    return hechos.stream()
        .filter(hecho -> this.criteriosDePertenencia.stream()
            .allMatch(criterio -> criterio.cumple(hecho)))
        .collect(Collectors.toList());
  }
}
```

---

### 3.3. Repository 

**Implementación:**
- Interfaces como `IHechosRepository`, `IColeccionesRepository`, `ISolicitudesRepository`
- Spring Data JPA proporciona implementaciones automáticas

**Justificación (SOLID y Cualidades):**
- **SRP**: Separa la lógica de acceso a datos de la lógica de negocio
- **DIP**: Services dependen de interfaces Repository, no de implementaciones JPA
- **Testabilidad**: Fácil crear mocks de repositories para testing
- **Mantenibilidad**: Cambios en la capa de persistencia no afectan la lógica de negocio
- **Abstracción**: Oculta detalles de implementación de JPA/Hibernate

**Código de ejemplo:**
```java
public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  List<Hecho> findByFiltersConUbicacion(...);
}

@Service
public class HechosService {
  private final IHechosRepository hechosRepository; // Depende de interfaz
}
```

---

### 3.4. Adapter 

**Implementación:**
- **`OngApiAdapter`**: Adapta la API externa de ONG al formato interno del sistema
- Implementa `IApiExternaAdapter` para unificar la interfaz

**Justificación (SOLID y Cualidades):**
- **OCP**: Permite integrar nuevas APIs externas sin modificar el código que las consume
- **DIP**: El sistema depende de `IApiExternaAdapter` (abstracción), no de adapters concretos
- **Reusabilidad**: El mismo patrón puede aplicarse para otras APIs externas
- **Mantenibilidad**: Cambios en la API externa solo afectan al adapter
- **Desacoplamiento**: Aísla el sistema de cambios en APIs externas


---

### 3.5. Template Method 

**Implementación:**
- **`AAlgoritmoConsenso`**: Define métodos comunes (`contarApariciones`, `aparicionesMayorA`) y método abstracto (`esConsensuado`)

**Justificación (SOLID y Cualidades):**
- **DRY (Don't Repeat Yourself)**: Evita duplicación de código común
- **OCP**: Permite agregar nuevos algoritmos extendiendo la clase abstracta
- **Mantenibilidad**: Lógica común centralizada, fácil de mantener
- **Reusabilidad**: Métodos comunes reutilizables por todas las implementaciones

**Código de ejemplo:**
```java
public abstract class AAlgoritmoConsenso {
  public abstract Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos);
  
  public int contarApariciones(Hecho hecho, List<Hecho> hechos) {
    // Lógica común reutilizable
  }
}
```

---

## 4. Cualidades de Diseño Logradas

### 4.1. Mantenibilidad ✅

- **Código organizado por capas**: Controllers, Services, Repositories, Domain
- **Responsabilidades claras**: Cada clase tiene una responsabilidad única
- **Bajo acoplamiento**: Dependencias a través de interfaces
- **Alta cohesión**: Clases con funcionalidades relacionadas agrupadas

### 4.2. Extensibilidad ✅

- **Open/Closed Principle**: Fácil agregar nuevas funcionalidades sin modificar código existente
- **Factory **: Permite agregar nuevos tipos sin cambiar código cliente
- **Strategy **: Permite agregar nuevas estrategias (criterios, algoritmos)

### 4.3. Reusabilidad ✅

- **Componentes modulares**: Services, Repositories, Mappers reutilizables
- **Interfaces bien definidas**: Fácil reutilizar implementaciones
- **Patrones estándar**: Uso de patrones conocidos facilita la reutilización

### 4.4. Testabilidad ✅

- **Dependency Injection**: Fácil inyectar mocks
- **Separación de responsabilidades**: Cada componente testeable de forma aislada
- **Interfaces**: Fácil crear implementaciones de prueba

### 4.5. Desacoplamiento ✅

- **Dependency Inversion**: Dependencias hacia abstracciones
- **Service Layer**: Controllers desacoplados de lógica de negocio
- **Repository **: Lógica de negocio desacoplada de persistencia

### 4.6. Encapsulamiento ✅

- **Atributos privados**: Control de acceso a datos
- **Métodos públicos bien definidos**: Interfaces claras
- **Lógica de dominio en entidades**: Validaciones y reglas de negocio encapsuladas

---

## 5. Arquitectura y Separación de Capas

### 5.1. Arquitectura en Capas

```
┌─────────────────────────────────────┐
│      Controllers (Presentación)     │  ← Manejan HTTP, delegan a Services
├─────────────────────────────────────┤
│      Services (Lógica de Negocio)   │  ← Coordinan operaciones, usan Repositories
├─────────────────────────────────────┤
│      Domain (Modelo de Dominio)     │  ← Entidades con lógica de negocio
├─────────────────────────────────────┤
│      Repositories (Persistencia)    │  ← Abstraen acceso a datos
└─────────────────────────────────────┘
```

**Beneficios:**
- **Separación de responsabilidades**: Cada capa tiene un propósito claro
- **Mantenibilidad**: Cambios en una capa no afectan otras
- **Testabilidad**: Cada capa testeable de forma independiente

### 5.2. Domain-Driven Design (DDD)

- **Entidades ricas**: `Hecho`, `Coleccion`, `SolicitudEliminacion` contienen lógica de negocio
- **Value Objects**: `Categoria`, `Provincia`, `Etiqueta`
- **Aggregates**: `Coleccion` como agregado raíz que gestiona `Hecho`
- **Domain Services**: Lógica de negocio compleja en servicios de dominio

**Beneficios:**
- **Modelo expresivo**: El código refleja el dominio del problema
- **Lógica centralizada**: Reglas de negocio en el dominio, no dispersas
- **Mantenibilidad**: Cambios en reglas de negocio afectan principalmente al dominio
