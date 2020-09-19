package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.GraffitiUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetGraffitiRowPacket {
	
	int[][] rows;
	int size;
	int packetId;
	
	public SetGraffitiRowPacket(int[][] rows, int size, int packetId) {
		this.rows = rows;
		this.size = size;
		this.packetId = packetId;
	}
	
	public static void encode(SetGraffitiRowPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.size);
		buf.writeInt(pkt.packetId);
		for (int i = 0; i < 16; i++) {
			buf.writeVarIntArray(pkt.rows[i + (16 * pkt.packetId)]);
		}
	}
	
	public static SetGraffitiRowPacket decode(PacketBuffer buf) {
		int size = buf.readInt();
		int packetId = buf.readInt();
		
		int[][] partialGrid = new int[size][16];
		
		for (int i = 0; i < 16; i++) {
			partialGrid[i] = buf.readVarIntArray();
		}
		
		int[][] gridOut = new int[partialGrid.length][16];
		
		for (int i = 0; i < partialGrid.length; i++) {
			for (int j = 0; j < 16; j++) {
				gridOut[i][j] = partialGrid[j][i];
			}
		}
		
		return new SetGraffitiRowPacket(gridOut, size, packetId);
	}
	
	public static class Handler {
		public static void handle(final SetGraffitiRowPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;
					PixelGridDrawable grid = container.graffiti.pixelGrid;

					if (grid == null) {
						grid = new PixelGridDrawable(msg.size);
					}
					
					if (grid.getSize() != msg.size) {
						grid.setNewGrid(msg.size, GraffitiUtils.rescaleMultiple(grid.getPixelGrid(), msg.size, true));
					}

					for (int i = 0; i < grid.getSize(); i++) {
						for (int j = 0; j < 16; j++) {
							grid.setPixelRaw(j + (16 * msg.packetId), 
									i, 
									msg.rows[i][j], 
									container.te);
						}
					}
					
					//System.out.println("Should be done. grid: " + grid.getSize());
					container.graffiti.pixelGrid = grid;
					container.te.assignGraffiti(container.graffiti, container.graffiti.getSide());
					container.te.update();
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}

}
