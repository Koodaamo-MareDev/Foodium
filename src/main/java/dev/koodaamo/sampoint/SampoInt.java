package dev.koodaamo.sampoint;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SampoInt {

	private int value = 0;

	private ArrayList<Consumer<Integer>> listeners = new ArrayList<>();
	
	public SampoInt() {
		
	}

	public SampoInt(Consumer<Integer> listener) {
		addListener(listener);
	}

	public SampoInt(Runnable listener) {
		addListener((i) -> {
			listener.run();
		});
	}

	public int set(int newValue) {
		return value = newValue;
	}

	public int get() {
		return value;
	}

	public void addListener(Consumer<Integer> listener) {
		listeners.add(listener);
	}

	public void fire() {
		for (Consumer<Integer> consumer : listeners) {
			if (consumer == null)
				continue;
			consumer.accept(value);
		}
	}
}
