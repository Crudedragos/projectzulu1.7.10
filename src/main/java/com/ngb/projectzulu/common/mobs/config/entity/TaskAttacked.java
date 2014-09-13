package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.util.DamageSource;

public interface TaskAttacked {
	public boolean hurtBy(DamageSource damageSource, float par2);
	public void nearbyAttacked(EntityConfigurationAnimal nearby, DamageSource attacker, float par2);
}
