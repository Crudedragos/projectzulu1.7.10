package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;

public class AIPanic extends AIBase implements TaskAttacked {
	private EntityEffects effects;
	private int chanceToPanic;
	private float speedModifier;
	private IEntitySelector notifySelector;

	private int panicTime = 0;
	private int directionChangeCooldown = 0;
	private ChunkCoordinates targetPosition;

	public AIPanic(EntityEffects effects, float speedModifier, int chanceToPanic, IEntitySelector notifySelector) {
		this.effects = effects;
		this.speedModifier = speedModifier;
		this.chanceToPanic = chanceToPanic;
		this.notifySelector = notifySelector;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return panicTime > 0;
	}

	@Override
	public void startExecuting() {
		if (directionChangeCooldown == 0 || !effects.hasPath()) {
			targetPosition = getRandomPosition();
			effects.setMovementTarget(getRandomPosition());
		}
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
		directionChangeCooldown = 0;
	}

	@Override
	public void updateTask() {
		--panicTime;
		if (--directionChangeCooldown <= 0) {
			directionChangeCooldown = 4 + effects.entity.getRNG().nextInt(7);
			effects.tryMovementTarget(targetPosition, effects.getBaseSpeed() * speedModifier);
		}
		effects.tryMovementTarget(targetPosition, effects.getBaseSpeed() * speedModifier);
	}

	@Override
	public boolean hurtBy(DamageSource damageSource, float par2) {
		if (chanceToPanic >= 0 && effects.entity.worldObj.rand.nextInt(100) - chanceToPanic <= 0) {
			panicTime = 20;
			List entityList = effects.entity.worldObj.getEntitiesWithinAABBExcludingEntity(effects.entity,
					effects.entity.boundingBox.expand(20.0D, 20.0D, 20.0D), notifySelector);
			Iterator nearbyEntityIterator = entityList.iterator();

			while (nearbyEntityIterator.hasNext()) {
				Entity nearbyEntity = (Entity) nearbyEntityIterator.next();
				// nearbyEntity.getClass().equals(this.getClass())
				if (nearbyEntity instanceof EntityConfigurationAnimal) {
					EntityConfigurationAnimal nearbyAlly = (EntityConfigurationAnimal) nearbyEntity;
					nearbyAlly.nearbyAttack(effects.entity, damageSource, par2);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void nearbyAttacked(EntityConfigurationAnimal nearby, DamageSource attacker, float par2) {
		if (chanceToPanic >= 0 && effects.entity.worldObj.rand.nextInt(100) - chanceToPanic <= 0) {
			panicTime = 20;
		}
	}
}