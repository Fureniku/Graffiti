package com.silvaniastudios.graffiti.block;

import com.silvaniastudios.graffiti.tileentity.GraffitiTileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

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

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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
}
