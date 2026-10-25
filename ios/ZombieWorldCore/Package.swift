// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "ZombieWorldCore",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "ZombieWorldCore", targets: ["ZombieWorldCore"]),
        .executable(name: "zombieworld-cli", targets: ["zombieworld-cli"]),
    ],
    targets: [
        .target(name: "ZombieWorldCore"),
        .executableTarget(name: "zombieworld-cli", dependencies: ["ZombieWorldCore"]),
        .testTarget(name: "ZombieWorldCoreTests", dependencies: ["ZombieWorldCore"]),
    ]
)
