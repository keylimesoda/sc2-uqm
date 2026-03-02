# UX Research: Combat Touch Controls
## Star Control II — Android Port

**Date:** 2026-02-12  
**Stimulus:** Feedback from a competitive Super Melee player: *"Thrust must be on the left side; left/right controls must be on the right side. A joystick won't cut it."*

---

## 1. Problem Statement

SC2 combat requires **five simultaneous binary inputs**:

| Input | Function | Frequency |
|-------|----------|-----------|
| **Thrust** | Forward acceleration | Near-constant during maneuver |
| **Turn Left** | Counter-clockwise rotation | Near-constant during maneuver |
| **Turn Right** | Clockwise rotation | Near-constant during maneuver |
| **Weapon** | Primary fire | Frequent — often while maneuvering |
| **Special** | Ability activation | Intermittent — often while maneuvering |

The competitive requirement is that **Thrust** (left thumb) and **Turn L/R** (right thumb) must be dedicated, always-available controls. This consumes both thumbs, leaving no obvious home for Weapon and Special.

### Key simultaneity requirements (ordered by frequency)

1. **Thrust + Turn** — basic maneuvering; nearly constant  
2. **Thrust + Turn + Weapon** — shooting while maneuvering; very common  
3. **Thrust + Turn + Special** — using abilities while maneuvering; common  
4. **Weapon + Turn** (no thrust) — stationary aimed fire; occasional  
5. **Special alone** — occasional  

Combinations #2 and #3 are the crux of the problem: the player physically needs a **third and fourth touch point** while both thumbs are occupied.

---

## 2. Industry Precedents & Best Practices

### 2.1 Competitive Mobile FPS: The "Claw" Layout

**PUBG Mobile, Call of Duty Mobile, Fortnite Mobile** solved an identical problem — movement + aim + fire + ADS/special — and converged on the **4-finger claw grip** as the competitive standard:

```
┌──────────────────────────────────────────────────┐
│ [L-INDEX: FIRE]                  [R-INDEX: SCOPE]│
│                                                  │
│                                                  │
│                                                  │
│   (L-THUMB)                        (R-THUMB)    │
│   movement                         aim           │
└──────────────────────────────────────────────────┘
```

- **Left thumb:** Movement joystick  
- **Right thumb:** Aim/look joystick  
- **Left index finger:** Fire trigger (top-left zone)  
- **Right index finger:** ADS / special (top-right zone)  

This layout is used by **millions of competitive mobile players** and has been validated at tournament level. The key insight: **landscape phone ergonomics naturally support index fingers resting on the upper screen edges** when thumbs anchor the lower corners.

**Relevance to SC2:** Near-perfect analog. Thrust → left thumb, Turn L/R → right thumb, Weapon → one index, Special → other index.

### 2.2 Mobile Racing: Split Controls

**Real Racing 3, Asphalt 9, Grid Autosport:**

- Left thumb: Brake/gas (two buttons, stacked or side-by-side)  
- Right thumb: Steering left/right (two buttons or tilt zones)  
- Nitro/special: Typically a swipe or a button placed near the right thumb  

Racing games tolerate brief thumb-lifts for infrequent actions (nitro). SC2 cannot — a momentary loss of thrust or turn in competitive play is punishing.

### 2.3 Twin-Stick Shooters

**Geometry Wars, Archero, Brawl Stars:**

- Two virtual joysticks (move + aim/fire)  
- Auto-fire is common, reducing the problem  

Not directly applicable — SC2 weapon/special timing is skill-expressive and cannot be automated.

### 2.4 Fighting Games on Mobile

**Street Fighter IV iOS, Mortal Kombat Mobile:**

- Left side: D-pad / virtual joystick  
- Right side: Multiple attack buttons in a cluster  

These accept that the right thumb rapidly alternates between buttons. But they don't require sustained simultaneous hold of directional + action inputs the way SC2 maneuvering does.

### 2.5 Console-Style Overlay (Virtual Gamepad)

Some emulators and ports render a full virtual gamepad:

- D-pad left, ABXY buttons right, L/R triggers at top  
- The **trigger zones at top** are the relevant precedent — they provide exactly the "extra two inputs" for index fingers.

