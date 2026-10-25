/// Entry point for frontends. Port of the game's `Application.main`.
public enum ZombieWorldGame {
    static let playerHealth = 300

    static let mainMapLayout = [
        "......................................................................##########",
        "......................................................................#........#",
        "....................................##########.................................#",
        "..........................###########........#####....................##########",
        "............++...........##......................########.......................",
        "..............++++.......#..............................##......................",
        ".............+++...+++...#...............................#......................",
        ".........................##..............................##.....................",
        "..........................#...............................#.....................",
        ".........................##...............................##....................",
        ".........................#...............................##.....................",
        ".........................###..............................##....................",
        "...........................####......................######.....................",
        "..............................#########.........######..........................",
        "............+++.......................#.........#...............................",
        ".............+++++....................#.........#...............................",
        "...............++........................................+++++..................",
        ".............+++....................................++++++++....................",
        "............+++.......................................+++.......................",
        "................................................................................",
        ".........................................................................++.....",
        "........................................................................++.++...",
        "......................................#####_#####........................++++...",
        "......................................#.........#.........................++....",
        "......................................###########...............................",
    ]

    static let townMapLayout = [
        ".........................................",
        ".........................................",
        "....###########..........................",
        "....#.........#..........................",
        "....#####_#####..########................",
        "....#.........#..#.......................",
        "....#.........####.......................",
        "....#....................#...............",
        "....#....................#..........++++.",
        "....######################........+++++..",
        "..................................+++....",
        ".........................................",
    ]

    /// Builds a new game and plays it until it ends, asking `frontend` for every player decision.
    /// Throws if the frontend throws (e.g. the player starts a new game).
    public static func run(frontend: GameFrontend) async throws {
        let world = makeWorld(Display(frontend: frontend))
        try await world.run()
    }

    static func makeWorld(_ display: Display) -> World {
        let world = ZombieWorld(display)
        let groundFactory = FancyGroundFactory({ Dirt() }, { Fence() }, { Tree() }, { Door() })

        let mainMap = GameMap(groundFactory, mainMapLayout)
        world.addGameMap(mainMap)
        let townMap = GameMap(groundFactory, townMapLayout)
        world.addGameMap(townMap)
        let maps = [mainMap, townMap]

        let mambosVoid = GameMap(groundFactory, ["."])
        world.addGameMap(mambosVoid)
        mambosVoid.addActor(MamboMarie(void: mambosVoid.at(0, 0), appearsAt: mainMap.at(79, 13)), mambosVoid.at(0, 0))

        let player = Player("Player", "@", playerHealth)
        world.addPlayer(player, mainMap.at(43, 15))

        let humans = ["Little Rock", "Tank Dempsey", "Vicente", "Andrea", "Elina", "Winter", "Clem", "Tallahassee", "Jaquelyn"]
        let farmers = ["Farmer Old McDonald", "Farmer Wendy", "Farmer Jones of Manor Farm", "Farmer Dwight Schrute"]

        func randomFreeLocationInCompound() -> Location {
            while true {
                let location = mainMap.at(Int.random(in: 30..<50), Int.random(in: 5..<12))
                if !location.containsAnActor() { return location }
            }
        }
        for name in humans {
            randomFreeLocationInCompound().addActor(Human(name))
        }
        for name in farmers {
            randomFreeLocationInCompound().addActor(Farmer(name))
        }
        mainMap.at(75, 1).addActor(Farmer("Lonely Farmer Steve"))

        mainMap.at(74, 20).addItem(Plank())
        mainMap.at(43, 23).addItem(Vehicle("train", "*", destination: townMap.at(20, 0), destinationName: "town"))
        townMap.at(20, 0).addItem(Vehicle("train", "*", destination: mainMap.at(43, 23), destinationName: "the compound"))
        townMap.at(6, 3).addItem(Shotgun())
        townMap.at(8, 3).addItem(SniperRifle(craftsInto: Turret()))

        let products = [
            Product(cost: 2) { Food() },
            Product(cost: 3) { ZombieMace() },
            Product(cost: 3) { ZombieClub() },
            Product(cost: 5) { Hoe() },
            Product(cost: 5) { AmmunitionCartridge() },
            Product(cost: 6) { Plank() },
            Product(cost: 30) { Turret() },
        ]
        townMap.at(12, 3).ground = Shop("Wallmart", products, unlockAge: 70)

        let mainZombies: [(String, Int, Int)] = [
            ("Groan", 30, 20), ("Boo", 30, 18), ("Uuuurgh", 10, 4), ("Mortalis", 50, 18), ("Gaaaah", 1, 10),
            ("Aaargh", 62, 4), ("Zoombie", 61, 12), ("Zomzilla", 70, 12), ("Creeper", 65, 8),
            ("Zombie Edward Richtofen", 67, 9), ("Bombie", 6, 12), ("Zombie Micheal Jackson", 2, 12),
            ("Zombie Meg", 5, 20), ("Zombie Megger", 40, 1), ("Zombie Megosauraus", 41, 1),
            ("Zombie Megania", 42, 1), ("Zombie Megatron", 43, 1),
        ]
        for (name, x, y) in mainZombies {
            mainMap.at(x, y).addActor(Zombie(name, mainMap))
        }
        let townZombies: [(String, Int, Int)] = [
            ("Zombie Mayor Duncan", 15, 5), ("Zombie Bob Dylan", 8, 7), ("Zombie Rupert Murdoch", 19, 6),
            ("Zombie Kanye", 13, 0), ("Zombie Rick", 33, 5), ("Zombie Harold Holt", 25, 6),
            ("Zombie Wallmart Employee", 10, 5),
        ]
        for (name, x, y) in townZombies {
            townMap.at(x, y).addActor(Zombie(name, townMap))
        }

        // Scatter ammunition around both maps
        for map in maps {
            for _ in 0..<15 {
                let location = map.at(Int.random(in: 0..<map.xRange.max), Int.random(in: 0..<map.yRange.max))
                if location.canActorEnter(player) {
                    location.addItem(AmmunitionCartridge())
                }
            }
        }

        return world
    }
}
