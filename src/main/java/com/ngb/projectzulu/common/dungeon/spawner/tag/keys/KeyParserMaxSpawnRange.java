package com.ngb.projectzulu.common.dungeon.spawner.tag.keys;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import com.ngb.projectzulu.common.core.ProjectZuluLog;
import com.ngb.projectzulu.common.dungeon.spawner.tag.ParsingHelper;
import com.ngb.projectzulu.common.dungeon.spawner.tag.TypeValuePair;
import com.ngb.projectzulu.common.dungeon.spawner.tag.settings.OptionalSettings.Operand;

public class KeyParserMaxSpawnRange extends KeyParserBase {

    public KeyParserMaxSpawnRange(Key key) {
        super(key, false, KeyType.VALUE);
    }

    @Override
    public boolean parseChainable(String parseable, ArrayList<TypeValuePair> parsedChainable,
            ArrayList<Operand> operandvalue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean parseValue(String parseable, HashMap<String, Object> valueCache) {
        String[] pieces = parseable.split(",");
        if (pieces.length == 2) {
            valueCache.put(Key.maxSpawnRange.key,
                    ParsingHelper.parseFilteredInteger(pieces[1], 0, Key.maxSpawnRange.key));
            return true;
        } else {
            ProjectZuluLog.severe("Error Parsing Needs EntityCap parameter. Invalid Argument Length.");
            return false;
        }
    }

    @Override
    public boolean isValidLocation(World world, EntityLiving entity, int xCoord, int yCoord, int zCoord,
            TypeValuePair typeValuePair, HashMap<String, Object> valueCache) {
        throw new UnsupportedOperationException();
    }
}