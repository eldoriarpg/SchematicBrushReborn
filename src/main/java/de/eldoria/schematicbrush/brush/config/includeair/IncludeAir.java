package de.eldoria.schematicbrush.brush.config.includeair;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.PasteMutation;
import de.eldoria.schematicbrush.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class IncludeAir implements Mutator<Boolean> {
    private boolean value;

    public IncludeAir(boolean value) {
        this.value = value;
    }

    public IncludeAir(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        value = map.getValue("value");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", value)
                .build();
    }


    @Override
    public void invoke(PasteMutation mutation) {
        mutation.includeAir(value);
    }

    @Override
    public void value(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public Boolean valueProvider() {
        return value;
    }

    @Override
    public String asComponent() {
        return MessageComposer.create()
                .text("  <%s>%s", Colors.HEADING, Colors.VALUE, value)
                .build();
    }

    @Override
    public String name() {
        return "Fixed";
    }
}
