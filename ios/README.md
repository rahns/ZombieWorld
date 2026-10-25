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

- **D-pad**: moves in the eight directions. The centre button waits for one turn.
- **Action list** (next to the pad): attack, pick up, drop, eat, craft, use guns, open doors, shop
  and so on. Each action still shows its original menu key.
- **Map**: tap it to switch between the close-up view and the whole map. The legend
  (••• menu → *Map legend*) explains every symbol.
- **Keyboard**: on an iPad or simulator with a hardware keyboard you can type the original menu
  keys (`8` = north, `5` = wait, `a`…`z` for the other actions).
- The ••• menu also has *New game* and *Quit game*.

## Project layout

```
ios/
├── ZombieWorld.xcodeproj         Xcode project for the app
├── ZombieWorld/                  SwiftUI app (UI only)
│   ├── ZombieWorldApp.swift      @main entry point
│   ├── GameSession.swift         Runs the engine and bridges its prompts to SwiftUI
│   ├── GameView.swift            Status bar, map, message log, d-pad, actions, game over
│   ├── MapView.swift             Coloured, scrolling text map
│   └── Theme.swift               Colours and map legend
└── ZombieWorldCore/              Swift package with the game itself (no UI code)
    ├── Sources/ZombieWorldCore/Engine/   Port of edu.monash.fit2099.engine
    ├── Sources/ZombieWorldCore/Game/     Port of the game package
    ├── Sources/zombieworld-cli/          Terminal version, like the Java original
    └── Tests/ZombieWorldCoreTests/       Unit tests and automated random playthroughs
```

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
