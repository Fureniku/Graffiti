package com.silvaniastudios.graffiti.items;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import com.silvaniastudios.graffiti.GraffitiBlocks;
import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.client.gui.GuiWriteText;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BasicPenItem extends Item {
	
	Color col;

	public BasicPenItem(Properties properties, Color col) {
		super(properties);
		this.col = col;
	}
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isCrouching()) {
			//People are gonna draw dicks with this mod and you thought I wouldn't abbreviate "pen itemstack" appropriately?
			ItemStack penIS = playerIn.getHeldItem(handIn);
			
			if (penIS.getDamage() != 0) {
				penIS.setDamage(0);
				if (!worldIn.isRemote) playerIn.sendMessage(new StringTextComponent("Pen set to Writing mode"));
			} else {
				penIS.setDamage(1);
				if (!worldIn.isRemote) playerIn.sendMessage(new StringTextComponent("Pen set to Drawing mode"));
			}
		}
		return ActionResult.resultPass(playerIn.getHeldItem(handIn));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos().offset(context.getFace());
		
		BlockPos clickedPos = context.getPos();
		Block clickedBlock = world.getBlockState(clickedPos).getBlock();
		
		if (!(clickedBlock instanceof GraffitiBlock)) {
			BlockState state = GraffitiBlocks.GRAFFITI.getDefaultState().with(GraffitiBlock.FACING, context.getFace().getOpposite());
			state = state.getStateForPlacement(context.getFace(), state, world, pos, pos, Hand.MAIN_HAND);
			
			if (world.getBlockState(pos).isAir(world, pos)) {
				world.setBlockState(pos, state);
				return ActionResultType.PASS;
			}
		} else {
			
				if (world.getTileEntity(clickedPos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(clickedPos);
					BlockState state = world.getBlockState(clickedPos);
					
					int size = 64;
					
					int x = te.getVoxel(context.getHitVec().x - clickedPos.getX(), state.get(GraffitiBlock.FACING), 64);
					int y = te.getVoxel(context.getHitVec().y - clickedPos.getY(), null, 64); //Dont pass dir for Y coordinate, its irrelevant and breaks things
					int z = te.getVoxel(context.getHitVec().z - clickedPos.getZ(), state.get(GraffitiBlock.FACING), 64);
					
					boolean textMode = context.getItem().getDamage() == 0 ? true : false;
					
					if (te.pixelGrid == null && !textMode) {
						te.pixelGrid = new PixelGridDrawable(16);
						te.update();
					}
					
					if (!textMode && te.pixelGrid.getSize() != 64) {
						size = te.pixelGrid.getSize();
						//get the voxels again coz of scaling
						x = te.getVoxel(context.getHitVec().x - clickedPos.getX(), state.get(GraffitiBlock.FACING), te.pixelGrid.getSize());
						y = te.getVoxel(context.getHitVec().y - clickedPos.getY(), null, te.pixelGrid.getSize()); //Dont pass dir for Y coordinate, its irrelevant and breaks things
						z = te.getVoxel(context.getHitVec().z - clickedPos.getZ(), state.get(GraffitiBlock.FACING), te.pixelGrid.getSize());
					}
					
					BlockPos camPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
					
					if (state.get(GraffitiBlock.FACING) == Direction.NORTH) {
						processDrawingAction(te, x, y, col, textMode, camPos.offset(Direction.SOUTH), 0);
						return ActionResultType.PASS;
					}
					if (state.get(GraffitiBlock.FACING) == Direction.EAST) {
						processDrawingAction(te, z, y, col, textMode, camPos.offset(Direction.WEST), 0);
						return ActionResultType.PASS;
					}
					if (state.get(GraffitiBlock.FACING) == Direction.SOUTH) {
						processDrawingAction(te, Math.abs(x-size), y, col, textMode, camPos.offset(Direction.NORTH), 0);
						return ActionResultType.PASS;
					}
					if (state.get(GraffitiBlock.FACING) == Direction.WEST) {
						processDrawingAction(te, Math.abs(z-size), y, col, textMode, camPos.offset(Direction.EAST), 0);
						return ActionResultType.PASS;
					}
					
					Direction d = context.getPlacementHorizontalFacing();
					
					if (state.get(GraffitiBlock.FACING) == Direction.UP) {
						int rot = 0;
						if (d == Direction.NORTH) rot = 180;
						if (d == Direction.EAST)  rot =  90;
						if (d == Direction.WEST)  rot = 270;
						processDrawingAction(te, x-1, z, col, textMode, camPos.offset(Direction.DOWN), rot);
						return ActionResultType.PASS;
					}
					if (state.get(GraffitiBlock.FACING) == Direction.DOWN) {
						int rot = 0;
						if (d == Direction.EAST)  rot = 270;
						if (d == Direction.SOUTH) rot = 180;
						if (d == Direction.WEST)  rot =  90;
						processDrawingAction(te, Math.abs(x-size), z, col, textMode, camPos.offset(Direction.UP), rot);
						return ActionResultType.PASS;
					}
				}
		}
		return ActionResultType.FAIL;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("Allows writing and drawing on the walls"));
		tooltip.add(new StringTextComponent("Sneak-rightclick in open air to switch modes"));
		tooltip.add(new StringTextComponent(""));
		String mode = stack.getDamage() == 0 ? "Writing" : "Drawing";
		tooltip.add(new StringTextComponent("Current mode: " + mode));
		
	}
	
	private void processDrawingAction(TileEntityGraffiti te, int x, int y, Color col, boolean textMode, BlockPos camPos, int rotation) {
		if (textMode) {
			if (te.getWorld().isRemote) {
				openWritingGui(te, x, y, col, rotation);
			}
		} else {
			te.pixelGrid.setPixel(x, y, col.getRGB());
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void openWritingGui(TileEntityGraffiti te, int x, int y, Color col, int rotation) {
		Minecraft mc = Minecraft.getInstance();
		mc.displayGuiScreen(new GuiWriteText(te, x, y, col.getRGB(), rotation));
		System.out.println("4");
	}
	
	
}
