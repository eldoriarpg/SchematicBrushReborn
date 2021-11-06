/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChangesImpl implements Changes {
    private final Map<Location, BlockData> changed;
    private final Map<Location, BlockData> original;

    private ChangesImpl(Map<Location, BlockData> changed, Map<Location, BlockData> original) {
        this.changed = changed;
        this.original = original;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void show(Player player) {
        sendChanges(player, changed);
    }

    @Override
    public void hide(Player player) {
        sendChanges(player, original);
    }

    private void sendChanges(Player player, Map<Location, BlockData> data) {
        for (var entry : data.entrySet()) {
            player.sendBlockChange(entry.getKey(), entry.getValue());
        }
    }

    public static class Builder {
        private final Map<Location, BlockData> changed = new HashMap<>();
        private final Map<Location, BlockData> original = new HashMap<>();

        public void add(Location location, BlockData original, BlockData changed) {
            if (original.matches(changed)) return;
            this.original.put(location, original);
            this.changed.put(location, changed);
        }

        public Changes build() {
            return new ChangesImpl(changed, original);
        }
    }
}
