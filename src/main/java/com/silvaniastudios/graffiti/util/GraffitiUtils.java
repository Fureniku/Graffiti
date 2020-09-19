package com.silvaniastudios.graffiti.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GraffitiUtils {
	
	//Increase the canvas size, stretching so the original image appears identical to before the increase
	public static int[][] increaseCanvasResized(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 16) { 
			newSize = 32; 
		} else if (originalSize == 32) {
			newSize = 64;
		} else if (originalSize == 64) {
			newSize = 128;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				newCanvas[j][i] = canvasIn[j/2][i/2];
			}
		}
		
		return newCanvas;
	}
	
	//Increase a canvas size, keeping the original image in the top-left corner
	public static int[][] increaseCanvasCropped(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 16) { 
			newSize = 32; 
		} else if (originalSize == 32) {
			newSize = 64;
		} else if (originalSize == 64) {
			newSize = 128;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				boolean x = i < newSize/2;
				boolean y = j < newSize/2;
				
				if (x && y) {
					newCanvas[j][i] = canvasIn[j][i];
				} else {
					newCanvas[j][i] = 0;
				}
			}
		}
		
		return newCanvas;
	}
	
	//Decrease a canvas size, cropping to show what was previously the top-left quarter.
	public static int[][] decreaseCanvasCropped(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 32) { 
			newSize = 16; 
		} else if (originalSize == 64) {
			newSize = 32;
		} else if (originalSize == 128) {
			newSize = 64;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				newCanvas[j][i] = canvasIn[j][i];
			}
		}
		
		return newCanvas;
	}
	
	//Decrease the canvas size, compressing the image and taking the top-left pixel of each four pixel area to create a new image
	public static int[][] decreaseCanvasResized(int[][] canvasIn) {
		int originalSize = canvasIn.length;
		int newSize = 0;
		
		if (originalSize == 32) { 
			newSize = 16; 
		} else if (originalSize == 64) {
			newSize = 32;
		} else if (originalSize == 128) {
			newSize = 64;
		} else {
			return canvasIn;
		}
		
		int[][] newCanvas = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++) {
			for (int j = 0; j < newSize; j++) {
				newCanvas[j][i] = canvasIn[j*2][i*2];
			}
		}
		
		return newCanvas;
	}
	
	//Repeatedly scale up or down 
	public static int[][] rescaleMultiple(int[][] canvasIn, int target, boolean rescale) {
		int[][] newCanvas = canvasIn.clone();
		
		int resizeIterations = sizeToId(target) - sizeToId(canvasIn.length);
		
		if (resizeIterations > 0) {
			for (int i = 0; i < resizeIterations; i++) {
				if (rescale) {
					newCanvas = increaseCanvasResized(newCanvas);
				} else {
					newCanvas = increaseCanvasCropped(newCanvas);
				}
			}
		} else if (resizeIterations < 0) {
			resizeIterations = Math.abs(resizeIterations);
			
			for (int i = 0; i < resizeIterations; i++) {
				if (rescale) {
					newCanvas = decreaseCanvasResized(newCanvas);
				} else {
					newCanvas = decreaseCanvasCropped(newCanvas);
				}
			}
		}
		return newCanvas;
	}
	
	public static int sizeToId(int size) {
		if (size == 16) { return 0; }
		if (size == 32) { return 1; }
		if (size == 64) { return 2; }
		if (size == 128) { return 3; }
		return -1;
	}

	public static int idToSize(int id) {
		if (id == 0) { return 16; }
		if (id == 1) { return 32; }
		if (id == 2) { return 64; }
		if (id == 3) { return 128; }
		return 0;
	}
	
	public static boolean hasBold(String str) {
		return str.contains("§l");
	}
	
	public static boolean hasItalic(String str) {
		return str.contains("§o");
	}
	
	public static boolean hasUnderline(String str) {
		return str.contains("§n");
	}
	
	public static boolean hasStrikethrough(String str) {
		return str.contains("§m");
	}

	public static String rightClickActionString(int id) {
		if (id == 0) {
			return "Click-Through";
		}
		if (id == 2) {
			return "Display Art";
		}
		if (id == 3) {
			return "Open URL";
		}
		return "No Action";
	}
	
	public int[][] rotateClockwise(int[][] gridIn) {
		int[][] gridOut = new int[gridIn.length][gridIn.length];
		
		for (int i = 0; i < gridIn.length; i++) {
			for (int j = 0; j < gridIn.length; j++) {
				gridOut[i][j] = gridIn[j][i];
			}
		}
		return gridOut;
	}
	
	/*
	 * Ray trace is used for a lot of places where we need to check if players looking at a specific part of graffiti.
	 * This is taken from the Item class with slight modifications, and made available everywhere.
	*/
	public static BlockRayTraceResult rayTrace(World worldIn, PlayerEntity player) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		Vec3d vec3d = player.getEyePosition(1.0F);
		float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
		float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
		Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		System.out.println("tracing");
		return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
	}
}
