import SpriteKit
import SwiftUI
import ZombieWorldCore

/// Owns the SpriteKit scene so it survives SwiftUI view updates.
@MainActor
final class SceneHolder: ObservableObject {
    let scene = WorldScene(size: CGSize(width: 390, height: 480))
}

struct GameView: View {
    @EnvironmentObject private var session: GameSession
    @EnvironmentObject private var music: MusicPlayer
    @StateObject private var holder = SceneHolder()
    @State private var isFollowingPlayer = true
    @State private var showLegend = false
    @State private var showLog = false
    @State private var confirmQuit = false
    @State private var confirmNewGame = false
    @State private var tileChoices: [MenuOption] = []
    @State private var showTileChoices = false
    @State private var toast: String?
    @State private var toastID = 0
    @FocusState private var hasKeyboardFocus: Bool

    var body: some View {
        VStack(spacing: 0) {
            mapArea
            ControlDeck()
        }
        .background(Retro.panel.ignoresSafeArea())
        .overlay {
            if let result = session.result {
                GameOverCard(result: result) { session.newGame() }
            }
        }
        .sheet(isPresented: $showLegend) { LegendSheet() }
        .sheet(isPresented: $showLog) { LogSheet(entries: session.log) }
        .confirmationDialog("Give up and end this game?", isPresented: $confirmQuit, titleVisibility: .visible) {
            Button("Quit game", role: .destructive) { session.choose("+") }
        }
        .confirmationDialog("Start a new game? Your current game will be lost.", isPresented: $confirmNewGame, titleVisibility: .visible) {
            Button("New game", role: .destructive) { session.newGame() }
        }
        .confirmationDialog("What do you want to do?", isPresented: $showTileChoices, titleVisibility: .visible) {
            ForEach(tileChoices) { option in
                Button(option.shortLabel) { session.choose(option.key) }
            }
        }
        .focusable()
        .focused($hasKeyboardFocus)
        .focusEffectDisabled()
        .onKeyPress(phases: .down) { press in
            // Hardware keyboards work like the original console version.
            guard let key = press.characters.first, session.options.contains(where: { $0.key == key }) else {
                return .ignored
            }
            session.choose(key)
            return .handled
        }
        .sensoryFeedback(trigger: session.status?.hitPoints ?? 0) { old, new in
            new < old ? .impact(weight: .medium) : nil
        }
        .onAppear {
            hasKeyboardFocus = true
            configureScene()
        }
        .onChange(of: session.frame) { _, frame in
            if let frame { holder.scene.apply(frame, game: session.generation) }
        }
        .onChange(of: session.options) { _, _ in updateMarkers() }
    }

    // MARK: - Map area

    private var mapArea: some View {
        ZStack {
            SpriteView(scene: holder.scene, options: [.ignoresSiblingOrder])
                .ignoresSafeArea(edges: .top)

            VStack(spacing: 8) {
                HStack(alignment: .top, spacing: 8) {
                    HUD(status: session.status, turn: session.turn)
                    Spacer(minLength: 0)
                    menuButton
                }
                Spacer(minLength: 0)
                if let toast {
                    Text(toast)
                        .font(.pixel(9))
                        .lineSpacing(4)
                        .multilineTextAlignment(.center)
                        .retroBox(padding: 10)
                        .transition(.scale.combined(with: .opacity))
                        .allowsHitTesting(false)
                }
                if !isFollowingPlayer {
                    HStack {
                        Spacer()
                        recenterButton
                    }
                    .transition(.move(edge: .trailing).combined(with: .opacity))
                }
                let lines = headlines
                if !lines.isEmpty {
                    MessageBox(lines: lines)
                        .onTapGesture { showLog = true }
                }
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 8)
        }
    }

    /// Brings the camera back to the player after scrolling or zooming around the map.
    private var recenterButton: some View {
        Button {
            holder.scene.recenterOnPlayer()
        } label: {
            HStack(spacing: 8) {
                Image(systemName: "location.fill")
                    .font(.system(size: 13, weight: .heavy))
                Text("ME").font(.pixel(8))
            }
            .retroBox(padding: 10)
        }
        .buttonStyle(.plain)
        .accessibilityLabel("Centre on player")
    }

