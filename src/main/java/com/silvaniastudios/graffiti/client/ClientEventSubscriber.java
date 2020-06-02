package com.silvaniastudios.graffiti.client;

import com.silvaniastudios.graffiti.Graffiti;
import com.silvaniastudios.graffiti.client.gui.GuiCanvasEditorMain;
import com.silvaniastudios.graffiti.tileentity.GraffitiContainerTypes;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Graffiti.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscriber {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(GraffitiContainerTypes.GRAFFITI.get(), GuiCanvasEditorMain::new);
	}
}
