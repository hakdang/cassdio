package kr.hakdang.cassdio.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hakdang.cassdio.common.utils.Jsons;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return Jsons.OBJECT_MAPPER;
    }

}