    private var menuButton: some View {
        Menu {
            Button {
                holder.scene.showWholeMap()
            } label: {
                Label("Whole map", systemImage: "map")
            }
            Button {
                holder.scene.recenterOnPlayer()
            } label: {
                Label("Centre on player", systemImage: "location.fill")
            }
            Button {
                showLog = true
            } label: {
                Label("Message log", systemImage: "text.bubble")
            }
            Button {
                showLegend = true
            } label: {
                Label("How to play", systemImage: "questionmark.circle")
            }
            Button {
                music.isEnabled.toggle()
            } label: {
                Label(music.isEnabled ? "Music off" : "Music on",
                      systemImage: music.isEnabled ? "speaker.slash" : "speaker.wave.2")
            }
            Divider()
            Button {
                if session.result == nil { confirmNewGame = true } else { session.newGame() }
            } label: {
                Label("New game", systemImage: "arrow.counterclockwise")
            }
            if session.options.contains(where: { $0.key == "+" }) {
                Button(role: .destructive) {
                    confirmQuit = true
                } label: {
                    Label("Quit game", systemImage: "flag")
                }
            }
        } label: {
            Image(systemName: "line.3.horizontal")
                .font(.system(size: 16, weight: .heavy))
                .frame(width: 22, height: 22)
                .retroBox(padding: 10)
        }
        .accessibilityLabel("Menu")
    }

    /// The most interesting things that happened last turn, for the message box.
    private var headlines: [String] {
        guard let latest = session.log.last?.turn else { return [] }
        let recent = session.log.filter { $0.turn == latest }.map(\.text).filter(Self.isInteresting)
        let mine = recent.filter { $0.contains("Player") }
        let others = recent.filter { !$0.contains("Player") }
        let lines = Array((mine + others).prefix(3))
        if lines.isEmpty && session.turn <= 1 {
            return ["Welcome to ZombieWorld!", "Tap a square to walk there. Hold a square to look at it."]
        }
        return lines
    }

    private static func isInteresting(_ text: String) -> Bool {
        !(text.contains(" moves ") || text.hasSuffix("does nothing") || text.contains("sits in her void"))
    }

    // MARK: - Map interaction

    private func configureScene() {
        holder.scene.onTap = { x, y in handleTap(x, y) }
        holder.scene.onLongPress = { x, y in inspect(x, y) }
        holder.scene.onFollowingChange = { following in
            withAnimation(.spring(duration: 0.3)) { isFollowingPlayer = following }
        }
        if let frame = session.frame {
            holder.scene.apply(frame, game: session.generation)
        }
        updateMarkers()
    }

    private func updateMarkers() {
        guard let frame = session.frame else { return }
        let markers = session.options.compactMap { option -> (x: Int, y: Int, hostile: Bool)? in
            guard let target = option.target, target.map == frame.mapIndex else { return nil }
            switch option.kind {
            case .move, .wait, .quit: return nil
            case .attack, .shoot, .aim: return (target.x, target.y, true)
            default: return (target.x, target.y, false)
            }
        }
        holder.scene.setMarkers(markers)
    }

    private func handleTap(_ x: Int, _ y: Int) {
        guard session.isWaitingForPlayer, session.result == nil, let frame = session.frame else { return }
        let tile = TilePoint(map: frame.mapIndex, x: x, y: y)
        let here = session.options.filter { $0.target == tile && $0.kind != .quit }

        let attacks = here.filter { [.attack, .shoot, .aim].contains($0.kind) }
        if attacks.count == 1 {
            session.choose(attacks[0].key)
            return
        }
        if let move = here.first(where: { $0.kind == .move }) {
            session.choose(move.key)
            return
        }
        if here.count == 1 {
            session.choose(here[0].key)
            return
        }
        if here.count > 1 {
            tileChoices = here
            showTileChoices = true
            return
        }
        guard !session.isSubmenu else { return }

        // Somewhere further away: take one step towards it.
        guard let px = frame.focusX, let py = frame.focusY, (x, y) != (px, py) else {
            showToast("Nothing to do here")
            return
        }
        func distance(_ ax: Int, _ ay: Int) -> Double {
            let dx = ax - x, dy = ay - y
            return Double(max(abs(dx), abs(dy))) + 0.01 * Double(dx * dx + dy * dy)
        }
        let steps = session.options.compactMap { option -> (MenuOption, Double)? in
            guard option.kind == .move, let target = option.target, target.map == frame.mapIndex else { return nil }
            return (option, distance(target.x, target.y))
        }
        if let best = steps.min(by: { $0.1 < $1.1 }), best.1 < distance(px, py) {
            session.choose(best.0.key)
        } else {
            showToast("You can't go that way")
        }
    }

