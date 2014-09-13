package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class DistanceSelector implements IEntitySelector {
	private Entity sourceEntity;
	private float distance;

	public DistanceSelector(Entity sourceEntity, float distance) {
		this.sourceEntity = sourceEntity;
		this.distance = distance;
	}

	@Override
	public boolean isEntityApplicable(Entity entity) {
		return sourceEntity.getDistanceToEntity(entity) < distance;
	}

	public static class EntityDistanceSelector implements IEntitySelector {
		private Entity sourceEntity;
		private float distance;

		public EntityDistanceSelector(Entity sourceEntity, float distance) {
			this.sourceEntity = sourceEntity;
			this.distance = distance;
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			return sourceEntity.getClass().equals(entity.getClass())
					&& sourceEntity.getDistanceToEntity(entity) < distance;
		}

	}
}
