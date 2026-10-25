/// Runs the game loop. Port of `edu.monash.fit2099.engine.World`.
class World {
    let display: Display
    private(set) var gameMaps: [GameMap] = []
    let actorLocations = ActorLocations()
    private(set) var player: Actor?
    private var lastActionMap: [ObjectIdentifier: Action] = [:]

    init(_ display: Display) {
        self.display = display
    }

    func addGameMap(_ gameMap: GameMap) {
        gameMaps.append(gameMap)
        gameMap.actorLocations = actorLocations
        gameMap.display = display
    }

    func addPlayer(_ player: Actor, _ location: Location) {
        self.player = player
        actorLocations.add(player, location)
        actorLocations.setPlayer(player)
    }

    /// Runs until the game is over. Throws if the frontend abandons the game.
    func run() async throws {
        guard let player else { preconditionFailure("A player must be added before running the world") }

        for actor in actorLocations {
            lastActionMap[ObjectIdentifier(actor)] = DoNothingAction()
        }

        while stillRunning() {
            try Task.checkCancellation()
            if let location = actorLocations.locationOf(player) {
                location.map.draw(display, focus: location)
            }
            for actor in actorLocations where stillRunning() {
                try await processActorTurn(actor)
            }
            for gameMap in gameMaps {
                gameMap.tick()
            }
        }
        if let location = actorLocations.locationOf(player) {
            location.map.draw(display, focus: location)
        }
        await display.finish(endGameMessage())
    }

    /// Gives an Actor its turn.
    func processActorTurn(_ actor: Actor) async throws {
        guard let here = actorLocations.locationOf(actor) else { return }
        let map = here.map
        let actions = Actions()

        for item in actor.inventory {
            actions.add(item.getAllowableActions())
            actions.add(item.getDropAction())
        }

        for exit in here.exits {
            let destination = exit.destination
            if let other = actorLocations.getActorAt(destination) {
                actions.add(other.getAllowableActions(actor, exit.name, map))
            } else {
                actions.add(destination.ground.allowableActions(actor, destination, exit.name))
            }
            actions.add(destination.getMoveAction(actor, exit.name, exit.hotKey))
        }

        for item in here.items {
            actions.add(item.getAllowableActions())
            actions.add(item.getPickUpAction())
        }
        actions.add(DoNothingAction())

        let lastAction = lastActionMap[ObjectIdentifier(actor)] ?? DoNothingAction()
        let action = try await actor.playTurn(actions, lastAction, map, display)
        lastActionMap[ObjectIdentifier(actor)] = action
        if let result = action.execute(actor, map), !result.isEmpty {
            display.println(result)
        }
    }

    func stillRunning() -> Bool {
        guard let player else { return false }
        return actorLocations.contains(player)
    }

    func endGameMessage() -> String { "Game Over" }
}
