package org.uqm.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import org.libsdl.app.SDLActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Transparent touch overlay for Ur-Quan Masters.
 *
 * Supports three overlay modes:
 *   COMBAT  – 4-finger claw: THRUST + TURN L/R (thumbs) + WEAPON/SPECIAL triggers (index fingers)
 *   MENU    – CANCEL + SELECT + optional DELETE + 4-sector joystick
 *   FLIGHT  – CANCEL only + 4-sector joystick
 *
 * Controls map to SDL keyboard events so the existing VControl input
 * system handles them identically to physical keys.
 */
public class TouchOverlayView extends View {

    // ── Overlay mode ────────────────────────────────────────────
    public enum OverlayMode { COMBAT, MENU, FLIGHT }

    private OverlayMode currentMode = OverlayMode.MENU;
    private boolean deleteVisible = false;

    // Per-mode flags (updated in applyModeConfig)
    private boolean comboEnabled = false;   // combo-assist seam active
    private boolean useSixSectors = false;  // 6-sector joystick vs 4-sector
    private boolean useRadialGating = false;// inner cardinal-only ring

    // ── Opacity / sizing constants ──────────────────────────────
    private static final int ALPHA_NORMAL = 60;
    private static final int ALPHA_PRESSED = 120;
    private static final float JOY_BASE_RADIUS_FRAC = 0.15f;
    private static final float JOY_DEADZONE_FRAC = 0.15f;
    private static final float JOY_CARDINAL_RING_FRAC = 0.55f;

    private static final float JOY_ZONE_LEFT_WIDTH_FRAC = 0.40f;
    private static final float JOY_ZONE_BOTTOM_HEIGHT_FRAC = 0.60f;

    private static final float RIGHT_PANEL_HEIGHT_FRAC = 0.58f;
    private static final float STACK_SEAM_FRAC_OF_PANEL_HEIGHT = 0.04f;

    // ── Combat claw layout constants ────────────────────────────
    private static final int TRIGGER_ALPHA_NORMAL = 30;
    private static final int TRIGGER_ALPHA_PRESSED = 80;
    private static final float TRIGGER_WIDTH_FRAC = 0.20f;
    private static final float TRIGGER_HEIGHT_FRAC = 0.14f;
    private static final float THRUST_WIDTH_FRAC = 0.20f;
    private static final float THRUST_HEIGHT_FRAC = 0.25f;
    private static final float TURN_WIDTH_FRAC = 0.12f;
    private static final float TURN_HEIGHT_FRAC = 0.22f;
    private static final float TURN_GAP_DP = 6f;

    // ── Paint objects ───────────────────────────────────────────
    private final Paint paintJoystick = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintButton = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    // ── Direction state ─────────────────────────────────────────
    private boolean dpadUp, dpadDown, dpadLeft, dpadRight;

    // ── Floating joystick state ─────────────────────────────────
    private float joyCenterX, joyCenterY, joyRadius, joyKnobX, joyKnobY;
    private int joyPointerId = -1;
    private int joySector = SECTOR_NONE;
    private int pendingJoySector = SECTOR_NONE;
    private int pendingJoySectorFrames = 0;

    // ── Sector constants ────────────────────────────────────────
    private static final int SECTOR_NONE     = 0;
    private static final int SECTOR_UP       = 1;
    private static final int SECTOR_RIGHT    = 2;
    private static final int SECTOR_DOWN     = 3;
    private static final int SECTOR_LEFT     = 4;
    private static final int SECTOR_UP_RIGHT = 5;
    private static final int SECTOR_UP_LEFT  = 6;

    // ── Action buttons ──────────────────────────────────────────
    //  [0] primary   = CANCEL (menu, flight) — hidden in combat
    //  [1] secondary = SELECT (menu) / hidden (flight, combat)
    //  [2] PAUSE     – always visible
    //  [3] EXIT      – always visible
    //  [4] DELETE    – visible only in MENU mode when deleteVisible flag is set
    //  [5..9] COMBAT-only: discrete claw buttons
    private static final int BTN_PRIMARY       = 0;
    private static final int BTN_SECONDARY     = 1;
    private static final int BTN_PAUSE         = 2;
    private static final int BTN_EXIT          = 3;
    private static final int BTN_DELETE        = 4;
    private static final int BTN_THRUST        = 5;
    private static final int BTN_TURN_LEFT     = 6;
    private static final int BTN_TURN_RIGHT    = 7;
    private static final int BTN_WEAPON_TRIG   = 8;
    private static final int BTN_SPECIAL_TRIG  = 9;
    private static final int NUM_BUTTONS       = 10;

