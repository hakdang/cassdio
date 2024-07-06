package kr.hakdang.cassdio.web;

import kr.hakdang.cassdio.common.PackageConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = PackageConstants.BASE_PACKAGE)
@SpringBootApplication(scanBasePackages = {PackageConstants.BASE_PACKAGE})
public class CassdioWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CassdioWebApplication.class, args);
    }

}
