package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechosServiceDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechosServiceResponseDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositories.IHechosRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class HechosService {

    private WebClient webClient;
    private IHechosRepository hechosRepository;

    public HechosService(WebClient.Builder webClientDinamicaBuilder) {
        this.webClient = webClientDinamicaBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<List<HechosServiceDTO>> getAllHechos(){
        Mono<List<HechosServiceDTO>> hechosDinamica = webClient
                .mutate()
                .baseUrl("http://localhost:8080")
                .build()
                .get()
                .uri("/api/hechos")
                .retrieve()
                .bodyToFlux(HechosServiceDTO.class)
                .collectList();

        Mono<List<HechosServiceDTO>> hechosProxy = webClient
                .mutate()
                .baseUrl("http://localhost:8082")
                .build()
                .get()
                .uri("/api/desastres")
                .retrieve()
                .bodyToFlux(HechosServiceDTO.class)
                .collectList();

        // Combinar ambas fuentes
        return Mono.zip(hechosDinamica, hechosProxy)
                .map(tuple -> {
                    List<HechosServiceDTO> combinados = new ArrayList<>();
                    combinados.addAll(tuple.getT1()); // hechos dinámica
                    combinados.addAll(tuple.getT2()); // hechos proxy_ong
                    return combinados;
                });
    }

    public List<HechosServiceDTO> getHechosFiltrados(Criterio filtro) {
        return this.hechosRepository.findAll().stream().filter(filtro::cumple).map(this::hechosServiceDTO).toList();
    }

    private HechosServiceDTO hechosServiceDTO(Hecho hecho) {
        HechosServiceDTO dto = new HechosServiceDTO();

        dto.setDescripcion(hecho.getDescripcion());
        dto.setId(hecho.getId());
        dto.setTitulo(hecho.getTitulo());
        dto.setEstado(hecho.getEstado().name());
        dto.setLatitud(hecho.getLatitud());
        dto.setLongitud(hecho.getLongitud());
        dto.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dto.setOrigen(hecho.getOrigen().name());
        dto.setFechaDeCarga(hecho.getFechaDeCarga().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dto.setIdCategoria(hecho.getCategoria().getId());
        dto.setIdContribuyente(hecho.getContribuyente().getId());
        dto.setIdEtiquetas(hecho.getIdEtiquetas());
        dto.setMultimediaPaths(hecho.getMultimediaPaths());

        return dto;

    }
}
