package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.base.Optional;
import com.ngb.projectzulu.common.mobs.config.entity.EntityTame.Command;

public class AIFollowOwner extends AIBase {
	private EntityEffects effects;
	private float speedModifier;
	private float disToStartFollow; // Distance away from owner entity must be to start following
	private float disToStopFollow; // Once following, distance to Owner entity must be to stop

	private EntityPlayer owner;
	private int changeDirectionCooldown;

	public AIFollowOwner(EntityEffects effects, float speedModifier) {
		this(effects, speedModifier, 10.0f, 2.0f);
	}

	public AIFollowOwner(EntityEffects effects, float speedModifier, float disToStartFollow, float disToStopFollow) {
		this.effects = effects;
		this.speedModifier = speedModifier;
		this.disToStartFollow = disToStartFollow;
		this.disToStopFollow = disToStopFollow;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (effects.getTamed().isPresent() && effects.getTamed().get().getCommand() == Command.FOLLOWING) {
			Optional<EntityPlayer> player = getOwner();
			return player.isPresent()
					&& effects.entity.getDistanceSqToEntity(player.get()) < disToStartFollow * disToStartFollow;
		}
		return false;
	}

	private Optional<EntityPlayer> getOwner() {
		EntityPlayer player = effects.entity.worldObj.func_152378_a(effects.getTamed().get().getOwner());
		return player != null ? Optional.of(player) : Optional.<EntityPlayer> absent();
	}

	@Override
	public void startExecuting() {
		changeDirectionCooldown = 0;
		owner = getOwner().get();
	}

	@Override
	public boolean continueExecuting() {
		return effects.hasPath()
				&& effects.entity.getDistanceSqToEntity(owner) > disToStopFollow * disToStopFollow
				&& effects.getTamed().get().getCommand() == Command.FOLLOWING;
	}

	@Override
	public void resetTask() {
		owner = null;
		effects.setMovementTarget((Entity) null);
	}

	@Override
	public void updateTask() {
		effects.entity.getLookHelper().setLookPositionWithEntity(owner, 10.0F, 30.0f);
		if (--changeDirectionCooldown <= 0) {
			changeDirectionCooldown = 10; // TODO Configurable
			boolean moved = effects.tryMovementTarget(owner, effects.getBaseSpeed() * speedModifier);
			// TP to player if FAR away
			if (!moved && effects.entity.getDistanceSqToEntity(owner) >= 144.0D) {
				ChunkCoordinates tpLoc = getValidTPLocationNearPlayer();
				if (tpLoc != null) {
					effects.entity.setLocationAndAngles(tpLoc.posX, tpLoc.posY, tpLoc.posZ, effects.entity.rotationYaw,
							effects.entity.rotationPitch);
					effects.setMovementTarget((Entity) null);
				}
			}
		}
	}

	private ChunkCoordinates getValidTPLocationNearPlayer() {
		int tpX = MathHelper.floor_double(owner.posX) - 2;
		int tpZ = MathHelper.floor_double(owner.posZ) - 2;
		int tpY = MathHelper.floor_double(owner.boundingBox.minY);
		for (int x = 0; x <= 4; ++x) {
			for (int z = 0; z <= 4; ++z) {
				if ((x < 1 || z < 1 || x > 3 || x > 3)
						&& World.doesBlockHaveSolidTopSurface(effects.entity.worldObj, tpX + x, tpY - 1, tpZ + z)
						&& !effects.entity.worldObj.getBlock(tpX + x, tpY, tpZ + z).isNormalCube()
						&& !effects.entity.worldObj.getBlock(tpX + x, tpY + 1, tpZ + z).isNormalCube()) {
					return new ChunkCoordinates(tpX + x, tpY, tpZ + z);
				}
			}
		}
		return null;
	}
}
