package de.eldoria.schematicbrush.config.sections.presets;

import com.google.gson.Gson;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DatabasePresetRegistry implements PresetRegistry {

    private static final Gson GSON = new Gson();
    private Map<UUID, PresetContainer> playerPresets = new HashMap<>();
    private PresetContainer globalPresets = new PresetContainerImpl();
    private final DataSource dataSource;
    private final Plugin plugin;

    public DatabasePresetRegistry(DataSource dataSource, Plugin plugin) {
        this.dataSource = dataSource;
        this.plugin = plugin;
        this.reload();
    }

    private void reload() {
        this.playerPresets.clear();
        this.globalPresets.getPresets().clear();
        CompletableFuture.runAsync(() -> {
            try(Connection connection = this.dataSource.getConnection()) {
                try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_presets (PLAYER_UUID longtext, PLAYER_PRESET longtext)")) {
                    preparedStatement.execute();
                }

                try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS global_presets (GLOBAL_PRESET longtext)")) {
                    preparedStatement.execute();
                }

                try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT PLAYER_UUID, PLAYER_PRESET FROM player_presets")) {
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while(resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString("PLAYER_UUID"));
                        Preset preset = this.fromJson(resultSet.getString("PLAYER_PRESET"));

                        this.getOrCreatePlayerPresets(uuid).addPreset(preset);
                    }
                }

                try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT GLOBAL_PRESET FROM global_presets")) {
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while(resultSet.next()) {
                        Preset preset = this.fromJson(resultSet.getString("GLOBAL_PRESET"));

                        this.globalPresets.addPreset(preset);
                    }
                }
            } catch (SQLException exception) {
                this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
            }
        });
    }

    private void addToDatabase(Preset preset) {
        CompletableFuture.runAsync(() -> {
            try(Connection connection = this.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO global_presets (GLOBAL_PRESET) VALUES(?)")) {
                preparedStatement.setString(1, this.toJson(preset));
                preparedStatement.execute();
            } catch (SQLException exception) {
                this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
            }
        });
    }

    private void addToDatabase(Player player, Preset preset) {
        CompletableFuture.runAsync(() -> {
            try(Connection connection = this.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_presets (PLAYER_UUID, PLAYER_PRESET) VALUES(?, ?)")) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, this.toJson(preset));
                preparedStatement.execute();
            } catch (SQLException exception) {
                this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
            }
        });
    }

    private void removeFromDatabase(String name) {
        Optional<Preset> preset = this.globalPresets.getPreset(name);

        if(preset.isEmpty()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try(Connection connection = this.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM global_presets WHERE GLOBAL_PRESET=?")) {
                preparedStatement.setString(1, this.toJson(preset.get()));
                preparedStatement.execute();
            } catch (SQLException exception) {
                this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
            }
        });
    }

    private void removeFromDatabase(Player player, String name) {
        Optional<Preset> preset = this.playerPresets.get(player.getUniqueId()).getPreset(name);

        if(preset.isEmpty()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try(Connection connection = this.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM player_presets WHERE PLAYER_UUID=?, PLAYER_PRESET=?")) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, this.toJson(preset.get()));
                preparedStatement.execute();
            } catch (SQLException exception) {
                this.plugin.getLogger().log(Level.SEVERE, exception.getMessage());
            }
        });
    }

    private String toJson(Preset preset) {
        return GSON.toJson(preset);
    }

    private Preset fromJson(String json) {
        return GSON.fromJson(json, Preset.class);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("playerPresets", playerPresets, (key, v) -> key.toString())
                .add("globalPresets", globalPresets)
                .build();
    }

    /**
     * Get a preset container of a player
     *
     * @param player player
     * @return preset container if exists
     */
    private Optional<PresetContainer> getPlayerPresets(Player player) {
        return Optional.ofNullable(playerPresets.get(player.getUniqueId()));
    }

    private PresetContainer getOrCreatePlayerPresets(Player player) {
        return playerPresets.computeIfAbsent(player.getUniqueId(), key -> new PresetContainerImpl());
    }

    private PresetContainer getOrCreatePlayerPresets(UUID uuid) {
        return playerPresets.computeIfAbsent(uuid, key -> new PresetContainerImpl());
    }

    /**
     * Get presets of a player by name
     *
     * @param player player to add
     * @param name   name
     * @return preset with this name if exists
     */
    @Override
    public Optional<Preset> getPreset(Player player, String name) {
        if (name.startsWith("g:")) {
            return globalPresets.getPreset(name.substring(2));
        }
        return getPlayerPresets(player).flatMap(p -> p.getPreset(name));
    }

    /**
     * Get a global preset by name
     *
     * @param name   name
     * @return preset with this name if exists
     */
    @Override
    public Optional<Preset> getGlobalPreset(String name) {
        return globalPresets.getPreset(name);
    }

    /**
     * Add a player preset
     *
     * @param player player
     * @param preset preset
     */
    @Override
    public void addPreset(Player player, Preset preset) {
        this.addToDatabase(player, preset);
        getOrCreatePlayerPresets(player).addPreset(preset);
    }

    /**
     * Add a global preset
     *
     * @param preset preset
     */
    @Override
    public void addPreset(Preset preset) {
        this.addToDatabase(preset);
        globalPresets.addPreset(preset);
    }

    /**
     * Remove a player preset
     *
     * @param player player
     * @param name   name
     * @return true if preset was removed
     */
    @Override
    public boolean removePreset(Player player, String name) {
        this.removeFromDatabase(player, name);
        return getPlayerPresets(player).map(p -> p.remove(name)).orElse(false);
    }

    /**
     * Remove a global preset
     *
     * @param name name
     * @return true if preset was removed
     */
    @Override
    public boolean removePreset(String name) {
        this.removeFromDatabase(name);
        return globalPresets.remove(name);
    }

    /**
     * Get presets of a player
     *
     * @param player player
     * @return all presets of the player
     */
    @Override
    public Collection<Preset> getPresets(Player player) {
        return getPlayerPresets(player).map(PresetContainer::getPresets).orElse(Collections.emptyList());
    }

    /**
     * Get global presets
     *
     * @return all global presets
     */
    @Override
    public Collection<Preset> getPresets() {
        return globalPresets.getPresets();
    }

    /**
     * Complete presets
     *
     * @param player player
     * @param arg    arguments to complete
     * @return list of possible values
     */
    @Override
    public List<String> complete(Player player, String arg) {
        if (arg.startsWith("g:")) {
            return TabCompleteUtil.complete(arg.substring(2), globalPresets.names())
                    .stream()
                    .map(name -> "g:" + name)
                    .collect(Collectors.toList());
        }
        var names = getPlayerPresets(player).map(PresetContainer::names).orElse(Collections.emptySet());
        return TabCompleteUtil.complete(arg, names);
    }

    @Override
    public int count() {
        return globalPresets.getPresets().size() + playerPresets.values().stream().mapToInt(container -> container.getPresets().size()).sum();
    }
}