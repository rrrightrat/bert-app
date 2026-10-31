package com.example

import com.example.data.model.ContainerType
import com.example.data.model.RedstoneMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RedstoneMathTest {

    @Test
    fun hopperComparator_signalStrengthCalculations() {
        // 0 items -> SS 0
        val emptyResult = RedstoneMath.calculateItemContainerSignal(ContainerType.HOPPER, items64 = 0)
        assertEquals(0, emptyResult.signalStrength)
        assertEquals(0.0, emptyResult.fullnessPercentage, 0.01)

        // 1 item in hopper (1 / 320 weight) -> SS 1
        val oneItemResult = RedstoneMath.calculateItemContainerSignal(ContainerType.HOPPER, items64 = 1)
        assertEquals(1, oneItemResult.signalStrength)

        // 45 items (Standard ImpulseSV filter resting state) -> SS 2
        val restingFilter = RedstoneMath.calculateItemContainerSignal(ContainerType.HOPPER, items64 = 45)
        assertEquals(2, restingFilter.signalStrength)

        // 46 items (Filter item enters hopper -> unlocks lower hopper) -> SS 3
        val activeFilter = RedstoneMath.calculateItemContainerSignal(ContainerType.HOPPER, items64 = 46)
        assertEquals(3, activeFilter.signalStrength)

        // 69 items (Overflow crosstalk hazard!) -> SS 4
        val overflowFilter = RedstoneMath.calculateItemContainerSignal(ContainerType.HOPPER, items64 = 69)
        assertEquals(4, overflowFilter.signalStrength)

        // 320 items (Full 5 stacks) -> SS 15
        val fullHopper = RedstoneMath.calculateItemContainerSignal(ContainerType.HOPPER, items64 = 320)
        assertEquals(15, fullHopper.signalStrength)
    }

    @Test
    fun nonStackableItems_weightCalculation() {
        // 1 non-stackable item in a hopper gives 64 weight = 20% fullness -> SS 3
        val nonStackable = RedstoneMath.calculateItemContainerSignal(ContainerType.HOPPER, nonStackables = 1)
        assertEquals(3, nonStackable.signalStrength)
        assertEquals(20.0, nonStackable.fullnessPercentage, 0.1)
    }

    @Test
    fun targetSignalSolver_hopperThresholds() {
        // Solving for SS 3 on hopper should return 46 min items
        val solveSS3 = RedstoneMath.calculateTargetSignalRequirements(ContainerType.HOPPER, 3)
        assertEquals(46, solveSS3.minWeightNeeded)

        // Solving for SS 1 on hopper should return 1 min item
        val solveSS1 = RedstoneMath.calculateTargetSignalRequirements(ContainerType.HOPPER, 1)
        assertEquals(1, solveSS1.minWeightNeeded)

        // Solving for SS 15 on barrel (27 slots) should return 1728 items
        val solveSS15Barrel = RedstoneMath.calculateTargetSignalRequirements(ContainerType.BARREL, 15)
        assertEquals(1728, solveSS15Barrel.minWeightNeeded)
    }

    @Test
    fun ethoHopperClock_periodCalculation() {
        // 1 item transfers in 8 GT = 4 RT = 0.4s
        val oneItem = RedstoneMath.calculateEthoClockByItems(1)
        assertEquals(8L, oneItem.oneWayGameTicks)
        assertEquals(4L, oneItem.oneWayRedstoneTicks)
        assertEquals(0.4, oneItem.oneWaySeconds, 0.001)
        assertEquals(0.8, oneItem.fullCycleSeconds, 0.001)

        // 38 items -> 30.4s full cycle
        val thirtySec = RedstoneMath.calculateEthoClockByItems(38)
        assertEquals(30.4, thirtySec.fullCycleSeconds, 0.01)
        assertEquals(304L, thirtySec.fullCycleRedstoneTicks)
    }

    @Test
    fun repeaterChain_optimalLayout() {
        // 14 RT = 3x 4-tick + 1x 2-tick repeaters = 4 repeaters total
        val chain14 = RedstoneMath.calculateRepeaterChain(14)
        assertEquals(3, chain14.count4Tick)
        assertEquals(0, chain14.count3Tick)
        assertEquals(1, chain14.count2Tick)
        assertEquals(0, chain14.count1Tick)
        assertEquals(4, chain14.totalRepeaters)
        assertEquals(14, chain14.actualTicks)
        assertEquals(1.4, chain14.delaySeconds, 0.01)
    }

    @Test
    fun tntLaunch_ballisticsCalculation() {
        val launch = RedstoneMath.calculateTNTLaunch(16, "TNT")
        assertEquals(16, launch.tntCount)
        assertNotNull(launch.estimatedDistanceBlocks)
        assertEquals(38, launch.recommendedPayloadDelayTicks)
    }
}
