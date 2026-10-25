# ZombieWorld for iOS

A native iPhone and iPad version of ZombieWorld, written in Swift and SwiftUI. It is a port of the
Java game in `src/`. The rules, map, characters and messages are the same, and you play by touch
instead of typing menu keys.

## Running it

1. Open `ios/ZombieWorld.xcodeproj` in Xcode 16 or later.
2. Select the **ZombieWorld** scheme and an iPhone simulator, then press **Run** (⌘R).
3. To run it on a real device, choose your team under *Signing & Capabilities* and change the bundle
   identifier (`com.example.ZombieWorld`) to one you own.

The app needs iOS 17 or later.

## How to play

- **Tap a square** to act on it: walk there one step at a time, attack the zombie standing there,
  open a door, enter the shop, or pick up what's under you. Squares you can act on are marked:
  red brackets for enemies, gold for everything else.
- **Hold a square** to see what's there (who it is and their health, items, terrain).
- **Pad**: steps in the eight directions. The middle button waits for a turn.
- **Actions** (next to the pad): everything else you can do, such as eat, craft, use guns, drop
  items, ride the train, collect coins.
- **Message box**: shows what just happened. Tap it for the full log.
- **Pinch** the map to zoom. The ☰ menu has the whole-map view, the log, *How to play* (with a
  sprite legend), music on/off, *New game* and *Quit game*.
- **Music** follows the ring/silent switch and mixes with other apps' audio.
- **Keyboard**: on an iPad or simulator with a hardware keyboard you can type the original menu
  keys (`8` = north, `5` = wait, `a`…`z` for the other actions).

## Getting the app onto an iPhone without a Mac

Every push that changes `ios/` runs the **iOS build** GitHub Actions workflow on a free macOS
runner. It produces an unsigned `ZombieWorld-unsigned.ipa`, which you can download from the run's
*Artifacts* section and sign with your own certificate.

## Project layout

```
ios/
├── ZombieWorld.xcodeproj         Xcode project for the app
├── ZombieWorld/                  SwiftUI app (UI only)
│   ├── ZombieWorldApp.swift      @main entry point
│   ├── GameSession.swift         Runs the engine and bridges its prompts to SwiftUI
│   ├── GameView.swift            HUD, message box, pad, actions, menus, sheets, tap handling
│   ├── WorldScene.swift          SpriteKit map: tiles, animated actors, effects, camera, touches
│   ├── Sprites.swift             Which sprite to draw for each map character
│   ├── Retro.swift               Pixel font, palette, text boxes, icons, legend
│   ├── MusicPlayer.swift         Background music (toggle in the ☰ menu)
│   ├── Audio/theme.wav           Background music loop
│   ├── Fonts/                    Press Start 2P pixel font
│   └── Assets.xcassets/Sprites   16×16 sprites (see CREDITS.md)
└── ZombieWorldCore/              Swift package with the game itself (no UI code)
    ├── Sources/ZombieWorldCore/Engine/   Port of edu.monash.fit2099.engine
    ├── Sources/ZombieWorldCore/Game/     Port of the game package
    ├── Sources/zombieworld-cli/          Terminal version, like the Java original
    └── Tests/ZombieWorldCoreTests/       Unit tests and automated random playthroughs
tools/make_sprites.py             Rebuilds the sprites from Kenney's packs
tools/make_icon.py                Draws the app icon
tools/make_music.py               Synthesises the background music loop
CREDITS.md                        Sprite and font credits and licences
```

The sprites are from Kenney's free CC0 packs, plus a few drawn in the same style.
See `CREDITS.md`.

The engine has no UI code. The Java engine blocked on `Scanner` input. Here the engine is
`async`: when it needs a decision it sends a `Prompt` (new log lines, a map snapshot, player
status and menu options) to a `GameFrontend` and waits for the chosen key. The SwiftUI app and
the terminal version are two implementations of that protocol.

You can build and test the core with SwiftPM on macOS or Linux:

```sh
cd ios/ZombieWorldCore
swift test
swift run zombieworld-cli   # play in the terminal
```

## Differences from the Java version

These are fixes for bugs in the Java version:

- Each shop purchase gives you a new item. In the Java version every purchase of a product handed
  out the same object.
- A zombie bite heals 5 hit points, which is what the Java code intended. Because of a typo
  (`gethealAmount`), the Java version healed 10.
- Moving onto an occupied square (by train, or Mambo Marie's teleport) fails with a message.
  The Java engine threw an exception and the game crashed.
- If a sub-menu has nothing to choose (for example, aiming the sniper rifle when no zombies are on
  the map), you lose the turn. The Java version waited for input forever.
- If Mambo Marie can't move, she waits. The Java version crashed.
