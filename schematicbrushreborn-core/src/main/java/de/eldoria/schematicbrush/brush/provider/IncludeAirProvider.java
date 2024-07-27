/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.Completion;
import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.includeair.IncludeAir;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class IncludeAirProvider extends ModifierProvider {
    public static final IncludeAirProvider FIXED = new IncludeAirProvider(IncludeAir.class,
            "Fixed",
            "components.provider.includeAir.fixed.name",
            "components.provider.includeAir.fixed.description") {
        private final Argument[] arguments = {Argument.unlocalizedInput("state", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return new IncludeAir(args.asBoolean(0));
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return Completion.completeBoolean(args.asString(0));
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new IncludeAir(false);
        }
    };

    public IncludeAirProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description) {
        super(clazz, name, localizedName, description);
    }
}
