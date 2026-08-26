package ru.itlab.ordersservice.storage.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.itlab.ordersservice.BaseDBDependencyTest;
import ru.itlab.ordersservice.config.R2dbcConfig;
import ru.itlab.ordersservice.storage.model.MenuOrder;

import static ru.itlab.ordersservice.testdata.TestConstants.*;


@Import({R2dbcConfig.class})
@ImportAutoConfiguration({JacksonAutoConfiguration.class})
@DataR2dbcTest
public class MenuOrderRepositoryTest extends BaseDBDependencyTest {
    @Autowired
    private MenuOrderRepository repository;

    @Test
    void findAllByCreatedBy_returnsCorrectSortedByDateAsc() {
        var pageRequest = PageRequest.of(0, 2)
                .withSort(Sort.by(Sort.Direction.ASC,"createdAt"));
        Flux<MenuOrder> orders = repository.findAllByCreatedBy(USERNAME_ONE, pageRequest);
        StepVerifier.create(orders)
                .expectNextMatches(order ->
                        order.getCreatedBy().equals(USERNAME_ONE) &&
                                order.getCreatedAt().equals(ORDER_ONE_DATE))
                .expectNextMatches(order ->
                        order.getCreatedBy().equals(USERNAME_ONE) &&
                                order.getCreatedAt().equals(ORDER_TWO_DATE))
                .verifyComplete();
    }

    @Test
    void findAllByCreatedBy_returnsCorrectSortedByDateDesc() {
        var pageRequest = PageRequest.of(0, 2)
                .withSort(Sort.by(Sort.Direction.DESC,"createdAt"));
        Flux<MenuOrder> orders = repository.findAllByCreatedBy(USERNAME_ONE, pageRequest);
        StepVerifier.create(orders)
                .expectNextMatches(order ->
                        order.getCreatedBy().equals(USERNAME_ONE) &&
                                order.getCreatedAt().equals(ORDER_THREE_DATE))
                .expectNextMatches(order ->
                        order.getCreatedBy().equals(USERNAME_ONE) &&
                                order.getCreatedAt().equals(ORDER_TWO_DATE))
                .verifyComplete();
    }

    @Test
    void findAllByCreatedBy_returnsEmptyListWhenUserHasNoOrders() {
        var pageRequest = PageRequest.of(0, 10)
                .withSort(Sort.by(Sort.Direction.ASC,"createdAt"));
        Flux<MenuOrder> orders = repository.findAllByCreatedBy("unknown user", pageRequest);
        StepVerifier.create(orders)
                .expectNextCount(0)
                .verifyComplete();
    }
}
