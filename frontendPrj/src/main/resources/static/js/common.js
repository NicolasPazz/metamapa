function showToast(message, type = 'info') {
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
    }, 3000);
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
  if (metaTag) {
    return metaTag.getAttribute('content');
  }
  // Fallback: usar configuración del servidor o asumir agregador en puerto 8084
  const origin = window.location.origin;
  const port = window.location.port;
  if (port && origin.includes(':' + port)) {
    return origin.replace(':' + port, ':8084') + '/api';
  }
  return origin + ':8084/api';
}

function getDinamicaApiBaseUrl() {
  const apiBaseUrl = getApiBaseUrl();
  // Cambiar puerto de 8084 (agregador) a 8081 (dinámica)
  return apiBaseUrl.replace(':8084', ':8081');
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
    return response;
  } catch (error) {
    console.error('Error en petición API:', error);
    throw error;
  }
}

