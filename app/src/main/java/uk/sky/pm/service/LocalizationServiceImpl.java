package uk.sky.pm.service;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import uk.sky.pm.enums.ErrorCode;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LocalizationServiceImpl implements LocalizationService {

    private final MessageSource messageSource;

    @Override
    public String localize(final ErrorCode errorCode) {
        return localize(errorCode, null);
    }

    @Override
    public String localize(MessageSourceResolvable resolvable) {
        return messageSource.getMessage(resolvable, LocaleContextHolder.getLocale());
    }

    @Override
    public String localize(final ErrorCode errorCode, final List<String> params) {
        return Optional.ofNullable(errorCode)
            .map(errCode -> messageSource
                .getMessage(errorCode.name(), CollectionUtils.emptyIfNull(params).toArray(), LocaleContextHolder.getLocale()))
            .orElse(null);
    }
}
