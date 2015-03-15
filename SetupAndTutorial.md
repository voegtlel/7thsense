# Requirements #

  * Properly configured Java (The code was designed and built with Java-SE 1.6).
  * Tool to extract a .zip file

# Installation #

  * Extract "seventhsense-`*`.zip" (where "`*`" is the current build no.).
  * Simply run seventhsense.jar after extraction.
  * To help finding bugs and the developing process, you can add the programm argument "-logger" to show a logger window and log to "log.txt"
  * See [InstallationLinux](InstallationLinux.md) for a Linux setup guide.

# Basic setup for a **Basic Scenario** #

The default starting view is "Library". Here you can edit the library with all of it's Directories and Scenarios.

## Creating a **Basic Scenario** ##

By clicking on "**+**" you can add a new entry to the library. Possible types are:
  * **Folders** are only structuring elements, that help to categorize **Scenarios**. They do not reflect the file system in any way.
  * **Basic Scenarios** are the playable scenarios and core elements of seventh sense.
  * **Link** entries refer to another entry in the library. They reflect the properties of the destination entry, which can be of any type.

## Editing a **Basic Scenario** ##

By selecting a **Basic Scenario** you can edit it's properties on the right side of the window.

### Creating and editing **Music Items** ###

The basic elements in a **Basic Scenario** are **Music Items**. They will be played all the time in the background. As soon as they have a **Media File** assigned to them, they are ready to play. You can adjust individual volume and fading options. By unchecking **"Is Intro Song"** the **Music Item** won't play as first song (except if there is no **Intro Song**). **"Is Loop Song"** works similar: When unchecked, the item won't be played again after the intro song.

_TODO_: Create a randomize checkbox for the **Music List**, functionality is already implemented, but missing the button.

### Creating and editing **Sound Effect Items** ###

**Sound Effect Items** can be used to add effects to the scenery.
Example: We have an urban scenario where peoples in a crowd are doing noises. These noises can be simulated by adding some effects, e.g. a sound for the crowd that is looping always (set both "**Restart Randomizer Time Range**" **Minimum** and **Maximum** to 0) and a cough sound that will play sometimes, lets say with a delay of 20 seconds to 3 minutes. Layering and randomizing different sounds will dramatically improve the authenticity of the scenario.

_TODO_: Implement Scripting, currently the corresponding gui is useless.

## Preparing the **Basic Scenario** for a session ##

To use the newly created **Basic Scenario**, switch to the **Playlist Manager** tab on the left. There you see the library on the left and your current playlist on the right side. Simply drag'n'drop (you can also use the buttons or copy'n'paste) the **Basic Scenario** to the right panel.

## Using the **Basic Scenario** in a game session ##

Select the **Playlist** tab on the left. By selecting a scenario you can start playing by pressing the play button. You can also assign it to a shortcut by dragging it onto one of the F-Buttons or on the description field on the right. As long as the window is in foreground you now can use the F`*`-keys to switch to a scenario.

_TODO_: Create a volume slider.