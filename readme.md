![GitHub Workflow Status](https://img.shields.io/github/workflow/status/eldoriarpg/SchematicBrushReborn/Verify%20state?style=for-the-badge&label=Building)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/eldoriarpg/SchematicBrushReborn/Publish%20to%20Nexus?style=for-the-badge&label=Publishing) \
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/maven-releases/de.eldoria/schematicbrushreborn-api?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Development)](https://img.shields.io/nexus/maven-dev/de.eldoria/schematicbrushreborn-api?label=DEV&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/de.eldoria/schematicbrushreborn-api?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge) \
[![wakatime](https://wakatime.com/badge/github/eldoriarpg/SchematicBrushReborn.svg)](https://wakatime.com/badge/github/eldoriarpg/SchematicBrushReborn)

# Schematic Brush Reborn

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

You miss something? Use the api to add your own schematic modifier or selectors. See the wiki for a example.

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

## Commands

Schematic Brush reborn has over 30 Commands, but you will only need 3.

### Create a brush

Use `/sbr` to create a new brush builder.

This will also load the brush in your hand if you hold a Schematic Brush. Remember to bind the edited brush again to
change the settings.

### Refresh the schematics of your brush

Use `/sbr reloadSchematics` to reload the schematics selected by your schematic sets.

### Manage your presets

Use `/sbrp` to manage your presets.

### Some more notable commands

`/sbrs preview <state>` to enable or disable schematic preview.
`/sbrs showNames <state>` to enable or disable printing the name of the pasted schematic
`/sbra reload` to reload plugin configuration.
`/sbra reloadcache` to reload the schematic cache
`/sbra debug` to create debug pastes for the plugin.

## Permissions

| Permission | Usage|
| ----- | ---- |
| schematicbrush.brush.use | To use the schematic brush. |
| schematicbrush.brush.preview | To use the schematic preview. |
| schematicbrush.preset.use | To use presets in general. |
| schematicbrush.preset.global | To save and delete global presets. |
| schematicbrush.admin.reload | Reload the plugin configuration `/sbra reload`. |
| schematicbrush.admin.reloadcache | To reload the schematic cache. |
| schematicbrush.admin.debug | To create debug pastes for bugreports. |

![## Features](http://chojo.u.catgirlsare.sexy/64F0fGlX.png)

+ Use schematics from your server as a world edit brush.
+ Modify your current brush every time without creating a new one.
+ Customize your brush with different flags
+ Use weighted schematic sets to keep control of your schematics.
+ Use random schematic sets to get more diversity.
+ Save your favorite schematics sets as a set to use them everytime.

## Requirements]

Spigot or Paper 1.13 and above.  
Java 16 and above.  
WorldEdit 7.0 or FAWE #388 or newer.  
