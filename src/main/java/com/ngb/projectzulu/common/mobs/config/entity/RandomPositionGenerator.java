package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
	/**
	 * used to store a driection when the user passes a point to move towards or away from. WARNING: NEVER THREAD SAFE.
	 * MULTIPLE findTowards and findAway calls, will share this var
	 */
	private static Vec3 staticVector = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

	/**
	 * finds a random target within par1(x,z) and par2 (y) blocks
	 */
	public static Vec3 findRandomTarget(EntityConfigurationAnimal par0EntityCreature, int par1, int par2) {
		return findRandomTargetBlock(par0EntityCreature, par1, par2, (Vec3) null);
	}

	/**
	 * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
	 */
	public static Vec3 findRandomTargetBlockTowards(EntityConfigurationAnimal par0EntityCreature, int par1, int par2,
			Vec3 par3Vec3) {
		staticVector.xCoord = par3Vec3.xCoord - par0EntityCreature.posX;
		staticVector.yCoord = par3Vec3.yCoord - par0EntityCreature.posY;
		staticVector.zCoord = par3Vec3.zCoord - par0EntityCreature.posZ;
		return findRandomTargetBlock(par0EntityCreature, par1, par2, staticVector);
	}

	/**
	 * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
	 */
	public static Vec3 findRandomTargetBlockAwayFrom(EntityConfigurationAnimal par0EntityCreature, int par1, int par2,
			Vec3 par3Vec3) {
		staticVector.xCoord = par0EntityCreature.posX - par3Vec3.xCoord;
		staticVector.yCoord = par0EntityCreature.posY - par3Vec3.yCoord;
		staticVector.zCoord = par0EntityCreature.posZ - par3Vec3.zCoord;
		return findRandomTargetBlock(par0EntityCreature, par1, par2, staticVector);
	}

	/**
	 * finds a random target within par1(x,z) and par2 (y) blocks trending towards desired height level above ground
	 */
	public static Vec3 flyRandomlyTowardHeightLevel(EntityConfigurationAnimal par0EntityCreature, int par1, int par2,
			int heightLevel) {
		return flyToRandomTargetBlockAtHeight(par0EntityCreature, par1, par2, (Vec3) null, heightLevel);
	}

	/**
	 * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
	 * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
	 */
	private static Vec3 findRandomTargetBlock(EntityConfigurationAnimal entity, int par1, int par2, Vec3 par3Vec3) {
		EntityEffects effects = entity.getEntityEffects();
		Random var4 = entity.getRNG();
		boolean var5 = false;
		int var6 = 0;
		int var7 = 0;
		int var8 = 0;
		float var9 = -99999.0F;
		boolean var10;

		if (effects.tether.hasHome()) {
			double var11 = effects.tether.getHomePosition().getDistanceSquared(
					MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY),
					MathHelper.floor_double(entity.posZ)) + 4.0F;
			double var13 = effects.tether.getMaximumHomeDistance() + par1;
			var10 = var11 < var13 * var13;
		} else {
			var10 = false;
		}

		for (int var16 = 0; var16 < 10; ++var16) {
			int var12 = var4.nextInt(2 * par1) - par1;
			int var17 = var4.nextInt(2 * par2) - par2;
			int var14 = var4.nextInt(2 * par1) - par1;

			if (par3Vec3 == null || var12 * par3Vec3.xCoord + var14 * par3Vec3.zCoord >= 0.0D) {
				var12 += MathHelper.floor_double(entity.posX);
				var17 += MathHelper.floor_double(entity.posY);
				var14 += MathHelper.floor_double(entity.posZ);

				if (!var10 || effects.tether.isWithinHomeDistance(var12, var17, var14)) {
					float var15 = 0f;// entity.getBlockPathWeight(var12, var17, var14);

					if (var15 > var9) {
						var9 = var15;
						var6 = var12;
						var7 = var17;
						var8 = var14;
						var5 = true;
					}
				}
			}
		}

		if (var5) {
			// return par0EntityCreature.worldObj.getWorldVec3Pool().getVecFromPool((double)var6, (double)var7,
			// (double)var8);
			return Vec3.createVectorHelper(var6, var7, var8);
		} else {
			return null;
		}
	}

	/**
	 * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
	 * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
	 */
	private static Vec3 flyToRandomTargetBlockAtHeight(EntityConfigurationAnimal entity, int par1, int par2,
			Vec3 par3Vec3, int heightLevel) {
		EntityEffects effects = entity.getEntityEffects();
		Random var4 = entity.getRNG();
		boolean var5 = false;
		int var6 = 0;
		int var7 = 0;
		int var8 = 0;
		float var9 = -99999.0F;
		boolean var10;

		if (effects.tether.hasHome()) {
			double var11 = effects.tether.getHomePosition().getDistanceSquared(
					MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY),
					MathHelper.floor_double(entity.posZ)) + 4.0F;
			double var13 = effects.tether.getMaximumHomeDistance() + par1;
			var10 = var11 < var13 * var13;
		} else {
			var10 = false;
		}

		for (int var16 = 0; var16 < 10; ++var16) {
			int var12 = var4.nextInt(2 * par1) - par1;
			int var17;
			if (entity.posY > entity.worldObj.getPrecipitationHeight((int) entity.posX, (int) entity.posZ)
					+ heightLevel * 1.25) {
				var17 = var4.nextInt(2 * par2) - par2 * 3 / 2;
			} else if (entity.posY < entity.worldObj.getPrecipitationHeight((int) entity.posX, (int) entity.posZ)
					+ heightLevel) {
				var17 = var4.nextInt(2 * par2) - par2 / 2;
			} else {
				var17 = var4.nextInt(2 * par2) - par2;
			}

			int var14 = var4.nextInt(2 * par1) - par1;

			if (par3Vec3 == null || var12 * par3Vec3.xCoord + var14 * par3Vec3.zCoord >= 0.0D) {
				var12 += MathHelper.floor_double(entity.posX);
				var17 += MathHelper.floor_double(entity.posY);
				var14 += MathHelper.floor_double(entity.posZ);

				if (!var10 || effects.tether.isWithinHomeDistance(var12, var17, var14)) {
					float var15 = 0f;// entity.getBlockPathWeight(var12, var17, var14);

					if (var15 > var9) {
						var9 = var15;
						var6 = var12;
						var7 = var17;
						var8 = var14;
						var5 = true;
					}
				}
			}
		}

		if (var5) {
			// return entity.worldObj.getWorldVec3Pool().getVecFromPool((double)var6, (double)var7, (double)var8);
			return Vec3.createVectorHelper(var6, var7, var8);
		} else {
			return null;
		}
	}
}
