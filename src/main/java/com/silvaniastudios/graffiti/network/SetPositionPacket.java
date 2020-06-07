package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPositionPacket {
	
	boolean blockAligned;
	
	int blockX;
	int blockY;
	int blockZ;
	
	double offset;
	
	public SetPositionPacket(BlockPos bPos, boolean blockAligned, double offset) {
		this.blockX = bPos.getX();
		this.blockY = bPos.getY();
		this.blockZ = bPos.getZ();
		
		this.blockAligned = blockAligned;
		this.offset = offset;
	}
	
	public static void encode(SetPositionPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		
		buf.writeBoolean(pkt.blockAligned);
		buf.writeDouble(pkt.offset);
	}
	
	public static SetPositionPacket decode(PacketBuffer buf) {
		return new SetPositionPacket(
				new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), //block pos
				buf.readBoolean(), //position
				buf.readDouble() //offset
				);
	}
	
	public static class Handler {
		public static void handle(final SetPositionPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					
					if (msg.offset >= -0.5 && msg.offset <= 0.5) {
						te.setAlignment(msg.blockAligned, msg.offset);
						te.update();
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
