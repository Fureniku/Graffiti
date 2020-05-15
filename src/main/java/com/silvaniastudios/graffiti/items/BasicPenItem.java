package com.silvaniastudios.graffiti.items;

import java.awt.Color;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.silvaniastudios.graffiti.GraffitiBlocks;
import com.silvaniastudios.graffiti.block.GraffitiBlock;
import com.silvaniastudios.graffiti.client.gui.GuiWriteText;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

public class BasicPenItem extends Item {
	
	Color col;
	GameProfile fakePlayerProfile = new GameProfile(UUID.randomUUID(), "FakePlayer");
	FakePlayer guiCam = null;

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
			} else {
				penIS.setDamage(1);
			}
		}
		System.out.println("on item right click!");
		return ActionResult.resultPass(playerIn.getHeldItem(handIn));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos().offset(context.getFace());
		
		BlockPos clickedPos = context.getPos();
		Block clickedBlock = world.getBlockState(clickedPos).getBlock();
		
		if (clickedBlock instanceof GraffitiBlock) {
			if (!world.isRemote) {				
				if (world.getTileEntity(clickedPos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(clickedPos);
					BlockState state = world.getBlockState(clickedPos);
					
					int x = te.getVoxel(context.getHitVec().x - clickedPos.getX(), state.get(GraffitiBlock.FACING));
					int y = te.getVoxel(context.getHitVec().y - clickedPos.getY(), null); //Dont pass dir for Y coordinate, its irrelevant and breaks things
					int z = te.getVoxel(context.getHitVec().z - clickedPos.getZ(), state.get(GraffitiBlock.FACING));
					
					boolean textMode = context.getItem().getDamage() == 0 ? true : false;
					BlockPos camPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
					
					if (state.get(GraffitiBlock.FACING) == Direction.NORTH) {
						processDrawingAction(te, x, y, col, textMode, camPos.offset(Direction.SOUTH));
					}
					if (state.get(GraffitiBlock.FACING) == Direction.EAST) {
						processDrawingAction(te, z, y, col, textMode, camPos.offset(Direction.WEST));
					}
					if (state.get(GraffitiBlock.FACING) == Direction.SOUTH) {
						processDrawingAction(te, Math.abs(x-16), y, col, textMode, camPos.offset(Direction.NORTH));
					}
					if (state.get(GraffitiBlock.FACING) == Direction.WEST) {
						processDrawingAction(te, Math.abs(z-16), y, col, textMode, camPos.offset(Direction.EAST));
					}
				}
			}
		} else {
			BlockState state = GraffitiBlocks.GRAFFITI.getDefaultState().with(GraffitiBlock.FACING, context.getFace().getOpposite());
			state = state.getStateForPlacement(context.getFace(), state, world, pos, pos, Hand.MAIN_HAND);
			
			if (world.getBlockState(pos).isAir(world, pos)) {
				world.setBlockState(pos, state);
				return ActionResultType.PASS;
			}
		}
		return ActionResultType.FAIL;
	}
	
	private void processDrawingAction(TileEntityGraffiti te, int x, int y, Color col, boolean textMode, BlockPos camPos) {
		if (textMode) {
			System.out.println("wrote some text apparently");
			//Create an entity to move the camera to for UI display
			if (guiCam == null) {
				guiCam = new FakePlayer((ServerWorld) te.getWorld(), fakePlayerProfile);
			}
			guiCam.setPositionAndRotation(camPos.getX()-0.5, camPos.getY()-0.5, camPos.getZ()-0.5, 0, 0);
			//te.writeText(new GraffitiTextDrawable("teststring", x, y, col.getRGB(), 1));
			openWritingGui(te, x, y, col, guiCam);
			guiCam = null;
		} else {
			te.setPixel(x, y, col.getRGB());
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void openWritingGui(TileEntityGraffiti te, int x, int y, Color col, FakePlayer cam) {
		Minecraft mc = Minecraft.getInstance();
		mc.displayGuiScreen(new GuiWriteText(te, x, y, col.getRGB()));
		System.out.println("4");
	}
	
	
}
