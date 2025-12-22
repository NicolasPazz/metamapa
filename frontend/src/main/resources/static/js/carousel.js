function renderMultimediaCarousel(multimedia, carouselId, showControls = true) {
  if (!multimedia || !Array.isArray(multimedia) || multimedia.length === 0) {
    return '';
  }

  const apiBaseUrl = getApiBaseUrl();
  let html = '<div class="multimedia-carousel" id="' + carouselId + '">';
  html += '<div class="carousel-container">';
  
  // Preparar items del carrusel
  const items = [];
  multimedia.forEach(function(item, index) {
    let tipoRaw = item.tipoContenido || item.tipo;
    if (tipoRaw && typeof tipoRaw === 'object') {
      tipoRaw = tipoRaw.name || tipoRaw.toString();
    }
    const tipo = (tipoRaw || '').toString().toUpperCase();
    const path = item.pathArchivo || item.path || '';
    
    let fileUrl;
    if (path.startsWith('http')) {
      fileUrl = path;
    } else {
      fileUrl = apiBaseUrl + '/multimedia/' + encodeURIComponent(path);
    }
    
    items.push({ tipo, path, fileUrl, index });
  });
  
  // Botón anterior
  if (showControls && items.length > 1) {
    html += '<button class="carousel-btn carousel-prev" onclick="carouselPrev(\'' + carouselId + '\')" aria-label="Anterior">‹</button>';
  }
  
  // Contenedor de slides
  html += '<div class="carousel-slides">';
  items.forEach(function(item, index) {
    html += '<div class="carousel-slide' + (index === 0 ? ' active' : '') + '">';
    html += renderMultimediaItem(item);
    html += '</div>';
  });
  html += '</div>'; // carousel-slides
  
  // Botón siguiente
  if (showControls && items.length > 1) {
    html += '<button class="carousel-btn carousel-next" onclick="carouselNext(\'' + carouselId + '\')" aria-label="Siguiente">›</button>';
  }
  html += '</div>'; // carousel-container
  html += '</div>'; // multimedia-carousel
  
  return html;
}

function renderMultimediaItem(item) {
  let html = '';
  
  if (item.tipo === 'IMAGEN') {
    html += '<div class="carousel-item-image">';
    html += '<img src="' + item.fileUrl + '" alt="' + item.path + '" loading="lazy" ';
    html += 'onclick="window.open(\'' + item.fileUrl + '\', \'_blank\')" ';
    html += 'onerror="this.onerror=null; this.style.display=\'none\'; this.nextElementSibling.style.display=\'flex\';" />';
    html += '<div class="carousel-error" style="display: none;">';
    html += '<span style="font-size: 2rem;">🖼️</span>';
    html += '<span>Imagen no disponible</span>';
    html += '</div>';
    html += '</div>';
  } else if (item.tipo === 'VIDEO') {
    html += '<div class="carousel-item-video">';
    html += '<video src="' + item.fileUrl + '" controls preload="metadata" ';
    html += 'onerror="this.onerror=null; this.style.display=\'none\'; this.nextElementSibling.style.display=\'flex\';"></video>';
    html += '<div class="carousel-error" style="display: none;">';
    html += '<span style="font-size: 2rem;">🎬</span>';
    html += '<span>Video no disponible</span>';
    html += '</div>';
    html += '</div>';
  } else if (item.tipo === 'AUDIO') {
    html += '<div class="carousel-item-audio">';
    html += '<div style="font-size: 4rem; text-align: center; margin-bottom: 20px;">🎵</div>';
    html += '<audio src="' + item.fileUrl + '" controls style="width: 100%;" ';
    html += 'onerror="this.onerror=null; this.parentElement.querySelector(\'.carousel-error\').style.display=\'flex\';"></audio>';
    html += '<div class="carousel-error" style="display: none; flex-direction: column; align-items: center; gap: 10px; margin-top: 20px;">';
    html += '<span style="font-size: 2rem;">🎵</span>';
    html += '<span>Audio no disponible</span>';
    html += '</div>';
    html += '<div style="font-size: 0.85rem; color: #666; text-align: center; margin-top: 10px; word-break: break-all;" title="' + item.path + '">' + item.path + '</div>';
    html += '</div>';
  } else {
    html += '<div class="carousel-item-file">';
    html += '<a href="' + item.fileUrl + '" target="_blank" class="file-link">';
    html += '<span style="font-size: 3rem;">📎</span>';
    html += '<span>' + item.path + '</span>';
    html += '</a>';
    html += '</div>';
  }
  
  return html;
}

function carouselPrev(carouselId) {
  const carousel = document.getElementById(carouselId);
  if (!carousel) return;
  
  const slides = carousel.querySelectorAll('.carousel-slide');
  let currentIndex = -1;
  
  slides.forEach((slide, index) => {
    if (slide.classList.contains('active')) {
      currentIndex = index;
    }
  });
  
  if (currentIndex === -1) return;
  
  const prevIndex = (currentIndex - 1 + slides.length) % slides.length;
  
  slides[currentIndex].classList.remove('active');
  slides[prevIndex].classList.add('active');
}

function carouselNext(carouselId) {
  const carousel = document.getElementById(carouselId);
  if (!carousel) return;
  
  const slides = carousel.querySelectorAll('.carousel-slide');
  let currentIndex = -1;
  
  slides.forEach((slide, index) => {
    if (slide.classList.contains('active')) {
      currentIndex = index;
    }
  });
  
  if (currentIndex === -1) return;
  
  const nextIndex = (currentIndex + 1) % slides.length;
  
  slides[currentIndex].classList.remove('active');
  slides[nextIndex].classList.add('active');
}

