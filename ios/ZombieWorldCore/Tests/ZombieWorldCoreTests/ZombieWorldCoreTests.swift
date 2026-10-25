import XCTest
@testable import ZombieWorldCore

/// Picks random menu options, to play whole games without a person.
final class RandomFrontend: GameFrontend, @unchecked Sendable {
    private let maxPrompts: Int
    private(set) var prompts: [Prompt] = []
    private(set) var result: GameResult?

    init(maxPrompts: Int) {
        self.maxPrompts = maxPrompts
    }

    func choose(_ prompt: Prompt) async throws -> Character {
        prompts.append(prompt)
        if prompts.count > maxPrompts { throw CancellationError() }
        XCTAssertFalse(prompt.options.isEmpty, "Menus must never be empty")
        XCTAssertEqual(Set(prompt.options.map(\.key)).count, prompt.options.count, "Menu keys must be unique")
        // Never quit, so games last.
        let candidates = prompt.options.filter { $0.key != "+" }
        return (candidates.randomElement() ?? prompt.options[0]).key
    }

    func gameEnded(_ result: GameResult) async {
        self.result = result
    }
}

/// Always presses the given key if it's offered, otherwise does nothing.
final class ScriptedFrontend: GameFrontend, @unchecked Sendable {
    var script: [Character]
    private(set) var prompts: [Prompt] = []

    init(_ script: [Character]) {
        self.script = script
    }

    func choose(_ prompt: Prompt) async throws -> Character {
        prompts.append(prompt)
        guard !script.isEmpty else { throw CancellationError() }
        return script.removeFirst()
    }

    func gameEnded(_ result: GameResult) async {}
}

final class ZombieWorldCoreTests: XCTestCase {
    func testRandomGamesRunWithoutCrashing() async throws {
        for _ in 0..<8 {
            let frontend = RandomFrontend(maxPrompts: 250)
            do {
                try await ZombieWorldGame.run(frontend: frontend)
            } catch is CancellationError {}
            XCTAssertFalse(frontend.prompts.isEmpty)
        }
    }

    func testFirstPromptShowsMapStatusAndMoves() async throws {
        let frontend = ScriptedFrontend([])
        do { try await ZombieWorldGame.run(frontend: frontend) } catch is CancellationError {}
        let prompt = try XCTUnwrap(frontend.prompts.first)
        let frame = try XCTUnwrap(prompt.frame)
        XCTAssertEqual(frame.width, 80)
        XCTAssertEqual(frame.height, 25)
        XCTAssertEqual(frame.focusX, 43)
        XCTAssertEqual(frame.focusY, 15)
        XCTAssertEqual(Array(frame.rows[15])[43], "@")
        XCTAssertEqual(prompt.status?.hitPoints, 300)
        XCTAssertEqual(prompt.status?.coins, 0)
        let keys = Set(prompt.options.map(\.key))
        XCTAssertTrue(keys.isSuperset(of: ["5", "+"]))
        XCTAssertFalse(prompt.isSubmenu)
    }

    func testQuittingEndsTheGame() async throws {
        let frontend = ScriptedFrontend(["+"])
        try await ZombieWorldGame.run(frontend: frontend)
        XCTAssertEqual(frontend.prompts.count, 1)
    }

    func testMovingNorthMovesThePlayer() async throws {
        let frontend = ScriptedFrontend(["8"])
        do { try await ZombieWorldGame.run(frontend: frontend) } catch is CancellationError {}
        XCTAssertEqual(frontend.prompts.count, 2)
        XCTAssertEqual(frontend.prompts[1].frame?.focusY, 14)
        XCTAssertTrue(frontend.prompts[1].log.contains("Player moves North"))
    }

    func testCropGrowsAndFertilises() {
        let crop = Crop()
        let map = GameMap(FancyGroundFactory({ Dirt() }), ["..."])
        for _ in 0..<10 { crop.tick(map.at(0, 0)) }
        XCTAssertEqual(crop.displayChar, ":")
        XCTAssertFalse(crop.isRipe())
        crop.fertilise()
        XCTAssertTrue(crop.isRipe())
        crop.tick(map.at(0, 0))
        XCTAssertEqual(crop.displayChar, "1")
    }

    func testDoorToggles() {
        let door = Door()
        let human = Human("Test")
        XCTAssertFalse(door.canActorEnter(human))
        door.toggle()
        XCTAssertTrue(door.canActorEnter(human))
        XCTAssertEqual(door.displayChar, "|")
    }

