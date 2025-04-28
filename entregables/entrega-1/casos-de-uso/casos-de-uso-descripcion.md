> ## CU: Subir hechos
>
> **Actor**: Visualizador
>
> **Req. Func.**: Cargar campos de hechos
>
> **Req. No func.**: Soporte multimedia, cargar desde CSV
>
> **Descripción**: Subir hechos indicando título, descripción, categoría, contenido multimedia (opcional), lugar y fecha.
>
> **Flujo normal**:
> 1. Pedir título, descripción, categoría, contenido multimedia (opcional), lugar y fecha.
> 2. Cargar datos de forma manual.
> 3. Subir el hecho a la base de datos y asignarlo a una colección si corresponde algún criterio de pertenencia.
> 4. Verificar si el usuario está identificado; si no, no adjuntar sus datos al hecho.
> 5. Fin C.U.
>
> **Flujo alternativo**:  
> 2.1. Cargar datos por CSV.  
> 2.2. Procesar el CSV.  
> 2.3. Ir al paso 3.  
> 4.1. Si el usuario está identificado, adjuntar sus datos al hecho.  
> 4.2. Fin C.U.

---

> ## CU: Identificarse
>
> **Actor**: Visualizador
>
> **Descripción**: Identificarse para asociar los hechos subidos a datos personales.
>
> **Req. Func.**: Ingresar nombre, apellido y edad
>
> **Req. No func.**: Solo el nombre es obligatorio
>
> **Flujo normal**:
> 1. Pedir nombre, apellido y edad.
> 2. Ingresar el nombre.
> 3. Elegir no ingresar más datos.
> 4. Fin C.U.
>
> **Flujo alternativo**:  
> 3.1. Ingresar apellido y edad.  
> 3.2. Fin C.U.

---

> ## CU: Solicitar eliminación de hecho
>
> **Actor**: Visualizador
>
> **Descripción**: Solicitar y fundamentar la eliminación de un hecho.
>
> **Req. Func.**: Ingresar fundamentación
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. Mostrar interfaz para fundamentar la solicitud.
> 2. Fundamentar la eliminación con más de 500 caracteres.
> 3. Fin C.U.
>
> **Flujo alternativo**:  
> 2.1. Ingresar menos de 500 caracteres.  
> 2.2. Mostrar mensaje de error exigiendo 500 caracteres.  
> 2.3. Volver al paso 1.

---

> ## CU: Buscar colecciones
>
> **Actor**: Visualizador
>
> **Descripción**: Buscar colecciones específicas a partir de etiquetas de filtrado.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. Mostrar etiquetas de filtrado disponibles.
> 2. Seleccionar filtros.
> 3. Mostrar colecciones filtradas a partir de etiquetas.
> 4. Seleccionar colección.
> 5. Fin C.U.

---

> ## CU: Controlar solicitudes de eliminación
>
> **Actor**: Administrador
>
> **Descripción**: Revisar y gestionar las solicitudes de eliminación de hechos.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. Mostrar solicitudes y sus respectivas fundamentaciones.
> 2. Seleccionar una solicitud.
> 3. Mostrar opciones de aceptar o rechazar.
> 4. Aceptar solicitud.
> 5. Cambiar el estado del hecho a eliminado para que no se muestre en la plataforma.
> 6. Fin C.U.
>
> **Flujo alternativo**:  
> 4.1. Rechazar solicitud.  
> 4.2. Eliminar la solicitud.  
> 4.3. Fin C.U.

---

> ## CU: Crear colección
>
> **Actor**: Administrador
>
> **Descripción**: Crear una colección de hechos, asignándole nombre, descripción y criterio de pertenencia.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. Pedir nombre, descripción y criterio de pertenencia.
> 2. Ingresar nombre, descripción y criterio.
> 3. Verificar que el criterio exista.
> 4. Crear la colección con los hechos de la fuente correspondiente.
> 5. Fin C.U.
>
> **Flujo alternativo**:  
> 3.1. Detectar que el criterio no existe.  
> 3.2. Mostrar error indicando que el criterio no existe.  
> 3.3. Volver al paso 1.

---

> ## CU: Editar hecho
>
> **Actor**: Administrador
>
> **Descripción**: Revisar y etiquetar hechos existentes.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo pendiente**
