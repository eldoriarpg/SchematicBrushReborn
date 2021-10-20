package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;

public class PasteMutation {
    private Clipboard clipboard;
    private AffineTransform transform = new AffineTransform();
    private EditSession session;
    private BlockVector3 pasteOffset = BlockVector3.ZERO;
    private boolean includeAir = false;

    public PasteMutation(Clipboard clipboard, EditSession session) {
        this.clipboard = clipboard;
        this.session = session;
    }

    public Clipboard clipboard() {
        return clipboard;
    }

    public AffineTransform transform() {
        return transform;
    }

    public EditSession session() {
        return session;
    }

    public void clipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    public void transform(AffineTransform transform) {
        this.transform = transform;
    }

    public void session(EditSession session) {
        this.session = session;
    }

    public BlockVector3 pasteOffset() {
        return pasteOffset;
    }

    public void pasteOffset(BlockVector3 pasteOffset) {
        this.pasteOffset = pasteOffset;
    }

    public boolean isIncludeAir() {
        return includeAir;
    }

    public void includeAir(boolean includeAir) {
        this.includeAir = includeAir;
    }
}
