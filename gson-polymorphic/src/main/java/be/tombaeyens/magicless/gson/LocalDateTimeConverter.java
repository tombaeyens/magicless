package be.tombaeyens.magicless.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/** Usage while building a Gson object
 *
 * Gson gson = new GsonBuilder()
 *   ...
 *   .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeConverter())
 *   ...
 *   .create()
 */
public class LocalDateTimeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @Override
  public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(FORMATTER.format(src));
  }

  @Override
  public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {
    return FORMATTER.parse(json.getAsString(), LocalDateTime::from);
  }
}
