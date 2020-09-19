package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.items.MagicPenItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPenColourPacket {
	
	int colour;
	boolean writing;
	
	public SetPenColourPacket(int colour, boolean writing) {
		this.colour = colour;
		this.writing = writing;
	}
	
	public static void encode(SetPenColourPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.colour);
		buf.writeBoolean(pkt.writing);
	}
	
	public static SetPenColourPacket decode(PacketBuffer buf) {
		return new SetPenColourPacket(buf.readInt(), buf.readBoolean());
	}
	
	public static class Handler {
		public static void handle(final SetPenColourPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				ItemStack held = player.getHeldItem(Hand.MAIN_HAND);
				
				if (held.getItem() instanceof MagicPenItem) {
					MagicPenItem pen = (MagicPenItem) held.getItem();
					
					pen.setPenColour(held, msg.colour);
					pen.setWritingMode(held, msg.writing);
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}

}
