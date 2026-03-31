package com.flexicharge.flexicharge.shared;

public final class AppConstants {

    // Constructor privado para evitar instanciación
    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }

    // Seguridad y JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";

    // Mensajes de Error
    public static final String ERR_CUSTOMER_NOT_FOUND = "Cliente no encontrado con id: ";
    public static final String ERR_EMAIL_EXISTS = "El email ya está registrado.";
    public static final String ERR_PLAN_NOT_FOUND = "El Plan seleccionado no existe.";
    public static final String ERR_PLAN_EXISTS = "El Plan seleccionado existe.";
    public static final String ERR_CUSTOMER_EMAIL_EXISTS = "No existe un cliente con email: ";

    // Estados
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    // Paginación por defecto (si la usas luego)
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";

    public static final String STARTED = "STARTED";
}