    func testMenuGivesHotkeysFirstAndLettersToTheRest() async throws {
        let frontend = ScriptedFrontend(["a"])
        let display = Display(frontend: frontend)
        let actions = Actions()
        let player = Player("P", "@", 10)
        actions.add(EndLifeAction())
        actions.add(DoNothingAction())
        let map = GameMap(FancyGroundFactory({ Dirt() }), [".."])
        let food = Food()
        actions.add(PickUpItemAction(food))
        _ = map
        let chosen = try await Menu().showMenu(player, actions, display)
        XCTAssertTrue(chosen is PickUpItemAction)
        XCTAssertEqual(frontend.prompts[0].options.map(\.key), ["+", "5", "a"])
    }

    func testZombieKilledByPlayerDropsCoinAndCorpse() {
        let display = Display(frontend: ScriptedFrontend([]))
        let world = World(display)
        let map = GameMap(FancyGroundFactory({ Dirt() }), ["...", "...", "..."])
        world.addGameMap(map)
        let player = Player("P", "@", 100)
        world.addPlayer(player, map.at(0, 0))
        let zombie = Zombie("Z1", map)
        map.at(1, 1).addActor(zombie)
        let attack = AttackAction(zombie)
        _ = attack.executeAsItem(map, 1000)
        XCTAssertFalse(map.contains(zombie))
        XCTAssertTrue(map.at(1, 1).items.contains { $0 is Corpse })
        XCTAssertTrue(map.at(1, 1).items.contains { $0 is Coin })
    }

    func testHumanHuntsWeapons() {
        let display = Display(frontend: ScriptedFrontend([]))
        let world = World(display)
        let map = GameMap(FancyGroundFactory({ Dirt() }), ["....."])
        world.addGameMap(map)
        let human = Human("H")
        map.at(0, 0).addActor(human)
        map.at(3, 0).addItem(Plank())
        let action = HuntBehaviour(Weapon.self, name: "Weapon", range: 4).getAction(human, map)
        XCTAssertEqual((action as? MoveActorAction)?.moveToLocation, map.at(1, 0))
    }
}

final class FrontendDataTests: XCTestCase {
    func testFrameHasLayersAndActors() async throws {
        let frontend = ScriptedFrontend(["8"])
        do { try await ZombieWorldGame.run(frontend: frontend) } catch is CancellationError {}
        let first = try XCTUnwrap(frontend.prompts.first?.frame)
        XCTAssertEqual(first.mapIndex, 0)
        XCTAssertEqual(first.ground.count, 25)
        XCTAssertEqual(first.items.count, 25)
        XCTAssertEqual(Array(first.ground[22])[43], "_")
        XCTAssertEqual(Array(first.items[23])[43], "*")
        let player = try XCTUnwrap(first.actors.first { $0.symbol == "@" })
        XCTAssertEqual(player.x, 43)
        XCTAssertEqual(player.y, 15)
        XCTAssertEqual(Set(first.actors.map(\.id)).count, first.actors.count, "actor ids are unique")

        let second = try XCTUnwrap(frontend.prompts[1].frame)
        let playerAfter = try XCTUnwrap(second.actors.first { $0.symbol == "@" })
        XCTAssertEqual(playerAfter.id, player.id, "ids are stable between frames")
        XCTAssertEqual(playerAfter.y, 14)
    }

    func testMoveOptionsTargetTheirSquare() async throws {
        let frontend = ScriptedFrontend([])
        do { try await ZombieWorldGame.run(frontend: frontend) } catch is CancellationError {}
        let options = try XCTUnwrap(frontend.prompts.first?.options)
        let north = try XCTUnwrap(options.first { $0.key == "8" })
        XCTAssertEqual(north.kind, .move)
        XCTAssertEqual(north.target, TilePoint(map: 0, x: 43, y: 14))
        XCTAssertEqual(options.first { $0.key == "5" }?.kind, .wait)
        XCTAssertEqual(options.first { $0.key == "+" }?.kind, .quit)
    }

