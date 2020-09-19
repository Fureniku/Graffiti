package com.silvaniastudios.graffiti.network;

import com.silvaniastudios.graffiti.Graffiti;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class GraffitiPacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Graffiti.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	
	public static void registerPackets() {
		int packet_id = 0;
		
		INSTANCE.registerMessage(packet_id++, WriteTextPacket.class, WriteTextPacket::encode, WriteTextPacket::decode, WriteTextPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, SetPositionPacket.class, SetPositionPacket::encode, SetPositionPacket::decode, SetPositionPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, ModifyGridPacket.class, ModifyGridPacket::encode, ModifyGridPacket::decode, ModifyGridPacket.Handler::handle); //add a new drawing grid to a graffiti block
		INSTANCE.registerMessage(packet_id++, LockEditPacket.class, LockEditPacket::encode, LockEditPacket::decode, LockEditPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, RightClickActionPacket.class, RightClickActionPacket::encode, RightClickActionPacket::decode, RightClickActionPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, PenCustomColourPacket.class, PenCustomColourPacket::encode, PenCustomColourPacket::decode, PenCustomColourPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, SetPenColourPacket.class, SetPenColourPacket::encode, SetPenColourPacket::decode, SetPenColourPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, WriteTextFromPenPacket.class, WriteTextFromPenPacket::encode, WriteTextFromPenPacket::decode, WriteTextFromPenPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, DeleteGraffitiPacket.class, DeleteGraffitiPacket::encode, DeleteGraffitiPacket::decode, DeleteGraffitiPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, ClearGraffitiPacket.class, ClearGraffitiPacket::encode, ClearGraffitiPacket::decode, ClearGraffitiPacket.Handler::handle);
		INSTANCE.registerMessage(packet_id++, SetGraffitiRowPacket.class, SetGraffitiRowPacket::encode, SetGraffitiRowPacket::decode, SetGraffitiRowPacket.Handler::handle);
	}
}
