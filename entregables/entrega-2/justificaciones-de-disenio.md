# TP DDS (Grupo 10)
## MetaMapa – Justificaciones de diseño (Entrega 2)

---
## Principios SOLID
Buscamos respetar todos los principios **SOLID** de la siguiente manera:

- **Single Responsibility**  
     Cada *módulo* posee una responsabilidad única y bien definida.

  | Módulo             | Función                                                                                                                                                                                                                                                                                                                                                            |
    |--------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | **estatica**       | Fuente que importa **datasets CSV** (implementada en la Entrega 1).                                                                                                                                                                                                                                                                                                |
  | **dinamica**       | Fuente donde los **contribuyentes** suben y, si están registrados, editan hechos.                                                                                                                                                                                                                                                                                  |
  | **proxy_ong**      | Fuente *proxy* que consume **APIs de terceros (ONGs)**.                                                                                                                                                                                                                                                                                                            |
  | **proxy_metamapa** | Fuente *proxy* que consume la **API REST de otras instancias MetaMapa**. Soporta perfiles `dev` y `prod` mediante la carpeta `client/` con `StubClient` (profile `dev`) o `WebClient` (profile `prod`).                                                                                                                                                            |
  | **agregador**      | Servicio que **combina fuentes**, refresca colecciones y persiste hechos. Gestiona solicitudes de eliminación (con detector de spam) y expone la **API REST principal** del sistema. Accede a las fuentes a través de la abstracción `infra → fuente → impl`, por lo que **no** conoce detalles internos de cada módulo, manteniendo así un **bajo acoplamiento**. |
  - En cada modulo se adopto un patrón arquitectónico de capas donde cada una tiene su propia responsabilidad.

 - **Open/Closed**  
  El sistema permite agregar nuevas funcionalidades sin modificar el código existente (`mantenibilidad`).  
  Ejemplo:
   - Se pueden incorporar nuevas `fuentes` de hechos compatibles solamente agregándolas a las propiedades del agregador, ya que utilizamos una `Simple Factory` para instanciarlas. 
   - Se pueden incorporar nuevas `fuentes` de hechos no compatibles solamente agregando su implementacion concreta de la interfaz IFuente, ya que utilizamos una `Adapter` para poder tratarlas polimorficamente con la misma interfaz.
 - **Liskov Substitution**  
   Las subclases de `Fuente` pueden reemplazar a su clase padre sin afectar el funcionamiento del sistema.
 - **Interface Segregation**  
   Las interfaces que utilizamos (por ejemplo `Criterio`) son simples y no exigen la implementación de métodos que no van a ser utilizados por las clases que lo implementan.
 - **Dependency Inversion**  
   Las clases de alto nivel, como `Colección`, no dependen de clases concretas de bajo nivel, sino de `abstracciones` como `Criterio` y `Fuente`.
---
> ## Encapsulamiento
> Todos los atributos de las clases son privados para mantener un `encapsulamiento` correcto. 
> Esto garantiza el control del acceso a los datos.
---
 ## Patrones de diseño
| Patrón                   | Código que lo implementa | Justificación                                                                                                                                |
|--------------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| **Simple Factory** | `infra.fuente.factory.FuenteFactory` | Instancia la fuente correcta según config (`CRON`, `TIEMPO_REAL`).                                                                           |
| **Repository**           | `domain.repositorie.*` | Abstracción de persistencia en memoria.                                                                                                      |
| **Singleton**             | `agregador.schedulers.agregadorScheduler`                       | Instancia única que programa el *cron job* de refresco de colecciones.                                                                       |
| **Adapter**               | `agregador.domain.fuente.*`                                     | Adapta las interfaces de fuentes a la interfaz requerida por el agregador. FuenteService usa IFuente sin conocer como lo resuelve cada tipo. |
---

>## Asincronía & Schedulers
> - El **agregador** define un `@Scheduled(cron = "0 0 * * * *")` que refresca colecciones **cada hora**.
> - La operación es *stateless* y *asincrónico*. No bloquea el hilo principal, permitiendo que el sistema siga respondiendo a solicitudes mientras se actualizan las colecciones.
---
>## Perfiles Spring (`dev`, `prod`)
> - **proxy_metamapa** usa perfiles para `AgregadorClient`:
  >   - `StubAgregadorClient` → profile `dev` (fixture de datos mockeados).
>   - `WebClientAgregadorClient` → profile `prod` (llamadas HTTP).
> - El perfil se define en las properties, facilitando la **testeabilidad**.
---
>## Spam Detection
> - Se encapsula el algoritmo TF-IDF en `solicitud.detector.DetectorDeSpam`.
> - Futuro cambio a un servicio externo sólo requiere una nueva implementación de la interfaz.
---