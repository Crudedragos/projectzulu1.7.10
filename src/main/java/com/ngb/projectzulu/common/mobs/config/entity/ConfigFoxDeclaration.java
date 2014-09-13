package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.HashSet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;

import com.ngb.projectzulu.common.api.CustomMobData;
import com.ngb.projectzulu.common.api.ItemList;
import com.ngb.projectzulu.common.core.ConfigHelper;
import com.ngb.projectzulu.common.core.DefaultProps;
import com.ngb.projectzulu.common.core.ItemGenerics;
import com.ngb.projectzulu.common.core.entitydeclaration.EntityProperties;
import com.ngb.projectzulu.common.core.entitydeclaration.SpawnableDeclaration;
import com.ngb.projectzulu.common.mobs.entity.EntityFox;
import com.ngb.projectzulu.common.mobs.models.ModelFox;
import com.ngb.projectzulu.common.mobs.renders.RenderTameable;
import com.ngb.projectzulu.common.mobs.renders.RenderWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigFoxDeclaration extends SpawnableDeclaration {

	public ConfigFoxDeclaration() {
		super("ConfigFox", 9, EntityConfigFox.class, EnumCreatureType.creature);
		setSpawnProperties(10, 100, 1, 3);
		setRegistrationProperties(128, 3, true);
		setDropAmount(0, 2);

		eggColor1 = (204 << 16) + (132 << 8) + 22;
		eggColor2 = (224 << 16) + (224 << 8) + 224;
	}

	@Override
	public void outputDataToList(Configuration config, CustomMobData customMobData) {
		ConfigHelper.configDropToMobData(config, "MOB CONTROLS." + mobName, customMobData, Items.beef, 0, 5);
		ConfigHelper.configDropToMobData(config, "MOB CONTROLS." + mobName, customMobData, ItemList.furPelt, 0, 10);
		ConfigHelper.configDropToMobData(config, "MOB CONTROLS." + mobName, customMobData, ItemList.scrapMeat, 0, 15);
		ConfigHelper.configDropToMobData(config, "MOB CONTROLS." + mobName, customMobData,
				ItemList.genericCraftingItems, ItemGenerics.Properties.SmallHeart.meta(), 4);
		customMobData.entityProperties = new EntityProperties(12f, 2.0f, 0.3f, 100f).createFromConfig(config, mobName);
		super.outputDataToList(config, customMobData);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderWrapper getEntityrender(Class<? extends EntityLivingBase> entityClass) {
		return new RenderConfigTameable(new ModelConfigFox(), 0.5f, new ResourceLocation(DefaultProps.mobKey, "fox.png"));
	}

	@Override
	public HashSet<String> getDefaultBiomesToSpawn() {
		HashSet<String> defaultBiomesToSpawn = new HashSet<String>();
		return defaultBiomesToSpawn;
	}
}