    private final ActionButton[] buttons = new ActionButton[NUM_BUTTONS];

    // Combo-assist seam (combat mode only)
    private final RectF comboAssistRect = new RectF();
    private boolean comboPressed = false;
    private int comboPointerId = -1;

    // ── SDL keycodes we inject ──────────────────────────────────
    // These are ANDROID keycodes; SDL2's JNI layer maps them to SDL scancodes.
    private static final int AKEY_UP     = KeyEvent.KEYCODE_DPAD_UP;         // 19
    private static final int AKEY_DOWN   = KeyEvent.KEYCODE_DPAD_DOWN;       // 20
    private static final int AKEY_LEFT   = KeyEvent.KEYCODE_DPAD_LEFT;       // 21
    private static final int AKEY_RIGHT  = KeyEvent.KEYCODE_DPAD_RIGHT;      // 22
    private static final int AKEY_RETURN = KeyEvent.KEYCODE_ENTER;           // 66
    private static final int AKEY_SPACE  = KeyEvent.KEYCODE_SPACE;           // 62
    private static final int AKEY_ESCAPE = KeyEvent.KEYCODE_ESCAPE;          // 111
    private static final int AKEY_RSHIFT = KeyEvent.KEYCODE_SHIFT_RIGHT;     // 60
    private static final int AKEY_RCTRL  = KeyEvent.KEYCODE_CTRL_RIGHT;      // 114
    private static final int AKEY_F1     = KeyEvent.KEYCODE_F1;              // 131
    private static final int AKEY_F10    = KeyEvent.KEYCODE_F10;             // 140
    private static final int AKEY_DEL    = KeyEvent.KEYCODE_FORWARD_DEL;     // 112

    private final Map<Integer, Integer> keyHoldCounts = new HashMap<>();

    // ── Auto-fade timer ─────────────────────────────────────────
    private long lastTouchTime = 0;
    private static final long FADE_DELAY_MS = 4000;
    private static final long FADE_DURATION_MS = 1000;

    // ═════════════════════════════════════════════════════════════
    //  ActionButton helper
    // ═════════════════════════════════════════════════════════════
    static class ActionButton {
        final RectF bounds = new RectF();
        String label;
        int androidKeycode;
        int secondaryKeycode;  // -1 if unused; sent alongside primary
        int colorR, colorG, colorB;
        int hapticType;
        boolean pressed;
        int pointerId = -1;
        boolean visible = true; // mode-dependent visibility

        ActionButton(String label, int keycode, int colorR, int colorG, int colorB,
                     int hapticType) {
            this(label, keycode, -1, colorR, colorG, colorB, hapticType);
        }

        ActionButton(String label, int keycode, int secondaryKeycode,
                     int colorR, int colorG, int colorB, int hapticType) {
            this.label = label;
            this.androidKeycode = keycode;
            this.secondaryKeycode = secondaryKeycode;
            this.colorR = colorR;
            this.colorG = colorG;
            this.colorB = colorB;
            this.hapticType = hapticType;
        }

        void configure(String label, int keycode, int secondaryKeycode,
                       int colorR, int colorG, int colorB) {
            this.label = label;
            this.androidKeycode = keycode;
            this.secondaryKeycode = secondaryKeycode;
            this.colorR = colorR;
            this.colorG = colorG;
            this.colorB = colorB;
        }