    func testAttackOptionTargetsTheEnemy() {
        let frontend = ScriptedFrontend([])
        let display = Display(frontend: frontend)
        let world = World(display)
        let map = GameMap(FancyGroundFactory({ Dirt() }), ["...", "..."])
        world.addGameMap(map)
        let player = Player("P", "@", 100)
        world.addPlayer(player, map.at(0, 0))
        let zombie = Zombie("Z", map)
        map.at(1, 1).addActor(zombie)
        let actions = zombie.getAllowableActions(player, "South-East", map)
        let option = actions[0].menuOption(key: "a", actor: player, map: map)
        XCTAssertEqual(option.kind, .attack)
        XCTAssertEqual(option.target, TilePoint(map: 0, x: 1, y: 1))
    }
}

/// Plays random choices and records them, stopping (by throwing) after `limit` prompts.
final class RecordingFrontend: GameFrontend, @unchecked Sendable {
    private let limit: Int
    private(set) var choices: [Character] = []
    private(set) var prompts: [Prompt] = []

    init(limit: Int) {
        self.limit = limit
    }

    func choose(_ prompt: Prompt) async throws -> Character {
        prompts.append(prompt)
        if prompts.count > limit { throw CancellationError() }
        let key = (prompt.options.filter { $0.key != "+" }.randomElement() ?? prompt.options[0]).key
        choices.append(key)
        return key
    }

    func gameEnded(_ result: GameResult) async {}
}

final class SaveAndRestoreTests: XCTestCase {
    func testSameSeedAndChoicesReplayExactly() async throws {
        for seed: UInt64 in [1, 42, 9_876_543_210] {
            let original = RecordingFrontend(limit: 150)
            do { try await ZombieWorldGame.run(frontend: original, seed: seed) } catch is CancellationError {}

            let live = ScriptedFrontend([])
            let caughtUp = Counter()
            let replay = ReplayingFrontend(choices: original.choices, live: live) { turns in await caughtUp.set(turns) }
            do { try await ZombieWorldGame.run(frontend: replay, seed: seed) } catch is CancellationError {}

            guard let restored = live.prompts.first else {
                // The original game ended before running out of choices; nothing more to compare.
                continue
            }
            let expected = try XCTUnwrap(original.prompts.last)
            XCTAssertEqual(restored.frame, expected.frame, "seed \(seed)")
            XCTAssertEqual(restored.status, expected.status, "seed \(seed)")
            XCTAssertEqual(restored.options, expected.options, "seed \(seed)")
            let turns = await caughtUp.value
            XCTAssertEqual(turns, original.prompts.dropLast().filter { !$0.isSubmenu }.count)
        }
    }

    func testDifferentSeedsGiveDifferentGames() async throws {
        let a = ScriptedFrontend([]), b = ScriptedFrontend([])
        do { try await ZombieWorldGame.run(frontend: a, seed: 1) } catch is CancellationError {}
        do { try await ZombieWorldGame.run(frontend: b, seed: 2) } catch is CancellationError {}
        XCTAssertNotEqual(a.prompts.first?.frame, b.prompts.first?.frame)
    }

    func testReplayingImpossibleChoicesIsDetected() async {
        let replay = ReplayingFrontend(choices: ["8", "!", "!"], live: ScriptedFrontend([])) { _ in }
        do {
            try await ZombieWorldGame.run(frontend: replay, seed: 7)
            XCTFail("expected the replay to diverge")
        } catch {
            XCTAssertTrue(error is ReplayDivergedError, "\(error)")
        }
    }

    func testKilledActorsAreReportedAsDeaths() {
        let display = Display(frontend: ScriptedFrontend([]))
        let world = World(display)
        let map = GameMap(FancyGroundFactory({ Dirt() }), ["...", "..."])
        world.addGameMap(map)
        let player = Player("P", "@", 100)
        world.addPlayer(player, map.at(0, 0))
        let zombie = Zombie("Z", map)
        let leaver = Zombie("Leaver", map)
        map.at(1, 1).addActor(zombie)
        map.at(2, 1).addActor(leaver)
        map.draw(display, focus: nil)
        let zombieID = display.id(of: zombie)

        _ = AttackAction(zombie).executeAsItem(map, 1000)
        map.removeActor(leaver)  // leaves without dying
        map.draw(display, focus: nil)
        XCTAssertEqual(display.frame?.deaths, [zombieID])

        map.draw(display, focus: nil)
        XCTAssertEqual(display.frame?.deaths, [], "deaths are only reported once")
    }
}

actor Counter {
    private(set) var value = 0
    func set(_ newValue: Int) { value = newValue }
}
