package ar.edu.utn.frba.dds.datasource;

import ar.edu.utn.frba.dds.model.domain.hecho.DataHecho;
import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frba.dds.model.domain.hecho.OrigenHecho;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.io.FileReader;

public class FuenteEstatica extends Fuente {
  private String archivo;

  public FuenteEstatica(String archivo) {
    this.archivo = archivo;
  }

  public List<Hecho> importarHechos() throws Exception {
    List<Hecho> hechos = new ArrayList<>();

    for(DataHecho dataHecho : this.leerHechos()) {
      Hecho hecho = new Hecho(dataHecho, OrigenHecho.DATASET);
      hechos.add(hecho);
    }
    return hechos;
  }

  public void abrirFuente() {
    //TODO
  }

  public List<DataHecho> leerHechos() throws Exception {
    try (FileReader reader = new FileReader(this.archivo)) {
      CsvToBean<DataHecho> csvToBean = new CsvToBeanBuilder<DataHecho>(reader)
              .withType(DataHecho.class)
              .withIgnoreLeadingWhiteSpace(true)
              .build();

      return csvToBean.parse();
    }
  }

  public void cerrarFuente() {
    //TODO
  }

}
