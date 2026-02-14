Package: sdl1:x64-mingw-dynamic@1.2.15#23

**Host Environment**

- Host: x64-windows
- Compiler: GNU 14.2.0
- CMake Version: 3.31.10
-    vcpkg-tool version: 2025-12-16-44bb3ce006467fc13ba37ca099f64077b8bbf84d
    vcpkg-scripts version: 0a434205c5 2026-02-11 (4 hours ago)

**To Reproduce**

`vcpkg install sdl1:x64-mingw-dynamic zlib:x64-mingw-dynamic libogg:x64-mingw-dynamic libvorbis:x64-mingw-dynamic`

**Failure logs**

```
Downloading https://github.com/SDL-Mirror/SDL/archive/release-1.2.15.tar.gz -> SDL-Mirror-SDL-release-1.2.15.tar.gz
Successfully downloaded SDL-Mirror-SDL-release-1.2.15.tar.gz
-- Extracting source D:/Test/UQM_port/sc2-uqm/vcpkg/downloads/SDL-Mirror-SDL-release-1.2.15.tar.gz
-- Applying patch export-symbols-only-in-shared-build.patch
-- Applying patch fix-linux-build.patch
-- Applying patch sdl-config.patch
-- Using source at D:/Test/UQM_port/sc2-uqm/vcpkg/buildtrees/sdl1/src/ase-1.2.15-8506191a83.clean
-- Getting CMake variables for x64-mingw-dynamic
Downloading msys2-autoconf-wrapper-20250528-1-any.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/autoconf-wrapper-20250528-1-any.pkg.tar.zst
Successfully downloaded msys2-autoconf-wrapper-20250528-1-any.pkg.tar.zst
Downloading msys2-automake-wrapper-20250528-1-any.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/automake-wrapper-20250528-1-any.pkg.tar.zst
Successfully downloaded msys2-automake-wrapper-20250528-1-any.pkg.tar.zst
Downloading msys2-binutils-2.45.1-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/binutils-2.45.1-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-binutils-2.45.1-1-x86_64.pkg.tar.zst
Downloading msys2-libtool-2.5.4-4-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libtool-2.5.4-4-x86_64.pkg.tar.zst
Successfully downloaded msys2-libtool-2.5.4-4-x86_64.pkg.tar.zst
Downloading msys2-make-4.4.1-2-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/make-4.4.1-2-x86_64.pkg.tar.zst
Successfully downloaded msys2-make-4.4.1-2-x86_64.pkg.tar.zst
Downloading msys2-pkgconf-2.5.1-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/pkgconf-2.5.1-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-pkgconf-2.5.1-1-x86_64.pkg.tar.zst
Downloading msys2-which-2.23-4-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/which-2.23-4-x86_64.pkg.tar.zst
Successfully downloaded msys2-which-2.23-4-x86_64.pkg.tar.zst
Downloading msys2-bash-5.3.009-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/bash-5.3.009-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-bash-5.3.009-1-x86_64.pkg.tar.zst
Downloading msys2-coreutils-8.32-5-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/coreutils-8.32-5-x86_64.pkg.tar.zst
Successfully downloaded msys2-coreutils-8.32-5-x86_64.pkg.tar.zst
Downloading msys2-file-5.46-2-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/file-5.46-2-x86_64.pkg.tar.zst
Successfully downloaded msys2-file-5.46-2-x86_64.pkg.tar.zst
Downloading msys2-gawk-5.3.2-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/gawk-5.3.2-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-gawk-5.3.2-1-x86_64.pkg.tar.zst
Downloading msys2-grep-1~3.0-7-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/grep-1~3.0-7-x86_64.pkg.tar.zst
Successfully downloaded msys2-grep-1~3.0-7-x86_64.pkg.tar.zst
Downloading msys2-gzip-1.14-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/gzip-1.14-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-gzip-1.14-1-x86_64.pkg.tar.zst
Downloading msys2-diffutils-3.12-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/diffutils-3.12-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-diffutils-3.12-1-x86_64.pkg.tar.zst
Downloading msys2-sed-4.9-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/sed-4.9-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-sed-4.9-1-x86_64.pkg.tar.zst
-- Using cached msys2-msys2-runtime-3.6.5-1-x86_64.pkg.tar.zst
Downloading msys2-autoconf2.72-2.72-3-any.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/autoconf2.72-2.72-3-any.pkg.tar.zst
Successfully downloaded msys2-autoconf2.72-2.72-3-any.pkg.tar.zst
Downloading msys2-automake1.16-1.16.5-1-any.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/automake1.16-1.16.5-1-any.pkg.tar.zst
Successfully downloaded msys2-automake1.16-1.16.5-1-any.pkg.tar.zst
Downloading msys2-automake1.17-1.17-1-any.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/automake1.17-1.17-1-any.pkg.tar.zst
Successfully downloaded msys2-automake1.17-1.17-1-any.pkg.tar.zst
Downloading msys2-automake1.18-1.18.1-1-any.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/automake1.18-1.18.1-1-any.pkg.tar.zst
Successfully downloaded msys2-automake1.18-1.18.1-1-any.pkg.tar.zst
Downloading msys2-libiconv-1.18-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libiconv-1.18-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-libiconv-1.18-1-x86_64.pkg.tar.zst
Downloading msys2-libintl-0.22.5-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libintl-0.22.5-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-libintl-0.22.5-1-x86_64.pkg.tar.zst
Downloading msys2-zlib-1.3.1-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/zlib-1.3.1-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-zlib-1.3.1-1-x86_64.pkg.tar.zst
Downloading msys2-findutils-4.10.0-2-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/findutils-4.10.0-2-x86_64.pkg.tar.zst
Successfully downloaded msys2-findutils-4.10.0-2-x86_64.pkg.tar.zst
Downloading msys2-tar-1.35-3-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/tar-1.35-3-x86_64.pkg.tar.zst
Successfully downloaded msys2-tar-1.35-3-x86_64.pkg.tar.zst
Downloading msys2-gmp-6.3.0-2-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/gmp-6.3.0-2-x86_64.pkg.tar.zst
Successfully downloaded msys2-gmp-6.3.0-2-x86_64.pkg.tar.zst
Downloading msys2-gcc-libs-15.2.0-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/gcc-libs-15.2.0-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-gcc-libs-15.2.0-1-x86_64.pkg.tar.zst
Downloading msys2-libbz2-1.0.8-4-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libbz2-1.0.8-4-x86_64.pkg.tar.zst
Successfully downloaded msys2-libbz2-1.0.8-4-x86_64.pkg.tar.zst
Downloading msys2-liblzma-5.8.2-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/liblzma-5.8.2-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-liblzma-5.8.2-1-x86_64.pkg.tar.zst
Downloading msys2-libzstd-1.5.7-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libzstd-1.5.7-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-libzstd-1.5.7-1-x86_64.pkg.tar.zst
Downloading msys2-libreadline-8.3.003-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libreadline-8.3.003-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-libreadline-8.3.003-1-x86_64.pkg.tar.zst
Downloading msys2-mpfr-4.2.2-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/mpfr-4.2.2-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-mpfr-4.2.2-1-x86_64.pkg.tar.zst
Downloading msys2-libpcre-8.45-5-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libpcre-8.45-5-x86_64.pkg.tar.zst
Successfully downloaded msys2-libpcre-8.45-5-x86_64.pkg.tar.zst
Downloading msys2-m4-1.4.19-2-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/m4-1.4.19-2-x86_64.pkg.tar.zst
Successfully downloaded msys2-m4-1.4.19-2-x86_64.pkg.tar.zst
Downloading msys2-perl-5.40.3-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/perl-5.40.3-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-perl-5.40.3-1-x86_64.pkg.tar.zst
Downloading msys2-ncurses-6.5.20240831-2-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/ncurses-6.5.20240831-2-x86_64.pkg.tar.zst
Successfully downloaded msys2-ncurses-6.5.20240831-2-x86_64.pkg.tar.zst
Downloading msys2-libxcrypt-4.5.2-1-x86_64.pkg.tar.zst, trying https://mirror.msys2.org/msys/x86_64/libxcrypt-4.5.2-1-x86_64.pkg.tar.zst
Successfully downloaded msys2-libxcrypt-4.5.2-1-x86_64.pkg.tar.zst
-- Applying patch D:/Test/UQM_port/sc2-uqm/vcpkg/scripts/cmake/compile_wrapper_consider_clang-cl.patch
-- Using msys root at D:/Test/UQM_port/sc2-uqm/vcpkg/downloads/tools/msys2/fe50d45a6f168d59
-- Generating configure for x64-mingw-dynamic via autogen.sh
-- Finished generating configure for x64-mingw-dynamic
-- Using cached msys2-mingw-w64-x86_64-pkgconf-1~2.5.1-1-any.pkg.tar.zst
-- Using cached msys2-msys2-runtime-3.6.5-1-x86_64.pkg.tar.zst
-- Using msys root at D:/Test/UQM_port/sc2-uqm/vcpkg/downloads/tools/msys2/3e71d1f8e22ab23f
-- Configuring x64-mingw-dynamic-dbg
-- Configuring x64-mingw-dynamic-rel
CMake Error at scripts/cmake/vcpkg_execute_required_process.cmake:127 (message):
    Command failed: D:/Test/UQM_port/sc2-uqm/vcpkg/downloads/tools/msys2/fe50d45a6f168d59/usr/bin/bash.exe --noprofile --norc --debug -c "V=1 CPP='D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe -E' CC='D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe' CC_FOR_BUILD='D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe' CPP_FOR_BUILD='D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe -E' CXX_FOR_BUILD='D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe' CXX='D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe' RC='D:/Test/UQM_port/sc2-uqm/mingw64/bin/windres.exe' WINDRES='D:/Test/UQM_port/sc2-uqm/mingw64/bin/windres.exe' AR='D:/Test/UQM_port/sc2-uqm/mingw64/bin/ar.exe' LD='D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose' RANLIB='D:/Test/UQM_port/sc2-uqm/mingw64/bin/ranlib.exe' OBJDUMP='D:/Test/UQM_port/sc2-uqm/mingw64/bin/objdump.exe' STRIP='D:/Test/UQM_port/sc2-uqm/mingw64/bin/strip.exe' NM='D:/Test/UQM_port/sc2-uqm/mingw64/bin/nm.exe' DLLTOOL='D:/Test/UQM_port/sc2-uqm/mingw64/bin/dlltool.exe' CCAS=':' AS=':' ./../src/ase-1.2.15-8506191a83.clean/configure --build=x86_64-pc-mingw32 \"--disable-pulseaudio\" \"--disable-video-directfb\" \"gl_cv_double_slash_root=yes\" \"ac_cv_func_memmove=yes\" \"--disable-silent-rules\" \"--verbose\" \"--enable-shared\" \"--disable-static\" \"--prefix=/D/Test/UQM_port/sc2-uqm/vcpkg_installed/x64-mingw-dynamic\" \"--bindir=\\${prefix}/tools/sdl1/bin\" \"--sbindir=\\${prefix}/tools/sdl1/sbin\" \"--libdir=\\${prefix}/lib\" \"--mandir=\\${prefix}/share/sdl1\" \"--docdir=\\${prefix}/share/sdl1\" \"--datarootdir=\\${prefix}/share/sdl1\""
    Working Directory: D:/Test/UQM_port/sc2-uqm/vcpkg/buildtrees/sdl1/x64-mingw-dynamic-rel
    Error code: -1
    See logs for more information:
      D:\Test\UQM_port\sc2-uqm\vcpkg\buildtrees\sdl1\config-x64-mingw-dynamic-rel-out.log
      D:\Test\UQM_port\sc2-uqm\vcpkg\buildtrees\sdl1\config-x64-mingw-dynamic-rel-err.log

Call Stack (most recent call first):
  scripts/cmake/vcpkg_configure_make.cmake:867 (vcpkg_execute_required_process)
  ports/sdl1/portfile.cmake:68 (vcpkg_configure_make)
  scripts/ports.cmake:206 (include)



```

