package com.silvaniastudios.graffiti.items;

import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CanvasEditorItem extends Item {

	public CanvasEditorItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		
		if (context.getHand() == Hand.MAIN_HAND && !world.isRemote) {
			BlockPos clickedPos = context.getPos();
			Block clickedBlock = world.getBlockState(clickedPos).getBlock();
			
			if (clickedBlock instanceof GraffitiBlock) {
				if (world.getTileEntity(clickedPos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(clickedPos);
					
					NetworkHooks.openGui((ServerPlayerEntity) context.getPlayer(), te, context.getPos());
				}
			}
		}
		
		return super.onItemUse(context);
	}
}
