package dev.koodaamo.foodium.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.inventory.DataSlot;

public class SimpleDataSlot extends DataSlot {

	public Consumer<Integer> setFunc;
	public Supplier<Integer> getFunc;
	
	public SimpleDataSlot(Consumer<Integer> setFunc, Supplier<Integer> getFunc) {
		this.setFunc = setFunc;
		this.getFunc = getFunc;
	}
	
	@Override
	public int get() {
		return getFunc.get();
	}

	@Override
	public void set(int i) {
		setFunc.accept(i);
	}

}
