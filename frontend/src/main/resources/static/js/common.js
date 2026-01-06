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
  // #region agent log
  fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:56',message:'getApiBaseUrl entry',data:{currentOrigin:window.location.origin,currentHost:window.location.host},timestamp:Date.now(),sessionId:'debug-session',runId:'post-fix',hypothesisId:'H1'})}).catch(()=>{});
  // #endregion
  
  // Detectar si estamos en producción (dominio público, no localhost ni IP privada)
  const isProduction = function() {
    const hostname = window.location.hostname;
    // Si es localhost o IP privada, estamos en desarrollo
    if (hostname === 'localhost' || hostname === '127.0.0.1' || hostname.startsWith('192.168.') || hostname.startsWith('10.') || hostname.startsWith('172.16.') || hostname.startsWith('172.17.') || hostname.startsWith('172.18.') || hostname.startsWith('172.19.') || hostname.startsWith('172.20.') || hostname.startsWith('172.21.') || hostname.startsWith('172.22.') || hostname.startsWith('172.23.') || hostname.startsWith('172.24.') || hostname.startsWith('172.25.') || hostname.startsWith('172.26.') || hostname.startsWith('172.27.') || hostname.startsWith('172.28.') || hostname.startsWith('172.29.') || hostname.startsWith('172.30.') || hostname.startsWith('172.31.')) {
      return false;
    }
    // Si tiene un dominio público (no es IP), estamos en producción
    return true;
  }();
  
  // #region agent log
  fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:65',message:'production detection',data:{isProduction:isProduction,hostname:window.location.hostname},timestamp:Date.now(),sessionId:'debug-session',runId:'post-fix',hypothesisId:'H3'})}).catch(()=>{});
  // #endregion
  
  // En producción, usar ruta relativa para que nginx enrute correctamente
  if (isProduction) {
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:70',message:'using relative path for production',data:{apiBaseUrl:'/api'},timestamp:Date.now(),sessionId:'debug-session',runId:'post-fix',hypothesisId:'H3'})}).catch(()=>{});
    // #endregion
    return '/api';
  }
  
  // En desarrollo local, mantener comportamiento original
  const metaTag = document.querySelector('meta[name="api-base-url"]');
  let apiBaseUrl;
  if (metaTag) {
    apiBaseUrl = metaTag.getAttribute('content');
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:78',message:'meta tag found (dev)',data:{metaUrl:apiBaseUrl},timestamp:Date.now(),sessionId:'debug-session',runId:'post-fix',hypothesisId:'H1'})}).catch(()=>{});
    // #endregion
  } else {
    // Fallback: usar configuración del servidor o asumir agregador en puerto 8084
    const origin = window.location.origin;
    const port = window.location.port;
    if (port && origin.includes(':' + port)) {
      apiBaseUrl = origin.replace(':' + port, ':8084') + '/api';
    } else {
      apiBaseUrl = origin + ':8084/api';
    }
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:86',message:'fallback url constructed (dev)',data:{fallbackUrl:apiBaseUrl},timestamp:Date.now(),sessionId:'debug-session',runId:'post-fix',hypothesisId:'H1'})}).catch(()=>{});
    // #endregion
  }
  
  const originalUrl = apiBaseUrl;
  // Reemplazar hosts internos de Docker por localhost para acceso desde navegador
  // Esto es necesario porque los hostnames de Docker no son accesibles desde el navegador
  apiBaseUrl = apiBaseUrl.replace(/https?:\/\/[^\/:]+(:\d+)?/, function(match) {
    // Extraer el protocolo y puerto
    const protocol = match.startsWith('https') ? 'https' : 'http';
    const portMatch = match.match(/:(\d+)/);
    const port = portMatch ? portMatch[1] : '8084';
    return protocol + '://localhost:' + port;
  });
  
  // #region agent log
  fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:95',message:'url after localhost conversion (dev)',data:{originalUrl:originalUrl,finalUrl:apiBaseUrl,isLocalhost:apiBaseUrl.includes('localhost')},timestamp:Date.now(),sessionId:'debug-session',runId:'post-fix',hypothesisId:'H1'})}).catch(()=>{});
  // #endregion
  
  return apiBaseUrl;
}

