package com.example.order_service.config.utils;

public enum OrderStatus {
    PENDING,       // Chờ xác nhận
    CONFIRMED,     // Đã xác nhận
    PICKING,       // Đang lấy hàng tại kho
    SHIPPING,      // Đang giao hàng
    DELIVERED,     // Giao hàng thành công
    FAILED,        // Giao hàng thất bại
    RETURNING,     // Đang hoàn hàng về kho
    REATTEMPT,     // Chờ giao lại
    CANCELLED      // Đã hủy
}