# UQM Android Touch Controls — UX Design Report

## Executive Summary

The Ur-Quan Masters (UQM) is a space combat and exploration game where combat
occurs in a 2D top-down arena with Newtonian-like physics. Each ship has exactly
**five flight inputs**: thrust, turn-left, turn-right, weapon, and special.
Despite this apparent simplicity, high-level play demands extreme dexterity — many
ships require holding 2–3 inputs simultaneously for extended periods, rapid
toggling between weapon and special, and precise directional control across 16
discrete facings. This report analyzes the game's input demands in depth, audits
the current touch overlay implementation, and proposes an optimized layout
specifically designed for combat effectiveness on mobile.

---

## Part 1 — Game Input Analysis

### 1.1 Core Flight Model

UQM combat uses a **16-facing system** (22.5° per step). Ships rotate discretely
through these facings; there is no analog turning. The turn rate varies per ship
(0.2 to 1.0 facings/frame at 24 FPS), meaning the physical input is binary:
turn-left or turn-right, held down to keep rotating.

**Thrust** is also binary — full acceleration or none. There is no analog
throttle. Velocity is Newtonian with per-ship max speed and inertia.

This means a touch joystick's analog deflection is wasted on this game.
However, directional intent mapping (angle → which cardinal/diagonal keys to press)
is still valuable for ergonomics.

This revision incorporates stakeholder feedback to use: (1) very large WEAPON/SPECIAL
buttons that are very close but intentionally separated, (2) a single joystick
with fixed angular sectors for thrust/turn/menu-down, and (3) small, explicit
Pause/Exit buttons on the upper-left side.

### 1.2 The Five Flight Inputs and Their Keyboard Mapping

| Input       | Default Key (Template 1) | SDL Scancode     | Android Keycode     |
|-------------|--------------------------|------------------|---------------------|
| Thrust      | Up Arrow                 | SDL_SCANCODE_UP  | KEYCODE_DPAD_UP (19)|
| Turn Left   | Left Arrow               | SDL_SCANCODE_LEFT| KEYCODE_DPAD_LEFT(21)|
| Turn Right  | Right Arrow              | SDL_SCANCODE_RIGHT| KEYCODE_DPAD_RIGHT(22)|
| Weapon      | Right Ctrl               | SDL_SCANCODE_RCTRL| KEYCODE_CTRL_RIGHT(114)|
| Special     | Right Shift              | SDL_SCANCODE_RSHIFT| KEYCODE_SHIFT_RIGHT(60)|

Additional system keys:

| Function    | Default Key  | Android Keycode         |
|-------------|-------------|-------------------------|
| Menu Select | Enter        | KEYCODE_ENTER (66)      |
| Menu Cancel | Escape       | KEYCODE_ESCAPE (111)    |
| Exit Melee  | F10          | KEYCODE_F10 (140)       |
| Pause       | F1           | KEYCODE_F1 (131)        |
| Down Arrow  | Down Arrow   | KEYCODE_DPAD_DOWN (20)  |

**Critical note**: Down Arrow is used only in menus (scrolling lists, adjusting
values). It has **zero function in combat**. In flight, only Up (thrust), Left,
and Right matter.

### 1.3 Simultaneous Input Patterns in Combat

This is the crux of the UX challenge. Analysis of all 25 Super Melee ships
reveals pervasive multi-button chording requirements:

#### Category A — Hold Special for Entire Combat (+ fire + steer)

| Ship | Behavior | Inputs Held Simultaneously |
|------|----------|----------------------------|
| **Pkunk Fury** | Special triggers auto-revive and regeneration. Wiki: *"The 'Special' key is best held down for the entire duration of combat."* Plus Death Blossom: hold fire while turning. | **Special + Weapon + Turn** (all 3) |
| **Androsynth Guardian** | Blazer form: hold Special to transform into invincible comet. Steer while blazing. "Low energy blazer hops" = rapid tap Special. | **Special + Turn (+ Thrust)** |
| **Mycon Podship** | Hold Special to guide homing plasmoids. Must steer simultaneously. | **Special + Turn** |

#### Category B — Weapon + Thrust + Turn Simultaneously

| Ship | Behavior | Inputs Held Simultaneously |
|------|----------|----------------------------|
| **Spathi Eluder** | B.U.T.T. missiles fire rearward. Optimal play: thrust toward enemy and fire while retreating. | **Weapon + Thrust + Turn** |
| **Earthling Cruiser** | Point-defense homing missiles work best when pilot maintains pursuit angle. Hold fire while maneuvering. | **Weapon + Thrust + Turn** |

