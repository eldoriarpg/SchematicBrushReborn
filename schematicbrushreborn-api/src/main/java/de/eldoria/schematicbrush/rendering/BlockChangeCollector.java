package de.eldoria.schematicbrush.rendering;

/**
 * Representing a block change collector which provides {@link Changes}.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface BlockChangeCollector {
    /**
     * The collected changes
     *
     * @return changes
     */
    Changes changes();
}
