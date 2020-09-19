package com.silvaniastudios.graffiti.items;

import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EraserItem extends DrawingItem {

	public EraserItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		
		BlockPos clickedPos = context.getPos();
		
		if (world.getBlockState(clickedPos).getBlock() instanceof GraffitiBlock) {
			return erase(world, context, clickedPos);
		} else if (world.getBlockState(context.getPos().offset(context.getFace())).getBlock() instanceof GraffitiBlock) {
			return erase(world, context, context.getPos().offset(context.getFace()));
		}
		return ActionResultType.FAIL;
	}
	
	private ActionResultType erase(World world, ItemUseContext context, BlockPos pos) {
		if (!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
				TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
				CompleteGraffitiObject graffiti = te.getGraffitiForFace(context.getFace().getOpposite());
				
				if (graffiti != null) {
					int size = 64;
					
					int x = te.getVoxel(context.getHitVec().x - pos.getX(), context.getFace().getOpposite(), 64);
					int y = te.getVoxel(context.getHitVec().y - pos.getY(), null, 64); //Dont pass dir for Y coordinate, its irrelevant and breaks things
					int z = te.getVoxel(context.getHitVec().z - pos.getZ(), context.getFace().getOpposite(), 64);

					if ((graffiti.pixelGrid == null || graffiti.pixelGrid.getSize() == 0)) {
						return ActionResultType.PASS;
					}
					
					if (graffiti.pixelGrid.getSize() != 64) {
						size = graffiti.pixelGrid.getSize();
						//get the voxels again coz of scaling
						x = te.getVoxel(context.getHitVec().x - pos.getX(), context.getFace().getOpposite(), graffiti.pixelGrid.getSize());
						y = te.getVoxel(context.getHitVec().y - pos.getY(), null, graffiti.pixelGrid.getSize()); //Dont pass dir for Y coordinate, its irrelevant and breaks things
						z = te.getVoxel(context.getHitVec().z - pos.getZ(), context.getFace().getOpposite(), graffiti.pixelGrid.getSize());
					}
					
					if (context.getFace().getOpposite() == Direction.NORTH) {
						graffiti.pixelGrid.erasePixel(x, y, te);
					}
					if (context.getFace().getOpposite() == Direction.EAST) {
						graffiti.pixelGrid.erasePixel(z, y, te);
					}
					if (context.getFace().getOpposite() == Direction.SOUTH) {
						graffiti.pixelGrid.erasePixel(Math.abs(x-size), y, te);
					}
					if (context.getFace().getOpposite() == Direction.WEST) {
						graffiti.pixelGrid.erasePixel(Math.abs(z-size), y, te);
					}
					
					if (context.getFace().getOpposite() == Direction.UP) {
						graffiti.pixelGrid.erasePixel(x-1, z, te);
					}
					if (context.getFace().getOpposite() == Direction.DOWN) {
						graffiti.pixelGrid.erasePixel(Math.abs(x-size), z, te);
						return ActionResultType.PASS;
					}
					
					return ActionResultType.PASS;
				}
			}
		}
		return ActionResultType.FAIL;
	}
}
