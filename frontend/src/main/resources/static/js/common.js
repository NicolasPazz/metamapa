function showToast(message, type = 'info', duration = 3000) {
  return new Promise((resolve) => {
    const container = document.getElementById('toastContainer');
    if (!container) {
      resolve();
      return;
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    container.appendChild(toast);

    setTimeout(() => {
      toast.classList.add('show');
    }, 10);

    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => {
        if (toast.parentNode) {
          toast.parentNode.removeChild(toast);
        }
        resolve();
      }, 300);
    }, duration);
  });
}

function obtenerUbicacionUsuario(onSuccess, onError) {
  if (!navigator.geolocation) {
    if (onError) {
      onError("La geolocalización no es soportada por tu navegador.");
    }
    return;
  }
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      if (onSuccess) {
        onSuccess({
          latitud: position.coords.latitude,
          longitud: position.coords.longitude
        });
      }
    },
    () => {
      if (onError) {
        onError("No se pudo obtener la ubicación.");
      }
    }
  );
}

function getApiBaseUrl() {
  const metaTag = document.querySelector('meta[name="api-base-url"]');
  let apiBaseUrl;
  if (metaTag) {
    apiBaseUrl = metaTag.getAttribute('content');
  } else {
    // Fallback: usar configuración del servidor o asumir agregador en puerto 8084
    const origin = window.location.origin;
    const port = window.location.port;
    if (port && origin.includes(':' + port)) {
      apiBaseUrl = origin.replace(':' + port, ':8084') + '/api';
    } else {
      apiBaseUrl = origin + ':8084/api';
    }
  }
  
  // Reemplazar hosts internos de Docker por localhost para acceso desde navegador
  // Esto es necesario porque los hostnames de Docker no son accesibles desde el navegador
  apiBaseUrl = apiBaseUrl.replace(/https?:\/\/[^\/:]+(:\d+)?/, function(match) {
    // Extraer el protocolo y puerto
    const protocol = match.startsWith('https') ? 'https' : 'http';
    const portMatch = match.match(/:(\d+)/);
    const port = portMatch ? portMatch[1] : '8084';
    return protocol + '://localhost:' + port;
  });
  
  return apiBaseUrl;
}

function getDinamicaApiBaseUrl() {
  const apiBaseUrl = getApiBaseUrl();
  // Cambiar puerto de 8084 (agregador) a 8081 (dinámica)
  let dinamicaUrl = apiBaseUrl.replace(':8084', ':8081');
  // Reemplazar hosts internos de Docker por localhost para acceso desde navegador
  dinamicaUrl = dinamicaUrl.replace(/https?:\/\/[^\/:]+(:\d+)?/, function(match) {
    // Extraer el protocolo y puerto
    const protocol = match.startsWith('https') ? 'https' : 'http';
    const portMatch = match.match(/:(\d+)/);
    const port = portMatch ? portMatch[1] : '8081';
    return protocol + '://localhost:' + port;
  });
  return dinamicaUrl;
}

/**
 * Redirige a la página de error correspondiente según el código de estado HTTP
 * @param {number} status - Código de estado HTTP
 */
function redirectToErrorPage(status) {
  // No redirigir si ya estamos en una página de error
  if (window.location.pathname.startsWith('/error') || 
      window.location.pathname.startsWith('/403') || 
      window.location.pathname.startsWith('/404') || 
      window.location.pathname.startsWith('/500')) {
    return;
  }

  let errorPage = '/error';
  
  if (status === 403) {
    errorPage = '/403';
  } else if (status === 404) {
    errorPage = '/404';
  } else if (status >= 500) {
    errorPage = '/500';
  }
  
  window.location.href = errorPage;
}

/**
 * Wrapper para fetch que maneja errores y redirige a páginas de error
 * @param {string} url - URL a la que hacer la petición
 * @param {object} options - Opciones para fetch
 * @param {boolean} skipErrorRedirect - Si es true, no redirige automáticamente en errores (útil para validaciones)
 * @returns {Promise<Response>} - Respuesta de fetch
 */
async function safeFetch(url, options = {}, skipErrorRedirect = false) {
  try {
    const response = await fetch(url, options);
    
    // Si la respuesta no es exitosa y no debemos saltar la redirección
    if (!response.ok && !skipErrorRedirect) {
      const status = response.status;
      
      // No redirigir para errores 400 (bad request) ya que suelen ser errores de validación
      // que se manejan en el contexto específico, a menos que sea explícitamente solicitado
      if (status !== 400 && status !== 401) {
        redirectToErrorPage(status);
        return response;
      }
    }
    
    return response;
  } catch (error) {
    console.error('Error en petición fetch:', error);
    // Para errores de red o conexión, redirigir a error genérico
    if (!skipErrorRedirect) {
      redirectToErrorPage(500);
    }
    throw error;
  }
}

async function apiFetch(endpoint, options = {}) {
  const baseUrl = getApiBaseUrl();
  const url = baseUrl + endpoint;
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  const mergedOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...(options.headers || {}),
    },
  };
  
  try {
    const response = await fetch(url, mergedOptions);
    
    // Si la respuesta no es exitosa y no es un error de validación (400 con mensaje específico),
    // redirigir a la página de error correspondiente
    if (!response.ok) {
      const status = response.status;
      
      // No redirigir para errores 400 (bad request) ya que suelen ser errores de validación
      // que se manejan en el contexto específico
      if (status !== 400 && status !== 401) {
        redirectToErrorPage(status);
        // Retornar la respuesta para que el código que llama pueda manejarla si es necesario
        return response;
      }
    }
    
    return response;
  } catch (error) {
    console.error('Error en petición API:', error);
    // Para errores de red o conexión, redirigir a error genérico
    redirectToErrorPage(500);
    throw error;
  }
}

