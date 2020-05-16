package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.TextDrawable;
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
	float scale;
	int col;
	int rot;
	
	public WriteTextPacket(String text, BlockPos pos, int x, int y, float scale, int col, int rot) {
		this.text = text;
		this.blockX = pos.getX();
		this.blockY = pos.getY();
		this.blockZ = pos.getZ();
		this.posX = x;
		this.posY = y;
		this.scale = scale;
		this.col = col;
		this.rot = rot;
	}
	
	public static void encode(WriteTextPacket pkt, PacketBuffer buf) {
		buf.writeString(pkt.text);
		
		buf.writeInt(pkt.blockX);
		buf.writeInt(pkt.blockY);
		buf.writeInt(pkt.blockZ);
		
		buf.writeInt(pkt.posX);
		buf.writeInt(pkt.posY);
		buf.writeFloat(pkt.scale);
		buf.writeInt(pkt.col);
		buf.writeInt(pkt.rot);
	}
	
	public static WriteTextPacket decode(PacketBuffer buf) {
		return new WriteTextPacket(buf.readString(50), //text
				new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), //block pos
				buf.readInt(),  //posX
				buf.readInt(),  //posY
				buf.readFloat(), //scale
				buf.readInt(),  //colour
				buf.readInt()); //rotation
	}
	
	public static class Handler {
		public static void handle(final WriteTextPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = new BlockPos(msg.blockX, msg.blockY, msg.blockZ);
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					
					te.writeText(new TextDrawable(msg.text, msg.posX, msg.posY, msg.col, msg.scale, msg.rot));
				}
			});
			
			ctx.get().setPacketHandled(true);
			
		}
	}
}