    private func inspect(_ x: Int, _ y: Int) {
        guard let frame = session.frame, y < frame.ground.count else { return }
        var parts: [String] = []
        if let actor = frame.actors.first(where: { $0.x == x && $0.y == y }) {
            let name = actor.symbol == "@" ? "You" : actor.name
            parts.append("\(name)\nHP \(actor.hitPoints)/\(actor.maxHitPoints)")
        }
        let items = Array(frame.items[y]), ground = Array(frame.ground[y])
        if x < items.count, let item = Retro.itemName(items[x]) {
            parts.append(item)
        }
        if x < ground.count {
            parts.append(Retro.groundName(ground[x]) ?? "Grass")
        }
        showToast(parts.joined(separator: "\n"))
    }

    private func showToast(_ text: String) {
        toastID += 1
        let id = toastID
        withAnimation(.spring(duration: 0.25)) { toast = text }
        Task {
            try? await Task.sleep(nanoseconds: 2_200_000_000)
            if id == toastID {
                withAnimation(.easeOut(duration: 0.2)) { toast = nil }
            }
        }
    }
}

// MARK: - HUD

private struct HUD: View {
    let status: PlayerStatus?
    let turn: Int

    var body: some View {
        if let status {
            HStack(spacing: 10) {
                VStack(alignment: .leading, spacing: 6) {
                    HStack(spacing: 6) {
                        Text("HP").font(.pixel(8))
                        HealthBar(fraction: Double(status.hitPoints) / Double(max(1, status.maxHitPoints)))
                    }
                    Text("\(max(0, status.hitPoints))/\(status.maxHitPoints)")
                        .font(.pixel(8))
                }
                Rectangle().fill(Retro.border.opacity(0.3)).frame(width: 2, height: 26)
                VStack(alignment: .leading, spacing: 6) {
                    HStack(spacing: 4) {
                        SpriteImage(name: "item_coin", size: 12)
                        Text("\(status.coins)").font(.pixel(8))
                    }
                    Text("T\(turn)").font(.pixel(8))
                }
            }
            .retroBox(padding: 10)
            .accessibilityElement(children: .ignore)
            .accessibilityLabel("Health \(status.hitPoints) of \(status.maxHitPoints), \(status.coins) coins, turn \(turn)")
        }
    }
}

private struct HealthBar: View {
    let fraction: Double

    var body: some View {
        let clamped = min(max(fraction, 0), 1)
        let colour = clamped > 0.5 ? Retro.green : clamped > 0.25 ? Retro.yellow : Retro.red
        ZStack(alignment: .leading) {
            Rectangle().fill(Retro.ink.opacity(0.2))
            Rectangle().fill(colour).frame(width: 80 * clamped)
        }
        .frame(width: 80, height: 7)
        .overlay(Rectangle().strokeBorder(Retro.border, lineWidth: 1.5))
        .animation(.easeOut(duration: 0.3), value: clamped)
    }
}

/// The Pokémon-style text box showing what just happened.
private struct MessageBox: View {
    let lines: [String]

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ForEach(Array(lines.enumerated()), id: \.offset) { item in
                Text(item.element)
                    .font(.pixel(8))
                    .lineSpacing(4)
                    .fixedSize(horizontal: false, vertical: true)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.trailing, 10)
        .retroBox(padding: 12)
        .overlay(alignment: .bottomTrailing) {
            Image(systemName: "arrowtriangle.down.fill")
                .font(.system(size: 7))
                .foregroundStyle(Retro.ink)
                .padding(10)
        }
        .accessibilityElement(children: .combine)
        .accessibilityHint("Shows the full message log")
    }
}

