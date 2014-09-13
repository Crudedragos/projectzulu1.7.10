package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Optional;
import com.ngb.projectzulu.common.mobs.entity.EntityGenericCreature;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class AIAttackNearbyTarget extends AIBase {
	private EntityEffects effects;
	private IEntitySelector targetFilter;

	private int attackTick = 0;
	private int distanceToAttack = 20;

	public AIAttackNearbyTarget(EntityEffects entityEffects, IEntitySelector targetFilter) {
		this.effects = entityEffects;
		this.targetFilter = targetFilter;
		setMutex(4);
	}

	@Override
	public boolean shouldExecute() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		return target.isPresent() && targetFilter.isEntityApplicable(target.get()) && target.get().isEntityAlive()
				&& isWithinRange();
	}

	@Override
	public void startExecuting() {
		tryAttack();
	}

	@Override
	public boolean continueExecuting() {
		return attackTick > 0;
	}

	private boolean isWithinRange() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		return target.isPresent()
				&& effects.entity.getDistanceSq(target.get().posX, target.get().boundingBox.minY, target.get().posZ) <= distanceToAttack
				&& target.get().boundingBox.maxY > effects.entity.boundingBox.minY
				&& target.get().boundingBox.minY < effects.entity.boundingBox.maxY;
	}

	private void tryAttack() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		/* Decrement attackTick and try to attack if target is close enough */
		if (attackTick <= 0 && isWithinRange()) {
			attackTick = 20;
			if (effects.entity.getHeldItem() != null) {
				effects.entity.swingItem();
			}
			effects.attackEntityAsMob(target.get());
		}
	}

	@Override
	public void resetTask() {
		attackTick = -1;
	}

	@Override
	public void updateTask() {
		if (attackTick > 0) {
			--attackTick;
		}
		tryAttack();
	}
}