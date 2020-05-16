package com.silvaniastudios.graffiti.drawables;

import net.minecraft.nbt.CompoundNBT;

public class PixelGridDrawable {
	
	int size;
	int[][] pixelArray;
	
	public PixelGridDrawable(int size) {
		this.size = size;
		this.pixelArray = new int[size][size];
	}

	public int getSize() {
		return size;
	}
	
	//Never naturally used, but this way I can easily view a grid in the console if I need to.
	public void debugArray() {
		for (int i = 0; i < size; i++) {
			String row = "";
			for (int j = 0; j < size; j++) {
				if (pixelArray[j][i] != 0) {
					row = row + "X";
				} else {
					row = row + " ";
				}
			}
			System.out.println(row);
		}
	}
	
	public boolean setPixel(int x, int y, int col) {
		y = Math.abs(y-size);
		pixelArray[x][y] = col;

		return true;
	}
	
	public boolean erasePixel(int x, int y) {
		return setPixel(x, y, 0);
	}
	
	public int getPixelRGB(int x, int y) {
		return pixelArray[x][y];
	}
	
	public static void serializeNBT(CompoundNBT nbt, PixelGridDrawable pixels) {
		if (pixels != null) {
			CompoundNBT gridnbt = new CompoundNBT();
			gridnbt.putInt("size", pixels.size);
			
			for (int i = 0; i < pixels.size; i++) {
				gridnbt.putIntArray("row_"+i, pixels.pixelArray[i]);
			}
			
			nbt.put("pixel_grid", gridnbt);
		}
	}
	
	public static PixelGridDrawable deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains("pixel_grid")) {
			CompoundNBT gridnbt = nbt.getCompound("pixel_grid");
			int size = gridnbt.getInt("size");
			
			int[][] arr = new int[size][size];
			
			for (int i = 0; i < size; i++) {
				if (gridnbt.contains("row_"+i)) {
					arr[i] = gridnbt.getIntArray("row_"+i);
				}
			}

			PixelGridDrawable drawable = new PixelGridDrawable(size);
			drawable.pixelArray = arr;
			return drawable;
		}
		return new PixelGridDrawable(16);
	}
}
