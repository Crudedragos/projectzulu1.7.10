package com.ngb.projectzulu.common.mobs.config.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.google.common.base.Optional;
import com.ngb.projectzulu.common.ProjectZulu_Core;
import com.ngb.projectzulu.common.core.PZPacket;
import com.ngb.projectzulu.common.core.packets.PacketTameParticle;
import com.ngb.projectzulu.common.mobs.config.entity.EntityTame.Command;
import com.ngb.projectzulu.common.mobs.entity.EntityStates;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class AITame extends AIBase implements TaskInteract {
	private EntityEffects effects;

	private ItemFilter tamingFilter;
	private int itemHealingValue = 0;
	private String defaultEntityName;

	public AITame(EntityEffects effects, int itemHealingValue, String defaultEntityName, ItemFilter tamingFilter) {
		this.effects = effects;
		this.itemHealingValue = itemHealingValue;
		this.defaultEntityName = defaultEntityName;
		this.tamingFilter = tamingFilter;
		this.setMutexBits(0);
	}

	@Override
	public boolean shouldExecute() {
		return false;
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	@Override
	public void resetTask() {
	}

	@Override
	public void updateTask() {
	}

	@Override
	public boolean interact(EntityPlayer player) {
		ItemStack var2 = player.inventory.getCurrentItem();

		if (effects.getTamed().isPresent()) {
			Optional<EntityPlayer> owner = getOwner();
			/* TODO: Un Tame? */
			if (EntityPlayer.func_146094_a(player.getGameProfile()).equals(effects.getTamed().get().getOwner())) {
				if (var2 != null) {
					// Task Name Tameable
					if (var2.getItem() == Items.paper || var2.getItem() == Items.name_tag) {
						player.openGui(ProjectZulu_Core.modInstance, 2, player.worldObj, effects.entity.getEntityId(),
								0, 0);
						return true;
						// Task ItemHeal
					} else if (getHealingValueIfValid(var2) > 0
							&& effects.entity.getHealth() < effects.entity.getMaxHealth()) {
						if (!player.capabilities.isCreativeMode) {
							--var2.stackSize;
						}
						effects.entity.heal(getHealingValueIfValid(var2));

						if (var2.stackSize <= 0) {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
						}
						return true;
					}
				}

				/* Toggle Animal Action Sitting --> Follow -- > Attack(Not Implemented) -- > None --> Sitting etc.. */
				if (!tamingFilter.isMatch(var2)) {
					if (effects.entity.worldObj.isRemote) {
						return true;
					}
					if (effects.getTamed().get().getCommand() == Command.SITTING) {
						effects.getTamed().get().setCommand(Command.FOLLOWING);
					} else if (effects.getTamed().get().getCommand() == Command.FOLLOWING) {
						effects.getTamed().get().setCommand(Command.ATTACKING);
					} else if (effects.getTamed().get().getCommand() == Command.ATTACKING) {
						effects.getTamed().get().setCommand(Command.DEFENDING);
					} else if (effects.getTamed().get().getCommand() == Command.DEFENDING) {
						effects.getTamed().get().setCommand(Command.SITTING);
					}
					return true;
				}
			}
		} else if (tamingFilter.isMatch(var2)) {
			if (!player.capabilities.isCreativeMode) {
				--var2.stackSize;
			}

			if (var2.stackSize <= 0) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
			}

			if (!effects.entity.worldObj.isRemote) {
				boolean tameEffectSuccess = false;
				if (effects.entity.getRNG().nextInt(3) == 0) {
					effects.setTamed(new EntityTame(EntityPlayer.func_146094_a(player.getGameProfile()),
							getDefaultEntityName(), Command.FOLLOWING));
					effects.entity.setAttackTarget((EntityLiving) null);
					effects.entity.setHealth(effects.entity.getMaxHealth());
					tameEffectSuccess = true;
				}
				/* Send Tame Effect Packet */
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				DataOutputStream data = new DataOutputStream(bytes);
				/* Write PacketID into Packet */
				try {
					data.writeInt(5);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					data.writeInt(effects.entity.getEntityId());
					data.writeBoolean(tameEffectSuccess);
				} catch (Exception e) {
					e.printStackTrace();
				}
				PZPacket packet = new PacketTameParticle().setPacketData(effects.entity.getEntityId(),
						tameEffectSuccess);
				ProjectZulu_Core.getPipeline().sendToAllAround(
						packet,
						new TargetPoint(effects.entity.dimension, effects.entity.posX, effects.entity.posY,
								effects.entity.posZ, 10));
			}
			return true;
		}
		return false;
	}

	private Optional<EntityPlayer> getOwner() {
		EntityPlayer player = effects.entity.worldObj.func_152378_a(effects.getTamed().get().getOwner());
		return player != null ? Optional.of(player) : Optional.<EntityPlayer> absent();
	}

	private int getHealingValueIfValid(ItemStack stack) {
		return itemHealingValue;
	}

	public String getDefaultEntityName() {
		return defaultEntityName;
	}
}