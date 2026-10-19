package ru.itlab.ordersservice.kafka;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.Network;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.itlab.ordersservice.BaseDBDependencyTest;
import ru.itlab.ordersservice.SchemaRegistryContainer;
import ru.itlab.ordersservice.TestWebClientConfig;
import ru.itlab.ordersservice.storage.model.MenuOrder;
import ru.itlab.ordersservice.storage.model.OrderStatus;
import ru.itlab.ordersservice.storage.repositories.MenuOrderRepository;
import ru.itlab.OrderDispatchStatus;
import ru.itlab.OrderDispatchedEvent;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
@Import(TestWebClientConfig.class)
@SpringBootTest
class KafkaOrderDispatchListenerTest extends BaseDBDependencyTest {

    public static final String CONFLUENT_VERSION = "7.5.2";

    private static final String ORDER_DISPATCH_TOPIC = "v1.orders_dispatch";

    private static final Network NETWORK = Network.newNetwork();

    public static final ConfluentKafkaContainer KAFKA = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.2"))
            .withNetwork(NETWORK);

    public static final SchemaRegistryContainer SCHEMA_REGISTRY =
            new SchemaRegistryContainer(CONFLUENT_VERSION).withKafka(KAFKA);

    @BeforeAll
    static void setup() {
        KAFKA.start();
        SCHEMA_REGISTRY.start();

        System.setProperty("spring.kafka.bootstrap-servers", KAFKA.getBootstrapServers());
        System.setProperty("spring.kafka.consumer.properties.schema.registry.url", "http://localhost:" + SCHEMA_REGISTRY.getFirstMappedPort());
        System.setProperty("spring.kafka.producer.key-serializer", "org.apache.kafka.common.serialization.StringSerializer");
        System.setProperty("spring.kafka.producer.value-serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        System.setProperty("spring.kafka.producer.properties.schema.registry.url", "http://localhost:" + SCHEMA_REGISTRY.getFirstMappedPort());
    }

    @Autowired
    private KafkaTemplate<String, OrderDispatchedEvent> kafkaTemplate;

    @Autowired
    private MenuOrderRepository menuOrderRepository;

    @Test
    void consumeOrderDispatchEvent_consumesEventAndUpdatesMenuOrderStatus() throws Exception {
        MenuOrder order = menuOrderRepository.findAll().blockFirst();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);

        OrderDispatchedEvent event = OrderDispatchedEvent.newBuilder()
                .setOrderId(order.getId())
                .setStatus(OrderDispatchStatus.ACCEPTED)
                .build();
        kafkaTemplate.send(ORDER_DISPATCH_TOPIC, String.valueOf(order.getId()), event).get();

        Awaitility
                .await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    MenuOrder updated = menuOrderRepository.findById(order.getId()).block();
                    assertThat(updated.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
                });
    }
}
