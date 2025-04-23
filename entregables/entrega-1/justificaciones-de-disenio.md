# TP DDS (Grupo 10)
## MetaMapa: Sistema de gestión de mapeos colaborativos
---
## Justificación de diseño iniciales
Esta primera implementación tiene un diseño estructurado que emplea una serie de patrones de diseño y principios orientados a objetos para abordar el problema de forma modular y flexible. A continuación se detallan las principales decisiones de diseño:
> ## Principios SOLID
> Buscamos respetar todos los principios `SOLID` de la siguiente manera:
> - **Single Responsibility**  
  Cada clase tiene una responsabilidad única y bien definida.  
  Ejemplo:
>  - `Hecho` representa hechos y sus reglas.
>  - Los distintos `Criterio` `encapsulan` lógicas de filtrado específicas.
> - **Open/Closed**  
  El sistema permite agregar nuevas funcionalidades sin modificar el código existente (`mantenibilidad`).  
  Ejemplo:
>  - Se pueden implementar nuevos `criterios` de filtrado.
>  - Se pueden incorporar nuevas `fuentes` de hechos.
> - **Liskov Substitution**  
  > Las subclases de `Fuente` (`FuenteEstatica`, `FuenteDinamica` y `FuenteProxy`) pueden reemplazar a su clase padre sin afectar el funcionamiento del sistema.
> - **Interface Segregation**  
  > Las interfaces que utilizamos (por ejemplo `Criterio`) son simples y no exigen la implementación de métodos que no van a ser utilizados por las clases que lo implementan.
> - **Dependency Inversion**  
  > Las clases de alto nivel, como `Colección`, no dependen de clases concretas de bajo nivel, sino de `abstracciones` como `Criterio` y `Fuente`.

> ## Encapsulamiento
> Todos los atributos de las clases son privados para mantener un `encapsulamiento` correcto. 
 Esto garantiza el control del acceso a los datos.

> ## Uso de Enums
> Se emplean **enums** para representar:
> - El estado de una `solicitud de eliminación`.
> - El estado de un `hecho`.
> - El origen de un `hecho`.
> - El `tipo de fecha`.
> 
> Esto `simplifica` la implementación y evita sobreingeniería (por ejemplo, no se utiliza una jerarquía de clases abstractas cuando no es necesario).
> Mejora la `reusabilidad` de ciertos componentes para distintos contextos.
> 
> 
> Ejemplo:  
Utilizamos un enum para el `origen de un hecho` en vez de una clase abstracta de la cual puedan `heredar` otras clases, como `OrigenContribuyente`, `OrigenDataset`, etc., ya que pensamos que este atributo solo va a funcionar para filtrar diferente hechos según su origen, y no para poder asociarlos a sus diferentes orígenes.

> ## Manejo de Categorías
> Las categorías de los hechos se representarán como cadenas de texto (`String`).
> 
> Para facilitar el filtrado y la comparación entre hechos, las categorías se almacenarán y evaluarán en minúsculas utilizando el método `toLowerCase()`.

> ## Visualizador
> 
> No se incluyó una clase `Visualizador` porque:
> 
> - No representa una entidad con responsabilidades propias dentro del dominio.
> - No tiene relaciones con otras clases del modelo, a diferencia de `Contribuyente`.
>
> Por esta razón, decidimos solo tener una clase `Contribuyente`, el cual puede tener sus atributos vacíos en caso de solo querer visualizar los hechos, promoviendo la `usabilidad`.
> 
> Si quiere subir un hecho deberá identificarse anteriormente.

> ## Patrones de diseño
> Durante el desarrollo del sistema se identificaron diversas necesidades que fueron abordadas mediante el uso de patrones de diseño, detallados a continuación:
> 
> ### Patrón Strategy
>
> Se implementó en los `criterios de pertenencia` a través de la interfaz `Criterio`, permitiendo `encapsular` diferentes algoritmos de filtrado en distintas clases concretas.  
> Ventajas:
> - Facilita la incorporación de nuevos criterios sin modificar código existente (`mantenibilidad`).
> - `Encapsula` la lógica de cada criterio con responsabilidad única.
> 
> ### Patrón Composite
> 
> Aplicado en `CriterioCombinado`, que agrupa múltiples `criterios de pertenencia`.  
> Ventajas:
> - Permite tratar criterios simples y combinados de forma `polimorfica`.
> - Aporta `flexibilidad` para componer lógicas complejas de filtrado.
> - Mejora la `testeabilidad` ya que se puede testear cada criterio por separado, y combinados.

> ## Modularidad
> Se mantiene el `acoplamiento` al minimo, por ejemplo con la clase `Colección` que depende de la interfaz `Fuente`, sin conocer si proviene de un CSV, una carga manual o una API externa. Tampoco sabe si su criterio de pertenencia es simple o combinado.

> ## DataHecho
> Si bien la clase `DataHecho` cumple el patrón de una `data class`, es decir, una clase con atributos y métodos de acceso (getters/setters), su propósito principal es funcionar como estructura de transporte de atributos entre distintas fuentes (por ejemplo, importaciones desde archivos CSV).
> 
> Se trata de una decisión consciente para `desacoplar` `Hecho` de la forma en que los datos son leídos. De esta forma:
> - Se mantiene el principio de responsabilidad única.
> - Se reduce la complejidad en la construcción `Hecho`.
