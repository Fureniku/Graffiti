package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class DeleteGraffitiPacket {
	
	public DeleteGraffitiPacket() {}
	
	public static void encode(DeleteGraffitiPacket pkt, PacketBuffer buf) {}
	
	public static DeleteGraffitiPacket decode(PacketBuffer buf) {
		return new DeleteGraffitiPacket();
	}
	
	public static class Handler {
		public static void handle(final DeleteGraffitiPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;
					
					if (!container.te.isLocked()) {
						container.te.assignGraffiti(null, container.graffiti.getSide());
						
						player.sendMessage(new StringTextComponent("Graffiti deleted."));
					} else {
						player.sendMessage(new StringTextComponent("Graffiti is locked for edits - cannot delete."));
					}
					
					container.te.update();
					
					if (!container.te.doesGraffitiExist()) {
						ctx.get().getSender().world.removeBlock(container.te.getPos(), false);
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}

}
