package dev.koodaamo.propertygen;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonAccessor {
	private final JsonObject root;

	public JsonAccessor(JsonObject root) {
		this.root = root;
	}

	public JsonAccessor withFloat(String path, Consumer<Float> consumer) {
		getPrimitive(path).ifPresent(p -> {
			if (p.isNumber())
				consumer.accept(p.getAsFloat());
		});
		return this;
	}

	public JsonAccessor withInt(String path, Consumer<Integer> consumer) {
		getPrimitive(path).ifPresent(p -> {
			if (p.isNumber())
				consumer.accept(p.getAsInt());
		});
		return this;
	}

	public JsonAccessor withBoolean(String path, Consumer<Boolean> consumer) {
		getPrimitive(path).ifPresent(p -> {
			if (p.isBoolean())
				consumer.accept(p.getAsBoolean());
		});
		return this;
	}

	public JsonAccessor withBoolean(String path, Runnable runnable) {
		getPrimitive(path).ifPresent(p -> {
			if (p.isBoolean() && p.getAsBoolean())
				runnable.run();
		});
		return this;
	}

	public JsonAccessor withString(String path, Consumer<String> consumer) {
		getPrimitive(path).ifPresent(p -> {
			if (p.isString())
				consumer.accept(p.getAsString());
		});
		return this;
	}

	public JsonAccessor withObject(String path, Consumer<JsonObject> consumer) {
		getElement(path).ifPresent(el -> {
			if (el.isJsonObject())
				consumer.accept(el.getAsJsonObject());
		});
		return this;
	}
	
	public JsonAccessor withAccessor(String path, Consumer<JsonAccessor> consumer) {
		getElement(path).ifPresent(el -> {
			if (el.isJsonObject())
				consumer.accept(new JsonAccessor(el.getAsJsonObject()));
		});
		return this;
	}

	public JsonAccessor withArray(String path, Consumer<JsonArray> consumer) {
		getElement(path).ifPresent(el -> {
			if (el.isJsonArray())
				consumer.accept(el.getAsJsonArray());
		});
		return this;
	}

	// --- Helpers ---

	private Optional<JsonElement> getElement(String path) {
		String[] keys = path.split(Pattern.quote("."));
		JsonElement current = root;
		for (String key : keys) {
			if (!(current instanceof JsonObject obj) || !obj.has(key))
				return Optional.empty();
			current = obj.get(key);
		}
		return Optional.ofNullable(current);
	}

	private Optional<JsonPrimitive> getPrimitive(String path) {
		return getElement(path).filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive);
	}
}
