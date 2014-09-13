package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.Comparator;

import net.minecraft.entity.Entity;

public class ComparatorNearbyEntity implements Comparator {
	private Entity theEntity;

	public ComparatorNearbyEntity(Entity theEntity) {
		this.theEntity = theEntity;
	}

	public int compareDistanceSq(Entity par1Entity, Entity par2Entity) {
		double var3 = this.theEntity.getDistanceSqToEntity(par1Entity);
		double var5 = this.theEntity.getDistanceSqToEntity(par2Entity);
		return var3 < var5 ? -1 : (var3 > var5 ? 1 : 0);
	}

	@Override
	public int compare(Object par1Obj, Object par2Obj) {
		return this.compareDistanceSq((Entity) par1Obj, (Entity) par2Obj);
	}
}
