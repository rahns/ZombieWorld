import SwiftUI
import ZombieWorldCore

struct GameView: View {
    @EnvironmentObject private var session: GameSession
    @State private var showWholeMap = false
    @State private var showLegend = false
    @State private var confirmQuit = false
    @State private var confirmNewGame = false
    @FocusState private var hasKeyboardFocus: Bool

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                StatusBar(status: session.status, turn: session.turn)

                Group {
                    if let frame = session.frame {
                        MapView(frame: frame, showWholeMap: showWholeMap)
                            .contentShape(Rectangle())
                            .onTapGesture { showWholeMap.toggle() }
                    } else {
                        ProgressView()
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 290)
                .background(Theme.background)

                LogView(entries: session.log)
                    .frame(maxHeight: .infinity)

                ControlPanel()
                    .background(Theme.panel)
            }
            .background(Theme.background.ignoresSafeArea())
            .navigationTitle("ZombieWorld")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar { toolbar }
            .sheet(isPresented: $showLegend) { LegendView() }
            .confirmationDialog("Give up and end this game?", isPresented: $confirmQuit, titleVisibility: .visible) {
                Button("Quit game", role: .destructive) { session.choose("+") }
            }
            .confirmationDialog("Start a new game? Your current game will be lost.", isPresented: $confirmNewGame, titleVisibility: .visible) {
                Button("New game", role: .destructive) { session.newGame() }
            }
            .overlay {
                if let result = session.result {
                    GameOverCard(result: result) { session.newGame() }
                }
            }
        }
        .focusable()
        .focused($hasKeyboardFocus)
        .focusEffectDisabled()
        .onAppear { hasKeyboardFocus = true }
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
    }

    @ToolbarContentBuilder
    private var toolbar: some ToolbarContent {
        ToolbarItem(placement: .topBarLeading) {
            Button {
                showWholeMap.toggle()
            } label: {
                Image(systemName: showWholeMap ? "plus.magnifyingglass" : "map")
            }
            .accessibilityLabel(showWholeMap ? "Zoom in on player" : "Show whole map")
        }
        ToolbarItem(placement: .topBarTrailing) {
            Menu {
                Button {
                    showLegend = true
                } label: {
                    Label("Map legend", systemImage: "list.bullet.rectangle")
                }
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
                Image(systemName: "ellipsis.circle")
            }
        }
    }
}

private struct StatusBar: View {
    let status: PlayerStatus?
    let turn: Int

    var body: some View {
        HStack(spacing: 14) {
            if let status {
                Image(systemName: "heart.fill")
                    .foregroundStyle(.red)
                ProgressView(value: Double(max(0, status.hitPoints)), total: Double(status.maxHitPoints))
                    .tint(healthColor(status))
                    .frame(maxWidth: 140)
                Text("\(status.hitPoints)/\(status.maxHitPoints)")
                    .monospacedDigit()
                Spacer()
                Label("\(status.coins)", systemImage: "dollarsign.circle.fill")
                    .foregroundStyle(.yellow)
                    .monospacedDigit()
            } else {
                Spacer()
            }
            Text("Turn \(turn)")
                .foregroundStyle(.secondary)
                .monospacedDigit()
        }
        .font(.subheadline.weight(.semibold))
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .background(Theme.panel)
    }

    private func healthColor(_ status: PlayerStatus) -> Color {
        let fraction = Double(status.hitPoints) / Double(max(1, status.maxHitPoints))
        return fraction > 0.5 ? .green : fraction > 0.25 ? .orange : .red
    }
}

private struct LogView: View {
    let entries: [LogEntry]

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView {
                LazyVStack(alignment: .leading, spacing: 2) {
                    ForEach(entries) { entry in
                        Text(entry.text)
                            .font(.caption.monospaced())
                            .foregroundStyle(color(for: entry.text))
                            .fontWeight(entry.text.contains("Player") ? .bold : .regular)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .id(entry.id)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
            .onChange(of: entries.last?.id) { _, last in
                guard let last else { return }
                withAnimation(.easeOut(duration: 0.15)) {
                    proxy.scrollTo(last, anchor: .bottom)
                }
            }
        }
    }

    private func color(for text: String) -> Color {
        if text.contains("is killed") || text.contains("Game Over") || text.contains("Game over") { return .red }
        if text.contains("rose from the dead") || text.contains("Mambo") { return Color(red: 0.85, green: 0.5, blue: 1.0) }
        if text.hasPrefix("Player") { return .yellow }
        if text.contains("Player") { return .orange }
        return Color(white: 0.75)
    }
}

/// The d-pad and action list for the main menu, or the options of a sub-menu.
private struct ControlPanel: View {
    @EnvironmentObject private var session: GameSession

