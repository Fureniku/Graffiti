package com.silvaniastudios.graffiti.util;

import java.awt.Color;

public enum EnumColours {
	
	BLACK(0, 0x000000, "Black"),
	DARK_BLUE(170, 0x0000AA, "Dark Blue"),
	DARK_GREEN(43520, 0x00AA00, "Dark Green"),
	DARK_AQUA(43690, 0x00AAAA, "Dark Aqua"),
	DARK_RED(11141120, 0xAA0000, "Dark Red"),
	DARK_PURPLE(11141290, 0xAA00AA, "Dark Purple"),
	GOLD(16755200, 0xFFAA00, "Gold"),
	GRAY(11184810, 0xAAAAAA, "Gray"),
	DARK_GRAY(5592405, 0x555555, "Dark Gray"),
	BLUE(5592575, 0x5555FF, "Blue"),
	GREEN(5635925, 0x55FF55, "Green"),
	AQUA(5636095, 0x55FFFF, "Aqua"),
	RED(16733525, 0xFF5555, "Red"),
	LIGHT_PURPLE(16733695, 0xFF55FF, "Light Purple"),
	YELLOW(16777045, 0xFFFF55, "Yellow"),
	WHITE(16777215, 0xFFFFFF, "White"), 
	ORANGE(16738816, 0xFF6A00, "Orange"),
	PINK(16744319, 0xFF7F7F, "Pink"),
	BROWN(8343040, 0x7F4E00, "Brown"),
	
	PASTEL_BLUE(10724351, 0xA3A3FF, "Pastel Blue"),
	BLOOD_RED(8519680, 0x820000, "Blood Red"),
	DEEP_PURPLE(7798903, 0x770077, "Deep Purple"),
	BABY_PINK(16505830, 0xFBDBE6, "Baby Pink"),
	MINT_GREEN(11075496, 0xA8FFA8, "Mint Green"),
	LEAF_GREEN(26624, 0x006800, "Leaf Green"),
	ROYAL_BLUE(9983, 0x0026FF, "Royal Blue"),
	ICE_BLUE(13434879, 0xCCFFFF, "Ice Blue"),
	SKY_BLUE(38143, 0x0094FF, "Sky Blue");
	
	
	public final int val;
	public final int hex;
	public final String name;
	
	private EnumColours(int val, int hex, String name) {
		this.val = val;
		this.hex = hex;
		this.name = name;
	}
	
	public Color getCol() {
		return new Color(this.val);
	}
	
	//For some reason Minecraft *hates* the actual ints when drawing raw colours, so we need to do this.
	public int getMCCol() {
		return this.val - 16777216;
	}
}
