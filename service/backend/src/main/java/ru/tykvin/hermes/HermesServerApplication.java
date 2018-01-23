package ru.tykvin.hermes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HermesServerApplication {
    static {
        System.setProperty("org.jooq.no-logo", "true");
    }
    public static void main(String[] args) {
        SpringApplication.run(HermesServerApplication.class, args);
    }
}
