package uk.sky.pm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.sky.pm.enums.ErrorCode;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalizationServiceImplTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private LocalizationServiceImpl localizationService;

    @Test
    void localize_shouldReturnLocalizedMessage_forErrorCodeWithoutParams() {
        var errorCode = ErrorCode.INVALID_DATA;
        var locale = Locale.ENGLISH;
        LocaleContextHolder.setLocale(locale);

        var expectedMessage = "Invalid request";
        when(messageSource.getMessage(eq(errorCode.name()), any(), any())).thenReturn(expectedMessage);

        var result = localizationService.localize(errorCode);

        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(errorCode.name(), new Object[]{}, locale);
    }

    @Test
    void localize_shouldReturnLocalizedMessage_forErrorCodeWithParams() {
        // Given
        var errorCode = ErrorCode.USER_NOT_FOUND;
        var params = List.of("john.doe@example.com");
        var locale = Locale.ENGLISH;
        LocaleContextHolder.setLocale(locale);

        var expectedMessage = "User john.doe@example.com not found";
        when(messageSource.getMessage(errorCode.name(), params.toArray(), locale))
            .thenReturn(expectedMessage);

        var result = localizationService.localize(errorCode, params);

        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(errorCode.name(), params.toArray(), locale);
    }

    @Test
    void localize_shouldReturnLocalizedMessage_forResolvable() {
        var resolvable = mock(MessageSourceResolvable.class);
        var locale = Locale.ENGLISH;
        LocaleContextHolder.setLocale(locale);

        var expectedMessage = "Some resolved message";
        when(messageSource.getMessage(resolvable, locale)).thenReturn(expectedMessage);

        var result = localizationService.localize(resolvable);

        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(resolvable, locale);
    }
}

