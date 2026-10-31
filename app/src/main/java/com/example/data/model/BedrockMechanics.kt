package com.example.data.model

data class MechanicGuide(
    val id: String,
    val title: String,
    val category: String, // "ENGINE", "PISTONS", "STORAGE", "COMBAT_FARMS", "SENSORS"
    val summary: String,
    val javaDifference: String,
    val practicalSolution: String,
    val tags: List<String>
)

data class LogicGateInfo(
    val name: String,
    val expression: String,
    val description: String,
    val truthTable: List<Pair<String, String>>, // Input -> Output
    val bedrockComponentList: String,
    val delayTicks: String,
    val commonUsage: String
)

object BedrockKnowledgeBase {

    val MECHANICS: List<MechanicGuide> = listOf(
        MechanicGuide(
            id = "random_update_order",
            title = "Random Update Order (Non-Determinism)",
            category = "ENGINE",
            summary = "When two or more pistons, droppers, or redstone lines are triggered on the exact same tick, Bedrock resolves their execution order randomly rather than by hash/directional coordinate.",
            javaDifference = "Java executes updates in deterministic locational order (facing/coordinate hierarchy). Bedrock's order changes every pulse.",
            practicalSolution = "Always enforce sequence with repeaters! Add at least a 1-redstone-tick (2-game-tick) repeater between two actions that must fire sequentially (e.g. piston 1 pushes, then piston 2 fires). Never rely on simultaneous pulse priority.",
            tags = listOf("engine", "timing", "pistons", "reliability", "desync")
        ),
        MechanicGuide(
            id = "no_quasi_connectivity",
            title = "No Quasi-Connectivity (No QC)",
            category = "PISTONS",
            summary = "Pistons, droppers, and dispensers will only activate if directly powered or if an adjacent solid block is powered. They never react to power 2 blocks above or diagonally.",
            javaDifference = "Java has Quasi-Connectivity (QC) inherited from iron door code. Bedrock has zero QC.",
            practicalSolution = "Take advantage of this! You can pack droppers, hoppers, and pistons directly next to each other vertically without accidental diagonal firing. To trigger a piston from above, use an observer, target block, or solid block step-down.",
            tags = listOf("pistons", "qc", "compact", "java_diff")
        ),
        MechanicGuide(
            id = "movable_tile_entities",
            title = "Movable Tile Entities (Chests, Hoppers, Furnaces)",
            category = "STORAGE",
            summary = "In Bedrock Edition, sticky and regular pistons can push, pull, and swap tile entities including Chests, Hoppers, Droppers, Dispensers, Furnaces, Smockers, and Barrels while retaining items.",
            javaDifference = "Java Edition pistons cannot push tile entities (they refuse to push and act as an immovable block).",
            practicalSolution = "Build shifting storage silos, multi-item sorting carousels, mobile crafters, and piston feed tape item transport systems with moving hoppers and chests. Great for compact hidden armories!",
            tags = listOf("storage", "hoppers", "chests", "pistons", "advantage")
        ),
        MechanicGuide(
            id = "sticky_piston_block_spit",
            title = "Sticky Pistons Retain Blocks (No 1-Tick Spit)",
            category = "PISTONS",
            summary = "Sticky pistons in Bedrock NEVER leave their held block behind, even on a 1-tick pulse or observer activation. They will always pull the block back upon retraction.",
            javaDifference = "Java sticky pistons 'spit' (drop) their block when given a 1-tick pulse and re-grab it on the next 1-tick pulse, forming the basis of T-Flip-Flops.",
            practicalSolution = "In Bedrock, build T-Flip-Flops using a Dropper-Hopper loop, a Cauldon/Water piston shifter, or a double piston extender. Do not try Java 1-tick block spitting.",
            tags = listOf("pistons", "t-flip-flop", "pulse", "java_diff")
        ),
        MechanicGuide(
            id = "dust_into_pistons",
            title = "Redstone Dust Points Into Pistons Directly",
            category = "PISTONS",
            summary = "Redstone dust placed next to a piston automatically bends and points directly into the piston face, powering it without needing a solid redirect block or Target Block.",
            javaDifference = "In Java, dust runs parallel unless redirected into a target block or repeater.",
            practicalSolution = "Save resources and space! Run a simple dust line right alongside a row of pistons (e.g., sugar cane or melon farms) and each piston will activate natively.",
            tags = listOf("dust", "pistons", "wiring", "simplification")
        ),
        MechanicGuide(
            id = "trident_killers",
            title = "Trident Killers & Looting III XP Transfer",
            category = "COMBAT_FARMS",
            summary = "Thrown tridents pushed continuously across a 2x2 or 1x1 piston chamber deal constant melee weapon damage to mobs. The player gets full XP.",
            javaDifference = "Only works in Bedrock Edition. Java tridents lose velocity and stop dealing piston damage.",
            practicalSolution = "Hold a sword enchanted with Looting III, Smite, or Bane of Arthropods in your main hand while standing near the farm! Bedrock transfers your held sword's Looting III enchantment to the trident kills automatically.",
            tags = listOf("trident", "farming", "looting", "xp", "pistons")
        ),
        MechanicGuide(
            id = "two_phase_tick_engine",
            title = "Two-Phase Tick Engine (Consumer vs Producer)",
            category = "ENGINE",
            summary = "Bedrock splits tick processing into distinct producer and consumer phases. Certain components only accept changes on even ticks, while others tick on odd ticks.",
            javaDifference = "Java has a single tick queue. Zero-tick pulses are impossible on Bedrock because piston minimum movement is 3 game ticks.",
            practicalSolution = "Avoid zero-tick farm designs copied from Java; they will crash or fail silently. Always give piston contraptions at least 3-4 game ticks (2 redstone ticks) between state changes.",
            tags = listOf("engine", "ticks", "zero-tick", "timing")
        ),
        MechanicGuide(
            id = "crafter_comparator_math",
            title = "Crafter (1.21+) Comparator Math",
            category = "STORAGE",
            summary = "In 1.21+, comparators reading a Crafter output a signal strength equal to the number of DISABLED slots (0 to 9), regardless of items inside.",
            javaDifference = "Same across both editions, but Crafters are movable by pistons in Bedrock!",
            practicalSolution = "Use Crafters as immovable, zero-lag analog signal constants! Disable 5 slots to emit an unbreakable Signal Strength 5 that hoppers cannot drain.",
            tags = listOf("crafter", "1.21", "comparator", "analog")
        ),
        MechanicGuide(
            id = "sculk_frequencies",
            title = "Sculk Sensor Vibration Frequencies (1-15)",
            category = "SENSORS",
            summary = "Calibrated Sculk Sensors filter vibrations using an input signal strength 1-15 matching the vibration type.",
            javaDifference = "Same vibration frequency specs across editions.",
            practicalSolution = "Reference: 1: Step, 2: Flap, 3: Swim, 4: Glide, 5: Splash, 6: Projectile Shoot, 7: Projectile Land, 8: Entity Damage, 9: Item Equip, 10: Block Interact, 11: Container Open, 12: Block Place, 13: Block Destroy, 14: Fluid Place/Take, 15: Explosion/Piston Extend.",
            tags = listOf("sculk", "sensors", "wireless", "frequencies")
        )
    )

