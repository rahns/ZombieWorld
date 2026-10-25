/// The world, with this game's win and lose conditions.
final class ZombieWorld: World {
    private var containsPlayer = false
    private var containsMambo = false
    private var containsZombie = false
    private var containsHuman = false

    override func stillRunning() -> Bool {
        updateGameState()
        return containsPlayer && (containsMambo || containsZombie) && containsHuman
    }

    override func endGameMessage() -> String {
        updateGameState()
        if !(containsMambo || containsZombie) {
            return "Mambo Marie and all the zombies are dead! Player wins."
        } else if !containsHuman {
            return "All humans are dead! Game over."
        }
        return "Player died. Game Over."
    }

    private func updateGameState() {
        containsPlayer = player.map(actorLocations.contains) ?? false
        containsMambo = false
        containsZombie = false
        containsHuman = false
        for actor in actorLocations {
            if actor is Zombie {
                containsZombie = true
            } else if actor is MamboMarie {
                containsMambo = true
            } else if actor is Human && !(actor is Player) {
                containsHuman = true
            }
        }
    }
}
