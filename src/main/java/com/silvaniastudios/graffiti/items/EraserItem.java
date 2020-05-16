package com.silvaniastudios.graffiti.items;

import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EraserItem extends Item {

	public EraserItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		
		BlockPos clickedPos = context.getPos();
		Block clickedBlock = world.getBlockState(clickedPos).getBlock();
		
		if (clickedBlock instanceof GraffitiBlock) {
			if (!world.isRemote) {
				if (world.getTileEntity(clickedPos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(clickedPos);
					BlockState state = world.getBlockState(clickedPos);
					
					if (te.pixelGrid != null) {
						int x = te.getVoxel(context.getHitVec().x - clickedPos.getX(), state.get(GraffitiBlock.FACING), te.pixelGrid.getSize());
						int y = te.getVoxel(context.getHitVec().y - clickedPos.getY(), state.get(GraffitiBlock.FACING), te.pixelGrid.getSize());
						int z = te.getVoxel(context.getHitVec().z - clickedPos.getZ(), state.get(GraffitiBlock.FACING), te.pixelGrid.getSize());
						
						if (state.get(GraffitiBlock.FACING) == Direction.NORTH) {
							te.pixelGrid.erasePixel(x, y);
						}
						if (state.get(GraffitiBlock.FACING) == Direction.EAST) {
							te.pixelGrid.erasePixel(z, y);
						}
						if (state.get(GraffitiBlock.FACING) == Direction.SOUTH) {
							te.pixelGrid.erasePixel(Math.abs(x-16), y);
						}
						if (state.get(GraffitiBlock.FACING) == Direction.WEST) {
							te.pixelGrid.erasePixel(Math.abs(z-16), y);
						}
						
						return ActionResultType.PASS;
					}
				}
			}
		}
		return ActionResultType.FAIL;
	}
}
