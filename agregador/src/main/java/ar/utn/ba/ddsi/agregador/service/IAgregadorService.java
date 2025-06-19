package ar.utn.ba.ddsi.agregador.service;

import reactor.core.publisher.Mono;

public interface IAgregadorService {
    public Mono<Void> actualizarHechos();
}
