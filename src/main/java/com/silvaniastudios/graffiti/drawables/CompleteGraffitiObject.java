package com.silvaniastudios.graffiti.drawables;

import java.util.ArrayList;

import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class CompleteGraffitiObject {
	
	String clickUrl = "";
	ItemStack backdropBlock;
	double alignmentOffset = 0.015625; // 1/64, to avoid z-fighting
	int rightClickActionId;
	int backgroundTransparency = 200;
	Direction side;
	
	boolean offsetIntoBlock = true;
	
	public PixelGridDrawable pixelGrid;
	public ArrayList<TextDrawable> textList = new ArrayList<TextDrawable>();
	
	public CompleteGraffitiObject(Direction side, PixelGridDrawable grid, ArrayList<TextDrawable> text, ItemStack backdropBlock, int rightClickActionId,
			int backgroundTransparency,	String clickUrl, double alignmentOffset, boolean offsetIntoBlock) {
		this.pixelGrid = grid;
		this.textList = text;
		this.backdropBlock = backdropBlock;
		this.rightClickActionId = rightClickActionId;
		this.backgroundTransparency = backgroundTransparency;
		this.clickUrl = clickUrl;
		this.alignmentOffset = alignmentOffset;
		this.offsetIntoBlock = offsetIntoBlock;
		this.side = side;
	}
	
	public CompleteGraffitiObject(Direction side, PixelGridDrawable grid, ArrayList<TextDrawable> text) {
		this(side, grid, text, null, 0, 200, "", 0.015625, true);
	}
	
	public boolean hasPixelGrid() {
		return pixelGrid != null && pixelGrid.size > 0;
	}
	
	public double getAlignment() {
		return alignmentOffset;
	}
	
	public boolean isOffsetIntoBlock() {
		return offsetIntoBlock;
	}
	
	public int getRightClickAction() {
		return rightClickActionId;
	}
	
	public void setRightClickAction(int id) {
		this.rightClickActionId = id;
	}
	
	public String getUrl() {
		return this.clickUrl;
	}
	
	public void setUrl(String url) {
		this.clickUrl = url;
	}
	
	public Direction getSide() {
		return side;
	}
	
	public void setBackgroundTransparency(int transparency) {
		this.backgroundTransparency = transparency;
	}
	
	public int getBackgroundTransparency() {
		return backgroundTransparency;
	}
	
	public void setAlignment(boolean offset, double offsetAmt, TileEntityGraffiti tile) {
		if (!tile.isLocked()) {
			alignmentOffset = offsetAmt;
			offsetIntoBlock = offset;
		}
	}
	
	public boolean writeText(TextDrawable text, TileEntityGraffiti tile) {
		if (!tile.isLocked()) {
			textList.add(text);
			return true;
		}
		return false;
	}

	public static void serializeNBT(CompoundNBT tag, CompleteGraffitiObject graffiti, String dirId) {
		System.out.println("serializing NBT on " + dirId);
		if (graffiti.textList != null) { System.out.println("text is not null"); } else { System.out.println("text is null"); }
		CompoundNBT nbt = new CompoundNBT();
		if (graffiti.pixelGrid != null) { PixelGridDrawable.serializeNBT(nbt, graffiti.pixelGrid); }
		if (graffiti.textList != null) { TextDrawable.serializeNBT(nbt, graffiti.textList); }
		//if (getBackdropBlock() != null) nbt.put("item", getBackdropBlock().serializeNBT());
		nbt.putInt("side", graffiti.side.getIndex());
		
		nbt.putDouble("alignmentOffset", graffiti.alignmentOffset);
		
		nbt.putBoolean("offsetIntoBlock", graffiti.offsetIntoBlock);
		nbt.putInt("rightClickActionID", graffiti.rightClickActionId);
		nbt.putInt("backgroundTransparency", graffiti.backgroundTransparency);
		nbt.putString("url", graffiti.clickUrl);
		
		tag.put("graffiti" + dirId, nbt);
	}
	
	public static CompleteGraffitiObject deserializeNBT(CompoundNBT compound, String dirId) {
		CompoundNBT nbt = compound.getCompound("graffiti" + dirId);
		PixelGridDrawable pixelGrid = PixelGridDrawable.deserializeNBT(nbt);
		ArrayList<TextDrawable> textList = TextDrawable.deserializeNBT(nbt);
		int side = nbt.getInt("side");
		
		ItemStack backdropBlock = null;
		int rightClickActionId = 0;
		int backgroundTransparency = 200;
		String clickUrl = "";
		double alignmentOffset = 0.015625;
		boolean offsetIntoBlock = true;

		if (nbt.contains("item")) backdropBlock = ItemStack.read(nbt.getCompound("item"));
		if (nbt.contains("rightClickActionID")) { rightClickActionId = nbt.getInt("rightClickActionID"); }
		if (nbt.contains("backgroundTransparency")) { backgroundTransparency = nbt.getInt("backgroundTransparency"); }
		if (nbt.contains("url")) { clickUrl = nbt.getString("url"); }
		if (nbt.contains("alignmentOffset")) { alignmentOffset = nbt.getDouble("alignmentOffset"); }
		if (nbt.contains("offsetIntoBlock")) { offsetIntoBlock = nbt.getBoolean("offsetIntoBlock"); }
		
		return new CompleteGraffitiObject(Direction.byIndex(side), pixelGrid, textList, backdropBlock, rightClickActionId, backgroundTransparency, clickUrl, alignmentOffset, offsetIntoBlock);
	}
}