// MARK: - Controls

private struct ControlDeck: View {
    @EnvironmentObject private var session: GameSession

    var body: some View {
        Group {
            if session.result != nil {
                Color.clear
            } else if session.isSubmenu {
                SubmenuPanel()
            } else {
                HStack(alignment: .top, spacing: 10) {
                    DPad()
                    ActionPanel()
                }
            }
        }
        .padding(10)
        .frame(maxWidth: .infinity)
        .frame(height: 232)
        .background(Retro.panel)
        .disabled(!session.isWaitingForPlayer)
    }
}

private struct DPad: View {
    @EnvironmentObject private var session: GameSession

    private static let keys: [[Character]] = [["7", "8", "9"], ["4", "5", "6"], ["1", "2", "3"]]
    private static let icons: [Character: String] = [
        "7": "arrow.up.left", "8": "arrow.up", "9": "arrow.up.right",
        "4": "arrow.left", "5": "hourglass", "6": "arrow.right",
        "1": "arrow.down.left", "2": "arrow.down", "3": "arrow.down.right",
    ]

    var body: some View {
        VStack(spacing: 6) {
            Grid(horizontalSpacing: 6, verticalSpacing: 6) {
                ForEach(Self.keys, id: \.self) { row in
                    GridRow {
                        ForEach(row, id: \.self) { key in
                            padButton(key)
                        }
                    }
                }
            }
            Text(session.options.contains { $0.key == "5" } ? "WAIT = MIDDLE" : " ")
                .font(.pixel(6))
                .foregroundStyle(Color.white.opacity(0.4))
        }
    }

    private func padButton(_ key: Character) -> some View {
        let option = session.options.first { $0.key == key }
        return Button {
            session.choose(key)
        } label: {
            Image(systemName: Self.icons[key] ?? "circle")
                .font(.system(size: 18, weight: .heavy))
                .foregroundStyle(option == nil ? Color.white.opacity(0.18) : Color.white)
                .frame(width: 50, height: 50)
        }
        .buttonStyle(ConsoleButtonStyle(tint: key == "5" ? Retro.button.opacity(0.7) : Retro.button))
        .disabled(option == nil)
        .accessibilityLabel(option?.shortLabel ?? "Blocked")
    }
}

private struct ActionPanel: View {
    @EnvironmentObject private var session: GameSession

