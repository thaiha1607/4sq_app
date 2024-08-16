package com.foursquare.server.service.helper;

public enum ShipmentStatusCode {
    PENDING(1),
    PROCESSED(2),
    SHIPPED(3),
    IN_TRANSIT(4),
    OUT_FOR_DELIVERY(5),
    DELIVERED(6),
    FAILED_DELIVERY_ATTEMPT(7),
    RETURNED(8),
    CANCELLED(9),
    ON_HOLD(10);

    private final int id;

    ShipmentStatusCode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ShipmentStatusCode fromId(int id) {
        for (ShipmentStatusCode statusCode : ShipmentStatusCode.values()) {
            if (statusCode.getId() == id) {
                return statusCode;
            }
        }
        throw new IllegalArgumentException("Invalid ShipmentStatusCode id: " + id);
    }
}
