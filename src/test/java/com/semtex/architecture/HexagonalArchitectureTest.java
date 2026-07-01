package com.semtex.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Verifica las reglas de la arquitectura hexagonal. Estos tests FALLAN si el dominio importa
 * infraestructura o frameworks, o si una capa viola la regla de dependencia.
 */
@AnalyzeClasses(packages = "com.semtex", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule el_dominio_no_depende_de_spring = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
            .as("El dominio debe ser POJO puro (sin Spring).");

    @ArchTest
    static final ArchRule el_dominio_no_depende_de_jpa = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "org.hibernate..")
            .as("El dominio no debe conocer JPA/Hibernate.");

    @ArchTest
    static final ArchRule el_dominio_no_depende_de_infraestructura = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .as("El dominio no debe depender de adaptadores de infraestructura.");

    @ArchTest
    static final ArchRule la_aplicacion_no_depende_de_infraestructura = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .as("La capa de aplicación solo depende de puertos del dominio, no de infraestructura.");
}
