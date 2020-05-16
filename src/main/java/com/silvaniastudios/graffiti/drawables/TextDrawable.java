package com.silvaniastudios.graffiti.drawables;

import java.util.ArrayList;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class TextDrawable extends DrawableBase {
	
	private String text;
	private int colour;
	private int xPos;
	private int yPos;
	private float scale;
	private int rotation;
	
	public TextDrawable(String txt, int x, int y, int col, float scale, int rot) {
		this.text = txt;
		this.colour = col;
		this.xPos = x;
		this.yPos = y;
		this.scale = scale;
		this.rotation = rot;
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
	
	public float scale() {
		return scale;
	}
	
	public int getRotation() {
		return rotation;
	}

	public static CompoundNBT serializeNBT(CompoundNBT nbt, ArrayList<TextDrawable> textList) {
		ListNBT listnbt = new ListNBT();
		
		for (int i = 0; i < textList.size(); i++) {
			TextDrawable t = textList.get(i);
			CompoundNBT textnbt = new CompoundNBT();
			textnbt.putString("text", t.text);
			textnbt.putInt("colour", t.colour);
			textnbt.putInt("xPos", t.xPos);
			textnbt.putInt("yPos", t.yPos);
			textnbt.putFloat("scale", t.scale);
			textnbt.putInt("rotation", t.rotation);
			
			listnbt.add(textnbt);
		}
		
		nbt.put("text_objects", listnbt);
		return nbt;
	}
	
	public static ArrayList<TextDrawable> deserializeNBT(CompoundNBT nbt) {
		ArrayList<TextDrawable> textList = new ArrayList<TextDrawable>();
		
		if (nbt.contains("text_objects")) {
			ListNBT list = nbt.getList("text_objects", 10);
			
			for (int i = 0; i < list.size(); i++) {
				CompoundNBT textnbt = list.getCompound(i);
				String text = textnbt.getString("text");
				int colour = textnbt.getInt("colour");
				int xPos = textnbt.getInt("xPos");
				int yPos = textnbt.getInt("yPos");
				float scale = textnbt.getFloat("scale");
				int rot = textnbt.getInt("rotation");
				
				textList.add(new TextDrawable(text, xPos, yPos, colour, scale, rot));
			}
		}
		return textList;
	}
}
