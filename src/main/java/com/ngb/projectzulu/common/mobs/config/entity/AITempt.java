package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

import com.google.common.base.Optional;
import com.ngb.projectzulu.common.mobs.config.entity.ItemFilter.SimpleItemFilter;

// Unlike vanilla counterpart doesn't contain isScaredOfMovement component, this should be implemented in an external task with a higher priority that masks AITemp if desired
public class AITempt extends AIBase {
	private EntityEffects effects;
	private float speedModifier;
	private float searchradius; // Radius to search for players/blocks for tempted item
	private ItemFilter tempting;

	private int delayTemptCounter = 0; // Controls how often an Entity can be Tempted, while > 0 task cannot be started
	private EntityPlayer tempter;

	public AITempt(EntityEffects effects, float speedModifier, float searchRadius, ItemFilter temptingItemFilter) {
		this.effects = effects;
		this.speedModifier = speedModifier;
		this.searchradius = 20;
		this.tempting = temptingItemFilter;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return (--delayTemptCounter > 0) ? false : searchForTempting().isPresent();
	}

	private Optional<EntityPlayer> searchForTempting() {
		float closetEffectiveDistance = 200f;
		EntityPlayer closestPlayer = null;
		for (Object obj : effects.entity.worldObj.playerEntities) {
			EntityPlayer player = (EntityPlayer) obj;
			ItemStack heldItem = player.inventory.getCurrentItem();
			if (tempting.isMatch(heldItem)) {
				float effectiveDisance = calcEffectiveDistance(effects.entity, player);
				if (effectiveDisance < searchradius && effectiveDisance < closetEffectiveDistance) {
					closestPlayer = player;
					closetEffectiveDistance = effectiveDisance;
				}
			}
		}
		return closestPlayer != null ? Optional.of(closestPlayer) : Optional.<EntityPlayer> absent();
	}

	private float calcEffectiveDistance(Entity e1, Entity e2) {
		// Distance to ValidTempting, horizontally should be favored slightly over vertically
		// effectiveDisance = Math.sqrt(Math.pow(a, b)) player.getDistanceSq(effects.entity);
		return e1.getDistanceToEntity(e2);
	}

	@Override
	public void startExecuting() {
		tempter = searchForTempting().get();
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
		// After Tempting is DONE, set to a number so entity cannot be immediately re-attempted
		delayTemptCounter = 30;
		effects.setMovementTarget((ChunkCoordinates) null);
	}

	@Override
	public void updateTask() {

		// stare into the soul of your tormentor
		effects.entity.getLookHelper().setLookPosition(tempter.posX, tempter.posY, tempter.posZ, 30.0F, 40f);
		if (effects.entity.getDistanceSq(tempter.posX, tempter.posY, tempter.posZ) < 6.25D) {
			effects.setMovementTarget((Entity) null);
		} else {
			effects.tryMovementTarget(tempter, effects.getBaseSpeed() * speedModifier);
		}
	}
}