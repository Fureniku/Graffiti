package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPositionPacket {
	
	boolean blockAligned;
	double offset;
	
	public SetPositionPacket(boolean blockAligned, double offset) {
		this.blockAligned = blockAligned;
		this.offset = offset;
	}
	
	public static void encode(SetPositionPacket pkt, PacketBuffer buf) {
		buf.writeBoolean(pkt.blockAligned);
		buf.writeDouble(pkt.offset);
	}
	
	public static SetPositionPacket decode(PacketBuffer buf) {
		return new SetPositionPacket(
				buf.readBoolean(), //position
				buf.readDouble() //offset
				);
	}
	
	public static class Handler {
		public static void handle(final SetPositionPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;
					if (msg.offset >= -0.25 && msg.offset <= 0.25) {
						container.graffiti.setAlignment(msg.blockAligned, msg.offset, container.te);
						container.te.update();
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
