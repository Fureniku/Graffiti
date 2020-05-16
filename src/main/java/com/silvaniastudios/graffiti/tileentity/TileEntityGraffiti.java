package com.silvaniastudios.graffiti.tileentity;

import java.util.ArrayList;

import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class TileEntityGraffiti extends TileEntity {
	
	public PixelGridDrawable pixelGrid;
	public ArrayList<TextDrawable> textList = new ArrayList<TextDrawable>();

	public TileEntityGraffiti() {
		super(GraffitiTileEntityTypes.GRAFFITI);
	}
	
	public void writeText(TextDrawable text) {
		textList.add(text);
		update();
	}
	
	public void update() {
		this.markDirty();
		sendUpdates();
	}
	
	public int getVoxel(double coordinate, Direction d, int size) {
		//North/east is slightly different so negate 1 to "calibrate"
		if (d == Direction.NORTH || d == Direction.EAST) {
			return (int) Math.ceil(coordinate*size)-1;
		}
		return (int) Math.ceil(coordinate*size);
	}
	
	public CompoundNBT write(CompoundNBT compound) {
		PixelGridDrawable.serializeNBT(compound, pixelGrid);
		TextDrawable.serializeNBT(compound, textList);
		return super.write(compound);
	}
	
	public void read(CompoundNBT compound) {
		pixelGrid = PixelGridDrawable.deserializeNBT(compound);
		textList = TextDrawable.deserializeNBT(compound);
		super.read(compound);
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
