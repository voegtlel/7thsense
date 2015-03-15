# General #
  * Differentiate in Library and Playlist
# Data #
  * FolderNode (Node)
    * Serializable
    * +Name
  * ScenarioNode (Leaf)
    * Serializable
    * +Name
    * +play(fadeTime)
    * +stop(fadeTime)
    * +pause()
    * +resume()
  * (ScenarioLinkNode (Leaf))
    * Serializable
    * +Name
    * Links to other ScenarioNode
  * BasicScenario
    * +Music
      * +Randomize
      * +MusicList
        * +MusicItem
          * +FadeType
            * NoFade: No fading, music starts immediatly
            * VolumeFade: Song is faded in by volume
            * AfterFade: Song starts immediatly after fading
          * +IsIntroSong: Song is possibly played as first
          * +IsLoopSong: Song is possibly played after first song
          * +Volume
        * +SoundList
          * +SoundItem
            * +Time range (min/max time) for replay
            * +Volume
            * +Effects? (Reverb, Echo, EQ)
# Library #
  * Editor
  * Views: Tree, "Flat" (List), Table? (Multi-column, cell-select), "2/3-Listview"
    * Tree: "Auto-collapse non-active Nodes"
  * SoundFxEditor
    * (Tab) Basic Properties
      * (Grouped panel) Randomizer
        * Time range for restart/replay
        * Sliders + Editbox
        * Sliders are synchronized (if max is moved below min, it is decreased and vice versa)
        * For Loop, sliders must be moved to 0
        * For disabling random playing (for scripting), sliders must be moved to infinite (-1?).
      * (Grouped panel) Volume
      * (Grouped panel) Effects? (Else Scripted?)
        * Reverb (Hall)
        * Echo
        * Dumb (EQ)
    * (Tab) Script
      * (Editbox) Script-Name (name to use for referencing in sibling scripts)
      * (Checkboxed grouped panel) Initialization-Script
        * (Editbox) Javascript Code
      * (Checkboxed grouped panel) Finish-Script
        * (Editbox) Javascript Code
# Backup System #
  * Export All
    * (Checkbox) Including Media
  * Import All
    * (RadioButton) Merge/Override
  * Verify Paths
    * Search new path for missing medias
      * (Checkbox) Search by filename
      * (Checkbox) Search by hash
      * (Both)

# Moving from Library to Playlist #

  * Left shows Library, Right shows Playlist
  * Between: Slider, Buttons for adding/removing/sorting (alternative: drag'n'drop, copy/paste)
    * (Button) Add (from library)
    * (Button) Remove (from playlist)
    * (Button) Move selected up (playlist)
    * (Button) Move selected down (playlist)

# Playlist #
  * Left shows view of scenarios, right shows player
  * Player
    * (Button) Play (Fade)
    * (Button) Pause (Immediately)
    * (Button) Stop (Immediately)
    * (Slider) FadeTime
    * Shortcuts
      * (Button) <Shortcut F-key> <Destination scenario>
      * Drag'n'drop to shortcut
      * Push button to set selected scenario for shortcut
    * Media
      * Jump to Library
      * Save Playlist

# Todo #
  * "Del" key where transfer handlers are enabled instead of ctrl-x
  * Add class around FadeSoundItem to ensure replay can be faded
  * For future:
    * Context dependent help
    * Fade in/out for sfx?
    * Add a add "multiple media files" button to Music/SoundFx list