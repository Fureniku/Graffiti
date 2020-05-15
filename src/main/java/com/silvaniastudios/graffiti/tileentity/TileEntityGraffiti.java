package com.silvaniastudios.graffiti.tileentity;

import java.util.ArrayList;

import com.silvaniastudios.graffiti.drawables.GraffitiTextDrawable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class TileEntityGraffiti extends TileEntity {
	
	public int[][] pixelArray = new int[16][16];
	public ArrayList<GraffitiTextDrawable> textList = new ArrayList<GraffitiTextDrawable>();

	public TileEntityGraffiti() {
		super(GraffitiTileEntityTypes.GRAFFITI);
	}
	
	public void writeText(GraffitiTextDrawable text) {
		textList.add(text);
		this.markDirty();
		sendUpdates();
	}
	
	public void setPixel(int x, int y, int col) {
		y = Math.abs(y-16);
		System.out.println("setting pixel " + x + ", " + y);
		pixelArray[x][y] = col;
		this.markDirty();
		sendUpdates();
	}
	
	public void erasePixel(int x, int y) {
		setPixel(x, y, 0);
	}
	
	public int getPixelRGB(int x, int y) {
		return pixelArray[x][y];
	}
	
	//Never naturally used anymore, but just kept in case I implement debugging later.
	public void debugArray() {
		for (int i = 0; i < 16; i++) {
			String row = "";
			for (int j = 0; j < 16; j++) {
				if (pixelArray[j][i] != 0) {
					row = row + "X";
				} else {
					row = row + " ";
				}
			}
			System.out.println(row);
		}
	}
	
	public CompoundNBT write(CompoundNBT compound) {
		for (int i = 0; i < 16; i++) {
			compound.putIntArray("row_"+i, pixelArray[i]);
		}
		GraffitiTextDrawable.serializeNBT(compound, textList);
		return super.write(compound);
	}
	
	public void read(CompoundNBT compound) {
		for (int i = 0; i < 16; i++) {
			if (compound.contains("row_"+i)) {
				pixelArray[i] = compound.getIntArray("row_"+i);
			}
		}
		textList = GraffitiTextDrawable.deserializeNBT(compound);
		super.read(compound);
	}
	
	public int getVoxel(double coordinate, Direction d) {
		//North/east is slightly different so negate 1 to "calibrate"
		if (d == Direction.NORTH || d == Direction.EAST) {
			return (int) Math.ceil(coordinate*16)-1;
		}
		return (int) Math.ceil(coordinate*16);
	}

	public BlockState getState() { 
		return world.getBlockState(pos);
	}
	
	@SuppressWarnings("deprecation")
	public boolean isLoaded() {
		return this.hasWorld() && this.hasPosition() ? this.getWorld().isBlockLoaded(this.getPos()) : false;
	}
	
	public boolean hasPosition() {
		return this.pos != null && this.pos != BlockPos.ZERO;
	}
	
	public void sendUpdates() {
		this.markDirty();
		
		if (this.isLoaded()) {
			final BlockState state = this.getState();
			this.getWorld().notifyBlockUpdate(this.pos, state, state, 3);
		}
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 0, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		this.read(pkt.getNbtCompound());
		this.getWorld().notifyBlockUpdate(this.pos, this.getState(), this.getState(), 3);
	}
}
