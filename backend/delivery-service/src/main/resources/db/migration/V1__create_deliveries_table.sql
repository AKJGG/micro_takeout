CREATE SCHEMA IF NOT EXISTS delivery_service;

CREATE TABLE IF NOT EXISTS delivery_service.deliveries (
    id BIGSERIAL PRIMARY KEY,
    delivery_number VARCHAR(50) NOT NULL UNIQUE,
    order_id BIGINT NOT NULL,
    delivery_person_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    pickup_address VARCHAR(255),
    delivery_address VARCHAR(255),
    current_latitude DECIMAL(10, 8),
    current_longitude DECIMAL(11, 8),
    estimated_arrival_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deliveries_order_id ON delivery_service.deliveries(order_id);
CREATE INDEX idx_deliveries_delivery_person_id ON delivery_service.deliveries(delivery_person_id);
CREATE INDEX idx_deliveries_status ON delivery_service.deliveries(status);

