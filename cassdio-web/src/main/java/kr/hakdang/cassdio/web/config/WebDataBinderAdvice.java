package kr.hakdang.cassdio.web.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * WebDataBinderAdvice
 *
 * @author seungh0
 * @since 2024-07-01
 */
@RestControllerAdvice
public class WebDataBinderAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

}
