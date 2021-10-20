package de.eldoria.schematicbrush.brush.config.placement;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.SchematicMutator;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class PlacementProvider extends ModifierProvider {
    public PlacementProvider(String name) {
        super(name);
    }

    private static final APlacement bottom = new Bottom();
    private static final APlacement drop = new Drop();
    private static final APlacement middle = new Middle();
    private static final APlacement original = new Original();
    private static final APlacement raise = new Raise();
    private static final APlacement top = new Top();

    @Override
    public List<String> complete(Arguments args, Player player) {
        return Collections.emptyList();
    }

    @Override
    public SchematicMutator defaultSetting() {
        return new Drop();
    }

    public static final PlacementProvider BOTTOM = of("bottom", bottom);
    public static final PlacementProvider DROP = of("drop", drop);
    public static final PlacementProvider MIDDLE = of("middle", middle);
    public static final PlacementProvider ORIGINAL = of("original", original);
    public static final PlacementProvider RAISE = of("raise", raise);
    public static final PlacementProvider TOP = of("top", top);

    private static PlacementProvider of(String name, APlacement placement){
        return new PlacementProvider(name) {
            @Override
            public SchematicMutator parse(Arguments args) throws CommandException {
                return placement;
            }
        };
    }
}
