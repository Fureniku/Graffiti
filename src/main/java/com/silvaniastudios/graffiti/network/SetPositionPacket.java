package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPositionPacket {
	
	int position;
	
	int blockX;
	int blockY;
	int blockZ;
	
	public SetPositionPacket(BlockPos bPos, int pos) {
		position = pos;
		this.blockX = bPos.getX();
		this.blockY = bPos.getY();
		this.blockZ = bPos.getZ();
	}
	
	public static void encode(SetPositionPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		
		buf.writeInt(pkt.position);
	}
	
	public static SetPositionPacket decode(PacketBuffer buf) {
		return new SetPositionPacket(
				new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), //block pos
				buf.readInt());  //position
	}
	
	public static class Handler {
		public static void handle(final SetPositionPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					
					if (msg.position >= 0 && msg.position <= 2) {
						te.setAlignment(msg.position);
						te.update();
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