/**
 * Convierte una fecha en formato datetime-local (zona local del navegador) a UTC
 * @param {string} datetimeLocalString - Fecha en formato "yyyy-MM-ddTHH:mm" (zona local)
 * @returns {string} - Fecha en formato "yyyy-MM-ddTHH:mm" ajustada a UTC
 */
function convertLocalToUTC(datetimeLocalString) {
  if (!datetimeLocalString) return null;
  
  // Crear un Date object interpretando la fecha como hora local
  const localDate = new Date(datetimeLocalString);
  
  // Obtener los componentes en UTC
  const year = localDate.getUTCFullYear();
  const month = String(localDate.getUTCMonth() + 1).padStart(2, '0');
  const day = String(localDate.getUTCDate()).padStart(2, '0');
  const hours = String(localDate.getUTCHours()).padStart(2, '0');
  const minutes = String(localDate.getUTCMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day}T${hours}:${minutes}`;
}

/**
 * Convierte una fecha UTC a formato datetime-local en la zona horaria del navegador
 * @param {string} utcDateTimeString - Fecha en formato "yyyy-MM-ddTHH:mm" (UTC) o ISO string
 * @returns {string} - Fecha en formato "yyyy-MM-ddTHH:mm" en zona local del navegador
 */
function convertUTCToLocal(utcDateTimeString) {
  if (!utcDateTimeString) return null;
  
  // Si viene en formato ISO con Z, parsearlo directamente
  // Si viene en formato "yyyy-MM-ddTHH:mm", asumir que es UTC
  let date;
  if (utcDateTimeString.includes('Z') || utcDateTimeString.includes('+') || utcDateTimeString.includes('-', 10)) {
    date = new Date(utcDateTimeString);
  } else {
    // Asumir que es UTC y agregar 'Z' para parsearlo correctamente
    date = new Date(utcDateTimeString + 'Z');
  }
  
  // Obtener los componentes en hora local
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day}T${hours}:${minutes}`;
}

/**
 * Formatea una fecha UTC mostrándola en la zona horaria local del navegador
 * @param {string} utcDateTimeString - Fecha en formato ISO o "yyyy-MM-ddTHH:mm" (UTC)
 * @param {string} format - Formato deseado ('short', 'medium', 'long', o patrón personalizado)
 * @returns {string} - Fecha formateada en zona local
 */
function formatDateInLocalTimezone(utcDateTimeString, format = 'short') {
  if (!utcDateTimeString) return '';
  
  let date;
  if (utcDateTimeString.includes('Z') || utcDateTimeString.includes('+') || utcDateTimeString.includes('-', 10)) {
    date = new Date(utcDateTimeString);
  } else {
    // Asumir que es UTC
    date = new Date(utcDateTimeString + 'Z');
  }
  
  // Validar que la fecha sea válida
  if (isNaN(date.getTime())) {
    console.warn('Fecha inválida:', utcDateTimeString);
    return '';
  }
  
  if (format === 'short') {
    return date.toLocaleString('es-AR', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } else if (format === 'medium') {
    return date.toLocaleString('es-AR', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  } else if (format === 'long') {
    return date.toLocaleString('es-AR', { 
      weekday: 'long',
      day: '2-digit', 
      month: 'long', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } else if (format && format.includes('dd') && format.includes('MM') && format.includes('yyyy')) {
    // Formato personalizado tipo 'dd/MM/yyyy HH:mm' o 'dd/MM/yyyy, HH:mm:ss'
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    let result = format.replace('dd', day).replace('MM', month).replace('yyyy', year);
    if (format.includes('HH:mm')) {
      result = result.replace('HH', hours).replace('mm', minutes);
    }
    if (format.includes('HH:mm:ss')) {
      result = result.replace('HH', hours).replace('mm', minutes).replace('ss', seconds);
    }
    return result;
  } else {
    // Formato por defecto
    return date.toLocaleString('es-AR', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}

// Manejador global de errores no capturados
window.addEventListener('error', function(event) {
  console.error('Error no capturado:', event.error);
  // No redirigir si ya estamos en una página de error
  if (!window.location.pathname.startsWith('/error') && 
      !window.location.pathname.startsWith('/403') && 
      !window.location.pathname.startsWith('/404') && 
      !window.location.pathname.startsWith('/500')) {
    redirectToErrorPage(500);
  }
});

// Manejador global de promesas rechazadas no capturadas
window.addEventListener('unhandledrejection', function(event) {
  console.error('Promesa rechazada no capturada:', event.reason);
  // No redirigir si ya estamos en una página de error
  if (!window.location.pathname.startsWith('/error') && 
      !window.location.pathname.startsWith('/403') && 
      !window.location.pathname.startsWith('/404') && 
      !window.location.pathname.startsWith('/500')) {
    redirectToErrorPage(500);
  }
  // Prevenir que el error se muestre en la consola del navegador
  event.preventDefault();
});

