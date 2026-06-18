/**
 * Contexto <b>shared</b>: piezas transversales reutilizadas por el resto de contextos.
 *
 * <ul>
 *   <li>{@code tenant} — {@code TenantContext} request-scoped con el tenant resuelto del JWT.</li>
 *   <li>{@code security} — base de Spring Security (resource server) y resolución de tenant.</li>
 *   <li>{@code web} — {@code GlobalExceptionHandler} y formato de error común.</li>
 *   <li>{@code persistence} — soporte de filtro Hibernate multi-tenant.</li>
 * </ul>
 *
 * No contiene reglas de negocio; es infraestructura compartida.
 */
package com.semtex.shared;
