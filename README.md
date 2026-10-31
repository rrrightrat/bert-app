# BERT — Bedrock Edition Redstone Toolkit

[![Minecraft Bedrock](https://img.shields.io/badge/Minecraft-Bedrock%201.21%2B-darkred?logo=minecraft)](https://www.minecraft.net/)
[![Platform](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://www.android.com/)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20%7C%20Material%203-blue?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Database-Room%20(Local%20Offline)-purple)](https://developer.android.com/training/data-storage/room)

**BERT (Bedrock Edition Redstone Toolkit)** is a specialized engineering companion built specifically to solve the complex calculations, non-deterministic tick timings, container comparator math, and contraption physics unique to **Minecraft Bedrock Edition (MCBE)**.

Designed with a sleek, minimalist dark aesthetic, BERT gives technical Bedrock builders instant precision tools without being limited to clunky sliders — every tool supports direct keyboard numeric input, quick preset chips, and local blueprint saving.

---

## 📥 How to Download the Latest Version

You can always download the latest compiled Android application directly from the GitHub repository:

1. **Go to Releases:** Navigate to the [**Releases**](https://github.com/rrrightrat/bert-app/releases) page of this repository.
2. **Find the Latest Release:** Look for the release tagged as **Latest** (e.g., `v1.0.0` or higher).
3. **Download the APK:** Under the **Assets** section at the bottom of the release notes, click on **`BERT-debug.apk`** (or `BERT.apk`) to download the installer directly to your device.
4. **Install on Android:**
   * Open the downloaded `.apk` file on your Android phone or tablet.
   * If prompted by Android with *"Install unknown apps"*, tap **Settings** and toggle **Allow from this source**.
   * Tap **Install** and launch **BERT** from your home screen.

---

## ✨ Features & Capabilities

### 1. 🎛️ Signal Strength & Comparator Workbench
* **Container Fullness Calculator:** Computes exact signal strength (0–15) using Bedrock's formula:
  $$\text{Signal Strength} = \left\lfloor 1 + \left(\frac{\text{Total Item Weight}}{\text{Slots} \times 64}\right) \times 14 \right\rfloor$$
  Supports Hoppers (5 slots), Single/Double Chests, Barrels, Droppers/Dispensers, Furnaces, Brewing Stands, Crafters, Lecterns, Composters, Cauldrons, and Chiseled Bookshelves.
* **Mixed Stack Weighting:** Seamlessly mixes 64-stack items ($1\times$), 16-stack items like Ender Pearls and Eggs ($4\times$), and non-stackables like Swords and Shulker Boxes ($64\times$).
* **Reverse Target Signal Solver:** Input any desired signal strength (e.g., $SS\ 3$ for an unblock gate or $SS\ 5$ for an analog line) to get the exact minimum items, maximum buffer, and recommended slot layout.
* **Interactive 15-Segment LED Signal Bar:** Visualizes live power levels, signal transmission range in blocks, and exact items required to reach the next signal level.
* **Mode Simulator:** Live interactive testing of Comparator Subtraction Mode ($\max(0, \text{Rear} - \text{Side})$) vs. Comparison Mode ($\text{Rear} \ge \text{Side}$).

### 2. ⏱️ Timings & Clock Engineering
* **Etho Hopper Clock Calculator:** Accurately converts item counts to tick delays based on Bedrock's 8 Game Tick (4 Redstone Tick / 0.4s) transfer rate. Also reverse-solves the exact item count needed for target durations (e.g., 15s, 30s, or 5-minute farm flush cycles).
* **Repeater Delay Chain Optimizer:** Calculates the most compact combination of 4-tick, 3-tick, 2-tick, and 1-tick repeaters needed to hit any target Redstone Tick delay.
* **Universal Tick Converter:** Instant conversion between real-time seconds, milliseconds, Game Ticks (GT, 20/s), Redstone Ticks (RT, 10/s), and frequency (Hz).
* **Comparator Pulse Extender (Fader):** Calculates signal decay steps and total pulse extension in dual-comparator decay loops.
* **Torch Burnout Warning:** Automatically alerts you if a clock frequency exceeds Bedrock's limit ($>8\text{ toggles per }60\text{ GT}$).

### 3. 📦 Item Sorter & Storage Engineering
* **ImpulseSV Filter Configurator:** Visualizes standard $41+1+1+1+1$ item filter states:
  * Resting: $SS\ 2$ (45 items)
  * Sorting Unlock: $SS\ 3$ (46 items $\to$ unlocks lower hopper)
  * Crosstalk Overflow: $SS\ 4$ (69 items $\to$ drains adjacent slices)
  * Calculates safe buffer capacity ($23$ items).
* **18-Item Budget & 16-Stackable Filters:** Analyzes safe overflow margins for rare items and pearls.
* **Hopper Throughput Calculator:** Evaluates transfer rates for single hoppers ($2.5\text{ items/s}$), parallel hoppers ($5.0\text{ items/s}$), and Minecart hoppers ($20\text{ items/s}$).

### 4. 🚀 Ballistics & TNT Physics (RR Right Rat Domain)
* **TNT Propellant Cannon Model:** Calculates horizontal velocity vectors ($V_x$), vertical vectors ($V_y$), and estimated reach in blocks for Boats, Players, Primed TNT, and Tridents.
* **Fuse Timing Synchronizer:** Aligns payload release delays with Bedrock's 80 Game Tick prime fuse.
* **Bedrock Trident Killer Calculator:** Computes hits/sec and total chamber DPS for 4-piston and 1-piston loops, factoring in Impaling V multipliers and Looting III sword transfer mechanics.
* **Boat Launcher & Ice Highway:** Piston spacing and timing intervals for Blue Ice ($72\text{ m/s}$), Packed Ice, and Regular Ice.

### 5. 💻 Computational Redstone & Logic
* **8-Bit Multi-Base Bus Converter:** Direct keyboard decimal input ($0\text{--}255$) synchronized with 8 interactive bit toggle switches ($b_0\text{--}b_7$), Hexadecimal (`0x00`–`0xFF`), Binary, and 4-Bit Analog Redstone ($SS\ 0\text{--}15$).
* **Bedrock Logic Gate Library:** Truth tables, tick propagation delays, and Bedrock-verified component recipes for AND, OR, NOT, XOR, XNOR, RS-NOR Latch, and T-Flip-Flop.

### 6. 📖 Bedrock Mechanics & Quirk Compendium
Searchable encyclopedia covering the core differences between Bedrock and Java:
* **Random Update Order (Non-Determinism):** Why simultaneous updates resolve randomly and why sequential pistons require 1-tick repeater buffers.
* **No Quasi-Connectivity (No QC):** How Bedrock's lack of QC enables ultra-compact vertical dropper/piston layouts.
* **Movable Tile Entities:** Pushing chests, hoppers, droppers, furnaces, and crafters with sticky pistons.
* **Sticky Piston Block Retaining:** Why 1-tick pulses do not spit blocks in Bedrock.
* **Direct Redstone Dust Redirection:** Redstone dust pointing straight into pistons without target blocks.
* **Sculk Sensor Vibration Frequencies:** Complete table for calibrated sculk sensors ($1\text{--}15$).

### 7. 🔖 Local Blueprints & Presets
* Backed by a local **Room Database** (`bert_redstone.db`).
* Save any calculation or setup directly from the tools.
* Pre-loaded with verified Bedrock reference blueprints.
* Search, filter by category, favorite, copy specs to clipboard, or write custom notes.

---

## 🎨 Design & Usability

* **Minimalist Dark Theme:** High-contrast palette designed for long redstone sessions.
* **Keyboard Direct Input:** Every calculator includes numeric text fields with full keyboard support (`NumericInputField`), preset pills, and fine-step controls (`-` / `+`).
* **Welcome Home Screen:** Netflix-inspired feature shelves and search bar for instant tool launch.
* **Custom Gear Icon:** Minimalist white gear silhouette with a centered "B" on a black background.

---

## 🛠️ Building from Source

To compile the project locally using Android Studio or the Gradle CLI:

```bash
# Clone the repository
git clone https://github.com/rrrightrat/BERT.git
cd BERT

# Build Debug APK
gradle assembleDebug

# The APK will be generated at:
# app/build/outputs/apk/debug/app-debug.apk

# Run Unit and Robolectric Tests
gradle :app:testDebugUnitTest
```

---

## 👤 Credits & Attribution

* **Original Concept & Research:** [RR Right Rat](https://www.youtube.com/@rrrightrat)
* **Target Engine:** Minecraft Bedrock Edition (MCBE) 1.21+ Tricky Trials & Up
