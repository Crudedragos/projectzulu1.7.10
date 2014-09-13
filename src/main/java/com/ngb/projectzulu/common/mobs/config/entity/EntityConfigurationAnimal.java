package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.ngb.projectzulu.common.api.CustomEntityList;
import com.ngb.projectzulu.common.api.CustomMobData;
import com.ngb.projectzulu.common.mobs.config.entity.EntityTame.Command;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityConfigurationAnimal extends EntityLiving {
	protected EntityEffects effects;
	private List<TaskAttacked> attackListeners; // TODO: Sort by priority
	private List<TaskInteract> interactListeners; // TODO: Sort by priority
	// During Mating the entity needs to check if the other entity is mating, thus must be exposed
	private Optional<AIMate> loveTask;
	private CustomMobData data;
	
	
	public enum DataWatcherKey {
		GROWING_AGE(12), CLIMEABLEBLOCK(16), GROUNDED(17), SADDLED(21), TAMED(22), COMMAND(23), USERNAME(24), STATE(20);

		public final int key;

		DataWatcherKey(int key) {
			this.key = key;
		}
	}

	public EntityConfigurationAnimal(World world) {
		super(world);
		setSize(0.6f, 1.0f);
		effects = new EntityEffects(this);
		attackListeners = new ArrayList<TaskAttacked>();
		interactListeners = new ArrayList<TaskInteract>();
		loveTask = Optional.absent();
		data = CustomEntityList.getByName(EntityList.getEntityString(this)).modData.get();
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		CustomEntityList entityEntry = CustomEntityList.getByName(EntityList.getEntityString(this));
		if (entityEntry != null && entityEntry.modData.get().entityProperties != null) {
			data = entityEntry.modData.get();
			// Register Damage Attribute
			getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);

			// Set Base values of attributes
			getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(
					entityEntry.modData.get().entityProperties.maxHealth);
			getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(
					entityEntry.modData.get().entityProperties.moveSpeed);
			getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(
					entityEntry.modData.get().entityProperties.followRange);
			getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(
					entityEntry.modData.get().entityProperties.knockbackResistance);
			getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(
					entityEntry.modData.get().entityProperties.attackDamage);
			// flightChance = entityEntry.modData.get().entityProperties.flightChance;
		}
	}

	protected void entityInit() {
		super.entityInit();
		/* Growing Age */
		dataWatcher.addObject(DataWatcherKey.GROWING_AGE.key, new Integer(0));
		/* Handles Whether the Entity Is Saddled */
		dataWatcher.addObject(DataWatcherKey.SADDLED.key, Byte.valueOf((byte) 0));
		/* Handles the Taming State 0 == Wild, otherwise Tames */
		dataWatcher.addObject(DataWatcherKey.TAMED.key, Byte.valueOf((byte) 0));
		/* Handles the Entity's Tame Command */
		dataWatcher.addObject(DataWatcherKey.COMMAND.key, "");
		/* Handles the Entity's Usernam */
		dataWatcher.addObject(DataWatcherKey.USERNAME.key, "");

		/* Entity State */
		dataWatcher.addObject(DataWatcherKey.STATE.key, Short.valueOf((short) 0));
		/* Handle whether Entity is Flying or not */
		if (defaultGrounded()) {
			dataWatcher.addObject(DataWatcherKey.GROUNDED.key, Byte.valueOf((byte) 1));
		} else {
			dataWatcher.addObject(DataWatcherKey.GROUNDED.key, Byte.valueOf((byte) 0));
		}
		// Beside climeableblock (is this needed? Used in Mummy)
		dataWatcher.addObject(DataWatcherKey.CLIMEABLEBLOCK.key, new Byte((byte) 0));
	}

	protected boolean defaultGrounded() {
		return true;
	}

	@Override
	protected void updateAITasks() {
		if (!effects.isGrounded() && effects.flightNav.hasTarget()) {
			effects.flightNav.update();
		}

		if (effects.getTamed().isPresent()) {
			// dataWatcher.updateObject(22, p_75692_2_);
			/* Handles the Taming State 0 == Wild, Bit 3 handles if Tamed (&4), Bit 1 handles if Sitting (&1) */
			dataWatcher.updateObject(DataWatcherKey.TAMED.key, Byte.valueOf((byte) 0));
			/* Command */
			dataWatcher.updateObject(DataWatcherKey.COMMAND.key, effects.getTamed().get().getCommand().toString());
			/* Handles the Entity's tame name */
			dataWatcher.updateObject(DataWatcherKey.USERNAME.key, effects.getTamed().get().getUsername());
		}
		super.updateAITasks();
	}

	@Override
	public void onUpdate() {
		if (worldObj.isRemote) {
			boolean isTamed = dataWatcher.getWatchableObjectByte(DataWatcherKey.TAMED.key) != 0;
			if (!isTamed && effects.getTamed().isPresent()) {
				effects.setTamed(null);
			} else if (isTamed && !effects.getTamed().isPresent()) {
				Optional<Command> command = Command.getFromString(dataWatcher
						.getWatchableObjectString(DataWatcherKey.COMMAND.key));
				EntityTame tame = new EntityTame(null,
						dataWatcher.getWatchableObjectString(DataWatcherKey.USERNAME.key),
						command.isPresent() ? command.get() : Command.FOLLOWING);
				effects.setTamed(tame);
			} else if (isTamed && effects.getTamed().isPresent()) {
				Optional<Command> command = Command.getFromString(dataWatcher
						.getWatchableObjectString(DataWatcherKey.COMMAND.key));
				if (command.isPresent()) {
					effects.getTamed().get().setCommand(command.get());
				}
				effects.getTamed().get().setUsername(dataWatcher.getWatchableObjectString(DataWatcherKey.USERNAME.key));
			}
		}
		super.onUpdate();
	}

	public final void addTask(int priority, EntityAIBase task) {
		if (task instanceof TaskAttacked) {
			attackListeners.add((TaskAttacked) task);
		}
		if (task instanceof TaskInteract) {
			interactListeners.add((TaskInteract) task);
		}
		if (task instanceof AIMate) {
			loveTask = Optional.of((AIMate) task);
		}
		tasks.addTask(priority, task);
	}

	public EntityEffects getEntityEffects() {
		return effects;
	}

	@Override
	public DataWatcher getDataWatcher() {
		return dataWatcher;
	}

	public boolean nearbyAttack(EntityConfigurationAnimal nearby, DamageSource attacker, float par2) {
		for (TaskAttacked task : attackListeners) {
			task.nearbyAttacked(nearby, attacker, par2);
		}
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		for (TaskAttacked task : attackListeners) {
			if (task.hurtBy(source, damage)) {
				return true;
			}
		}
		return super.attackEntityFrom(source, damage);
	}

	@Override
	protected boolean interact(EntityPlayer player) {
		for (TaskInteract task : interactListeners) {
			if (task.interact(player)) {
				return true;
			}
		}
		return super.interact(player);
	}

	public boolean isInLove() {
		return loveTask.isPresent() ? loveTask.get().isInLove() : false;
	}

	@Override
	public void moveEntityWithHeading(float par1, float par2) {
		if (!effects.isGrounded()) {
			effects.flightNav.moveEntityWithHeading(par1, par2);
		} else {
			super.moveEntityWithHeading(par1, par2);
			return;
		}
	}

	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setByte("Is Grounded", dataWatcher.getWatchableObjectByte(17));
	}

	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		if (par1NBTTagCompound.hasKey("Is Grounded")) {
			dataWatcher.updateObject(17, par1NBTTagCompound.getByte("Is Grounded"));
		}
	}
}
