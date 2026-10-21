/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.updates;

import de.eldoria.eldoutilities.updater.AutoUpdateDecision;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateResponse;
import de.eldoria.schematicbrush.config.sections.UpdateSettings;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Looks for new Schematic Brush versions on Eldoria's release service Lyna, at start and every six hours after that.
 * <p>
 * A new version is told to the console and, on join, to players with {@link Permissions#UPDATES}. Premium builds can
 * download it: automatically when auto update is on, otherwise from the update message, which also offers to switch
 * auto update on. The admin's choice is written to the config. A new major version is never installed automatically.
 * Other builds cannot download from Lyna and only announce new versions.
 */
public final class Updates {
    /**
     * Lyna's download type of the Paper build, since the product also lists Spigot and legacy builds.
     */
    static final int PAPER_STABLE = 4;

    private final Plugin plugin;
    private final Supplier<UpdateSettings> settings;
    private final Runnable save;
    private final boolean premium;
    private @Nullable Updater<LynaUpdateResponse, LynaUpdateData> updater;

    /**
     * @param plugin   the plugin whose versions are looked up
     * @param settings the current update settings
     * @param save     saves the config after an admin's choice changed the settings
     * @param premium  whether this build may download from Lyna
     */
    public Updates(Plugin plugin, Supplier<UpdateSettings> settings, Runnable save, boolean premium) {
        this.plugin = plugin;
        this.settings = settings;
        this.save = save;
        this.premium = premium;
    }

    /**
     * Applies the current update settings: starts looking for new versions, hands the running updater the new
     * settings, or stops it when the settings neither want to hear about new versions nor download them.
     */
    public void apply() {
        var current = settings.get();
        if (!current.notice() && !(premium && current.autoUpdate())) {
            stop();
            return;
        }
        var data = data(current);
        if (updater == null) {
            updater = Updater.lyna(data);
            updater.start();
        } else {
            updater.reconfigure(data);
        }
    }

    /**
     * Stops looking for new versions.
     */
    public void stop() {
        if (updater != null) updater.stop();
        updater = null;
    }

    LynaUpdateData data(UpdateSettings current) {
        var builder = LynaUpdateData.builder(plugin)
                                    .notifyPermission(Permissions.UPDATES)
                                    .notifyUpdate(current.notice())
                                    .downloadType(PAPER_STABLE);
        if (!premium) return builder.build();
        return builder.autoUpdate(current.autoUpdate())
                      .askForAutoUpdate(current.askForAutoUpdate())
                      .autoUpdateHook(this::decided)
                      .build();
    }

    void decided(AutoUpdateDecision decision) {
        switch (decision) {
            case ENABLED -> settings.get().enableAutoUpdate();
            case NEVER_ASK -> settings.get().stopAsking();
        }
        save.run();
    }
}
