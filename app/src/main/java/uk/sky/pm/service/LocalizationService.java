package uk.sky.pm.service;

import org.springframework.context.MessageSourceResolvable;
import uk.sky.pm.enums.ErrorCode;

import java.util.List;

public interface LocalizationService {

    String localize(ErrorCode errorCode);

    String localize(MessageSourceResolvable resolvable);

    String localize(ErrorCode errorCode, List<String> params);

}
