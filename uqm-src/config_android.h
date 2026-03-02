/* config_android.h  –  Compile-time configuration for Android builds.
 * Generated for CMake/NDK cross-compilation.
 */

#ifndef CONFIG_ANDROID_H_
#define CONFIG_ANDROID_H_

/* Directories are resolved at runtime via command-line args
 * (--contentdir, --configdir, --savedir) passed from Java.
 * These defaults are fallbacks only.                        */
#define CONTENTDIR "."
#define USERDIR    "."
#define CONFIGDIR  USERDIR
#define MELEEDIR   "${UQM_CONFIG_DIR}/teams/"
#define SAVEDIR    "${UQM_CONFIG_DIR}/save/"

/* Byte order – ARM and x86 Android are both little-endian */
#undef WORDS_BIGENDIAN

/* POSIX functions available via Bionic libc */
#define HAVE_READDIR_R
#define HAVE_SETENV
#undef  HAVE_STRUPR
#define HAVE_STRCASECMP_UQM
#undef  HAVE_STRICMP
#undef  HAVE_GETOPT_LONG

/* Wide character support (Bionic provides these) */
#define HAVE_ISWGRAPH
#define HAVE_WCHAR_T
#define HAVE_WINT_T
#define HAVE__BOOL

#endif  /* CONFIG_ANDROID_H_ */
