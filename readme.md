# Schematic Brush Reborn
![## Description](http://chojo.u.catgirlsare.sexy/wHmbIpte.png)  

Schematic Brush Reborn is a complete new rewrite of the old [Schematic Brush](https://github.com/mikeprimm/SchematicBrush)
which doesn't received any updates for version 1.13+.

So we decided to write it new from scratch and make it even better than before. We added several new functionalities,
which should make using schematic brushes easier.

We added tabcomplete for nearly everything. You can start directly and learn from the plugin on the fly how to use it.
We load schematics from our own schematic folder, of FAWE and World Edit. So we dont care, where you store your schematics.
We support every schematic format which World Edit supports.

We allow you to save your favourite schematic sets as a preset.
You can use them every time and even combine them with other presets.

![## Features](http://chojo.u.catgirlsare.sexy/64F0fGlX.png)  

+ Use schematics from your server as a world edit brush.
+ Modify your current brush every time without creating a new one.
+ Customize your brush with different flags
+ Use weighted schematic sets to keep control of your schematics.
+ Use random schematic sets to get more diversity.
+ Save your favourite schematics sets as a set to use them everytime.

![## Requirements](http://chojo.u.catgirlsare.sexy/jtvCkmgC.png)  

Spigot or Paper 1.13 and above.  
Java 11 and above.  
World Edit 7.1 and above.  
FAWE is also supported, but on the current state pretty unstable.  

![## How does it work?](http://chojo.u.catgirlsare.sexy/V05UGdwq.png)  

A schematic brush can contain one or more schematic sets. So a brush is a collection of one or more schematic sets.

A schematic set is defined by a selector and several modifications of the schematic. The general syntax is:

`<selector>@rotation!flip:weight`

The modifiers are optional and the order doesn't matter.

The brush itself has some flags, which allows to set a place method, offset and replacement rules.

`/sbr <schematic sets...> <flags...>`

You don't have to remember any of this. The plugin features a rich tabcomplete, which supports you all the time.

![autocomplete image](http://chojo.u.catgirlsare.sexy/WJ3Eh7N2.png)  

We also track your schematic, preset and directory names. We can complete everything for you.

### Schematic Set
A schematic set consists of a selector an optional modifier.
#### Selector
A selector can be several things.

##### Name
A name. This will use every schematic which exactly matches this name.
If you want a broader search you can add a * at the and. This will use every schematic which starts with the name.

`<name>@rotation!flip:weight`
+ `tree_*` - this will match all schematics which start with tree_ 
+ `tree_acacia_01` - This will match the schematic with the name 'tree_acacia_01'

##### Regex
 If you want it more specific you can use a regex too. A regex has to start with a `^`.

`^<regex>@rotation!flip:weight`
+ `^tree_.+?` - This will match all schematics that start with tree
+ `^tree_acacia_[0-9]{1,3}` - This will match all schematics that start with tree_acacia_ and have the number 1-999 at the end.
+ For further information I recommend this [site](https://www.regextester.com/)

##### Directory
If you are an organized humen, you have all your schematics properly sorted in folder.
You can select all schematics inside a folder with:

`$<directory>@rotation!flip:weight`
+ `$tree` - Uses all schematics inside the folder trees

Currently only the direct folder inside the schematics folder is valid. No subfolders of these.

##### Preset
A saved preset can be loaded with:
`&<preset>@rotation!flip:weight`

#### Modifier
The modifier are optional. The order doesn't matter.

The entered values will be applied before pasting the schematic.

Every modifier has some explicit values.
For for flip and rotation you can use a `*` for a random value.
These will change everytime the brush pastes a schematic.

##### Flip
You can flip a schematic on the North South Axis (N) or on the East West Axis (E). Probably all values are valid.
But they do all the same. So N is equals to S and NS and SN.
If the `*` is used the rotation will be None, N and E.

`!flip`

##### Rotation
The rotation can be 0 which is default, 90, 180 or 270.
If the `*` is used the rotation will be 0, 90, 180 or 270.

`@rotation`

##### Weight
The weight defines the chance that the brush is used if a schematic brush contains more than one brush.
If a brush doesn't has a weight the average of all weighted brushes will be used.

`:weight`

### Brush Flags
A brush can have several flags which change the behaviour of the brush and the placement of the schematics.
All flags have a short form.

#### Placement
A schematic can be placed in several method.
Before a schematic is placed, we modify the origin of the schematic.
The origin position is equal to the block you pointed at with the block.
The origin is always at the x and z center of your schematic, but we allow you to change the y value which is most important.

**Middle**: The origin will be in the y center of the schematic
**Bottom**: The origin will be on the lowest point of you schematic
**Drop**: The origin will be at the lowest non air block in your schematic. This is the default value.
**Top**: The origin will be on the highest point of you schematic
**Drop**: The origin will be at the highest non air block in your schematic

`-placement:type`
`-place:type`
`-p:type`

Hint you can also use only the first letter of the type. This means that: `-p:d` is equal to `-placement:drop`

#### YOffset
The y offset is applied after the placement changes. You can raise or lower a schematic before pasting.
This is useful for trees if you want be sure, that they will not be placed on top of gras.

`-yoffset:number`
`-yoff:number`
`-y:number`

#### Replaceall
On default the schematic brush only replaces air. We dont want to destroy existing structures.
This will change the behavioud. So we can replace blocks, which are solid.

`-replaceall`
`-repla`
`-r`

#### Placeair
On default we dont place air block. If you want to change this use this flag.

`-includeair`
`-incair`
`-a`

![## Commands](http://chojo.u.catgirlsare.sexy/ovFjigc1.png)  
All command profide the information listed below, if you use them withour any arguments.
All command have a rich tabcompletion which always helps you with building your brush

### Schematicbrush
Alias: `schbr`, `sbr`

Argumente: `<schematic sets...> <flags>`
Permission: `schematicbrush.brush.use`

This command is be core of schematic brush. It allows you to create a schematic brush on a item.

### SchematicBrushModify
Alias: `schrbm` `sbrm`
Permission: `schematicbrush.brush.use`

This command allows you to modify your current equiped brush

#### append
Appends one or more schematic sets to your brush.

`sbrm append <schematic sets...>`
`sbrm a <schematic sets...>`

#### remove
Remove a schematic set from your brush. The id can be found with the info command.

`sbrm remove <id>`
`sbrm r <id>`

#### edit
Replace a schematic set with a new schematic set. The id can be found with the info command.

`sbrm edit <id> <schematic set>`
`sbrm e <id> <schematic set>`

#### reload
Reloads the brushs in the schematic.
Use this if you added new schematics, which match one or more schematic sets in your brush.

`sbrm reload`
`sbrm rel`

#### info
Get the settings and schematic sets on your current brush.

`sbrm info`
`sbrm i`

### SchematicBrushPreset
Alias: `schbrp` `sbrp`

This command allows you to save schematic sets as presets to use them directly.
Presets can be used by every user who can use a schematic brush.
Their names can't have spaces.

#### savecurrent
Save schematic sets of your current equiped brush as a preset.

Permission: `schematicbrush.preset.save`

`sbrp savecurrent <name of preset>` 
`sbrp c <name of preset>`

#### save
Save one or more schematic sets as a preset.

Permission: `schematicbrush.preset.save`

`sbrp save <name of preset> <schematic sets...>`
`sbrp s <name of preset> <schematic sets...>`

#### descr
Set a description for a preset.

Permission: `schematicbrush.preset.save`

`sbrp descr <description>`
`sbrp d <description>`

#### appendset
Add one or more schematic sets to a preset.

Permission: `schematicbrush.preset.modify`

`sbrp appendset <name of preset> <schematic sets...>`
`sbrp as <name of preset> <schematic sets...>`

#### removeset
Remove a schematic set from a preset.

Permission: `schematicbrush.preset.modify`

`sbrp removeset <name of preset> <id>`
`sbrp rs <name of preset> <id>`

#### remove
Remove a preset.

Permission: `schematicbrush.preset.remove`

`sbrp remove <name of preset>`
`sbrp r <name of preset>`

#### info
Get a list of all schematic sets inside a preset.

Permission: `schematicbrush.brush.use`

`sbrp info <name of preset>`
`sbrp i <name of preset>`

#### list
Get a list of all preset.

Permission: `schematicbrush.brush.use`

`sbrp list`
`sbrp l`

### SchematicBrushAdmin
Alias: `schbra` `sbra`

This command allwos you to reload and other stuff.

#### reload
Reload the plugin.

`sbra reload`

#### reloadschematics
Reload the schematics. This is needed, if you add new schematics and want to use them in a brush.
We store them to access them faster. Don't worry. this doesn't need much RAM.

`sbra reloadschematics`

#### info
General information about the plugin.

`sbra info`

