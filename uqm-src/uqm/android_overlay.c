/*
 * android_overlay.c  –  Touch overlay mode switching via JNI
 *
 * Calls UQMActivity.nativeSetOverlayMode(int, boolean) on the Java side
 * using SDL's JNI helpers.  The Java method posts to the UI thread
 * and reconfigures the TouchOverlayView layout.
 */

#ifdef ANDROID

#include "SDL.h"
#include <jni.h>
#include "android_overlay.h"

static jclass    sActivityClass   = NULL;
static jmethodID sSetOverlayMethod = NULL;
static int       sInitAttempted   = 0;

void
UQM_SetOverlayMode (int mode, int deleteFlag)
{
	JNIEnv *env = (JNIEnv *) SDL_AndroidGetJNIEnv ();
	if (!env)
		return;

	/* One-time lookup of the Java class and method */
	if (!sInitAttempted)
	{
		jobject activity;
		jclass  cls;

		sInitAttempted = 1;

		activity = (jobject) SDL_AndroidGetActivity ();
		if (!activity)
			return;

		cls = (*env)->GetObjectClass (env, activity);
		sActivityClass = (jclass) (*env)->NewGlobalRef (env, cls);
		(*env)->DeleteLocalRef (env, cls);
		(*env)->DeleteLocalRef (env, activity);

		if (sActivityClass)
		{
			sSetOverlayMethod = (*env)->GetStaticMethodID (
					env, sActivityClass,
					"nativeSetOverlayMode", "(IZ)V");
		}
	}

	if (sSetOverlayMethod)
	{
		(*env)->CallStaticVoidMethod (
				env, sActivityClass, sSetOverlayMethod,
				(jint) mode, (jboolean) (deleteFlag ? JNI_TRUE : JNI_FALSE));
	}
}

#endif /* ANDROID */
