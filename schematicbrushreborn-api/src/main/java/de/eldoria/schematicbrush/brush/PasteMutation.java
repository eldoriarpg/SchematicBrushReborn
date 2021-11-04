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
