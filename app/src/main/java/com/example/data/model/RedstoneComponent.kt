package com.example.data.model

enum class ComponentCategory(val displayName: String) {
    ALL("All Components"),
    POWER("Power Sources"),
    TRANSMISSION("Transmission & Logic"),
    SENSORS("Sensors & Detectors"),
    ACTUATORS("Actuators & Movers"),
    STORAGE("Containers & Storage")
}

data class RedstoneComponent(
    val id: String,
    val name: String,
    val category: ComponentCategory,
    val signalOutput: String,
    val delay: String,
    val movableByPiston: Boolean,
    val hardPowerable: Boolean,
    val transparentBlock: Boolean = false,
    val description: String,
    val bedrockQuirks: String,
    val javaDifferences: String,
    val proTips: String,
    val propertiesList: List<Pair<String, String>>
)

object RedstoneComponentDatabase {
    val ALL_COMPONENTS: List<RedstoneComponent> = listOf(
        // POWER SOURCES
        RedstoneComponent(
            id = "redstone_torch",
            name = "Redstone Torch",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 (Constant when unpowered)",
            delay = "1 Redstone Tick (2 Game Ticks / 0.1s)",
            movableByPiston = false,
            hardPowerable = false,
            description = "Fundamental power emitter and NOT gate inverter. Strongly powers the block directly above it and weakly powers adjacent redstone dust (except the block it is attached to).",
            bedrockQuirks = "In Bedrock, redstone torches burn out when toggled more than 8 times in 60 game ticks (3 seconds). When burned out, it emits smoke particles and drops offline for a random cooldown period. Attaching to moving blocks breaks the torch.",
            javaDifferences = "Same 1-tick delay as Java, but tick priority in torch towers may resolve with slight tick ordering variance in Bedrock due to random update order in simultaneous ticks.",
            proTips = "Stack torches alternating on blocks for vertical signal transmission. Use vertical torch towers for instant downward or 1-tick upward transmission.",
            propertiesList = listOf(
                "Max Signal" to "15",
                "Delay" to "1 redstone tick (0.1s)",
                "Soft Power" to "Adjacent dust & blocks",
                "Hard Power" to "Block directly above",
                "Burnout Threshold" to ">8 pulses in 60 ticks"
            )
        ),
        RedstoneComponent(
            id = "redstone_block",
            name = "Block of Redstone",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 (Permanent)",
            delay = "Instant (0 ticks)",
            movableByPiston = true,
            hardPowerable = false,
            description = "Permanent, solid power source block. Emits signal 15 to all 6 adjacent sides. Strongly powers adjacent redstone dust but cannot power through solid opaque blocks.",
            bedrockQuirks = "Crucial Bedrock feature: Block of Redstone is fully movable by pistons and sticky pistons! This allows you to construct mobile T-flip-flops, flying machine engines, and shift registers that are impossible with non-movable equivalents.",
            javaDifferences = "Movable in both editions, but in Bedrock it interacts with movable tile entities (chests, furnaces, hoppers) enabling compact piston-fed storage power.",
            proTips = "Place on top of a piston arm to activate components upon piston extension without soft-powering adjacent opaque blocks.",
            propertiesList = listOf(
                "Max Signal" to "15 (Permanent)",
                "Movable by Piston" to "Yes (Piston & Sticky Piston)",
                "Powers Dust" to "Yes (All 6 directions)",
                "Powers Through Block" to "No (Does not hard-power adjacent blocks)"
            )
        ),
        RedstoneComponent(
            id = "lever",
            name = "Lever",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 (Toggled On/Off)",
            delay = "Instant (0 ticks)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Manual toggle switch. When turned ON, strongly powers the solid opaque block it is mounted on and weakly powers the other 5 adjacent spaces with signal 15.",
            bedrockQuirks = "Can be placed on top, bottom, or sides of opaque blocks, upside-down stairs, and top slabs. Cannot be placed on glass, leaves, or hoppers.",
            javaDifferences = "Functionally identical to Java. Provides reliable manual input for clock toggles and master power switches.",
            proTips = "Mount on an iron door block's supporting block to cleanly power the door from both sides.",
            propertiesList = listOf(
                "Max Signal" to "15",
                "Mounting" to "Top, bottom, or sides of solid blocks",
                "Hard Powers" to "Mounted block",
                "State" to "Bistable (Toggle ON / Toggle OFF)"
            )
        ),
        RedstoneComponent(
            id = "stone_button",
            name = "Stone Button",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 for 1.0s (10 Redstone Ticks)",
            delay = "Instant (0 ticks)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Short-duration pulse generator. Generates a pulse of signal 15 lasting exactly 10 redstone ticks (20 game ticks / 1.0 second).",
            bedrockQuirks = "Only players and mobs can press a stone button. Arrows and tridents do NOT activate stone buttons (unlike wooden buttons).",
            javaDifferences = "Exact 1.0-second pulse duration matches Java. Stone buttons prevent accidental triggering from stray skeleton arrows in farms.",
            proTips = "Ideal trigger for item dispensers, droppers, and elevator callers where a clean 1-second pulse is needed without jamming.",
            propertiesList = listOf(
                "Max Signal" to "15",
                "Pulse Length" to "10 redstone ticks (1.0 second)",
                "Triggered by Projectiles" to "No (Player/Mob click only)",
                "Hard Powers" to "Mounted block"
            )
        ),
        RedstoneComponent(
            id = "wooden_button",
            name = "Wooden Button (All Wood Types)",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 for 1.5s (15 Redstone Ticks)",
            delay = "Instant (0 ticks)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Longer-duration pulse generator. Generates a pulse of signal 15 lasting exactly 15 redstone ticks (30 game ticks / 1.5 seconds). Can be pressed by hand or shot with projectiles.",
            bedrockQuirks = "Arrows and tridents shot into a wooden button will keep it active until the arrow despawns (60 seconds) or is collected.",
            javaDifferences = "Same duration as Java (1.5s vs stone's 1.0s).",
            proTips = "Use for long-pulse piston door inputs so players have ample time to walk through without getting clipped by closing blocks.",
            propertiesList = listOf(
                "Max Signal" to "15",
                "Pulse Length" to "15 redstone ticks (1.5 seconds)",
                "Triggered by Projectiles" to "Yes (Arrows, tridents)",
                "Variants" to "Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, Bamboo, Crimson, Warped"
            )
        ),
        RedstoneComponent(
            id = "pressure_plates",
            name = "Pressure Plates (Stone & Wood)",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 while stepped on + 10 redstone ticks cooldown",
            delay = "Instant (0 ticks)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Walkover floor trigger. Strongly powers the block underneath it and weakly powers adjacent redstone dust.",
            bedrockQuirks = "Stone Pressure Plates detect players and mobs only. Wooden Pressure Plates detect players, mobs, dropped items, arrows, and tridents.",
            javaDifferences = "In Bedrock, entity hitbox detection on pressure plate boundaries aligns with Bedrock's physics tick rate.",
            proTips = "Use wooden pressure plates inside mob grinders to detect dropped mob loot or item overflows.",
            propertiesList = listOf(
                "Stone Plate" to "Players & Mobs only",
                "Wood Plate" to "Players, Mobs, Items, Arrows, Tridents",
                "Hard Powers" to "Block directly below",
                "Cooldown" to "10 redstone ticks (1.0s)"
            )
        ),
        RedstoneComponent(
            id = "weighted_pressure_plates",
            name = "Weighted Pressure Plates (Gold & Iron)",
            category = ComponentCategory.POWER,
            signalOutput = "Analog Signal 1 to 15 based on entity count",
            delay = "Instant (0 ticks)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Entity count meter. Light (Gold) measures entity count linearly (1 entity = signal 1, up to 60 entities = signal 15). Heavy (Iron) measures bulk entities (1-10 entities = signal 1, up to 150 entities = signal 15).",
            bedrockQuirks = "Each individual dropped item in an unmerged stack counts as a separate entity until merged by Bedrock's item-grouping tick.",
            javaDifferences = "Exact mathematical scaling: Light = ceil(Entities / 4) with 1 entity = 1. Heavy = ceil(Entities / 10).",
            proTips = "Gold weighted plates make great item counter triggers for shop payment sorters or multi-item batchers.",
            propertiesList = listOf(
                "Light (Gold)" to "1 signal per entity (max 60 entities = 15)",
                "Heavy (Iron)" to "1 signal per 10 entities (max 150 entities = 15)",
                "Max Signal" to "15",
                "Entities Detected" to "Players, mobs, dropped items, arrows"
            )
        ),
        RedstoneComponent(
            id = "daylight_sensor",
            name = "Daylight Sensor & Inverted Daylight Sensor",
            category = ComponentCategory.POWER,
            signalOutput = "Analog Signal 0 to 15 based on celestial angle",
            delay = "Periodic (Updates on game tick schedule)",
            movableByPiston = false,
            hardPowerable = false,
            description = "Solar panel clock. Emits signal 0 to 15 depending on ambient sunlight intensity. Right-clicking inverts it into a Moonlight / Night Detector.",
            bedrockQuirks = "Requires direct skylight or transparent blocks above. Rain and thunderstorms reduce daylight output by 3 signal levels. Inverted mode outputs maximum signal (15) at midnight (tick 18000).",
            javaDifferences = "Bedrock's daylight sensor calculates light levels identically, but can be read through waterlogged states differently.",
            proTips = "Use as a universal real-time game clock to reset daily villager trading halls or auto-fire morning farm harvests.",
            propertiesList = listOf(
                "Signal Range" to "0 to 15",
                "Day Mode" to "Peaks at solar noon (tick 6000 = signal 15)",
                "Night Mode" to "Peaks at midnight (tick 18000 = signal 15)",
                "Weather Effect" to "-3 signal strength in rain/storm"
            )
        ),
        RedstoneComponent(
            id = "target_block",
            name = "Target Block",
            category = ComponentCategory.POWER,
            signalOutput = "Analog Signal 1 to 15 based on projectile accuracy",
            delay = "Instant on projectile impact",
            movableByPiston = true,
            hardPowerable = true,
            description = "Projectile hit detector and universal redstone dust redirector. Emits signal 1 (outer rim) to 15 (bullseye) when hit by arrows, tridents, snowballs, eggs, or fire charges.",
            bedrockQuirks = "GAME CHANGER in Bedrock: Target blocks redirect all adjacent redstone dust directly into their faces without requiring repeaters! Furthermore, Target Blocks are fully movable by pistons in Bedrock, allowing mobile dust redirection circuits.",
            javaDifferences = "In Java target blocks are also solid, but in Bedrock their combination with movable tile entities makes them essential for high-density compact wiring.",
            proTips = "Use target blocks as solid redirection blocks in place of repeaters to save space, reduce lag, and eliminate 1-tick repeater delays.",
            propertiesList = listOf(
                "Bullseye Signal" to "15 (Outer rim = 1)",
                "Pulse Length" to "8 redstone ticks (arrows) / 4 ticks (snowballs)",
                "Dust Redirection" to "Redirects redstone dust to face directly into it",
                "Movable by Piston" to "Yes (Bedrock Edition)"
            )
        ),
        RedstoneComponent(
            id = "tripwire_hook",
            name = "Tripwire Hook & String",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 while tripped",
            delay = "Instant (0 ticks)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Invisible line sensor. Two tripwire hooks connected by up to 40 blocks of string. Emits signal 15 when any player, mob, item, boat, or minecart enters the string.",
            bedrockQuirks = "Can be safely disarmed by cutting with Shears. In Bedrock, breaking with hands or water instantly triggers the signal.",
            javaDifferences = "Functions identically. Crucial component for mob trap triggers, string duplication prevention, and perimeter alarms.",
            proTips = "Hook onto an iron trapdoor or piston drop to create instant undetectable pitfall traps.",
            propertiesList = listOf(
                "Max Span" to "40 blocks of string between hooks",
                "Output Signal" to "15",
                "Safe Disarm" to "Shears only",
                "Hard Powers" to "Supporting block"
            )
        ),
        RedstoneComponent(
            id = "trapped_chest",
            name = "Trapped Chest",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 1 to 15 (1 signal per player viewing inventory)",
            delay = "Instant (0 ticks)",
            movableByPiston = true,
            hardPowerable = true,
            description = "Disguised container trigger. Emits signal strength equal to the number of players currently opening its interface (up to 15 players). Weakly powers adjacent blocks and strongly powers the block underneath.",
            bedrockQuirks = "Movable by pistons in Bedrock! Unlike Java where chests cannot be moved by pistons, Bedrock allows full piston movement of trapped chests with their contents intact.",
            javaDifferences = "In Java, chests are tile entities that block piston movement. In Bedrock, you can build moving secret vaults and mobile armories.",
            proTips = "Place next to a normal chest without them connecting into a double chest. Ideal for hidden door triggers inside legitimate storage rooms.",
            propertiesList = listOf(
                "Max Signal" to "15 (1 per player opening)",
                "Movable by Piston" to "Yes (Bedrock exclusive)",
                "Double Chest" to "Yes (Combines with trapped chests only)",
                "Visual Marker" to "Slight reddish tint around latch"
            )
        ),
        RedstoneComponent(
            id = "lightning_rod",
            name = "Lightning Rod",
            category = ComponentCategory.POWER,
            signalOutput = "Signal 15 for 8 Redstone Ticks (16 Game Ticks / 0.8s)",
            delay = "Instant on strike",
            movableByPiston = true,
            hardPowerable = true,
            description = "Lightning attractor and power generator. Emits signal 15 when struck by natural lightning or when hit by a Channeling trident during a thunderstorm.",
            bedrockQuirks = "Can be waterlogged. Attracts lightning within a 64x64x64 block bounding box around the rod in Bedrock.",
            javaDifferences = "Channeling trident strike reliably powers the rod in both editions. Fully pushable by pistons in Bedrock.",
            proTips = "Combine with an R.S. NOR latch to create an automatic storm detector or supercharged creeper generator.",
            propertiesList = listOf(
                "Output Signal" to "15",
                "Duration" to "8 redstone ticks (0.8s)",
                "Attract Range" to "64 blocks radius",
                "Channeling Trident" to "Powers on hit during thunderstorm"
            )
        ),

        // TRANSMISSION & LOGIC
        RedstoneComponent(
            id = "redstone_dust",
            name = "Redstone Dust",
            category = ComponentCategory.TRANSMISSION,
            signalOutput = "Signal 1 to 15 (Decays 1 level per block)",
            delay = "Instant (0 ticks, but updates within tick)",
            movableByPiston = false,
            hardPowerable = false,
            description = "The wire of Minecraft. Transmits analog signals up to 15 blocks before reaching 0. Transmits up and down single-block steps over solid or transparent blocks.",
            bedrockQuirks = "RANDOM UPDATE ORDER (CRITICAL MCBE MECHANIC): When redstone dust branches in multiple directions and activates in the same game tick, Bedrock does NOT use deterministic directional coordinates (like Java's East-West priority). Instead, the update order is non-deterministic (random)! Also: Redstone dust redirects towards adjacent pistons and droppers automatically. Soft inversion allows dust on top of a block to power through transparent stairs.",
            javaDifferences = "In Java, redstone update order is strictly deterministic (West-East, North-South). In Bedrock, update order is pseudo-random within the tick, which causes simultaneous piston races to behave unpredictably unless separated by repeaters.",
            proTips = "Always add a 1-tick repeater on timing-critical branches in Bedrock to enforce strict order of execution and eliminate random update races.",
            propertiesList = listOf(
                "Max Signal" to "15",
                "Transmission Range" to "15 blocks without repeater",
                "Signal Decay" to "-1 per block",
                "Update Order" to "Non-deterministic (Random in Bedrock)",
                "Dust Redirection" to "Redirects towards pistons & droppers automatically"
            )
        ),
        RedstoneComponent(
            id = "redstone_repeater",
            name = "Redstone Repeater",
            category = ComponentCategory.TRANSMISSION,
            signalOutput = "Signal 15 (Refreshes signal to full)",
            delay = "Configurable: 1, 2, 3, or 4 Redstone Ticks (2, 4, 6, 8 Game Ticks = 0.1s to 0.4s)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Diode, signal booster, and precision clock delay. Transmits signals in only one direction. Locks its current state into a memory cell (D-latch) when powered from the side by another repeater or comparator.",
            bedrockQuirks = "Does not exhibit Java's 0-tick micro-tick bugs. A 1-tick repeater strictly enforces 2 game ticks (0.10s) delay in Bedrock. When locked by a side repeater, a bedrock bedrock-bar appears, freezing its output.",
            javaDifferences = "Java allows 0-tick pulses to force instant piston updates. Bedrock strictly obeys the game tick schedule (1 redstone tick = 2 game ticks = 0.1s).",
            proTips = "Use repeater locking for compact 1-bit memory latches, shift registers, and combination locks without bulky torch gates.",
            propertiesList = listOf(
                "Delays" to "1 tick (0.1s), 2 ticks (0.2s), 3 ticks (0.3s), 4 ticks (0.4s)",
                "Output Signal" to "15",
                "Diode" to "One-way only",
                "Repeater Locking" to "Locks when side-powered by repeater/comparator",
                "Hard Powers" to "Block directly in front"
            )
        ),
        RedstoneComponent(
            id = "redstone_comparator",
            name = "Redstone Comparator",
            category = ComponentCategory.TRANSMISSION,
            signalOutput = "Analog Signal 0 to 15 (Based on container fullness or side math)",
            delay = "1 Redstone Tick (2 Game Ticks / 0.1s)",
            movableByPiston = false,
            hardPowerable = true,
            description = "Container fullness reader and analog math unit. Has 2 modes: Comparison Mode (front torch OFF) and Subtraction Mode (front torch ON). Reads fullness of chests, hoppers, barrels, cauldrons, composters, crafters, and lecterns directly or through 1 solid block.",
            bedrockQuirks = "Comparison Mode: If rear signal A >= side signal B, outputs A; otherwise outputs 0. Subtraction Mode: Outputs max(0, A - B). In Bedrock, creating a loopback with subtraction mode produces a rapid pulse fader clock that decays smoothly.",
            javaDifferences = "Formula for containers in Bedrock: floor((Items / MaxItems) * 14) + 1 if Items > 0, else 0. Identical math to Java.",
            proTips = "Use Subtraction mode with side signal 1 to create an instant signal attenuator, or loop the output back into the side for a 2-tick pulse fader.",
            propertiesList = listOf(
                "Delay" to "1 redstone tick (0.1s)",
                "Comparison Mode" to "Outputs A if A >= max(B1, B2), else 0",
                "Subtraction Mode" to "Outputs max(0, A - max(B1, B2))",
                "Container Reading" to "Reads through up to 1 solid opaque block",
                "Analog Output" to "0 to 15"
            )
        ),
        RedstoneComponent(
            id = "copper_bulb",
            name = "Copper Bulb (Waxed & Unwaxed)",
            category = ComponentCategory.TRANSMISSION,
            signalOutput = "Signal 15 (When read by Comparator while LIT)",
            delay = "1 Redstone Tick in Bedrock (2 Game Ticks / 0.1s)",
            movableByPiston = true,
            hardPowerable = false,
            description = "Built-in 1-block T-Flip-Flop! Added in 1.21. Toggles between LIT and UNLIT on every rising edge redstone pulse. A comparator reading the back of a copper bulb outputs signal 15 when the bulb is lit.",
            bedrockQuirks = "CRITICAL BEDROCK PROPERTY: In Bedrock, the Copper Bulb has a 1-redstone-tick delay (2 game ticks). This makes it a clean, tick-synchronized 1-block bistable toggle switch. Can be scraped with an axe to revert oxidation.",
            javaDifferences = "In Java, the Copper Bulb had a 0-tick delay during early snapshots before being aligned. In Bedrock, the 1-tick delay creates perfect sync with 1-tick pulse generators.",
            proTips = "Replaces bulky 4-block dropper-hopper or piston T-flip-flops with a single 1x1 block! Wire a button into a copper bulb with a comparator on the back for instant single-block toggle doors.",
            propertiesList = listOf(
                "State" to "Bistable T-Flip-Flop (Toggles on rising edge)",
                "Comparator Output" to "15 when LIT, 0 when UNLIT",
                "Delay" to "1 redstone tick (0.1s)",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Light Level" to "15 (Unaffected) to 4 (Oxidized)"
            )
        ),

        // SENSORS & DETECTORS
        RedstoneComponent(
            id = "observer",
            name = "Observer",
            category = ComponentCategory.SENSORS,
            signalOutput = "Signal 15 for 1 Redstone Tick (2 Game Ticks / 0.1s)",
            delay = "1 Redstone Tick (2 Game Ticks / 0.1s)",
            movableByPiston = true,
            hardPowerable = true,
            description = "Block update detector. Detects state changes on the face side and emits a strong 1-tick pulse of signal 15 from the rear red dot.",
            bedrockQuirks = "In Bedrock, Observers detect movable tile entities (chests, furnaces, hoppers moving), piston extension and retraction stages, fire igniting/extinguishing, water flow, leaf decay, and redstone wire power changes. Two observers facing each other create an ultra-fast 2-game-tick clock.",
            javaDifferences = "In Java, observers generate a 1-game-tick pulse; in Bedrock, observer pulses are strictly 2 game ticks (1 redstone tick). Fully pushable by pistons in Bedrock.",
            proTips = "Place an observer facing a sugar cane, pumpkin, or melon stem to trigger an instant single-pulse harvester piston.",
            propertiesList = listOf(
                "Pulse Length" to "1 redstone tick (2 game ticks / 0.1s)",
                "Output Signal" to "15",
                "Hard Powers" to "Block directly facing the back dot",
                "Movable by Piston" to "Yes (Bedrock Edition)"
            )
        ),
        RedstoneComponent(
            id = "sculk_sensor",
            name = "Sculk Sensor",
            category = ComponentCategory.SENSORS,
            signalOutput = "Analog Signal 1 to 15 based on vibration frequency",
            delay = "1 Redstone Tick to activate, 30 Game Ticks (1.5s) cooldown",
            movableByPiston = true,
            hardPowerable = false,
            description = "Acoustic vibration detector. Detects vibrations within an 8-block radius and emits an analog signal strength from 1 to 15 corresponding to the exact vibration type.",
            bedrockQuirks = "Can be waterlogged to make it silent! Wool occludes vibrations (wool placed between vibration source and sensor blocks detection). Fully movable by pistons in Bedrock.",
            javaDifferences = "Vibration frequency table is identical across editions. In Bedrock, can be moved by pistons while active without breaking state.",
            proTips = "Wool blocks absorb vibrations completely. Build vibration tunnels out of wool to isolate detection to a specific door or chest.",
            propertiesList = listOf(
                "Detection Radius" to "8 blocks",
                "Cooldown" to "30 game ticks (1.5s)",
                "Waterloggable" to "Yes (Silent operation)",
                "Wool Occlusion" to "Blocks all vibrations through wool line"
            )
        ),
        RedstoneComponent(
            id = "calibrated_sculk_sensor",
            name = "Calibrated Sculk Sensor",
            category = ComponentCategory.SENSORS,
            signalOutput = "Signal 15 (When vibration matches side input frequency)",
            delay = "1 Redstone Tick to activate, 10 Game Ticks (0.5s) cooldown",
            movableByPiston = true,
            hardPowerable = false,
            description = "Frequency-filtered vibration sensor. Accepts a side input signal (1 to 15) from a comparator or lectern and ONLY triggers when a vibration matching that exact frequency occurs within 16 blocks.",
            bedrockQuirks = "Cooldown is only 10 game ticks (0.5 seconds), which is 3x faster than normal sculk sensors! Doubles detection radius to 16 blocks.",
            javaDifferences = "Frequency table: 1=Step, 2=Flap, 3=Swim, 4=Glide, 5=Hit, 6=Splash, 7=Eat, 8=Shoot, 9=Armor, 10=Block Place, 11=Block Break, 12=Open Container, 13=Close Container, 14=Explosion, 15=Mob Death.",
            proTips = "Set side signal to 12 (container open) to create an invisible wireless security alarm whenever someone opens a chest up to 16 blocks away.",
            propertiesList = listOf(
                "Detection Radius" to "16 blocks",
                "Cooldown" to "10 game ticks (0.5s - 3x faster!)",
                "Frequency Filtering" to "Filters for frequency matching side input signal (1-15)",
                "Waterloggable" to "Yes"
            )
        ),
        RedstoneComponent(
            id = "lectern",
            name = "Lectern",
            category = ComponentCategory.SENSORS,
            signalOutput = "Analog Signal 1 to 15 (Based on current open page)",
            delay = "Instant on page turn",
            movableByPiston = true,
            hardPowerable = false,
            description = "Book stand and precision analog signal generator. When read by a comparator, emits an analog signal strength from 1 to 15 depending on the current open page relative to total pages in the book.",
            bedrockQuirks = "Formula: signal = floor((CurrentPage - 1) / (TotalPages - 1) * 14) + 1. Using a 15-page book, Page 1 = Signal 1, Page 2 = Signal 2, ... Page 15 = Signal 15! In Bedrock, Lecterns are movable by pistons.",
            javaDifferences = "Page math is identical to Java. Because Bedrock allows piston movement of tile entities, Lecterns can be shifted mechanically into comparator read stations.",
            proTips = "Place a 15-page book on a lectern to create a 15-setting dial selector for combination locks, destination rail selectors, or potion brewing presets.",
            propertiesList = listOf(
                "Max Signal" to "15",
                "15-Page Book" to "Page 1..15 = Exact Signal 1..15",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Pulse on Turn" to "Emits 1-tick pulse when page turns"
            )
        ),
        RedstoneComponent(
            id = "chiseled_bookshelf",
            name = "Chiseled Bookshelf",
            category = ComponentCategory.SENSORS,
            signalOutput = "Analog Signal 1 to 6 (Based on last slot interacted with)",
            delay = "Instant on interaction",
            movableByPiston = true,
            hardPowerable = false,
            description = "Storage block for up to 6 books. When read by a comparator, outputs a signal strength equal to the slot index (1 to 6) of the most recently inserted or removed book.",
            bedrockQuirks = "Can be interacted with by hoppers and droppers. In Bedrock, it retains its comparator output until a different slot is touched, making it a 6-state rotary memory block.",
            javaDifferences = "Fully pushable by pistons in Bedrock Edition with books retained.",
            proTips = "Build secret door triggers where removing the book in slot 5 emits signal 5, opening a hidden passage behind the wall.",
            propertiesList = listOf(
                "Signal Range" to "1 to 6 (Last slot interacted)",
                "Slots" to "Top: 1, 2, 3 | Bottom: 4, 5, 6",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Hopper Compatible" to "Yes (Inserts & extracts)"
            )
        ),
        RedstoneComponent(
            id = "composter",
            name = "Composter",
            category = ComponentCategory.SENSORS,
            signalOutput = "Analog Signal 0 to 8 (Based on compost fill level)",
            delay = "Instant on compost stage change",
            movableByPiston = true,
            hardPowerable = false,
            description = "Organic recycler. When read by a comparator, outputs signal 0 (empty), 1-7 (stages of fill), and 8 (full with white bone meal ready to harvest).",
            bedrockQuirks = "Hoppers can feed compostable items into the top and extract bone meal from the bottom. In Bedrock, composters are transparent to mob spawning and pushable by pistons.",
            javaDifferences = "Identical comparator scaling (0 to 8). Commonly used in Bedrock item sorters as an unmovable hopper-locking block or lag-free comparator source.",
            proTips = "Place a composter filled to level 1 behind a comparator to provide a permanent, lag-free signal 1 reference without clocks.",
            propertiesList = listOf(
                "Signal Range" to "0 to 8",
                "Empty" to "0",
                "Level 1..7" to "1..7",
                "Full (Ready)" to "8",
                "Hopper Integration" to "Top feeds in, bottom extracts"
            )
        ),
        RedstoneComponent(
            id = "cauldron",
            name = "Cauldron (Water, Lava, Powder Snow & Potions)",
            category = ComponentCategory.SENSORS,
            signalOutput = "Analog Signal 1 to 3 (Based on liquid fill level)",
            delay = "Instant on level change",
            movableByPiston = true,
            hardPowerable = false,
            description = "Liquid container. When read by a comparator, outputs signal 1, 2, or 3 based on water, lava, or powder snow fill levels (1 bottle = 1 level).",
            bedrockQuirks = "EXCLUSIVE BEDROCK FEATURE: Cauldrons in Bedrock can hold POTIONS and DYED WATER! You can store potions inside cauldrons and tip arrows into potion arrows. A comparator reads potion levels (1, 2, or 3). Movable by pistons in Bedrock!",
            javaDifferences = "Java cauldrons cannot store potions or dyed water. In Bedrock, potion cauldrons make automated potion-tipping redstone factories possible.",
            proTips = "Level 1 cauldron behind a comparator provides a compact signal 1 power source for item sorter lock circuits.",
            propertiesList = listOf(
                "Signal Levels" to "Level 1 = Signal 1, Level 2 = Signal 2, Level 3 = Signal 3",
                "Bedrock Exclusive" to "Holds Potions & Dyed Water",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Tipping Arrows" to "Dip arrows into potion cauldron"
            )
        ),
        RedstoneComponent(
            id = "beehive",
            name = "Beehive & Bee Nest",
            category = ComponentCategory.SENSORS,
            signalOutput = "Analog Signal 0 to 5 (Based on honey level)",
            delay = "Instant on honey stage change",
            movableByPiston = false,
            hardPowerable = false,
            description = "Bee dwelling. When read by a comparator, outputs signal 0 to 5 corresponding to the current honey level.",
            bedrockQuirks = "At honey level 5, bees have completed honey production and honey is dripping. A dispenser with shears will harvest honeycombs; a dispenser with glass bottles will harvest honey bottles without angering bees.",
            javaDifferences = "Identical comparator scaling (0 to 5). Bee nests require Silk Touch to pick up with bees inside.",
            proTips = "Wire a comparator into a redstone wire of length 5 into a dispenser. When honey level reaches 5, the signal reaches the dispenser, auto-harvesting the honey!",
            propertiesList = listOf(
                "Honey Level 0..4" to "Signal 0..4",
                "Full Honey (Level 5)" to "Signal 5 (Triggers harvest)",
                "Dispenser Automation" to "Shears = Honeycombs | Glass Bottle = Honey Bottle",
                "Silk Touch" to "Retains bees on pickup"
            )
        ),

        // ACTUATORS & MOVERS
        RedstoneComponent(
            id = "piston",
            name = "Piston",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Mechanic / Actuator)",
            delay = "Extension: 2 Redstone Ticks (3 Game Ticks / 0.15s), Retraction: 2 Redstone Ticks",
            movableByPiston = false,
            hardPowerable = true,
            description = "Block pusher. Pushes up to 12 blocks in the direction facing when powered. Retracts when unpowered without pulling blocks back.",
            bedrockQuirks = "MOVABLE TILE ENTITIES (CRITICAL MCBE RULE): In Bedrock Edition, pistons can push tile entities! Chests, trapped chests, barrels, hoppers, droppers, dispensers, brewing stands, and furnaces can all be pushed by pistons! Also: NO QUASI-CONNECTIVITY in Bedrock. Pistons only activate when directly powered by redstone dust, adjacent powered block, or repeater.",
            javaDifferences = "In Java, tile entities (chests, hoppers, furnaces) are unmovable block entities. In Bedrock, they are fully movable! In Java, Quasi-Connectivity allows powering pistons 2 blocks above; Bedrock strictly does NOT have QC.",
            proTips = "Use Bedrock's movable tile entities to build moving storage vaults, revolving automated furnaces, and multi-chest item conveyors.",
            propertiesList = listOf(
                "Push Limit" to "12 blocks",
                "Extension Time" to "3 game ticks (0.15s)",
                "Push Tile Entities" to "YES (Chests, Hoppers, Furnaces in Bedrock!)",
                "Quasi-Connectivity" to "NONE (Bedrock strictly adheres to direct power)",
                "Slime/Honey" to "Adheres up to 12 adjacent blocks"
            )
        ),
        RedstoneComponent(
            id = "sticky_piston",
            name = "Sticky Piston",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Mechanic / Actuator)",
            delay = "Extension: 2 Redstone Ticks, Retraction: 2 Redstone Ticks",
            movableByPiston = false,
            hardPowerable = true,
            description = "Block pusher and puller. Pushes up to 12 blocks on extension and pulls the directly adjacent attached block back on retraction.",
            bedrockQuirks = "NO 1-TICK BLOCK SPITTING (CRITICAL MCBE DIFFERENCE): In Java, a 1-tick pulse causes a sticky piston to spit out its block and leave it behind. In Bedrock, a sticky piston ALWAYS pulls its block back, regardless of pulse duration! T-flip-flops in Bedrock cannot rely on Java block-spitting.",
            javaDifferences = "In Java, 1-tick pulses spit blocks for instant T-flip-flops. In Bedrock, sticky pistons never spit blocks; use Copper Bulbs or dropper-hopper latches instead. Pushes tile entities in Bedrock.",
            proTips = "For seamless piston double-extenders in Bedrock, time repeater delays: 1st piston = 0 ticks, 2nd piston = 2 ticks, retraction = 3 ticks and 1 tick.",
            propertiesList = listOf(
                "Push Limit" to "12 blocks",
                "Pull Limit" to "1 block attached to face",
                "1-Tick Spitting" to "NO (Always retains block in Bedrock)",
                "Push Tile Entities" to "YES (Chests, hoppers, furnaces)",
                "Quasi-Connectivity" to "NONE"
            )
        ),
        RedstoneComponent(
            id = "dropper",
            name = "Dropper",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Ejects items as entities or into containers)",
            delay = "Instant on rising edge pulse",
            movableByPiston = true,
            hardPowerable = true,
            description = "Item ejector. On every rising edge redstone pulse, ejects 1 random item from its 9-slot inventory. If facing an inventory (chest, hopper, dispenser), it deposits the item directly into that inventory without dropping it into the world.",
            bedrockQuirks = "Movable by pistons in Bedrock! No Quasi-Connectivity: Droppers never activate from blocks 2 spaces above or diagonally without wire. Droppers can be stacked vertically facing upward to create silent instant item elevators.",
            javaDifferences = "In Java, droppers suffer from Quasi-Connectivity, causing unintended activation from nearby wires. In Bedrock, droppers are strictly local and pushable by pistons.",
            proTips = "Stack droppers vertically facing into each other with an observer clock to create a 100% reliable, silent item waterless vertical elevator.",
            propertiesList = listOf(
                "Slots" to "9 items",
                "Action" to "Drops into world or deposits into container",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Quasi-Connectivity" to "None",
                "Vertical Stacking" to "Creates vertical item elevators"
            )
        ),
        RedstoneComponent(
            id = "dispenser",
            name = "Dispenser",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Uses or fires items)",
            delay = "Instant on rising edge pulse",
            movableByPiston = true,
            hardPowerable = true,
            description = "Mechanical item user. Fires arrows, tridents, fire charges, splash potions, and eggs; equips armor on players/mobs; shears sheep; places boats, minecarts, water/lava buckets, and bonemeals crops.",
            bedrockQuirks = "Movable by pistons in Bedrock! No Quasi-Connectivity. Can be moved into place by a piston to shear bees or arm defensive turrets.",
            javaDifferences = "In Java, dispensers cannot be moved by pistons and have Quasi-Connectivity. Bedrock dispensers can be moved on flying machines.",
            proTips = "Load with bone meal facing a crop or tree sapling to build automated nanoprocess tree/crop farms.",
            propertiesList = listOf(
                "Slots" to "9 items",
                "Projectiles" to "Shoots arrows, fire charges, potions, snowballs",
                "Tools" to "Shears sheep/beehives, places water/lava, equips armor",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Quasi-Connectivity" to "None"
            )
        ),
        RedstoneComponent(
            id = "crafter",
            name = "Crafter (1.21 Component)",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "Analog Signal 0 to 9 (Disabled slots + filled slots)",
            delay = "Instant on rising edge pulse",
            movableByPiston = true,
            hardPowerable = true,
            description = "Automated crafting block! Added in 1.21. On receiving a redstone pulse, consumes ingredients from its 3x3 grid and crafts the resulting recipe, ejecting it into the world or adjacent inventory.",
            bedrockQuirks = "Slots can be disabled by clicking them. When read by a comparator, output signal = (number of disabled slots + number of slots containing items), from 0 to 9! Movable by pistons in Bedrock!",
            javaDifferences = "Fully pushable by pistons in Bedrock Edition. Comparator math is identical (0-9).",
            proTips = "Disable unused slots to lock the crafting shape (e.g. disable 4 slots for iron boots). Wire a comparator reading signal 9 to trigger crafting when all required slots are full!",
            propertiesList = listOf(
                "Grid" to "3x3 crafting grid",
                "Slot Disabling" to "Click slots to toggle on/off",
                "Comparator Output" to "Signal 0 to 9 (Disabled slots + item-filled slots)",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Pulse Trigger" to "Crafts on rising edge pulse"
            )
        ),
        RedstoneComponent(
            id = "hopper",
            name = "Hopper",
            category = ComponentCategory.STORAGE,
            signalOutput = "None (Locks and halts transfer when powered)",
            delay = "Transfer Rate: 4 Redstone Ticks (8 Game Ticks / 0.4s per item)",
            movableByPiston = true,
            hardPowerable = false,
            description = "Item conveyor and filter conduit. Transfers 1 item every 4 redstone ticks (0.4 seconds). Pulls items from the container above and pushes items into the container it faces. When powered by redstone, it LOCKS, stopping all pulling and pushing.",
            bedrockQuirks = "MOVABLE BY PISTONS (BEDROCK EXCLUSIVE): In Bedrock, hoppers can be pushed by pistons while retaining all 5 slots of inventory! Locking mechanism: A powered hopper cannot push items out and cannot pull items from above, but an unpowered hopper below it CAN pull items out of a locked hopper.",
            javaDifferences = "In Java, hoppers are tile entities that cannot be moved by pistons. In Bedrock, hoppers are movable tile entities. Pull-priority over push is identical.",
            proTips = "For Etho Hopper Clocks, connect 2 hoppers facing into each other with a comparator reading each hopper into pistons that shift a redstone block.",
            propertiesList = listOf(
                "Slots" to "5 item slots",
                "Transfer Speed" to "0.4s (4 redstone ticks / 8 game ticks per item)",
                "Locking" to "Halts transfer when powered by any redstone signal",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Priority" to "Pull from above occurs before push into target"
            )
        ),
        RedstoneComponent(
            id = "redstone_lamp",
            name = "Redstone Lamp",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Emits light level 15)",
            delay = "Turns ON: Instant (0 ticks), Turns OFF: 2 Redstone Ticks (4 Game Ticks / 0.2s)",
            movableByPiston = true,
            hardPowerable = true,
            description = "Controllable electric light. Emits maximum light level 15 when powered. Solid block that can be strongly powered.",
            bedrockQuirks = "Movable by pistons in Bedrock! Has a 2-redstone-tick turn-off delay (0.2s), but turns ON instantly.",
            javaDifferences = "Pushable in Bedrock. Identical turn-off delay of 2 ticks.",
            proTips = "Use as visual indicator lamps on display boards, 7-segment displays, and logic gate outputs.",
            propertiesList = listOf(
                "Light Level" to "15 (When powered)",
                "Turn ON Delay" to "Instant (0 ticks)",
                "Turn OFF Delay" to "2 redstone ticks (0.2s)",
                "Movable by Piston" to "Yes (Bedrock Edition)",
                "Solid Block" to "Yes (Transmits strong power)"
            )
        ),
        RedstoneComponent(
            id = "note_block",
            name = "Note Block",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Plays musical acoustic notes)",
            delay = "Instant on rising edge pulse",
            movableByPiston = true,
            hardPowerable = true,
            description = "Musical instrument synthesizer. Plays 1 of 25 musical pitches (2 full octaves, F#3 to F#5) on receiving a redstone pulse. Pitch is tuned by right-clicking (0 to 24 semitones). Instrument depends on the block underneath.",
            bedrockQuirks = "Instruments in Bedrock: Wood=Double Bass, Sand/Gravel=Snare, Glass=Sticks, Stone=Bass Drum, Gold=Bells, Clay=Flute, Packed Ice=Chimes, Wool=Guitar, Bone=Xylophone, Iron=Iron Xylophone, Soul Sand=Cowbell, Pumpkin=Didgeridoo, Emerald=Bit, Hay=Banjo, Glowstone=Pling. Mob head placed on top plays mob ambient sound!",
            javaDifferences = "Pushable by pistons in Bedrock Edition with tuned pitch retained.",
            proTips = "Place a player/mob head on top of a note block in 1.20+ to play authentic Ender Dragon, Zombie, Skeleton, Creeper, or Piglin sounds!",
            propertiesList = listOf(
                "Pitch Range" to "0 to 24 semitones (2 full octaves)",
                "Instruments" to "16 distinct instruments determined by block below",
                "Mob Heads" to "Plays custom mob sounds when head is mounted on top",
                "Movable by Piston" to "Yes (Bedrock Edition)"
            )
        ),
        RedstoneComponent(
            id = "tnt",
            name = "TNT",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Explosive ordnance)",
            delay = "Fuse: 80 Game Ticks (40 Redstone Ticks / 4.0 Seconds)",
            movableByPiston = true,
            hardPowerable = false,
            description = "High-explosive charge. When ignited by redstone or fire, becomes an active primed TNT entity affected by gravity and explosion physics.",
            bedrockQuirks = "TNT in water deals 0 block damage in both editions. Underwater TNT (crafted with Sodium in Education/Bedrock mode) breaks blocks even submerged! TNT momentum vectors can be aligned with fence posts or flowing water.",
            javaDifferences = "TNT cannon momentum vectors calculate with Bedrock's entity collision math. Cannon payload dispensers must account for Bedrock's 80-game-tick fuse.",
            proTips = "For precision TNT cannons, trigger propellant 76 game ticks before the payload to launch the shell right as it explodes on contact.",
            propertiesList = listOf(
                "Fuse Time" to "80 game ticks (4.0 seconds)",
                "Blast Power" to "4 (Explosion radius ~7 blocks)",
                "Water Submersion" to "Deals entity damage but 0 block damage",
                "Movable by Piston" to "Yes (Before primed)"
            )
        ),
        RedstoneComponent(
            id = "iron_door_trapdoor",
            name = "Iron Door & Iron Trapdoor",
            category = ComponentCategory.ACTUATORS,
            signalOutput = "None (Mechanic barrier)",
            delay = "Instant (0 ticks)",
            movableByPiston = false,
            hardPowerable = false,
            description = "Security barriers. Unlike wooden variants, Iron Doors and Iron Trapdoors cannot be opened by right-clicking with hands; they strictly require a redstone signal (1 to 15) to open.",
            bedrockQuirks = "Stay open as long as a redstone signal is applied. In Bedrock, can be powered from the top, sides, or the solid block underneath.",
            javaDifferences = "Identical behavior. Highly blast resistant (blast resistance 25 vs wood's 15).",
            proTips = "Mount an inverted redstone torch underneath the floor block to create an automatic default-closed security vault door.",
            propertiesList = listOf(
                "Manual Opening" to "No (Redstone signal required)",
                "Activation Signal" to "Signal 1 to 15",
                "Blast Resistance" to "25 (Immune to ghast fireballs)",
                "Placement" to "Doors: 2 blocks high | Trapdoors: top or bottom slab half"
            )
        )
    )
}
