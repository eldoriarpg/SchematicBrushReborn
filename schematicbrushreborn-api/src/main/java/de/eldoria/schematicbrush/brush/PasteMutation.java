package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;

/**
 * Class representing mutations to a paste operation of a {@link SchematicBrush}
 */
public class PasteMutation {
    private Clipboard clipboard;
    private AffineTransform transform = new AffineTransform();
    private EditSession session;
    private BlockVector3 pasteOffset = BlockVector3.ZERO;
    private boolean includeAir;

    public PasteMutation(Clipboard clipboard, EditSession session) {
        this.clipboard = clipboard;
        this.session = session;
    }

    /**
     * Clipboard of next paste.
     *
     * @return clipboard
     */
    public Clipboard clipboard() {
        return clipboard;
    }

    /**
     * Transform of next paste
     *
     * @return transform
     */
    public AffineTransform transform() {
        return transform;
    }

    /**
     * session of next paste
     *
     * @return session
     */
    public EditSession session() {
        return session;
    }

    /**
     * paste offset of next paste
     *
     * @return offset
     */
    public BlockVector3 pasteOffset() {
        return pasteOffset;
    }

    /**
     * Include air
     *
     * @return true if air should be included
     */
    public boolean isIncludeAir() {
        return includeAir;
    }

    /**
     * Set the clipboard of the paste
     *
     * @param clipboard clipboard
     */
    public void clipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    /**
     * Set the transform of the paste
     *
     * @param transform transform
     */
    public void transform(AffineTransform transform) {
        this.transform = transform;
    }

    /**
     * Set the paste offset
     *
     * @param pasteOffset paste offset
     */
    public void pasteOffset(BlockVector3 pasteOffset) {
        this.pasteOffset = pasteOffset;
    }

    /**
     * Set the include air
     *
     * @param includeAir includeair
     */
    public void includeAir(boolean includeAir) {
        this.includeAir = includeAir;
    }
}
