package com.silvaniastudios.graffiti.items;

import java.util.List;

import javax.annotation.Nullable;

import com.silvaniastudios.graffiti.client.gui.GuiColourPicker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicPenItem extends BasicPenItem {
	
	int[] customColours = new int[21];
	
	public MagicPenItem(Properties properties) {
		super(properties, null);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (handIn == Hand.MAIN_HAND) {
			BlockRayTraceResult result = rayTrace(worldIn, playerIn);
			
			if (playerIn.isCrouching() && result.getType().equals(RayTraceResult.Type.MISS)) {
				if (worldIn.isRemote) {
					processClientRightClick(playerIn.getHeldItem(handIn));
				}
			}
		}
		return ActionResult.resultPass(playerIn.getHeldItem(handIn));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void processClientRightClick(ItemStack item) {
		Minecraft mc = Minecraft.getInstance();
		mc.displayGuiScreen(new GuiColourPicker(item));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("Allows writing and drawing on the walls"));
		tooltip.add(new StringTextComponent("Supports full RGB colour options!"));
		tooltip.add(new StringTextComponent("Sneak-rightclick in open air to open colour picker"));
		tooltip.add(new StringTextComponent(""));
		if (stack.hasTag()) {
			tooltip.add(new StringTextComponent("Current mode: " + (stack.getTag().getBoolean("writing") ? "Writing" : "Drawing")));
		}
		tooltip.add(new StringTextComponent("Color: " + getHexColour(getColor(stack))));
		
	}
	
	public int[] getCustomColours(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		
		if (nbt.contains("customColours")) {
			return nbt.getIntArray("customColours");
		}
		return new int[21];
	}
	
	public void setCustomColour(ItemStack stack, int col, int slot) {
		CompoundNBT nbt = stack.getOrCreateTag();
		
		int[] colours = new int[21];
		
		if (nbt.contains("customColours")) {
			colours = nbt.getIntArray("customColours");
		}
		
		if (slot < colours.length) {
			colours[slot] = col;
		}
		
		nbt.putIntArray("customColours", colours);
	}
	
	public void setPenColour(ItemStack stack, int col) {
		CompoundNBT nbt = stack.getOrCreateTag();

		nbt.putInt("colour", col);
	}
}
