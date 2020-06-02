package com.silvaniastudios.graffiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.silvaniastudios.graffiti.client.GraffitiRenderer;
import com.silvaniastudios.graffiti.network.GraffitiPacketHandler;
import com.silvaniastudios.graffiti.tileentity.GraffitiContainerTypes;
import com.silvaniastudios.graffiti.tileentity.GraffitiTileEntityTypes;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Graffiti.MODID)
public class Graffiti
{
	
	public static final String MODID = "graffiti";
	public static final String VERSION = "GRADLE_VERSION";
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public Graffiti() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		// Register the setup method for modloading
		modEventBus.addListener(this::setup);
		// Register the doClientStuff method for modloading
		modEventBus.addListener(this::doClientStuff);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		
		GraffitiContainerTypes.CONTAINER_TYPES.register(modEventBus);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		GraffitiPacketHandler.registerPackets();
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
		ClientRegistry.bindTileEntityRenderer(GraffitiTileEntityTypes.GRAFFITI, GraffitiRenderer::new);
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		// do something when the server starts
		LOGGER.info("HELLO from server starting");
	}
}
