package dev.koodaamo.foodium.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import dev.koodaamo.sampoint.SampoInt;

public class LazyDataSlot extends SimpleDataSlot {

	boolean cacheFlag = true;

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
	
	public static LazyDataSlot shared(SampoInt data) {
		return new LazyDataSlot(data::set, data::get);
	}
}
