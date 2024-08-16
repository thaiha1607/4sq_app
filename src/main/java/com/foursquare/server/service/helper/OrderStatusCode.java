package com.foursquare.server.service.helper;

public enum OrderStatusCode {
    PENDING(1),
    CONFIRMED(2),
    PROCESSING(3),
    WAITING_FOR_ACTION(4),
    SHIPPED(5),
    DELIVERED(6),
    CANCELLED(7),
    RETURNED(8),
    ON_HOLD(9),
    FAILED_DELIVERY_ATTEMPT(10),
    REFUNDED(11),
    PARTIALLY_SHIPPED(12),
    PARTIALLY_DELIVERED(13),
    AWAITING_PAYMENT(14);

    private final int id;

    OrderStatusCode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static OrderStatusCode fromId(int id) {
        for (OrderStatusCode statusCode : OrderStatusCode.values()) {
            if (statusCode.getId() == id) {
                return statusCode;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatusCode id: " + id);
    }
}
