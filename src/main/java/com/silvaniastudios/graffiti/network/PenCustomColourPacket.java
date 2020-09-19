package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.items.MagicPenItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

public class PenCustomColourPacket {
	
	int colour;
	int slot;
	
	public PenCustomColourPacket(int colour, int slot) {
		this.colour = colour;
		this.slot = slot;
	}
	
	public static void encode(PenCustomColourPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.colour);
		buf.writeInt(pkt.slot);
	}
	
	public static PenCustomColourPacket decode(PacketBuffer buf) {
		return new PenCustomColourPacket(buf.readInt(), buf.readInt());
	}
	
	public static class Handler {
		public static void handle(final PenCustomColourPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				ItemStack held = player.getHeldItem(Hand.MAIN_HAND);
				
				if (held.getItem() instanceof MagicPenItem) {
					MagicPenItem pen = (MagicPenItem) held.getItem();
					
					pen.setCustomColour(held, msg.colour, msg.slot);
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
