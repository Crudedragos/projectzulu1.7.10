package com.ngb.projectzulu.common.mobs.config.entity;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

public class FlightNavigator {
	private EntityConfigurationAnimal flyingEntity;
	private Optional<ChunkCoordinates> aerialTarget;

	public boolean hasTarget() {
		return aerialTarget.isPresent();
	}

	public boolean setTargetPosition(ChunkCoordinates targetPosition) {
		if (isTargetPositionValid(targetPosition)) {
			aerialTarget = Optional.of(targetPosition);
			return true;
		}
		aerialTarget = Optional.absent();
		return false;
	}

	protected int maxFlightHeight = 20;

	public int getMaxFlightHeight() {
		return maxFlightHeight;
	}

	public FlightNavigator(EntityConfigurationAnimal flyingEntity) {
		this.flyingEntity = flyingEntity;
	}

	public void update() {
		double var1 = (double) aerialTarget.get().posX + 0.5D - flyingEntity.posX;
		double var3 = (double) aerialTarget.get().posY + 0.1D - flyingEntity.posY;
		double var5 = (double) aerialTarget.get().posZ + 0.5D - flyingEntity.posZ;

		/* Change Velocity */
		/* Normalize the Direction I want to travel in, then add and scale it to Motion */
		flyingEntity.motionX += (Math.signum(var1) * 0.5D - flyingEntity.motionX) * 0.10000000149011612D * 0.3D;
		flyingEntity.motionY += (Math.signum(var3) * 0.699999988079071D - flyingEntity.motionY) * 0.10000000149011612D * 0.3D;
		flyingEntity.motionZ += (Math.signum(var5) * 0.5D - flyingEntity.motionZ) * 0.10000000149011612D * 0.3D;
		float var7 = (float) (Math.atan2(flyingEntity.motionZ, flyingEntity.motionX) * 180.0D / Math.PI) - 90.0F;
		float var8 = MathHelper.wrapAngleTo180_float(var7 - flyingEntity.rotationYaw);
		flyingEntity.moveForward = 0.5F;
		flyingEntity.rotationYaw += var8;
	}

	/* Checks if Entity is at the target position, return true if TargetPosition is null */
	public boolean atTargetPosition() {
		if (aerialTarget != null) {
			return flyingEntity.getDistance(aerialTarget.get().posX, aerialTarget.get().posY, aerialTarget.get().posZ) < 2;
		}
		return true;
	}

	public boolean isTargetPositionValid() {
		return aerialTarget.isPresent() ? isTargetPositionValid(aerialTarget.get()) : false;
	}

	public boolean isTargetPositionValid(ChunkCoordinates targetPosition) {
		/* Invalid if Water, is below height = 3 (superflat), and if its null */
		if (targetPosition != null
				&& (!flyingEntity.worldObj.isAirBlock(targetPosition.posX, targetPosition.posY, targetPosition.posZ)
						|| targetPosition.posY < 3 || flyingEntity.worldObj
						.getBlock(targetPosition.posX, targetPosition.posY, targetPosition.posZ).getMaterial()
						.equals(Material.water))) {
			return false;
		}
		return true;
	}

	public void moveEntityWithHeading(float par1, float par2) {
		if (flyingEntity.isInWater()) {
			flyingEntity.moveFlying(par1, par2, 0.02F);
			flyingEntity.moveEntity(flyingEntity.motionX, flyingEntity.motionY, flyingEntity.motionZ);
			flyingEntity.motionX *= 0.800000011920929D;
			flyingEntity.motionY *= 0.800000011920929D;
			flyingEntity.motionZ *= 0.800000011920929D;
		} else if (flyingEntity.handleLavaMovement()) {
			flyingEntity.moveFlying(par1, par2, 0.02F);
			flyingEntity.moveEntity(flyingEntity.motionX, flyingEntity.motionY, flyingEntity.motionZ);
			flyingEntity.motionX *= 0.5D;
			flyingEntity.motionY *= 0.5D;
			flyingEntity.motionZ *= 0.5D;
		} else {
			float var3 = 0.91F;
			if (flyingEntity.onGround) {
				var3 = 0.54600006F;
				Block block = flyingEntity.worldObj.getBlock(MathHelper.floor_double(flyingEntity.posX),
						MathHelper.floor_double(flyingEntity.boundingBox.minY) - 1,
						MathHelper.floor_double(flyingEntity.posZ));
				if (block != null) {
					var3 = block.slipperiness * 0.91F;
				}
			}

			float var8 = 0.16277136F / (var3 * var3 * var3);
			flyingEntity.moveFlying(par1, par2, flyingEntity.onGround ? 0.1F * var8 : 0.02F);
			var3 = 0.91F;

			if (flyingEntity.onGround) {
				var3 = 0.54600006F;
				Block block = flyingEntity.worldObj.getBlock(MathHelper.floor_double(flyingEntity.posX),
						MathHelper.floor_double(flyingEntity.boundingBox.minY) - 1,
						MathHelper.floor_double(flyingEntity.posZ));
				if (block != null) {
					var3 = block.slipperiness * 0.91F;
				}
			}

			flyingEntity.moveEntity(flyingEntity.motionX, flyingEntity.motionY, flyingEntity.motionZ);
			flyingEntity.motionX *= (double) var3;
			flyingEntity.motionY *= (double) var3;
			flyingEntity.motionZ *= (double) var3;
		}
		flyingEntity.prevLimbSwingAmount = flyingEntity.limbSwingAmount;
		double var10 = flyingEntity.posX - flyingEntity.prevPosX;
		double var9 = flyingEntity.posZ - flyingEntity.prevPosZ;
		float var7 = MathHelper.sqrt_double(var10 * var10 + var9 * var9) * 4.0F;
		if (var7 > 1.0F) {
			var7 = 1.0F;
		}
		flyingEntity.limbSwingAmount += (var7 - flyingEntity.limbSwingAmount) * 0.4F;
		flyingEntity.limbSwing += flyingEntity.limbSwing;
	}
}
