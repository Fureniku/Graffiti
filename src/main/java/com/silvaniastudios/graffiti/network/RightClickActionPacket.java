package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class RightClickActionPacket {
	
	int actionID;
	int transparency;
	String url;
	
	public RightClickActionPacket(int actionId, int transparency, String url) {
		this.actionID = actionId;
		this.transparency = transparency;
		this.url = url;
	}
	
	public static void encode(RightClickActionPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.actionID);
		buf.writeInt(pkt.transparency);
		buf.writeString(pkt.url);
	}
	
	public static RightClickActionPacket decode(PacketBuffer buf) {
		return new RightClickActionPacket(
				buf.readInt(), //action id
				buf.readInt(), //transparency
				buf.readString(500) //url
				);
	}
	
	public static class Handler {
		public static void handle(final RightClickActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;
					
					container.graffiti.setRightClickAction(msg.actionID);
					
					if (msg.actionID == 2) {
						container.graffiti.setBackgroundTransparency(msg.transparency);
					}
					
					if (msg.actionID == 3 && !msg.url.isEmpty()) {
						container.graffiti.setUrl(msg.url);
					}
					
					container.te.update();
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
