package uk.sky.pm.api.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.sky.pm.api.rest.v1.dto.ErrorApiDto;
import uk.sky.pm.enums.ErrorCode;
import uk.sky.pm.exception.BusinessConflictException;
import uk.sky.pm.exception.BusinessNotFoundException;
import uk.sky.pm.service.LocalizationService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class ExceptionHandlerController {

    private final LocalizationService localizationService;

    @ExceptionHandler(exception = BusinessConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorApiDto handeBusinessConflictException(final BusinessConflictException ex) {
        return new ErrorApiDto(ex.getErrorCode().name(), localizationService.localize(ex.getErrorCode()));
    }

    @ExceptionHandler(exception = BusinessNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorApiDto handeBusinessNotFoundException(final BusinessNotFoundException ex) {
        return new ErrorApiDto(ex.getErrorCode().name(), localizationService.localize(ex.getErrorCode()));
    }

    @ExceptionHandler(exception = HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApiDto handeHandlerMethodValidationException(final HandlerMethodValidationException ex) {
        var errorMessage = CollectionUtils.emptyIfNull(ex.getParameterValidationResults()).stream()
            .flatMap(result -> CollectionUtils.emptyIfNull(result.getResolvableErrors()).stream())
            .map(MessageSourceResolvable::getDefaultMessage)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Invalid input data");
        return new ErrorApiDto(ErrorCode.INVALID_DATA.name(), errorMessage);
    }

    @ExceptionHandler(exception = MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApiDto handeMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        log.debug("Invalid input value '{}' in REST request for param '{}' and require type is '{}'", ex.getValue(), ex.getPropertyName(), ex.getRequiredType());
        return new ErrorApiDto(ErrorCode.WRONG_INPUT_DATA.name(),
            localizationService.localize(ErrorCode.WRONG_INPUT_DATA, List.of(
                Optional.ofNullable(ex.getValue()).map(Object::toString).orElse("N/A"),
                ex.getPropertyName(),
                Optional.ofNullable(ex.getRequiredType()).map(Class::getName).orElse("N/A"))));
    }

    @ExceptionHandler(exception = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApiDto handeBindException(final BindException ex) {
        return new ErrorApiDto(ErrorCode.INVALID_DATA.name(), localizationService.localize(ex.getFieldError()));
    }

    @ExceptionHandler(exception = RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApiDto handeRuntimeException(final RuntimeException ex) {
        log.error("Runtime exception has occured", ex);
        return new ErrorApiDto(ErrorCode.OTHER_ERROR.name(), localizationService.localize(ErrorCode.OTHER_ERROR));
    }

    @ExceptionHandler(exception = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorApiDto handeAccessDeniedException(final AccessDeniedException ex) {
        return new ErrorApiDto(ErrorCode.ACCESS_DENIED.name(), localizationService.localize(ErrorCode.ACCESS_DENIED));
    }
}
