package ar.edu.utn.frba.dds.datasource;

import ar.edu.utn.frba.dds.model.domain.hecho.DataHecho;
import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import java.util.List;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;

public class FuenteEstatica extends Fuente {
  private String archivo;

  public List<Hecho> importarHechos() {
    //TODO
    return null;
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
