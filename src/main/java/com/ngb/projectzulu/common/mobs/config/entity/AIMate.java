package com.ngb.projectzulu.common.mobs.config.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Optional;

public class AIMate extends AIBase implements TaskInteract {
	private EntityEffects effects;
	private float speedModifier;
	private ItemFilter breddingFilter;

	private int loveTimer = 0; // Lovetimer
	private int spawnBabyDelay = 0; // Delay preventing a baby from spawning immediately when two mates find each other.
	private Optional<EntityConfigurationAnimal> nearbyMate;

	public AIMate(EntityEffects effects, float speedModifier, ItemFilter breddingFilter) {
		this.effects = effects;
		this.speedModifier = speedModifier;
		this.breddingFilter = breddingFilter;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return effects.entity.isEntityAlive() && loveTimer > 0 && effects.getGrowingAge() == 0;
	}

	private Optional<EntityConfigurationAnimal> getNearbymate() {
		float var1 = 8.0F;
		List var2 = effects.entity.worldObj.getEntitiesWithinAABB(effects.entity.getClass(),
				effects.entity.boundingBox.expand(var1, var1, var1));
		Iterator iterator = var2.iterator();
		EntityConfigurationAnimal entity;

		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entity = (EntityConfigurationAnimal) iterator.next();
		} while (!canMateWith(entity));
		return Optional.of(entity);
	}

	private boolean canMateWith(EntityConfigurationAnimal targetEntity) {
		/* If Passed Entity is Self or Different Type, return False */
		if (targetEntity == effects.entity || targetEntity.getClass() != effects.entity.getClass()) {
			return false;
		}
		/* Otherwise, if Target is in Love, and Self is. Then let them mate */
		if (this.isInLove() && targetEntity.isInLove()) {
			return true;
		}
		return false;
	}

	@Override
	public void startExecuting() {
		nearbyMate = getNearbymate();
		spawnBabyDelay = 60;
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
		nearbyMate = Optional.absent();
		loveTimer = 0;
		spawnBabyDelay = 0;
	}

	@Override
	public void updateTask() {
		--loveTimer;
		if (loveTimer % 10 == 0) {
			spawnParticle("heart");
		}
		if (nearbyMate.isPresent()) {
			--spawnBabyDelay;
			effects.entity.getLookHelper().setLookPositionWithEntity(nearbyMate.get(), 10.0F, 30.0f);
			effects.entity.getNavigator().tryMoveToEntityLiving(nearbyMate.get(),
					effects.getBaseSpeed() * speedModifier);
			if (spawnBabyDelay <= 0) {
				spawnBaby();
			}
		} else if (loveTimer % 5 == 0) {
			nearbyMate = getNearbymate();
		}
	}

	private void spawnBaby() {
		EntityConfigurationAnimal baby = getBabyAnimalEntity();

		if (baby != null) {
			effects.setGrowingAge(6000);
			nearbyMate.get().getEntityEffects().setGrowingAge(6000);
		}
	}

	private EntityConfigurationAnimal getBabyAnimalEntity() {
		Object object = null;
		try {
			Class<?> thisClass = effects.entity.getClass();
			Constructor<?> ctor = thisClass.getConstructor(World.class);
			try {
				object = ctor.newInstance(new Object[] { effects.entity.worldObj });
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return (EntityConfigurationAnimal) object;
	}

	@Override
	public boolean interact(EntityPlayer player) {
		ItemStack handItem = player.inventory.getCurrentItem();
		if (breddingFilter.isMatch(handItem) && effects.getGrowingAge() == 0) {
			if (!player.capabilities.isCreativeMode) {
				--handItem.stackSize;
				if (handItem.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
				}
			}
			loveTimer = 600; // Historical, Arbitrary
			for (int j = 0; j < 7; j++) {
				spawnParticle("heart");
			}
			return true;
		}
		return false;
	}

	// May need to turn into Packet, cannot remember how particles work
	private void spawnParticle(String particle) {
		float width = effects.entity.width;
		Random rand = effects.entity.worldObj.rand;
		effects.entity.worldObj.spawnParticle(particle, effects.entity.posX
				+ rand.nextFloat() * width * 2.0F - width, effects.entity.posY + 0.5D
				+ rand.nextFloat() * effects.entity.height, effects.entity.posZ
				+ rand.nextFloat() * width * 2.0F - width, rand.nextGaussian() * 0.02D,
				rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D);
	}

	public boolean isInLove() {
		return loveTimer > 0;
	}

	public void resetLove() {
		loveTimer = 0;
	}
}
