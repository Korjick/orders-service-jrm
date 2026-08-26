package ru.itlab.ordersservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import ru.itlab.ordersservice.storage.converters.MenuLineItemCollectionReadConverter;
import ru.itlab.ordersservice.storage.converters.MenuLineItemCollectionWriteConverter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().build();
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ObjectMapper objectMapper) {
        List<Converter<?,?>> converters = List.of(
                new MenuLineItemCollectionReadConverter(objectMapper),
                new MenuLineItemCollectionWriteConverter(objectMapper)
        );
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
    }
}
