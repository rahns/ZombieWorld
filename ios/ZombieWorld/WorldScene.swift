import SpriteKit
import UIKit
import ZombieWorldCore

/// Draws the map with sprites, animates actors between frames and turns touches into map squares.
final class WorldScene: SKScene {
    static let tileSize: CGFloat = 16

    /// Called with the map square that was tapped.
    var onTap: ((Int, Int) -> Void)?
    /// Called with the map square that was pressed and held.
    var onLongPress: ((Int, Int) -> Void)?

    var showsWholeMap = false {
        didSet { updateCamera(animated: true) }
    }

    private let worldNode = SKNode()
    private let groundLayer = SKNode()
    private let decorationLayer = SKNode()
    private let itemLayer = SKNode()
    private let markerLayer = SKNode()
    private let actorLayer = SKNode()
    private let effectLayer = SKNode()
    private let cameraNode = SKCameraNode()

    private var game: Int?
    private var mapIndex: Int?
    private var mapWidth = 0
    private var mapHeight = 0
    private var groundNodes: [SKSpriteNode] = []
    private var groundSymbols: [Character] = []
    private var decorationNodes: [Int: SKSpriteNode] = [:]
    private var itemNodes: [Int: SKSpriteNode] = [:]
    private var itemSymbols: [Int: Character] = [:]
    private var actorNodes: [Int: ActorNode] = [:]
    private var actors: [Int: ActorSnapshot] = [:]
    private var focus = CGPoint.zero
    private var zoom: CGFloat = 2.25
    private var pinchStartZoom: CGFloat = 2.25
    private var touchStart: CGPoint?
    private var longPressFired = false

