package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoveGridPacket {

	int blockX;
	int blockY;
	int blockZ;
	
	public RemoveGridPacket(BlockPos bPos) {

		this.blockX = bPos.getX();
		this.blockY = bPos.getY();
		this.blockZ = bPos.getZ();
	}
	
	public static void encode(RemoveGridPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		
	}
	
	public static RemoveGridPacket decode(PacketBuffer buf) {
		return new RemoveGridPacket(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
	}
	
	public static class Handler {
		public static void handle(final RemoveGridPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					if (!te.isLocked()) {
						te.pixelGrid = null;
						te.update();
						ctx.get().getSender().sendMessage(new StringTextComponent("Removed pixel grid"));
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
