package dev.koodaamo.propertygen;

import net.minecraftforge.eventbus.api.IEventBus;

public interface IPropertyGenerator {

	public IPropertyGenerator registerAll(String location);

	public IPropertyGenerator registerOne(String location);
	
	public void register(IEventBus modEventBus);
}