function getDinamicaApiBaseUrl() {
  // Detectar si estamos en producción
  const isProduction = function() {
    const hostname = window.location.hostname;
    if (hostname === 'localhost' || hostname === '127.0.0.1' || hostname.startsWith('192.168.') || hostname.startsWith('10.') || hostname.startsWith('172.16.') || hostname.startsWith('172.17.') || hostname.startsWith('172.18.') || hostname.startsWith('172.19.') || hostname.startsWith('172.20.') || hostname.startsWith('172.21.') || hostname.startsWith('172.22.') || hostname.startsWith('172.23.') || hostname.startsWith('172.24.') || hostname.startsWith('172.25.') || hostname.startsWith('172.26.') || hostname.startsWith('172.27.') || hostname.startsWith('172.28.') || hostname.startsWith('172.29.') || hostname.startsWith('172.30.') || hostname.startsWith('172.31.')) {
      return false;
    }
    return true;
  }();
  
  // En producción, usar ruta relativa (nginx enruta /api/ al frontend que luego lo reenvía)
  if (isProduction) {
    return '/api';
  }
  
  // En desarrollo, mantener comportamiento original
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

async function safeFetch(url, options = {}, skipErrorRedirect = false) {
  // #region agent log
  fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:122',message:'safeFetch called',data:{url:url,currentOrigin:window.location.origin,method:options.method||'GET',skipErrorRedirect:skipErrorRedirect},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H2'})}).catch(()=>{});
  // #endregion
  try {
    const response = await fetch(url, options);
    
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:124',message:'safeFetch response received',data:{url:url,status:response.status,ok:response.ok,statusText:response.statusText},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H2'})}).catch(()=>{});
    // #endregion
    
    // Si la respuesta no es exitosa y no debemos saltar la redirección
    if (!response.ok && !skipErrorRedirect) {
      const status = response.status;
      
      // No redirigir para errores 400 (bad request) ya que suelen ser errores de validación
      // que se manejan en el contexto específico, a menos que sea explícitamente solicitado
      if (status !== 400 && status !== 401) {
        // #region agent log
        fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:132',message:'safeFetch redirecting to error page',data:{status:status,url:url},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H2'})}).catch(()=>{});
        // #endregion
        redirectToErrorPage(status);
        return response;
      }
    }
    
    return response;
  } catch (error) {
    console.error('Error en petición fetch:', error);
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:140',message:'safeFetch error caught',data:{url:url,errorName:error.name,errorMessage:error.message,errorStack:error.stack},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H5'})}).catch(()=>{});
    // #endregion
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
  
  // #region agent log
  fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:151',message:'apiFetch called',data:{endpoint:endpoint,baseUrl:baseUrl,finalUrl:url,currentOrigin:window.location.origin,method:options.method||'GET'},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H2'})}).catch(()=>{});
  // #endregion
  
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
    
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:169',message:'fetch response received',data:{url:url,status:response.status,ok:response.ok,statusText:response.statusText},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H2'})}).catch(()=>{});
    // #endregion
    
    // Si la respuesta no es exitosa y no es un error de validación (400 con mensaje específico),
    // redirigir a la página de error correspondiente
    if (!response.ok) {
      const status = response.status;
      
      // No redirigir para errores 400 (bad request) ya que suelen ser errores de validación
      // que se manejan en el contexto específico
      if (status !== 400 && status !== 401) {
        // #region agent log
        fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:178',message:'redirecting to error page',data:{status:status,url:url},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H2'})}).catch(()=>{});
        // #endregion
        redirectToErrorPage(status);
        // Retornar la respuesta para que el código que llama pueda manejarla si es necesario
        return response;
      }
    }
    
    return response;
  } catch (error) {
    console.error('Error en petición API:', error);
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/5ddc6da1-1429-48c5-ae62-39f9647c5a94',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'common.js:187',message:'fetch error caught',data:{url:url,errorName:error.name,errorMessage:error.message,errorStack:error.stack},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'H5'})}).catch(()=>{});
    // #endregion
    // Para errores de red o conexión, redirigir a error genérico
    redirectToErrorPage(500);
    throw error;
  }
}

function convertLocalToUTC(datetimeLocalString) {
  if (!datetimeLocalString) return null;
  
  // Validar formato básico: yyyy-MM-ddTHH:mm
  const dateTimeRegex = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})$/;
  const match = datetimeLocalString.match(dateTimeRegex);
  
  if (!match) {
    console.warn('Formato de fecha inválido en convertLocalToUTC:', datetimeLocalString);
    return null;
  }
  
  // Extraer componentes de la fecha local
  const year = parseInt(match[1], 10);
  const month = parseInt(match[2], 10) - 1; // Los meses en Date son 0-indexados
  const day = parseInt(match[3], 10);
  const hours = parseInt(match[4], 10);
  const minutes = parseInt(match[5], 10);
  
  // Validar rangos básicos
  if (year < 1900 || year > 2100 || month < 0 || month > 11 || 
      day < 1 || day > 31 || hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
    console.warn('Valores de fecha fuera de rango en convertLocalToUTC:', datetimeLocalString);
    return null;
  }
  
  // Crear un Date object interpretando la fecha como hora local
  // Usamos el constructor que acepta componentes individuales para evitar ambigüedad
  const localDate = new Date(year, month, day, hours, minutes);
  
  // Validar que la fecha sea válida
  if (isNaN(localDate.getTime())) {
    console.warn('Fecha inválida en convertLocalToUTC:', datetimeLocalString);
    return null;
  }
  
  // Obtener los componentes en UTC
  const utcYear = localDate.getUTCFullYear();
  const utcMonth = String(localDate.getUTCMonth() + 1).padStart(2, '0');
  const utcDay = String(localDate.getUTCDate()).padStart(2, '0');
  const utcHours = String(localDate.getUTCHours()).padStart(2, '0');
  const utcMinutes = String(localDate.getUTCMinutes()).padStart(2, '0');
  
  // Validar que el año UTC sea válido (no debería ser menor a 1900)
  if (utcYear < 1900 || utcYear > 2100) {
    console.error('Año UTC inválido después de conversión:', utcYear, 'de fecha local:', datetimeLocalString);
    return null;
  }
  
  return `${utcYear}-${utcMonth}-${utcDay}T${utcHours}:${utcMinutes}`;
}

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

