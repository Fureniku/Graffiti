package com.silvaniastudios.graffiti.block;

import com.silvaniastudios.graffiti.client.gui.GuiDisplayArt;
import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.items.BasicPenItem;
import com.silvaniastudios.graffiti.items.CanvasEditorItem;
import com.silvaniastudios.graffiti.items.DrawingItem;
import com.silvaniastudios.graffiti.tileentity.GraffitiTileEntityTypes;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GraffitiBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public GraffitiBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));

	}
	
	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return GraffitiTileEntityTypes.GRAFFITI.create();
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = VoxelShapes.empty();
		
		if (worldIn.getTileEntity(pos) instanceof TileEntityGraffiti) {
			TileEntityGraffiti te = (TileEntityGraffiti) worldIn.getTileEntity(pos);

			if (context.getEntity() != null) {
				if (context.getEntity() instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) context.getEntity();
					for (Direction dir : Direction.values()) {
						if (te.getGraffitiForFace(dir) != null) {
							if (player.getAdjustedHorizontalFacing().getOpposite() != dir) {
								VoxelShape faceShape = VoxelShapes.empty();
								
								double offset = te.getGraffitiForFace(dir).getAlignment() * 16;
								BlockState stateBehind = te.getWorld().getBlockState(te.getPos().offset(dir));
								VoxelShape voxelShape = stateBehind.getCollisionShape(te.getWorld(), te.getPos().offset(dir));
								
								if (te.getGraffitiForFace(dir).isOffsetIntoBlock() && !voxelShape.isEmpty()) {
									AxisAlignedBB shapeBehind = voxelShape.getBoundingBox();
									double minX = 0;
									double minY = 0;
									double minZ = 0;
									double maxX = 1;
									double maxY = 1;
									double maxZ = 1;
									
									if (dir == Direction.NORTH) {
										minX = constrain(shapeBehind.minX*16D); //6
										minY = constrain(shapeBehind.minY*16D); //0
										minZ = (shapeBehind.maxZ * 16D) - 16; //6 = 10
										maxX = constrain(shapeBehind.maxX*16D); //10
										maxY = constrain(shapeBehind.maxY*16D); //16
										maxZ = minZ + offset;					//10 = 10.25
									}
							
									if (dir == Direction.SOUTH) {
										minX = constrain(shapeBehind.minX*16D);
										minY = constrain(shapeBehind.minY*16D);
										minZ = (shapeBehind.minZ * 16D) + 16;
										maxX = constrain(shapeBehind.maxX*16D);
										maxY = constrain(shapeBehind.maxY*16D);
										maxZ = minZ + offset;
									}
									if (dir == Direction.EAST)  {
										minX = (shapeBehind.minX * 16D) + 16;
										minY = constrain(shapeBehind.minY*16D);
										minZ = constrain(shapeBehind.minZ*16D);
										maxX = minX - offset;
										maxY = constrain(shapeBehind.maxY*16D);
										maxZ = constrain(shapeBehind.maxZ*16D);
									}
									
									
									if (dir == Direction.WEST) {
										minX = (shapeBehind.maxX * 16D) - 16;
										minY = constrain(shapeBehind.minY*16D);
										minZ = constrain(shapeBehind.minZ*16D);
										maxX = minX + offset;
										maxY = constrain(shapeBehind.maxY*16D);
										maxZ = constrain(shapeBehind.maxZ*16D);
									}
									
									if (dir == Direction.UP)   { 
										minX = constrain(shapeBehind.minX*16D);
										minY = (shapeBehind.minY * 16D) + 16;
										minZ = constrain(shapeBehind.minZ*16D);
										maxX = constrain(shapeBehind.maxX*16D);
										maxY = minY - offset;
										maxZ = constrain(shapeBehind.maxZ*16D);
									}
									if (dir == Direction.DOWN) {
										minX = constrain(shapeBehind.minX*16D);
										minY = (shapeBehind.maxY * 16D) - 16;
										minZ = constrain(shapeBehind.minZ*16D);
										maxX = constrain(shapeBehind.maxX*16D);
										maxY = minY + offset;
										maxZ = constrain(shapeBehind.maxZ*16D);
									}
									
									faceShape = Block.makeCuboidShape(minX, minY, minZ, maxX, maxY, maxZ);
									
								} else {
									if (dir == Direction.NORTH) { faceShape = Block.makeCuboidShape(0.0D, 			0.0D, 			0.0D,			16.0D, 	16.0D, 	offset); }
									if (dir == Direction.EAST)  { faceShape = Block.makeCuboidShape(16.0D - offset, 0.0D, 			0.0D,			16.0D,	16.0D, 	16.0D); }
									if (dir == Direction.SOUTH) { faceShape = Block.makeCuboidShape(0.0D, 			0.0D, 			16.0D - offset, 16.0D, 	16.0D, 	16.0D); }
									if (dir == Direction.WEST)  { faceShape = Block.makeCuboidShape(0.0D,			0.0D, 			0.0D, 			offset,	16.0D, 	16.0D); }
									if (dir == Direction.UP)    { faceShape = Block.makeCuboidShape(0.0D,			16.0D - offset, 0.0D, 			16.0D, 	16.0D,	16.0D); }
									if (dir == Direction.DOWN)  { faceShape = Block.makeCuboidShape(0.0D, 			0.0D,			0.0D, 			16.0D, 	offset,	16.0D); }
								}

								
								shape = VoxelShapes.or(shape, faceShape);
							}
						}
					}
					return shape;
				}
			}
		}
		
		if (state.get(FACING) == Direction.NORTH) {
			shape = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 0.25D);
		} else if (state.get(FACING) == Direction.EAST) {
			shape = Block.makeCuboidShape(15.75D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		} else if (state.get(FACING) == Direction.SOUTH) {
			shape = Block.makeCuboidShape(0.0D, 0.0D, 15.75D, 16.0D, 16.0D, 16.0D);
		} else if (state.get(FACING) == Direction.WEST) {
			shape = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 0.25D, 16.0D, 16.0D);
		} else if (state.get(FACING) == Direction.UP) {
			shape = Block.makeCuboidShape(0.0D, 15.75D, 0.0D, 16.0D, 16.0D, 16.0D);
		} else {
			shape = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.25D, 16.0D);
		}
		
		return shape;
	}
	
	private double constrain(double in) {
		if (in < 0) { return 0; }
		if (in > 16) { return 16; }
		return in;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof DrawingItem || player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof CanvasEditorItem) {
			return ActionResultType.FAIL;
		} else {
			Direction face = hit.getFace();
			TileEntity te = worldIn.getTileEntity(pos);
			if (te != null && te instanceof TileEntityGraffiti) {
				TileEntityGraffiti tile = (TileEntityGraffiti) te;
				CompleteGraffitiObject graffiti = tile.getGraffitiForFace(face);

				if (graffiti != null) {
					int actionId = graffiti.getRightClickAction();
					
					if (actionId == 0) { //clickthrough (default)
						BlockPos behindPos = pos.offset(state.get(FACING));
						BlockState behindState = worldIn.getBlockState(behindPos);
						
						return behindState.getBlock().onBlockActivated(behindState, worldIn, behindPos, player, handIn, hit);
					}
					
					if (actionId == 1) { //no action
						return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
					}
					
					//actions 2/3 are client-only
					if (worldIn.isRemote) {
						processClientActivations(graffiti, actionId);
					}
				}
			}
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void processClientActivations(CompleteGraffitiObject graffiti, int actionId) {
		Minecraft mc = Minecraft.getInstance();
		if (actionId == 2) { //display art
			mc.displayGuiScreen(new GuiDisplayArt(graffiti));
		}
		
		if (actionId == 3) { //open url
			mc.displayGuiScreen(new ConfirmOpenLinkScreen((p_213064_1_) -> {
				if (p_213064_1_) {
					Util.getOSType().openURI(graffiti.getUrl());
				}

				mc.displayGuiScreen(null);
			}, graffiti.getUrl(), true));
		}
	}
	
	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		Item item = player.getHeldItem(Hand.OFF_HAND).getItem();
		
		if (item instanceof BasicPenItem) {
			BasicPenItem pen = (BasicPenItem) item;
			
			BlockRayTraceResult result = pen.rayTrace(worldIn, player);
			
			pen.onItemUseSensitive(new ItemUseContext(player, Hand.OFF_HAND, result), Hand.OFF_HAND);
		}
	}
}
