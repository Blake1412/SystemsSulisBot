import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

interface DatabaseEntity {
    default String getTitle() {
        return get("title");
    }

    default String getModule() {
        return get("module");
    }

    default String getLink() {
        return get("link");
    }

    default String getName() {
        return this.getClass().getName();
    }

    default Map<String, String> getKeyValues() {
        Map<String, String> keyValues = new LinkedHashMap<>();
        Arrays.stream(this.getClass().getDeclaredFields())
              .forEach(field -> {
                  try {
                      field.setAccessible(true);
                      keyValues.put(field.getName(), field.get(this).toString());
                  } catch (IllegalAccessException e) {
                      throw new RuntimeException(e);
                  }
              });
        return keyValues;
    }

    private String get(String field) {
        try {
            return this.getClass().getField(field).get(this).toString();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
