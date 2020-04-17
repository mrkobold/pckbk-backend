package server.serialization;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Serializator {

    public static String serialize(Object o) {
        if (o == null) {
            return "";
        }
        if (o.getClass().isPrimitive() ||
                o instanceof Byte ||
                o instanceof Short ||
                o instanceof Character ||
                o instanceof Integer ||
                o instanceof Long ||
                o instanceof Float ||
                o instanceof Double ||
                o instanceof Boolean ||
                o instanceof String) {
            return o.toString();
        }
        if (o instanceof Collection) {
            return serialize((Collection) o);
        }
        if (o.getClass().isArray()) {
            return serialize((Object[]) o);
        }
        return serializeObject(o);
    }

    private static String serializeObject(Object o) {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
        }

        return "{" +
                Arrays.stream(fields)
                        .map(getFieldStringFunction(o))
                        .collect(Collectors.joining(", ")) +
                "}";
    }

    private static Function<Field, String> getFieldStringFunction(Object o) {
        return field -> {
            String serializedField = null;
            try {
                Object o1 = field.get(o);
                serializedField = serialize(o1);
            } catch (IllegalAccessException ignore) {
            }
            return "\"" + field.getName() + "\": " + "\"" + serializedField + "\"";
        };
    }

    private static <T> String serialize(T[] array) {
        return "[" +
                Arrays.stream(array)
                        .map(Serializator::serialize)
                        .collect(Collectors.joining(", ")) +
                "]";
    }

    private static String serialize(Collection<?> collection) {
        return "[" +
                collection.stream()
                        .map(Serializator::serialize)
                        .collect(Collectors.joining(", ")) +
                "]";
    }
}