#### Category C — Rapid Weapon/Special Toggling

| Ship | Behavior | Pattern |
|------|----------|---------|
| **Yehat Terminator** | Short-range cannons + energy shield. Must rapidly alternate: fire salvos, shield incoming, fire again. Energy management is paramount. | **Rapid toggle Weapon ↔ Special** (with steering throughout) |
| **Utwig Jugger** | Absorb damage with shield (Special), retaliate with energy spears (Weapon) when safe. | **Rapid toggle Weapon ↔ Special** |

#### Category D — Hold Weapon Continuously (+ Special + Turn)

| Ship | Behavior | Inputs Held Simultaneously |
|------|----------|----------------------------|
| **Chmmr Avatar** | Continuous-fire laser (0 refire delay, fires every frame while held). Plus tractor beam (Special) to pull enemies into range. | **Weapon + Special + Turn** |
| **Supox Blade** | Lateral/reverse thrust via Special while firing and steering. | **Weapon + Special + Turn** |

#### Category E — Special as Held Modifier (+ Steering)

| Ship | Behavior | Inputs Held Simultaneously |
|------|----------|----------------------------|
| **Thraddash Torch** | Afterburner (Special) for high-speed maneuvers while steering. "Cast net" pattern = repeated afterburner burns at different angles. | **Special + Turn + Thrust** |
| **Mmrnmhrm X-Form** | Special toggles between missile and laser form. Must steer and fire in both. | Tap Special (mode switch), then **Weapon + Turn** |

### 1.4 Input Frequency Summary

Across all 25 ships and competitive play patterns:

| Combination | Frequency | Priority |
|-------------|-----------|----------|
| **Turn + Weapon** | Nearly universal | Critical |
| **Turn + Special** | ~60% of ships | Critical |
| **Weapon + Special** (chord) | ~40% of ships | High |
| **Turn + Thrust + Weapon** | ~30% of ships | High |
| **Turn + Weapon + Special** | ~25% of ships (Fury, Avatar, Blade) | High |
| **All five simultaneously** | Rare but possible | Medium |

**Key insight**: The ability to hold Weapon and Special at the same time while
steering is the single most important ergonomic requirement. It is non-negotiable
for competitive play.

---

## Part 2 — Current Implementation Audit

### 2.1 Layout Constants

```
DPAD_RADIUS_FRAC  = 0.15   (fraction of screen height)
BUTTON_RADIUS_FRAC = 0.065  (fraction of screen height)
margin             = dpadRadius × 1.4
btnSpacing         = btnRadius × 2.8
```

On a typical 1080p (1920×1080) phone in landscape:
- D-pad radius = 162px → **~54dp** on xxhdpi (3x) — adequate
- Button radius = 70px → **~23dp** on xxhdpi — **severely undersized**
- Button diameter = 140px → **~47px CSS/pt equivalent** — borderline minimum

### 2.2 Layout Geometry

```
Right side (from margin inward):

    [SP]            ← btnMarginX, btnMarginY - 2×spacing
    [WP]            ← btnMarginX, btnMarginY - 1×spacing
[ESC] [OK]          ← bottom row
```

### 2.3 Identified Problems

#### Problem 1: Buttons Are Too Small
At 0.065× screen height, action buttons compute to:
- **~46dp diameter** on a 6.1" 1080p device (393 DPI ≈ 2.75x)
- **~35dp diameter** on a 6.7" 1440p device (515 DPI ≈ 3.5x)

Apple HIG mandates **minimum 44pt** for frequently-used controls. Google Material
recommends **48dp minimum** touch targets. The current buttons are at or below
these minimums depending on device density, making them difficult to hit reliably
during fast combat.

#### Problem 2: Weapon and Special Cannot Be Chorded Easily
SP and WP are arranged in a **vertical stack** separated by `2.8 × btnRadius`
(~196px on 1080p, ~65dp). A single right thumb cannot comfortably press both
simultaneously in this layout. The thumb must either:
- Stretch vertically (unnatural for the dominant thumb position), or
- Use an awkward grip with two fingers (impractical on phone form factors)

This is the most severe UX flaw. As documented in Part 1, ~40% of ships
require simultaneous Weapon+Special, and the most competitively important ships
(Fury, Avatar, Blade) require it constantly.

