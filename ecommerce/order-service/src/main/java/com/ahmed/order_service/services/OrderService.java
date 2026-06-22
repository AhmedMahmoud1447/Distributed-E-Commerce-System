package com.ahmed.order_service.services;

import com.ahmed.order_service.dtos.OrderDto;

public interface OrderService {
    OrderDto.Response createOrder(OrderDto.Request request);
    OrderDto.Response getOrderById(Long id);
}