        boolean contains(float x, float y, float hitPadding) {
            return visible
                    && x >= bounds.left - hitPadding
                    && x <= bounds.right + hitPadding
                    && y >= bounds.top - hitPadding
                    && y <= bounds.bottom + hitPadding;
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  Constructors & init
    // ═════════════════════════════════════════════════════════════
    public TouchOverlayView(Context context) {
        super(context);
        init();
    }

    public TouchOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(false);

        paintJoystick.setStyle(Paint.Style.FILL);
        paintButton.setStyle(Paint.Style.FILL);
        paintText.setColor(Color.WHITE);
        paintText.setTextAlign(Paint.Align.CENTER);

        // Create buttons with placeholder values — applyModeConfig will set them
        buttons[BTN_PRIMARY]   = new ActionButton("",  0, 0, 0, 0, HapticFeedbackConstants.VIRTUAL_KEY);
        buttons[BTN_SECONDARY] = new ActionButton("",  0, 0, 0, 0, HapticFeedbackConstants.VIRTUAL_KEY);
        buttons[BTN_PAUSE]     = new ActionButton("PAUSE", AKEY_F1,  120, 120, 120, HapticFeedbackConstants.LONG_PRESS);
        buttons[BTN_EXIT]      = new ActionButton("EXIT",  AKEY_F10, 120, 120, 120, HapticFeedbackConstants.LONG_PRESS);
        buttons[BTN_DELETE]    = new ActionButton("DELETE", AKEY_DEL, 180, 100, 40,  HapticFeedbackConstants.VIRTUAL_KEY);
        buttons[BTN_DELETE].visible = false;

        // Combat claw buttons (hidden by default — enabled in COMBAT mode)
        buttons[BTN_THRUST]       = new ActionButton("THRUST", AKEY_UP,    51, 180, 51,  HapticFeedbackConstants.KEYBOARD_TAP);
        buttons[BTN_THRUST].visible = false;
        buttons[BTN_TURN_LEFT]    = new ActionButton("\u25C4",  AKEY_LEFT,  180, 180, 60, HapticFeedbackConstants.CLOCK_TICK);
        buttons[BTN_TURN_LEFT].visible = false;
        buttons[BTN_TURN_RIGHT]   = new ActionButton("\u25BA",  AKEY_RIGHT, 180, 180, 60, HapticFeedbackConstants.CLOCK_TICK);
        buttons[BTN_TURN_RIGHT].visible = false;
        buttons[BTN_WEAPON_TRIG]  = new ActionButton("WPN",  AKEY_RCTRL,  204, 51, 51,  HapticFeedbackConstants.VIRTUAL_KEY);
        buttons[BTN_WEAPON_TRIG].visible = false;
        buttons[BTN_SPECIAL_TRIG] = new ActionButton("SPC",  AKEY_RSHIFT, 51, 102, 204, HapticFeedbackConstants.LONG_PRESS);
        buttons[BTN_SPECIAL_TRIG].visible = false;

        joySector = SECTOR_NONE;
        pendingJoySector = SECTOR_NONE;
        pendingJoySectorFrames = 0;

        applyModeConfig();
    }

    // ═════════════════════════════════════════════════════════════
    //  Mode switching — called from UQMActivity (UI thread)
    // ═════════════════════════════════════════════════════════════
    /**
     * Switch overlay layout.
     * @param mode      COMBAT / MENU / FLIGHT
     * @param showDelete  true to show the DELETE button (only relevant in MENU)
     */
    public void setMode(OverlayMode mode, boolean showDelete) {
        if (mode == currentMode && showDelete == deleteVisible) {
            return; // no change
        }

        // Release everything before reconfiguring
        releaseAll();

        currentMode = mode;
        deleteVisible = showDelete;
        applyModeConfig();

        // Re-layout and redraw
        if (getWidth() > 0 && getHeight() > 0) {
            layoutControls(getWidth(), getHeight());
        }
        invalidate();
    }

