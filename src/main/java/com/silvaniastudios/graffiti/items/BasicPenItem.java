package com.silvaniastudios.graffiti.items;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.silvaniastudios.graffiti.GraffitiBlocks;
import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.client.gui.GuiWriteText;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BasicPenItem extends DrawingItem {
	
	Color col;

	public BasicPenItem(Properties properties, Color col) {
		super(properties);
		this.col = col;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (handIn == Hand.MAIN_HAND) {
			//People are gonna draw dicks with this mod and you thought I wouldn't abbreviate "pen itemstack" appropriately?
			ItemStack penIS = playerIn.getHeldItem(handIn);
			
			CompoundNBT tag = penIS.getOrCreateTag();
			BlockRayTraceResult result = rayTrace(worldIn, playerIn);

			if (playerIn.isCrouching() && result.getType().equals(RayTraceResult.Type.MISS)) {
				boolean writingMode = tag.getBoolean("writing");
	
				if (!worldIn.isRemote) playerIn.sendMessage(new StringTextComponent("Pen set to " + (writingMode ? "Drawing" : "Writing") + " mode"));
				setWritingMode(penIS, !writingMode);
			}
		}
		return ActionResult.resultPass(playerIn.getHeldItem(handIn));
	}
	
	public void setWritingMode(ItemStack penIS, boolean writingMode) {
		CompoundNBT tag = penIS.getOrCreateTag();
		tag.putBoolean("writing", writingMode);
	}
	
	@Override
	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		return false;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		return onItemUseSensitive(context, Hand.MAIN_HAND);
	}
	
	public ActionResultType onItemUseSensitive(ItemUseContext context, Hand hand) {
		if (context.getHand() != hand) {
			return ActionResultType.FAIL;
		}
		
		ItemStack stack = context.getPlayer().getHeldItem(hand);
		CompoundNBT nbt = stack.getOrCreateTag();
		
		if (!nbt.contains("colour")) {
			nbt.putInt("colour", col.getRGB());
		}
		
		stack.setTag(nbt);
		
		World world = context.getWorld();
		BlockPos pos = context.getPos().offset(context.getFace());
		
		BlockPos clickedPos = context.getPos();
		Block clickedBlock = world.getBlockState(clickedPos).getBlock();
		
		if (!(clickedBlock instanceof GraffitiBlock)) {
			
			BlockState state = GraffitiBlocks.GRAFFITI.getDefaultState().with(GraffitiBlock.FACING, context.getFace().getOpposite());
			state = state.getStateForPlacement(context.getFace(), state, world, pos, pos, hand);
			
			if (world.getBlockState(pos).isAir(world, pos)) {
				world.setBlockState(pos, state);
				
				TileEntityGraffiti te = new TileEntityGraffiti();
				te.assignGraffiti(new CompleteGraffitiObject(context.getFace().getOpposite(), null, new ArrayList<TextDrawable>()), context.getFace().getOpposite());
				
				world.setTileEntity(pos, te);
			} else if (world.getBlockState(pos).getBlock() instanceof GraffitiBlock) {
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {				
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					
					//If there's already a graffiti here, pass the drawing into it. If the players right clicking in this instance its probably due to minecraft raycast limitations rather than intentional.
					if (te.getGraffitiForFace(context.getFace().getOpposite()) != null) {
						return beginDraw(te, context, pos, stack);
					} else {
						//if there's not a graffiti, add a new panel
						te.assignGraffiti(new CompleteGraffitiObject(context.getFace().getOpposite(), null, new ArrayList<TextDrawable>()), context.getFace().getOpposite());
					}
				}
			}
			
			return ActionResultType.PASS;
		} else {
			if (world.getTileEntity(clickedPos) instanceof TileEntityGraffiti) {
				TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(clickedPos);
				return beginDraw(te, context, clickedPos, stack);
			}
		}
		return ActionResultType.FAIL;
	}
	
	private ActionResultType beginDraw(TileEntityGraffiti te, ItemUseContext context, BlockPos pos, ItemStack stack) {
		CompleteGraffitiObject graffiti = te.getGraffitiForFace(context.getFace().getOpposite());
		if (graffiti == null) {
			graffiti = new CompleteGraffitiObject(context.getFace().getOpposite(), null, new ArrayList<TextDrawable>());
			te.assignGraffiti(graffiti, context.getFace().getOpposite());
		}
		
		int size = 64;
		
		int x = te.getVoxel(context.getHitVec().x - pos.getX(), context.getFace().getOpposite(), 64);
		int y = te.getVoxel(context.getHitVec().y - pos.getY(), null, 64); //Dont pass dir for Y coordinate, its irrelevant and breaks things
		int z = te.getVoxel(context.getHitVec().z - pos.getZ(), context.getFace().getOpposite(), 64);
		
		boolean textMode = stack.getTag().getBoolean("writing");
		
		if ((graffiti.pixelGrid == null || graffiti.pixelGrid.getSize() == 0) && !textMode) {
			graffiti.pixelGrid = new PixelGridDrawable(16);
			te.update();
		}
		
		if (!textMode && graffiti.pixelGrid.getSize() != 64) {
			size = graffiti.pixelGrid.getSize();
			//get the voxels again coz of scaling
			x = te.getVoxel(context.getHitVec().x - pos.getX(), context.getFace().getOpposite(), graffiti.pixelGrid.getSize());
			y = te.getVoxel(context.getHitVec().y - pos.getY(), null, graffiti.pixelGrid.getSize()); //Dont pass dir for Y coordinate, its irrelevant and breaks things
			z = te.getVoxel(context.getHitVec().z - pos.getZ(), context.getFace().getOpposite(), graffiti.pixelGrid.getSize());
		}
		
		Color colour = new Color(stack.getTag().getInt("colour"));
		
		if (context.getFace().getOpposite() == Direction.NORTH) {
			processDrawingAction(te, graffiti, x, y, colour, textMode, 0, stack);
			return ActionResultType.PASS;
		}
		if (context.getFace().getOpposite() == Direction.EAST) {
			processDrawingAction(te, graffiti, z, y, colour, textMode, 0, stack);
			return ActionResultType.PASS;
		}
		if (context.getFace().getOpposite() == Direction.SOUTH) {
			processDrawingAction(te, graffiti, Math.abs(x-size), y, colour, textMode, 0, stack);
			return ActionResultType.PASS;
		}
		if (context.getFace().getOpposite() == Direction.WEST) {
			processDrawingAction(te, graffiti, Math.abs(z-size), y, colour, textMode, 0, stack);
			return ActionResultType.PASS;
		}
		
		Direction d = context.getPlacementHorizontalFacing();
		
		if (context.getFace().getOpposite() == Direction.UP) {
			int rot = 0;
			if (d == Direction.NORTH) rot = 180;
			if (d == Direction.EAST)  rot =  90;
			if (d == Direction.WEST)  rot = 270;
			processDrawingAction(te, graffiti, x-1, z, colour, textMode, rot, stack);
			return ActionResultType.PASS;
		}
		if (context.getFace().getOpposite() == Direction.DOWN) {
			int rot = 0;
			if (d == Direction.EAST)  rot = 270;
			if (d == Direction.SOUTH) rot = 180;
			if (d == Direction.WEST)  rot =  90;
			processDrawingAction(te, graffiti, Math.abs(x-size), z, colour, textMode, rot, stack);
			return ActionResultType.PASS;
		}
		
		return ActionResultType.FAIL;
	}
	
	@SuppressWarnings("resource")
	private void processDrawingAction(TileEntityGraffiti te, CompleteGraffitiObject graffiti, int x, int y, Color col, boolean textMode, int rotation, ItemStack stack) {
		if (textMode) {
			if (te.getWorld().isRemote) {
				openWritingGui(te, graffiti, x, y, col, rotation, stack);
			}
		} else {
			graffiti.pixelGrid.setPixel(x, y, col.getRGB(), te);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void openWritingGui(TileEntityGraffiti te, CompleteGraffitiObject graffiti, int x, int y, Color col, int rotation, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		mc.displayGuiScreen(new GuiWriteText(te, graffiti, x, y, getColor(stack).getRGB(), rotation));
	}
	
	
	//Taken from item.rayTrace, modified slightly so our block can use it to get hit position.
	public BlockRayTraceResult rayTrace(World worldIn, PlayerEntity player) {
		return (BlockRayTraceResult) rayTrace(worldIn, player, RayTraceContext.FluidMode.NONE);
	}
	
	public Color getColorRaw() {
		return col;
	}
	
	public Color getColor(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains("colour")) {
			return new Color(nbt.getInt("colour"));
		}
		
		if (col != null) {
			nbt.putInt("colour", col.getRGB());
			stack.setTag(nbt);
			return col;
		}
		return Color.WHITE;
	}
	
	public String getHexColour(Color colour) {
		return String.format("#%02X%02X%02X", colour.getRed(), colour.getGreen(), colour.getBlue());  
	}
	
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("Allows writing and drawing on the walls"));
		tooltip.add(new StringTextComponent("Sneak-rightclick in open air to switch modes"));
		tooltip.add(new StringTextComponent(""));
		if (stack.hasTag()) {
			tooltip.add(new StringTextComponent("Current mode: " + (stack.getTag().getBoolean("writing") ? "Writing" : "Drawing")));
		}
		tooltip.add(new StringTextComponent("Color: " + getHexColour(getColor(stack))));	
	}
}
