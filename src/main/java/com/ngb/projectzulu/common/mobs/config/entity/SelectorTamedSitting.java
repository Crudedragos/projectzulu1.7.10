package com.ngb.projectzulu.common.mobs.config.entity;

import com.ngb.projectzulu.common.mobs.config.entity.EntityTame.Command;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class SelectorTamedSitting implements IEntitySelector {

	@Override
	public boolean isEntityApplicable(Entity entity) {
		if (entity instanceof EntityConfigurationAnimal && !entity.isInWater() && entity.onGround) {
			EntityEffects effects = ((EntityConfigurationAnimal) entity).getEntityEffects();
			return effects.getTamed().isPresent() ? effects.getTamed().get().getCommand() == Command.SITTING : false;
		}
		return false;
	}
}
