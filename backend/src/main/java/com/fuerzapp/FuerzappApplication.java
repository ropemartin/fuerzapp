// Copyright (c) 2026 Roberto Martín Casañas. Todos los derechos reservados.
package com.fuerzapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FuerzappApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuerzappApplication.class, args);
    }
}
