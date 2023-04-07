/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.replaceall;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("sbrReplaceAll")
public class ReplaceAll implements Mutator<Boolean> {
    private final boolean value;

    @JsonCreator
    public ReplaceAll(@JsonProperty("value") boolean value) {
        this.value = value;
    }

    public ReplaceAll(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        value = map.getValue("value");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", value)
                .build();
    }

    @Override
    public void invoke(PasteMutation mutation) {
        var preBrushMask = mutation.session().getMask();
        // Apply replace mask
        if (!value) {
            // Check if the user has a block mask defined and append if present.
            //Mask mask = WorldEditBrushAdapter.getMask(brushOwner);
            if (preBrushMask instanceof BlockTypeMask blockMask) {
                blockMask.add(BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR);
            } else {
                mutation.session().setMask(
                        new BlockTypeMask(mutation.session(), BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR));
            }
        } else {
            mutation.session().setMask(Masks.alwaysTrue());
        }
    }

    @Override
    public Mutator<Boolean> copy() {
        return new ReplaceAll(value);
    }

    @Override
    public void value(@NotNull Boolean value) {
    }

    @Override
    public @NotNull Boolean value() {
        return value;
    }

    @Override
    public Boolean valueProvider() {
        return value;
    }

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public String descriptor() {
        return String.format("%s", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplaceAll replaceAll)) return false;

        return value == replaceAll.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }
}
