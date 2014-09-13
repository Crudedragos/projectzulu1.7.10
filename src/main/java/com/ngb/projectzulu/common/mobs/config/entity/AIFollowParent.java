package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.List;

import net.minecraft.entity.Entity;

import com.google.common.base.Optional;

public class AIFollowParent extends AIBase {
	private EntityEffects effects;
	private float speedModifier;
	private float disToStartFollow; // Distance away from owner entity must be to start following
	private float disToStopFollow; // Once following, distance to Owner entity must be to stop

	private EntityConfigurationAnimal parent;
	private int changeDirectionCooldown;

	public AIFollowParent(EntityEffects effects, float speedModifier) {
		this(effects, speedModifier, 9.0f, 9.0f);
	}

	public AIFollowParent(EntityEffects effects, float speedModifier, float disToStartFollow, float disToStopFollow) {
		this.effects = effects;
		this.speedModifier = speedModifier;
		this.disToStartFollow = disToStartFollow;
		this.disToStopFollow = disToStopFollow;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (effects.getGrowingAge() < 0) {
			Optional<EntityConfigurationAnimal> parent = getParent();
			return parent.isPresent()
					&& effects.entity.getDistanceSqToEntity(parent.get()) < disToStartFollow * disToStartFollow;
		}
		return false;
	}

	// Parent is the CLOSEST ADULT entity
	private Optional<EntityConfigurationAnimal> getParent() {
		List nearbyEntities = effects.entity.worldObj.getEntitiesWithinAABB(effects.entity.getClass(),
				effects.entity.boundingBox.expand(8.0D, 4.0D, 8.0D));
		EntityConfigurationAnimal closestEntity = null;
		float closestDistance = -1f;
		for (Object obj : nearbyEntities) {
			EntityConfigurationAnimal entity = (EntityConfigurationAnimal) obj;
			if (entity.getEntityEffects().getGrowingAge() >= 0) {
				float distance = entity.getDistanceToEntity(effects.entity);
				if (closestDistance != -1 && distance < closestDistance) {
					closestEntity = entity;
					closestDistance = distance;
				}
			}
		}
		return closestEntity != null ? Optional.of(closestEntity) : Optional.<EntityConfigurationAnimal> absent();
	}

	@Override
	public void startExecuting() {
		changeDirectionCooldown = 0;
		parent = getParent().get();
	}

	@Override
	public boolean continueExecuting() {
		return parent.isEntityAlive() && effects.hasPath()
				&& effects.entity.getDistanceSqToEntity(parent) > disToStopFollow * disToStopFollow;
	}

	@Override
	public void resetTask() {
		parent = null;
		effects.setMovementTarget((Entity) null);
	}

	@Override
	public void updateTask() {
		if (--changeDirectionCooldown <= 0) {
			changeDirectionCooldown = 10; // TODO Configurable
			boolean moved = effects.tryMovementTarget(parent, effects.getBaseSpeed() * speedModifier);
		}
	}
}