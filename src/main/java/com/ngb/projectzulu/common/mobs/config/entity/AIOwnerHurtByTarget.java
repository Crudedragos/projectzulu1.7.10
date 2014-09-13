package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Optional;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AIOwnerHurtByTarget extends AITarget {
	private EntityEffects effects;
	private int previousRevengeTimer;

	public AIOwnerHurtByTarget(EntityEffects entityEffects) {
		this(entityEffects, 16.0f, true);
	}

	public AIOwnerHurtByTarget(EntityEffects entityEffects, float targetDistance, boolean checkSight) {
		super(entityEffects.entity, targetDistance, checkSight, false);
		this.effects = entityEffects;
		setMutex(2);
	}

	@Override
	public boolean shouldExecute() {
		Optional<EntityPlayer> owner = getOwner();
		if (owner.isPresent()) {
			EntityLivingBase target = owner.get().getAITarget();
			return owner.get().func_142015_aE() != previousRevengeTimer
					&& isSuitableTarget(owner.get().getAITarget(), false);
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
		previousRevengeTimer = owner.get().func_142015_aE();
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
	public AIOwnerHurtByTarget setMutex(int mutex) {
		setMutexBits(mutex);
		return this;
	}
}