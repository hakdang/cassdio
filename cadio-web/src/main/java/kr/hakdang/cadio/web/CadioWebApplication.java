package kr.hakdang.cadio.web;

import org.springframework.boot.SpringApplication;

//@ConfigurationPropertiesScan(basePackages = PackageConstants.BASE_PACKAGE)
//@SpringBootApplication(scanBasePackages = {PackageConstants.BASE_PACKAGE})
public class CadioWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadioWebApplication.class, args);
    }

}
