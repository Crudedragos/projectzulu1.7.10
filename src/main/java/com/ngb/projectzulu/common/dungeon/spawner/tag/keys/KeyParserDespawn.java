package com.ngb.projectzulu.common.dungeon.spawner.tag.keys;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import com.ngb.projectzulu.common.dungeon.spawner.tag.TypeValuePair;
import com.ngb.projectzulu.common.dungeon.spawner.tag.settings.OptionalSettings.Operand;

public class KeyParserDespawn extends KeyParserBase {

    public KeyParserDespawn(Key key) {
        super(key, true, KeyType.PARENT);
    }

    @Override
    public boolean parseChainable(String parseable, ArrayList<TypeValuePair> parsedChainable,
            ArrayList<Operand> operandvalue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean parseValue(String parseable, HashMap<String, Object> valueCache) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValidLocation(World world, EntityLiving entity, int xCoord, int yCoord, int zCoord,
            TypeValuePair typeValuePair, HashMap<String, Object> valueCache) {
        throw new UnsupportedOperationException();
    }
}