package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AINearestAttackableTarget extends AITarget {
	private EntityEffects effects;

	private Class<?> targetClass;
	private IEntitySelector entitySelector;

	// Chance that a potential target is selected
	private int targetChance;
	private EntityLivingBase targetEntity;

	public AINearestAttackableTarget(EntityEffects entityEffects, Class<?> targetClass) {
		this(entityEffects, targetClass, new SimpleSelector(targetClass), 16.0f, true);
	}

	public AINearestAttackableTarget(EntityEffects entityEffects, Class<?> targetClass, IEntitySelector entitySelector,
			float targetDistance, boolean checkSight) {
		super(entityEffects.entity, targetDistance, checkSight, false);
		this.effects = entityEffects;
		this.targetClass = targetClass;
		this.entitySelector = entitySelector;
		setMutex(2);
	}

	@Override
	public boolean shouldExecute() {
		if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) == 0) {
			if (targetClass == EntityPlayer.class) {
				EntityPlayer target = taskOwner.worldObj.getClosestVulnerablePlayerToEntity(taskOwner,
						targetDistance);

				if (isSuitableTarget(target, false)) {
					targetEntity = target;
					return true;
				}
			} else {
				List entityList = taskOwner.worldObj.selectEntitiesWithinAABB(this.targetClass,
						taskOwner.boundingBox.expand(targetDistance, 4.0D, targetDistance),
						entitySelector);
				Collections.sort(entityList, new ComparatorNearbyEntity(effects.entity));
				Iterator iterator = entityList.iterator();
				while (iterator.hasNext()) {
					Entity entity = (Entity) iterator.next();
					EntityLiving target = (EntityLiving) entity;
					if (isSuitableTarget(target, false)) {
						targetEntity = target;
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		this.taskOwner.setAttackTarget(targetEntity);
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
	public AINearestAttackableTarget setMutex(int mutex) {
		setMutexBits(mutex);
		return this;
	}
}
