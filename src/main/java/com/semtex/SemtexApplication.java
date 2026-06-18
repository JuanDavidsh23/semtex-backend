package com.semtex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de Semtex — copiloto administrativo/financiero con IA para PyMEs.
 *
 * Arquitectura hexagonal modular por bounded context:
 *   shared · identity · document · financial · chat · email · audit
 *
 * Regla de dependencia dentro de cada contexto: infrastructure -> application -> domain.
 * El dominio es POJO puro (sin Spring/JPA). Verificado con ArchUnit.
 */
@SpringBootApplication
public class SemtexApplication {

    public static void main(String[] args) {
        SpringApplication.run(SemtexApplication.class, args);
    }
}
