## MetaMapa – Justificaciones de Diseño

---

## 1. Tecnologías Utilizadas

### 1.1. Lenguaje y Framework Base

- **Lenguaje:** Java 17 (OpenJDK)
- **Framework:** Spring Boot 3.5.6
- **Build Tool:** Maven
- **Arquitectura:** Basada en servicios con Spring Boot

### 1.2. Frontend (Cliente Liviano)

- **Framework:** Spring Boot 3.5.6 con Spring MVC
- **Motor de plantillas:** Thymeleaf (server-side rendering)
- **Maquetado:** HTML5 y CSS
- **Cliente HTTP:** Spring WebFlux/WebClient (para consumir APIs REST)
- **Seguridad:** Spring Security con autenticación basada en sesiones HTTP
- **Integración Thymeleaf-Security:** Thymeleaf Extras Spring Security 6

### 1.3. Backend (Servicios)

- **Framework:** Spring Boot 3.5.6
- **Persistencia:** Spring Data JPA / Hibernate ORM
- **Base de datos:** MySQL (conector `mysql-connector-j`)
- **APIs REST:** Spring Web (annotations: `@RestController`, `@GetMapping`, `@PostMapping`, etc.)
- **Comunicación entre servicios:** Spring WebFlux/WebClient (HTTP/REST)
- **Validación:** Jakarta Validation API
- **Seguridad:** Spring Security con JWT (JSON Web Tokens)
- **Biblioteca JWT:** JJWT 0.11.5 (`io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- **Observabilidad:** Spring Boot Actuator (health checks, info endpoints)
- **Utilidades:** Lombok (reducción de boilerplate code)

### 1.4. Procesamiento de Archivos

- **CSV:** OpenCSV 5.8 (`com.opencsv:opencsv`) - Lectura y escritura de archivos CSV
- **Multimedia:** Java NIO (`java.nio.file.Files`, `java.nio.file.Paths`) - Manejo de archivos multimedia (imágenes, videos, audios)
- **Upload:** Spring Multipart (`MultipartFile`) - Carga de archivos

### 1.5. Infraestructura y Despliegue

- **Contenedorización:** Docker
- **Orquestación:** Docker Compose
- **Reverse Proxy:** Nginx (con HTTPS/SSL)
- **Certificados SSL:** Certbot (Let's Encrypt) - Renovación automática de certificados
- **Cloud:** AWS EC2 (instancias) y AWS RDS (MySQL gestionado)

### 1.6. Protocolos de Comunicación

- **Cliente → Servicios:** APIs REST (HTTP/JSON)
- **Servicios → Servicios:** HTTP/REST con WebClient (Spring WebFlux)
- **Base de datos:** MySQL Protocol (JDBC)

### 1.7. Módulos del Sistema

- `frontend`: Cliente liviano
- `agregador`: Servicio principal de agregación de hechos y colecciones
- `dinamica`: Gestión de solicitudes dinámicas de contribuyentes
- `estatica`: Procesamiento de archivos CSV
- `estadistica`: Generación de estadísticas
- `users`: Servicio de autenticación y gestión de usuarios
- `proxy_ong`: Proxy para fuente ONG externa
- `proxy_metamapa`: Proxy para fuente MetaMapa externa

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

## 4. Despliegue y Contenedorización con Docker

### 4.1. Arquitectura Docker ✅

El sistema utiliza contenedorización y orquestación:

**Estructura de Contenedores:**
- **MySQL**: Base de datos compartida (desarrollo) o RDS (producción)
- **8 Servicios**: users, estatica, dinamica, proxy-ong, proxy-metamapa, agregador, estadistica, frontend
- **Nginx**: Reverse proxy con HTTPS (solo producción)
- **Certbot**: Gestión automática de certificados SSL (solo producción)

**Justificación (SOLID y Cualidades):**
- **SRP**: Cada contenedor tiene una responsabilidad única (servicio específico)
- **Desacoplamiento**: Servicios independientes que se comunican vía red Docker
- **Escalabilidad**: Fácil escalar servicios individuales según demanda
- **Portabilidad**: Mismo entorno en desarrollo, testing y producción
- **Mantenibilidad**: Configuración centralizada en archivos de orquestación

**Código de ejemplo:**
```yaml
services:
  agregador:
    build:
      context: .
      dockerfile: Dockerfile
      args:
        MODULE_PATH: backend/agregador
        MODULE_NAME: agregador
    environment:
      PORT: 8084
      DB_HOST: ${RDS_HOST}
      USERS_SERVICE_URL: http://users:8087
    depends_on:
      users:
        condition: service_healthy
```

### 4.2. Configuración HTTP/HTTPS con Dominio de Producción ✅

**Configuración de Producción:**

El sistema implementa HTTPS mediante:
- **Nginx como Reverse Proxy**: Recibe todas las peticiones en puertos 80 (HTTP) y 443 (HTTPS). Nginx es un proxy reverso porque actúa como intermediario entre los clientes (navegadores) y los servidores backend (aplicaciones Spring Boot). A diferencia de un proxy forward que protege a los clientes, un proxy reverso protege y gestiona los servidores backend, enrutando las peticiones según reglas de configuración y ocultando la arquitectura interna del sistema.
- **Certbot (Let's Encrypt)**: Renovación automática de certificados SSL/TLS
- **Redirección HTTP → HTTPS**: Todas las peticiones HTTP se redirigen a HTTPS
- **Dominio configurable**: Variable de entorno `CERTBOT_DOMAIN` (ej: `metamapa-dds.ddns.net`)

**Justificación (SOLID y Cualidades):**
- **Seguridad**: Comunicación encriptada entre cliente y servidor
- **OCP**: Configuración de dominio sin modificar código de aplicación
- **Separación de responsabilidades**: Nginx maneja SSL, aplicaciones manejan lógica de negocio
- **Mantenibilidad**: Certificados renovados automáticamente sin intervención manual

**Configuración Nginx:**
```nginx
server {
    listen 80;
    server_name metamapa-dds.ddns.net;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name metamapa-dds.ddns.net;
    
    ssl_certificate /etc/letsencrypt/live/metamapa-dds.ddns.net/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/metamapa-dds.ddns.net/privkey.pem;
    
    location / {
        proxy_pass http://frontend:8080;
    }
}
```

**Beneficios:**
- **Seguridad**: Protección de datos en tránsito
- **Confianza**: Certificados válidos aumentan confianza del usuario
- **Cumplimiento**: Cumple con estándares de seguridad modernos

---

## 5. Autenticación y Autorización

### 5.1. Autenticación en Frontend (Spring Security con Sesiones HTTP) ✅

El frontend utiliza Spring Security con autenticación basada en sesiones HTTP. El flujo completo se explica paso a paso:

#### Paso 1: Usuario envía credenciales

El usuario accede al formulario de login y envía sus credenciales mediante POST a `/login`. Spring Security intercepta esta petición y delega la autenticación al `CustomAuthProvider`.

#### Paso 2: CustomAuthProvider valida credenciales

El `CustomAuthProvider` implementa `AuthenticationProvider` y es responsable de validar las credenciales contra el servicio `users`:

```32:72:frontend/src/main/java/ar/utn/ba/ddsi/frontend/providers/CustomAuthProvider.java
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // Llamada a servicio externo para obtener tokens
        AuthResponseDTO authResponse = externalAuthService.login(username, password);
        
            if (authResponse == null) {
                throw new BadCredentialsException("Usuario o contraseña inválidos");
            }

            log.info("Usuario logeado! Configurando variables de sesión");
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

        request.getSession().setAttribute("accessToken", authResponse.getAccessToken());
            request.getSession().setAttribute("refreshToken", authResponse.getRefreshToken());
            request.getSession().setAttribute("username", username);

            log.info("Buscando roles y permisos del usuario");
            RolesPermisosDTO rolesPermisos = externalAuthService.getRolesPermisos(authResponse.getAccessToken());

            log.info("Cargando roles y permisos del usuario en sesión");
            request.getSession().setAttribute("userId", rolesPermisos.getUserId());
        request.getSession().setAttribute("rol", rolesPermisos.getRol());
        request.getSession().setAttribute("permisos", rolesPermisos.getPermisos());
        
        List<GrantedAuthority> authorities = new ArrayList<>();
            rolesPermisos.getPermisos().forEach(permiso -> {
                authorities.add(new SimpleGrantedAuthority(permiso.name()));
            });
        authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesPermisos.getRol().name()));
        
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
            
        } catch (RuntimeException e) {
            throw new BadCredentialsException("Error en el sistema de autenticación: " + e.getMessage());
    }
}
```

**Proceso detallado:**
1. **Línea 34-35**: Extrae username y password del objeto `Authentication`
2. **Línea 39**: Llama a `MetaMapaApiService.login()` que hace POST a `/api/auth` del servicio `users`
3. **Línea 49-51**: Almacena `accessToken` y `refreshToken` en la sesión HTTP
4. **Línea 54**: Obtiene roles y permisos del usuario llamando a `/api/auth/user/roles-permisos`
5. **Línea 57-59**: Almacena `userId`, `rol` y `permisos` en la sesión
6. **Línea 61-65**: Crea autoridades Spring Security (permisos individuales + rol)
7. **Línea 67**: Retorna `UsernamePasswordAuthenticationToken` con las autoridades

#### Paso 3: MetaMapaApiService comunica con servicio users

El `MetaMapaApiService` es el encargado de comunicarse con el servicio `users`:

```34:55:frontend/src/main/java/ar/utn/ba/ddsi/frontend/services/MetaMapaApiService.java
    public AuthResponseDTO login(String username, String password) {
        try {
            return webClient
                    .post()
                    .uri(authServiceUrl + "/auth")
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password
                    ))
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
        }
    }
