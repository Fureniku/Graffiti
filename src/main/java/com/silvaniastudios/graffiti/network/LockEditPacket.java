package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class LockEditPacket {
	
	int blockX;
	int blockY;
	int blockZ;
	boolean locked;
	
	public LockEditPacket(BlockPos bPos, boolean lockStatus) {
		this.blockX = bPos.getX();
		this.blockY = bPos.getY();
		this.blockZ = bPos.getZ();
		
		this.locked = lockStatus;
	}
	
	public static void encode(LockEditPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		
		buf.writeBoolean(pkt.locked);
	}
	
	public static LockEditPacket decode(PacketBuffer buf) {
		return new LockEditPacket(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readBoolean());
	}
	
	public static class Handler {
		public static void handle(final LockEditPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);

					te.toggleLocked(msg.locked, ctx.get().getSender());
					te.update();
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}

}
