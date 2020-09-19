package com.silvaniastudios.graffiti.network;

import java.util.function.Supplier;

import com.silvaniastudios.graffiti.drawables.PixelGridDrawable;
import com.silvaniastudios.graffiti.tileentity.ContainerGraffiti;
import com.silvaniastudios.graffiti.util.GraffitiUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class ModifyGridPacket {
	
	int size;
	int transparency;
	
	boolean rescale;
	
	public ModifyGridPacket(int size, int transparency, boolean rescale) {
		this.size = size;
		this.transparency = transparency;
		
		this.rescale = rescale;
	}
	
	public static void encode(ModifyGridPacket pkt, PacketBuffer buf) {
		buf.writeInt(pkt.size);
		buf.writeInt(pkt.transparency);
		buf.writeBoolean(pkt.rescale);
	}
	
	public static ModifyGridPacket decode(PacketBuffer buf) {
		return new ModifyGridPacket(
				buf.readInt(), //size
				buf.readInt(), //transparency
				buf.readBoolean() //resize;
				);
	}
	
	public static class Handler {
		public static void handle(final ModifyGridPacket msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				Container ctr = player.openContainer;
				
				if (ctr instanceof ContainerGraffiti) {
					ContainerGraffiti container = (ContainerGraffiti) ctr;
					System.out.println("canvas packet in: " + msg.size + ", " + msg.transparency);
					if (!container.te.isLocked()) {
						if (container.graffiti.pixelGrid == null || container.graffiti.pixelGrid.getSize() == 0) {
							System.out.println("empty grid, make a new one");
							if (msg.size == 16 || msg.size == 32 || msg.size == 64 || msg.size == 128) {
								container.graffiti.pixelGrid = new PixelGridDrawable(msg.size);
								container.graffiti.pixelGrid.setTransparency(msg.transparency);
								player.sendMessage(new StringTextComponent("Added new " + msg.size + "x" + msg.size + " pixel grid"));
							} else {
								System.out.println("Malformed canvas editing packet received from " + player.getDisplayName());
							}
						} else if ((msg.size == 16 || msg.size == 32 || msg.size == 64 || msg.size == 128)) {
							System.out.println("set transparency");
							container.graffiti.pixelGrid.setTransparency(msg.transparency);
							if (msg.size != container.graffiti.pixelGrid.getSize()) {
								System.out.println("modify grid size");
								container.graffiti.pixelGrid.setTransparency(msg.transparency);
								container.graffiti.pixelGrid.setNewGrid(msg.size, GraffitiUtils.rescaleMultiple(container.graffiti.pixelGrid.getPixelGrid(), msg.size, msg.rescale));
								player.sendMessage(new StringTextComponent("Pixel grid resized."));
							}
						} else if (msg.size == 0) {
							System.out.println("wipe grid");
							container.graffiti.pixelGrid = new PixelGridDrawable(0);
							container.te.update();
							
							NetworkHooks.openGui((ServerPlayerEntity) player, container.te, container.te.getPos());
							player.sendMessage(new StringTextComponent("Pixel grid removed."));
						}
						container.te.update();
					}
				}
			});
			
			ctx.get().setPacketHandled(true);
		}
	}
}
