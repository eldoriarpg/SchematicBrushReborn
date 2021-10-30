package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.math.BlockVector3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SnapshotContainerTest {

    @Test
    void toInnerChunkCoord() {
        Assertions.assertEquals(BlockVector3.at(0, 1, 0), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(0, 1, 0)));
        Assertions.assertEquals(BlockVector3.at(15, 1, 15), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(15, 1, 15)));
        Assertions.assertEquals(BlockVector3.at(0, 1, 0), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(16, 1, 16)));
        Assertions.assertEquals(BlockVector3.at(15, 1, 15), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(31, 1, 31)));

        Assertions.assertEquals(BlockVector3.at(15, 1, 15), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(-1, 1, -1)));
        Assertions.assertEquals(BlockVector3.at(0, 1, 0), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(-16, 1, -16)));
        Assertions.assertEquals(BlockVector3.at(15, 1, 15), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(-17, 1, -17)));
        Assertions.assertEquals(BlockVector3.at(0, 1, 0), SnapshotContainer.toInnerChunkCoord(BlockVector3.at(-32, 1, -32)));
    }
}
