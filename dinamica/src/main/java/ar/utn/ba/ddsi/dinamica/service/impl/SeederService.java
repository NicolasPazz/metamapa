package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.hecho.*;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.*;
import ar.utn.ba.ddsi.dinamica.domain.user.Contribuyente;
import ar.utn.ba.ddsi.dinamica.service.ISeederService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class SeederService implements ISeederService {
    @Autowired
    private IHechosRepository hechosRepository;
    @Autowired
    private ICategoriasRepository categoriasRepository;
    @Autowired
    private IEtiquetasRepository etiquetasRepository;
    @Autowired
    private IContribuyentesRepository contribuyentesRepository;
    @Autowired
    private IMultimediasRepository multimediasRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void inicializar() {

        //MULTIMEDIA
        ContenidoMultimedia contMult1 = new ContenidoMultimedia(baseUrl + "/images/hecho/aeronaveOlavarria",TipoContenido.IMAGEN);
        ContenidoMultimedia contMult2 = new ContenidoMultimedia(baseUrl + "/images/hecho/aeronaveOlavarria2",TipoContenido.IMAGEN);
        ContenidoMultimedia contMult3 = new ContenidoMultimedia(baseUrl + "/images/hecho/aeronaveVenadoTuerto",TipoContenido.IMAGEN);
        ContenidoMultimedia contMult4 = new ContenidoMultimedia(baseUrl + "/images/hecho/derrumbeChaco",TipoContenido.IMAGEN);
        ContenidoMultimedia contMult5 = new ContenidoMultimedia(baseUrl + "/images/hecho/derrumbeChaco2",TipoContenido.IMAGEN);

        this.multimediasRepository.save(contMult1);
        this.multimediasRepository.save(contMult2);
        this.multimediasRepository.save(contMult3);
        this.multimediasRepository.save(contMult4);
        this.multimediasRepository.save(contMult5);

        //ETIQUETAS
        Etiqueta etiqueta1 = new Etiqueta("Grave");
        Etiqueta etiqueta2 = new Etiqueta("Buenos Aires");
        Etiqueta etiqueta3 = new Etiqueta("Caída de aeronave");
        Etiqueta etiqueta4 = new Etiqueta("Voluntariado");
        Etiqueta etiqueta5 = new Etiqueta("Derrumbe");
        Etiqueta etiqueta6 = new Etiqueta("Accidente industrial");
        Etiqueta etiqueta7 = new Etiqueta("Accidente ferroviario");
        Etiqueta etiqueta8 = new Etiqueta("Evacuación");
        Etiqueta etiqueta9 = new Etiqueta("Neuquén");
        Etiqueta etiqueta10 = new Etiqueta("Chaco");

        this.etiquetasRepository.save(etiqueta1);
        this.etiquetasRepository.save(etiqueta2);
        this.etiquetasRepository.save(etiqueta3);
        this.etiquetasRepository.save(etiqueta4);
        this.etiquetasRepository.save(etiqueta5);
        this.etiquetasRepository.save(etiqueta6);
        this.etiquetasRepository.save(etiqueta7);
        this.etiquetasRepository.save(etiqueta8);
        this.etiquetasRepository.save(etiqueta9);
        this.etiquetasRepository.save(etiqueta10);

        //CATEGORIAS
        Categoria categoria1 = new Categoria("Caida de aeronave");
        Categoria categoria2 = new Categoria("Accidente en paso a nivel");

        this.categoriasRepository.save(categoria1);
        this.categoriasRepository.save(categoria2);

        //CONTRIBUYENTES
        Contribuyente contribuyente1 = new Contribuyente("Ramiro");
        Contribuyente contribuyente2 = new Contribuyente("Ramiro", "Gutierrez", LocalDate.of(2000,1,1));
        Contribuyente contribuyente3 = new Contribuyente("Francisco");

        this.contribuyentesRepository.save(contribuyente1);
        this.contribuyentesRepository.save(contribuyente2);
        this.contribuyentesRepository.save(contribuyente3);

        //HECHOS
        var hechos = List.of(
           Hecho.builder()
                   .data(DataHecho.builder()
                           .titulo("Caída de aeronave impacta en Olavarría")
                           .descripcion("Grave caída de aeronave ocurrió en las inmediaciones de Olavarría, Buenos Aires. El incidente provocó pánico entre los residentes locales. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.")
                           .fechaDeAcontecimiento(LocalDate.of(2001,11,29))
                           .latitud(-36.868375)
                           .longitud(-60.343297)
                           .nombreCategoria(buscarOCrearCategoria("Caída de aeronave").getNombre())
                           .fechaDeCarga(LocalDateTime.now())
                           .build())
                   .origen(OrigenHecho.CONTRIBUYENTE)
                   .estado(EstadoHecho.VISIBLE)
                   .categoria(buscarOCrearCategoria("Caída de aeronave"))
                   .multimedia(List.of(contMult1,contMult2))
                   .transiciones(List.of())
                   .etiquetas(Set.of(etiqueta1, etiqueta2, etiqueta3, etiqueta4))
                   .contribuyente(contribuyente1)
                   .build(),
                Hecho.builder()
                        .data(DataHecho.builder()
                                .titulo("Serio incidente: Accidente con maquinaria industrial en Chos Malal, Neuquén")
                                .descripcion("Un grave accidente con maquinaria industrial se registró en Chos Malal, Neuquén. El incidente dejó a varios sectores sin comunicación. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.")
                                .latitud(-37.345571)
                                .longitud(-70.241485)
                                .fechaDeAcontecimiento(LocalDate.of(2001,8,16))
                                .nombreCategoria(buscarOCrearCategoria("Accidente con maquinaria industrial").getNombre())
                                .fechaDeCarga(LocalDateTime.now())
                                .build())
                        .origen(OrigenHecho.CONTRIBUYENTE)
                        .estado(EstadoHecho.VISIBLE)
                        .categoria(buscarOCrearCategoria("Accidente con maquinaria industrial"))
                        .multimedia(List.of())
                        .transiciones(List.of())
                        .etiquetas(Set.of(etiqueta1,etiqueta6,etiqueta4,etiqueta9))
                        .contribuyente(contribuyente2)
                        .build(),
                Hecho.builder()
                        .data(DataHecho.builder()
                                .titulo("Caída de aeronave impacta en Venado Tuerto, Santa Fe")
                                .descripcion("Grave caída de aeronave ocurrió en las inmediaciones de Venado Tuerto, Santa Fe. El incidente destruyó viviendas y dejó a familias evacuadas. Autoridades nacionales se han puesto a disposición para brindar asistencia.")
                                .latitud(-33.768051)
                                .longitud(-61.921032)
                                .fechaDeAcontecimiento(LocalDate.of(2008,8,8))
                                .nombreCategoria(buscarOCrearCategoria("Caída de aeronave").getNombre())
                                .fechaDeCarga(LocalDateTime.now())
                                .build())
                        .origen(OrigenHecho.CONTRIBUYENTE)
                        .estado(EstadoHecho.VISIBLE)
                        .categoria(buscarOCrearCategoria("Caída de aeronave"))
                        .multimedia(List.of(contMult3))
                        .transiciones(List.of())
                        .etiquetas(Set.of())
                        .contribuyente(contribuyente3)
                        .build(),
                Hecho.builder()
                        .data(DataHecho.builder()
                                .titulo("Accidente en paso a nivel deja múltiples daños en Pehuajó, Buenos Aires")
                                .descripcion("Grave accidente en paso a nivel ocurrió en las inmediaciones de Pehuajó, Buenos Aires. El incidente generó preocupación entre las autoridades provinciales. El Ministerio de Desarrollo")

                                .latitud(-35.855811)
                                .longitud(-61.940589)
                                .fechaDeAcontecimiento(LocalDate.of(2020,1,27))
                                .nombreCategoria(buscarOCrearCategoria("Accidente en paso a nivel").getNombre())
                                .fechaDeCarga(LocalDateTime.now())
                                .build())
                        .origen(OrigenHecho.CONTRIBUYENTE)
                        .estado(EstadoHecho.VISIBLE)
                        .categoria(buscarOCrearCategoria("Accidente en paso a nivel"))
                        .multimedia(List.of())
                        .transiciones(List.of())
                        .etiquetas(Set.of())
                        .contribuyente(contribuyente1)
                        .build(),
                Hecho.builder()
                        .data(DataHecho.builder()
                                .titulo("Devastador Derrumbe en obra en construcción afecta a Presidencia Roque Sáenz Peña")
                                .descripcion("Un grave derrumbe en obra en construcción se registró en Presidencia Roque Sáenz Peña, Chaco. El incidente generó preocupación entre las autoridades provinciales. El intendente local se ha trasladado al lugar para supervisar las operaciones.")
                                .latitud(-26.780008)
                                .longitud(-60.458782)
                                .fechaDeAcontecimiento(LocalDate.of(2016,6,4))
                                .nombreCategoria(buscarOCrearCategoria("Derrumbe en obra en construcción").getNombre())
                                .fechaDeCarga(LocalDateTime.now())
                                .build())
                        .origen(OrigenHecho.CONTRIBUYENTE)
                        .estado(EstadoHecho.VISIBLE)
                        .categoria(buscarOCrearCategoria("Derrumbe en obra en construcción"))
                        .multimedia(List.of(contMult4, contMult5))
                        .transiciones(List.of())
                        .etiquetas(Set.of())
                        //contribuyente anonimo
                        .contribuyente(null)
                        .build()
        );

        hechos.forEach(h->{
            this.hechosRepository.save(h);
        });

    }


    //TODO: no me termina de cerrar si esto deberia ir aca o en repositorio
    private Categoria buscarOCrearCategoria(String nombre){
        return categoriasRepository.findAll().stream()
                .filter(c->c.getNombre().equalsIgnoreCase(nombre))
                .findFirst().orElseGet(()->{
                    Categoria nueva = new Categoria(nombre);
                    categoriasRepository.save(nueva);
                    return nueva;
                });
    }

    private Etiqueta buscarOCrearEtiqueta(String nombre){
        return etiquetasRepository.findAll().stream()
                .filter(e->e.getNombre().equalsIgnoreCase(nombre))
                .findFirst().orElseGet(()->{
                    Etiqueta nueva = new Etiqueta(nombre);
                    etiquetasRepository.save(nueva);
                    return nueva;
                });
    }


}