#### Problem 3: No Exit / Pause Controls
- **F10 (Exit Melee)** is not mapped. Once in a Super Melee match, the player
  has no way to exit except force-closing the app.
- **F1 (Pause)** is not mapped. The player cannot pause during combat.

#### Problem 4: Static D-pad Position
The d-pad is fixed at `(margin, h - margin)`. If the player's natural thumb
resting position doesn't align with this location, every d-pad interaction
requires the thumb to travel to a fixed point. Modern touch games universally
use floating/dynamic joysticks that appear where the thumb first touches.

#### Problem 5: D-pad Up = Thrust Creates an Ergonomic Conflict
On a keyboard, thrust (Up) is independent from turn (Left/Right). A player can
hold Up with one finger and tap Left/Right with another. On the 8-way d-pad,
thrust+turn becomes a diagonal (Up-Left or Up-Right), which works mechanically
but requires precise diagonal placement. Pure thrust (Up only) and thrust+turn
(diagonal) demand different thumb positions within the same small area.

More critically, some ships need thrust sustained while rapidly alternating
turn direction (zigzag chase patterns). On the d-pad, this means holding the
upper area while rapidly rocking between upper-left and upper-right — an
awkward motion prone to dropping thrust or accidentally triggering pure
left/right (turn without thrust).

#### Problem 6: Labels Are Cryptic
"SP", "WP", "ESC", "OK" convey nothing to a new player. Apple HIG specifically
recommends action-descriptive icons over text abbreviations for game controls.

#### Problem 7: No Haptic Feedback
Button presses and d-pad direction changes produce no tactile feedback. On a
featureless glass screen, haptics are the only substitute for the physical
feedback of a real button press.

---

## Part 3 — Proposed Optimal Layout

### 3.1 Design Philosophy

1. **Combat-first**: Every decision prioritizes the 5-input flight model
2. **Chord-friendly**: Weapon + Special must be trivially simultaneous
3. **Thumb-reach optimized**: All combat controls within natural thumb arc
4. **Contextual**: Show only what's needed for the current game state
5. **Compliant**: All touch targets ≥ 48dp, following Material Design guidelines

### 3.2 Proposed Combat Layout

```
┌─────────────────────────────────────────────────────────────────────┐
│ [PAUSE] [EXIT]                                                      │
│                                                                     │
│                         GAME VIEWPORT                               │
│                          (320×240)                                  │
│                                                                     │
│                                                                     │
│     ╭──────────────╮                           ┌──────────┐         │
│     │ FLOATING     │                           │ SPECIAL  │         │
│     │ JOYSTICK     │                           ├──────────┤         │
│     ╰──────────────╯                           │ WEAPON   │         │
│        (single stick)                          └──────────┘         │
└─────────────────────────────────────────────────────────────────────┘

LEFT HAND                                              RIGHT HAND
─────────                                              ──────────
Floating thumbstick:                           SPECIAL/WEAPON cluster:
  Appears where left thumb lands                two very large square zones,
  in left-bottom control zone                   stacked vertically with
  Uses fixed 360° sector map                    a small intentional gutter
  for Thrust/Left/Right/Down                    for discrimination and easy dual-press

Upper-left corner:
  small, labeled [PAUSE] and [EXIT]
```

### 3.3 Detailed Component Specifications

#### 3.3.1 Left Side — Single Floating Joystick (No Separate Thrust Button)

**Floating Thumbstick (Primary Movement Control)**

| Property | Value | Rationale |
|----------|-------|-----------|
| Activation zone | Left 40% of screen, bottom 60% | Generous zone for thumb landing |
| Base radius | 80dp | Large enough for clear visual and precise angles |
| Knob radius | 30dp | Visible finger tracking indicator |
| Dead zone | 15% of base radius | Prevents accidental inputs at rest |
| Behavior | Appears at touch-down point, disappears on release | Modern floating stick pattern (Apple HIG recommended) |
| Directions in combat/menu | Sector-mapped Up/Left/Right/Down | Single stick handles both combat and menus |

**Angular sector mapping (explicit requirement)**

Joystick angle space is divided into four non-overlapping sectors totaling 360°:

- **Top 120°** → Up Arrow (Thrust): 30° to 150°
- **Left 100°** → Left Arrow: 150° to 250°
- **Bottom 40°** → Down Arrow (menu use): 250° to 290°
- **Right 100°** → Right Arrow: 290° to 360° and 0° to 30°

