package ru.itlab.ordersservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itlab.ordersservice.dto.CreateOrderRequest;
import ru.itlab.ordersservice.dto.OrderResponse;
import ru.itlab.ordersservice.dto.SortBy;

public interface MenuOrderService {

    Mono<OrderResponse> createOrder(CreateOrderRequest request, String username);

    Flux<OrderResponse> getOrdersOfUser(String username, SortBy sortBy, int from, int size);
}
