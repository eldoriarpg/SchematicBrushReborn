package de.eldoria.schematicbrush.brush.config.includeair;

import de.eldoria.schematicbrush.brush.config.PasteMutation;
import de.eldoria.schematicbrush.brush.config.SchematicMutator;

public class IncludeAir implements SchematicMutator {
    private final boolean value;

    public IncludeAir(boolean value) {
        this.value = value;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        mutation.includeAir(value);
    }
}
