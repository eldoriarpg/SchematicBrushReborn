package de.eldoria.schematicbrush.brush.config.util;

/**
 * Interface which provides components
 */
public interface ComponentProvider {
    /**
     * Name of component
     *
     * @return name as string
     */
    String name();

    /**
     * Descriptor of component. Should provide the value of the component.
     *
     * @return value as string
     */
    String descriptor();
}
