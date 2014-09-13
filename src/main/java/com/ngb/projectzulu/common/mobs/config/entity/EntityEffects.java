package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChunkCoordinates;
import com.google.common.base.Optional;

public class EntityEffects {
	public final EntityConfigurationAnimal entity;
	public final FlightNavigator flightNav;
	public final EntityHome tether;
	private Optional<EntityTame> tame;
	private Optional<ChunkCoordinates> aerialTarget;

	public void setTamed(EntityTame tame) {
		this.tame = tame != null ? Optional.of(tame) : Optional.<EntityTame> absent();
	}

	public Optional<EntityTame> getTamed() {
		return tame;
	}

	public EntityEffects(EntityConfigurationAnimal entity) {
		this.entity = entity;
		this.tether = new EntityHome(entity);
		this.flightNav = new FlightNavigator(entity);
		this.tame = Optional.absent();
	}

	public void setDataWatcher() {
		entity.getDataWatcher();
	}

	public Optional<EntityLivingBase> getAttackTaget() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null ? Optional.of(target) : Optional.<EntityLivingBase> absent();
	}

	public boolean isGrounded() {
		return entity.getDataWatcher().getWatchableObjectByte(17) != 0;
	}

	public boolean setMovementTarget(Entity entityTarget) {
		if (entityTarget != null) {
			if (isGrounded()) {
				return entity.getNavigator().setPath(entity.getNavigator().getPathToEntityLiving(entityTarget),
						getBaseSpeed());
			} else {
				aerialTarget = Optional.of(new ChunkCoordinates((int) entityTarget.posX, (int) entityTarget.posY + 1,
						(int) entityTarget.posZ));
				return true;
			}
		} else {
			entity.getNavigator().clearPathEntity();
			aerialTarget = Optional.absent();
			return true;
		}
	}

	public boolean setMovementTarget(ChunkCoordinates chunkCoordinates) {
		if (chunkCoordinates != null) {
			if (isGrounded()) {
				return entity.getNavigator().setPath(
						entity.getNavigator().getPathToXYZ(chunkCoordinates.posX, chunkCoordinates.posY,
								chunkCoordinates.posZ), getBaseSpeed());
			} else {
				aerialTarget = Optional.of(new ChunkCoordinates(chunkCoordinates.posX, chunkCoordinates.posY + 1,
						chunkCoordinates.posZ));
				return true;
			}
		} else {
			entity.getNavigator().clearPathEntity();
			aerialTarget = Optional.absent();
			return true;
		}
	}

	public boolean tryMovementTarget(Entity entityTarget, float moveSpeed) {
		if (isGrounded()) {
			return entity.getNavigator().tryMoveToEntityLiving(entityTarget, moveSpeed);
		} else {
			aerialTarget = Optional.of(new ChunkCoordinates((int) entityTarget.posX, (int) entityTarget.posY + 1,
					(int) entityTarget.posZ));
			return true;
		}
	}

	public boolean tryMovementTarget(ChunkCoordinates chunkCoordinates, float moveSpeed) {
		if (isGrounded()) {
			return entity.getNavigator().tryMoveToXYZ(chunkCoordinates.posX, chunkCoordinates.posY,
					chunkCoordinates.posZ, moveSpeed);
		} else {
			aerialTarget = Optional.of(new ChunkCoordinates(chunkCoordinates.posX, chunkCoordinates.posY + 1,
					chunkCoordinates.posZ));
			return true;
		}
	}

	public boolean hasPath() {
		if (isGrounded()) {
			return !entity.getNavigator().noPath();
		} else {
			return !flightNav.atTargetPosition() && flightNav.isTargetPositionValid();
		}
	}

	public boolean attackEntityAsMob(Entity target) {
		// TODO: Send Packet to Client with Animation
		return entity.attackEntityAsMob(target);
	}

	public float getBaseSpeed() {
		return 1.0f;
	}

	public int getGrowingAge() {
		return entity.getDataWatcher().getWatchableObjectInt(12);
	}

	public void setGrowingAge(int par1) {
		entity.getDataWatcher().updateObject(12, Integer.valueOf(par1));
	}
}