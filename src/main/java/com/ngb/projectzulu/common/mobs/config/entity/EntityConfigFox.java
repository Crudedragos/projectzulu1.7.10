package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import com.ngb.projectzulu.common.core.DefaultProps;
import com.ngb.projectzulu.common.mobs.config.entity.DistanceSelector.EntityDistanceSelector;
import com.ngb.projectzulu.common.mobs.config.entity.SelectorWatchable.SelectPlayerWatchable;

public class EntityConfigFox extends EntityConfigurationAnimal {

	public EntityConfigFox(World world) {
		super(world);
		setup();
	}

	protected void setup() {
		getNavigator().setAvoidsWater(true);
		// AITame
		/* Attack (Metex == 0100) */
		addTask(3, new AIAttackNearbyTarget(effects, new SimpleSelector(EntityPlayer.class)));

		/* Target (Mutex == 0010) */
//		addTask(3, new AIOwnerHurtTarget(effects));
//		addTask(4, new AIOwnerHurtByTarget(effects));
		addTask(5, new AIHurtByTarget(effects, false, false));
//		addTask(6, new AINearestAttackableTarget(effects, EntityPlayer.class, new SimpleSelector(EntityPlayer.class),
//				16.0f, true));

		/* Movement (Mutex == 0001) */
		// Mutex 0101 == Block Target && Movement
//		addTask(1, new AIStayStill(effects, new SelectorTamedSitting()).setMutex(3));
//		addTask(2, new AIPanic(effects, 0.6f, 25, new EntityDistanceSelector(effects.entity, 30f)));

		// TODO Split attack and MoveToAttack
		addTask(3, new AIMoveToTarget(effects, 0.6f, new SimpleSelector(EntityPlayer.class)));
//		addTask(4, new AIMate(effects, 1.0f, new ItemFilter.SimpleItemFilter(Items.egg)));
//		addTask(5, new AIFollowParent(effects, 1.0f));
//		addTask(5, new AIFollowOwner(effects, 1.0f));
		addTask(6, new AITempt(effects, 0.3f, 20f, new ItemFilter.SimpleItemFilter(Items.egg)));
		addTask(7, new AIWander(effects, 0.5f, 120));
		addTask(10, new AILookIdle(effects, 0.02f));

		/* Misc (Mutex == 1000) */
//		addTask(10, new AIWatchClosest(effects, 6.0f, 0.02f, new SelectPlayerWatchable()).setMutex(8));
//		addTask(15, new AITame(effects, 0, "Fox", new ItemFilter.SimpleItemFilter(Items.egg)));
	}

	@Override
	public int getTotalArmorValue() {
		return 2;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected String getHurtSound() {
		return DefaultProps.coreKey + ":" + DefaultProps.entitySounds + "foxhurtsound";
	}
}