    override init(size: CGSize) {
        super.init(size: size)
        scaleMode = .resizeFill
        anchorPoint = CGPoint(x: 0.5, y: 0.5)
        backgroundColor = Retro.night
        addChild(worldNode)
        for (z, layer) in [groundLayer, decorationLayer, itemLayer, markerLayer, actorLayer, effectLayer].enumerated() {
            layer.zPosition = CGFloat(z * 100)
            worldNode.addChild(layer)
        }
        addChild(cameraNode)
        camera = cameraNode
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) is not supported")
    }

    override func didMove(to view: SKView) {
        let pinch = UIPinchGestureRecognizer(target: self, action: #selector(handlePinch(_:)))
        view.addGestureRecognizer(pinch)
    }

    override func didChangeSize(_ oldSize: CGSize) {
        updateCamera(animated: false)
    }

    static func point(_ x: Int, _ y: Int) -> CGPoint {
        CGPoint(x: CGFloat(x) * tileSize + tileSize / 2, y: -CGFloat(y) * tileSize - tileSize / 2)
    }

    // MARK: - Showing frames

    /// Shows a new frame, animating whatever changed since the last one of the same game.
    func apply(_ frame: MapFrame, game: Int) {
        guard !frame.ground.isEmpty else { return }
        let isNewMap = game != self.game || frame.mapIndex != mapIndex || frame.width != mapWidth || frame.height != mapHeight
        self.game = game
        if isNewMap {
            rebuildMap(frame)
        } else {
            updateGround(frame)
        }
        updateItems(frame, animated: !isNewMap)
        updateActors(frame, animated: !isNewMap)
        if let x = frame.focusX, let y = frame.focusY {
            focus = Self.point(x, y)
        }
        updateCamera(animated: !isNewMap)
    }

    /// Highlights squares the player can act on: red for enemies, gold for everything else.
    func setMarkers(_ markers: [(x: Int, y: Int, hostile: Bool)]) {
        markerLayer.removeAllChildren()
        for marker in markers {
            let node = Self.bracket(color: marker.hostile ? Retro.danger : Retro.gold)
            node.position = Self.point(marker.x, marker.y)
            node.run(.repeatForever(.sequence([.fadeAlpha(to: 0.45, duration: 0.5), .fadeAlpha(to: 1, duration: 0.5)])))
            markerLayer.addChild(node)
        }
    }

    private func rebuildMap(_ frame: MapFrame) {
        for layer in [groundLayer, decorationLayer, itemLayer, markerLayer, actorLayer, effectLayer] {
            layer.removeAllChildren()
        }
        mapIndex = frame.mapIndex
        mapWidth = frame.width
        mapHeight = frame.height
        groundNodes = []
        groundSymbols = []
        decorationNodes = [:]
        itemNodes = [:]
        itemSymbols = [:]
        actorNodes = [:]
        actors = [:]

        let rows = frame.ground.map(Array.init)
        for y in 0..<mapHeight {
            for x in 0..<mapWidth {
                let symbol = rows[y][x]
                groundNodes.append(makeTile(in: groundLayer, x: x, y: y))
                groundSymbols.append(symbol)
                setGround(symbol, x: x, y: y)
            }
        }
    }

    private func makeTile(in layer: SKNode, x: Int, y: Int) -> SKSpriteNode {
        let node = SKSpriteNode(texture: nil, size: CGSize(width: Self.tileSize, height: Self.tileSize))
        node.position = Self.point(x, y)
        layer.addChild(node)
        return node
    }

    private func updateGround(_ frame: MapFrame) {
        let rows = frame.ground.map(Array.init)
        for y in 0..<mapHeight {
            for x in 0..<mapWidth where groundSymbols[y * mapWidth + x] != rows[y][x] {
                groundSymbols[y * mapWidth + x] = rows[y][x]
                setGround(rows[y][x], x: x, y: y)
                decorationNodes[y * mapWidth + x]?.run(.sequence([.scale(to: 1.25, duration: 0.1), .scale(to: 1, duration: 0.1)]))
            }
        }
    }

    private func setGround(_ symbol: Character, x: Int, y: Int) {
        let index = y * mapWidth + x
        let sprites = Sprites.ground(symbol, x: x, y: y)
        groundNodes[index].texture = Sprites.texture(sprites.base)
        if let decoration = sprites.decoration {
            let node: SKSpriteNode
            if let existing = decorationNodes[index] {
                node = existing
            } else {
                node = makeTile(in: decorationLayer, x: x, y: y)
                decorationNodes[index] = node
            }
            node.texture = Sprites.texture(decoration)
        } else if let node = decorationNodes.removeValue(forKey: index) {
            node.removeFromParent()
        }
    }

    private func updateItems(_ frame: MapFrame, animated: Bool) {
        let rows = frame.items.map(Array.init)
        for y in 0..<min(mapHeight, rows.count) {
            for x in 0..<min(mapWidth, rows[y].count) {
                let index = y * mapWidth + x
                let symbol = rows[y][x]
                guard itemSymbols[index] != symbol else { continue }
                if let name = Sprites.item(symbol) {
                    itemSymbols[index] = symbol
                    let node: SKSpriteNode
                    if let existing = itemNodes[index] {
                        node = existing
                    } else {
                        node = makeTile(in: itemLayer, x: x, y: y)
                        itemNodes[index] = node
                    }
                    node.texture = Sprites.texture(name)
                    if animated {
                        node.setScale(0.3)
                        node.run(.sequence([.scale(to: 1.15, duration: 0.12), .scale(to: 1, duration: 0.08)]))
                    }
                } else {
                    itemSymbols[index] = nil
                    if let node = itemNodes.removeValue(forKey: index) {
                        node.run(.sequence([.group([.scale(to: 0.2, duration: 0.15), .fadeOut(withDuration: 0.15)]), .removeFromParent()]))
                    }
                }
            }
        }
    }

    private func updateActors(_ frame: MapFrame, animated: Bool) {
        var seen = Set<Int>()
        for actor in frame.actors {
            seen.insert(actor.id)
            let target = Self.point(actor.x, actor.y)
            if let node = actorNodes[actor.id], let old = actors[actor.id] {
                node.update(actor, texture: Sprites.texture(Sprites.actor(actor)))
                let dx = actor.x - old.x, dy = actor.y - old.y
                if dx != 0 { node.face(left: dx < 0) }
                if dx != 0 || dy != 0 {
                    if max(abs(dx), abs(dy)) <= 1 && animated {
                        node.walk(to: target)
                    } else {
                        node.teleport(to: target)
                    }
                }
                let change = actor.hitPoints - old.hitPoints
                if change != 0 && animated {
                    showFloatingText(change < 0 ? "\(change)" : "+\(change)",
                                     color: change < 0 ? Retro.danger : Retro.heal, at: target)
                    if change < 0 { node.flashHurt() }
                }
            } else {
                let node = ActorNode(actor, texture: Sprites.texture(Sprites.actor(actor)))
                node.position = target
                actorLayer.addChild(node)
                actorNodes[actor.id] = node
                if animated { node.appear() }
            }
            actors[actor.id] = actor
        }

        for (id, node) in actorNodes where !seen.contains(id) {
            actorNodes[id] = nil
            actors[id] = nil
            if animated {
                showFloatingText("KO", color: Retro.danger, at: node.position)
                node.die()
            } else {
                node.removeFromParent()
            }
        }
        // Lower rows are drawn in front.
        for (id, node) in actorNodes {
            node.zPosition = CGFloat(actors[id]?.y ?? 0)
        }
    }

    private func showFloatingText(_ text: String, color: UIColor, at point: CGPoint) {
        let label = Self.pixelLabel(text, color: color, size: 6)
        label.position = CGPoint(x: point.x, y: point.y + 6)
        label.zPosition = 1000
        effectLayer.addChild(label)
        label.run(.sequence([
            .group([.moveBy(x: 0, y: 12, duration: 0.8), .sequence([.wait(forDuration: 0.5), .fadeOut(withDuration: 0.3)])]),
            .removeFromParent(),
        ]))
    }

    static func pixelLabel(_ text: String, color: UIColor, size: CGFloat) -> SKNode {
        let node = SKNode()
        for (offset, colour) in [(CGPoint(x: 0.6, y: -0.6), UIColor.black), (.zero, color)] {
            let label = SKLabelNode(fontNamed: Retro.fontName)
            label.text = text
            label.fontSize = size
            label.fontColor = colour
            label.verticalAlignmentMode = .center
            label.position = offset
            node.addChild(label)
        }
        return node
    }

    private static func bracket(color: UIColor) -> SKShapeNode {
        let half = tileSize / 2 - 0.5, arm: CGFloat = 4
        let path = CGMutablePath()
        for (sx, sy) in [(-1.0, -1.0), (1.0, -1.0), (-1.0, 1.0), (1.0, 1.0)] {
            let corner = CGPoint(x: sx * half, y: sy * half)
            path.move(to: CGPoint(x: corner.x - sx * arm, y: corner.y))
            path.addLine(to: corner)
            path.addLine(to: CGPoint(x: corner.x, y: corner.y - sy * arm))
        }
        let node = SKShapeNode(path: path)
        node.strokeColor = color
        node.lineWidth = 1
        node.isAntialiased = false
        return node
    }

    // MARK: - Camera

    private func updateCamera(animated: Bool) {
        guard mapWidth > 0, size.width > 0, size.height > 0 else { return }
        let mapSize = CGSize(width: CGFloat(mapWidth) * Self.tileSize, height: CGFloat(mapHeight) * Self.tileSize)
        let scale: CGFloat
        var target: CGPoint
        if showsWholeMap {
            scale = 1 / min(size.width / mapSize.width, size.height / mapSize.height)
            target = CGPoint(x: mapSize.width / 2, y: -mapSize.height / 2)
        } else {
            scale = 1 / zoom
            target = focus
        }
        // Keep the camera inside the map where the map is bigger than the screen.
        let halfWidth = size.width * scale / 2, halfHeight = size.height * scale / 2
        target.x = mapSize.width <= halfWidth * 2 ? mapSize.width / 2 : min(max(target.x, halfWidth), mapSize.width - halfWidth)
        target.y = mapSize.height <= halfHeight * 2 ? -mapSize.height / 2 : max(min(target.y, -halfHeight), -mapSize.height + halfHeight)

        cameraNode.removeAction(forKey: "camera")
        if animated {
            let move = SKAction.move(to: target, duration: 0.22)
            move.timingMode = .easeOut
            let zoomAction = SKAction.scale(to: scale, duration: 0.22)
            zoomAction.timingMode = .easeOut
            cameraNode.run(.group([move, zoomAction]), withKey: "camera")
        } else {
            cameraNode.position = target
            cameraNode.setScale(scale)
        }
    }

    @objc private func handlePinch(_ recognizer: UIPinchGestureRecognizer) {
        switch recognizer.state {
        case .began:
            pinchStartZoom = showsWholeMap ? 1 / cameraNode.xScale : zoom
            cancelTouch()
        case .changed:
            zoom = min(max(pinchStartZoom * recognizer.scale, 1), 5)
            if showsWholeMap { showsWholeMap = false }
            updateCamera(animated: false)
        default:
            break
        }
    }

    // MARK: - Touches

    private func tile(at touch: UITouch) -> (x: Int, y: Int)? {
        let point = touch.location(in: worldNode)
        let x = Int(floor(point.x / Self.tileSize)), y = Int(floor(-point.y / Self.tileSize))
        guard (0..<mapWidth).contains(x), (0..<mapHeight).contains(y) else { return nil }
        return (x, y)
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard touches.count == 1, let touch = touches.first, let view else { return }
        touchStart = touch.location(in: view)
        longPressFired = false
        let square = tile(at: touch)
        run(.sequence([.wait(forDuration: 0.45), .run { [weak self] in
            guard let self, self.touchStart != nil, let square else { return }
            self.longPressFired = true
            self.onLongPress?(square.x, square.y)
        }]), withKey: "longPress")
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard let touch = touches.first, let view, let start = touchStart else { return }
        let point = touch.location(in: view)
        if hypot(point.x - start.x, point.y - start.y) > 12 { cancelTouch() }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        defer { cancelTouch() }
        guard touchStart != nil, !longPressFired, let touch = touches.first, let square = tile(at: touch) else { return }
        onTap?(square.x, square.y)
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        cancelTouch()
    }

    private func cancelTouch() {
        touchStart = nil
        removeAction(forKey: "longPress")
    }
}

