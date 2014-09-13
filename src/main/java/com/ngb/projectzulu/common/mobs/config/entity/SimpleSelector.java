package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class SimpleSelector implements IEntitySelector {
	private Class<?> targetClass;

	public SimpleSelector(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public boolean isEntityApplicable(Entity entity) {
		return targetClass.isAssignableFrom(entity.getClass());
	}
}
