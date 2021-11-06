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

package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;

/**
 * Represents a paste mutation, which will be applied when the brush is pasted.
 */
public interface PasteMutation {
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
}
