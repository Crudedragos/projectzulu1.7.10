package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

import com.google.common.base.Optional;
import com.ngb.projectzulu.common.ProjectZulu_Core;
import com.ngb.projectzulu.common.core.PZPacket;
import com.ngb.projectzulu.common.mobs.config.entity.EntityConfigurationAnimal.DataWatcherKey;
import com.ngb.projectzulu.common.mobs.packets.PacketAnimTime;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityEffects {
	public final EntityConfigurationAnimal entity;
	public final FlightNavigator flightNav;
	public final EntityHome tether;
	private Optional<EntityTame> tame;
	private Optional<ChunkCoordinates> aerialTarget;
	private int growingAge;

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
		return entity.getDataWatcher().getWatchableObjectByte(DataWatcherKey.GROUNDED.key) != 0;
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

	public boolean attackEntityAsMob(Entity targetEntity) {
		// TODO: Send Packet to Client with Animation
		// animTime = maxAnimTime;
		// if (!entity.worldObj.isRemote) {
		// PZPacket packet = new PacketAnimTime().setPacketData(entity.getEntityId(), entity.animTime);
		// ProjectZulu_Core.getPipeline().sendToAllAround(packet,
		// new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 30));
		// }

		float damage = (float) entity.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		int knockbackScale = 0;

		if (targetEntity instanceof EntityLivingBase) {
			damage += EnchantmentHelper.getEnchantmentModifierLiving(entity, (EntityLivingBase) targetEntity);
			knockbackScale += EnchantmentHelper.getKnockbackModifier(entity, (EntityLivingBase) targetEntity);
		}

		boolean attackedSucceded = targetEntity.attackEntityFrom(DamageSource.causeMobDamage(entity), damage);

		if (attackedSucceded) {
			if (knockbackScale > 0) {
				targetEntity.addVelocity((double) (-MathHelper.sin(entity.rotationYaw * (float) Math.PI / 180.0F)
						* (float) knockbackScale * 0.5F), 0.1D,
						(double) (MathHelper.cos(entity.rotationYaw * (float) Math.PI / 180.0F)
								* (float) knockbackScale * 0.5F));
				entity.motionX *= 0.6D;
				entity.motionZ *= 0.6D;
			}

			int fireScale = EnchantmentHelper.getFireAspectModifier(entity);

			if (fireScale > 0) {
				targetEntity.setFire(fireScale * 4);
			}

			if (targetEntity instanceof EntityLivingBase) {
				EnchantmentHelper.func_151384_a((EntityLivingBase) targetEntity, entity);
			}
			EnchantmentHelper.func_151385_b(entity, targetEntity);

		}
		return attackedSucceded && entity.attackEntityAsMob(targetEntity);
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