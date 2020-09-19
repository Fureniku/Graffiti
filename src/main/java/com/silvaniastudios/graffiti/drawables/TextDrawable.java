package com.silvaniastudios.graffiti.drawables;

import java.util.ArrayList;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class TextDrawable extends DrawableBase {
	
	private String text;
	private int colour;
	private short xPos;
	private short yPos;
	private float scale;
	private short rotation;
	private String format;
	private short alignment; //0 = left, 1 = centre, 2 = right
	
	public TextDrawable(String txt, short x, short y, int col, float scale, short rot, String format, short alignment) {
		this.text = txt;
		this.colour = col;
		this.xPos = x;
		this.yPos = y;
		this.scale = scale;
		this.rotation = rot;
		this.format = format;
		this.alignment = alignment;
	}
	
	public String getText() {
		return text;
	}
	
	public int getCol() {
		return colour;
	}
	
	public short xPos() {
		return xPos;
	}
	
	public short yPos() {
		return yPos;
	}
	
	public float scale() {
		return scale;
	}
	
	public short getRotation() {
		return rotation;
	}
	
	public String getFormat() {
		return format;
	}
	
	public short getAlignment() {
		return alignment;
	}
	
	public String getDrawableText() {
		return format + text;
	}

	public static CompoundNBT serializeNBT(CompoundNBT nbt, ArrayList<TextDrawable> textList) {
		ListNBT listnbt = new ListNBT();
		
		System.out.println("Serializing " + textList.size() + " texts");
		
		for (int i = 0; i < textList.size(); i++) {
			TextDrawable t = textList.get(i);
			CompoundNBT textnbt = new CompoundNBT();
			textnbt.putString("text", t.text);
			textnbt.putInt("colour", t.colour);
			textnbt.putShort("xPos", t.xPos);
			textnbt.putShort("yPos", t.yPos);
			textnbt.putFloat("scale", t.scale);
			textnbt.putShort("rotation", t.rotation);
			textnbt.putString("format", t.format);
			textnbt.putShort("alignment", t.alignment);
			
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
				short xPos = textnbt.getShort("xPos");
				short yPos = textnbt.getShort("yPos");
				float scale = textnbt.getFloat("scale");
				short rot = textnbt.getShort("rotation");
				String format = textnbt.getString("format");
				short alignment = textnbt.getShort("alignment");
				textList.add(new TextDrawable(text, xPos, yPos, colour, scale, rot, format, alignment));
			}
		}
		return textList;
	}
}
