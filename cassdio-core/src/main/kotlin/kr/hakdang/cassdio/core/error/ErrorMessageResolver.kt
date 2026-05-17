package kr.hakdang.cassdio.core.error

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

@Component
class ErrorMessageResolver(
    private val messageSource: MessageSource,
) {
    fun resolve(errorCode: ErrorCode): String =
        try {
            messageSource.getMessage(errorCode.messageKey, null, LocaleContextHolder.getLocale())
        } catch (_: NoSuchMessageException) {
            errorCode.defaultMessage
        }
}
