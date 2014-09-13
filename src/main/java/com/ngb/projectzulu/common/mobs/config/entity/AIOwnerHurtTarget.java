package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.base.Optional;

public class AIOwnerHurtTarget extends AITarget {
	private EntityEffects effects;
	private int prevLastAttackTime;

	public AIOwnerHurtTarget(EntityEffects entityEffects) {
		this(entityEffects, 16.0f, true);
	}

	public AIOwnerHurtTarget(EntityEffects entityEffects, float targetDistance, boolean checkSight) {
		super(entityEffects.entity, targetDistance, checkSight, false);
		this.effects = entityEffects;
		setMutex(2);
	}

	@Override
	public boolean shouldExecute() {
		Optional<EntityPlayer> owner = getOwner();
		if (owner.isPresent()) {
			return owner.get().getLastAttackerTime() != prevLastAttackTime
					&& isSuitableTarget(owner.get().getLastAttacker(), false);
		}
		return false;
	}

	private Optional<EntityPlayer> getOwner() {
		if (effects.getTamed().isPresent()) {
			EntityPlayer owner = effects.entity.worldObj.func_152378_a(effects.getTamed().get().getOwner());
			return owner != null ? Optional.of(owner) : Optional.<EntityPlayer> absent();
		}
		return Optional.<EntityPlayer> absent();
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		Optional<EntityPlayer> owner = getOwner();

		effects.entity.setAttackTarget(owner.get().getAITarget());
		prevLastAttackTime = owner.get().getLastAttackerTime();
	}

	@Override
	public boolean continueExecuting() {
		return super.continueExecuting();
	}

	@Override
	public void resetTask() {
		super.resetTask();
	}

	@Override
	public void updateTask() {
	}

	@Override
	public AIOwnerHurtTarget setMutex(int mutex) {
		setMutexBits(mutex);
		return this;
	}
}