    var body: some View {
        let actions = session.options.filter { $0.kind != .move && $0.kind != .quit && $0.key != "5" }
        VStack(alignment: .leading, spacing: 8) {
            Text("ACTIONS")
                .font(.pixel(8))
                .foregroundStyle(Retro.yellow)
            ScrollView {
                VStack(spacing: 6) {
                    if actions.isEmpty {
                        Text("Nothing to do here. Explore!")
                            .font(.pixel(7))
                            .lineSpacing(4)
                            .foregroundStyle(Color.white.opacity(0.5))
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.top, 6)
                    }
                    ForEach(actions) { option in
                        ActionRow(option: option) { session.choose(option.key) }
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct ActionRow: View {
    let option: MenuOption
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                Image(systemName: Retro.icon(for: option.kind))
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(Retro.tint(for: option.kind))
                    .frame(width: 18)
                Text(option.shortLabel)
                    .font(.pixel(7))
                    .lineSpacing(4)
                    .foregroundStyle(Color.white)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 9)
        }
        .buttonStyle(ConsoleButtonStyle())
    }
}

/// Shop, gun and target menus, styled like a Pokémon menu.
private struct SubmenuPanel: View {
    @EnvironmentObject private var session: GameSession

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let footer = session.footer {
                Text(footer)
                    .font(.pixel(7))
                    .lineSpacing(4)
                    .foregroundStyle(Retro.yellow)
                    .fixedSize(horizontal: false, vertical: true)
            }
            ScrollView {
                VStack(alignment: .leading, spacing: 2) {
                    ForEach(session.options) { option in
                        SubmenuRow(option: option) { session.choose(option.key) }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .retroBox(padding: 8)
        }
    }
}

private struct SubmenuRow: View {
    let option: MenuOption
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(alignment: .firstTextBaseline, spacing: 8) {
                Text("▶").font(.pixel(8))
                Text(option.shortLabel)
                    .font(.pixel(8))
                    .lineSpacing(4)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
            .foregroundStyle(Retro.ink)
            .padding(.vertical, 8)
            .padding(.horizontal, 4)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Overlays and sheets

private struct GameOverCard: View {
    let result: GameResult
    let onNewGame: () -> Void

    private var playerWon: Bool { result.message.contains("Player wins") }

    var body: some View {
        ZStack {
            Color.black.opacity(0.55).ignoresSafeArea()
            VStack(spacing: 18) {
                SpriteImage(name: playerWon ? "actor_player" : "item_corpse", size: 64)
                Text(playerWon ? "VICTORY!" : "GAME OVER")
                    .font(.pixel(18))
                    .foregroundStyle(playerWon ? Color(red: 0.75, green: 0.5, blue: 0.1) : Retro.red)
                Text(result.message)
                    .font(.pixel(8))
                    .lineSpacing(5)
                    .multilineTextAlignment(.center)
                Button(action: onNewGame) {
                    Text("NEW GAME")
                        .font(.pixel(10))
                        .foregroundStyle(Color.white)
                        .padding(.horizontal, 18)
                        .padding(.vertical, 12)
                }
                .buttonStyle(ConsoleButtonStyle())
            }
            .padding(10)
            .retroBox(padding: 18)
            .padding(28)
        }
    }
}

private struct LogSheet: View {
    let entries: [LogEntry]
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 8) {
                        ForEach(Array(entries.enumerated()), id: \.element.id) { index, entry in
                            if index == 0 || entries[index - 1].turn != entry.turn {
                                Text("— TURN \(entry.turn) —")
                                    .font(.pixel(7))
                                    .foregroundStyle(Retro.yellow)
                                    .padding(.top, 8)
                            }
                            Text(entry.text)
                                .font(.pixel(7))
                                .lineSpacing(4)
                                .foregroundStyle(entry.text.contains("Player") ? Color.white : Color.white.opacity(0.65))
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .id(entry.id)
                        }
                    }
                    .padding(16)
                }
                .onAppear {
                    if let last = entries.last { proxy.scrollTo(last.id, anchor: .bottom) }
                }
            }
            .background(Retro.panel)
            .navigationTitle("Message log")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") { dismiss() }
                }
            }
        }
        .preferredColorScheme(.dark)
    }
}

private struct LegendSheet: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            List {
                Section("How to play") {
                    Text("Protect the humans and wipe out every zombie, including Mambo Marie when she appears.")
                    Text("Tap a square to walk towards it, or to attack, open a door or use what's there. Hold a square to see what it is.")
                    Text("Use the pad to move one step, or the middle button to wait. Everything else you can do is in the Actions list.")
                    Text("Zombies drop coins. The Wallmart shop in town opens after 70 turns. Ride the train to get there.")
                    Text("Drag the map to look around and pinch to zoom, from the whole map down to a close-up. Tap ME to jump back to your character.")
                }
                legendSection("Characters", Retro.characters)
                legendSection("Items", Retro.items)
                legendSection("Places", Retro.grounds)
                Section("Credits") {
                    Text("Sprites by Kenney (kenney.nl), CC0. Font: Press Start 2P, SIL Open Font License. Music composed for this game.")
                        .font(.footnote)
                }
            }
            .navigationTitle("How to play")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") { dismiss() }
                }
            }
        }
        .preferredColorScheme(.dark)
    }

    private func legendSection(_ title: String, _ entries: [LegendEntry]) -> some View {
        Section(title) {
            ForEach(entries) { entry in
                HStack(spacing: 14) {
                    SpriteImage(name: entry.sprite, size: 32)
                        .background(Color(red: 0.45, green: 0.75, blue: 0.4).opacity(0.35), in: RoundedRectangle(cornerRadius: 4))
                    Text(entry.name)
                }
            }
        }
    }
}