```

```103:114:frontend/src/main/java/ar/utn/ba/ddsi/frontend/services/MetaMapaApiService.java
    public RolesPermisosDTO getRolesPermisos(String accessToken) {
        try {
            return webApiCallerService.getWithAuth(
                authServiceUrl + "/auth/user/roles-permisos",
                accessToken,
                RolesPermisosDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error al obtener roles y permisos: " + e.getMessage(), e);
        }
    }
```

#### Paso 4: Servicio users genera tokens JWT

El servicio `users` recibe la petición de login en `AuthController`:

```29:56:backend/users/src/main/java/ar/utn/ba/ddsi/users/controllers/AuthController.java
    @PostMapping
    public ResponseEntity<AuthResponseDTO> loginApi(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        log.info("[AuthController] - POST - /api/auth - username: {}, password: [PROTEGIDO]", username);
        try {
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            loginService.autenticarUsuario(username, password);
            
            String accessToken = loginService.generarAccessToken(username);
            String refreshToken = loginService.generarRefreshToken(username);
            
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
```

El `LoginService` valida las credenciales y genera los tokens:

```27:41:backend/users/src/main/java/ar/utn/ba/ddsi/users/services/LoginService.java
    public Usuario autenticarUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByUsername(username);
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Username no encontrado: " + username);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return usuario;
    }
```

Los tokens JWT se generan usando `JwtUtil`:

```18:35:backend/users/src/main/java/ar/utn/ba/ddsi/users/utils/JwtUtil.java
    public static String generarAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("metamapa-users-server")
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    public static String generarRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("metamapa-users-server")
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .claim("type", "refresh") // diferenciamos refresh del access
                .signWith(key)
                .compact();
    }
```

**Características de los tokens:**
- **Access Token**: Válido por 15 minutos (línea 15)
- **Refresh Token**: Válido por 7 días (línea 16)
- **Firma**: Ambos tokens se firman con la misma clave secreta compartida entre servicios

#### Paso 5: Obtención de roles y permisos

Cuando el frontend necesita los roles y permisos, llama a `/api/auth/user/roles-permisos`:

```116:128:backend/users/src/main/java/ar/utn/ba/ddsi/users/controllers/AuthController.java
    @GetMapping("/user/roles-permisos")
    public ResponseEntity<UserRolesPermissionsDTO> getUserRolesAndPermissions(Authentication authentication) {
        log.info("[AuthController] - GET - /api/auth/user/roles-permisos - Username: {}", authentication != null ? authentication.getName() : "null");
        try {
            String username = authentication.getName();
            UserRolesPermissionsDTO response = loginService.obtenerRolesYPermisosUsuario(username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
```

El `LoginService` obtiene los roles y permisos del usuario:

```51:72:backend/users/src/main/java/ar/utn/ba/ddsi/users/services/LoginService.java
    public UserRolesPermissionsDTO obtenerRolesYPermisosUsuario(String username) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByUsername(username);
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Username no encontrado: " + username);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        Rol rolPrincipal = usuario.getRoles() != null && !usuario.getRoles().isEmpty() 
            ? usuario.getRoles().get(0) 
            : Rol.CONTRIBUYENTE;
        
        List<Permiso> permisos = obtenerPermisosPorRol(rolPrincipal);
        
        return UserRolesPermissionsDTO.builder()
                .userId(usuario.getId())
                .username(usuario.getUsername())
                .rol(rolPrincipal)
                .permisos(permisos)
                .build();
    }
```

Los permisos se asignan según el rol:

```105:124:backend/users/src/main/java/ar/utn/ba/ddsi/users/services/LoginService.java
    private List<Permiso> obtenerPermisosPorRol(Rol rol) {
        List<Permiso> permisos = new ArrayList<>();
        
        switch (rol) {
            case ADMIN:
                permisos.add(Permiso.MANEJAR_COLECCIONES);
                permisos.add(Permiso.MANEJAR_SOLICITUDES);
                permisos.add(Permiso.CREAR_HECHO);
                permisos.add(Permiso.SOLICITAR_MODIFICACION);
                break;
            case CONTRIBUYENTE:
                permisos.add(Permiso.CREAR_HECHO);
                permisos.add(Permiso.SOLICITAR_MODIFICACION);
                break;
            default:
                break;
        }
        
        return permisos;
    }
```

#### Paso 6: Configuración de Spring Security en Frontend

La configuración de seguridad define las reglas de acceso:

```25:80:frontend/src/main/java/ar/utn/ba/ddsi/frontend/config/SecurityConfig.java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/hechos/crear", "/hechos/**", "/solicitudes/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/static/**").permitAll()
                        .requestMatchers("/health", "/actuator/health").permitAll()
                        .requestMatchers("/login", "/registro", "/403").permitAll()
                        .requestMatchers("/terminos", "/privacidad").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/colecciones", "/colecciones/**").permitAll()
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hechos/mis-hechos", "/hechos/mis-hechos/**", "/hechos/misHechos", "/hechos/misHechos/**").authenticated()
                        .requestMatchers("/hechos", "/hechos/**").permitAll()
                        .requestMatchers("/solicitudes/modificar/**").authenticated()
                        .requestMatchers(request -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            return uri.matches("/solicitudes/\\d+") && "PATCH".equals(method);
                        }).authenticated()
                        .requestMatchers(request -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            return "/solicitudes".equals(uri) && "GET".equals(method);
                        }).hasRole("ADMIN")
permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
                            response.sendRedirect(isAdmin ? "/admin" : "/");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login?unauthorized")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/403")
                        )
                );

        return http.build();
    }
```

**Reglas de acceso definidas:**
- **Línea 38**: `/admin/**` requiere rol `ADMIN`
- **Línea 39**: `/hechos/mis-hechos/**` requiere autenticación (cualquier usuario logueado)
- **Línea 41**: `/hechos/**` es público (permitAll)
- **Línea 42**: `/solicitudes/modificar/**` requiere autenticación
- **Línea 58-62**: Después del login exitoso, redirige a `/admin` si es ADMIN, sino a `/`

### 5.2. Autenticación en Backend (JWT Stateless) ✅

Los servicios backend (`agregador`, `dinamica`) utilizan JWT para autenticación stateless. No dependen de sesiones HTTP, sino que validan el token JWT en cada petición.

#### Paso 1: Cliente envía token JWT

El frontend envía el token JWT almacenado en sesión en el header `Authorization: Bearer <token>` cuando hace llamadas a endpoints protegidos del backend. Esto se maneja mediante `WebApiCallerService`:

```162:166:frontend/src/main/java/ar/utn/ba/ddsi/frontend/services/internal/WebApiCallerService.java
    private String getAccessTokenFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getSession().getAttribute("accessToken");
    }
```

```60:69:frontend/src/main/java/ar/utn/ba/ddsi/frontend/services/internal/WebApiCallerService.java
    public <T> T get(String url, Class<T> responseType) {
        return executeWithTokenRetry(accessToken -> 
            webClient
                .get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(responseType)
                .block()
        );
    }
```

#### Paso 2: JwtAuthenticationFilter intercepta peticiones

Cada servicio backend tiene un `JwtAuthenticationFilter` que intercepta todas las peticiones y valida el token JWT para endpoints protegidos:

```33:75:backend/agregador/src/main/java/ar/utn/ba/ddsi/agregador/config/JwtAuthenticationFilter.java
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        log.info("[JwtAuthenticationFilter] FILTRO EJECUTADO - Procesando petición: {} {}", request.getMethod(), path);
        
        // Solo validar JWT para endpoints admin
        if (path.startsWith("/api/admin") || path.matches("/api/hechos/\\d+/ocultar")) {
            log.info("[JwtAuthenticationFilter] Endpoint protegido detectado: {}", path);
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
                    
                    String username = claims.getSubject();
                    var auth = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("[JwtAuthenticationFilter] Usuario autenticado: {}", username);
                } catch (Exception e) {
                    log.warn("[JwtAuthenticationFilter] Token JWT inválido: {}", e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                    return;
                }
            } else {
                log.warn("[JwtAuthenticationFilter] Intento de acceso a endpoint protegido sin token: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Se requiere autenticación");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
```

**Proceso detallado:**
1. **Línea 38**: Obtiene el path de la petición
2. **Línea 42**: Verifica si el path comienza con `/api/admin` o coincide con el patrón de ocultar hechos
3. **Línea 44**: Extrae el header `Authorization`
4. **Línea 45**: Verifica que el header tenga el formato `Bearer <token>`
5. **Línea 46**: Extrae el token (elimina el prefijo "Bearer ")
6. **Línea 48-52**: Valida y parsea el token JWT usando la clave secreta compartida
7. **Línea 54**: Extrae el username del claim `subject` del token
8. **Línea 55-59**: Crea un `UsernamePasswordAuthenticationToken` y lo establece en el contexto de seguridad
9. **Línea 63-65**: Si el token es inválido, retorna error 401
10. **Línea 67-70**: Si no hay token, retorna error 401

El mismo patrón se aplica en el servicio `dinamica`:

```27:65:backend/dinamica/src/main/java/ar/utn/ba/ddsi/dinamica/config/JwtAuthenticationFilter.java
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        if (path.startsWith("/api/admin")) {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String username = claims.getSubject();
                    var auth = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    log.warn("[JwtAuthenticationFilter] Token JWT inválido: {}", e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                    return;
                }
            } else {
                log.warn("[JwtAuthenticationFilter] Intento de acceso a endpoint protegido sin token: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Se requiere autenticación");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
```

#### Paso 3: Validación del token JWT

La clave secreta debe ser la misma en todos los servicios para que la validación funcione:

```25:27:backend/agregador/src/main/java/ar/utn/ba/ddsi/agregador/config/JwtAuthenticationFilter.java
    // Clave compartida con el servicio users - debe ser la misma en todos los servicios
    // Esta clave coincide con la de JwtUtil en el servicio users
    private static final SecretKey key = Keys.hmacShaKeyFor("metamapa-secret-key-for-jwt-token-generation-should-be-long-enough".getBytes());
```

```11:13:backend/users/src/main/java/ar/utn/ba/ddsi/users/utils/JwtUtil.java
    // Clave compartida con agregador y dinamica - debe ser la misma en todos los servicios
    @Getter
    private static final Key key = Keys.hmacShaKeyFor("metamapa-secret-key-for-jwt-token-generation-should-be-long-enough".getBytes());
```

### 5.3. Renovación de Tokens (Refresh Token) ✅

Cuando el access token expira (después de 15 minutos), el frontend puede renovarlo usando el refresh token:

```138:160:frontend/src/main/java/ar/utn/ba/ddsi/frontend/services/internal/WebApiCallerService.java
    private AuthResponseDTO refreshToken(String refreshToken) {
        try {
            RefreshTokenDTO refreshRequest = RefreshTokenDTO.builder()
                    .refreshToken(refreshToken)
                    .build();

            AuthResponseDTO response = webClient
                    .post()
                    .uri(authServiceUrl + "/auth/refresh")
                    .bodyValue(refreshRequest)
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Error al refrescar token: respuesta nula");
            }
            updateTokensInSession(response.getAccessToken(), response.getRefreshToken());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error al refrescar token: " + e.getMessage(), e);
        }
    }
```

El servicio `users` valida el refresh token y genera un nuevo access token:

```58:81:backend/users/src/main/java/ar/utn/ba/ddsi/users/controllers/AuthController.java
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        log.info("[AuthController] - POST - /api/auth/refresh - refreshToken: [PROTEGIDO]");
        try {
            String username = JwtUtil.validarToken(request.getRefreshToken());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey())
                    .build()
                    .parseClaimsJws(request.getRefreshToken())
                    .getBody();

            if (!"refresh".equals(claims.get("type"))) {
                return ResponseEntity.badRequest().build();
            }

            String newAccessToken = JwtUtil.generarAccessToken(username);
            TokenResponse response = new TokenResponse(newAccessToken, request.getRefreshToken());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
```

**Justificación (SOLID y Cualidades):**
- **SRP**: `CustomAuthProvider` encapsula únicamente la lógica de autenticación del frontend
- **SRP**: `JwtAuthenticationFilter` tiene la única responsabilidad de validar tokens JWT en backend
- **DIP**: Frontend depende de `MetaMapaApiService` (abstracción), no de implementación concreta
- **Seguridad**: Tokens almacenados en sesión segura del servidor (frontend), validación en cada petición (backend)
- **Escalabilidad**: Backend stateless permite escalar horizontalmente sin compartir sesiones
- **Desacoplamiento**: Backend no depende de gestión de sesiones del frontend
- **Mantenibilidad**: Lógica de autenticación centralizada en cada servicio

**Beneficios:**
- **Seguridad**: Protección de endpoints sensibles mediante validación de tokens
- **Flexibilidad**: Fácil agregar nuevos roles y permisos sin modificar código existente
- **Auditoría**: Spring Security registra intentos de acceso no autorizados
- **Experiencia de usuario**: Renovación automática de tokens sin requerir nuevo login

---

## 6. Implementación de API REST para Consulta de Hechos y Colecciones

### 6.1. Endpoints Públicos de Consulta ✅

El sistema expone una API REST completa para consulta de hechos y colecciones:

**Endpoints Principales:**

1. **GET `/api/front/hechos`** - Consulta paginada de hechos con filtros
2. **GET `/api/front/colecciones`** - Consulta paginada de colecciones
3. **GET `/api/front/colecciones/{handle}/hechos`** - Hechos de una colección específica
4. **GET `/api/front/categorias`** - Lista de categorías disponibles
5. **GET `/api/front/provincias`** - Lista de provincias con hechos
6. **GET `/api/front/fuentes`** - Lista de fuentes disponibles

**Justificación (SOLID y Cualidades):**
- **SRP**: Cada endpoint tiene una responsabilidad específica
- **RESTful**: Sigue principios REST (GET para consultas, recursos identificables)
- **Paginación**: Evita sobrecarga con grandes volúmenes de datos
- **Filtrado**: Permite consultas específicas sin exponer toda la base de datos

**Ejemplo de Implementación:**
```java
@GetMapping("/hechos")
public ResponseEntity<PageResponse<HechoDTO>> getHechosPaginados(
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) LocalDate fecha_reporte_desde,
        @RequestParam(required = false) LocalDate fecha_reporte_hasta,
        @RequestParam(required = false) LocalDateTime fecha_acontecimiento_desde,
        @RequestParam(required = false) LocalDateTime fecha_acontecimiento_hasta,
        @RequestParam(required = false) String ubicacion,
        @RequestParam(required = false) String fuente,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size) {
    
    log.info("[FrontController] - GET - /api/front/hechos - categoria: {}, ...");
    
    PageResponse<HechoDTO> response = hechosService.getPaginatedHechos(
        categoria, fecha_reporte_desde, fecha_reporte_hasta,
        fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
        ubicacion, fuente, page, size);
    
    return ResponseEntity.ok(response);
}
```

### 6.2. Estructura de Respuesta Paginada ✅

**PageResponse:**
```java
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
```

**Justificación (SOLID y Cualidades):**
- **Reusabilidad**: Clase genérica reutilizable para cualquier tipo de entidad
- **Eficiencia**: Solo transfiere datos necesarios por página
- **UX**: Facilita implementación de paginación en frontend
- **Mantenibilidad**: Estructura consistente en toda la API

**Beneficios:**
- **Performance**: Reduce carga de red y base de datos
- **Escalabilidad**: Funciona eficientemente con grandes volúmenes
- **Usabilidad**: Mejor experiencia de usuario con paginación

---

## 7. Integración Asíncrona: Procesamiento de Archivos CSV

### 7.1. Caso de Uso: Carga y Procesamiento Asíncrono de CSV ✅

**Contexto:**
El módulo `estatica` permite cargar archivos CSV que contienen hechos. El procesamiento de estos archivos puede ser costoso en tiempo, especialmente para archivos grandes.

**Implementación Asíncrona:**

**Motivos:**
1. **No bloquear el hilo principal**: El procesamiento de CSV puede tardar varios segundos o minutos
2. **Mejor experiencia de usuario**: El usuario recibe respuesta inmediata sin esperar el procesamiento completo
3. **Escalabilidad**: Permite procesar múltiples archivos en paralelo
4. **Resiliencia**: Errores en procesamiento no afectan otras peticiones

**Justificación (SOLID y Cualidades):**
- **SRP**: `AsyncProcessingService` encapsula lógica de ejecución asíncrona
- **Desacoplamiento**: Controller no depende de detalles de implementación asíncrona
- **Mantenibilidad**: Lógica de procesamiento separada de lógica de ejecución
- **Testabilidad**: Fácil testear procesamiento sin esperar ejecución real

**Código de ejemplo:**
```java
@Service
public class AsyncProcessingService {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setName("csv-processor");
        thread.setDaemon(true);
        return thread;
    });

    public void ejecutarAsync(Runnable tarea) {
        executor.submit(() -> {
            try {
                tarea.run();
            } catch (Exception e) {
                log.error("Error en tarea asíncrona: {}", e.getMessage(), e);
            }
        });
    }
}
```

**Uso en Controller:**
```java
@PostMapping("/api/csv")
public ResponseEntity<String> cargarCsv(@ModelAttribute CsvUploadRequestDTO request) {
    log.info("[FilesController] - POST - /api/csv - archivo: {}", ...);
    
    // Procesar de forma asíncrona
    asyncProcessingService.ejecutarAsync(() -> {
        filesService.procesarCsvSubido(request.getArchivo());
    });
    
    return ResponseEntity.ok("Archivo recibido, procesamiento en curso");
}
```

### 7.2. Beneficios de la Integración Asíncrona ✅

**Beneficios:**
1. **Responsividad**: Usuario recibe respuesta inmediata
2. **Throughput**: Sistema puede manejar más peticiones simultáneas
3. **Resiliencia**: Errores en procesamiento no afectan otras operaciones
4. **Escalabilidad**: Fácil escalar procesamiento independientemente

**Justificación (SOLID y Cualidades):**
- **Mantenibilidad**: Separación clara entre recepción y procesamiento
- **Testabilidad**: Fácil testear lógica de procesamiento de forma aislada
- **Extensibilidad**: Fácil agregar más tipos de procesamiento asíncrono

### 7.3. Dificultades y Soluciones ✅

**Dificultades Encontradas:**

1. **Manejo de Errores**: Errores en procesamiento asíncrono no se comunican directamente al usuario
   - **Solución**: Logging detallado y posiblemente sistema de notificaciones futuro

2. **Estado de Procesamiento**: Usuario no sabe cuándo termina el procesamiento
   - **Solución**: Actualmente se confía en logs; futuro: endpoint de estado de procesamiento

3. **Recursos**: Thread pool consume recursos del sistema
   - **Solución**: ExecutorService con shutdown graceful en `@PreDestroy`

4. **Consistencia**: Procesamiento asíncrono puede fallar sin que el usuario lo sepa
   - **Solución**: Logging exhaustivo y monitoreo de errores

**Justificación (SOLID y Cualidades):**
- **Mantenibilidad**: Dificultades identificadas y documentadas facilitan mejoras futuras
- **Extensibilidad**: Arquitectura permite agregar soluciones (notificaciones, estado) sin refactorizar

**Código de shutdown graceful:**
```java
@PreDestroy
public void shutdown() {
    log.info("Cerrando executor de procesamiento...");
    executor.shutdown();
    try {
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

---

## 8. Observabilidad y Monitoreo Proactivo

### 8.1. Spring Boot Actuator para Observabilidad ✅

**Implementación:**

El sistema utiliza endpoints de monitoreo y observabilidad:

- **`/actuator/health`**: Health check de cada servicio
- **`/actuator/info`**: Información de la aplicación
- Health checks configurados en Docker Compose para autorestart

**Justificación (SOLID y Cualidades):**

- **SRP**: La lógica de observabilidad está encapsulada en un componente dedicado, separando esta responsabilidad de la lógica de negocio
- **OCP**: Permite agregar nuevos endpoints de monitoreo sin modificar código de aplicación, solo mediante configuración
- **DIP**: La aplicación depende de abstracciones de monitoreo, no de implementaciones concretas
- **Mantenibilidad**: Configuración centralizada en `application.properties` facilita cambios sin recompilar
- **Testabilidad**: Permite usar stubs de health checks en tests cambiando la configuración `management.endpoint.health.show-details`

**Código de ejemplo:**
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
management.endpoints.web.base-path=/actuator
```

**Beneficios:**
- **Disponibilidad**: Health checks permiten detectar fallos y autorestart automático
- **Observabilidad**: Visibilidad del estado de cada servicio en tiempo real
- **Operaciones**: Facilita troubleshooting y diagnóstico de problemas

### 8.2. Health Checks en Docker Compose ✅

**Implementación:**

Cada servicio tiene health checks configurados que verifican `/actuator/health`:

```yaml
healthcheck:
  test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8084/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 150s
```

**Justificación (SOLID y Cualidades):**

- **Robustez**: Sistema detecta automáticamente servicios no saludables y puede reiniciarlos
- **Fiabilidad**: Health checks periódicos garantizan que los servicios estén operativos antes de aceptar tráfico
- **Mantenibilidad**: Configuración declarativa en Docker Compose facilita ajustar intervalos y timeouts sin modificar código

**Beneficios:**
- **Autorecovery**: Docker reinicia automáticamente servicios que fallan
- **Dependencias**: `depends_on: condition: service_healthy` garantiza que servicios dependientes solo inicien cuando sus dependencias estén listas
- **Disponibilidad**: Reduce tiempo de inactividad por fallos

---

## 9. Normalización de Datos

### 9.1. Normalización de Provincias y Ubicaciones ✅

**Implementación:**

El sistema implementa normalización de datos geográficos mediante `GeocodificacionService`:

1. **Normalización de nombres de provincias**: Diccionario que mapea variantes ("CABA", "Capital Federal", "c.a.b.a.") a nombres estandarizados
2. **Geocodificación por coordenadas**: Determina provincia a partir de latitud/longitud cuando el nombre no está disponible
3. **Normalización de categorías**: `Categoria.normalizar()` convierte a lowercase y trim para unificar variantes

**Justificación (SOLID y Cualidades):**

- **SRP**: `GeocodificacionService` encapsula únicamente la lógica de normalización geográfica, separándola de otras responsabilidades como persistencia o validación
- **OCP**: Fácil agregar nuevas variantes de nombres de provincias sin modificar la lógica de normalización, solo agregando entradas al diccionario `NORMALIZACION_PROVINCIAS`
- **Reusabilidad**: El mismo servicio puede ser usado por múltiples fuentes (estática, dinámica, proxy) para normalizar datos antes de persistirlos
- **Mantenibilidad**: Diccionario centralizado facilita actualizar mapeos cuando se descubren nuevas variantes

**Código de ejemplo:**
```java
@Service
public class GeocodificacionService {
    private static final Map<String, String> NORMALIZACION_PROVINCIAS = new HashMap<>();
    
    static {
        NORMALIZACION_PROVINCIAS.put("caba", "Ciudad Autónoma de Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("capital federal", "Ciudad Autónoma de Buenos Aires");
        NORMALIZACION_PROVINCIAS.put("bs as", "Buenos Aires");
    }
    
    public String normalizarProvincia(String provincia) {
        String provinciaLower = provincia.toLowerCase().trim();
        return NORMALIZACION_PROVINCIAS.getOrDefault(provinciaLower, provincia);
    }
}
```

**Beneficios:**
- **Consistencia**: Datos de diferentes fuentes se unifican en un formato estándar
- **Búsqueda**: Facilita búsquedas y filtros por provincia sin importar la variante usada
- **Estadísticas**: Permite generar estadísticas precisas agrupando por provincia normalizada

### 9.2. Normalización de Categorías ✅

**Implementación:**

Las categorías se normalizan mediante el método estático `Categoria.normalizar()` que aplica lowercase y trim:

```java
public static String normalizar(String nombre) {
    if (nombre == null) {
        return null;
    }
    return nombre.trim().toLowerCase();
}
```

**Justificación (SOLID y Cualidades):**

- **SRP**: La normalización está encapsulada en la clase `Categoria`, responsabilidad única de esta entidad de dominio
- **LSP**: Cualquier string puede ser normalizado de la misma forma, garantizando comportamiento predecible
- **Reusabilidad**: Método estático puede ser usado en múltiples contextos (persistencia, búsqueda, comparación) sin instanciar objetos
- **Mantenibilidad**: Cambios en la lógica de normalización solo requieren modificar un método

**Beneficios:**
- **Unificación**: "Incendio Forestal", "incendio forestal", "INCENDIO FORESTAL" se tratan como la misma categoría
- **Persistencia**: Evita duplicados en base de datos por diferencias de mayúsculas/minúsculas
- **Búsqueda**: Búsquedas case-insensitive funcionan correctamente

---

## 10. Control de Acceso por IP

### 10.1. OriginFilter: Filtro de Control de Acceso ✅

**Implementación:**

El sistema implementa `OriginFilter` que extiende `OncePerRequestFilter` de Spring Security para controlar acceso por IP:

- **Permite**: localhost, redes privadas (10.x.x.x), redes Docker (172.16-31.x.x)
- **Bloquea**: Todas las demás direcciones IP públicas
- **Configurable**: Se puede desactivar en desarrollo mediante `SECURITY_ORIGIN_FILTER_ENABLED=false`

**Justificación (SOLID y Cualidades):**

- **SRP**: `OriginFilter` tiene una única responsabilidad: validar origen de peticiones HTTP, separada de autenticación/autorización
- **OCP**: Fácil agregar nuevas reglas de IP permitidas sin modificar la lógica del filtro, solo extendiendo `isAllowedAddress()`
- **Strategy Pattern**: El método `isAllowedAddress()` implementa una estrategia de validación intercambiable
- **DIP**: El filtro depende de la abstracción `HttpServletRequest`, no de implementaciones concretas
- **Testabilidad**: Fácil testear diferentes IPs mockeando `HttpServletRequest` sin necesidad de configuración de red real

**Código de ejemplo:**
```java
@Component
public class OriginFilter extends OncePerRequestFilter {
    @Value("${security.origin-filter.enabled:true}")
    private boolean enabled;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String remoteAddr = getRemoteAddress(request);
        boolean isAllowed = isAllowedAddress(remoteAddr);
        
        if (!isAllowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAllowedAddress(String address) {
        // Valida localhost, redes privadas, Docker
        return address.startsWith("10.") || 
               (address.startsWith("172.") && esRedDocker(address));
    }
}
```

**Beneficios:**
- **Seguridad**: Protege servicios backend de acceso público no autorizado
- **Arquitectura basada en servicios**: Solo permite comunicación entre servicios en la misma red privada
- **Flexibilidad**: En desarrollo se desactiva fácilmente cambiando una variable de entorno

---

## 11. Rate Limiting

### 11.1. Rate Limiting con Nginx ✅

**Implementación:**

Nginx implementa rate limiting en la capa de reverse proxy:

- **General**: 10 requests/segundo por IP con burst de 20
- **APIs**: 5 requests/segundo por IP con burst de 10
- **Almacenamiento**: Zonas en memoria (10MB pueden manejar ~160k IPs)

**Justificación (SOLID y Cualidades):**

- **SRP**: Nginx maneja rate limiting como responsabilidad separada de la lógica de aplicación, siguiendo el principio de separación de concerns
- **Facade Pattern**: Nginx actúa como fachada que oculta la complejidad de rate limiting a las aplicaciones backend
- **Eficiencia de Desempeño**: Rate limiting a nivel de proxy evita que peticiones excesivas lleguen a los servicios backend, mejorando performance
- **Robustez**: Protege contra ataques DDoS y abuso de APIs sin requerir cambios en código de aplicación

**Configuración Nginx:**
```nginx
# Zona de rate limiting
limit_req_zone $binary_remote_addr zone=general_limit:10m rate=10r/s;
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=5r/s;

# Aplicación en location
location / {
    limit_req zone=general_limit burst=20 nodelay;
    limit_req_status 429;
    proxy_pass http://frontend;
}

location /api/ {
    limit_req zone=api_limit burst=10 nodelay;
    limit_req_status 429;
    proxy_pass http://frontend;
}
```

**Beneficios:**
- **Protección**: Previene sobrecarga del sistema por tráfico excesivo
- **Justicia**: Distribuye recursos equitativamente entre usuarios
- **Disponibilidad**: Mantiene servicios disponibles incluso bajo ataque
- **Eficiencia**: Rechaza peticiones excesivas antes de que consuman recursos de aplicación

---

## 12. Detector de Spam

### 12.1. Detector de Spam con Algoritmo TF-IDF ✅

**Implementación:**

El sistema implementa `DetectorDeSpamTFIDF` que utiliza múltiples técnicas:

1. **TF-IDF (Term Frequency-Inverse Document Frequency)**: Calcula relevancia del texto comparándolo con corpus legítimo
2. **Detección de palabras spam**: Diccionario de palabras típicas de spam
3. **Detección de patrones**: URLs, repeticiones excesivas, textos muy cortos
4. **Score combinado**: Combina múltiples factores con pesos configurables

**Justificación (SOLID y Cualidades):**

- **SRP**: `DetectorDeSpamTFIDF` encapsula únicamente la lógica de detección de spam, separada de validación de longitud o formato
- **Strategy Pattern**: Implementa la interfaz `DetectorDeSpam`, permitiendo intercambiar algoritmos (TF-IDF, machine learning, servicio externo) sin modificar código cliente
- **OCP**: Fácil agregar nuevos factores de detección (nuevas palabras spam, nuevos patrones) sin modificar la estructura del algoritmo, solo agregando métodos y ajustando pesos
- **DIP**: `SolicitudEliminacionFactory` depende de la abstracción `DetectorDeSpam`, no de la implementación concreta `DetectorDeSpamTFIDF`
- **Testabilidad**: Fácil testear con diferentes textos mock sin necesidad de servicios externos, ya que el algoritmo es completamente local

**Código de ejemplo:**
```java
@Component
public class DetectorDeSpamTFIDF implements DetectorDeSpam {
    private final double umbralSpam = 0.35;
    
    @Override
    public boolean esSpam(String texto) {
        // 1. Verificar palabras spam (50% peso)
        double palabrasSpamRatio = calcularRatioPalabrasSpam(palabras);
        
        // 2. Calcular TF-IDF (30% peso)
        double tfidfScore = calcularTFIDF(palabras);
        
        // 3. Detectar patrones (20% peso)
        double patronesScore = detectarPatrones(texto);
        
        double spamScore = palabrasSpamRatio * 0.5 + 
                          (1.0 - tfidfScore) * 0.3 + 
                          patronesScore * 0.2;
        
        return spamScore > umbralSpam;
    }
}
```

**Beneficios:**
- **Automatización**: Rechaza spam automáticamente sin intervención manual
- **Eficiencia**: Procesa solicitudes rápidamente sin llamadas a servicios externos
- **Extensibilidad**: Fácil reemplazar por servicio externo (ej: AWS Comprehend) implementando la misma interfaz
- **Precisión**: Combina múltiples técnicas para reducir falsos positivos/negativos

---

## 13. Manejo de Multimedia y Storage

### 13.1. Almacenamiento de Archivos Multimedia ✅

**Implementación:**

El sistema maneja archivos multimedia (imágenes, videos, audios) mediante `MultimediaStorageService`:

**Desarrollo (Docker Compose):**
- **Path**: `./backend/data/multimedia` (volumen montado)
- **Compartido**: Mismo volumen entre `dinamica` (escribe) y `agregador` (lee)
- **Persistencia**: Archivos persisten entre reinicios del contenedor

**Producción (EC2):**
- **Path**: `/app/data/multimedia` (dentro del contenedor)
- **Volumen**: Montado desde host EC2 para persistencia
- **Backup**: Archivos en sistema de archivos del servidor

**Justificación (SOLID y Cualidades):**

- **SRP**: `MultimediaStorageService` encapsula únicamente la lógica de almacenamiento y recuperación de archivos, separada de validación de formato o procesamiento de contenido
- **Adapter Pattern**: El servicio adapta diferentes rutas de almacenamiento (desarrollo vs producción) a una interfaz unificada mediante variables de entorno
- **OCP**: Fácil cambiar la estrategia de almacenamiento (local filesystem, S3, Azure Blob) sin modificar código cliente, solo cambiando la implementación del servicio
- **Portabilidad**: Configuración mediante `MULTIMEDIA_STORAGE_PATH` permite usar diferentes rutas en desarrollo y producción sin recompilar
- **Mantenibilidad**: Lógica de almacenamiento centralizada facilita cambios futuros (migración a cloud storage)

**Código de ejemplo:**
```java
@Service
public class MultimediaStorageService {
    @Value("${multimedia.storage.path:backend/data/multimedia}")
    private String storagePath;
    
    public String guardarArchivo(MultipartFile archivo) throws IOException {
        validarFormato(archivo);
        String nombreUnico = generarNombreUnico(archivo);
        Path destino = obtenerDirectorio().resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        return nombreUnico;
    }
    
    private Path obtenerDirectorio() {
        Path path = Paths.get(storagePath);
        if (!path.isAbsolute()) {
            // Resolver ruta relativa al proyecto
            path = projectRoot.resolve(storagePath);
        }
        return path.toAbsolutePath().normalize();
    }
}
```

**Configuración Docker:**
```yaml
# Desarrollo
volumes:
  - ./backend/data/multimedia:/app/data/multimedia

# Producción
volumes:
  - /opt/metamapa/multimedia:/app/data/multimedia
```

**Beneficios:**
- **Consistencia**: Misma interfaz de servicio en desarrollo y producción
- **Flexibilidad**: Fácil cambiar ubicación de almacenamiento sin modificar código
- **Seguridad**: Validación de rutas previene path traversal attacks
- **Escalabilidad**: Preparado para migrar a almacenamiento en la nube (S3, Azure Blob)

### 13.2. Almacenamiento de Archivos CSV ✅

**Implementación:**

Los archivos CSV se almacenan en `CSV_STORAGE_PATH` configurado por variable de entorno:

- **Desarrollo**: `./backend/data/csv` (volumen montado)
- **Producción**: `/app/data/csv` (dentro del contenedor, montado desde host)

**Justificación (SOLID y Cualidades):**

- **SRP**: Separación de almacenamiento CSV y multimedia en servicios diferentes (`FilesService` vs `MultimediaStorageService`)
- **Reusabilidad**: Mismo patrón de configuración por variables de entorno usado para multimedia
- **Mantenibilidad**: Configuración centralizada facilita cambios de ubicación

**Beneficios:**
- **Organización**: Archivos CSV y multimedia en ubicaciones separadas
- **Backup**: Fácil hacer backup de cada tipo de archivo independientemente
- **Limpieza**: Fácil limpiar archivos temporales sin afectar multimedia

---

## 14. Optimizaciones de Performance

### 14.1. Paginación en Base de Datos ✅

**Decisión de Diseño:**
Implementación de paginación real a nivel de base de datos utilizando `Pageable` de Spring Data JPA, en lugar de cargar todos los registros y paginar en memoria.

**Justificación (SOLID y Cualidades):**
- **Eficiencia**: Solo se transfieren y procesan los registros necesarios para la página solicitada, reduciendo significativamente el uso de memoria y ancho de banda
- **Escalabilidad**: El sistema puede manejar grandes volúmenes de datos sin degradación de performance, ya que la paginación se realiza en la base de datos
- **Separación de Concerns**: La lógica de paginación está delegada a Spring Data JPA, manteniendo el código de negocio más limpio

**Implementación:**
```java
@Query("SELECT h FROM Hecho h " +
    "LEFT JOIN h.provincia " +
    "WHERE h.estado = 'VISIBLE' AND ...")
Page<Hecho> findByFiltersPaginated(..., Pageable pageable);
```

**Beneficios:**
- Reducción de carga de datos: de potencialmente miles de registros a solo los necesarios (ej: 100 por página)
- Mejor uso de recursos: menos memoria, menos transferencia de red, menos procesamiento
- Experiencia de usuario mejorada: tiempos de respuesta más rápidos y predecibles

### 14.2. Procesamiento Asíncrono de Fuentes Externas ✅

**Decisión de Diseño:**
Uso de `CompletableFuture` para ejecutar llamadas HTTP a múltiples fuentes externas en paralelo, en lugar de ejecutarlas secuencialmente.

**Justificación (SOLID y Cualidades):**
- **Eficiencia**: Aprovecha mejor los recursos del servidor ejecutando múltiples operaciones I/O en paralelo
- **Performance**: Reduce el tiempo total de respuesta al ejecutar llamadas concurrentes en lugar de sumar tiempos secuenciales
- **Resiliencia**: Si una fuente externa falla o es lenta, las demás pueden completarse independientemente
- **Escalabilidad**: Permite manejar múltiples fuentes sin impacto lineal en el tiempo de respuesta

**Implementación:**
```java
List<CompletableFuture<List<HechoDTO>>> futures = fuentes.stream()
    .map(fuente -> CompletableFuture.supplyAsync(() -> {
        // Llamada a fuente externa
    }))
    .collect(Collectors.toList());
```

**Beneficios:**
- Reducción de latencia: el tiempo total es aproximadamente el de la fuente más lenta, no la suma de todas
- Mejor utilización de recursos: el servidor puede procesar múltiples requests HTTP simultáneamente
- Tolerancia a fallos: errores en una fuente no bloquean las demás

### 14.3. Cache en Memoria para Optimización de Performance ✅

**Decisión de Diseño:**
Implementación de cache en memoria usando Caffeine para optimizar las consultas más frecuentes y costosas del sistema, incluyendo datos de referencia (categorías, provincias) y la primera página de hechos sin filtros.

**Tipo de Cache:**
Cache en memoria usando Caffeine, una biblioteca de cache de alto rendimiento para Java. Los datos se almacenan en la memoria RAM del servidor de aplicación, no en base de datos ni en sistemas externos. Esto proporciona acceso extremadamente rápido pero requiere que los datos cacheados se reconstruyan si el servidor se reinicia.

**Duración del Cache:**
El cache tiene un tiempo de expiración de 5 minutos (`expireAfterWrite`). Después de este tiempo, los datos cacheados se invalidan automáticamente y se reconstruyen en la siguiente consulta. Además, el cache se invalida inmediatamente mediante `@CacheEvict` cuando se realizan operaciones de escritura (crear, modificar, eliminar).

**Justificación (SOLID y Cualidades):**
- **Eficiencia**: Evita ejecutar queries repetidas para datos que raramente cambian o consultas costosas frecuentes
- **Performance**: Respuestas casi instantáneas (<10ms) para datos cacheados vs queries de base de datos y fuentes externas (500ms-3000ms+)
- **Reducción de Carga**: Menos queries a la base de datos y menos llamadas a fuentes externas para información frecuentemente consultada
- **Principio de Responsabilidad Única**: El cache está encapsulado en la configuración y anotaciones, sin afectar la lógica de negocio
- **Escalabilidad**: Permite manejar mejor picos de tráfico sin sobrecargar el sistema

**Implementación:**

Cache para datos de referencia (categorías, provincias):
```java
@Cacheable(value = "categorias", unless = "#result == null || #result.getBody().isEmpty()")
public ResponseEntity<List<String>> getAllCategoriasNombres() { ... }

@CacheEvict(value = "categorias", allEntries = true)
public void persistirCategoriasDesdeHechos(List<Hecho> hechos) { ... }
```

**Invalidación de caché de categorías:**
La caché de categorías se invalida automáticamente mediante `@CacheEvict` en el método `persistirCategoriasDesdeHechos`, que se ejecuta en los siguientes casos:
- **Al crear un hecho manualmente**: Cuando un usuario crea un nuevo hecho (`HechosService.crearHecho`), si el hecho tiene una categoría nueva, se persiste y se invalida la caché.
- **Al actualizar hechos desde fuentes externas**: Cuando el scheduler actualiza hechos desde fuentes externas (`AgregadorService.actualizarHechos`), si se detectan categorías nuevas en los hechos recibidos, se persisten y se invalida la caché.
- **Al actualizar hechos según consenso**: Cuando se actualizan hechos de colecciones según consenso (`ColeccionesService`), si hay categorías nuevas, se invalida la caché.

Esto garantiza que la lista de categorías disponibles siempre esté actualizada cuando se agregan nuevas categorías al sistema, ya sea manualmente o desde fuentes externas.

Cache para primera página de hechos sin filtros:
```java
@Cacheable(value = "hechosPrimeraPagina", 
           condition = "#page == 0 && #categoria == null && #fecha_reporte_desde == null && #fecha_reporte_hasta == null && #fecha_acontecimiento_desde == null && #fecha_acontecimiento_hasta == null && #ubicacion == null && #fuente == null",
           unless = "#result == null || #result.getContent().isEmpty()")
public PageResponse<HechoDTO> getPaginatedHechos(...) { ... }

@CacheEvict(value = "hechosPrimeraPagina", allEntries = true)
public ResponseEntity<?> crearHecho(HechoDTO hechoInputDTO) { ... }

@CacheEvict(value = "hechosPrimeraPagina", allEntries = true)
public ResponseEntity<?> ocultarHecho(Long idHecho) { ... }

// Invalidación manual cuando se actualizan hechos desde fuentes externas
public void actualizarHechos() {
  // ... guardar hechos nuevos desde fuentes externas ...
  if (cacheManager != null) {
    var cache = cacheManager.getCache("hechosPrimeraPagina");
    if (cache != null) {
      cache.clear(); // Solo se invalida cuando realmente se guardaron hechos nuevos
    }
  }
}
```

**Beneficios:**
- Respuestas más rápidas para datos y consultas frecuentemente accedidos
- Reducción significativa de carga en base de datos y fuentes externas
- Invalidación automática cuando los datos cambian mediante `@CacheEvict`
- Cache condicional para hechos asegura que solo se cachee cuando no hay filtros, manteniendo coherencia de datos filtrados
- Mejor experiencia de usuario con tiempos de respuesta reducidos
- **No se cachean respuestas vacías**: La condición `unless = "#result == null || #result.getContent().isEmpty()"` evita cachear cuando no hay hechos disponibles. Esto previene el problema donde, si un usuario accede a "/hechos" cuando aún no hay hechos, se cachea una respuesta vacía que luego persiste incluso después de crear hechos nuevos. Al no cachear respuestas vacías, se garantiza que la próxima consulta mostrará los hechos recién creados correctamente.
- **Invalidación al actualizar desde fuentes externas**: Cuando el scheduler actualiza hechos desde fuentes externas (proxy ONG, proxy MetaMapa, etc.), la caché se invalida manualmente solo cuando realmente se guardaron hechos nuevos. Esto evita el problema donde, al arrancar el sistema, se cachea la primera página con solo algunos hechos (por ejemplo, solo de proxy MetaMapa), y luego cuando se cargan más hechos desde otras fuentes (como proxy ONG), la caché no se actualiza y sigue mostrando la versión parcial. Con esta invalidación, cada vez que se agregan hechos desde fuentes externas, la caché se limpia y la próxima consulta mostrará todos los hechos disponibles.

---

## 15. Despliegue en EC2 con RDS

### 15.1. Arquitectura de Producción ✅

**Implementación:**

El sistema se despliega en AWS EC2 con RDS MySQL:

- **EC2**: Instancia que ejecuta todos los servicios en contenedores Docker
- **RDS**: Base de datos MySQL gestionada por AWS (alta disponibilidad, backups automáticos)
- **Nginx**: Reverse proxy con HTTPS (Let's Encrypt)
- **Certbot**: Renovación automática de certificados SSL

**Justificación (SOLID y Cualidades):**

- **Arquitectura basada en servicios**: Cada servicio es independiente y puede escalarse por separado
- **Separación de Concerns**: Base de datos (RDS) separada de lógica de aplicación (EC2)
- **Portabilidad**: Misma configuración de contenedores funciona en desarrollo y producción, solo cambian variables de entorno
- **Fiabilidad**: RDS proporciona alta disponibilidad, backups automáticos y recuperación ante desastres sin gestión manual
- **Mantenibilidad**: Configuración declarativa facilita despliegues reproducibles

**Configuración:**
```yaml
# docker-compose.prod.yml
services:
  agregador:
    environment:
      DB_HOST: ${RDS_HOST}  # Endpoint de RDS
      DB_PORT: ${RDS_PORT}
      DB_USERNAME: ${RDS_USERNAME}
      DB_PASSWORD: ${RDS_PASSWORD}
      DB_USE_SSL: ${DB_USE_SSL}
```

**Beneficios:**
- **Escalabilidad**: RDS puede escalar verticalmente (instancias más grandes) y horizontalmente (read replicas)
- **Disponibilidad**: RDS Multi-AZ proporciona failover automático
- **Seguridad**: SSL/TLS para conexiones a base de datos, backups encriptados
- **Operaciones**: Backups automáticos, patches gestionados por AWS, monitoreo integrado

