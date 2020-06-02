package com.silvaniastudios.graffiti.tileentity;

import java.util.Objects;

import com.silvaniastudios.graffiti.GraffitiBlocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class ContainerGraffiti extends Container {
	
	public final TileEntityGraffiti te;
	private final IWorldPosCallable canInteractWithCallable;
	
	public ContainerGraffiti(final int id, final PlayerInventory inv, final TileEntityGraffiti tile) {
		super(GraffitiContainerTypes.GRAFFITI.get(), id);
		
		te = tile;
		canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, GraffitiBlocks.GRAFFITI.getBlock());
	}
	
	private static TileEntityGraffiti getTileEntity(final PlayerInventory inv, final PacketBuffer data) {
		Objects.requireNonNull(inv, "no");
		Objects.requireNonNull(data, "no");
		
		final TileEntity tileAtPos = inv.player.world.getTileEntity(data.readBlockPos());
		
		if (tileAtPos instanceof TileEntityGraffiti) {
			return (TileEntityGraffiti) tileAtPos;
		}
		
		throw new IllegalStateException("no");
	}
	
	public ContainerGraffiti(final int id, final PlayerInventory inv, final PacketBuffer data) {
		this(id, inv, getTileEntity(inv, data));
	}
}
