package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.PageResponse;
import ar.utn.ba.ddsi.frontend.models.dtos.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoFormDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HechoService {
    final private WebClient webClient;

    public HechoService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8084/api").build();
    }

    public List<HechoDTO> getAllHechos() {
        return webClient.get().uri("/hechos").retrieve().bodyToFlux(HechoDTO.class).collectList().block();
    }

    public void crearHecho(HechoFormDTO form) { // Este metodo no deberia usar este webclient ya que es de agregador, y este metodo va a dinamica
        List<ContenidoMultimediaDTO> multimedia = fileToMultimediaDTO(form.getContenidoMultimedia());
        HechoDTO hechoParaEnviar = new HechoDTO();
        hechoParaEnviar.setTitulo(form.getTitulo());
        hechoParaEnviar.setDescripcion(form.getDescripcion());
        hechoParaEnviar.setLatitud(form.getLatitud());
        hechoParaEnviar.setLongitud(form.getLongitud());

        hechoParaEnviar.setEstado("VISIBLE");
        hechoParaEnviar.setOrigen("CONTRIBUYENTE");
        hechoParaEnviar.setFechaDeCarga(LocalDateTime.now());

        // TODO: endpoint para mandarle el hecho nuevo al agregador agregadorClient.post(hechoParaEnviar)
    }

    public List<HechoDTO> getAllHechosFilter(String modo, String ubicacion, LocalDate fecha_hasta, LocalDate fecha_desde, String categoria ) {
        // Convertir LocalDate a LocalDateTime (inicio y final del día)
        LocalDateTime fechaDesdeDateTime = fecha_desde != null ? fecha_desde.atStartOfDay() : null;
        LocalDateTime fechaHastaDateTime = fecha_hasta != null ? fecha_hasta.atTime(23, 59, 59) : null;

        // Construir la URI con los parámetros de filtro
        List<HechoDTO> hechos = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/hechos");
                    if (categoria != null && !categoria.isEmpty()) {
                        uriBuilder.queryParam("categoria", categoria);
                    }
                    if (ubicacion != null && !ubicacion.isEmpty()) {
                        uriBuilder.queryParam("ubicacion", ubicacion);
                    }
                    if (fechaDesdeDateTime != null) {
                        uriBuilder.queryParam("fecha_acontecimiento_desde", fechaDesdeDateTime.toString());
                    }
                    if (fechaHastaDateTime != null) {
                        uriBuilder.queryParam("fecha_acontecimiento_hasta", fechaHastaDateTime.toString());
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();

        return hechos != null ? hechos : List.of();
    }

    public List<String> obtenerCategoriasUnicas(){
        // Obtener categorías directamente desde el agregador
        List<String> categorias = webClient.get()
                .uri("/front/categorias")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();

        return categorias != null ? categorias : List.of();
    }

    public List<String> obtenerFuentesConHechos(){
        // Obtener fuentes directamente desde el agregador
        List<String> fuentes = webClient.get()
                .uri("/front/fuentes")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();

        return fuentes != null ? fuentes : List.of();
    }

    public List<String> obtenerProvinciasConHechos(){
        // Obtener provincias con hechos desde el agregador
        List<String> provincias = webClient.get()
                .uri("/front/provincias")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();

        return provincias != null ? provincias : List.of();
    }

    public List<ContenidoMultimediaDTO> fileToMultimediaDTO(MultipartFile file) {
        List<ContenidoMultimediaDTO> MultimediaLista = new ArrayList<>();
        if (file == null || file.isEmpty()) return MultimediaLista;

        // Construccion de DTO
        ContenidoMultimediaDTO contenidoMultimediaDTO = new ContenidoMultimediaDTO();
        contenidoMultimediaDTO.setTipoContenido(file.getContentType());
        contenidoMultimediaDTO.setPathArchivo(file.getOriginalFilename());
        MultimediaLista.add(contenidoMultimediaDTO);

        return MultimediaLista;
    }

    public HechoDTO getOneHecho() {
        // Obtener un solo hecho para la landing usando el endpoint optimizado
        try {
            return webClient.get()
                    .uri("/front/hecho-reciente")
                    .retrieve()
                    .bodyToMono(HechoDTO.class)
                    .block();
        } catch (Exception e) {
            // Si falla el endpoint optimizado, no devolver nada (el landing mostrará el mensaje de "no hay hechos")
            return null;
        }
    }

    public HechoDTO getHechoById(Long id) {
        // Intentar obtener un hecho específico por ID
        try {
            return webClient.get()
                    .uri("/hechos/" + id)
                    .retrieve()
                    .bodyToMono(HechoDTO.class)
                    .block();
        } catch (Exception e) {
            // Si el endpoint específico no existe, intentar obtenerlo de la lista
            try {
                return getAllHechos().stream()
                        .filter(h -> h.getId() != null && h.getId().equals(id))
                        .findFirst()
                        .orElse(null);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public HechoDTO getHechoByFuenteIdAndIdOrigen(Long fuenteId, Long idOrigen) {
        // Buscar un hecho externo por fuenteId e idOrigen
        try {
            return getAllHechos().stream()
                    .filter(h -> h.getFuenteId() != null && h.getFuenteId().equals(fuenteId) &&
                            h.getIdOrigen() != null && h.getIdOrigen().equals(idOrigen))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getDatosMapa(Long hechoId, Long fuenteId, Long idOrigen) {
        Map<String, Object> datos = new java.util.HashMap<>();
        
        if (hechoId != null) {
            // Mostrar solo un hecho específico por ID
            HechoDTO hechoEspecifico = getHechoById(hechoId);
            
            if (hechoEspecifico != null) {
                datos.put("hechos", List.of(hechoEspecifico));
                datos.put("centrarEnHecho", true);
                datos.put("hechoId", hechoId);
            } else {
                datos.put("hechos", List.of());
            }
        } else if (fuenteId != null && idOrigen != null) {
            // Mostrar solo un hecho externo por fuenteId e idOrigen
            HechoDTO hechoEspecifico = getHechoByFuenteIdAndIdOrigen(fuenteId, idOrigen);
            
            if (hechoEspecifico != null) {
                datos.put("hechos", List.of(hechoEspecifico));
                datos.put("centrarEnHecho", true);
                datos.put("fuenteId", fuenteId);
                datos.put("idOrigen", idOrigen);
            } else {
                datos.put("hechos", List.of());
            }
        } else {
            // Mostrar todos los hechos
            List<HechoDTO> hechos = getAllHechos();
            datos.put("hechos", hechos);
        }
        
        return datos;
    }

    @SuppressWarnings("unchecked")
    public PageResponse<HechoDTO> getAllHechosPaginados(String modo, String ubicacion, LocalDate fecha_hasta, LocalDate fecha_desde, String categoria, String fuente, int page, int size) {
        LocalDateTime fechaDesdeDateTime = fecha_desde != null ? fecha_desde.atStartOfDay() : null;
        LocalDateTime fechaHastaDateTime = fecha_hasta != null ? fecha_hasta.atTime(23, 59, 59) : null;

        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/front/hechos");
                        if (categoria != null && !categoria.isEmpty()) {
                            uriBuilder.queryParam("categoria", categoria);
                        }
                        if (ubicacion != null && !ubicacion.isEmpty()) {
                            uriBuilder.queryParam("ubicacion", ubicacion);
                        }
                        if (fuente != null && !fuente.isEmpty()) {
                            uriBuilder.queryParam("fuente", fuente);
                        }
                        if (fechaDesdeDateTime != null) {
                            uriBuilder.queryParam("fecha_acontecimiento_desde", fechaDesdeDateTime.toString());
                        }
                        if (fechaHastaDateTime != null) {
                            uriBuilder.queryParam("fecha_acontecimiento_hasta", fechaHastaDateTime.toString());
                        }
                        uriBuilder.queryParam("page", page);
                        uriBuilder.queryParam("size", size);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            // Procesar respuesta y extraer datos
            if (response != null) {
                List<HechoDTO> content = (List<HechoDTO>) response.getOrDefault("content", List.of());
                Boolean hasNext = (Boolean) response.getOrDefault("hasNext", false);
                Boolean hasPrevious = (Boolean) response.getOrDefault("hasPrevious", false);
                Integer currentPage = (Integer) response.getOrDefault("currentPage", page);
                Integer pageSize = (Integer) response.getOrDefault("pageSize", size);
                Number totalElements = (Number) response.getOrDefault("totalElements", 0);

                return new PageResponse<>(
                    content,
                    hasNext,
                    hasPrevious,
                    currentPage,
                    pageSize,
                    totalElements.longValue()
                );
            }

            return new PageResponse<>(List.of(), false, false, page, size, 0L);
        } catch (Exception e) {
            return new PageResponse<>(List.of(), false, false, page, size, 0L);
        }
    }
}
