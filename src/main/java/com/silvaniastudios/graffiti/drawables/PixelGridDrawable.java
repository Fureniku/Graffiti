package com.silvaniastudios.graffiti.drawables;

import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.nbt.CompoundNBT;

public class PixelGridDrawable extends DrawableBase {
	
	int size;
	int[][] pixelArray;
	int positionId = 0;
	double positionOffset = 0;
	int transparency = 255;
	
	public PixelGridDrawable(int size) {
		this.size = size;
		this.pixelArray = new int[size][size];
	}
	
	public PixelGridDrawable(int[][] grid) {
		this.size = grid.length;
		this.pixelArray = grid;
	}

	public int getSize() {
		return size;
	}
	
	public int[][] getPixelGrid() {
		return pixelArray;
	}
	
	public void setNewGrid(int size, int[][] grid) {
		this.size = size;
		this.pixelArray = grid;
	}
	
	public int getTransparency() {
		return transparency;
	}
	
	public void setTransparency(int t) {
		this.transparency = t;
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
	
	public boolean setPixel(int x, int y, int col, TileEntityGraffiti te) {
		y = Math.abs(y-size);
		return setPixelRaw(x, y, col, te);
	}
	
	public boolean setPixelRaw(int x, int y, int col, TileEntityGraffiti te) {
		if (!te.isLocked()) {
			if (pixelArray.length != 0) {
				pixelArray[x][y] = col;
			}
			return true;
		}
		return false;
	}
	
	public boolean erasePixel(int x, int y, TileEntityGraffiti te) {
		return setPixel(x, y, 0, te);
	}
	
	public int getPixelRGB(int x, int y) {
		return pixelArray[x][y];
	}
	
	public static void serializeNBT(CompoundNBT nbt, PixelGridDrawable pixels) {
		if (pixels != null) {
			CompoundNBT gridnbt = new CompoundNBT();
			pixels.size = pixels.getPixelGrid().length;
			gridnbt.putInt("size", pixels.size);
			gridnbt.putInt("transparency", pixels.transparency);
			
			for (int i = 0; i < pixels.size; i++) {
				gridnbt.putIntArray("row_"+i, pixels.pixelArray[i]);
			}
			
			nbt.put("pixel_grid", gridnbt);
		} else {
			//if grids have been removed, we need to clear them from the NBT
			nbt.put("pixel_grid", new CompoundNBT());
		}
	}
	
	public static PixelGridDrawable deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains("pixel_grid")) {
			CompoundNBT gridnbt = nbt.getCompound("pixel_grid");
			int size = gridnbt.getInt("size");
			int transparency = 255;
			if (gridnbt.contains("transparency")) { transparency = gridnbt.getInt("transparency"); }
			
			int[][] arr = new int[size][size];
			
			for (int i = 0; i < size; i++) {
				if (gridnbt.contains("row_"+i)) {
					arr[i] = gridnbt.getIntArray("row_"+i);
				}
			}

			PixelGridDrawable drawable = new PixelGridDrawable(size);
			drawable.pixelArray = arr;
			drawable.setTransparency(transparency);
			return drawable;
		}
		return new PixelGridDrawable(16);
	}
}
