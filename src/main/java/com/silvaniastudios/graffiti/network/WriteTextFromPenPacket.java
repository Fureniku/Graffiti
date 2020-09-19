package com.silvaniastudios.graffiti.network;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.tileentity.TileEntityGraffiti;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class WriteTextFromPenPacket {

	String text;
	BlockPos pos;
	short posX;
	short posY;
	float scale;
	int col;
	short rot;
	String format;
	short alignment;
	int editId;
	boolean delete;
	int side;
	
	public WriteTextFromPenPacket(String text, BlockPos pos, short x, short y, float scale, int col, short rot, String format, short alignment, int side) {
		this.text = text;
		this.pos = pos;
		this.posX = x;
		this.posY = y;
		this.scale = scale;
		this.col = col;
		this.rot = rot;
		this.format = format;
		this.alignment = alignment;
		this.side = side;
	}
	
	public static void encode(WriteTextFromPenPacket pkt, PacketBuffer buf) {
		buf.writeString(pkt.text);
		buf.writeBlockPos(pkt.pos);
		buf.writeShort(pkt.posX);
		buf.writeShort(pkt.posY);
		buf.writeFloat(pkt.scale);
		buf.writeInt(pkt.col);
		buf.writeShort(pkt.rot);
		
		buf.writeString(pkt.format);
		buf.writeShort(pkt.alignment);
		buf.writeInt(pkt.side);
	}
	
	public static WriteTextFromPenPacket decode(PacketBuffer buf) {
		return new WriteTextFromPenPacket(buf.readString(50), //text
				buf.readBlockPos(), //block pos
				buf.readShort(),  //posX
				buf.readShort(),  //posY
				buf.readFloat(), //scale
				buf.readInt(),  //colour
				buf.readShort(), //rotation
				buf.readString(10), //format
				buf.readShort(), //alignment
				buf.readInt() //direction
		);
	}
	
	public static class Handler {
		public static void handle(final WriteTextFromPenPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				World world = ctx.get().getSender().world;
				BlockPos pos = msg.pos;
				Direction side = Direction.byIndex(msg.side);
				
				if (world.getTileEntity(pos) instanceof TileEntityGraffiti) {
					TileEntityGraffiti te = (TileEntityGraffiti) world.getTileEntity(pos);
					CompleteGraffitiObject graffiti = te.getGraffitiForFace(side);
					
					if (graffiti == null) {
						graffiti = new CompleteGraffitiObject(side, null, new ArrayList<TextDrawable>());
						te.assignGraffiti(graffiti, side);
					}
					graffiti.writeText(new TextDrawable(msg.text, msg.posX, msg.posY, msg.col, msg.scale, msg.rot, msg.format, msg.alignment), te);
					te.update();
				}
			});
			
			ctx.get().setPacketHandled(true);
			
		}
	}
}
