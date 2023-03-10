/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.world.World;
import org.bukkit.entity.Player;

/**
 * Represents a paste mutation, which will be applied when the brush is pasted.
 */
public interface PasteMutation {
    static ParserContext createContext(Actor actor, Extent clipboard, World world) {
        var parserContext = new ParserContext();
        parserContext.setActor(actor);
        parserContext.setExtent(clipboard);
        parserContext.setRestricted(true);
        parserContext.setPreferringWildcard(false);
        parserContext.setTryLegacy(true);
        parserContext.setWorld(world);
        return parserContext;
    }

    /**
     * Clipboard of next paste.
     *
     * @return clipboard
     */
    Clipboard clipboard();

    /**
     * Transform of next paste
     *
     * @return transform
     */
    AffineTransform transform();

    /**
     * session of next paste
     *
     * @return session
     */
    EditSession session();

    /**
     * paste offset of next paste
     *
     * @return offset
     */
    BlockVector3 pasteOffset();

    /**
     * Include air
     *
     * @return true if air should be included
     */
    boolean isIncludeAir();

    /**
     * A mask which will be applied on the clipboard.
     *
     * @return a mask
     */
    Mask maskSource();

    /**
     * Set the mask which will be applied on the clipboard.
     *
     * @param mask mask
     */
    void maskSource(Mask mask);

    /**
     * Set the clipboard of the paste
     *
     * @param clipboard clipboard
     */
    void clipboard(Clipboard clipboard);

    /**
     * Set the transform of the paste
     *
     * @param transform transform
     */
    void transform(AffineTransform transform);

    /**
     * Set the paste offset
     *
     * @param pasteOffset paste offset
     */
    void pasteOffset(BlockVector3 pasteOffset);

    /**
     * Set the include air
     *
     * @param includeAir includeair
     */
    void includeAir(boolean includeAir);

    /**
     * Get the actor of the paste.
     *
     * @return actor
     */
    default Actor actor() {
        return BukkitAdapter.adapt(player());
    }

    /**
     * Get the owner of the brush
     *
     * @return the owner
     */
    Player player();

    /**
     * Gets a parser context for the paste action
     *
     * @return a new parser action
     */
    default ParserContext parserContext() {
        return createContext(actor(), clipboard(), session().getWorld());
    }
}
