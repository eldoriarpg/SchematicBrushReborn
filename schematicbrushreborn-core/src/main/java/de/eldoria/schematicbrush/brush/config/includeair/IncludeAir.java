/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.includeair;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("sbrIncludeAir")
public class IncludeAir implements Mutator<Boolean> {
    private boolean value;

    @JsonCreator
    public IncludeAir(@JsonProperty("value") boolean value) {
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
    public void value(@NotNull Boolean value) {
        this.value = value;
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
    public String descriptor() {
        return String.format("%s", value);
    }

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public String localizedName() {
        return ILocalizer.escape("components.modifier.includeAir.fixed.name");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IncludeAir includeAir)) return false;

        return value == includeAir.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }
}
