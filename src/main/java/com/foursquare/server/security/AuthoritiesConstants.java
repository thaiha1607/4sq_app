package com.foursquare.server.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String SALESPERSON = "ROLE_SALESPERSON";

    public static final String WAREHOUSE = "ROLE_WAREHOUSE";

    public static final String SHIPPER = "ROLE_SHIPPER";

    public static final String MANAGER = "ROLE_MANAGER";

    private AuthoritiesConstants() {}
}
