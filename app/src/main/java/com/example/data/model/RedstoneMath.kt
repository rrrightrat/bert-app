package com.example.data.model

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

enum class ContainerType(
    val displayName: String,
    val slots: Int,
    val isItemContainer: Boolean = true,
    val description: String = ""
) {
    HOPPER("Hopper", 5, true, "5 slots, 320 max weight capacity. Core of item sorters & clocks."),
    DROPPER_DISPENSER("Dropper / Dispenser", 9, true, "9 slots, 576 max weight capacity. Common for pulse generators."),
    BARREL("Barrel / Shulker Box", 27, true, "27 slots, 1,728 max weight capacity. Standard compact storage."),
    CHEST("Single Chest", 27, true, "27 slots, 1,728 max weight capacity."),
    DOUBLE_CHEST("Double Chest", 54, true, "54 slots, 3,456 max weight capacity. Large storage buffer."),
    FURNACE("Furnace / Smoker / Blast Furnace", 3, true, "3 slots (Input, Fuel, Output), 192 max weight capacity."),
    BREWING_STAND("Brewing Stand", 5, true, "5 slots (3 potions, 1 ingredient, 1 blaze powder), 320 max capacity."),
    CRAFTER("Crafter", 9, false, "Outputs signal strength equal to the number of disabled/toggled slots (0 to 9)."),
    LECTERN("Lectern", 0, false, "Outputs signal based on current open page: floor((page - 1) / (totalPages - 1) * 14) + 1."),
    CHISELED_BOOKSHELF("Chiseled Bookshelf", 6, false, "Outputs signal strength equal to the last interacted book slot (1 to 6)."),
    COMPOSTER("Composter", 8, false, "Outputs signal strength equal to compost fill level (0 to 8). Level 8 is ready for harvest."),
    CAULDRON("Cauldron", 3, false, "Outputs signal strength equal to water/powder snow level (0 to 3). Level 3 is full."),
    JUKEBOX("Jukebox", 15, false, "Outputs fixed signal strength 1 to 15 depending on the music disc played."),
    RESPAWN_ANCHOR("Respawn Anchor", 4, false, "Outputs signal strength equal to glowstone charges (0 to 4)."),
    CAKE("Cake", 7, false, "Outputs signal strength 2 * remaining slices (14 when full, down to 2 with 1 slice left)."),
    SCULK_SENSOR("Sculk Sensor", 15, false, "Outputs signal strength 1 to 15 according to vibration frequency.")
}

data class ComparatorCalculationResult(
    val signalStrength: Int,
    val fullnessPercentage: Double,
    val totalWeight: Int,
    val maxWeight: Int,
    val itemsUntilNextSignal: Int?,
    val formulaExplanation: String
)

data class TargetSignalBreakdown(
    val targetSignal: Int,
    val container: ContainerType,
    val minWeightNeeded: Int,
    val maxWeightForSignal: Int,
    val asStack64: Int,
    val asStack16: Int,
    val asNonStackable: Int,
    val slotSuggestion: String
)

object RedstoneMath {

    /**
     * Calculates signal strength for standard item containers.
     * sumWeight: items64 * 1 + items16 * 4 + items1 * 64
     */
    fun calculateItemContainerSignal(
        container: ContainerType,
        items64: Int = 0,
        items16: Int = 0,
        nonStackables: Int = 0
    ): ComparatorCalculationResult {
        val totalCapacityWeight = container.slots * 64
        val totalWeight = (items64 * 1) + (items16 * 4) + (nonStackables * 64)

        if (totalWeight <= 0) {
            val nextWeight = 1
            return ComparatorCalculationResult(
                signalStrength = 0,
                fullnessPercentage = 0.0,
                totalWeight = 0,
                maxWeight = totalCapacityWeight,
                itemsUntilNextSignal = nextWeight,
                formulaExplanation = "Empty container = Signal Strength 0"
            )
        }

        val clampedWeight = min(totalWeight, totalCapacityWeight)
        val fullness = clampedWeight.toDouble() / totalCapacityWeight.toDouble()
        val rawSignal = 1.0 + floor(fullness * 14.0)
        val signalStrength = min(15, max(1, rawSignal.toInt()))

        // Items until next signal
        val itemsUntilNextSignal = if (signalStrength >= 15) {
            null
        } else {
            val nextTargetSignal = signalStrength + 1
            val minWeightForNext = ceil(((nextTargetSignal - 1).toDouble() / 14.0) * totalCapacityWeight).toInt()
            max(1, minWeightForNext - totalWeight)
        }

        val explanation = if (signalStrength == 15 && totalWeight >= totalCapacityWeight) {
            "100% full ($totalWeight/$totalCapacityWeight weight) = Max Signal Strength 15"
        } else {
            "floor(1 + ($totalWeight / $totalCapacityWeight) * 14) = floor(1 + ${(fullness * 14.0).format(3)}) = $signalStrength"
        }

        return ComparatorCalculationResult(
            signalStrength = signalStrength,
            fullnessPercentage = (fullness * 100.0),
            totalWeight = totalWeight,
            maxWeight = totalCapacityWeight,
            itemsUntilNextSignal = itemsUntilNextSignal,
            formulaExplanation = explanation
        )
    }

