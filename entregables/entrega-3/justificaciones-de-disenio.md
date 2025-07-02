# TP DDS (Grupo 10)
## MetaMapa – Justificaciones de diseño (Entrega 3)

---
## 1. Operaciones CRUD

* **Convención REST**. Exponemos un recurso **Colección** con los endpoints estándar:
    * `POST /colecciones` → crear
    * `GET /colecciones/{handle}` → leer
    * `PATCH /colecciones/{handle}` → actualización parcial
    * `DELETE /colecciones/{handle}` → eliminar
* Elegimos `PATCH` porque permite enviar solo las propiedades modificadas, evita sobreescrituras accidentales y mantiene la operación idempotente.
* **Códigos HTTP**: `201` al crear, `404` si el `handle` no existe, `400` cuando falla la validación y `204` para eliminaciones exitosas.

> **Beneficios**: API autodescriptiva, menor `acoplamiento`, alta `mantenibilidad` y mejor `interoperabilidad`.

---

## 2. Uso de DTOs

Se implementan **Data Transfer Objects (DTO)** para operaciones como crear o modificar *Hechos*, *Colecciones* o *Solicitudes*.  
Con DTOs de entrada —por ejemplo `CrearHechoInputDTO` y `CriterioInputDTO`— logramos:

* **Separar** el modelo de dominio del modelo de transporte.
* **Validar** los datos de entrada, reduciendo errores.
* **Controlar** exactamente qué campos se reciben desde el frontend.

---

## 3. Patrones aplicados

### 3.1. `Factory`

|  | Factory | Implementaciones concretas | Beneficios |
|---------|---------|---------------------------|------------|
| **Criterio de pertenencia** | `CriterioDePertenenciaFactory` | `CriterioFecha`, `CriterioIgualdad`, `CriterioLugar`, `CriterioFuente`, `CriterioOrigen` | Incorpora validaciones y facilita añadir e instanciar un nuevo criterio. |
| **Algoritmo de consenso** | `AAlgoritmoDeConsensoFactory` | `MayoríaSimple`, `MayoríaAbsoluta`, `MúltiplesMenciones` | Cada algoritmo define `esConsensuado(Hecho)`. La colección desconoce los detalles, cumpliendo `Dependency Inversion`. |
| **Fuentes de hechos** | `FuenteFactory` | `FuenteCron`, `FuenteTiempoReal` | Inyecta la URL desde configuración, asigna ID incremental y construye el `WebClient`. |

> Esta combinación reduce el `acoplamiento` al encapsular la lógica de instanciación y aumenta la `extensibilidad`.

---

### 3.2. `Strategy`

|  | Estrategias | Punto clave |
|----------|-------------|-------------|
| **Algoritmos de consenso** | `MayoríaSimple`, `MayoríaAbsoluta`, `MúltiplesMenciones` | Permiten intercambiar la lógica de consenso en tiempo de ejecución. |
| **Criterios de pertenencia** | `CriterioFecha`, `CriterioIgualdad`, `CriterioLugar`, `CriterioFuente`, `CriterioOrigen` | Definen distintas reglas para determinar si un hecho pertenece a la colección. |

**Cómo se implementa:**

* `AAlgoritmoConsenso` declara el método común `esConsensuado(Hecho)`.
* Cada subclase concreta encapsula su propia regla y **no depende** de las demás.
* `Coleccion` mantiene una referencia al algoritmo; al invocar `esConsensuado()` delega sin conocer la lógica interna → respeta el principio de inversión de dependencias.
* El mismo enfoque se replica para `Criterio`: la colección recibe una estrategia que implementa `cumple(Hecho)` y puede cambiarse en tiempo de ejecución.

> Resultado: familia de comportamientos **encapsulados, intercambiables y abiertos a extensión** sin modificar código existente (`Open/Closed`).
