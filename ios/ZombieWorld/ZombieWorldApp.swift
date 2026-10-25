import SwiftUI

@main
struct ZombieWorldApp: App {
    @StateObject private var session = GameSession()

    init() {
        Retro.registerFont()
    }

    var body: some Scene {
        WindowGroup {
            GameView()
                .environmentObject(session)
                .preferredColorScheme(.dark)
                .onAppear {
                    if !session.hasStarted { session.newGame() }
                }
        }
    }
}