/// A character on the map: sprite, shadow and a health bar when hurt.
private final class ActorNode: SKNode {
    private let sprite: SKSpriteNode
    private let healthBack = SKSpriteNode(color: .black, size: CGSize(width: 12, height: 2))
    private let healthFill = SKSpriteNode(color: .green, size: CGSize(width: 12, height: 2))
    private let isPlayer: Bool

    init(_ actor: ActorSnapshot, texture: SKTexture) {
        sprite = SKSpriteNode(texture: texture, size: CGSize(width: WorldScene.tileSize, height: WorldScene.tileSize))
        isPlayer = actor.symbol == "@"
        super.init()

        let shadow = SKShapeNode(ellipseOf: CGSize(width: 11, height: 3))
        shadow.fillColor = UIColor.black.withAlphaComponent(0.28)
        shadow.strokeColor = .clear
        shadow.position = CGPoint(x: 0, y: -7)
        addChild(shadow)

        sprite.position = CGPoint(x: 0, y: 0.5)
        addChild(sprite)
        // A gentle idle bob, out of step between actors.
        let bob = SKAction.sequence([.moveBy(x: 0, y: 1, duration: 0.45), .moveBy(x: 0, y: -1, duration: 0.45)])
        sprite.run(.sequence([.wait(forDuration: Double(actor.id % 7) * 0.13), .repeatForever(bob)]))

        healthBack.anchorPoint = CGPoint(x: 0, y: 0.5)
        healthFill.anchorPoint = CGPoint(x: 0, y: 0.5)
        healthBack.position = CGPoint(x: -6, y: 10)
        healthFill.position = CGPoint(x: -6, y: 10)
        healthBack.zPosition = 1
        healthFill.zPosition = 2
        addChild(healthBack)
        addChild(healthFill)
        updateHealth(actor)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) is not supported")
    }

    func update(_ actor: ActorSnapshot, texture: SKTexture) {
        sprite.texture = texture
        updateHealth(actor)
    }

    private func updateHealth(_ actor: ActorSnapshot) {
        let fraction = max(0, min(1, CGFloat(actor.hitPoints) / CGFloat(max(1, actor.maxHitPoints))))
        let hidden = isPlayer || fraction >= 1
        healthBack.isHidden = hidden
        healthFill.isHidden = hidden
        healthFill.size.width = 12 * fraction
        healthFill.color = fraction > 0.5 ? Retro.heal : fraction > 0.25 ? Retro.gold : Retro.danger
    }

    func face(left: Bool) {
        sprite.xScale = left ? -1 : 1
    }

    func walk(to point: CGPoint) {
        removeAction(forKey: "move")
        let move = SKAction.move(to: point, duration: 0.18)
        move.timingMode = .easeInEaseOut
        let hop = SKAction.sequence([.moveBy(x: 0, y: 2, duration: 0.09), .moveBy(x: 0, y: -2, duration: 0.09)])
        run(move, withKey: "move")
        sprite.run(hop)
    }

    func teleport(to point: CGPoint) {
        removeAction(forKey: "move")
        run(.sequence([.fadeOut(withDuration: 0.12), .move(to: point, duration: 0), .fadeIn(withDuration: 0.18)]), withKey: "move")
    }

    func flashHurt() {
        sprite.run(.sequence([
            .colorize(with: Retro.danger, colorBlendFactor: 0.8, duration: 0.05),
            .wait(forDuration: 0.1),
            .colorize(withColorBlendFactor: 0, duration: 0.15),
        ]))
        let shake = SKAction.sequence([.moveBy(x: 1.5, y: 0, duration: 0.04), .moveBy(x: -3, y: 0, duration: 0.04), .moveBy(x: 1.5, y: 0, duration: 0.04)])
        sprite.run(shake)
    }

    func appear() {
        alpha = 0
        setScale(0.4)
        run(.group([.fadeIn(withDuration: 0.25), .scale(to: 1, duration: 0.25)]))
    }

    func die() {
        sprite.color = Retro.danger
        sprite.colorBlendFactor = 0.7
        run(.sequence([
            .group([.fadeOut(withDuration: 0.4), .scaleX(to: 1.3, y: 0.2, duration: 0.4)]),
            .removeFromParent(),
        ]))
    }
}
