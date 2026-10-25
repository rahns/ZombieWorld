import SwiftUI
import ZombieWorldCore

/// Draws the map as coloured monospaced text, either zoomed in around the player or whole.
struct MapView: View {
    let frame: MapFrame
    let showWholeMap: Bool

    /// Monospaced glyph proportions relative to the font size.
    private static let glyphWidth: CGFloat = 0.6
    private static let lineHeight: CGFloat = 1.18
    private static let zoomedFontSize: CGFloat = 17

    var body: some View {
        GeometryReader { geometry in
            let fontSize = fontSize(for: geometry.size)
            let cellWidth = fontSize * Self.glyphWidth
            let rowHeight = fontSize * Self.lineHeight
            let columns = min(frame.width, max(1, Int(geometry.size.width / cellWidth)))
            let rows = min(frame.height, max(1, Int(geometry.size.height / rowHeight)))
            let originX = origin(focus: frame.focusX, visible: columns, total: frame.width)
            let originY = origin(focus: frame.focusY, visible: rows, total: frame.height)

            VStack(alignment: .leading, spacing: 0) {
                ForEach(originY..<originY + rows, id: \.self) { y in
                    Text(attributedRow(y, from: originX, count: columns))
                        .font(.system(size: fontSize, weight: .semibold, design: .monospaced))
                        .fixedSize()
                        .frame(height: rowHeight)
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .accessibilityElement(children: .ignore)
            .accessibilityLabel(accessibilitySummary)
        }
    }

    private func fontSize(for size: CGSize) -> CGFloat {
        guard showWholeMap, frame.width > 0, frame.height > 0 else { return Self.zoomedFontSize }
        let byWidth = size.width / (CGFloat(frame.width) * Self.glyphWidth)
        let byHeight = size.height / (CGFloat(frame.height) * Self.lineHeight)
        return max(4, min(byWidth, byHeight, Self.zoomedFontSize))
    }

    /// Keeps the focus centred where possible without scrolling past the edges.
    private func origin(focus: Int?, visible: Int, total: Int) -> Int {
        guard let focus, visible < total else { return 0 }
        return min(max(0, focus - visible / 2), total - visible)
    }

    private func attributedRow(_ y: Int, from x: Int, count: Int) -> AttributedString {
        let row = Array(frame.rows[y])
        var result = AttributedString()
        var index = x
        while index < x + count {
            let symbol = row[index]
            var runEnd = index + 1
            while symbol != "@", runEnd < x + count, row[runEnd] != "@", Theme.color(for: row[runEnd]) == Theme.color(for: symbol) {
                runEnd += 1
            }
            var run = AttributedString(String(row[index..<runEnd]))
            run.foregroundColor = Theme.color(for: symbol)
            if symbol == "@" {
                run.backgroundColor = Color.yellow.opacity(0.25)
            }
            result += run
            index = runEnd
        }
        return result
    }

    private var accessibilitySummary: String {
        let all = frame.rows.joined()
        let zombies = all.filter { $0 == "Z" }.count
        let humans = all.filter { $0 == "H" || $0 == "f" }.count
        var text = "Map. \(zombies) zombies and \(humans) humans visible."
        if let x = frame.focusX, let y = frame.focusY {
            text += " You are at column \(x), row \(y)."
        }
        return text
    }
}