    /**
     * Configure button labels, keycodes, visibility, and mode flags
     * based on the current overlay mode.
     */
    private void applyModeConfig() {
        // Reset all mode-dependent buttons to hidden
        for (int i = 0; i < NUM_BUTTONS; i++) {
            if (buttons[i] != null && i != BTN_PAUSE && i != BTN_EXIT) {
                buttons[i].visible = false;
            }
        }

        switch (currentMode) {
            case COMBAT:
                // 4-finger claw: discrete buttons, no joystick
                buttons[BTN_THRUST].visible       = true;
                buttons[BTN_TURN_LEFT].visible    = true;
                buttons[BTN_TURN_RIGHT].visible   = true;
                buttons[BTN_WEAPON_TRIG].visible  = true;
                buttons[BTN_SPECIAL_TRIG].visible = true;
                comboEnabled    = false;
                useSixSectors   = false;
                useRadialGating = false;
                break;

            case MENU:
                // CANCEL (top) + SELECT (bottom) + optional DELETE
                buttons[BTN_PRIMARY].configure(
                        "CANCEL", AKEY_SPACE, -1, 100, 120, 180);
                buttons[BTN_SECONDARY].configure(
                        "SELECT", AKEY_RETURN, -1, 51, 153, 51);
                buttons[BTN_PRIMARY].visible   = true;
                buttons[BTN_SECONDARY].visible = true;
                buttons[BTN_DELETE].visible    = deleteVisible;
                comboEnabled    = false;
                useSixSectors   = false;
                useRadialGating = false;
                break;

            case FLIGHT:
                // Single CANCEL button (full panel height)
                buttons[BTN_PRIMARY].configure(
                        "CANCEL", AKEY_ESCAPE, -1, 100, 120, 180);
                buttons[BTN_PRIMARY].visible   = true;
                buttons[BTN_SECONDARY].visible = false;
                buttons[BTN_DELETE].visible    = false;
                comboEnabled    = false;
                useSixSectors   = false;
                useRadialGating = false;
                break;
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  Layout
    // ═════════════════════════════════════════════════════════════
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutControls(w, h);
    }

    private void layoutControls(int w, int h) {
        if (currentMode == OverlayMode.COMBAT) {
            layoutCombatControls(w, h);
            return;
        }

        joyRadius = h * JOY_BASE_RADIUS_FRAC;

        float rightEdgeMargin = dpToPx(4f);
        float bottomMargin = dpToPx(12f);
        float panelHeight = h * RIGHT_PANEL_HEIGHT_FRAC;
        float panelBottom = h - bottomMargin;
        float panelTop = panelBottom - panelHeight;

        float seam = Math.max(dpToPx(8f), panelHeight * STACK_SEAM_FRAC_OF_PANEL_HEIGHT);

        // Right edge for the button stack
        float stackRight = w - rightEdgeMargin;

        if (currentMode == OverlayMode.FLIGHT) {
            // ── FLIGHT: single CANCEL button spanning the full panel ──
            float squareSide = Math.min(panelHeight * 0.45f, dpToPx(160f));
            float stackX = stackRight - squareSide;
            float stackTop = panelTop + (panelHeight - squareSide) * 0.5f;
            buttons[BTN_PRIMARY].bounds.set(stackX, stackTop,
                    stackX + squareSide, stackTop + squareSide);
            // Secondary, combo, delete — not used
            buttons[BTN_SECONDARY].bounds.setEmpty();
            buttons[BTN_DELETE].bounds.setEmpty();
            comboAssistRect.setEmpty();

        } else {
            // ── MENU: two stacked buttons ──
            float squareSide = (panelHeight - seam) * 0.5f;
            squareSide = Math.min(squareSide, dpToPx(160f));
            float stackX = stackRight - squareSide;
            float stackTop = panelTop + (panelHeight - (2f * squareSide + seam)) * 0.5f;

            buttons[BTN_PRIMARY].bounds.set(
                    stackX, stackTop, stackX + squareSide, stackTop + squareSide);
            buttons[BTN_SECONDARY].bounds.set(
                    stackX, stackTop + squareSide + seam,
                    stackX + squareSide, stackTop + 2f * squareSide + seam);

            if (comboEnabled) {
                // Combo-assist seam between primary and secondary
                float seamCenterY = buttons[BTN_PRIMARY].bounds.bottom + seam * 0.5f;
                float comboHeight = Math.max(dpToPx(10f),
                        Math.min(dpToPx(14f), seam + dpToPx(6f)));
                float comboWidth = squareSide * 0.80f;
                float comboLeft = stackRight - comboWidth;
                comboAssistRect.set(comboLeft, seamCenterY - comboHeight * 0.5f,
                        comboLeft + comboWidth, seamCenterY + comboHeight * 0.5f);
            } else {
                comboAssistRect.setEmpty();
            }

            // DELETE button — small, to the left of the main stack
            if (currentMode == OverlayMode.MENU && deleteVisible) {
                float delSize = squareSide * 0.50f;
                float delGap = dpToPx(8f);
                float delRight = stackX - delGap;
                float delLeft = delRight - delSize;
                // Vertically centered between primary and secondary
                float delCenterY = (buttons[BTN_PRIMARY].bounds.bottom
                        + buttons[BTN_SECONDARY].bounds.top) * 0.5f;
                buttons[BTN_DELETE].bounds.set(
                        delLeft, delCenterY - delSize * 0.5f,
                        delLeft + delSize, delCenterY + delSize * 0.5f);
            } else {
                buttons[BTN_DELETE].bounds.setEmpty();
            }
        }

        // ── System buttons — upper-right corner (always) ──
        float systemSize = Math.max(dpToPx(34f), h * 0.055f);
        float gap = dpToPx(8f);
        float margin = dpToPx(8f);
        float x1 = w - margin - systemSize;
        float x0 = x1 - gap - systemSize;
        float y0 = margin;

        buttons[BTN_PAUSE].bounds.set(x0, y0, x0 + systemSize, y0 + systemSize);
        buttons[BTN_EXIT].bounds.set(x1, y0, x1 + systemSize, y0 + systemSize);
    }

    private void layoutCombatControls(int w, int h) {
        // No joystick in combat mode
        joyRadius = 0;

        float safeTop = dpToPx(4f);
        float safeLeft = dpToPx(4f);
        float safeRight = dpToPx(4f);
        float safeBottom = dpToPx(12f);

        // ── Index-finger trigger zones (top corners, flush to edge) ──
        float trigW = Math.max(w * TRIGGER_WIDTH_FRAC, dpToPx(90f));
        float trigH = Math.max(h * TRIGGER_HEIGHT_FRAC, dpToPx(65f));

        buttons[BTN_WEAPON_TRIG].bounds.set(
                safeLeft, safeTop,
                safeLeft + trigW, safeTop + trigH);
        buttons[BTN_SPECIAL_TRIG].bounds.set(
                w - safeRight - trigW, safeTop,
                w - safeRight, safeTop + trigH);

        // ── Thumb buttons (bottom) ──
        float thrustW = Math.max(w * THRUST_WIDTH_FRAC, dpToPx(80f));
        float thrustH = Math.max(h * THRUST_HEIGHT_FRAC, dpToPx(80f));
        buttons[BTN_THRUST].bounds.set(
                safeLeft, h - safeBottom - thrustH,
                safeLeft + thrustW, h - safeBottom);

        float turnW = Math.max(w * TURN_WIDTH_FRAC, dpToPx(60f));
        float turnH = Math.max(h * TURN_HEIGHT_FRAC, dpToPx(80f));
        float turnGap = dpToPx(TURN_GAP_DP);
        float turnRight = w - safeRight;
        float turnBottom = h - safeBottom;

        buttons[BTN_TURN_LEFT].bounds.set(
                turnRight - 2f * turnW - turnGap, turnBottom - turnH,
                turnRight - turnW - turnGap, turnBottom);
        buttons[BTN_TURN_RIGHT].bounds.set(
                turnRight - turnW, turnBottom - turnH,
                turnRight, turnBottom);

        // ── System buttons – upper center (between triggers) ──
        float systemSize = Math.max(dpToPx(34f), h * 0.055f);
        float sysGap = dpToPx(8f);
        float sysCenterX = w * 0.5f;
        float sysY = dpToPx(8f);
        buttons[BTN_PAUSE].bounds.set(
                sysCenterX - systemSize - sysGap * 0.5f, sysY,
                sysCenterX - sysGap * 0.5f, sysY + systemSize);
        buttons[BTN_EXIT].bounds.set(
                sysCenterX + sysGap * 0.5f, sysY,
                sysCenterX + sysGap * 0.5f + systemSize, sysY + systemSize);

        // Clear non-combat button bounds
        buttons[BTN_PRIMARY].bounds.setEmpty();
        buttons[BTN_SECONDARY].bounds.setEmpty();
        buttons[BTN_DELETE].bounds.setEmpty();
        comboAssistRect.setEmpty();
    }

    // ═════════════════════════════════════════════════════════════
    //  Drawing
    // ═════════════════════════════════════════════════════════════
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate fade alpha
        float alphaScale = 1.0f;
        long elapsed = SystemClock.uptimeMillis() - lastTouchTime;
        if (lastTouchTime > 0 && elapsed > FADE_DELAY_MS) {
            float fadeFrac = (elapsed - FADE_DELAY_MS) / (float) FADE_DURATION_MS;
            alphaScale = Math.max(0.3f, 1.0f - fadeFrac);
        }

        int baseAlpha = (int) (ALPHA_NORMAL * alphaScale);
        int pressedAlpha = (int) (ALPHA_PRESSED * alphaScale);

        // ── Floating joystick ──
        if (joyPointerId != -1) {
            paintJoystick.setColor(Color.argb(baseAlpha, 80, 80, 80));
            canvas.drawCircle(joyCenterX, joyCenterY, joyRadius, paintJoystick);

            float knobRadius = joyRadius * 0.38f;
            paintJoystick.setColor(Color.argb(pressedAlpha, 220, 220, 220));
            canvas.drawCircle(joyKnobX, joyKnobY, knobRadius, paintJoystick);
        }

        // ── Combo-assist seam (combat only) ──
        if (comboEnabled && comboPressed) {
            paintButton.setColor(Color.argb(pressedAlpha, 180, 180, 220));
            canvas.drawRoundRect(comboAssistRect, dpToPx(8f), dpToPx(8f), paintButton);
        }

        // ── Action buttons ──
        for (ActionButton btn : buttons) {
            if (!btn.visible) continue;

            int alpha;
            if (btn == buttons[BTN_WEAPON_TRIG] || btn == buttons[BTN_SPECIAL_TRIG]) {
                // Triggers: low-opacity (blind-operated, proprioceptive zones)
                alpha = btn.pressed ? (int) (TRIGGER_ALPHA_PRESSED * alphaScale)
                                    : (int) (TRIGGER_ALPHA_NORMAL * alphaScale);
            } else {
                alpha = btn.pressed ? pressedAlpha : baseAlpha;
            }
            if (comboEnabled && comboPressed
                    && (btn == buttons[BTN_PRIMARY] || btn == buttons[BTN_SECONDARY])) {
                alpha = pressedAlpha;
            }

            paintButton.setColor(Color.argb(alpha, btn.colorR, btn.colorG, btn.colorB));
            float corner = dpToPx(10f);
            canvas.drawRoundRect(btn.bounds, corner, corner, paintButton);

            paintText.setAlpha((int) (220 * alphaScale));

            float buttonHeight = btn.bounds.height();
            float textSize;
            if (btn == buttons[BTN_DELETE]) {
                textSize = Math.min(buttonHeight * 0.26f, dpToPx(12f));
            } else if (buttonHeight <= dpToPx(44f)) {
                textSize = Math.min(buttonHeight * 0.34f, dpToPx(12f));
            } else if (btn == buttons[BTN_PRIMARY] || btn == buttons[BTN_SECONDARY]) {
                textSize = Math.min(buttonHeight * 0.28f, dpToPx(15f));
            } else {
                textSize = Math.min(buttonHeight * 0.36f, dpToPx(16f));
            }
            paintText.setTextSize(textSize);

            float cx = btn.bounds.centerX();
            float cy = btn.bounds.centerY()
                    - ((paintText.descent() + paintText.ascent()) * 0.5f);
            canvas.drawText(btn.label, cx, cy, paintText);
        }

        // Keep refreshing for fade animation
        if (alphaScale > 0.3f) {
            postInvalidateDelayed(50);
        }
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    // ═════════════════════════════════════════════════════════════
    //  Touch handling
    // ═════════════════════════════════════════════════════════════
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastTouchTime = SystemClock.uptimeMillis();

        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                handleTouch(pointerId, event.getX(pointerIndex),
                        event.getY(pointerIndex), true);
                break;

            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int id = event.getPointerId(i);
                    handleTouch(id, event.getX(i), event.getY(i), true);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                handleTouch(pointerId, event.getX(pointerIndex),
                        event.getY(pointerIndex), false);
                break;

            case MotionEvent.ACTION_CANCEL:
                releaseAll();
                break;
        }

