package kr.hakdang.cadio.web;

import kr.hakdang.cadio.common.PackageConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = PackageConstants.BASE_PACKAGE)
@SpringBootApplication(scanBasePackages = {PackageConstants.BASE_PACKAGE})
public class CadioWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadioWebApplication.class, args);
    }

}