    /**
     * Calculates reverse requirements to produce an exact target signal strength.
     */
    fun calculateTargetSignalRequirements(
        container: ContainerType,
        targetSignal: Int
    ): TargetSignalBreakdown {
        val clampedSignal = targetSignal.coerceIn(0, 15)
        val totalCapacityWeight = container.slots * 64

        if (clampedSignal == 0) {
            return TargetSignalBreakdown(
                targetSignal = 0,
                container = container,
                minWeightNeeded = 0,
                maxWeightForSignal = 0,
                asStack64 = 0,
                asStack16 = 0,
                asNonStackable = 0,
                slotSuggestion = "Empty container (0 items in all ${container.slots} slots)"
            )
        }

        val minWeight = if (clampedSignal == 1) {
            1
        } else {
            ceil(((clampedSignal - 1).toDouble() / 14.0) * totalCapacityWeight).toInt()
        }

        val maxWeight = if (clampedSignal == 15) {
            totalCapacityWeight
        } else {
            val raw = (clampedSignal.toDouble() / 14.0) * totalCapacityWeight
            floor(raw - 0.0000001).toInt()
        }

        val items64 = minWeight
        val items16 = ceil(minWeight / 4.0).toInt()
        val nonStackableCount = minWeight / 64
        val remainder64 = minWeight % 64

        // Build helpful slot distribution suggestion
        val suggestion = when (container) {
            ContainerType.HOPPER -> {
                when (targetSignal) {
                    1 -> "1 item in any slot (e.g. 1 / 0 / 0 / 0 / 0)"
                    2 -> "23 items of 64-stack (e.g. 19 in slot 1, 1 in each other slot)"
                    3 -> "46 items of 64-stack (Active impulseSV sorter unblock threshold)"
                    4 -> "69 items of 64-stack (Warning: crosstalk spillover point!)"
                    else -> "$minWeight total weight across 5 slots (max 64 per slot)"
                }
            }
            ContainerType.BARREL, ContainerType.CHEST -> {
                val fullStacks = minWeight / 64
                val rem = minWeight % 64
                if (fullStacks > 0 && rem > 0) {
                    "$fullStacks full stacks (64) + 1 slot with $rem items"
                } else if (fullStacks > 0) {
                    "$fullStacks full stacks of 64 items"
                } else {
                    "$minWeight items in any single slot"
                }
            }
            else -> "$minWeight weight distributed across ${container.slots} slots"
        }

        return TargetSignalBreakdown(
            targetSignal = clampedSignal,
            container = container,
            minWeightNeeded = minWeight,
            maxWeightForSignal = maxWeight,
            asStack64 = items64,
            asStack16 = items16,
            asNonStackable = nonStackableCount,
            slotSuggestion = suggestion
        )
    }

    /**
     * Lectern calculation:
     * totalPages: total pages in written book (1 to 100)
     * currentPage: page currently open (1 to totalPages)
     */
    fun calculateLecternSignal(currentPage: Int, totalPages: Int): Int {
        if (totalPages <= 1) return 15
        val page = currentPage.coerceIn(1, totalPages)
        val signal = floor(((page - 1).toDouble() / (totalPages - 1).toDouble()) * 14.0) + 1.0
        return signal.toInt().coerceIn(1, 15)
    }

    /**
     * Etho Hopper Clock Calculator.
     * 1 item transfers every 8 game ticks = 4 redstone ticks = 0.4 seconds.
     * Full cycle period = 2 * (items * 8 gt) = items * 16 gt = items * 0.8 seconds.
     */
    data class EthoClockResult(
        val items: Int,
        val oneWayGameTicks: Long,
        val oneWayRedstoneTicks: Long,
        val oneWaySeconds: Double,
        val fullCycleGameTicks: Long,
        val fullCycleRedstoneTicks: Long,
        val fullCycleSeconds: Double,
        val frequencyHz: Double
    )

    fun calculateEthoClockByItems(items: Int): EthoClockResult {
        val safeItems = max(1, items)
        val oneWayGt = safeItems.toLong() * 8L
        val oneWayRt = safeItems.toLong() * 4L
        val oneWaySec = safeItems * 0.4

        val fullCycleGt = oneWayGt * 2L
        val fullCycleRt = oneWayRt * 2L
        val fullCycleSec = oneWaySec * 2.0
        val freq = if (fullCycleSec > 0) 1.0 / fullCycleSec else 0.0

        return EthoClockResult(
            items = safeItems,
            oneWayGameTicks = oneWayGt,
            oneWayRedstoneTicks = oneWayRt,
            oneWaySeconds = oneWaySec,
            fullCycleGameTicks = fullCycleGt,
            fullCycleRedstoneTicks = fullCycleRt,
            fullCycleSeconds = fullCycleSec,
            frequencyHz = freq
        )
    }