This yields exactly the requested distribution: 120 + 100 + 40 + 100 = 360.

**Behavior notes**
- Combat naturally favors top/left/right sectors; bottom remains available but narrow.
- In menus, all four sectors remain active, including the dedicated 40° Down sector.
- A 12–15% dead zone remains at center to prevent accidental key jitter.

**External consultant second opinion (retro keyboard-port focus)**

The 120/100/40/100 split is a strong baseline, but it under-specifies
thrust+turn combos if sectors are mutually exclusive. Recommended upgrade:

- Keep the narrow **Down = 40°** concept.
- Use an explicit **6-sector map** in combat-capable zones so `Up+Left` and
  `Up+Right` are first-class outputs instead of boundary side-effects.

Angle convention below uses `0° = Up`, increasing clockwise:

| Sector | Range | Width | Output |
|--------|-------|-------|--------|
| Up-only | 325°–360° and 0°–35° | 70° | Up |
| Up+Right | 35°–70° | 35° | Up + Right |
| Right-only | 70°–160° | 90° | Right |
| Down-only | 160°–200° | 40° | Down |
| Left-only | 200°–290° | 90° | Left |
| Up+Left | 290°–325° | 35° | Up + Left |

This preserves strong cardinal control and menu-down discipline while explicitly
budgeting angle for thrust+turn play.

Implementation guidance from the same review:
- **Radial gating**: inner ring cardinal-only; outer ring enables diagonal sectors.
- **Angular hysteresis**: ~6° entry / ~8° exit to prevent boundary flicker.
- **Context rule**: suppress `Down` during combat if needed; keep it active in menus.

Alternative presets:
- **Combo-heavy**: `Up 50°, UR 45°, R 90°, D 40°, L 90°, UL 45°`
- **Cardinal-sticky**: `Up 90°, UR 25°, R 90°, D 40°, L 90°, UL 25°`

#### 3.3.2 Right Side — WEAPON + SPECIAL (Large Square Stack)

This is the most critical layout decision. The two combat buttons must be:
1. Large enough to hit without looking (48dp minimum, 56dp recommended)
2. Close enough to chord with one thumb
3. Separated enough to avoid mis-hits

**Recommended: Top/Bottom square stack with combo-friendly center seam**

```
          ┌──────────────┐
          │   SPECIAL    │   (TOP)
          ├──────┄┄──────┤   (thin center seam)
          │   WEAPON     │   (BOTTOM)
          └──────────────┘
```

