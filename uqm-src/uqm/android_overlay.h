/*
 * android_overlay.h  –  Touch overlay mode switching (Android only)
 *
 * Provides UQM_SetOverlayMode() which calls into the Java-side
 * TouchOverlayView to switch between Combat / Menu / Flight layouts.
 *
 * On non-Android platforms the macro expands to nothing.
 */

#ifndef UQM_ANDROID_OVERLAY_H
#define UQM_ANDROID_OVERLAY_H

#ifdef ANDROID

#define OVERLAY_MODE_COMBAT  0
#define OVERLAY_MODE_MENU    1
#define OVERLAY_MODE_FLIGHT  2

/*
 * Switch the touch overlay layout.
 *   mode       – OVERLAY_MODE_COMBAT / _MENU / _FLIGHT
 *   deleteFlag – 1 to show the DELETE button (MENU mode only)
 */
void UQM_SetOverlayMode(int mode, int deleteFlag);

#else

/* No-op on non-Android platforms */
#define UQM_SetOverlayMode(mode, deleteFlag)  ((void)0)

#endif /* ANDROID */

#endif /* UQM_ANDROID_OVERLAY_H */
