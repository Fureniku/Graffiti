package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.GraffitiTextDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class WriteTextPacket {

	String text;
	int blockX;
	int blockY;
	int blockZ;
	int posX;
	int posY;
	int col;
	
	public WriteTextPacket(String text, BlockPos pos, int x, int y, int col) {
		System.out.println("Creating packet");
		this.text = text;
		this.blockX = pos.getX();
		this.blockY = pos.getY();
		this.blockZ = pos.getZ();
		this.posX = x;
		this.posY = y;
		this.col = col;
	}
	
	public static void encode(WriteTextPacket pkt, PacketBuffer buf) {
		System.out.println("encode");
		buf.writeString(pkt.text);
		
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		
		buf.writeInt(pkt.posX);
		buf.writeInt(pkt.posY);
		buf.writeInt(pkt.col);
	}
	
	public static WriteTextPacket decode(PacketBuffer buf) {
		System.out.println("decode");
		//							text										block position					text posX		text posY		text col
		return new WriteTextPacket(buf.readString(), new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt(), buf.readInt(), buf.readInt());
	}
	
	public static class Handler {
		public static void handle(final WriteTextPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				System.out.println("Got packet on server");
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					
					te.writeText(new GraffitiTextDrawable(msg.text, msg.posX, msg.posY, msg.col, 1));
				}
			});
			
			ctx.get().setPacketHandled(true);
			
		}
	}
}
