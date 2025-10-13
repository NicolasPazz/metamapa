package ar.utn.ba.ddsi.estadistica.service;

import ar.utn.ba.ddsi.estadistica.domain.dto.output.*;

public interface IEstadisticasService {
    // EstadisticasService por coleccion
    ProvinciaPorColeccionDTO calcularProvinciaMasHechosPorColeccion(String handle);
    // EstadisticasService por categoria
    CategoriaEstadisticaDTO calcularCategoriaMasHechos();
    // EstadisticasService por provincia
    ProvinciaPorCategoriaDTO calcularProvinciaMasHechosPorCategoria(String categoria);
    // EstadisticasService por hora
    HoraEstadisticaDTO calcularHoraMasHechosPorCategoria(String categoria);
    // EstadisticasService de spam
    SpamEstadisticaDTO calcularSolicitudesSpam();
    void refrescoEstadisticas();
}