---

## 3. Proposed Solution: Hybrid Claw-Ready Layout

Based on industry consensus, the recommended approach is a **claw-primary layout** with **thumb-fallback zones** for accessibility:

### 3.1 Primary Layout (4-Finger Claw)

```
┌──────────────────────────────────────────────────────────────┐
│                                                              │
│  ╔═══════════╗                              ╔═══════════╗   │
│  ║  WEAPON   ║                              ║  SPECIAL  ║   │
│  ║ (L index) ║                              ║ (R index) ║   │
│  ╚═══════════╝                              ╚═══════════╝   │
│                                                              │
│                     [ game view ]                            │
│                                                              │
│                                                              │
│   ╔═══════════╗                        ╔════════╦════════╗   │
│   ║           ║                        ║  TURN  ║  TURN  ║   │
│   ║  THRUST   ║                        ║  LEFT  ║  RIGHT ║   │
│   ║ (L thumb) ║                        ║(R thm) ║(R thm) ║   │
│   ╚═══════════╝                        ╚════════╩════════╝   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

**Control zones:**

| Zone | Position | Finger | Input | Notes |
|------|----------|--------|-------|-------|
| Bottom-left | Lower-left quadrant | Left thumb | **Thrust** | Large, easy hold target |
| Bottom-right | Lower-right, split | Right thumb | **Turn L / Turn R** | Two adjacent buttons |
| Top-left | Upper-left edge | Left index | **Weapon** | Trigger-style zone |
| Top-right | Upper-right edge | Right index | **Special** | Trigger-style zone |

### 3.2 Why This Works

1. **All five inputs fully simultaneous.** Four fingers, no conflicts.  
2. **Proven ergonomics.** The claw grip is the established competitive mobile standard.  
3. **Matches the pro's requirement exactly.** Dedicated thrust (left thumb), dedicated turn L/R (right thumb), weapon and special always available via index fingers.  
4. **No joystick.** All discrete buttons — the player always knows their exact input state.  
5. **Low latency.** Direct button press, no gesture recognition delay.  

### 3.3 Thumb-Accessible Fallback (Casual Players)

For non-competitive / casual players who prefer a 2-thumb grip:

- **Weapon** also has a small secondary zone adjacent to / above the Thrust button. The left thumb can rock up to tap it, at the cost of briefly releasing thrust.  
- **Special** also has a small secondary zone adjacent to / above the Turn buttons. The right thumb can rock up to tap it, at the cost of briefly releasing turn.  

These secondary zones are **smaller and lower-opacity** to signal they're fallbacks, not primary. This gives casual players something reachable without the claw grip while keeping the competitive layout clean.

### 3.4 Sizing & Spacing Guidelines

Based on touch HIG research (Google Material Design, Apple HIG, Fitts's Law):

| Element | Minimum | Recommended | Notes |
|---------|---------|-------------|-------|
| Trigger zones (top) | 48×48 dp | 64 dp tall, full-corner width | Must be easy to find without looking |
| Thrust button | 80×80 dp | ~25% screen height | Large, forgiving hold target |
| Turn Left/Right | 60×80 dp each | ~20% screen height | Side-by-side, distinct gap between |
| Gap between Turn L/R | 4 dp | 6–8 dp | Enough to feel the edge, not so much it wastes space |
| Trigger-to-thumb gap | ≥120 dp | — | Prevent accidental cross-activation |

### 3.5 Visual Hinting

- **Index trigger zones** should render as translucent strips hugging the top corners, with a subtle label ("WPN" / "SPC") and an icon.  
- **A first-launch tooltip** or brief animation should demonstrate the claw grip for users unfamiliar with it.  
- Color-code: **Weapon = red/warm**, **Special = blue/cool** — consistent with the current color scheme (`WEAPON: 204,51,51` / `SPECIAL: 51,102,204`).

---

## 4. Alternative Approaches Considered

### 4.A Shoulder-Only (Index Triggers, No Thumb Fallback)

Same as §3.1 but without the casual fallback zones. Simplest to implement, but alienates non-claw players. **Not recommended** as the sole option.

### 4.B Gesture-Based (Swipe to Fire)

Swipe up on thrust zone → weapon, swipe up on turn zone → special. Avoids extra fingers but introduces recognition latency (≥80ms), ambiguity with holds, and a steep learning curve. **Rejected** — unacceptable input latency for competitive play.

### 4.C Pressure / Force Touch

Use press-force to distinguish thrust from weapon (light press = thrust, hard press = weapon). Not universally supported; inconsistent across devices; fatiguing. **Rejected.**

### 4.D Gyroscope / Accelerometer Hybrid

Use device tilt for turning, freeing the right thumb for weapon/special. Works for some racing games but:
- SC2 requires precise 1-frame turn input — tilt is too imprecise  
- Tilt conflicts with maintaining visual stability on a small screen  
- **Rejected** for competitive; could be a fun casual option in a settings menu.

### 4.E Auto-Fire with Manual Special

Weapon fires automatically when aimed (like Brawl Stars). Special remains manual.  
- Removes weapon timing as a skill expression  
- Some ships rely on precise weapon timing (e.g., Earthling Cruiser's nuke)  
- **Rejected** for default; could be offered as an accessibility option.

---

## 5. Implementation Impact

### What changes from the current layout

| Aspect | Current (Joystick) | Proposed (Discrete + Claw) |
|--------|-------------------|---------------------------|
| Left side | Floating 6-sector joystick | Single large THRUST button |
| Right side | SPECIAL + WEAPON stacked | TURN LEFT + TURN RIGHT side-by-side |
| Top corners | (empty) | WEAPON trigger (TL) + SPECIAL trigger (TR) |
| PAUSE / EXIT | Upper-right | Upper-center (shifted to avoid trigger conflicts) |
| Combo assist seam | Between SPECIAL/WEAPON | Between WEAPON/SPECIAL triggers, or removed |
| Finger count | 2 (both thumbs) | 4 (thumbs + index fingers) |

### New UI elements needed

1. **Two top-corner trigger zones** — new `ActionButton` entries or a new `TriggerZone` class  
2. **Thrust button** — replaces the left-side joystick zone in combat mode  
3. **Turn Left / Turn Right buttons** — replace the right-side stacked buttons in combat mode  
4. **Optional thumb-fallback zones** — smaller, semi-transparent secondary targets for weapon/special near the thumb zones  
5. **PAUSE/EXIT relocation** — move from upper-right to upper-center to clear the SPECIAL trigger zone

### SDL key mapping

No changes needed on the C side. Same keycodes:

| Button | Android Keycode | SDL Mapping |
|--------|----------------|-------------|
| Thrust | DPAD_UP (19) | Up arrow → thrust |
| Turn Left | DPAD_LEFT (21) | Left arrow |
| Turn Right | DPAD_RIGHT (22) | Right arrow |
| Weapon | CTRL_RIGHT (114) | RCtrl → weapon |
| Special | SHIFT_RIGHT (60) | RShift → special |

The 6-sector joystick's UP-LEFT and UP-RIGHT diagonals mapped thrust+turn combos; discrete buttons reproduce this via simultaneous multi-touch.

---

## 6. Recommendation

**Implement the Hybrid Claw-Ready Layout (§3)** as the COMBAT overlay mode.

1. **Phase 1:** Core claw layout — thrust, turn L/R, weapon trigger, special trigger. MENU and FLIGHT modes unchanged.  
2. **Phase 2:** Thumb-fallback zones for casual players.  
3. **Phase 3:** First-launch overlay tutorial showing the claw grip.  

This aligns with established competitive mobile norms, satisfies the pro player's requirements, and remains accessible to casual players via the fallback zones.

---

## 7. Open Questions

1. **Combo assist:** With weapon and special on separate fingers, is the combo seam still needed? Simultaneous press is trivial with two index fingers. Likely remove.  
2. **Tablet vs. phone:** On larger screens, the claw grip is less natural (wider reach). Consider an alternative tablet layout with thumb clusters. Defer until phone layout is proven.  
3. **User testing:** The claw layout should be tested with both competitive and casual SC2 players before finalizing.

---

## Addendum A: Competitive Trigger Zone Constraints

**Date:** 2026-02-12  
**Question:** Do standard Apple/Google HIG minimums (48dp touch target) apply to upper trigger zones in competitive mobile shooters?

### Short Answer: No. Competitive shooters use fundamentally different constraints.

The HIG minimum of 48×48 dp (Apple: 44×44 pt) is designed for **buttons the user looks at and taps with precision**. Competitive mobile shooter triggers violate nearly every assumption behind that guideline:

| HIG Assumption | Competitive Trigger Reality |
|---|---|
| User looks at the button | Player's eyes are fixed on center screen — triggers are operated **blind** |
| Thumb taps with fine motor control | Index finger presses with **gross motor control** from awkward grip angle |
| Single discrete tap | Sustained hold, rapid re-press, or "riding" the zone continuously |
| Button is among many nearby targets | Trigger is an **isolated zone** — no adjacent conflicting elements |
| Occasional use | Weapon trigger fires **hundreds of times per match** under adrenaline |

### The Real Constraints (derived from competitive mobile FPS norms)

Competitive mobile shooters (PUBG Mobile, CoD Mobile, Fortnite Mobile, Apex Legends Mobile, Free Fire) all ship with **full HUD editors** where every button can be resized 50–200% and freely repositioned. The competitive community, through millions of hours of tournament play, has converged on specific norms:

#### Constraint 1: Zone Size — Much Larger Than HIG

| Metric | HIG Minimum | Competitive Trigger Norm | Ratio |
|---|---|---|---|
| Width | 48 dp | 90–150 dp (15–22% of screen width) | ~2–3× |
| Height | 48 dp | 70–120 dp (12–18% of screen height) | ~1.5–2.5× |
| Touch area | 2,304 dp² | 6,300–18,000 dp² | ~3–8× |

**Why so large?**
- **Blind operation:** The player never looks at triggers. Larger = more forgiving to find by proprioception alone.
- **False-negative cost is catastrophic:** A missed fire in a 1v1 gunfight = death. Players oversize to eliminate any possibility of a miss.
- **Index finger ergonomics:** The index finger curls over the screen edge at an angle. The contact patch is elongated and imprecise compared to a thumb pad. Wider targets compensate.
- **Grip shift tolerance:** During intense play, the hand shifts on the device. A large zone remains reachable even as grip position drifts ±10mm.

#### Constraint 2: Edge Anchoring — Flush to Bezel

Triggers are **always flush to the screen edge** (top edge, or top + side corner). This is non-negotiable in competitive play because:

- The physical screen edge acts as a **tactile landmark** — the finger finds the zone by feeling the phone bezel/case edge, not by looking.
- No visual search required. Player touches the top-left corner of the phone? That's always weapon.
- **Inset only by system safe area** (notch/punch-hole/status bar), never by aesthetic padding.

```
┌─ safe area inset ─┐
│ ╔════════════╗     │  . . . . . . . .     ╔════════════╗ │
│ ║            ║     │                       ║            ║ │  ← flush to top
│ ║  WEAPON    ║     │                       ║  SPECIAL   ║ │
│ ║  trigger   ║     │                       ║  trigger   ║ │
│ ╚════════════╝     │                       ╚════════════╝ │
│ ← flush to left    │                    flush to right → │
```

#### Constraint 3: Vertical Strip vs. Square

Unlike thumb buttons (roughly square), index triggers in competitive play are typically **wider than tall** — a horizontal strip hugging the top edge:

- **Width:** 15–22% of screen width (~100–150 dp on a typical 6.5" phone)  
- **Height:** 10–15% of screen height (~65–100 dp)  
- **Aspect ratio:** ~1.5:1 to 2:1 (landscape rectangle)

This matches the index finger's natural contact shape when curling over the top edge of a phone in landscape.

#### Constraint 4: Dead Zone Between Triggers

The **center of the screen top edge** must be a dead zone — no trigger target there. Reasons:

- Prevents left-hand fire from accidentally hitting right-hand special (or vice versa)  
- Center-top often has the notch/camera cutout  
- The center gap should be **≥ 30% of screen width** between the inner edges of the two triggers  
- Pro players typically leave even more (40–50%) to avoid any cross-finger ambiguity

#### Constraint 5: No Overlap With Thumb Zones

The trigger zones must be **vertically separated** from the thumb zones by a significant gap:

- Minimum: 25% of screen height between trigger bottom edge and thumb zone top edge  
- This prevents the thumb from accidentally entering the trigger area during frantic maneuvers  
- Also prevents the index finger from drifting down into the thumb area

#### Constraint 6: Transparency & Minimal Visual Footprint

Competitive players set trigger **opacity to 0–30%** in HUD editors. Reasons:

- The trigger is never looked at — visual prominence wastes screen real estate  
- Lower opacity preserves the game view underneath  
- Some pros use **0% opacity** (fully invisible) since they locate triggers purely by proprioception  

For our implementation: render triggers at very low alpha (20–40), significantly less than thumb buttons.

### Recommended Dimensions for SC2 Combat Triggers

Based on competitive norms, adapted for our specific game:

| Property | Weapon (top-left) | Special (top-right) |
|---|---|---|
| Width | 20% of screen width | 20% of screen width |
| Height | 14% of screen height | 14% of screen height |
| X position | Flush to left safe edge | Flush to right safe edge |
| Y position | Flush to top safe edge | Flush to top safe edge |
| Min dead center gap | 35% of screen width | 35% of screen width |
| Alpha (normal) | 30 | 30 |
| Alpha (pressed) | 80 | 80 |
| Color | Red/warm (204, 51, 51) | Blue/cool (51, 102, 204) |
| Min absolute size | 90 × 65 dp | 90 × 65 dp |

### Key Takeaway

The HIG 48dp minimum is a **floor for precision-tapped UI elements.** Competitive mobile shooter triggers are a different category entirely — **blind-operated, proprioceptive action zones.** They follow a distinct set of constraints driven by ergonomics, error tolerance, and muscle memory. Our implementation should follow the competitive shooter norms, not the HIG minimum.

---

## Addendum B: Differentiated Haptics Design

**Date:** 2026-02-12  
**Question:** How should haptic feedback differ between trigger zones (index fingers) and thumb buttons (thrust, turn L/R) to aid spatial awareness?

### Design Principle: Haptic Channel Coding

In combat, the player operates 4–5 buttons blind. Haptics must communicate **which button was activated** through touch alone — a form of channel coding. The Android haptics API gives us several dimensions to vary:

| Haptic Dimension | Perception | API |
|---|---|---|
| **Intensity** (strong vs. light) | Very distinguishable | `VibrationEffect` amplitude, `Composition` scale |
| **Duration** (long vs. short) | Distinguishable | `createOneShot` duration, primitive choice |
| **Sharpness** (crisp click vs. soft thud) | Distinguishable on devices with good actuators | `HapticFeedbackConstants` choice, `Composition` primitives |
| **Pattern** (single vs. double pulse) | Highly distinguishable | `Composition` with delays |

The goal is to make each **functional group** — movement vs. weapon vs. special — feel distinct without overwhelming the player with vibration.

### Proposed Haptic Scheme

#### Tier 1: HapticFeedbackConstants (broadest device support, API 3+)

These work on nearly every Android device and are the primary fallback layer:

| Button | On Press | On Release | Rationale |
|---|---|---|---|
| **Thrust** | `KEYBOARD_PRESS` (light tick) | `KEYBOARD_RELEASE` (soft release) | Very frequent — lightest possible feel. Sustained hold shouldn't buzz. |
| **Turn L / Turn R** | `CLOCK_TICK` (ultra-light) | *none* | Most frequent input — near-continuous during combat. Must be barely perceptible or it becomes annoying. |
| **Weapon trigger** | `VIRTUAL_KEY` (medium click) | *none* | Distinct from movement — firmer "firing" confirmation. |
| **Special trigger** | `CONFIRM` (strong, rounded pulse) | *none* | Strongest single-event feel. Special abilities are high-stakes, infrequent — the player needs to know it registered. |
| **Combo (W+S)** | `LONG_PRESS` (heavy thud) | *none* | Unique heavy feel signals dual activation. |

#### Tier 2: VibrationEffect.Composition (richer devices, API 30+)

On devices supporting composition primitives, we can craft more expressive effects:

| Button | Composition | Feel Description |  
|---|---|---|
| **Thrust** | `PRIMITIVE_LOW_TICK` at scale 0.3 | Barely-there tick — proprioceptive confirmation without distraction |
| **Turn L / Turn R** | `PRIMITIVE_TICK` at scale 0.2 | Whisper-light. Direction changes happen 10+ times/second — must not fatigue |
| **Weapon trigger** | `PRIMITIVE_CLICK` at scale 0.7 | Clean, sharp "trigger pull" — iconic FPS fire feel |
| **Special trigger** | `PRIMITIVE_CLICK` at scale 1.0 + `PRIMITIVE_QUICK_RISE` at scale 0.5 after 10ms | Two-phase: crisp activation + brief "power surge" swell. Feels different from weapon. |
| **Combo (W+S)** | `PRIMITIVE_THUD` at scale 0.8 | Heavy, reverberating — distinct from everything else. Communicates "both firing" through weight. |

#### Tier 3: Sustained-Hold Behavior

Critical for combat: **no buzz during sustained hold.** Haptics fire only on:
- **Press edge** (finger makes contact)  
- **Release edge** (for Thrust only — signals engine cut)  
- **Never during hold** — a player holding thrust for 3 seconds should feel one tick at the start, then nothing.  

This follows Android's design principle: *"haptic effects applied to very frequent events should be very subtle."*

### Frequency-Aware Attenuation

During intense combat, multiple buttons fire in rapid succession. To prevent haptic saturation:

1. **Rate-limit haptics per button** — no more than 1 pulse per 80ms for any single button. If Turn L is pressed at 15 inputs/sec, only ~12 haptic pulses/sec fire (the rest are silent).
2. **Global cooldown** — if 3+ haptic events fire within 50ms, suppress all but the highest-priority one. Priority: Special > Weapon > Combo > Thrust > Turn.
3. **During combo activation** — suppress individual weapon/special haptics and play only the combo feel.

### Implementation Approach

```java
// In TouchOverlayView.java — new helper
private void fireHaptic(int buttonId) {
    long now = SystemClock.uptimeMillis();
    if (now - lastHapticTime[buttonId] < HAPTIC_COOLDOWN_MS) return;
    lastHapticTime[buttonId] = now;
    
    // Global suppression check
    if (now - lastGlobalHapticTime < GLOBAL_HAPTIC_COOLDOWN_MS
            && buttonPriority(buttonId) < lastHapticPriority) return;
    lastGlobalHapticTime = now;
    lastHapticPriority = buttonPriority(buttonId);
    
    if (Build.VERSION.SDK_INT >= 30 && hasCompositionSupport()) {
        fireCompositionHaptic(buttonId); // Tier 2
    } else {
        performHapticFeedback(hapticConstant(buttonId)); // Tier 1
    }
}
```

### Fallback Strategy

| Device Capability | Strategy |
|---|---|
| Supports `VibrationEffect.Composition` primitives | Tier 2 — rich, differentiated |
| Has amplitude control but no composition | Tier 1 with `HapticFeedbackConstants` |
| On/off vibration only (low-end) | Haptics only for Weapon and Special (skip movement entirely to avoid buzz) |

### User-Facing Settings

Offer a single **Haptic Intensity** slider in settings:
- **Off** — no haptics  
- **Light** — Tier 1 constants only, movement haptics disabled  
- **Normal** — full scheme as described above (default)  
- **Strong** — all Tier 2 scales increased by 1.4× (capped at 1.0)

### Key Takeaway

The haptic scheme uses **intensity + sharpness + pattern** to create three distinct "channels":
1. **Movement (thumb):** Whisper-light ticks — felt but never intrusive  
2. **Weapon (index):** Clean, sharp click — classic trigger feel  
3. **Special (index):** Two-phase click+swell — feels heavier and more "charged"

This lets the player distinguish which finger activated which button through touch alone, even with eyes locked on the game.