<details><summary>D:\Test\UQM_port\sc2-uqm\vcpkg\buildtrees\sdl1\config-x64-mingw-dynamic-rel-err.log</summary>

```
configure: WARNING: unrecognized options: --disable-silent-rules
sed: can't read conftest.c: No such file or directory
cat: confdefs.h: No such file or directory
```
</details>

<details><summary>D:\Test\UQM_port\sc2-uqm\vcpkg\buildtrees\sdl1\config-x64-mingw-dynamic-rel-out.log</summary>

```
checking for gcc... D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe
checking whether the C compiler works... yes
checking for C compiler default output file name... a.exe
checking for suffix of executables... .exe
checking whether we are cross compiling... no
checking for suffix of object files... o
checking whether the compiler supports GNU C... yes
checking whether D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe accepts -g... yes
checking for D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe option to enable C11 features... none needed
checking for stdio.h... yes
checking for stdlib.h... yes
checking for string.h... yes
checking for inttypes.h... yes
checking for stdint.h... yes
checking for strings.h... yes
checking for sys/stat.h... yes
checking for sys/types.h... yes
checking for unistd.h... yes
checking for wchar.h... yes
checking for minix/config.h... no
checking whether it is safe to define __EXTENSIONS__... yes
checking whether _XOPEN_SOURCE should be defined... no
checking build system type... x86_64-pc-mingw32
checking host system type... x86_64-pc-mingw32
checking for a sed that does not truncate output... /usr/bin/sed
checking for grep that handles long lines and -e... /usr/bin/grep
checking for egrep... /usr/bin/grep -E
checking for fgrep... /usr/bin/grep -F
checking for ld used by D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe... D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose
checking if the linker (D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose) is GNU ld... yes
checking for BSD- or MS-compatible name lister (nm)... D:/Test/UQM_port/sc2-uqm/mingw64/bin/nm.exe
checking the name lister (D:/Test/UQM_port/sc2-uqm/mingw64/bin/nm.exe) interface... BSD nm
checking whether ln -s works... no, using cp -pR
checking the maximum length of command line arguments... 8192
checking whether the shell understands some XSI constructs... yes
checking whether the shell understands "+="... yes
checking for D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose option to reload object files... -r
checking for objdump... D:/Test/UQM_port/sc2-uqm/mingw64/bin/objdump.exe
checking how to recognize dependent libraries... pass_all
checking for ar... D:/Test/UQM_port/sc2-uqm/mingw64/bin/ar.exe
checking for strip... D:/Test/UQM_port/sc2-uqm/mingw64/bin/strip.exe
checking for ranlib... D:/Test/UQM_port/sc2-uqm/mingw64/bin/ranlib.exe
checking command to parse D:/Test/UQM_port/sc2-uqm/mingw64/bin/nm.exe output from D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe object... ok
checking for dlfcn.h... no
checking for as... :
checking for dlltool... D:/Test/UQM_port/sc2-uqm/mingw64/bin/dlltool.exe
checking for objdump... (cached) D:/Test/UQM_port/sc2-uqm/mingw64/bin/objdump.exe
checking for objdir... .libs
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe supports -fno-rtti -fno-exceptions... no
checking for D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe option to produce PIC... -DDLL_EXPORT -DPIC
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe PIC flag -DDLL_EXPORT -DPIC works... yes
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe static flag -static works... yes
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe supports -c -o file.o... yes
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe supports -c -o file.o... (cached) yes
checking whether the D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe linker (D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose) supports shared libraries... yes
checking whether -lc should be explicitly linked in... yes
checking dynamic linker characteristics... Win32 ld.exe
checking how to hardcode library paths into programs... immediate
checking whether stripping libraries is possible... yes
checking if libtool supports shared libraries... yes
checking whether to build shared libraries... yes
checking whether to build static libraries... no
checking whether byte ordering is bigendian... no
checking for gcc... (cached) D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe
checking whether the compiler supports GNU C... (cached) yes
checking whether D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe accepts -g... (cached) yes
checking for D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-gcc.exe option to enable C11 features... (cached) none needed
checking whether the compiler supports GNU C++... yes
checking whether D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe accepts -g... yes
checking for D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe option to enable C++11 features... none needed
checking whether the compiler supports GNU C++... (cached) yes
checking whether D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe accepts -g... (cached) yes
checking for D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe option to enable C++11 features... (cached) none needed
checking how to run the C++ preprocessor... D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe -E
checking for ld used by D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe... D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose
checking if the linker (D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose) is GNU ld... yes
checking whether the D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe linker (D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose) supports shared libraries... yes
checking for D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe option to produce PIC... -DDLL_EXPORT -DPIC
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe PIC flag -DDLL_EXPORT -DPIC works... yes
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe static flag -static works... yes
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe supports -c -o file.o... yes
checking if D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe supports -c -o file.o... (cached) yes
checking whether the D:/Test/UQM_port/sc2-uqm/mingw64/bin/x86_64-w64-mingw32-g++.exe linker (D:/Test/UQM_port/sc2-uqm/mingw64/bin/ld.exe -verbose) supports shared libraries... yes
checking dynamic linker characteristics... Win32 ld.exe
checking how to hardcode library paths into programs... immediate
checking for a BSD-compatible install... /usr/bin/install -c
checking whether make sets $(MAKE)... yes
checking for windres... D:/Test/UQM_port/sc2-uqm/mingw64/bin/windres.exe
checking for an ANSI C-conforming const... yes
checking for inline... inline
checking for working volatile... yes
checking for egrep... (cached) /usr/bin/grep -E
checking for sys/types.h... (cached) yes
checking for stdio.h... (cached) yes
checking for stdlib.h... (cached) yes
checking for stddef.h... yes
checking for stdarg.h... yes
checking for malloc.h... yes
checking for memory.h... yes
checking for string.h... (cached) yes
checking for strings.h... (cached) yes
checking for inttypes.h... (cached) yes
checking for stdint.h... (cached) yes
checking for ctype.h... yes
checking for math.h... yes
checking for iconv.h... yes
checking for signal.h... yes
checking for size_t... yes
checking for int64_t... yes
checking for working memcmp... yes
checking for working strtod... yes
checking for mprotect... yes
checking for malloc... yes
checking for calloc... yes
checking for realloc... yes
checking for free... yes
checking for getenv... yes
checking for putenv... yes
checking for unsetenv... no
checking for qsort... yes
checking for abs... yes
checking for bcopy... no
checking for memset... yes
checking for memcpy... yes
checking for memmove... (cached) yes
checking for strlen... yes
checking for strlcpy... no
checking for strlcat... no
checking for strdup... yes
checking for _strrev... yes
checking for _strupr... yes
checking for _strlwr... yes
checking for strchr... yes
checking for strrchr... yes
checking for strstr... yes
checking for itoa... yes
checking for _ltoa... yes
checking for _uitoa... no
checking for _ultoa... yes
checking for strtol... yes
checking for strtoul... yes
checking for _i64toa... yes
checking for _ui64toa... yes
checking for strtoll... yes
checking for strtoull... yes
checking for atoi... yes
checking for atof... yes
checking for strcmp... yes
checking for strncmp... yes
checking for _stricmp... yes
checking for strcasecmp... yes
checking for _strnicmp... yes
checking for strncasecmp... yes
checking for sscanf... yes
checking for snprintf... no
checking for vsnprintf... yes
checking for iconv... no
checking for sigaction... no
checking for setjmp... yes
checking for nanosleep... yes
checking for libiconv_open in -liconv... yes
checking for pow in -lm... yes
checking for struct sigaction.sa_sigaction... no
checking Win32 compiler... yes
checking for ddraw.h... yes
checking for dsound.h... yes
checking for dinput.h... yes
checking for GCC -Wall option... yes
checking for necessary GCC -Wno-multichar option... no
configure: creating ./config.status
```
</details>

