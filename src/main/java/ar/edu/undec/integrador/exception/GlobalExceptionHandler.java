package ar.edu.undec.integrador.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(RecursoNoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage());
    }

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ProblemDetail reglaDeNegocio(ReglaDeNegocioException ex) {
        return problema(HttpStatus.CONFLICT, "Conflicto", ex.getMessage());
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ProblemDetail datosInvalidos(DatosInvalidosException ex) {
        ProblemDetail detalle = problema(HttpStatus.BAD_REQUEST, "Datos inválidos", ex.getMessage());
        if (!ex.getErrores().isEmpty()) {
            detalle.setProperty("errores", ex.getErrores());
        }
        return detalle;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ProblemDetail detalle = problema(HttpStatus.BAD_REQUEST, "Datos inválidos", "La petición tiene errores");
        detalle.setProperty("errores", errores);
        return detalle;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail restriccion(ConstraintViolationException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (ConstraintViolation<?> violacion : ex.getConstraintViolations()) {
            String ruta = violacion.getPropertyPath().toString();
            String campo = ruta.substring(ruta.lastIndexOf('.') + 1);
            errores.putIfAbsent(campo, violacion.getMessage());
        }
        ProblemDetail detalle = problema(HttpStatus.BAD_REQUEST, "Datos inválidos", "La petición tiene errores");
        detalle.setProperty("errores", errores);
        return detalle;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail cuerpoIlegible(HttpMessageNotReadableException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Datos inválidos", "El cuerpo de la petición no es válido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tipoInvalido(MethodArgumentTypeMismatchException ex) {
        return problema(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "El parámetro '" + ex.getName() + "' tiene un formato inválido");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail parametroFaltante(MissingServletRequestParameterException ex) {
        return problema(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "Falta el parámetro '" + ex.getParameterName() + "'");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridad(DataIntegrityViolationException ex) {
        return problema(
                HttpStatus.CONFLICT,
                "Conflicto",
                "No se pudo completar la operación por una restricción de datos");
    }

    private ProblemDetail problema(HttpStatus estado, String titulo, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        return problema;
    }
}
