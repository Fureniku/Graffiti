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
	}
}