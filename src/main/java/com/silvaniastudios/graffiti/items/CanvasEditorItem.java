package com.silvaniastudios.graffiti.items;

import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

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
			
			if (world.getBlockState(clickedPos).getBlock() instanceof GraffitiBlock) {
				return attemptOpenGui(context, clickedPos);
			} else {
				BlockPos offsetPos = context.getPos().offset(context.getFace());
				if (world.getBlockState(offsetPos).getBlock() instanceof GraffitiBlock) {
					return attemptOpenGui(context, offsetPos);
				}
			}
		}
		
		return super.onItemUse(context);
	}
	
	private ActionResultType attemptOpenGui(ItemUseContext context, BlockPos pos) {
		if (context.getWorld().getTileEntity(pos) instanceof TileEntityGraffiti) {
			TileEntityGraffiti te = (TileEntityGraffiti) context.getWorld().getTileEntity(pos);
			
			NetworkHooks.openGui((ServerPlayerEntity) context.getPlayer(), te, buf -> {
				buf.writeBlockPos(pos);
				buf.writeInt(context.getFace().getOpposite().getIndex());
			});
			return ActionResultType.PASS;
		}
		return ActionResultType.FAIL;
	}
}
