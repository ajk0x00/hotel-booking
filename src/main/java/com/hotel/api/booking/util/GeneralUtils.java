package com.hotel.api.booking.util;

import java.lang.reflect.Field;
import java.util.Arrays;

public class GeneralUtils {

    private static final Logger logger = new Logger("com.hotel.api.booking.util.GeneralUtils");

    public static <T, R> void map(T source, R target) {
        Arrays.stream(source.getClass().getDeclaredFields())
                .forEach(field -> mapField(source, target, field)
                );
    }

    public static <T, R> void map(T source, R target, boolean shouldMapId) {
        if (shouldMapId)
            map(source, target);
        else
            Arrays.stream(source.getClass().getDeclaredFields()).forEach(field -> {
                if (!field.getName().equals("id"))
                    mapField(source, target, field);
            });
    }

    private static <T, R> void mapField(T source, R target, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(source);
            Field targetField = target.getClass().getDeclaredField(field.getName());
            targetField.setAccessible(true);
            targetField.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException exception) {
            logger.logException(exception);
        }
    }
}
