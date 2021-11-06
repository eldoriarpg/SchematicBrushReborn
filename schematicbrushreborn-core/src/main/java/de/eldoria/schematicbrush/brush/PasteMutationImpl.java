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
 * Class representing mutations to a paste operation of a {@link SchematicBrush}
 */
public class PasteMutationImpl implements PasteMutation {
    private final EditSession session;
    private Clipboard clipboard;
    private AffineTransform transform = new AffineTransform();
    private BlockVector3 pasteOffset = BlockVector3.ZERO;
    private boolean includeAir;

    public PasteMutationImpl(Clipboard clipboard, EditSession session) {
        this.clipboard = clipboard;
        this.session = session;
    }

    /**
     * Clipboard of next paste.
     *
     * @return clipboard
     */
    @Override
    public Clipboard clipboard() {
        return clipboard;
    }

    /**
     * Transform of next paste
     *
     * @return transform
     */
    @Override
    public AffineTransform transform() {
        return transform;
    }

    /**
     * session of next paste
     *
     * @return session
     */
    @Override
    public EditSession session() {
        return session;
    }

    /**
     * paste offset of next paste
     *
     * @return offset
     */
    @Override
    public BlockVector3 pasteOffset() {
        return pasteOffset;
    }

    /**
     * Include air
     *
     * @return true if air should be included
     */
    @Override
    public boolean isIncludeAir() {
        return includeAir;
    }

    /**
     * Set the clipboard of the paste
     *
     * @param clipboard clipboard
     */
    @Override
    public void clipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    /**
     * Set the transform of the paste
     *
     * @param transform transform
     */
    @Override
    public void transform(AffineTransform transform) {
        this.transform = transform;
    }

    /**
     * Set the paste offset
     *
     * @param pasteOffset paste offset
     */
    @Override
    public void pasteOffset(BlockVector3 pasteOffset) {
        this.pasteOffset = pasteOffset;
    }

    /**
     * Set the include air
     *
     * @param includeAir includeair
     */
    @Override
    public void includeAir(boolean includeAir) {
        this.includeAir = includeAir;
    }
}
