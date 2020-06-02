package com.silvaniastudios.graffiti.tileentity;

import com.silvaniastudios.graffiti.Graffiti;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class GraffitiContainerTypes {
	
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(
			ForgeRegistries.CONTAINERS, Graffiti.MODID);
	
	public static final RegistryObject<ContainerType<ContainerGraffiti>> GRAFFITI = CONTAINER_TYPES.register("graffiti", () -> IForgeContainerType.create(ContainerGraffiti::new));

}