    val LOGIC_GATES: List<LogicGateInfo> = listOf(
        LogicGateInfo(
            name = "AND Gate",
            expression = "Q = A AND B",
            description = "Outputs TRUE only when BOTH inputs A and B are active.",
            truthTable = listOf(
                "A=0, B=0" to "Q=0",
                "A=1, B=0" to "Q=0",
                "A=0, B=1" to "Q=0",
                "A=1, B=1" to "Q=1"
            ),
            bedrockComponentList = "2 Solid blocks, 2 Redstone Torches on top, 1 Torch on output side, 1 Redstone Dust in between.",
            delayTicks = "2 Redstone Ticks (0.2s)",
            commonUsage = "Security doors requiring two keys, multi-condition mob farm safety gates."
        ),
        LogicGateInfo(
            name = "OR Gate",
            expression = "Q = A OR B",
            description = "Outputs TRUE if EITHER input A, input B, or both are active.",
            truthTable = listOf(
                "A=0, B=0" to "Q=0",
                "A=1, B=0" to "Q=1",
                "A=0, B=1" to "Q=1",
                "A=1, B=1" to "Q=1"
            ),
            bedrockComponentList = "2 Repeaters or Diodes meeting at a single redstone wire.",
            delayTicks = "1 Redstone Tick (0.1s)",
            commonUsage = "Multiple call buttons for elevators, multiple levers opening the same iron door."
        ),
        LogicGateInfo(
            name = "NOT Gate (Inverter)",
            expression = "Q = NOT A",
            description = "Inverts the input signal (turns ON when OFF, and OFF when ON).",
            truthTable = listOf(
                "A=0" to "Q=1",
                "A=1" to "Q=0"
            ),
            bedrockComponentList = "1 Solid block with a Redstone Torch mounted on the side or top.",
            delayTicks = "1 Redstone Tick (0.1s)",
            commonUsage = "Day/Night sensors, inverting button pulses for holding doors closed."
        ),
        LogicGateInfo(
            name = "XOR Gate (Exclusive OR)",
            expression = "Q = A XOR B",
            description = "Outputs TRUE if inputs A and B are DIFFERENT (one is ON, one is OFF).",
            truthTable = listOf(
                "A=0, B=0" to "Q=0",
                "A=1, B=0" to "Q=1",
                "A=0, B=1" to "Q=1",
                "A=1, B=1" to "Q=0"
            ),
            bedrockComponentList = "Bedrock compact: 1 Comparator in subtraction mode flanked by 2 locked repeaters or 3-torch layout.",
            delayTicks = "2 Redstone Ticks (0.2s)",
            commonUsage = "Two-way hallway light switches, Binary Adders (Half-Adder and Full-Adder sum bit)."
        ),
        LogicGateInfo(
            name = "RS-NOR Latch (1-Bit Memory Cell)",
            expression = "Set / Reset Memory",
            description = "Stores a 1-bit state: 'Set' turns output ON permanently until 'Reset' is pulsed.",
            truthTable = listOf(
                "Set Pulse" to "State becomes ON (1)",
                "Reset Pulse" to "State becomes OFF (0)",
                "No Input" to "Remains in previous state"
            ),
            bedrockComponentList = "2 Redstone Torches powering each other's mounting block crosswise with redstone dust lines.",
            delayTicks = "1 Redstone Tick (0.1s)",
            commonUsage = "Minecart station loading/unloading memories, combo lock stages, alarm latching."
        ),
        LogicGateInfo(
            name = "T-Flip-Flop (Bedrock Edition)",
            expression = "Toggle on Pulse",
            description = "Converts a momentary button press into a persistent ON/OFF toggle switch.",
            truthTable = listOf(
                "Button Pulse 1" to "Output toggles ON",
                "Button Pulse 2" to "Output toggles OFF"
            ),
            bedrockComponentList = "1 Dropper facing into 1 Hopper pointing back into the Dropper, 1 Comparator reading the Hopper.",
            delayTicks = "2 Redstone Ticks (0.2s)",
            commonUsage = "Single button iron doors, toggleable auto-smelters, light switches."
        )
    )
}
