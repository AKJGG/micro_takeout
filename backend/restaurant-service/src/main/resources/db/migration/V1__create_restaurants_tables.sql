CREATE SCHEMA IF NOT EXISTS restaurant_service;

CREATE TABLE IF NOT EXISTS restaurant_service.restaurants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(20),
    image_url VARCHAR(255),
    owner_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    rating DECIMAL(3, 2) DEFAULT 0.00,
    total_reviews INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS restaurant_service.menu_items (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurant_service.restaurants(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    category VARCHAR(50),
    available BOOLEAN DEFAULT TRUE,
    stock INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_restaurants_owner_id ON restaurant_service.restaurants(owner_id);
CREATE INDEX idx_restaurants_status ON restaurant_service.restaurants(status);
CREATE INDEX idx_menu_items_restaurant_id ON restaurant_service.menu_items(restaurant_id);
CREATE INDEX idx_menu_items_category ON restaurant_service.menu_items(category);

