package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;
import com.silvaniastudios.graffiti.util.GraffitiUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class ModifyGridPacket {
	
	int blockX;
	int blockY;
	int blockZ;
	
	int size;
	int transparency;
	
	boolean rescale;
	
	public ModifyGridPacket(BlockPos bPos, int size, int transparency, boolean rescale) {

		this.blockX = bPos.getX();
		this.blockY = bPos.getY();
		this.blockZ = bPos.getZ();
		
		this.size = size;
		this.transparency = transparency;
		
		this.rescale = rescale;
	}
	
	public static void encode(ModifyGridPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		buf.writeInt(pkt.size);
		buf.writeInt(pkt.transparency);
		buf.writeBoolean(pkt.rescale);
	}
	
	public static ModifyGridPacket decode(PacketBuffer buf) {
		return new ModifyGridPacket(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), //blockpos
				buf.readInt(), //size
				buf.readInt(), //transparency
				buf.readBoolean() //resize;
				);
	}
	
	public static class Handler {
		public static void handle(final ModifyGridPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				PlayerEntity player = ctx.get().getSender();
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					if (!te.isLocked()) {
						if (te.pixelGrid == null || te.pixelGrid.getSize() == 0) {
							if (msg.size == 16 || msg.size == 32 || msg.size == 64 || msg.size == 128) {
								te.pixelGrid = new PixelGridDrawable(msg.size);
								te.pixelGrid.setTransparency(msg.transparency);
								te.update();
								player.sendMessage(new StringTextComponent("Added new " + msg.size + "x" + msg.size + " pixel grid"));
							} else {
								System.out.println("Malformed canvas editing packet received from " + player.getDisplayName());
							}
						} else if ((msg.size == 16 || msg.size == 32 || msg.size == 64 || msg.size == 128) && msg.size != te.pixelGrid.getSize()) {
							te.pixelGrid.setTransparency(msg.transparency);
							te.pixelGrid.setNewGrid(msg.size, GraffitiUtils.rescaleMultiple(te.pixelGrid.getPixelGrid(), msg.size, msg.rescale));
							te.update();
							player.sendMessage(new StringTextComponent("Pixel grid resized."));
						} else if (msg.size == 0) {
							te.pixelGrid = new PixelGridDrawable(0);
							te.update();
							
							NetworkHooks.openGui((ServerPlayerEntity) player, te, te.getPos());
							player.sendMessage(new StringTextComponent("Pixel grid removed."));
						}
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
		
		
	}
}
