package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

/**
 * 
 */
public class EntityHome {
	private EntityLiving entity;
	private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
	private float maximumHomeDistance = -1.0f;

	public EntityHome(EntityLiving entity) {
		this.entity = entity;
	}

	/**
	 * Returns true if entity is within home distance from current position
	 */
	public boolean isWithinHomeDistanceCurrentPosition() {
		return this.isWithinHomeDistance(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY),
				MathHelper.floor_double(entity.posZ));
	}

	public boolean isWithinHomeDistance(int par1, int par2, int par3) {
		return maximumHomeDistance == -1.0F ? true
				: homePosition.getDistanceSquared(par1, par2, par3) < maximumHomeDistance * maximumHomeDistance;
	}

	public void setHomeArea(int par1, int par2, int par3, int par4) {
		this.homePosition.set(par1, par2, par3);
		this.maximumHomeDistance = par4;
	}

	public ChunkCoordinates getHomePosition() {
		return this.homePosition;
	}

	public float getMaximumHomeDistance() {
		return this.maximumHomeDistance;
	}

	public void detachHome() {
		this.maximumHomeDistance = -1.0F;
	}

	public boolean hasHome() {
		return this.maximumHomeDistance != -1.0F;
	}
}
