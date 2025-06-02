package dev.koodaamo.foodium.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LazyDataSlot extends SimpleDataSlot {

	private int value;

	boolean cacheFlag = false;

	public LazyDataSlot(Consumer<Integer> setFunc, Supplier<Integer> getFunc) {
		super(setFunc, getFunc);
	}

	public void invalidate() {
		cacheFlag = true;
	}

	@Override
	public int get() {
		return this.value;
	}

	@Override
	public void set(int value) {
		this.value = value;
	}

	@Override
	public boolean checkAndClearUpdateFlag() {
		boolean update = cacheFlag;
		cacheFlag = false;
		return update;
	}
}