    private static let padKeys: [[Character]] = [["7", "8", "9"], ["4", "5", "6"], ["1", "2", "3"]]
    private static let padIcons: [Character: String] = [
        "7": "arrow.up.left", "8": "arrow.up", "9": "arrow.up.right",
        "4": "arrow.left", "5": "hourglass", "6": "arrow.right",
        "1": "arrow.down.left", "2": "arrow.down", "3": "arrow.down.right",
    ]

    var body: some View {
        Group {
            if session.result != nil {
                EmptyView()
            } else if session.isSubmenu {
                submenu
            } else {
                mainMenu
            }
        }
        .frame(height: 210)
        .disabled(!session.isWaitingForPlayer)
    }

    private var mainMenu: some View {
        let padKeys = Set(Self.padKeys.joined())
        let others = session.options.filter { !padKeys.contains($0.key) && $0.key != "+" }
        return HStack(alignment: .top, spacing: 12) {
            dPad
            OptionList(options: others, emptyText: "Move with the pad, or wait a turn.")
        }
        .padding(12)
    }

    private var dPad: some View {
        Grid(horizontalSpacing: 6, verticalSpacing: 6) {
            ForEach(Self.padKeys, id: \.self) { row in
                GridRow {
                    ForEach(row, id: \.self) { key in
                        padButton(key)
                    }
                }
            }
        }
    }

    private func padButton(_ key: Character) -> some View {
        let option = session.options.first { $0.key == key }
        return Button {
            session.choose(key)
        } label: {
            Image(systemName: Self.padIcons[key] ?? "circle")
                .font(.title3.weight(.semibold))
                .frame(width: 54, height: 54)
                .background(Color.white.opacity(option == nil ? 0.03 : 0.1), in: RoundedRectangle(cornerRadius: 10))
        }
        .buttonStyle(.plain)
        .foregroundStyle(option == nil ? Color.white.opacity(0.2) : (key == "5" ? Color.secondary : Theme.accent))
        .disabled(option == nil)
        .accessibilityLabel(option?.shortLabel ?? "Blocked")
    }

    private var submenu: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let footer = session.footer {
                Text(footer)
                    .font(.footnote.monospaced())
                    .foregroundStyle(.secondary)
            }
            OptionList(options: session.options, emptyText: "")
        }
        .padding(12)
    }
}

private struct OptionList: View {
    @EnvironmentObject private var session: GameSession
    let options: [MenuOption]
    let emptyText: String

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 6) {
                if options.isEmpty {
                    Text(emptyText)
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                        .padding(.top, 8)
                }
                ForEach(options) { option in
                    Button {
                        session.choose(option.key)
                    } label: {
                        HStack(alignment: .firstTextBaseline, spacing: 8) {
                            Text(String(option.key))
                                .font(.caption.monospaced().weight(.bold))
                                .foregroundStyle(Theme.background)
                                .frame(width: 20, height: 20)
                                .background(Theme.accent, in: RoundedRectangle(cornerRadius: 5))
                            Text(option.shortLabel)
                                .font(.subheadline)
                                .multilineTextAlignment(.leading)
                                .frame(maxWidth: .infinity, alignment: .leading)
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 8)
                        .background(Color.white.opacity(0.07), in: RoundedRectangle(cornerRadius: 8))
                    }
                    .buttonStyle(.plain)
                }
            }
        }
    }
}

private struct GameOverCard: View {
    let result: GameResult
    let onNewGame: () -> Void

    private var playerWon: Bool { result.message.contains("Player wins") }

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: playerWon ? "trophy.fill" : "xmark.octagon.fill")
                .font(.system(size: 48))
                .foregroundStyle(playerWon ? Color.yellow : Color.red)
            Text(playerWon ? "Victory" : "Game Over")
                .font(.largeTitle.bold())
            Text(result.message)
                .multilineTextAlignment(.center)
                .foregroundStyle(.secondary)
            Button("New Game", action: onNewGame)
                .buttonStyle(.borderedProminent)
                .tint(Theme.accent)
                .foregroundStyle(Theme.background)
        }
        .padding(28)
        .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 20))
        .padding(32)
    }
}

private struct LegendView: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            List {
                Section("Map") {
                    ForEach(Theme.legend) { entry in
                        HStack(spacing: 16) {
                            Text(entry.symbol)
                                .font(.body.monospaced().weight(.bold))
                                .foregroundStyle(Theme.color(for: entry.symbol.first ?? " "))
                                .frame(width: 64, alignment: .leading)
                            Text(entry.meaning)
                        }
                    }
                }
                Section("How to play") {
                    Text("Protect the humans in the compound and wipe out every zombie, including Mambo Marie when she appears.")
                    Text("Move with the pad or the number keys on a keyboard. Other actions appear beside the pad.")
                    Text("Zombies drop coins. The Wallmart shop in town opens after 70 turns; take the train (*) to get there.")
                    Text("Tap the map to switch between the close-up and the whole map.")
                }
            }
            .navigationTitle("Legend")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") { dismiss() }
                }
            }
        }
    }
}
