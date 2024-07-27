/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.schematicbrush.brush.config.placement.APlacement;
import de.eldoria.schematicbrush.brush.config.placement.Bottom;
import de.eldoria.schematicbrush.brush.config.placement.Drop;
import de.eldoria.schematicbrush.brush.config.placement.Middle;
import de.eldoria.schematicbrush.brush.config.placement.Original;
import de.eldoria.schematicbrush.brush.config.placement.Raise;
import de.eldoria.schematicbrush.brush.config.placement.Top;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class PlacementProvider extends ModifierProvider {
    private static final APlacement bottom = new Bottom();
    public static final PlacementProvider BOTTOM = of("Bottom", bottom, "components.provider.placement.bottom.name", "components.provider.placement.bottom.description");
    private static final APlacement drop = new Drop();
    public static final PlacementProvider DROP = of("Drop", drop, "components.provider.placement.drop.name", "components.provider.placement.drop.description");
    private static final APlacement middle = new Middle();
    public static final PlacementProvider MIDDLE = of("Middle", middle, "components.provider.placement.middle.name", "components.provider.placement.middle.description");
    private static final APlacement original = new Original();
    public static final PlacementProvider ORIGINAL = of("Original", original, "components.provider.placement.original.name", "components.provider.placement.original.description");
    private static final APlacement raise = new Raise();
    public static final PlacementProvider RAISE = of("Raise", raise, "components.provider.placement.raise.name", "components.provider.placement.raise.description");
    private static final APlacement top = new Top();
    public static final PlacementProvider TOP = of("Top", top, "components.provider.placement.top.name", "components.provider.placement.top.description");

    public PlacementProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description) {
        super(clazz, name, localizedName, description);
    }

    private static PlacementProvider of(String name, APlacement placement, String localizedName, String description) {
        return new PlacementProvider(placement.getClass(), name, localizedName, description) {
            @Override
            public Mutator<?> parse(Arguments args) {
                return placement;
            }
        };
    }

    @Override
    public List<String> complete(Arguments args, Player player) {
        return Collections.emptyList();
    }

    @Override
    public Mutator<?> defaultSetting() {
        return new Drop();
    }

    @Override
    public boolean hasArguments() {
        return false;
    }
}
