package ar.edu.undec.integrador.exception;

import java.util.Map;

public class DatosInvalidosException extends RuntimeException {

    private final Map<String, String> errores;

    public DatosInvalidosException(String mensaje) {
        this(mensaje, Map.of());
    }

    public DatosInvalidosException(String mensaje, Map<String, String> errores) {
        super(mensaje);
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
