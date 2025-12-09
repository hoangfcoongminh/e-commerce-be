package com.edward.order.enums;

public enum OrderStatus {
    PENDING,          // Chờ xác nhận (đơn mới tạo, chưa xử lý)
    CONFIRMED,        // Đã xác nhận (shop/cửa hàng duyệt)
    PROCESSING,       // Đang chuẩn bị hàng
    SHIPPING,         // Đang giao hàng (đã đưa cho đơn vị vận chuyển)
    DELIVERED,        // Giao hàng thành công
    CANCELLED,        // Người dùng hoặc shop hủy trước khi giao
    RETURN_REQUESTED, // Người dùng yêu cầu trả hàng / hoàn tiền
    RETURNED,         // Đã trả hàng / hoàn tiền thành công
    FAILED            // Giao hàng thất bại (không liên lạc được, sai địa chỉ…)
}
