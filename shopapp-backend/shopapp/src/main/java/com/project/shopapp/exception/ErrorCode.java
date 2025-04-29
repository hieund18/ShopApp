package com.project.shopapp.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    // loi chung
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT(1002, "Invalid format data", HttpStatus.BAD_REQUEST),
    INVALID_FILE(1003, "Invalid file", HttpStatus.BAD_REQUEST),
    INVALID_FILE_SIZE(1004, "File is too large! Maximum size is 10 MB", HttpStatus.PAYLOAD_TOO_LARGE),
    DATA_CONFLICT(1005, "Cannot delete because this item is still in use", HttpStatus.CONFLICT),

    // category error
    CATEGORY_NAME_NOT_BLANK(1101, "Category's name cannot be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(1102, "Category existed", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1103, "Category not existed", HttpStatus.NOT_FOUND),

    // role error
    ROLE_EXISTED(1201, "PredefinedRole existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1202, "PredefinedRole not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_BLANK(1203, "PredefinedRole cannot be blank", HttpStatus.BAD_REQUEST),

    // product error
    PRODUCT_NAME_NOT_EMPTY(1301, "Product's name cannot be blank", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_NAME(1302, "Title must be at least {min} characters", HttpStatus.BAD_REQUEST),
    MIN_PRODUCT_PRICE(1303, "Price must be greater than or equal to {value}", HttpStatus.BAD_REQUEST),
    MAX_PRODUCT_PRICE(1304, "Price must be less than or equal to {value}", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(1305, "Product existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(1306, "Product not existed", HttpStatus.NOT_FOUND),
    PRICE_NOT_BLANK(1307, "Price cannot be blank", HttpStatus.BAD_REQUEST),
    QUANTITY_NOT_BLANK(1308, "Quantity cannot be blank", HttpStatus.BAD_REQUEST),
    MIN_PRODUCT_QUANTITY(1309, "Quantity must be greater than or equal to {value}", HttpStatus.BAD_REQUEST),

    // user error
    PHONE_NUMBER_NOT_BLANK(1401, "Phone number cannot be blank", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(1402, "Phone number must be 10 digits", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_BLANK(1403, "Password cannot be blank", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1404, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1405, "Phone number existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1406, "User not existed", HttpStatus.NOT_FOUND),
    INVALID_DOB(1407, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    DEACTIVATED_USER(1408, "User is deactivated", HttpStatus.BAD_REQUEST),
    PASSWORD_EXISTED(1409, "Password existed", HttpStatus.BAD_REQUEST),
    ACCOUNT_LINKED_GOOGLE(1410, "Account linked to google", HttpStatus.BAD_REQUEST),
    GOOGLE_ACCOUNT_EXISTED(1411, "Google account linked to another user", HttpStatus.BAD_REQUEST),
    ACCOUNT_LINKED_GITHUB(1412, "Account linked to github", HttpStatus.BAD_REQUEST),
    GITHUB_ACCOUNT_EXISTED(1413, "Github account linked to another user", HttpStatus.BAD_REQUEST),

    // productImage error
    MAX_IMAGE_QUANTITY(1501, "Maximum 5 images", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_EXISTED(1502, "Image not existed", HttpStatus.NOT_FOUND),

    // order error
    ADDRESS_NOT_BLANK(1601, "Address cannot be blank", HttpStatus.BAD_REQUEST),
    MIN_TOTAL_MONEY(1602, "Total money must be greater than or to {value}", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(1603, "Order not existed", HttpStatus.NOT_FOUND),
    INVALID_SHIPPING_DATE(1604, "Shipping date must be after current date", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1605, "Invalid order status", HttpStatus.BAD_REQUEST),
    CART_EMPTY(1606, "Cart empty", HttpStatus.BAD_REQUEST),
    CANNOT_MODIFY_ORDER(1607, "Cannot modify order", HttpStatus.BAD_REQUEST),
    INVALID_STATE_TRANSITION(1608, "Invalid state transition", HttpStatus.BAD_REQUEST),
    INVALID_ACTIVE_STATUS(1609, "Invalid active status update", HttpStatus.BAD_REQUEST),

    // orderDetail error
    MIN_ORDER_QUANTITY(1701, "Quantity order must be greater than or equal to {value}", HttpStatus.BAD_REQUEST),
    ORDER_QUANTITY_NOT_BLANK(1702, "Order quantity cannot be blank", HttpStatus.BAD_REQUEST),
    ORDER_DETAIL_NOT_EXISTED(1703, "Order detail not existed", HttpStatus.NOT_FOUND),

    // cart error
    EXCEEDED_QUANTITY_AVAILABLE(1801, "Exceeded quantity available", HttpStatus.BAD_REQUEST),
    CART_NOT_EXISTED(1802, "Cart not exited", HttpStatus.NOT_FOUND),
    INVALID_PRODUCT(1803, "Invalid product", HttpStatus.BAD_REQUEST),

    // authenticate error
    UNAUTHENTICATED(1901, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1902, "You do not have permission", HttpStatus.FORBIDDEN),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    int code;
    String message;
    HttpStatusCode statusCode;
}
