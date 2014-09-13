package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityConfigurationAnimal extends EntityLiving {
	private EntityEffects effects;
	private List<TaskAttacked> attackListeners; // TODO: Sort by priority
	private List<TaskInteract> interactListeners; // TODO: Sort by priority
	// During Mating the entity needs to check if the other entity is mating, thus must be exposed
	private Optional<AIMate> loveTask;

	public EntityConfigurationAnimal(World world) {
		super(world);
		setSize(0.6f, 1.0f);
		attackListeners = new ArrayList<TaskAttacked>();
		interactListeners = new ArrayList<TaskInteract>();
		loveTask = Optional.absent();
	}

	@SuppressWarnings("unused")
	@Override
	protected void entityInit() {
		super.entityInit();
		/* Growing Age */
		this.dataWatcher.addObject(12, new Integer(0));
		/* Handles Whether the Entity Is Saddled */
		this.dataWatcher.addObject(21, Byte.valueOf((byte) 0));
		/* Handles the Taming State 0 == Wild, Bit 3 handles if Tamed (&4), Bit 1 handles if Sitting (&1) */
		this.dataWatcher.addObject(22, Byte.valueOf((byte) 0));
		/* Handles the Owners name */
		this.dataWatcher.addObject(23, "");
		/* Handles the Entity's name */
		this.dataWatcher.addObject(24, "");
		/* Entity State */
		this.dataWatcher.addObject(20, Short.valueOf((short) 0));
		/* Handle whether Entity is Flying or not */
		if (true/* defaultGrounded() */) {
			this.dataWatcher.addObject(17, Byte.valueOf((byte) 1));
		} else {
			this.dataWatcher.addObject(17, Byte.valueOf((byte) 0));
		}
		// Beside climeableblock (is this needed? Used in Mummy)
		this.dataWatcher.addObject(16, new Byte((byte) 0));
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
	protected void updateAITasks() {
		if (!effects.isGrounded() && effects.flightNav.hasTarget()) {
			effects.flightNav.update();
		}
		super.updateAITasks();
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
		par1NBTTagCompound.setByte("Is Grounded", this.dataWatcher.getWatchableObjectByte(17));
	}

	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		if (par1NBTTagCompound.hasKey("Is Grounded")) {
			this.dataWatcher.updateObject(17, par1NBTTagCompound.getByte("Is Grounded"));
		}
	}
}
