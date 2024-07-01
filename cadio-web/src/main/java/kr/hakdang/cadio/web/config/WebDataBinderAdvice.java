package kr.hakdang.cadio.web.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebDataBinderAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

}
