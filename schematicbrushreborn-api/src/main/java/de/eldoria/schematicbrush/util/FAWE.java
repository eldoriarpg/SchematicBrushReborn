package de.eldoria.schematicbrush.util;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import org.bukkit.Bukkit;

import java.util.function.Supplier;

public final class FAWE {
    private static Supplier<Boolean> isFawe = () -> {
        var fawe=   Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
        isFawe = () -> fawe;
        SchematicBrushReborn.getInstance().getLogger().info("Detected FAWE: " + fawe);
        return fawe;
    };

    public static boolean isFawe(){
        return isFawe.get();
    }
}
