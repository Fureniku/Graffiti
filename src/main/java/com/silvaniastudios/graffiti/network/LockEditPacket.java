package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class LockEditPacket {

	boolean locked;
	
	public LockEditPacket(boolean lockStatus) {
		this.locked = lockStatus;
	}
	
	public static void encode(LockEditPacket pkt, PacketBuffer buf) {
		buf.writeBoolean(pkt.locked);
	}
	
	public static LockEditPacket decode(PacketBuffer buf) {
		return new LockEditPacket(buf.readBoolean());
	}
	
	public static class Handler {
		public static void handle(final LockEditPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;

					container.te.toggleLocked(msg.locked, ctx.get().getSender());
					container.te.update();
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}

}
