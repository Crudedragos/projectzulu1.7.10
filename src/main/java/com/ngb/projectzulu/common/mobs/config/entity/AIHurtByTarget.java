package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;

public class AIHurtByTarget extends AITarget {
	private EntityEffects effects;
	private boolean shouldCallAllies;

	public AIHurtByTarget(EntityEffects entityEffects, boolean shouldCallAllies, boolean checkSight) {
		this(entityEffects, shouldCallAllies, 16.0f, checkSight);
	}

	public AIHurtByTarget(EntityEffects entityEffects, boolean shouldCallAllies, float targetDistance,
			boolean checkSight) {
		super(entityEffects.entity, targetDistance, checkSight, false);
		this.effects = entityEffects;
		this.shouldCallAllies = shouldCallAllies;
		setMutex(2);
	}

	@Override
	public boolean shouldExecute() {
		return isSuitableTarget(taskOwner.getAITarget(), false);
	}

	@Override
	public void startExecuting() {
		taskOwner.setAttackTarget(taskOwner.getAITarget());
		if (shouldCallAllies) {
			List nearby = taskOwner.worldObj.getEntitiesWithinAABB(
					taskOwner.getClass(),
					AxisAlignedBB.getBoundingBox(taskOwner.posX, taskOwner.posY, taskOwner.posZ, taskOwner.posX + 1.0D,
							taskOwner.posY + 1.0D, taskOwner.posZ + 1.0D).expand(this.targetDistance, 4.0D,
							this.targetDistance));
			Iterator iterator = nearby.iterator();

			while (iterator.hasNext()) {
				EntityLiving entity = (EntityLiving) iterator.next();
				if (taskOwner != entity && entity.getAttackTarget() == null) {
					entity.setAttackTarget(taskOwner.getAITarget());
				}
			}
		}
		super.startExecuting();
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
		taskOwner.setAttackTarget(null);
	}

	@Override
	public void updateTask() {
		startExecuting();
	}
}