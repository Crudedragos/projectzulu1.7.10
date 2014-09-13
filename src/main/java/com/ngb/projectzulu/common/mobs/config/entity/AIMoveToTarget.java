package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;

import com.google.common.base.Optional;

public class AIMoveToTarget extends AIBase {
	private EntityEffects effects;
	private IEntitySelector targetFilter;
	private boolean continuousPathing;
	private float speedModifier;

	private int changeDirectionCooldown = 0;

	public AIMoveToTarget(EntityEffects entityEffects, float speedModifier, IEntitySelector targetFilter) {
		this(entityEffects, speedModifier, false, targetFilter);
	}

	public AIMoveToTarget(EntityEffects entityEffects, float speedModifier,
			boolean continuousPathing, IEntitySelector targetFilter) {
		this.effects = entityEffects;
		this.targetFilter = targetFilter;
		this.speedModifier = speedModifier;
		this.continuousPathing = continuousPathing;
		setMutex(1);
	}

	@Override
	public boolean shouldExecute() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		if (target.isPresent() && targetFilter.isEntityApplicable(target.get())) {
			PathEntity path = effects.entity.getNavigator().getPathToEntityLiving(target.get());
			boolean result = effects.isGrounded() ? effects.entity.getNavigator().getPathToEntityLiving(target.get()) != null
					: true;
			return result;
		} else {
			return false;
		}
	}

	@Override
	public void startExecuting() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		effects.setMovementTarget(target.get());
		// effects.getBaseSpeed() * speedModifier
	}

	@Override
	public boolean continueExecuting() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		if (target.isPresent() && target.get().isEntityAlive()) {
			if (!continuousPathing) {
				return effects.hasPath();
			} else {
				return effects.tether.isWithinHomeDistance(MathHelper.floor_double(target.get().posX),
						MathHelper.floor_double(target.get().posY), MathHelper.floor_double(target.get().posZ));
			}
		}
		return false;
	}

	@Override
	public void resetTask() {
		effects.setMovementTarget((Entity) null);
	}

	@Override
	public void updateTask() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		effects.entity.getLookHelper().setLookPositionWithEntity(target.get(), 30.0F, 30.0F);

		/* Check if Entity can See Target and if ChangeDirectionCooldown is down so we should Search for a new Path */
		if ((continuousPathing || effects.entity.getEntitySenses().canSee(target.get()))
				&& --changeDirectionCooldown <= 0) {
			changeDirectionCooldown = 4 + effects.entity.getRNG().nextInt(7);
			effects.tryMovementTarget(target.get(), effects.getBaseSpeed() * speedModifier);
		}
	}
}