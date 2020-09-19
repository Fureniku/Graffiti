package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.CompleteGraffitiObject;
import com.silvaniastudios.graffiti.drawables.TextDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class WriteTextPacket {

	String text;
	short posX;
	short posY;
	float scale;
	int col;
	short rot;
	String format;
	short alignment;
	short editId;
	boolean delete;
	
	public WriteTextPacket(String text, short x, short y, float scale, int col, short rot, String format, short alignment, short editId, boolean delete) {
		this.text = text;
		this.posX = x;
		this.posY = y;
		this.scale = scale;
		this.col = col;
		this.rot = rot;
		this.format = format;
		this.alignment = alignment;
		this.editId = editId;
		this.delete = delete;
	}
	
	public WriteTextPacket(String text, short x, short y, float scale, int col, short rot, String format, short alignment) {
		this(text, x, y, scale, col, rot, format, alignment, (short) -1, false);
	}
	
	public static void encode(WriteTextPacket pkt, PacketBuffer buf) {
		buf.writeString(pkt.text);
		
		buf.writeShort(pkt.posX);
		buf.writeShort(pkt.posY);
		buf.writeFloat(pkt.scale);
		buf.writeInt(pkt.col);
		buf.writeShort(pkt.rot);
		
		buf.writeString(pkt.format);
		buf.writeShort(pkt.alignment);
		buf.writeShort(pkt.editId);
		buf.writeBoolean(pkt.delete);
	}
	
	public static WriteTextPacket decode(PacketBuffer buf) {
		return new WriteTextPacket(buf.readString(50), //text
				buf.readShort(),  //posX
				buf.readShort(),  //posY
				buf.readFloat(), //scale
				buf.readInt(),  //colour
				buf.readShort(), //rotation
				buf.readString(10), //format
				buf.readShort(), //alignment
				buf.readShort(), //edit ID
				buf.readBoolean()); //delete
	}
	
	public static class Handler {
		public static void handle(final WriteTextPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;
					
					if (msg.editId >= 0 && msg.editId <= container.graffiti.textList.size()) {
						container.graffiti.textList.remove(msg.editId);
						container.te.update();
					}
					
					if (!msg.delete) {
						System.out.println("Text successfully received: " + msg.text);
						CompleteGraffitiObject g = container.te.getGraffitiForFace(container.graffiti.getSide());
						
						g.writeText(new TextDrawable(msg.text, msg.posX, msg.posY, msg.col, msg.scale, msg.rot, msg.format, msg.alignment), container.te);
						container.te.update();
						
						container.graffiti = container.te.getGraffitiForFace(container.graffiti.getSide());
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
			
		}
	}
}
