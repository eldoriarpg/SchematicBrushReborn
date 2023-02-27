/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C EldoriaRPG Team and Contributor
 */
package de.eldoria.schematicbrush.util;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class UserData {

    public final String type;
    private final Map<String, String> buildProperties;
    public final String user = "%%__USER__%%";
    public final String resource = "%%__RESOURCE__%%";
    public final String nonce = "%%__NONCE__%%";

    private static UserData data = null;

    private UserData(Map<String, String> buildProperties) {
        type = buildProperties.getOrDefault("type", "LOCAL");
        this.buildProperties = buildProperties;
    }

    public static UserData get(Plugin plugin) {
        if (data != null) {
            return data;
        }

        Map<String, String> buildProperties = new HashMap<>();
        try (var in = plugin.getResource("build.data")) {
            if (in != null) {
                buildProperties = Arrays.stream(new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n"))
                        .filter(r -> !r.isBlank())
                        .map(e -> e.split("=", 2))
                        .filter(e -> e.length == 2)
                        .collect(Collectors.toMap(e -> e[0], e -> e[1]));
            }
        } catch (IOException e) {

        }

        data = new UserData(buildProperties);
        return data;
    }

    public int resourceId() {
        return Integer.parseInt(resource);
    }


    public String resource() {
        if ("PATREON".equalsIgnoreCase(type)) {
            return "https://www.patreon.com/eldoriaplugins";
        }
        return resource;
    }

    public boolean isSpigotPremium() {
        return !user.equals(String.join("", new String[]{"%%__", "USER", "__%%"}));
    }

    public boolean isPremium() {
        if ("PATREON".equalsIgnoreCase(type)) {
            return true;
        }
        return !user.equals(String.join("", new String[]{"%%__", "USER", "__%%"}));
    }

    public String user() {
        if ("PATREON".equalsIgnoreCase(type)) {
            return type;
        }
        return user;
    }

    public String asString() {
        List<String> properties = new ArrayList<>();
        properties.add("Premium: " + isPremium());
        properties.add("User: " + user());
        properties.add("Nonce " + nonce);
        buildProperties.entrySet().stream().map(e -> "%s: %s".formatted(e.getKey(), e.getValue())).forEach(properties::add);
        return String.join("\n", properties);
    }
}
