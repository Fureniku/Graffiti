package com.silvaniastudios.graffiti.tileentity;

import java.util.Objects;

import com.silvaniastudios.graffiti.GraffitiBlocks;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;

public class ContainerGraffiti extends Container {
	
	public final TileEntityGraffiti te;
	public CompleteGraffitiObject graffiti;
	private final IWorldPosCallable canInteractWithCallable;
	
	public ContainerGraffiti(final int id, final PlayerInventory inv, final TileEntityGraffiti tile, final int dirId) {
		super(GraffitiContainerTypes.GRAFFITI.get(), id);
		te = tile;
		graffiti = tile.getGraffitiForFace(Direction.byIndex(dirId));
		
		canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, GraffitiBlocks.GRAFFITI.getBlock());
	}
	
	private static TileEntityGraffiti getTileEntity(final PlayerInventory inv, final PacketBuffer data) {
		Objects.requireNonNull(inv, "Inventory data is null");
		Objects.requireNonNull(data, "Tile Entity data is null");
		
		final TileEntity tileAtPos = inv.player.world.getTileEntity(data.readBlockPos());
		
		if (tileAtPos instanceof TileEntityGraffiti) {
			return (TileEntityGraffiti) tileAtPos;
		}
		
		throw new IllegalStateException("no");
	}
	
	public ContainerGraffiti(final int id, final PlayerInventory inv, final PacketBuffer data) {
		this(id, inv, getTileEntity(inv, data), data.readInt());
	}
}
