package ar.utn.ba.ddsi.estatica.domain.hecho;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_DATE_ONLY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected LocalDateTime convert(String value) throws CsvDataTypeMismatchException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String trimmedValue = value.trim();

        // Intentar parsear con hora primero
        try {
            return LocalDateTime.parse(trimmedValue, FORMATTER_WITH_TIME);
        } catch (DateTimeParseException e) {
            // Si falla, intentar parsear solo la fecha y asignar hora 00:00:00
            try {
                LocalDate date = LocalDate.parse(trimmedValue, FORMATTER_DATE_ONLY);
                return date.atStartOfDay(); // 00:00:00
            } catch (DateTimeParseException ex) {
                throw new CsvDataTypeMismatchException(
                    "No se pudo parsear la fecha: " + value +
                    ". Se esperaba formato 'dd/MM/yyyy' o 'dd/MM/yyyy HH:mm:ss'"
                );
            }
        }
    }
}

