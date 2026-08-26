package ru.itlab.ordersservice.storage.converters;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.http.HttpStatus;
import ru.itlab.ordersservice.exception.OrderServiceException;
import ru.itlab.ordersservice.storage.model.MenuLineItem;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@ReadingConverter
@RequiredArgsConstructor
public class MenuLineItemCollectionReadConverter implements Converter<Json, List<MenuLineItem>> {

    private final ObjectMapper objectMapper;

    @Override
    public List<MenuLineItem> convert(Json value) {
        try {
            return objectMapper.readValue(value.asArray(), new TypeReference<List<MenuLineItem>>() {});
        } catch (JacksonException e) {
            var msg = String.format("Failed to convert JSON %s to MenuLineItemCollection", value.asString());
            throw new OrderServiceException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
