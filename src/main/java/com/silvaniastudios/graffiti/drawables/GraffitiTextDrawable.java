package com.silvaniastudios.graffiti.drawables;

import java.util.ArrayList;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class GraffitiTextDrawable {
	
	private String text;
	private int colour;
	private int xPos;
	private int yPos;
	private int scale;
	
	public GraffitiTextDrawable(String txt, int x, int y, int col, int scale) {
		this.text = txt;
		this.colour = col;
		this.xPos = x;
		this.yPos = y;
		this.scale = scale;
	}
	
	public String getText() {
		return text;
	}
	
	public int getCol() {
		return colour;
	}
	
	public int xPos() {
		return xPos;
	}
	
	public int yPos() {
		return yPos;
	}
	
	public int scale() {
		return scale;
	}

	public static CompoundNBT serializeNBT(CompoundNBT nbt, ArrayList<GraffitiTextDrawable> textList) {
		ListNBT listnbt = new ListNBT();
		
		for (int i = 0; i < textList.size(); i++) {
			GraffitiTextDrawable t = textList.get(i);
			CompoundNBT textnbt = new CompoundNBT();
			textnbt.putString("text", t.text);
			textnbt.putInt("colour", t.colour);
			textnbt.putInt("xPos", t.xPos);
			textnbt.putInt("yPos", t.yPos);
			textnbt.putInt("scale", t.scale);
			
			listnbt.add(textnbt);
		}
		
		nbt.put("text_objects", listnbt);
		return nbt;
	}
	
	public static ArrayList<GraffitiTextDrawable> deserializeNBT(CompoundNBT nbt) {
		ArrayList<GraffitiTextDrawable> textList = new ArrayList<GraffitiTextDrawable>();
		
		if (nbt.contains("text_objects")) {
			ListNBT list = nbt.getList("text_objects", 10);
			
			for (int i = 0; i < list.size(); i++) {
				CompoundNBT textnbt = list.getCompound(i);
				String text = textnbt.getString("text");
				int colour = textnbt.getInt("colour");
				int xPos = textnbt.getInt("xPos");
				int yPos = textnbt.getInt("yPos");
				int scale = textnbt.getInt("scale");
				
				textList.add(new GraffitiTextDrawable(text, xPos, yPos, colour, scale));
			}
		}
		return textList;
	}
}
