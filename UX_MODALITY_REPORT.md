# UQM Android — Touch Overlay Modality Report

**Author:** UX Researcher  
**Date:** 2026-02-12  
**Purpose:** Define distinct overlay profiles for context-aware touch controls  

---

## Executive Summary

Source code analysis identified **41 distinct input states** across the UQM codebase. However, these collapse into **5 actionable overlay profiles** based on shared input patterns. This report defines each profile, what controls should be visible, and where mode transitions occur in the code.

---

## Part 1: Overlay Profiles

### Profile 1: COMBAT

**When active:** Real-time ship-to-ship battle  
**Game modes:** Battle (#3), Planet Lander (#21)  
**Frequency:** High — this is the core gameplay loop  

| Control | Key Sent | Visible | Notes |
|---------|----------|---------|-------|
| Joystick (6-sector) | UP/DOWN/LEFT/RIGHT | Yes | Full thrust+turn with diagonals |
| WEAPON | RCTRL | Yes, large | Primary fire |
| SPECIAL | RSHIFT | Yes, large | Ship special ability / Lander takeoff |
| Combo seam | RSHIFT+RCTRL | Yes (hidden) | Between WEAPON and SPECIAL |
| PAUSE | F1 | Yes, small | Upper corner |
| EXIT | F10 | Yes, small | Upper corner |

**Joystick mode:** 6-sector with radial gating (inner cardinal, outer diagonal)

---

### Profile 2: MENU NAVIGATION

**When active:** Any menu/list/dialog with discrete cursor movement  
**Game modes:** Main Menu (#1), Settings (#2), Super Melee Main (#4), Ship Picker (#6), Load Team (#7), Confirm (#8), Connecting (#11), Net Config (#12), Planet Orbit Menu (#18), Planet Scan (#19), Landing Site Picker (#20), Encounter (#22), Story Ship Pick (#23), Dialogue Responses (#25), Starbase (#28), Outfit (#29), Shipyard (#30), Roster (#31), Cargo (#32), Devices (#33), Game Options (#34), Save/Load (#35), In-Game Settings (#36), Exit Confirm (#37), Popup (#38)  
**Frequency:** Very high — most non-combat time  

| Control | Key Sent | Visible | Notes |
|---------|----------|---------|-------|
| Joystick (4-sector) | UP/DOWN/LEFT/RIGHT | Yes | Cardinal-only, no diagonals needed |
| SELECT | ENTER | Yes, large | Confirm/activate — replaces WEAPON position |
| CANCEL | SPACE | Yes, large | Back/exit — replaces SPECIAL position |
| PAUSE | F1 | Yes, small | Upper corner |
| EXIT | F10 | Yes, small | Upper corner |

**Joystick mode:** 4-sector cardinal only (no radial gating)

**Design rationale:**  
- SELECT replaces WEAPON (same position, same thumb) — natural "do the thing" button
- CANCEL replaces SPECIAL (same position, same thumb) — natural "go back" button
- Menu keys SELECT (Enter/RCtrl) and CANCEL (Space/RShift/Escape) are already bound to the same physical keys as WEAPON and SPECIAL respectively in the game config, so using the same button positions creates muscle memory continuity

---

### Profile 3: FLEET EDIT (Super Melee Team Building)

**When active:** Editing ship roster in Super Melee  
**Game modes:** Fleet Edit (#5)  
**Frequency:** Medium — before each Super Melee match  

| Control | Key Sent | Visible | Notes |
|---------|----------|---------|-------|
| Joystick (4-sector) | UP/DOWN/LEFT/RIGHT | Yes | Navigate ship grid |
| SELECT | ENTER | Yes, large | Add ship (opens picker) / edit name |
| CANCEL | SPACE | Yes, large | Exit back to melee menu |
| DELETE | FORWARD_DEL | Yes, medium | Remove selected ship — **unique to this mode** |
| PAUSE | F1 | Yes, small | Upper corner |
| EXIT | F10 | Yes, small | Upper corner |

**Key difference from Profile 2:** The DELETE button appears. This is the only profile that needs it.

**DELETE button placement:** Below CANCEL, or as a smaller button adjacent to it. Must be intentional to press (not accidental) since ship removal has no undo.

---

### Profile 4: FLIGHT (Hyperspace / Solar System)

**When active:** Piloting the flagship through space  
**Game modes:** HyperSpace Flight (#14), Interplanetary Flight (#16)  
**Frequency:** High — significant portion of story mode  

| Control | Key Sent | Visible | Notes |
|---------|----------|---------|-------|
| Joystick (4-sector) | UP/LEFT/RIGHT | Yes | Thrust + steer only; no DOWN used |
| CANCEL | SPACE | Yes, large | Opens pause menu (Starmap, Cargo, etc.) |
| PAUSE | F1 | Yes, small | Upper corner |
| EXIT | F10 | Yes, small | Upper corner |

**Joystick mode:** 3-sector (up/left/right only — DOWN is unused in flight). Could use standard 4-sector since DOWN is harmless, just wasted angular range.

**Design rationale:**  
- No WEAPON or SPECIAL during flight — flagship doesn't fire
- Only large button is CANCEL (opens game menu)
- Clean, minimal overlay lets the player enjoy the scenery

---

### Profile 5: DIALOGUE / CUTSCENE

**When active:** Alien conversation, video playback, intro, credits  
**Game modes:** Talk Segue (#24), Dialogue Responses (#25), Conversation Summary (#26), Last Replay (#27), Intro (#39), Video (#40), Credits (#41)  
**Frequency:** Medium — story conversations  

| Control | Key Sent | Visible | Notes |
|---------|----------|---------|-------|
| Joystick (4-sector) | UP/DOWN/LEFT/RIGHT | Yes | Scroll responses, rewind/FF speech |
| SELECT | ENTER | Yes, large | Choose response / skip slide / dismiss |
| CANCEL | SPACE | Yes, large | Skip speech / open summary / exit |
| PAUSE | F1 | Yes, small | Upper corner |
| EXIT | F10 | Yes, small | Upper corner |

**Note:** Functionally identical to Profile 2 (Menu Navigation). Could merge with Profile 2 if we want fewer profiles. Kept separate in case we want different visual styling (e.g., more transparent overlay during dialogue to avoid covering alien art).

---

## Part 2: Profile Comparison Matrix

| Feature | COMBAT | MENU | FLEET EDIT | FLIGHT | DIALOGUE |
|---------|--------|------|------------|--------|----------|
| Joystick sectors | 6 | 4 | 4 | 3-4 | 4 |
| Radial gating | Yes | No | No | No | No |
| Button 1 (top-right) | SPECIAL | CANCEL | CANCEL | CANCEL | CANCEL |
| Button 2 (bot-right) | WEAPON | SELECT | SELECT | — | SELECT |
| DELETE button | No | No | Yes | No | No |
| Combo seam | Yes | No | No | No | No |
| Button labels | SPECIAL/WEAPON | CANCEL/SELECT | CANCEL/SELECT/DELETE | CANCEL | CANCEL/SELECT |

---

## Part 3: Recommended Simplification

Since Profiles 2, 3, and 5 share the same button layout (SELECT + CANCEL + joystick), and Profile 3 just adds DELETE, the practical implementation is:

### **3 overlay layouts to implement:**

| Layout | Profiles | Description |
|--------|----------|-------------|
| **A: Combat** | Profile 1 | SPECIAL + WEAPON + combo seam + 6-sector joystick |
| **B: Menu** | Profiles 2, 3, 5 | SELECT + CANCEL + optional DELETE + 4-sector joystick |
| **C: Flight** | Profile 4 | CANCEL only + 4-sector joystick |

The DELETE button in layout B is toggled by a sub-flag (fleet edit mode = true/false).

---

## Part 4: Mode Transition Points (for JNI callbacks)

These are the C source locations where the overlay mode should change. Each needs a single-line JNI callback insertion.

### Transitions to COMBAT (Layout A)
| Location | Function | Trigger |
|----------|----------|---------|
| `battle.c` | `DoBattle` entry | Battle begins |
| `lander.c` | `DoPlanetSide` entry | Lander deployed |

### Transitions to MENU (Layout B, DELETE off)
| Location | Function | Trigger |
|----------|----------|---------|
| `restart.c` | `DoRestart` entry | Main menu loaded |
| `melee.c` | `DoMelee` entry | Super Melee main screen |
| `buildpick.c` | `DoPickShip` entry | Ship picker opened |
| `pickmele.c` | `DoGetMelee` entry | In-battle ship selection (uses flight keys but is a menu) |
| `comm.c` | `DoCommunication` entry | Dialogue started |
| All `DoMenuChooser` contexts | Various | Any standard menu |

### Transitions to MENU (Layout B, DELETE on)
| Location | Function | Trigger |
|----------|----------|---------|
| `melee.c` | `DoEdit` entry | Fleet editor entered |
| `getchar.c` | `DoTextEntry` entry | Text entry started (uses DELETE for character delete) |

### Transitions to FLIGHT (Layout C)
| Location | Function | Trigger |
|----------|----------|---------|
| `hyper.c` | Ship flight active | HyperSpace flight |
| `solarsys.c` | `DoIpFlight` entry | Interplanetary flight |

---

## Part 5: Special Cases & Edge Cases

### In-Battle Ship Pick (#9)
Uses **flight keys** (LEFT/RIGHT/UP/DOWN + WEAPON to confirm) rather than menu keys. This should stay in COMBAT layout even though it's technically a selection screen — the player's muscle memory is already on the combat buttons.

### Starmap (#17)
Uses ZOOM_IN/ZOOM_OUT (PageUp/PageDown) and SEARCH (/). These are rare enough that we can skip dedicated buttons. Zoom could be mapped to pinch gesture in a future iteration.

### Fuel Buying (#29)
Uses PAGE_UP/PAGE_DOWN for buying/selling fuel in bulk. Again, rare enough to skip dedicated buttons — player can tap UP/DOWN repeatedly.

### Text Entry (#13)
On desktop, uses full keyboard + joystick alphabet cycling. On mobile, we could potentially intercept text entry mode and show the Android soft keyboard instead. This is a separate feature request.

### Lander vs Combat
Both use flight keys. The lander has no SPECIAL (it uses ESCAPE/SPECIAL to take off). The combat overlay works fine — SPECIAL just triggers lander takeoff, which is the desired behavior.

---

## Part 6: Recommendations

1. **Start with 3 layouts** (Combat / Menu / Flight) — covers 95% of gameplay
2. **DELETE sub-flag** for fleet edit and text entry — simple visibility toggle
3. **JNI callback approach** — insert `UQM_SetOverlayMode(mode)` at ~12 transition points in the C source
4. **Merge Profiles 2 and 5** — dialogue is functionally identical to menu navigation
5. **Future: pinch-to-zoom** for starmap — not critical for MVP
6. **Future: soft keyboard** for text entry — nice-to-have, not blocking

---

## Appendix: Full Game Mode → Profile Mapping

| # | Game Mode | Profile | Layout |
|---|-----------|---------|--------|
| 1 | Main Menu | Menu | B |
| 2 | Settings Menu | Menu | B |
| 3 | Combat (Battle) | Combat | A |
| 4 | Super Melee Main | Menu | B |
| 5 | Fleet Edit | Menu+DELETE | B+flag |
| 6 | Ship Picker | Menu | B |
| 7 | Load Team | Menu | B |
| 8 | Confirm Settings | Menu | B |
| 9 | In-Battle Ship Pick | Combat | A |
| 10 | Game Over | Menu | B |
| 11 | Connecting (Net) | Menu | B |
| 12 | Net Config | Menu | B |
| 13 | Text Entry | Menu+DELETE | B+flag |
| 14 | HyperSpace Flight | Flight | C |
| 15 | Hyper/Solar Menu | Menu | B |
| 16 | IP Flight | Flight | C |
| 17 | Starmap | Menu | B |
| 18 | Planet Orbit Menu | Menu | B |
| 19 | Planet Scan | Menu | B |
| 20 | Landing Picker | Menu | B |
| 21 | Lander | Combat | A |
| 22 | Encounter | Menu | B |
| 23 | Story Ship Pick | Menu | B |
| 24 | Talk Segue | Menu | B |
| 25 | Dialogue Responses | Menu | B |
| 26 | Conv. Summary | Menu | B |
| 27 | Last Replay | Menu | B |
| 28 | Starbase Menu | Menu | B |
| 29 | Outfit | Menu | B |
| 30 | Shipyard | Menu | B |
| 31 | Roster | Menu | B |
| 32 | Cargo | Menu | B |
| 33 | Devices | Menu | B |
| 34 | Game Options | Menu | B |
| 35 | Save/Load | Menu | B |
| 36 | In-Game Settings | Menu | B |
| 37 | Exit Confirm | Menu | B |
| 38 | Popup | Menu | B |
| 39 | Intro | Menu | B |
| 40 | Video | Menu | B |
| 41 | Credits | Menu | B |
