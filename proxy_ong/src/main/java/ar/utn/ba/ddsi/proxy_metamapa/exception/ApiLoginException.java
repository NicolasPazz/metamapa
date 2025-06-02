package ar.utn.ba.ddsi.proxy_metamapa.exception;

public class ApiLoginException extends RuntimeException {
  public ApiLoginException(String message, Throwable cause) {
    super(message, cause);
  }
  public ApiLoginException(String message) {
    super(message);
  }
}