package com.silvaniastudios.graffiti.block;

import com.silvaniastudios.graffiti.items.BasicPenItem;
import com.silvaniastudios.graffiti.tileentity.GraffitiTileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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
		return createVoxelShape(state);
	}
	
	private VoxelShape createVoxelShape(BlockState state) {
		if (state.get(FACING) == Direction.NORTH) {
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 0.25D);
		}
		if (state.get(FACING) == Direction.EAST) {
			return Block.makeCuboidShape(15.75D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		}
		if (state.get(FACING) == Direction.SOUTH) {
			return Block.makeCuboidShape(0.0D, 0.0D, 15.75D, 16.0D, 16.0D, 16.0D);
		}
		if (state.get(FACING) == Direction.WEST) {
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 0.25D, 16.0D, 16.0D);
		}
		if (state.get(FACING) == Direction.UP) {
			return Block.makeCuboidShape(0.0D, 15.75D, 0.0D, 16.0D, 16.0D, 16.0D);
		}
		if (state.get(FACING) == Direction.DOWN) {
			return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.25D, 16.0D);
		}
		return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	}
	
	@SuppressWarnings("deprecation")
	@Override //Pass click to the block it's attached to
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BasicPenItem) {
			return ActionResultType.FAIL;
		} else {
			BlockPos behindPos = pos.offset(state.get(FACING));
			BlockState behindState = worldIn.getBlockState(behindPos);
			
			return behindState.getBlock().onBlockActivated(behindState, worldIn, behindPos, player, handIn, hit);
		}
	}
	
	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		System.out.println("left clicking!");
		Item item = player.getHeldItem(Hand.OFF_HAND).getItem();
		
		if (item instanceof BasicPenItem) {
			BasicPenItem pen = (BasicPenItem) item;
			
			BlockRayTraceResult result = pen.rayTrace(worldIn, player);
			
			pen.onItemUseSensitive(new ItemUseContext(player, Hand.OFF_HAND, result), Hand.OFF_HAND);
		}
	}
}
