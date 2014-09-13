package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.EntityLiving;

import com.ngb.projectzulu.common.mobs.entityai.EntityAITarget;

public abstract class AITarget extends EntityAITarget {
	public AITarget(EntityLiving entityLiving, float targetDistance, boolean shouldCheckSight) {
		super(entityLiving, targetDistance, shouldCheckSight, false);
	}

	public AITarget(EntityLiving entityLiving, float targetDistance, boolean shouldCheckSight, boolean field_75303_a) {
		super(entityLiving, targetDistance, shouldCheckSight, field_75303_a);
	}

	public AITarget setMutex(int mutex) {
		setMutex(mutex);
		return this;
	}
}
