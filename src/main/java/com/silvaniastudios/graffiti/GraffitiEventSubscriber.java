package com.silvaniastudios.graffiti;

import java.awt.Color;

import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.items.BasicPenItem;
import com.silvaniastudios.graffiti.items.CanvasEditorItem;
import com.silvaniastudios.graffiti.items.EraserItem;
import com.silvaniastudios.graffiti.items.MagicPenItem;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(modid = Graffiti.MODID, bus = EventBusSubscriber.Bus.MOD)
public class GraffitiEventSubscriber {
	
	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x000000)), "basic_pen_black"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x0000AA)), "basic_pen_dark_blue"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x00AA00)), "basic_pen_dark_green"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x00AAAA)), "basic_pen_dark_aqua"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xAA0000)), "basic_pen_dark_red"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xAA00AA)), "basic_pen_dark_purple"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xFFAA00)), "basic_pen_gold"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xAAAAAA)), "basic_pen_gray"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x555555)), "basic_pen_dark_gray"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x5555FF)), "basic_pen_blue"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x55FF55)), "basic_pen_green"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x55FFFF)), "basic_pen_aqua"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xFF5555)), "basic_pen_red"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xFF55FF)), "basic_pen_light_purple"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xFFFF55)), "basic_pen_yellow"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xFFFFFF)), "basic_pen_white"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xFF6A00)), "basic_pen_orange"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0xFF7F7F)), "basic_pen_pink"),
				setup(new BasicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1), new Color(0x7F4E00)), "basic_pen_brown"),
				setup(new MagicPenItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1)), "magic_pen"),
				setup(new EraserItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1)), "eraser"),
				setup(new CanvasEditorItem(new Item.Properties().group(GraffitiItemGroups.GRAFFITI_ITEM_GROUP).maxStackSize(1)), "canvas_editor")
			);
	}
	
	@SubscribeEvent
	public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
				setup(new GraffitiBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F).notSolid().doesNotBlockMovement()), "graffiti")
			);
	}
	
	@SubscribeEvent
	public static void onTERegistry(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(
				setup(TileEntityType.Builder.create(TileEntityGraffiti::new, GraffitiBlocks.GRAFFITI).build(null), "graffiti")
			);
	}
	
	public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
		return setup(entry, new ResourceLocation(Graffiti.MODID, name));
	}
	
	public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
		entry.setRegistryName(registryName);
		return entry;
	}
}
