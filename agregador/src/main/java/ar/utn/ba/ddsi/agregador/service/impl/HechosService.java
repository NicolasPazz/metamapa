package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.HechosServiceDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechosServiceResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class HechosService {

    private WebClient webClient;

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
}
