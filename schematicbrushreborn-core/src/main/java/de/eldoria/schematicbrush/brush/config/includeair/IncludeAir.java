/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.includeair;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class IncludeAir implements Mutator<Boolean> {
    private boolean value;

    public IncludeAir(boolean value) {
        this.value = value;
    }

    public IncludeAir(Map<String, Object> objectMap) {
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
        mutation.includeAir(value);
    }

    @Override
    public Mutator<Boolean> copy() {
        return new IncludeAir(value);
    }

    @Override
    public void value(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public Boolean valueProvider() {
        return value;
    }

    @Override
    public String descriptor() {
        return String.format("%s", value);
    }

    @Override
    public String name() {
        return "Fixed";
    }
}
