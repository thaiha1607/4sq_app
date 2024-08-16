package com.foursquare.server.service.helper;

public enum InvoiceStatusCode {
    DRAFT(1),
    ACTIVE(2),
    SENT(3),
    DISPUTED(4),
    OVERDUE(5),
    PARTIAL(6),
    PAID(7),
    VOID(8),
    DEBT(9),
    RESERVED(10);

    private final int id;

    InvoiceStatusCode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static InvoiceStatusCode fromId(int id) {
        for (InvoiceStatusCode statusCode : InvoiceStatusCode.values()) {
            if (statusCode.getId() == id) {
                return statusCode;
            }
        }
        throw new IllegalArgumentException("Invalid InvoiceStatusCode id: " + id);
    }
}
