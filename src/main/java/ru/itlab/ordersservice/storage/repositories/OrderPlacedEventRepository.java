package ru.itlab.ordersservice.storage.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.itlab.ordersservice.storage.model.OrderPlacedEvent;

public interface OrderPlacedEventRepository extends ReactiveCrudRepository<OrderPlacedEvent, Long> {
}