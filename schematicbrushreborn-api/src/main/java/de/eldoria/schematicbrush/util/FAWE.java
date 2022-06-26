/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import org.bukkit.Bukkit;

import java.util.function.Supplier;

public final class FAWE {
    public static boolean isFawe() {
        return isFawe.get();
    }

    private static Supplier<Boolean> isFawe = () -> {
        var fawe = Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
        isFawe = () -> fawe;
        SchematicBrushReborn.getInstance().getLogger().info("Detected FAWE: " + fawe);
        return fawe;
    };


}
