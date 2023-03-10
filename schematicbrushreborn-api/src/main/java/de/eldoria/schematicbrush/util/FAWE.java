/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import org.bukkit.Bukkit;

import java.util.function.Supplier;

public final class FAWE {
    /**
     * Check if FAWE is used on this server.
     * @return true if FAWE was found.
     */
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
