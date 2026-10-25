import SwiftUI

@main
struct ZombieWorldApp: App {
    @StateObject private var session = GameSession()
    @StateObject private var music = MusicPlayer()
    @Environment(\.scenePhase) private var scenePhase

    init() {
        Retro.registerFont()
    }

    var body: some Scene {
        WindowGroup {
            GameView()
                .environmentObject(session)
                .environmentObject(music)
                .preferredColorScheme(.dark)
                .onAppear {
                    if !session.hasStarted { session.start() }
                    music.start()
                }
                .onChange(of: scenePhase) { _, phase in
                    music.setActive(phase != .background)
                }
        }
    }
}
