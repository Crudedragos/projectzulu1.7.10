package com.ngb.projectzulu.common.mobs.config.entity;

import java.io.IOException;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.base.Optional;
import com.ngb.projectzulu.common.core.PZPacketBase;
import com.ngb.projectzulu.common.core.packets.PacketTameParticle;
import com.ngb.projectzulu.common.dungeon.packets.PacketByteStream;
import com.ngb.projectzulu.common.mobs.config.entity.EntityTame.Command;
import com.ngb.projectzulu.common.mobs.entity.EntityGenericTameable;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncTame extends PacketByteStream implements IMessageHandler<PacketSyncTame, IMessage> {
	private int entityId;
	private EntityTame tame;

	public PacketSyncTame setPacketData(int entityId, EntityTame tame) {
		this.entityId = entityId;
		this.tame = tame;
		return this;
	}

	@Override
	protected void writeData(ByteBufOutputStream buffer) throws IOException {
		buffer.writeInt(entityId);

		buffer.writeLong(tame.getOwner().getMostSignificantBits());
		buffer.writeLong(tame.getOwner().getLeastSignificantBits());

		buffer.writeInt(tame.getUsername().length());
		buffer.writeChars(tame.getUsername());

		buffer.writeInt(tame.getCommand().toString().length());
		buffer.writeChars(tame.getCommand().toString());
	}

	@Override
	protected void readData(ByteBufInputStream buffer) throws IOException {
		entityId = buffer.readInt();
		UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
		String username = readNextString(buffer);
		Optional<Command> command = Command.getFromString(readNextString(buffer));

		tame = new EntityTame(uuid, username, command.isPresent() ? command.get() : Command.FOLLOWING);
	}

	private String readNextString(ByteBufInputStream buffer) throws IOException {
		int nameLength = buffer.readInt();
		char[] nameChars = new char[nameLength];
		for (int i = 0; i < nameLength; i++) {
			nameChars[i] = buffer.readChar();
		}
		return new String(nameChars);
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		Entity entity = player.worldObj.getEntityByID(entityId);
		if (entity != null && entity instanceof EntityConfigurationAnimal) {
			((EntityConfigurationAnimal) entity).getEntityEffects().setTamed(tame);
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
	}

	@Override
	public IMessage onMessage(PacketSyncTame message, MessageContext ctx) {
		return handleMessage(message, ctx);
	}
}
