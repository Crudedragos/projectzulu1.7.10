package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import com.google.common.base.Optional;

public class AIWatchClosest extends AIBase {
	private EntityEffects effects;

	private float maxWatchDis;
	private float chanceToWatch;
	private IEntitySelector searchFilter;

	private int lookTime;
	protected Entity watchableEntity;

	public AIWatchClosest(EntityEffects effects, float maxWatchDis, float chanceToWatch, IEntitySelector watchableFilter) {
		this.effects = effects;
		this.maxWatchDis = maxWatchDis;
		this.chanceToWatch = chanceToWatch;
		this.searchFilter = watchableFilter;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return effects.entity.getRNG().nextFloat() < chanceToWatch && getNearestWatchable().isPresent();
	}

	public Optional<Entity> getNearestWatchable() {
		Entity nearestWatchable = null;
		float nearestDistance = -1;
		for (Object obj : effects.entity.worldObj.playerEntities) {
			Entity player = (Entity) obj;
			if (searchFilter.isEntityApplicable(player)) {
				float distance = effects.entity.getDistanceToEntity(player);
				if (nearestDistance != -1 && distance < nearestDistance) {
					nearestWatchable = player;
				}
			}
		}

		List entities = effects.entity.worldObj.getEntitiesWithinAABBExcludingEntity(effects.entity,
				effects.entity.boundingBox.expand((double) maxWatchDis, 3.0D, (double) maxWatchDis), searchFilter);
		for (Object obj : entities) {
			Entity entity = (Entity) obj;
			if (searchFilter.isEntityApplicable(entity)) {
				float distance = effects.entity.getDistanceToEntity(entity);
				if (nearestDistance != -1 && distance < nearestDistance) {
					nearestWatchable = entity;
				}
			}
		}
		return nearestWatchable != null ? Optional.of(nearestWatchable) : Optional.<Entity> absent();
	}

	@Override
	public void startExecuting() {
		watchableEntity = getNearestWatchable().get();
		lookTime = 40 + effects.entity.getRNG().nextInt(40);
		effects.setMovementTarget(watchableEntity);
	}

	@Override
	public boolean continueExecuting() {
		return !watchableEntity.isEntityAlive() ? false
				: (effects.entity.getDistanceSqToEntity(watchableEntity) > (double) (maxWatchDis * maxWatchDis) ? false
						: lookTime > 0);
	}

	@Override
	public void resetTask() {
		watchableEntity = null;
	}

	@Override
	public void updateTask() {
		effects.entity.getLookHelper().setLookPositionWithEntity(watchableEntity, 10.0F,
				(float) effects.entity.getVerticalFaceSpeed());
		--this.lookTime;
	}
}