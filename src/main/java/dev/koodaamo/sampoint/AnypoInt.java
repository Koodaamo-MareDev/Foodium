package dev.koodaamo.sampoint;

import java.util.function.Consumer;

public class AnypoInt extends SampoInt {
	
	private int cacheValue;

	public AnypoInt() {
		super();
	}

	public AnypoInt(Consumer<Integer> listener) {
		super(listener);
	}
	
	public AnypoInt(Runnable listener) {
		super(listener);
	}
	
	@Override
	public int set(int newValue) {
		boolean fireEvent = (cacheValue != newValue);
		super.set(newValue);
		
		if(fireEvent) {
			fire();
			return newValue;
		}
		return cacheValue = get();
	}
	
}
