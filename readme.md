[![Release](https://img.shields.io/nexus/maven-releases/de.eldoria/schematicbrushreborn-api?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][release]
[![Snapshot](https://img.shields.io/nexus/s/de.eldoria/schematicbrushreborn-api?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][snapshot]

![](https://imgur.com/4t8efNZ.png "Schematic Brush Reborn")
Schematic Brush Reborn is a revive of the old [Schematic Brush](https://github.com/mikeprimm/SchematicBrush).

However it is not really the old schematic brush and has a way better usability. It features several new features which
will make creating Schematic Brushes as simple as possible!

## Core Features

### Rich Text Editor UI

Use the text editor to adjust your schematic brush blazingly fast. You dont need to remember commands anymore. Just
click and it will happen!

### Modify what and how you paste

Use the selectors to directly select only the schematics you want. Select them by name, directory or use a regex if you
want.

Full control how you paste. Use different placing methods, apply and offset, prevent block replacements.

Want more diversity? Use the schematic modifier to apply different rotations or flips on your schematic.

### Save what you need

Save your favourite schematic sets as a preset to load them quickly when you need them again. You can have your own
personal presets or share them globally with other users on your server.

### Schematic preview

See what you will paste before you paste. You can even adjust the rotation or the flip direction.

### Strong api

You miss something? Use the api to add your own schematic modifier or selectors. See the
[wiki](https://github.com/eldoriarpg/SchematicBrushReborn/wiki/API) for a example.


## Dependency

**Gradle**

``` kotlin
repositories {
    maven("https://eldonexus.de/repository/maven-public")
}

dependencies {
    compileOnly("de.eldoria", "schematicbrushreborn-api", "version")
}
```

**Maven**

``` xml
<repository>
    <id>EldoNexus</id>
    <url>https://eldonexus.de/repository/maven-public/</url>
</repository>

<dependency>
    <groupId>de.eldoria</groupId>
    <artifactId>schematicbrushreborn-api</artifactId>
    <version>version</version>
</dependency>
```

[release]: https://eldonexus.de/#browse/browse:maven-releases:de%2Feldoria%2Fschematicbrushreborn-api
[snapshot]: https://eldonexus.de/#browse/browse:maven-snapshots:de%2Feldoria%2Fschematicbrushreborn-api
