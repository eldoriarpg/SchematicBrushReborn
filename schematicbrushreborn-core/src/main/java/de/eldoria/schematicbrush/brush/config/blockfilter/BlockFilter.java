/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.blockfilter;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Masks;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class BlockFilter implements Mutator<String> {
    private static final WorldEdit WORLD_EDIT = WorldEdit.getInstance();
    private final String maskString;

    public BlockFilter(String maskString) {
        this.maskString = maskString;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        mutation.maskSource(mask(mutation));
    }

    @Override
    public Mutator<String> copy() {
        return new BlockFilter(maskString);
    }

    @Override
    public String name() {
        return "BlockFilter";
    }

    @Override
    public String descriptor() {
        return maskString.isBlank() ? "None" : maskString;
    }

    @Override
    public String localeKey() {
        return "provider.filter.blockFilter.name";
    }

    @Override
    public void value(String value) {
    }

    @Override
    public String value() {
        return maskString;
    }

    @Override
    public String valueProvider() {
        return maskString;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("mask", maskString)
                .build();
    }

    private Mask mask(PasteMutation mutation) {
        if (maskString.isBlank()) return Masks.alwaysTrue();
        try {
            return WORLD_EDIT.getMaskFactory().parseFromInput(maskString, mutation.parserContext());
        } catch (InputParseException e) {
            SchematicBrushReborn.logger().log(Level.WARNING, "Could not parse saved mask " + maskString + ".", e);
        }
        return Masks.alwaysTrue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockFilter blockFilter)) return false;

        return maskString.equals(blockFilter.maskString);
    }

    @Override
    public int hashCode() {
        return maskString.hashCode();
    }
}
