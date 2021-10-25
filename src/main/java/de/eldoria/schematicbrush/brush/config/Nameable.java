package de.eldoria.schematicbrush.brush.config;

public abstract class Nameable {
    private final String name;

    public Nameable(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public static Nameable of(String name){
        return new Nameable(name) {
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nameable)) return false;

        Nameable nameable = (Nameable) o;

        return name.equals(nameable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
