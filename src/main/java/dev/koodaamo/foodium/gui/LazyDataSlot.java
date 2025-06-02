package dev.koodaamo.foodium.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LazyDataSlot extends SimpleDataSlot {

	boolean cacheFlag = false;

	public LazyDataSlot(Consumer<Integer> setFunc, Supplier<Integer> getFunc) {
		super(setFunc, getFunc);
	}

	public void invalidate() {
		cacheFlag = true;
	}

	@Override
	public boolean checkAndClearUpdateFlag() {
		boolean update = cacheFlag;
		cacheFlag = false;
		return update;
	}
	
	public static LazyDataSlot standalone() {
		return new LazyDataSlot(null, null) {
			int value;
			
			@Override
			public int get() {
				return value;
			}
			
			@Override
			public void set(int i) {
				value = i;
			}
		};
	}
}
