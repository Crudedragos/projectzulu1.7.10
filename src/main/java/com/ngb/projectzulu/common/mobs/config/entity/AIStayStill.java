package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.command.IEntitySelector;

//AI Used to make an entity NOT move during some condition. For example, tamed Fox must not move when sitting
public class AIStayStill extends AIBase {
	private EntityEffects effects;
	private IEntitySelector triggerFilter;

	public AIStayStill(EntityEffects effects, IEntitySelector triggerFilter) {
		this.effects = effects;
		this.triggerFilter = triggerFilter;
	}

	@Override
	public boolean shouldExecute() {
		return triggerFilter.isEntityApplicable(effects.entity);
	}

	@Override
	public void startExecuting() {
		effects.entity.getNavigator().clearPathEntity();
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
		super.resetTask();
	}

	@Override
	public void updateTask() {
	}
}
