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
		setMutex(1);
	}

	@Override
	public boolean shouldExecute() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		return target.isPresent() && targetFilter.isEntityApplicable(target.get()) && target.get().isEntityAlive()
				&& canAttack();
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	private boolean canAttack() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		return target.isPresent()
				&& attackTick <= 0
				&& effects.entity.getDistanceSq(target.get().posX, target.get().boundingBox.minY, target.get().posZ) <= distanceToAttack;
	}

	@Override
	public void resetTask() {
		attackTick = 0;
	}

	@Override
	public void updateTask() {
		Optional<EntityLivingBase> target = effects.getAttackTaget();
		/* Decrement attackTick and try to attack if target is close enough */
		attackTick = Math.max(attackTick - 1, 0);
		if (canAttack()) {
			attackTick = 20;
			if (effects.entity.getHeldItem() != null) {
				effects.entity.swingItem();
			}
			effects.attackEntityAsMob(target.get());
		}
	}
}