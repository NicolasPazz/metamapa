function renderHechoDetails(hecho, options = {}) {
  const {
    showMultimedia = true,
    showMapButton = true,
    showTags = true,
    carouselIdPrefix = 'multimedia-carousel'
  } = options;

  let html = '<h2>' + (hecho.titulo || 'Sin título') + '</h2>';

  // Categoría
  if (hecho.categoria) {
    html += '<p><strong>Categoría:</strong> ' + hecho.categoria + '</p>';
  }

  // Descripción
  if (hecho.descripcion) {
    html += '<p><strong>Descripción:</strong> ' + hecho.descripcion + '</p>';
  }

  // Fecha de acontecimiento
  if (hecho.fecha_acontecimiento) {
    const fecha = new Date(hecho.fecha_acontecimiento);
    html += '<p><strong>Fecha de acontecimiento:</strong> ' +
      fecha.toLocaleString('es-AR') + '</p>';
  }

  // Fecha de carga
  if (hecho.fecha_carga) {
    const fechaCarga = new Date(hecho.fecha_carga);
    html += '<p><strong>Fecha de carga:</strong> ' +
      fechaCarga.toLocaleString('es-AR') + '</p>';
  }

  // Provincia
  if (hecho.provincia) {
    html += '<p><strong>Provincia:</strong> ' + hecho.provincia + '</p>';
  }

  // Ubicación (coordenadas)
  if (hecho.latitud != null && hecho.longitud != null) {
    const lat = typeof hecho.latitud === 'number' ? hecho.latitud.toFixed(6) : hecho.latitud;
    const lng = typeof hecho.longitud === 'number' ? hecho.longitud.toFixed(6) : hecho.longitud;
    html += '<p><strong>Ubicación:</strong> ' + lat + ', ' + lng + '</p>';
  }

  // Fuente
  if (hecho.nombre_fuente) {
    let nombreFuente = hecho.nombre_fuente;
    if (nombreFuente.startsWith('Fuente ')) {
      nombreFuente = nombreFuente.substring(7);
    }
    html += '<p><strong>Fuente:</strong> ' + nombreFuente + '</p>';
  } else if (hecho.origen) {
    html += '<p><strong>Fuente:</strong> ' + hecho.origen + '</p>';
  }

  // Multimedia
  if (showMultimedia) {
    const multimedia = hecho.multimedia || hecho.contenidoMultimedia;
    if (multimedia && Array.isArray(multimedia) && multimedia.length > 0) {
      const carouselId = carouselIdPrefix + '-' + (hecho.id || Date.now());
      html += '<div style="margin-top: 20px;">';
      html += renderMultimediaCarousel(multimedia, carouselId, true);
      html += '</div>';
    }
  }

  // Etiquetas
  if (showTags && hecho.etiquetas && hecho.etiquetas.length > 0) {
    html += '<div style="margin-top: 20px;"><strong>Etiquetas:</strong><div class="tags">';
    hecho.etiquetas.forEach(etiqueta => {
      html += '<span class="tag">' + etiqueta + '</span>';
    });
    html += '</div></div>';
  }

  // Botón de mapa
  if (showMapButton && hecho.latitud != null && hecho.longitud != null && 
      !isNaN(hecho.latitud) && !isNaN(hecho.longitud) &&
      hecho.latitud >= -90 && hecho.latitud <= 90 &&
      hecho.longitud >= -180 && hecho.longitud <= 180) {
    let mapaUrl = null;
    if (hecho.id != null) {
      mapaUrl = '/hechos/mapa?hechoId=' + hecho.id;
    } else if (hecho.fuente_id != null && hecho.id_origen != null) {
      mapaUrl = '/hechos/mapa?fuenteId=' + hecho.fuente_id + '&idOrigen=' + hecho.id_origen;
    }
    
    if (mapaUrl != null) {
      html += '<div style="margin-top: 20px; text-align: center;">';
      html += '<a href="' + mapaUrl + '" class="btn primary">';
      html += 'Ver en mapa';
      html += '</a>';
      html += '</div>';
    }
  }

  return html;
}

function openHechoDetailPanel(hecho, panelId = 'hechoDetailPanel', contentId = 'hechoDetailContent', overlayId = 'overlay', options = {}) {
  const panel = document.getElementById(panelId);
  const overlay = document.getElementById(overlayId);
  const content = document.getElementById(contentId);

  if (!panel || !content) {
    console.error('Panel o contenido no encontrado');
    return;
  }

  content.innerHTML = renderHechoDetails(hecho, options);

  panel.classList.add('active');
  if (overlay) {
    overlay.classList.add('active');
  }
  document.body.style.overflow = 'hidden';
}

function closeHechoDetailPanel(panelId = 'hechoDetailPanel', overlayId = 'overlay') {
  const panel = document.getElementById(panelId);
  const overlay = document.getElementById(overlayId);

  if (panel) {
    panel.classList.remove('active');
  }
  if (overlay) {
    overlay.classList.remove('active');
  }
  document.body.style.overflow = '';
}

function findHecho(hechosData, criteria) {
  const { hechoId, fuenteId, idOrigen, hechoIndex } = criteria;

  if (hechoId && hechoId !== 'null') {
    const hechoIdNum = Number(hechoId);
    const hecho = hechosData.find(h => h.id != null && Number(h.id) === hechoIdNum);
    if (hecho) return hecho;
  }

  if (fuenteId && fuenteId !== 'null' && idOrigen && idOrigen !== 'null') {
    const fuenteIdNum = Number(fuenteId);
    const idOrigenNum = Number(idOrigen);
    const hecho = hechosData.find(h => 
      h.fuente_id != null && Number(h.fuente_id) === fuenteIdNum && 
      h.id_origen != null && Number(h.id_origen) === idOrigenNum
    );
    if (hecho) return hecho;
  }

  if (hechoIndex !== undefined) {
    return hechosData[parseInt(hechoIndex)];
  }

  return null;
}

