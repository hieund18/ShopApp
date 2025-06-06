CREATE DATABASE shopapp;
USE shopapp;

CREATE TABLE users(
	id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(100) NOT NULL COLLATE utf8mb4_unicode_ci UNIQUE,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    date_of_birth DATE,
    github_account_id VARCHAR(255) UNIQUE,
    google_account_id VARCHAR(255) UNIQUE
);

-- ALTER TABLE users ADD COLUMN role_id INT;

-- alter table users modify column phone_number VARCHAR(100) NOT NULL COLLATE utf8mb4_unicode_ci UNIQUE;
-- alter table users add column github_account_id VARCHAR(255) DEFAULT '';
-- ALTER TABLE users ADD CONSTRAINT unique_github_id UNIQUE (github_account_id);
-- alter table users modify column google_account_id varchar(255) default null unique;

CREATE TABLE roles(
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL
);

-- ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles (id);



CREATE TABLE users_roles(
	user_id INT,
    role_id INT,
    CONSTRAINT pk_users_roles PRIMARY KEY (user_id, role_id),
	FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	FOREIGN KEY (role_id) REFERENCES roles (id) -- ON DELETE RESTRICT (thua - mac dinh la restrict)
);

-- alter table roles auto_increment =1;

-- CREATE TABLE tokens(
-- 	id INT PRIMARY KEY AUTO_INCREMENT,
-- 	token VARCHAR(255) UNIQUE NOT NULL,
--     token_type VARCHAR(50) NOT NULL,
--     expiration_date DATETIME,
--     revoked TINYINT(1) NOT NULL,
--     expired TINYINT(1) NOT NULL,
--     user_id INT,
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- );

CREATE TABLE invalidated_tokens(
	id VARCHAR(50) PRIMARY KEY NOT NULL,
    expiry_time DATETIME
);

CREATE TABLE social_accounts(
	id INT PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social network',
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL COMMENT 'Email tai khoan',
    name VARCHAR(100) NOT NULL COMMENT 'Ten nguoi dung',
	user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE categories(
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Ten danh muc, vd: do dien tu'
);

CREATE TABLE products(
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(350) COMMENT 'Ten san pham',
    price FLOAT NOT NULL CHECK(price >= 0),
    thumbnail VARCHAR(300) DEFAULT '',
    description LONGTEXT,
    created_at DATETIME,
    updated_at DATETIME,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    is_active TINYINT(1) NOT NULL DEFAULT 1
);

ALTER TABLE products ADD COLUMN quantity INT NOT NULL CHECK(quantity >= 0);

CREATE TABLE orders(
	id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    fullname VARCHAR(100) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT '',
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status VARCHAR(20),
    total_money FLOAT CHECK(total_money >= 0)
);

ALTER TABLE orders ADD COLUMN shipping_method VARCHAR(100);
ALTER TABLE orders ADD COLUMN shipping_address VARCHAR(200);
ALTER TABLE orders ADD COLUMN shipping_date DATE;
ALTER TABLE orders ADD COLUMN tracking_number VARCHAR(100);
ALTER TABLE orders ADD COLUMN payment_method VARCHAR(100);

ALTER TABLE orders ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE orders
MODIFY COLUMN status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')
COMMENT 'Trang thai don hang';


CREATE TABLE order_details(
	id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products (id),
    price FLOAT CHECK(price >= 0),
    number_of_products INT CHECK(number_of_products > 0),
    total_money FLOAT CHECK(total_money >= 0),
    color VARCHAR(20) DEFAULT ''
);

CREATE TABLE product_images(
	id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    CONSTRAINT fk_product_images_product_id 
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    image_url VARCHAR(300),
    is_primary TINYINT(1) DEFAULT 0
);

CREATE TABLE carts(
	id	INT PRIMARY KEY AUTO_INCREMENT,
	user_id	INT,
    CONSTRAINT fk_cart_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    product_id INT,
    CONSTRAINT fk_cart_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    quantity INT NOT NULL CHECK (quantity > 0),
    price FLOAT NOT NULL,
    total_money FLOAT NOT NULL,
    color VARCHAR(50),
    created_at DATETIME
);

ALTER TABLE carts ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP;

    