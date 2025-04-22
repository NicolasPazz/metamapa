package ar.edu.utn.frba.dds.domain.datasource;

import ar.edu.utn.frba.dds.domain.model.hecho.DataHecho;
import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;
import ar.edu.utn.frba.dds.domain.model.hecho.OrigenHecho;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FuenteEstatica extends Fuente {

  private String rutaArchivo;
  private FileReader lector;

  public FuenteEstatica(String rutaArchivo) {
    this.rutaArchivo = rutaArchivo;
  }

  @Override
  public void abrirFuente() throws Exception {
    this.lector = new FileReader(this.rutaArchivo);
  }

  @Override
  public List<Hecho> importarHechos() throws Exception {
    this.abrirFuente();

    CsvToBean<DataHecho> csvToBean = new CsvToBeanBuilder<DataHecho>(this.lector)
        .withType(DataHecho.class)
        .withIgnoreLeadingWhiteSpace(true)
        .build();

    Iterator<DataHecho> iterator = csvToBean.iterator();
    Map<String, Hecho> hechosPorTitulo = new LinkedHashMap<>();

    while (iterator.hasNext()) {
      DataHecho data = iterator.next();
      Hecho nuevoHecho = new Hecho(data, OrigenHecho.DATASET);
      String titulo = nuevoHecho.getTitulo();

      hechosPorTitulo.put(titulo, nuevoHecho);
    }

    this.cerrarFuente();
    return new ArrayList<>(hechosPorTitulo.values());
  }

  @Override
  public void cerrarFuente() {
    try {
      if (this.lector != null) {
        this.lector.close();
      }
    } catch (IOException e) {
      throw new RuntimeException("Error al cerrar el archivo CSV", e);
    }
  }
}