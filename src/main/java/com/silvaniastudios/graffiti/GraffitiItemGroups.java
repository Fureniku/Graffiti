package com.silvaniastudios.graffiti;

import java.util.function.Supplier;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class GraffitiItemGroups {
	
	public static final ItemGroup GRAFFITI_ITEM_GROUP = new GraffitiItemGroup(Graffiti.MODID, () -> new ItemStack(GraffitiItems.BASIC_PEN_DARK_RED));

	public static class GraffitiItemGroup extends ItemGroup {
	
		private final Supplier<ItemStack> supp;
		
		public GraffitiItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
			super(name);
			this.supp = iconSupplier;
		}
		
		@Override
		public ItemStack createIcon() {
			return supp.get();
		}
	}

}
