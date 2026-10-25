import AVFoundation
import Foundation

/// Loops the background music. Uses the ambient audio category, so it respects the silent switch
/// and mixes with music from other apps.
@MainActor
final class MusicPlayer: ObservableObject {
    @Published var isEnabled = UserDefaults.standard.object(forKey: "musicEnabled") as? Bool ?? true {
        didSet {
            UserDefaults.standard.set(isEnabled, forKey: "musicEnabled")
            if isEnabled { play() } else { pause() }
        }
    }

    private var player: AVAudioPlayer?

    func start() {
        guard player == nil else {
            if isEnabled { play() }
            return
        }
        guard let url = Bundle.main.url(forResource: "theme", withExtension: "wav")
            ?? Bundle.main.url(forResource: "theme", withExtension: "wav", subdirectory: "Audio") else { return }
        try? AVAudioSession.sharedInstance().setCategory(.ambient, mode: .default)
        try? AVAudioSession.sharedInstance().setActive(true)
        player = try? AVAudioPlayer(contentsOf: url)
        player?.numberOfLoops = -1
        player?.volume = 0
        player?.prepareToPlay()
        if isEnabled { play() }
    }

    private func play() {
        guard let player else { return }
        player.play()
        player.setVolume(0.55, fadeDuration: 1.5)
    }

    private func pause() {
        guard let player else { return }
        player.setVolume(0, fadeDuration: 0.4)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.45) { [weak self] in
            guard let self, !self.isEnabled else { return }
            self.player?.pause()
        }
    }

    /// Call when the app goes to the background or comes back.
    func setActive(_ active: Bool) {
        if active, isEnabled {
            play()
        } else if !active {
            player?.pause()
        }
    }
}