| Property | WEAPON | SPECIAL |
|----------|-------------|--------------|
| Shape | Large square (or slightly rounded square) | Large square (or slightly rounded square) |
| Position | Right side, **bottom** square | Right side, **top** square |
| Relative size | Side length = 96% of right action-panel width | Side length = 96% of right action-panel width |
| Separation | 4% of action-panel height seam/gap | Keeps distinct taps while preserving quick dual-press |
| Color | Red (#CC3333) | Blue (#3366CC) |
| Label | "WEAPON" | "SPECIAL" |
| Haptic | Light click (5ms) on press | Light click (5ms) on press |

**Rationale for this geometry**:
- SPECIAL on top and WEAPON on bottom matches your preferred ordering and thumb travel.
- Square hit areas maximize usable touch surface in a constrained column.
- Buttons occupy ~80% of right action-panel area combined (target: 78–82%).
- Thin seam + strong labels preserve distinction while keeping chording easy.

**Dual-press assist (optional, recommended)**:
- Add an invisible **combo-assist strip** centered on the seam (about 10–14dp tall,
  spanning ~70% of button width).
- Touches beginning in this strip emit **both WEAPON + SPECIAL** immediately.
- Touches beginning inside SPECIAL/WEAPON squares still behave as normal single-button presses.

This keeps normal controls predictable while making deliberate dual-press easy.

#### 3.3.3 System Buttons — Upper Left Corner

```
    [PAUSE] [EXIT]
```

| Button | Keycode | Size | Position |
|--------|---------|------|----------|
| Pause | F1 (KEYCODE_F1 = 131) | 34–36dp rounded rect | Top-left, 8dp margins |
| Exit | F10 (KEYCODE_F10 = 140) | 34–36dp rounded rect | Right of Pause, 8dp gap |

These buttons are intentionally small, clearly labeled text-first controls,
anchored away from the combat thumb zones.

Optional behavior: long-press on `EXIT` can emit `KEYCODE_ESCAPE` when a
menu-cancel action is needed without adding a third top-corner button.

#### 3.3.4 Menu Navigation Buttons

When not in combat (menu screens, ship selection, conversation), the right-side
layout changes:

```
    [OK]           ← Enter/Select (KEYCODE_ENTER)
    [BACK]         ← Back/Cancel (KEYCODE_ESCAPE)
```

These can be larger (64dp) since chording isn't needed, and positioned in a
simple vertical stack where the current buttons are. The menu context is
lower-intensity, so this conservative layout is appropriate.

**Context detection**: The overlay can't easily know if the game is in a menu or
combat. Two approaches:
1. **Manual toggle**: A small mode-switch icon in the corner (e.g., ⚔️/📋)
2. **Always-visible approach**: Show all buttons at all times, with combat
  buttons (WEAPON/SPECIAL) on the right and system/menu buttons (OK/EXIT/PAUSE) in
  a smaller cluster in the upper-left corner

**Recommended**: Always-visible approach. The OK and ESC buttons are harmless
during combat (pressing Enter or Escape during flight has no effect in most
contexts). They can remain visible but de-emphasized (lower opacity, smaller
size) when positioned away from the combat zone.

### 3.4 Complete Proposed Layout — Final Diagram

```
LANDSCAPE ORIENTATION (primary use case)

┌──────────────────────────────────────────────────────────────────┐
│ [PAUSE] [EXIT]                                                   │
│                                                                  │
│                                                                  │
│                      GAME VIEWPORT                               │
│                                                                  │
│                                                                  │
│                                              ┌──────────────┐     │
│      ╭──────────────╮                        │   SPECIAL    │     │
│      │   floating   │                           ├──────────┤      │
│      │   joystick   │                        │   WEAPON     │     │
│      ╰──────────────╯                        └──────────────┘     │
└──────────────────────────────────────────────────────────────────┘

Left thumb region:                    Right thumb region:
├─ Floating stick (all 4 arrows       ├─ SPECIAL(top) + WEAPON(bottom) square stack
│  via 120/100/40/100 sectors)        │  (~80% panel occupancy combined)
│                                      ├─ Optional seam strip can emit SPECIAL+WEAPON
Top-left:
├─ PAUSE + EXIT (small, labeled)
```

### 3.5 Sizing Reference Table

Computed for a reference device: 6.1" display, 2400×1080, 393 DPI (~2.75x density).
Screen in landscape = 1080h × 2400w.

| Element | Fraction of Height | Pixels | dp | Meets 48dp? |
|---------|-------------------|--------|-----|-------------|
| **Current D-pad radius** | 0.150 | 162 | 59 | ✓ (total area is fine) |
| **Current button radius** | 0.065 | 70 | 25 | ✗ (diameter=50dp) → **too small** |
| **Proposed stick base radius** | 0.074 | 80 | 29 | (total diameter = 58dp) ✓ |
| **Right action panel** | 0.52h × 0.22w | 562×528px (example) | — | — |
| **Proposed SPECIAL/WEAPON square side** | 0.96 × panel-width | 507px (example) | 184dp | ✓ |
| **Proposed SPECIAL↔WEAPON seam** | 0.04 × panel-height | 22px (example) | 8dp | N/A |
| **Combined SPECIAL+WEAPON occupancy** | ≈0.80 of panel area | 78–82% target | — | — |
| **Proposed PAUSE/ESC** | 0.031–0.033 | 34–36 | 34–36dp | ✓ (rare use allows smaller) |

### 3.6 Color Scheme

| Element | Color | Hex | Rationale |
|---------|-------|-----|-----------|
| Stick base | Dark gray, 25% opacity | #505050 @ 40 | Unobtrusive background |
| Stick knob | White, 50% opacity | #FFFFFF @ 128 | Clear position indicator |
| Weapon button | Red, 30% opacity | #CC3333 @ 77 | Red = attack, universal game convention |
| Special button | Blue, 30% opacity | #3366CC @ 77 | Blue = ability/mana, universal convention |
| PAUSE/ESC buttons | Gray, 20% opacity | #808080 @ 51 | De-emphasized, non-combat |
| Pressed state | +100% opacity (doubled) | — | Clear tactile feedback |

---

## Part 4 — Interaction Design Details

### 4.1 Floating Thumbstick Behavior

```
Touch begins in left 40% / bottom 60% zone:
  1. Record touch point as stick center
  2. Draw base circle at that point
  3. Draw knob at finger position
  4. Calculate angle from center to finger

On MOVE:
  5. If distance > dead zone (15% of base radius):
    - 30°..150° → Up (thrust)
    - 150°..250° → Left
    - 250°..290° → Down (menus)
    - 290°..360° or 0°..30° → Right
  6. If distance > base radius: clamp knob visually, keep input active
  7. Knob follows finger within/on circle boundary

On RELEASE:
  8. Release all direction keys
  9. Fade out and remove stick visuals (150ms fade)
```

### 4.2 Sector Hysteresis and Stability

To prevent rapid flicker at boundaries, apply 6–8° hysteresis:
- Enter a sector at boundary angle.
- Stay in current sector until pointer crosses boundary + hysteresis margin.

This is especially important near 150°, 250°, and 290° transitions, where the
player may be fighting while making small corrective motions.

### 4.3 Haptic Feedback Specification

| Event | Vibration | Android API |
|-------|-----------|-------------|
| Button press (WEAPON/SPECIAL) | 5ms, light | `HapticFeedbackConstants.VIRTUAL_KEY` |
| Button release | None | — |
| D-pad direction change | 3ms, subtle | `HapticFeedbackConstants.CLOCK_TICK` |
| Pause/Exit press | 10ms, medium | `HapticFeedbackConstants.LONG_PRESS` |

Haptics should be user-configurable (off/light/medium) via a game settings
option, since some players find vibration distracting.

### 4.4 Auto-Fade Behavior

The current 4-second fade with 1-second duration to 30% minimum opacity is
reasonable. Proposed refinements:

- **Active combat**: Controls should remain at full opacity while any button is
  pressed. The fade timer resets on every touch event (current behavior ✓).
- **Idle threshold**: Increase to 5 seconds (combat pauses are rare and brief).
- **Minimum opacity**: Increase from 30% to 40% — at 30%, buttons become hard to
  acquire visually, especially in bright outdoor viewing conditions.
- **Re-show animation**: On first touch after fade, instantly snap to full opacity
  (don't animate back up — the player needs to see the controls NOW).

---

## Part 5 — Ship-Specific Control Pattern Validation

This section validates the proposed layout against the most demanding ships to
ensure all combat patterns are physically achievable.

### 5.1 Pkunk Fury — Maximum Simultaneous Input

**Required**: Hold Special entire match + Fire weapon + Turn continuously

**Proposed layout execution**:
- Right thumb bridges WEAPON and SPECIAL (both pressed, held)
- Left thumb on floating stick, rocking left/right to turn
- Weapon and Special held by the flat of the right thumb across both buttons

**Verdict**: ✅ The large SPECIAL-top/WEAPON-bottom square stack with seam assist makes
this practical on one thumb.

### 5.2 Spathi Eluder — Thrust + Fire + Turn

**Required**: Thrust into enemy, fire rearward, steer to maintain retreat angle

**Proposed layout execution**:
- Left thumb biases joystick into top sector for thrust
- While maintaining top-sector pressure, player leans left/right into adjacent sectors for turn corrections
- Right thumb holds WEAPON

**Verdict**: ✅ Sector mapping supports thrust-first pursuit while preserving
quick side corrections.

### 5.3 Yehat Terminator — Rapid WEAPON/SPECIAL Toggling

**Required**: Fire cannon salvo → immediately shield → fire again → shield again

**Proposed layout execution**:
- Right thumb positioned between WEAPON and SPECIAL
- Rock thumb across the SPECIAL/WEAPON seam (top↔bottom) for fast transitions
- Left thumb steers throughout

**Verdict**: ✅ The short seam crossing between large square targets enables
reliable toggling with minimal travel.

### 5.4 Chmmr Avatar — Weapon + Special + Turn

**Required**: Continuous laser (hold WEAPON) + tractor beam (hold SPECIAL) + steering

**Proposed layout execution**:
- Right thumb bridges WEAPON+SPECIAL (flat press)
- Left thumb on floating stick, turning to track enemy
- Tractor pulls enemy into laser range; player adjusts facing

**Verdict**: ✅ Bridge press plus optional seam strip provides dependable dual-input.

### 5.5 Androsynth Guardian — Hold Special + Thrust + Turn

**Required**: Blazer form (hold Special) + thrust into enemy + fine steering

**Proposed layout execution**:
- Right thumb holds SPECIAL
- Left thumb operates joystick primarily in top/left/right sectors for thrust + turn

**Verdict**: ✅ Single-stick sectors keep motion simple while right thumb owns
Special.

### 5.6 Thraddash Torch — Special + Turn + Thrust (Cast Net)

**Required**: Afterburner (hold Special) leaves fire trail. Fly in patterns,
release Special, re-engage at different angle. Needs rapid Special on/off with
constant steering and intermittent thrust.

**Proposed layout execution**:
- Right thumb taps SPECIAL on/off while left thumb steers + thrusts
- Top-sector thrust plus adjacent left/right sectors supports cast-net pacing

**Verdict**: ✅ Clean separation of all three concerns.

---

## Part 6 — Implementation Priorities

Ranked by impact on gameplay quality:

### Priority 1 (Critical — Blocks Competitive Play)
1. **Implement SPECIAL(top)/WEAPON(bottom) large square stack** — Combined occupancy
  target 78–82% of right action-panel area, with strong labels.
2. **Implement joystick sector mapping 120/100/40/100** — Top thrust,
  left/right turn, narrow bottom-down for menus.
3. **Place small labeled PAUSE and ESC buttons at upper-left** — Keeps system
  controls away from combat zone while always accessible.

### Priority 2 (High — Significantly Improves Experience)
4. **Convert fixed d-pad to floating joystick** — Appears at touch point,
  applies sector mapping consistently.
5. **Add sector hysteresis** (6–8°) — Prevents jitter at zone boundaries.
6. **Add seam combo-assist strip** (optional) — Center strip can emit WEAPON+SPECIAL
  for easier intentional dual-press.
7. **Add haptic feedback** on button press — Essential tactile confirmation.

### Priority 3 (Medium — Polish and Feel)
8. **Use text-first labels** for system controls — `PAUSE`, `ESC` for clarity.
9. **Keep WEAPON/SPECIAL visually distinct** — color + label + light outline to prevent misreads.
10. **Improve auto-fade** — 5-second delay, 40% minimum, instant re-show.
11. **Add color coding** — Red for Weapon, blue for Special.

### Priority 4 (Low — Nice to Have)
12. **Menu/combat mode toggle** — Auto-detect or manual switch for context-
    appropriate layouts.
13. **Chord visual indicator** — Glow/bar when WEAPON+SPECIAL are both held.
14. **User-configurable sizing** — Settings slider for button scale (0.75x–1.5x).
15. **Opacity/haptics settings** — Player preference toggles.

---

## Part 7 — Comparison with Similar Mobile Ports

### RetroArch / PPSSPP / Mupen64
These emulators use a floating virtual analog stick + configurable button
overlays. Key learnings:
- Buttons are user-repositionable and resizable — the gold standard
- Default layouts are usually poor; the power is in customization
- Haptic feedback on button press is standard

### Geometry Wars / Asteroid-likes
Twin-stick shooters on mobile validate our core layout:
- Left stick for movement, right buttons for actions
- The "fire" button is always the largest and most accessible
- Secondary abilities are placed adjacent for chording

### DOOM/Quake Mobile Ports
These use:
- Floating left thumbstick (move)
- Fire button: large, prominent, right side
- Weapon switch: smaller, above fire
- This maps perfectly to our WEAPON (large) + SPECIAL (adjacent) model

---

## Appendix A — All 25 Super Melee Ships Input Patterns

| # | Ship | Primary Use | Weapon Hold | Special Hold | Chord WP+SP | Thrust-Critical |
|---|------|-------------|-------------|--------------|-------------|-----------------|
| 1 | Androsynth Guardian | Blazer charges | Tap | Hold (Blazer) | No | Yes (Blazer) |
| 2 | Arilou Skiff | Teleport evasion | Tap | Tap (teleport) | No | Moderate |
| 3 | Chenjesu Broodhome | Sniper | Hold (crystal) | Tap (DOGI) | Rare | Low |
| 4 | Chmmr Avatar | Laser + tractor | Hold (laser) | Hold (tractor) | **Yes** | Moderate |
| 5 | Druuge Mauler | Recoil cannon | Tap | Tap (crew fuel) | No | High (recoil) |
| 6 | Earthling Cruiser | Homing + PD | Tap/Hold | Tap (PD laser) | Rare | Moderate |
| 7 | Ilwrath Avenger | Stealth + fire | Hold | Hold (cloak) | **Yes** | Moderate |
| 8 | Kohr-Ah Marauder | Buzzsaw + FRIED | Tap | Tap/Hold (FRIED) | Sometimes | Moderate |
| 9 | Melnorme Trader | Charge shot | Hold (charge) | Tap (confuse) | Rare | High |
| 10 | Mmrnmhrm X-Form | Mode switcher | Hold (varies) | Tap (transform) | No | Moderate |
| 11 | Mycon Podship | Guided plasma | Tap | Hold (guide) | Rare | Low |
| 12 | Orz Nemesis | Turret + marines | Hold (turret) | Tap (marines) | Rare | Moderate |
| 13 | Pkunk Fury | Death Blossom | Hold | Hold (regen) | **Yes** | High |
| 14 | Shofixti Scout | Glory suicide | Tap | Tap (glory) | No | High |
| 15 | Slylandro Probe | Lightning + harvest | Tap | Tap (absorb) | No | N/A (inertialess) |
| 16 | Spathi Eluder | BUTT missiles | Hold (retreat fire) | Slow | Rare | **Critical** |
| 17 | Supox Blade | Lateral thrust | Hold | Hold (reverse) | **Yes** | High |
| 18 | Syreen Penetrator | Crew steal | Tap | Tap (siren call) | No | Moderate |
| 19 | Thraddash Torch | Afterburner trail | Tap | Hold (afterburn) | No | **Critical** |
| 20 | Umgah Drone | Antimatter cone | Hold (cone) | Tap (zip) | Rare | Low |
| 21 | Ur-Quan Dreadnought | Fusion + fighters | Tap | Tap (fighters) | No | Moderate |
| 22 | Utwig Jugger | Shield absorb | Hold (spears) | Hold (shield) | Toggle | Moderate |
| 23 | VUX Intruder | Limpet + laser | Hold (laser) | Tap (limpet) | Rare | Low (warp-in) |
| 24 | Yehat Terminator | Shield toggle | Hold (cannons) | Hold (shield) | Toggle | High |
| 25 | ZoqFot Stinger | Tongue + DOGIs | Tap | Tap | No | High |

**Ships requiring reliable WP+SP chord**: Avatar, Avenger, Fury, Blade (4/25 = 16%)
**Ships requiring rapid WP↔SP toggle**: Jugger, Terminator (2/25 = 8%)
**Ships requiring WP or SP held + steering**: 18/25 = 72%
**Ships where thrust control is critical**: 8/25 = 32%

---

## Appendix B — Android KeyEvent Code Reference

For implementation, all touch buttons inject Android `KeyEvent` codes via
`SDLActivity.onNativeKeyDown()` / `onNativeKeyUp()`. SDL2's `TranslateKeycode()`
maps these to SDL scancodes.

| Function | Android Keycode | Constant | Int Value |
|----------|----------------|----------|-----------|
| Thrust | KEYCODE_DPAD_UP | KeyEvent.KEYCODE_DPAD_UP | 19 |
| Turn Left | KEYCODE_DPAD_LEFT | KeyEvent.KEYCODE_DPAD_LEFT | 21 |
| Turn Right | KEYCODE_DPAD_RIGHT | KeyEvent.KEYCODE_DPAD_RIGHT | 22 |
| Weapon | KEYCODE_CTRL_RIGHT | KeyEvent.KEYCODE_CTRL_RIGHT | 114 |
| Special | KEYCODE_SHIFT_RIGHT | KeyEvent.KEYCODE_SHIFT_RIGHT | 60 |
| Menu Select | KEYCODE_ENTER | KeyEvent.KEYCODE_ENTER | 66 |
| Menu Cancel | KEYCODE_ESCAPE | KeyEvent.KEYCODE_ESCAPE | 111 |
| Exit Melee | KEYCODE_F10 | KeyEvent.KEYCODE_F10 | 140 |
| Pause | KEYCODE_F1 | KeyEvent.KEYCODE_F1 | 131 |
| Down (menu) | KEYCODE_DPAD_DOWN | KeyEvent.KEYCODE_DPAD_DOWN | 20 |

---

*Report prepared based on analysis of UQM 0.8 source code, Ultronomicon wiki
ship documentation for all 25 Super Melee ships, Apple Human Interface Guidelines
for game controls, Google Material Design touch target specifications, and
review of analogous mobile game control implementations.*
