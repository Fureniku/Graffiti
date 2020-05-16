package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class AddGridPacket {
	
	int blockX;
	int blockY;
	int blockZ;
	
	int size;
	
	public AddGridPacket(BlockPos bPos, int size) {

		this.blockX = bPos.getX();
		this.blockY = bPos.getY();
		this.blockZ = bPos.getZ();
		
		this.size = size;
	}
	
	public static void encode(AddGridPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		buf.writeInt(pkt.size);
	}
	
	public static AddGridPacket decode(PacketBuffer buf) {
		return new AddGridPacket(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt());
	}
	
	public static class Handler {
		public static void handle(final AddGridPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				PlayerEntity player = ctx.get().getSender();
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					if (!te.isLocked()) {
						if (te.pixelGrid == null) {
							if (msg.size == 16 || msg.size == 32 || msg.size == 64 || msg.size == 128) {
								te.pixelGrid = new PixelGridDrawable(msg.size);
								te.update();
								player.sendMessage(new StringTextComponent("Added new " + msg.size + "x pixel grid"));
							} else {
								System.out.println("Malformed canvas editing packet received from " + player.getDisplayName());
							}
						} else {
							player.sendMessage(new StringTextComponent("Unable to add new pixel grid; one already exists!"));
						}
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
