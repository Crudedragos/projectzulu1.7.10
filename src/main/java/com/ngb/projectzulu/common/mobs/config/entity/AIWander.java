package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

public class AIWander extends AIBase {
	private EntityEffects effects;
	private float modifierSpeed;
	private int chanceToMove;

	private ChunkCoordinates targetPosition;

	public AIWander(EntityEffects effects, float modifierSpeed, int chanceToMove) {
		this.effects = effects;
		this.modifierSpeed = modifierSpeed;
		this.chanceToMove = chanceToMove;
	}

	@Override
	public boolean shouldExecute() {
		return effects.entity.getRNG().nextInt(chanceToMove) == 0;
	}

	@Override
	public void startExecuting() {
		targetPosition = getRandomPosition();
	}

	private ChunkCoordinates getRandomPosition() {
		Vec3 var1 = RandomPositionGenerator.findRandomTarget(effects.entity, 5, 4);
		return new ChunkCoordinates((int) var1.xCoord, (int) var1.yCoord, (int) var1.zCoord);
	}

	@Override
	public boolean continueExecuting() {
		return effects.hasPath();
	}

	@Override
	public void resetTask() {
		targetPosition = null;
		effects.setMovementTarget((ChunkCoordinates) null);
	}

	@Override
	public void updateTask() {
		effects.tryMovementTarget(targetPosition, effects.getBaseSpeed() * modifierSpeed);
	}
}
