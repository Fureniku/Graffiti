package com.silvaniastudios.graffiti.network;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClearGraffitiPacket {
	
	public ClearGraffitiPacket() {}
	
	public static void encode(ClearGraffitiPacket pkt, PacketBuffer buf) {}
	
	public static ClearGraffitiPacket decode(PacketBuffer buf) {
		return new ClearGraffitiPacket();
	}
	
	public static class Handler {
		public static void handle(final ClearGraffitiPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;
					
					if (!container.te.isLocked()) {
						System.out.println("Clearing all graffiti");
						CompleteGraffitiObject newGraffiti = new CompleteGraffitiObject(container.graffiti.getSide(), null, new ArrayList<TextDrawable>());
						container.te.assignGraffiti(newGraffiti, container.graffiti.getSide());
						
						container.graffiti = newGraffiti;
					}
					
					container.te.update();
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}

}
