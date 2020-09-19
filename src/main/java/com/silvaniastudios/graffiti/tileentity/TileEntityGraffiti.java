package com.silvaniastudios.graffiti.tileentity;

import java.util.ArrayList;

import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.drawables.TextDrawable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class TileEntityGraffiti extends TileEntity implements INamedContainerProvider {
	
	CompleteGraffitiObject graffiti_n;
	CompleteGraffitiObject graffiti_e;
	CompleteGraffitiObject graffiti_s;
	CompleteGraffitiObject graffiti_w;
	CompleteGraffitiObject graffiti_u;
	CompleteGraffitiObject graffiti_d;
	
	boolean locked = false;
	String lockedUuid = "";
	
	public TileEntityGraffiti() {
		super(GraffitiTileEntityTypes.GRAFFITI);
	}
	
	public void update() {
		sendUpdates();
	}
	
	public int getVoxel(double coordinate, Direction d, int size) {
		//North/east is slightly different so negate 1 to "calibrate"
		if (d == Direction.NORTH || d == Direction.EAST) {
			return (int) Math.ceil(coordinate*size)-1;
		}
		return (int) Math.ceil(coordinate*size);
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void toggleLocked(boolean lock, PlayerEntity player) {
		if (!this.locked && lock) {
			this.locked = true;
			lockedUuid = player.getCachedUniqueIdString();
			player.sendMessage(new StringTextComponent("Editing locked"));
		}
		
		if (this.locked && !lock) {
			if (player.getCachedUniqueIdString().equalsIgnoreCase(lockedUuid)) {
				this.locked = false;
				lockedUuid = "";
				player.sendMessage(new StringTextComponent("Editing unlocked"));
			}
		}
	}
	
	public CompleteGraffitiObject getGraffitiForFace(Direction d) {
		if (d == Direction.NORTH) { return graffiti_n; }
		if (d == Direction.EAST)  { return graffiti_e; }
		if (d == Direction.SOUTH) { return graffiti_s; }
		if (d == Direction.WEST)  { return graffiti_w; }
		if (d == Direction.DOWN)  { return graffiti_d; }
		return graffiti_u;
	}
	
	public void assignGraffiti(CompleteGraffitiObject graffiti, Direction d) {
		System.out.println("Completely overriding graffiti on " + d.getName() + " and it has this many texts: " + graffiti.textList.size());
		if (d == Direction.NORTH) { graffiti_n = graffiti; } 
		if (d == Direction.EAST)  { graffiti_e = graffiti; } 
		if (d == Direction.SOUTH) { graffiti_s = graffiti; } 
		if (d == Direction.WEST)  { graffiti_w = graffiti; } 
		if (d == Direction.DOWN)  { graffiti_d = graffiti; } 
		if (d == Direction.UP)    { graffiti_u = graffiti; } 
		
		sendUpdates();
	}
	
	public boolean doesGraffitiExist() {
		for (Direction dir : Direction.values()) {
			if (getGraffitiForFace(dir) != null) {
				return true;
			}
		}
		return false;
	}
	
	PixelGridDrawable oldGrid;
	ArrayList<TextDrawable> oldTexts; //the sacred texts!
	
	public CompoundNBT write(CompoundNBT compound) {
		if (graffiti_n != null) { CompleteGraffitiObject.serializeNBT(compound, graffiti_n, "_n"); } else { compound.remove("graffiti_n"); }
		if (graffiti_e != null) { CompleteGraffitiObject.serializeNBT(compound, graffiti_e, "_e"); } else { compound.remove("graffiti_e"); }
		if (graffiti_s != null) { CompleteGraffitiObject.serializeNBT(compound, graffiti_s, "_s"); } else { compound.remove("graffiti_s"); }
		if (graffiti_w != null) { CompleteGraffitiObject.serializeNBT(compound, graffiti_w, "_w"); } else { compound.remove("graffiti_w"); }
		if (graffiti_u != null) { CompleteGraffitiObject.serializeNBT(compound, graffiti_u, "_u"); } else { compound.remove("graffiti_u"); }
		if (graffiti_d != null) { CompleteGraffitiObject.serializeNBT(compound, graffiti_d, "_d"); } else { compound.remove("graffiti_d"); }
		
		if (oldGrid != null || oldTexts != null) {
			System.out.println("Old Graffiti data has been loaded. Now attempting to update to new format.");
			if (updateData(oldGrid, oldTexts, compound)) {
				oldGrid = null;
				oldTexts = null;
			}
		}
		
		compound.putBoolean("locked", locked);
		compound.putString("lockedUuid", lockedUuid);
		return super.write(compound);
	}
	
	@Override
	public void read(CompoundNBT compound) {
		if (compound.contains("pixel_grid") || compound.contains("text_objects")) {
			System.out.println("Loading old data, this will be updated next save cycle.");
			
			oldGrid = PixelGridDrawable.deserializeNBT(compound);
			oldTexts = TextDrawable.deserializeNBT(compound);
		}
		
		if (compound.contains("graffiti_n")) { graffiti_n = CompleteGraffitiObject.deserializeNBT(compound, "_n"); }
		if (compound.contains("graffiti_e")) { graffiti_e = CompleteGraffitiObject.deserializeNBT(compound, "_e"); }
		if (compound.contains("graffiti_s")) { graffiti_s = CompleteGraffitiObject.deserializeNBT(compound, "_s"); }
		if (compound.contains("graffiti_w")) { graffiti_w = CompleteGraffitiObject.deserializeNBT(compound, "_w"); }
		if (compound.contains("graffiti_u")) { graffiti_u = CompleteGraffitiObject.deserializeNBT(compound, "_u"); }
		if (compound.contains("graffiti_d")) { graffiti_d = CompleteGraffitiObject.deserializeNBT(compound, "_d"); }
		
		locked = compound.getBoolean("locked");
		lockedUuid = compound.getString("lockedUuid");
		
		super.read(compound);
	}
	
	//used for updating old storage data (1.2) to new system (1.3+), to stop people losing their art (hopefully)
	//Only four things existed then so we only need to update for those four. everything else was new this update anyway.
	public boolean updateData(PixelGridDrawable grid, ArrayList<TextDrawable> text, CompoundNBT compound) {
		Direction facing = this.getBlockState().get(BlockStateProperties.FACING);
		
		if (text == null) {
			text = new ArrayList<TextDrawable>();
		}
		
		CompleteGraffitiObject graffiti = new CompleteGraffitiObject(facing, grid, text);
		System.out.printf("[GRAFFITI] Updating old graffiti data at %s, %s, %s (facing %s)\n", pos.getX(), pos.getY(), pos.getZ(), facing);
		
		if (facing == Direction.NORTH) { graffiti_n = graffiti; }
		if (facing == Direction.EAST)  { graffiti_e = graffiti; }
		if (facing == Direction.SOUTH) { graffiti_s = graffiti; }
		if (facing == Direction.WEST)  { graffiti_w = graffiti; }
		if (facing == Direction.UP)    { graffiti_u = graffiti; }
		if (facing == Direction.DOWN)  { graffiti_d = graffiti; }
		
		compound.remove("pixel_grid");
		compound.remove("text_objects");
		
		update();
		return true;
	}
	
	//TODO is this needed?
	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		BlockRayTraceResult result = rayTrace(this.world, player);

		if (!result.getType().equals(RayTraceResult.Type.MISS)) {
			return new ContainerGraffiti(id, inv, this, result.getFace().getOpposite().getIndex());
		}
		//This should never happen, because it says the player right clicked it but also missed, which is impossible?
		return new ContainerGraffiti(id, inv, this, 0);
	}
	
	//Stolen from Item
	protected BlockRayTraceResult rayTrace(World worldIn, PlayerEntity player) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		Vec3d vec3d = player.getEyePosition(1.0F);
		float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
		float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
		Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
	}

	public BlockState getState() { 
		return world.getBlockState(pos);
	}
	
	@SuppressWarnings("deprecation")
	public boolean isLoaded() {
		return this.hasWorld() && this.hasPosition() ? this.getWorld().isBlockLoaded(this.getPos()) : false;
	}
	
	public boolean hasPosition() {
		return this.pos != null && this.pos != BlockPos.ZERO;
	}
	
	public void sendUpdates() {
		this.markDirty();
		
		if (this.isLoaded()) {
			final BlockState state = this.getState();
			this.getWorld().notifyBlockUpdate(this.pos, state, state, 3);
		}
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 0, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		this.read(pkt.getNbtCompound());
		this.getWorld().notifyBlockUpdate(this.pos, this.getState(), this.getState(), 3);
	}

	public ItemStack getBackdropBlock() {
		return new ItemStack(Blocks.COBBLESTONE);
	}



	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.graffiti");
	}
}
