package com.ngb.projectzulu.common.mobs.config.entity;

import java.util.UUID;

public class EntityTame {
	public enum Command {
		SITTING, FOLLOWING, DEFENDING, ATTACKING;
	}

	private UUID owner;
	private String username;
	private Command command;

	public EntityTame(UUID owner, String username, Command command) {
		this.owner = owner;
		this.username = username;
		this.command = command;
	}

	public UUID getOwner() {
		return owner;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String getUsername() {
		return username;
	}
}