    fun calculateEthoClockByTargetSeconds(targetSeconds: Double): EthoClockResult {
        val clampedSec = max(0.8, targetSeconds)
        // full cycle = items * 0.8 => items = round(targetSec / 0.8)
        val items = max(1, Math.round(clampedSec / 0.8).toInt())
        return calculateEthoClockByItems(items)
    }

    /**
     * Repeater delay chain optimizer.
     * Target delay in Redstone Ticks (1 rt = 2 gt = 0.1s).
     */
    data class RepeaterChainResult(
        val targetTicks: Int,
        val count4Tick: Int,
        val count3Tick: Int,
        val count2Tick: Int,
        val count1Tick: Int,
        val totalRepeaters: Int,
        val actualTicks: Int,
        val delaySeconds: Double
    )

    fun calculateRepeaterChain(targetTicks: Int): RepeaterChainResult {
        val ticks = max(1, targetTicks)
        val count4 = ticks / 4
        val remainder = ticks % 4

        var count3 = 0
        var count2 = 0
        var count1 = 0

        when (remainder) {
            3 -> count3 = 1
            2 -> count2 = 1
            1 -> count1 = 1
        }

        val totalRepeaters = count4 + count3 + count2 + count1
        val actualTicks = (count4 * 4) + (count3 * 3) + (count2 * 2) + (count1 * 1)
        val delaySec = actualTicks * 0.1

        return RepeaterChainResult(
            targetTicks = ticks,
            count4Tick = count4,
            count3Tick = count3,
            count2Tick = count2,
            count1Tick = count1,
            totalRepeaters = totalRepeaters,
            actualTicks = actualTicks,
            delaySeconds = delaySec
        )
    }

    /**
     * Comparator Fader / Pulse Extender.
     * In a 2-comparator loop with dust length L:
     * Signal decays by 1 every (loop travel delay = 2 rt + dust transmission).
     */
    data class ComparatorFaderResult(
        val initialSignal: Int,
        val wireLengthPerSide: Int,
        val decaySteps: Int,
        val totalRedstoneTicks: Int,
        val totalSeconds: Double
    )

    fun calculateComparatorFader(
        initialSignal: Int = 15,
        wireLengthPerSide: Int = 1
    ): ComparatorFaderResult {
        val sig = initialSignal.coerceIn(1, 15)
        val wire = wireLengthPerSide.coerceIn(1, 14)
        // Each loop through the two comparators takes 2 redstone ticks (1 tick per comparator)
        val stepRt = 2
        val decaySteps = sig
        val totalRt = decaySteps * stepRt
        val totalSec = totalRt * 0.1

        return ComparatorFaderResult(
            initialSignal = sig,
            wireLengthPerSide = wire,
            decaySteps = decaySteps,
            totalRedstoneTicks = totalRt,
            totalSeconds = totalSec
        )
    }

    /**
     * TNT Booster / Ballistics model for Bedrock contraptions.
     */
    data class TNTBallisticsResult(
        val tntCount: Int,
        val entityType: String,
        val estimatedHorizontalVelocity: Double,
        val estimatedVerticalVelocity: Double,
        val estimatedDistanceBlocks: Double,
        val recommendedPayloadDelayTicks: Int,
        val bedrockNote: String
    )

    fun calculateTNTLaunch(tntCount: Int, entityType: String): TNTBallisticsResult {
        val count = tntCount.coerceIn(1, 256)
        val massFactor = when (entityType.lowercase()) {
            "boat" -> 0.85 // Boats glide very well with low drag on ice/water
            "player" -> 1.0
            "tnt" -> 1.1 // Prime TNT payload
            "trident" -> 1.4 // Thrown projectiles
            else -> 1.0
        }

        // Each TNT delivers ~0.8 to 1.2 velocity units depending on water compression and alignment
        val baseSpeed = count * 1.05 * massFactor
        val estDistance = baseSpeed * 3.8
        val vSpeed = if (entityType.equals("tnt", true)) baseSpeed * 0.45 else baseSpeed * 0.65

        // Prime TNT fuse is 80 gt (40 rt)
        // Delay payload so propellant explodes at tick 78-79
        val recDelayRt = max(2, 40 - (count / 8).coerceAtMost(36))

        val note = if (count > 32) {
            "Large TNT cluster: Use waterlogged slabs or obsidian chamber to prevent explosion griefing in Bedrock."
        } else {
            "Standard booster: Water flow prevents terrain damage while imparting 100% blast vector."
        }

        return TNTBallisticsResult(
            tntCount = count,
            entityType = entityType,
            estimatedHorizontalVelocity = baseSpeed,
            estimatedVerticalVelocity = vSpeed,
            estimatedDistanceBlocks = estDistance,
            recommendedPayloadDelayTicks = recDelayRt,
            bedrockNote = note
        )
    }

    private fun Double.format(digits: Int) = String.format("%.${digits}f", this)
}
