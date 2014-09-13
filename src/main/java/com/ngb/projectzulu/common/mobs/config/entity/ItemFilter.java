package com.ngb.projectzulu.common.mobs.config.entity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ItemFilter {
	public boolean isMatch(ItemStack itemStack);

	public static class SimpleItemFilter implements ItemFilter {
		private Item item;

		public SimpleItemFilter(Item item) {
			this.item = item;
		}

		@Override
		public boolean isMatch(ItemStack itemStack) {
			return itemStack == null ? false : item.equals(itemStack.getItem());
		}
	}
}
