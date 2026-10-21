/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

/**
 * Whether the server is told about new Schematic Brush versions and whether they are downloaded.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "CanBeFinal"})
public class UpdateSettings {
    private boolean notice = true;
    private boolean autoUpdate = false;
    private boolean askForAutoUpdate = true;

    public UpdateSettings() {
    }

    /**
     * Creates settings from the update switch of older configs.
     *
     * @param notice whether update checks were on
     */
    UpdateSettings(boolean notice) {
        this.notice = notice;
    }

    /**
     * Whether the console and admins hear about a new version.
     *
     * @return true if new versions are announced
     */
    public boolean notice() {
        return notice;
    }

    /**
     * Whether a new stable release is downloaded, to take effect on the next start.
     *
     * @return true if updates are installed automatically
     */
    public boolean autoUpdate() {
        return autoUpdate;
    }

    /**
     * Whether admins are asked in chat to switch automatic updates on.
     *
     * @return true if admins are asked
     */
    public boolean askForAutoUpdate() {
        return askForAutoUpdate;
    }

    /**
     * Switches automatic updates on, as an admin chose in chat.
     */
    public void enableAutoUpdate() {
        autoUpdate = true;
    }

    /**
     * Stops asking admins to switch automatic updates on, as an admin chose in chat.
     */
    public void stopAsking() {
        askForAutoUpdate = false;
    }
}
