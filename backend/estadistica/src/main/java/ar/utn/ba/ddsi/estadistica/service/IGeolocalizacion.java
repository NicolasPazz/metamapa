package ar.utn.ba.ddsi.estadistica.service;

import ar.utn.ba.ddsi.estadistica.domain.ProvinciaEnum;

public interface IGeolocalizacion {
    // Obtiene la provincia de una coordenada
    ProvinciaEnum obtenerProvincia(Double latitud, Double longitud);
}
