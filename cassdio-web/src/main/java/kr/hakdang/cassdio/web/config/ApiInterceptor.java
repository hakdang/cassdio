package kr.hakdang.cassdio.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hakdang.cassdio.web.config.context.CassdioContext;
import kr.hakdang.cassdio.web.config.context.CassdioContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.UUID;

/**
 * ApiInterceptor
 *
 * @author akageun
 * @since 2024-07-25
 */
@Slf4j
@Component
public class ApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        CassdioContextHolder.set(
            CassdioContext.builder()
                .requestId(UUID.randomUUID().toString())
                .build()
        );

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CassdioContextHolder.clear();
    }
}
