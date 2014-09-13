package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import com.google.common.base.Optional;

public class AILookIdle extends AIBase {
	private EntityEffects effects;
	private float chanceToWatch;

	private int lookTime;
	private Vec3 lookPosition;
	private IEntitySelector searchFilter;

	public AILookIdle(EntityEffects effects, float chanceToWatch) {
		this.effects = effects;
		this.chanceToWatch = chanceToWatch;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return effects.entity.getRNG().nextFloat() < chanceToWatch;
	}

	@Override
	public void startExecuting() {
		double d0 = (Math.PI * 2D) * effects.entity.getRNG().nextDouble();
		lookPosition = Vec3.createVectorHelper(effects.entity.posX + Math.cos(d0), effects.entity.posY,
				effects.entity.posZ + Math.sin(d0));
		lookTime = 20 + effects.entity.getRNG().nextInt(20);
	}

	@Override
	public boolean continueExecuting() {
		return lookTime >= 0;
	}

	@Override
	public void resetTask() {
		lookTime = 0;
	}

	@Override
	public void updateTask() {
		effects.entity.getLookHelper().setLookPosition(lookPosition.xCoord, lookPosition.yCoord, lookPosition.zCoord,
				10.0f, (float) effects.entity.getVerticalFaceSpeed());
		--this.lookTime;
	}
}