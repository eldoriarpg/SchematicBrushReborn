/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

public final class UserData {

    private UserData() {
    }

    public static UserData get(){
        return new UserData();
    }

    public final String user = "%%__USER__%%";
    public final String resource = "%%__RESOURCE__%%";
    public final String nonce = "%%__NONCE__%%";

    public int resourceId() {
        return Integer.parseInt(resource);
    }

    public boolean isPremium() {
        return !user.equals(String.join("", new String[]{"%%__", "USER", "__%%"}));
    }

    public String asString() {
        return String.format("Premium: %s%nUser: %s%nNonce: %s", isPremium(), user, nonce);
    }
}
