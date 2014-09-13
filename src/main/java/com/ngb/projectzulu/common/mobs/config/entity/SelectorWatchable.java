package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class SelectorWatchable implements IEntitySelector {

	@Override
	public boolean isEntityApplicable(Entity entity) {
		return entity.isEntityAlive();
	}

	public static class SelectPlayerWatchable extends SelectorWatchable {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return EntityPlayer.class.isAssignableFrom(entity.getClass()) && super.isEntityApplicable(entity);
		}
	}

	public static class SelectWatchableTarget extends SelectorWatchable {
		private EntityLiving watcher;

		public SelectWatchableTarget(EntityLiving watcher) {
			this.watcher = watcher;
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			return watcher.getAttackTarget() == entity && super.isEntityApplicable(entity);
		}
	}
}
