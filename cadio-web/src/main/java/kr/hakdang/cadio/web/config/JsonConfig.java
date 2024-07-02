package kr.hakdang.cadio.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hakdang.cadio.common.Jsons;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return Jsons.OBJECT_MAPPER;
    }

}