        invalidate();
        return true;
    }

    private void handleTouch(int pointerId, float x, float y, boolean down) {
        float hitPadding = dpToPx(6f);

        // ── Joystick ownership / update ─────────────────────────
        if (pointerId == joyPointerId) {
            if (down) {
                updateJoystick(x, y);
            } else {
                releaseJoystick();
            }
            return;
        }

        // Acquire joystick in activation zone
        if (down && joyPointerId == -1 && inJoystickActivationZone(x, y)) {
            startJoystick(pointerId, x, y);
            return;
        }

        // ── Combo-assist (combat only) ──────────────────────────
        if (comboEnabled) {
            if (pointerId == comboPointerId) {
                if (down) {
                    if (comboAssistRect.contains(x, y)) {
                        return;
                    }
                    releaseCombo();
                } else {
                    releaseCombo();
                    return;
                }
            }

            if (down && comboPointerId == -1 && comboAssistRect.contains(x, y)) {
                pressCombo(pointerId);
                return;
            }
        }

        // ── Existing button pointer updates ─────────────────────
        for (ActionButton btn : buttons) {
            if (!btn.visible) continue;
            if (btn.pointerId == pointerId) {
                if (!down) {
                    releaseButton(btn);
                    return;
                }
                if (btn.contains(x, y, hitPadding)) {
                    return; // still on same button
                }
                // slid off — release and allow reacquire
                releaseButton(btn);
            }
        }

        // ── New button press ────────────────────────────────────
        if (down) {
            for (ActionButton btn : buttons) {
                if (!btn.visible) continue;
                if (btn.contains(x, y, hitPadding) && btn.pointerId == -1) {
                    pressButton(btn, pointerId);
                    return;
                }
            }
            return;
        }

        // ── Up-event cleanup for orphaned ownership ─────────────
        if (comboEnabled && pointerId == comboPointerId) {
            releaseCombo();
        }
        if (pointerId == joyPointerId) {
            releaseJoystick();
        }
        for (ActionButton btn : buttons) {
            if (btn.pointerId == pointerId) {
                releaseButton(btn);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  Button press / release
    // ═════════════════════════════════════════════════════════════
    private void pressButton(ActionButton btn, int pointerId) {
        if (btn.pressed || btn.pointerId != -1) return;
        btn.pressed = true;
        btn.pointerId = pointerId;
        pressKey(btn.androidKeycode);
        if (btn.secondaryKeycode != -1) {
            pressKey(btn.secondaryKeycode);
        }
        performHapticFeedback(btn.hapticType);
    }

    private void releaseButton(ActionButton btn) {
        if (!btn.pressed) {
            btn.pointerId = -1;
            return;
        }
        btn.pressed = false;
        btn.pointerId = -1;
        releaseKey(btn.androidKeycode);
        if (btn.secondaryKeycode != -1) {
            releaseKey(btn.secondaryKeycode);
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  Combo-assist seam (combat mode)
    // ═════════════════════════════════════════════════════════════
    private void pressCombo(int pointerId) {
        if (comboPressed) return;
        comboPressed = true;
        comboPointerId = pointerId;
        pressKey(AKEY_RSHIFT);
        pressKey(AKEY_RCTRL);
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    private void releaseCombo() {
        if (!comboPressed) {
            comboPointerId = -1;
            return;
        }
        comboPressed = false;
        comboPointerId = -1;
        releaseKey(AKEY_RSHIFT);
        releaseKey(AKEY_RCTRL);
    }

    // ═════════════════════════════════════════════════════════════
    //  Joystick
    // ═════════════════════════════════════════════════════════════
    private boolean inJoystickActivationZone(float x, float y) {
        if (currentMode == OverlayMode.COMBAT) return false; // no joystick in combat
        float maxX = getWidth() * JOY_ZONE_LEFT_WIDTH_FRAC;
        float minY = getHeight() * (1f - JOY_ZONE_BOTTOM_HEIGHT_FRAC);
        return x <= maxX && y >= minY;
    }

    private void startJoystick(int pointerId, float x, float y) {
        joyPointerId = pointerId;
        joyCenterX = x;
        joyCenterY = y;
        joyKnobX = x;
        joyKnobY = y;
        joySector = SECTOR_NONE;
        pendingJoySector = SECTOR_NONE;
        pendingJoySectorFrames = 0;
        updateDpad(false, false, false, false);
    }

    private void releaseJoystick() {
        joyPointerId = -1;
        joySector = SECTOR_NONE;
        pendingJoySector = SECTOR_NONE;
        pendingJoySectorFrames = 0;
        updateDpad(false, false, false, false);
    }

    private void updateJoystick(float x, float y) {
        float dx = x - joyCenterX;
        float dy = y - joyCenterY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        float clampedDist = Math.min(dist, joyRadius);
        if (dist > 0f) {
            float scale = clampedDist / dist;
            joyKnobX = joyCenterX + dx * scale;
            joyKnobY = joyCenterY + dy * scale;
        } else {
            joyKnobX = joyCenterX;
            joyKnobY = joyCenterY;
        }

        float deadZone = joyRadius * JOY_DEADZONE_FRAC;
        if (dist <= deadZone) {
            applyJoystickSector(SECTOR_NONE);
            return;
        }

        float angle = normalizedAngleDegrees(dx, dy);
        int targetSector;

        if (useSixSectors) {
            // Combat mode: radial gating — inner ring = 4-sector, outer ring = 6-sector
            if (useRadialGating && dist < joyRadius * JOY_CARDINAL_RING_FRAC) {
                targetSector = classifyFourSector(angle);
            } else {
                targetSector = classifySixSector(angle);
            }
        } else {
            // Menu / Flight: always 4-sector, no diagonal
            targetSector = classifyFourSector(angle);
        }

        // Temporal hysteresis: require same candidate for 3 consecutive move frames
        if (targetSector != joySector) {
            if (targetSector == pendingJoySector) {
                pendingJoySectorFrames++;
            } else {
                pendingJoySector = targetSector;
                pendingJoySectorFrames = 1;
            }
            if (pendingJoySectorFrames >= 3) {
                applyJoystickSector(targetSector);
                pendingJoySectorFrames = 0;
            }
        } else {
            pendingJoySector = targetSector;
            pendingJoySectorFrames = 0;
        }
    }

    private float normalizedAngleDegrees(float dx, float dy) {
        // 0° = up, clockwise positive
        float angle = (float) Math.toDegrees(Math.atan2(dx, -dy));
        if (angle < 0f) angle += 360f;
        return angle;
    }

    /** 4-sector: 100/110/40/110° split (narrowed Up, wider Left/Right) */
    private int classifyFourSector(float angle) {
        if (angle >= 310f || angle < 50f)  return SECTOR_UP;
        if (angle < 160f)                  return SECTOR_RIGHT;
        if (angle < 200f)                  return SECTOR_DOWN;
        return SECTOR_LEFT;
    }

    /** 6-sector: 60°/40°/90°/40°/90°/40° split for diagonal thrust-turn */
    private int classifySixSector(float angle) {
        if (angle >= 330f || angle < 30f)  return SECTOR_UP;
        if (angle < 70f)                   return SECTOR_UP_RIGHT;
        if (angle < 160f)                  return SECTOR_RIGHT;
        if (angle < 200f)                  return SECTOR_DOWN;
        if (angle < 290f)                  return SECTOR_LEFT;
        return SECTOR_UP_LEFT;
    }

    private void applyJoystickSector(int sector) {
        joySector = sector;

        boolean up = false, down = false, left = false, right = false;
        switch (sector) {
            case SECTOR_UP:       up = true;                  break;
            case SECTOR_RIGHT:    right = true;               break;
            case SECTOR_DOWN:     down = true;                break;
            case SECTOR_LEFT:     left = true;                break;
            case SECTOR_UP_RIGHT: up = true;  right = true;   break;
            case SECTOR_UP_LEFT:  up = true;  left = true;    break;
        }
        updateDpad(up, down, left, right);
    }

    private void updateDpad(boolean up, boolean down, boolean left, boolean right) {
        if (up != dpadUp) {
            dpadUp = up;
            setKeyState(AKEY_UP, up);
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        }
        if (down != dpadDown) {
            dpadDown = down;
            setKeyState(AKEY_DOWN, down);
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        }
        if (left != dpadLeft) {
            dpadLeft = left;
            setKeyState(AKEY_LEFT, left);
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        }
        if (right != dpadRight) {
            dpadRight = right;
            setKeyState(AKEY_RIGHT, right);
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  Key state management
    // ═════════════════════════════════════════════════════════════
    private void releaseAll() {
        updateDpad(false, false, false, false);
        releaseJoystick();
        releaseCombo();

        for (ActionButton btn : buttons) {
            releaseButton(btn);
        }

        for (Map.Entry<Integer, Integer> entry : keyHoldCounts.entrySet()) {
            int key = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                nativeSendKey(key, false);
            }
        }
        keyHoldCounts.clear();
    }

    private void setKeyState(int keycode, boolean pressed) {
        if (pressed) {
            pressKey(keycode);
        } else {
            releaseKey(keycode);
        }
    }

    private void pressKey(int keycode) {
        int count = keyHoldCounts.containsKey(keycode) ? keyHoldCounts.get(keycode) : 0;
        count += 1;
        keyHoldCounts.put(keycode, count);
        if (count == 1) {
            nativeSendKey(keycode, true);
        }
    }

    private void releaseKey(int keycode) {
        Integer boxed = keyHoldCounts.get(keycode);
        if (boxed == null || boxed <= 0) return;

        int count = boxed - 1;
        if (count <= 0) {
            keyHoldCounts.remove(keycode);
            nativeSendKey(keycode, false);
        } else {
            keyHoldCounts.put(keycode, count);
        }
    }

    /**
     * Inject a key event into SDL's event queue via JNI.
     */
    private void nativeSendKey(int androidKeycode, boolean pressed) {
        if (pressed) {
            SDLActivity.onNativeKeyDown(androidKeycode);
        } else {
            SDLActivity.onNativeKeyUp(androidKeycode);
        }
    }
}
