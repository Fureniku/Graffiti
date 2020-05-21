package com.silvaniastudios.graffiti.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class ContainerGraffiti extends Container {
	
	protected ContainerGraffiti(ContainerType<?> type, int id) {
		super(type, id);
		// TODO Auto-generated constructor stub
	}

	public ContainerGraffiti(int id, PlayerInventory inv) {
		super(null, id);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		// TODO Auto-generated method stub
		return false;
	}
}
