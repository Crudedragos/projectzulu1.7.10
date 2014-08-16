package com.ngb.projectzulu.common.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.Item;
import com.ngb.projectzulu.common.api.ItemList;
import com.ngb.projectzulu.common.api.SubItemPotionList;
import com.ngb.projectzulu.common.core.ProjectZuluLog;
import com.ngb.projectzulu.common.core.itemblockdeclaration.ItemDeclaration;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotion;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionBlindness;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionBubbling;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionCleansing;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionCurse;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionDigslowdown;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionDigspeed;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionIncendiary;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionJump;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionRegistry;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionResistance;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionSlowfall;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionThorns;
import com.ngb.projectzulu.common.potion.subitem.SubItemPotionWaterBreathing;

import com.google.common.base.Optional;

import cpw.mods.fml.common.registry.GameRegistry;

public class PZExtraPotionDeclaration extends ItemDeclaration {

    public PZExtraPotionDeclaration() {
        super("PZCustomPotion");
    }

    @Override
    protected boolean createItem() {
        Item item = new ItemPZPotion(name);
        ItemList.customPotions = Optional.of(item);
        int i = 0;
        List<SubItemPotion> list = new ArrayList<SubItemPotion>();
        addToLists(item, i++, SubItemPotionList.BUBBLING, list, SubItemPotionBubbling.class);
        addToLists(item, i++, SubItemPotionList.INCENDIARY, list, SubItemPotionIncendiary.class);
        addToLists(item, i++, SubItemPotionList.SLOWFALL, list, SubItemPotionSlowfall.class);
        addToLists(item, i++, SubItemPotionList.CLEANSING, list, SubItemPotionCleansing.class);
        addToLists(item, i++, SubItemPotionList.CURSE, list, SubItemPotionCurse.class);
        addToLists(item, i++, SubItemPotionList.THORNS, list, SubItemPotionThorns.class);
        addToLists(item, i++, SubItemPotionList.JUMP, list, SubItemPotionJump.class);
        addToLists(item, i++, SubItemPotionList.DIG_SPEED, list, SubItemPotionDigspeed.class);
        addToLists(item, i++, SubItemPotionList.DIG_SLOW, list, SubItemPotionDigslowdown.class);
        addToLists(item, i++, SubItemPotionList.RESISTANCE, list, SubItemPotionResistance.class);
        addToLists(item, i++, SubItemPotionList.WATER_BREATHING, list, SubItemPotionWaterBreathing.class);
        addToLists(item, i++, SubItemPotionList.BLINDNESS, list, SubItemPotionBlindness.class);
        for (SubItemPotion subItemPotion : list) {
            SubItemPotionRegistry.INSTANCE.addSubPotions(subItemPotion);
        }

        return true;
    }

    @Override
    protected void registerItem() {
        Item item = ItemList.customPotions.get();
        registerSubPotions(item);
        GameRegistry.registerItem(item, name);
    }

    private void registerSubPotions(Item itemID) {
        Collection<SubItemPotion> potions = SubItemPotionRegistry.INSTANCE.getPotions(itemID);
        for (SubItemPotion subItemPotion : potions) {
            subItemPotion.register();
        }
    }

    private void addToLists(Item itemID, int subID, SubItemPotionList entry, List<SubItemPotion> registryList,
            Class<? extends SubItemPotion> potionClass) {
        try {
            SubItemPotion subItemPotion = potionClass.getConstructor(new Class[] { Item.class, int.class })
                    .newInstance(new Object[] { itemID, subID });
            entry.set(subItemPotion);
            registryList.add(subItemPotion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
