package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.ai.EntityAIBase;

public abstract class AIBase extends EntityAIBase {
	public AIBase setMutex(int mutex) {
		setMutexBits(mutex);
		return this;
	}
}
