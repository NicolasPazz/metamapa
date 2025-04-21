> ## CU: Subir hechos
>
> **Actor**: Visualizador
>
> **Req. Func.**: Cargar campos de hechos
>
> **Req. No func.**: Soporte multimedia, cargar desde CSV
>
> **Descripción**: Cualquier visualizador debe poder subir hechos, indicando título,  
> descripción, categoría, contenido multimedia (opcional), lugar y fecha.
>
> **Flujo normal**:
> 1. El sistema pide título, descripción, categoría, contenido multimedia (opcional), lugar y fecha.
> 2. El visualizador carga datos de forma manual.
> 3. El sistema sube el hecho a la base de datos y lo asigna a una colección si corresponde algún criterio de pertenencia.
> 4. El usuario no está identificado, por lo que no se adjuntan sus datos al hecho.
> 5. Fin C.U.
>
> **Flujo alternativo**:  
> 2.1. El visualizador carga datos por CSV.  
> 2.2. El sistema procesa el CSV.  
> 2.3. Ir al paso 3.  
> 4.1. El usuario sí está identificado.  
> 4.2. Se adjuntan los datos del usuario al hecho.  
> 4.3. Fin C.U.

---

> ## CU: Identificarse
>
> **Actor**: Visualizador
>
> **Descripción**: El visualizador se da a conocer para asociar los hechos que sube a datos personales.
>
> **Req. Func.**: Poder ingresar nombre, apellido y edad
>
> **Req. No func.**: Solo el nombre es obligatorio
>
> **Flujo normal**:
> 1. El sistema pide nombre, apellido y edad.
> 2. El visualizador ingresa el nombre.
> 3. El visualizador elige no ingresar más datos.
> 4. Fin C.U.
>
> **Flujo alternativo**:  
> 3.1. El visualizador ingresa apellido y edad.  
> 3.2. Fin C.U.

---

> ## CU: Solicitar eliminación de hecho
>
> **Actor**: Visualizador
>
> **Descripción**: El visualizador solicita y fundamenta la eliminación de un hecho.
>
> **Req. Func.**: Poder ingresar fundamentación
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. El sistema muestra interfaz para fundamentar la solicitud.
> 2. El visualizador fundamenta la eliminación con más de 500 caracteres.
> 3. Fin C.U.
>
> **Flujo alternativo**:  
> 2.1. El visualizador ingresa menos de 500 caracteres.  
> 2.2. El sistema muestra mensaje de error exigiendo 500 caracteres.  
> 2.3. Volver al paso 1.

---

> ## CU: Buscar colecciones
>
> **Actor**: Visualizador
>
> **Descripción**: A partir de etiquetas de filtrado, el visualizador debe poder buscar colecciones específicas.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. El sistema muestra etiquetas de filtrado disponibles.
> 2. El visualizador selecciona filtros.
> 3. El sistema muestra colecciones filtradas a partir de etiquetas.
> 4. El visualizador selecciona colección.
> 5. Fin C.U.

---

> ## CU: Controlar solicitudes de eliminación
>
> **Actor**: Administrador
>
> **Descripción**: El administrador revisa las solicitudes de eliminación de hechos, verificando que estén bien fundamentadas y no sean spam.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. El sistema muestra solicitudes y sus respectivas fundamentaciones.
> 2. El administrador selecciona una solicitud.
> 3. El sistema muestra opción de aceptar o rechazar.
> 4. El administrador acepta.
> 5. El hecho pasa a estado eliminado y deja de mostrarse en la plataforma.
> 6. Fin C.U.
>
> **Flujo alternativo**:  
> 4.1. El administrador rechaza.  
> 4.2. El sistema elimina permanentemente la solicitud.  
> 4.3. Fin C.U.

---

> ## CU: Crear colección
>
> **Actor**: Administrador
>
> **Descripción**: Los administradores pueden crear colecciones (conjuntos de hechos), asignándoles un nombre, una descripción y un criterio de pertenencia a una fuente.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo normal**:
> 1. El sistema pide nombre, descripción y criterio de pertenencia.
> 2. El administrador ingresa nombre, descripción y criterio.
> 3. El sistema verifica que el criterio sí existe.
> 4. La colección es creada con los hechos de la fuente a la que pertenece.
> 5. Fin C.U.
>
> **Flujo alternativo**:  
> 3.1. El sistema verifica que el criterio no existe.  
> 3.2. El sistema muestra error indicando que el criterio no existe.  
> 3.3. Volver al paso 1.

---

> ## CU: Editar hecho
>
> **Actor**: Administrador
>
> **Descripción**: Los administradores pueden revisar y etiquetar los hechos.
>
> **Req. Func.**: –
>
> **Req. No func.**: –
>
> **Flujo pendiente**
