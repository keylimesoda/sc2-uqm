cmake_minimum_required(VERSION 3.15)
project(UrQuanMasters C)

set(CMAKE_C_STANDARD 99)

# ── Paths ─────────────────────────────────────────────────────────────
set(UQM_SRC "${CMAKE_CURRENT_SOURCE_DIR}/sc2/src")
set(VCPKG_INST "${CMAKE_CURRENT_SOURCE_DIR}/vcpkg_installed/x64-mingw-dynamic")
set(SDL_IMAGE_SRC "${CMAKE_CURRENT_SOURCE_DIR}/deps/SDL_image-1.2.12")

# ── Include directories ──────────────────────────────────────────────
# SDL headers live in include/SDL/ from vcpkg; UQM code uses <SDL.h> on
# non-Apple, so we add the SDL subdirectory directly.
include_directories(
    "${UQM_SRC}"
    "${UQM_SRC}/libs"            # internal libs (mikmod, lua, uio, etc.)
    "${UQM_SRC}/regex"            # regex.h (project's own regex)
    "${VCPKG_INST}/include"       # zlib.h, ogg/, vorbis/
    "${VCPKG_INST}/include/SDL"   # SDL.h, SDL_video.h, …
    "${SDL_IMAGE_SRC}"            # SDL_image.h (built from source)
)

# ── Library search path ──────────────────────────────────────────────
link_directories("${VCPKG_INST}/lib")

# ── Preprocessor definitions ─────────────────────────────────────────
add_definitions(
    -DWIN32
    -D_CONSOLE
    -D_MBCS
    -D_VW=320
    -D_VH=240
    -DHAVE_OPENGL
    -DGFXMODULE_SDL
    -DTHREADLIB_SDL
    -DHAVE_ZIP
    -DHAVE_JOYSTICK
    -DNETPLAY
    -DZLIB_DLL
    -DUSE_INTERNAL_MIKMOD
    -DUSE_INTERNAL_LUA
)

# ── SDL_image – build from source as a static library ────────────────
# Only BMP and PNG loaders are needed; disable optional deps (JPG, TIF, WEBP)
# so we don't need libjpeg / libtiff / libwebp.
add_library(SDL_image_static STATIC
    "${SDL_IMAGE_SRC}/IMG.c"
    "${SDL_IMAGE_SRC}/IMG_bmp.c"
    "${SDL_IMAGE_SRC}/IMG_gif.c"
    "${SDL_IMAGE_SRC}/IMG_jpg.c"
    "${SDL_IMAGE_SRC}/IMG_lbm.c"
    "${SDL_IMAGE_SRC}/IMG_pcx.c"
    "${SDL_IMAGE_SRC}/IMG_png.c"
    "${SDL_IMAGE_SRC}/IMG_pnm.c"
    "${SDL_IMAGE_SRC}/IMG_tga.c"
    "${SDL_IMAGE_SRC}/IMG_tif.c"
    "${SDL_IMAGE_SRC}/IMG_webp.c"
    "${SDL_IMAGE_SRC}/IMG_xcf.c"
    "${SDL_IMAGE_SRC}/IMG_xpm.c"
    "${SDL_IMAGE_SRC}/IMG_xv.c"
    "${SDL_IMAGE_SRC}/IMG_xxx.c"
)
target_compile_definitions(SDL_image_static PRIVATE
    -DLOAD_BMP
    -DLOAD_GIF
    -DLOAD_LBM
    -DLOAD_PCX
    -DLOAD_PNG
    -DLOAD_PNM
    -DLOAD_TGA
    -DLOAD_XCF
    -DLOAD_XPM
    -DLOAD_XV
    # Disable formats needing external libs we don't have:
    # -DLOAD_JPG   (needs libjpeg)
    # -DLOAD_TIF   (needs libtiff)
    # -DLOAD_WEBP  (needs libwebp)
)
target_include_directories(SDL_image_static PRIVATE
    "${VCPKG_INST}/include/SDL"
    "${VCPKG_INST}/include"       # for zlib.h used by PNG loader
)
# SDL_image 1.2 predates libpng 1.6 const-correctness changes
target_compile_options(SDL_image_static PRIVATE
    -Wno-incompatible-pointer-types
    -Wno-int-conversion
    -Wno-implicit-function-declaration
)

# ── UQM sources ──────────────────────────────────────────────────────
set(UQM_SOURCES
    # getopt / regex
    ${UQM_SRC}/getopt/getopt.c
    ${UQM_SRC}/getopt/getopt1.c
    ${UQM_SRC}/regex/regex.c

    # libs/callback
    ${UQM_SRC}/libs/callback/alarm.c
    ${UQM_SRC}/libs/callback/async.c
    ${UQM_SRC}/libs/callback/callback.c

    # libs/decomp
    ${UQM_SRC}/libs/decomp/lzdecode.c
    ${UQM_SRC}/libs/decomp/lzencode.c
    ${UQM_SRC}/libs/decomp/update.c

    # libs/file
    ${UQM_SRC}/libs/file/dirs.c
    ${UQM_SRC}/libs/file/files.c

    # libs/graphics/sdl
    ${UQM_SRC}/libs/graphics/sdl/2xscalers.c
    ${UQM_SRC}/libs/graphics/sdl/2xscalers_3dnow.c
    ${UQM_SRC}/libs/graphics/sdl/2xscalers_mmx.c
    ${UQM_SRC}/libs/graphics/sdl/2xscalers_sse.c
    ${UQM_SRC}/libs/graphics/sdl/biadv2x.c
    ${UQM_SRC}/libs/graphics/sdl/bilinear2x.c
    ${UQM_SRC}/libs/graphics/sdl/canvas.c
    ${UQM_SRC}/libs/graphics/sdl/hq2x.c
    ${UQM_SRC}/libs/graphics/sdl/nearest2x.c
    ${UQM_SRC}/libs/graphics/sdl/opengl.c
    ${UQM_SRC}/libs/graphics/sdl/palette.c
    ${UQM_SRC}/libs/graphics/sdl/primitives.c
    ${UQM_SRC}/libs/graphics/sdl/pure.c
    ${UQM_SRC}/libs/graphics/sdl/rotozoom.c
    ${UQM_SRC}/libs/graphics/sdl/scalers.c
    ${UQM_SRC}/libs/graphics/sdl/sdl_common.c
    ${UQM_SRC}/libs/graphics/sdl/sdluio.c
    ${UQM_SRC}/libs/graphics/sdl/triscan2x.c

    # libs/graphics
    ${UQM_SRC}/libs/graphics/bbox.c
    ${UQM_SRC}/libs/graphics/boxint.c
    ${UQM_SRC}/libs/graphics/clipline.c
    ${UQM_SRC}/libs/graphics/cmap.c
    ${UQM_SRC}/libs/graphics/context.c
    ${UQM_SRC}/libs/graphics/dcqueue.c
    ${UQM_SRC}/libs/graphics/drawable.c
    ${UQM_SRC}/libs/graphics/filegfx.c
    ${UQM_SRC}/libs/graphics/font.c
    ${UQM_SRC}/libs/graphics/frame.c
    ${UQM_SRC}/libs/graphics/gfx_common.c
    ${UQM_SRC}/libs/graphics/gfxload.c
    ${UQM_SRC}/libs/graphics/intersec.c
    ${UQM_SRC}/libs/graphics/loaddisp.c
    ${UQM_SRC}/libs/graphics/pixmap.c
    ${UQM_SRC}/libs/graphics/resgfx.c
    ${UQM_SRC}/libs/graphics/tfb_draw.c
    ${UQM_SRC}/libs/graphics/tfb_prim.c
    ${UQM_SRC}/libs/graphics/widgets.c

    # libs/heap
    ${UQM_SRC}/libs/heap/heap.c

    # libs/input
    ${UQM_SRC}/libs/input/sdl/input.c
    ${UQM_SRC}/libs/input/sdl/keynames.c
    ${UQM_SRC}/libs/input/sdl/vcontrol.c
    ${UQM_SRC}/libs/input/input_common.c

    # libs/list
    ${UQM_SRC}/libs/list/list.c

    # libs/lua  (internal Lua)
    ${UQM_SRC}/libs/lua/lapi.c
    ${UQM_SRC}/libs/lua/lauxlib.c
    ${UQM_SRC}/libs/lua/lbaselib.c
    ${UQM_SRC}/libs/lua/lbitlib.c
    ${UQM_SRC}/libs/lua/lcode.c
    ${UQM_SRC}/libs/lua/lctype.c
    ${UQM_SRC}/libs/lua/ldebug.c
    ${UQM_SRC}/libs/lua/ldo.c
    ${UQM_SRC}/libs/lua/ldump.c
    ${UQM_SRC}/libs/lua/lfunc.c
    ${UQM_SRC}/libs/lua/lgc.c
    ${UQM_SRC}/libs/lua/llex.c
    ${UQM_SRC}/libs/lua/lmathlib.c
    ${UQM_SRC}/libs/lua/lmem.c
    ${UQM_SRC}/libs/lua/lobject.c
    ${UQM_SRC}/libs/lua/lopcodes.c
    ${UQM_SRC}/libs/lua/lparser.c
    ${UQM_SRC}/libs/lua/lstate.c
    ${UQM_SRC}/libs/lua/lstring.c
    ${UQM_SRC}/libs/lua/lstrlib.c
    ${UQM_SRC}/libs/lua/ltable.c
    ${UQM_SRC}/libs/lua/ltablib.c
    ${UQM_SRC}/libs/lua/ltm.c
    ${UQM_SRC}/libs/lua/lundump.c
    ${UQM_SRC}/libs/lua/lvm.c
    ${UQM_SRC}/libs/lua/lzio.c

    # libs/luauqm
    ${UQM_SRC}/libs/luauqm/luauqm.c
    ${UQM_SRC}/libs/luauqm/scriptres.c

    # libs/log
    ${UQM_SRC}/libs/log/msgbox_win.c
    ${UQM_SRC}/libs/log/uqmlog.c

    # libs/math
    ${UQM_SRC}/libs/math/random.c
    ${UQM_SRC}/libs/math/random2.c
    ${UQM_SRC}/libs/math/sqrt.c

    # libs/md5
    ${UQM_SRC}/libs/md5/md5.c

    # libs/memory
    ${UQM_SRC}/libs/memory/w_memlib.c

    # libs/resource
    ${UQM_SRC}/libs/resource/direct.c
    ${UQM_SRC}/libs/resource/filecntl.c
    ${UQM_SRC}/libs/resource/getres.c
    ${UQM_SRC}/libs/resource/loadres.c
    ${UQM_SRC}/libs/resource/propfile.c
    ${UQM_SRC}/libs/resource/resinit.c
    ${UQM_SRC}/libs/resource/stringbank.c

    # libs/sound/openal — skip for now (no OpenAL installed)
    # ${UQM_SRC}/libs/sound/openal/audiodrv_openal.c

    # libs/sound/decoders
    ${UQM_SRC}/libs/sound/decoders/aiffaud.c
    ${UQM_SRC}/libs/sound/decoders/decoder.c
    ${UQM_SRC}/libs/sound/decoders/dukaud.c
    ${UQM_SRC}/libs/sound/decoders/modaud.c
    ${UQM_SRC}/libs/sound/decoders/oggaud.c
    ${UQM_SRC}/libs/sound/decoders/wav.c

    # libs/sound/mixer
    ${UQM_SRC}/libs/sound/mixer/sdl/audiodrv_sdl.c
    ${UQM_SRC}/libs/sound/mixer/nosound/audiodrv_nosound.c
    ${UQM_SRC}/libs/sound/mixer/mixer.c

    # libs/sound
    ${UQM_SRC}/libs/sound/audiocore.c
    ${UQM_SRC}/libs/sound/fileinst.c
    ${UQM_SRC}/libs/sound/music.c
    ${UQM_SRC}/libs/sound/resinst.c
    ${UQM_SRC}/libs/sound/sfx.c
    ${UQM_SRC}/libs/sound/sound.c
    ${UQM_SRC}/libs/sound/stream.c
    ${UQM_SRC}/libs/sound/trackplayer.c

    # libs/strings
    ${UQM_SRC}/libs/strings/getstr.c
    ${UQM_SRC}/libs/strings/sfileins.c
    ${UQM_SRC}/libs/strings/sresins.c
    ${UQM_SRC}/libs/strings/stringhashtable.c
    ${UQM_SRC}/libs/strings/strings.c
    ${UQM_SRC}/libs/strings/unicode.c

    # libs/video
    ${UQM_SRC}/libs/video/dukvid.c
    ${UQM_SRC}/libs/video/legacyplayer.c
    ${UQM_SRC}/libs/video/vfileins.c
    ${UQM_SRC}/libs/video/video.c
    ${UQM_SRC}/libs/video/videodec.c
    ${UQM_SRC}/libs/video/vidplayer.c
    ${UQM_SRC}/libs/video/vresins.c

    # libs/threads
    ${UQM_SRC}/libs/threads/sdl/sdlthreads.c
    ${UQM_SRC}/libs/threads/thrcommon.c

    # libs/time
    ${UQM_SRC}/libs/time/sdl/sdltime.c
    ${UQM_SRC}/libs/time/timecommon.c

    # libs/task
    ${UQM_SRC}/libs/task/tasklib.c

    # libs/uio
    ${UQM_SRC}/libs/uio/stdio/stdio.c
    ${UQM_SRC}/libs/uio/zip/zip.c
    ${UQM_SRC}/libs/uio/charhashtable.c
    # debug.c only in Debug builds:
    # ${UQM_SRC}/libs/uio/debug.c
    ${UQM_SRC}/libs/uio/defaultfs.c
    ${UQM_SRC}/libs/uio/fileblock.c
    ${UQM_SRC}/libs/uio/fstypes.c
    ${UQM_SRC}/libs/uio/gphys.c
    # hashtable.c excluded from all builds
    # memdebug.c excluded from all builds
    ${UQM_SRC}/libs/uio/io.c
    ${UQM_SRC}/libs/uio/ioaux.c
    ${UQM_SRC}/libs/uio/match.c
    ${UQM_SRC}/libs/uio/mount.c
    ${UQM_SRC}/libs/uio/mounttree.c
    ${UQM_SRC}/libs/uio/paths.c
    ${UQM_SRC}/libs/uio/physical.c
    ${UQM_SRC}/libs/uio/uiostream.c
    ${UQM_SRC}/libs/uio/uioutils.c
    ${UQM_SRC}/libs/uio/utils.c

    # libs/mikmod (internal MikMod)
    ${UQM_SRC}/libs/mikmod/drv_nos.c
    ${UQM_SRC}/libs/mikmod/load_it.c
    ${UQM_SRC}/libs/mikmod/load_mod.c
    ${UQM_SRC}/libs/mikmod/load_s3m.c
    ${UQM_SRC}/libs/mikmod/load_stm.c
    ${UQM_SRC}/libs/mikmod/load_xm.c
    ${UQM_SRC}/libs/mikmod/mdreg.c
    ${UQM_SRC}/libs/mikmod/mdriver.c
    ${UQM_SRC}/libs/mikmod/mloader.c
    ${UQM_SRC}/libs/mikmod/mlreg.c
    ${UQM_SRC}/libs/mikmod/mlutil.c
    ${UQM_SRC}/libs/mikmod/mmalloc.c
    ${UQM_SRC}/libs/mikmod/mmerror.c
    ${UQM_SRC}/libs/mikmod/mmio.c
    ${UQM_SRC}/libs/mikmod/mplayer.c
    ${UQM_SRC}/libs/mikmod/munitrk.c
    ${UQM_SRC}/libs/mikmod/mwav.c
    ${UQM_SRC}/libs/mikmod/npertab.c
    ${UQM_SRC}/libs/mikmod/sloader.c
    ${UQM_SRC}/libs/mikmod/virtch.c
    ${UQM_SRC}/libs/mikmod/virtch2.c
    ${UQM_SRC}/libs/mikmod/virtch_common.c

    # libs/network
    ${UQM_SRC}/libs/network/connect/connect.c
    ${UQM_SRC}/libs/network/connect/listen.c
    ${UQM_SRC}/libs/network/connect/resolve.c
    ${UQM_SRC}/libs/network/netmanager/ndesc.c
    ${UQM_SRC}/libs/network/netmanager/netmanager_win.c
    ${UQM_SRC}/libs/network/socket/socket.c
    ${UQM_SRC}/libs/network/socket/socket_win.c
    ${UQM_SRC}/libs/network/netport.c
    ${UQM_SRC}/libs/network/network_win.c

    # uqm/comm — alien dialog
    ${UQM_SRC}/uqm/comm/arilou/arilouc.c
    ${UQM_SRC}/uqm/comm/blackur/blackurc.c
    ${UQM_SRC}/uqm/comm/chmmr/chmmrc.c
    ${UQM_SRC}/uqm/comm/comandr/comandr.c
    ${UQM_SRC}/uqm/comm/druuge/druugec.c
    ${UQM_SRC}/uqm/comm/ilwrath/ilwrathc.c
    ${UQM_SRC}/uqm/comm/melnorm/melnorm.c
    ${UQM_SRC}/uqm/comm/mycon/myconc.c
    ${UQM_SRC}/uqm/comm/orz/orzc.c
    ${UQM_SRC}/uqm/comm/pkunk/pkunkc.c
    ${UQM_SRC}/uqm/comm/rebel/rebel.c
    ${UQM_SRC}/uqm/comm/shofixt/shofixt.c
    ${UQM_SRC}/uqm/comm/slyhome/slyhome.c
    ${UQM_SRC}/uqm/comm/slyland/slyland.c
    ${UQM_SRC}/uqm/comm/spahome/spahome.c
    ${UQM_SRC}/uqm/comm/spathi/spathic.c
    ${UQM_SRC}/uqm/comm/starbas/starbas.c
    ${UQM_SRC}/uqm/comm/supox/supoxc.c
    ${UQM_SRC}/uqm/comm/syreen/syreenc.c
    ${UQM_SRC}/uqm/comm/talkpet/talkpet.c
    ${UQM_SRC}/uqm/comm/thradd/thraddc.c
    ${UQM_SRC}/uqm/comm/umgah/umgahc.c
    ${UQM_SRC}/uqm/comm/urquan/urquanc.c
    ${UQM_SRC}/uqm/comm/utwig/utwigc.c
    ${UQM_SRC}/uqm/comm/vux/vuxc.c
    ${UQM_SRC}/uqm/comm/yehat/yehatc.c
    ${UQM_SRC}/uqm/comm/zoqfot/zoqfotc.c

    # uqm/lua
    ${UQM_SRC}/uqm/lua/luafuncs/commfuncs.c
    ${UQM_SRC}/uqm/lua/luafuncs/customfuncs.c
    ${UQM_SRC}/uqm/lua/luafuncs/eventfuncs.c
    ${UQM_SRC}/uqm/lua/luafuncs/logfuncs.c
    ${UQM_SRC}/uqm/lua/luafuncs/statefuncs.c
    ${UQM_SRC}/uqm/lua/luacomm.c
    ${UQM_SRC}/uqm/lua/luadebug.c
    ${UQM_SRC}/uqm/lua/luaevent.c
    ${UQM_SRC}/uqm/lua/luainit.c
    ${UQM_SRC}/uqm/lua/luastate.c

    # uqm/planets/generate
    ${UQM_SRC}/uqm/planets/generate/genand.c
    ${UQM_SRC}/uqm/planets/generate/genburv.c
    ${UQM_SRC}/uqm/planets/generate/genchmmr.c
    ${UQM_SRC}/uqm/planets/generate/gencol.c
    ${UQM_SRC}/uqm/planets/generate/gendefault.c
    ${UQM_SRC}/uqm/planets/generate/gendru.c
    ${UQM_SRC}/uqm/planets/generate/genilw.c
    ${UQM_SRC}/uqm/planets/generate/genmel.c
    ${UQM_SRC}/uqm/planets/generate/genmyc.c
    ${UQM_SRC}/uqm/planets/generate/genorz.c
    ${UQM_SRC}/uqm/planets/generate/genpet.c
    ${UQM_SRC}/uqm/planets/generate/genpku.c
    ${UQM_SRC}/uqm/planets/generate/genrain.c
    ${UQM_SRC}/uqm/planets/generate/gensam.c
    ${UQM_SRC}/uqm/planets/generate/genshof.c
    ${UQM_SRC}/uqm/planets/generate/gensly.c
    ${UQM_SRC}/uqm/planets/generate/gensol.c
    ${UQM_SRC}/uqm/planets/generate/genspa.c
    ${UQM_SRC}/uqm/planets/generate/gensup.c
    ${UQM_SRC}/uqm/planets/generate/gensyr.c
    ${UQM_SRC}/uqm/planets/generate/genthrad.c
    ${UQM_SRC}/uqm/planets/generate/gentrap.c
    ${UQM_SRC}/uqm/planets/generate/genutw.c
    ${UQM_SRC}/uqm/planets/generate/genvault.c
    ${UQM_SRC}/uqm/planets/generate/genvux.c
    ${UQM_SRC}/uqm/planets/generate/genwreck.c
    ${UQM_SRC}/uqm/planets/generate/genyeh.c
    ${UQM_SRC}/uqm/planets/generate/genzfpscout.c
    ${UQM_SRC}/uqm/planets/generate/genzoq.c

    # uqm/planets
    ${UQM_SRC}/uqm/planets/calc.c
    ${UQM_SRC}/uqm/planets/cargo.c
    ${UQM_SRC}/uqm/planets/devices.c
    ${UQM_SRC}/uqm/planets/gentopo.c
    ${UQM_SRC}/uqm/planets/lander.c
    ${UQM_SRC}/uqm/planets/orbits.c
    ${UQM_SRC}/uqm/planets/oval.c
    ${UQM_SRC}/uqm/planets/pl_stuff.c
    ${UQM_SRC}/uqm/planets/planets.c
    ${UQM_SRC}/uqm/planets/plangen.c
    ${UQM_SRC}/uqm/planets/pstarmap.c
    ${UQM_SRC}/uqm/planets/report.c
    ${UQM_SRC}/uqm/planets/roster.c
    ${UQM_SRC}/uqm/planets/scan.c
    ${UQM_SRC}/uqm/planets/solarsys.c
    ${UQM_SRC}/uqm/planets/surface.c

    # uqm/ships
    ${UQM_SRC}/uqm/ships/androsyn/androsyn.c
    ${UQM_SRC}/uqm/ships/arilou/arilou.c
    ${UQM_SRC}/uqm/ships/blackurq/blackurq.c
    ${UQM_SRC}/uqm/ships/chenjesu/chenjesu.c
    ${UQM_SRC}/uqm/ships/chmmr/chmmr.c
    ${UQM_SRC}/uqm/ships/druuge/druuge.c
    ${UQM_SRC}/uqm/ships/human/human.c
    ${UQM_SRC}/uqm/ships/ilwrath/ilwrath.c
    ${UQM_SRC}/uqm/ships/lastbat/lastbat.c
    ${UQM_SRC}/uqm/ships/melnorme/melnorme.c
    ${UQM_SRC}/uqm/ships/mmrnmhrm/mmrnmhrm.c
    ${UQM_SRC}/uqm/ships/mycon/mycon.c
    ${UQM_SRC}/uqm/ships/orz/orz.c
    ${UQM_SRC}/uqm/ships/pkunk/pkunk.c
    ${UQM_SRC}/uqm/ships/probe/probe.c
    ${UQM_SRC}/uqm/ships/shofixti/shofixti.c
    ${UQM_SRC}/uqm/ships/sis_ship/sis_ship.c
    ${UQM_SRC}/uqm/ships/slylandr/slylandr.c
    ${UQM_SRC}/uqm/ships/spathi/spathi.c
    ${UQM_SRC}/uqm/ships/supox/supox.c
    ${UQM_SRC}/uqm/ships/syreen/syreen.c
    ${UQM_SRC}/uqm/ships/thradd/thradd.c
    ${UQM_SRC}/uqm/ships/umgah/umgah.c
    ${UQM_SRC}/uqm/ships/urquan/urquan.c
    ${UQM_SRC}/uqm/ships/utwig/utwig.c
    ${UQM_SRC}/uqm/ships/vux/vux.c
    ${UQM_SRC}/uqm/ships/yehat/yehat.c
    ${UQM_SRC}/uqm/ships/zoqfot/zoqfot.c

    # uqm/supermelee/netplay
    ${UQM_SRC}/uqm/supermelee/netplay/proto/npconfirm.c
    ${UQM_SRC}/uqm/supermelee/netplay/proto/ready.c
    ${UQM_SRC}/uqm/supermelee/netplay/proto/reset.c
    ${UQM_SRC}/uqm/supermelee/netplay/checkbuf.c
    ${UQM_SRC}/uqm/supermelee/netplay/checksum.c
    ${UQM_SRC}/uqm/supermelee/netplay/crc.c
    ${UQM_SRC}/uqm/supermelee/netplay/netconnection.c
    ${UQM_SRC}/uqm/supermelee/netplay/netinput.c
    ${UQM_SRC}/uqm/supermelee/netplay/netmelee.c
    ${UQM_SRC}/uqm/supermelee/netplay/netmisc.c
    ${UQM_SRC}/uqm/supermelee/netplay/netoptions.c
    ${UQM_SRC}/uqm/supermelee/netplay/netrcv.c
    ${UQM_SRC}/uqm/supermelee/netplay/netsend.c
    ${UQM_SRC}/uqm/supermelee/netplay/netstate.c
    ${UQM_SRC}/uqm/supermelee/netplay/notify.c
    ${UQM_SRC}/uqm/supermelee/netplay/notifyall.c
    ${UQM_SRC}/uqm/supermelee/netplay/packet.c
    ${UQM_SRC}/uqm/supermelee/netplay/packethandlers.c
    ${UQM_SRC}/uqm/supermelee/netplay/packetq.c
    ${UQM_SRC}/uqm/supermelee/netplay/packetsenders.c

    # uqm/supermelee
    ${UQM_SRC}/uqm/supermelee/buildpick.c
    ${UQM_SRC}/uqm/supermelee/loadmele.c
    ${UQM_SRC}/uqm/supermelee/melee.c
    ${UQM_SRC}/uqm/supermelee/meleesetup.c
    ${UQM_SRC}/uqm/supermelee/pickmele.c

    # uqm – core game
    ${UQM_SRC}/uqm/battle.c
    ${UQM_SRC}/uqm/battlecontrols.c
    ${UQM_SRC}/uqm/border.c
    ${UQM_SRC}/uqm/build.c
    ${UQM_SRC}/uqm/cleanup.c
    ${UQM_SRC}/uqm/clock.c
    ${UQM_SRC}/uqm/cnctdlg.c
    ${UQM_SRC}/uqm/collide.c
    ${UQM_SRC}/uqm/comm.c
    ${UQM_SRC}/uqm/commanim.c
    ${UQM_SRC}/uqm/commglue.c
    ${UQM_SRC}/uqm/confirm.c
    ${UQM_SRC}/uqm/cons_res.c
    ${UQM_SRC}/uqm/credits.c
    ${UQM_SRC}/uqm/cyborg.c
    ${UQM_SRC}/uqm/displist.c
    ${UQM_SRC}/uqm/dummy.c
    ${UQM_SRC}/uqm/encount.c
    ${UQM_SRC}/uqm/flash.c
    ${UQM_SRC}/uqm/fmv.c
    ${UQM_SRC}/uqm/galaxy.c
    ${UQM_SRC}/uqm/gameev.c
    ${UQM_SRC}/uqm/gameinp.c
    ${UQM_SRC}/uqm/gameopt.c
    ${UQM_SRC}/uqm/gendef.c
    ${UQM_SRC}/uqm/getchar.c
    ${UQM_SRC}/uqm/globdata.c
    ${UQM_SRC}/uqm/gravity.c
    ${UQM_SRC}/uqm/grpinfo.c
    ${UQM_SRC}/uqm/hyper.c
    ${UQM_SRC}/uqm/init.c
    ${UQM_SRC}/uqm/intel.c
    ${UQM_SRC}/uqm/intro.c
    ${UQM_SRC}/uqm/ipdisp.c
    ${UQM_SRC}/uqm/load.c
    ${UQM_SRC}/uqm/load_legacy.c
    ${UQM_SRC}/uqm/loadship.c
    ${UQM_SRC}/uqm/master.c
    ${UQM_SRC}/uqm/menu.c
    ${UQM_SRC}/uqm/misc.c
    ${UQM_SRC}/uqm/oscill.c
    ${UQM_SRC}/uqm/outfit.c
    ${UQM_SRC}/uqm/pickship.c
    ${UQM_SRC}/uqm/plandata.c
    ${UQM_SRC}/uqm/process.c
    ${UQM_SRC}/uqm/restart.c
    ${UQM_SRC}/uqm/save.c
    ${UQM_SRC}/uqm/settings.c
    ${UQM_SRC}/uqm/setup.c
    ${UQM_SRC}/uqm/setupmenu.c
    ${UQM_SRC}/uqm/ship.c
    ${UQM_SRC}/uqm/shipstat.c
    ${UQM_SRC}/uqm/shipyard.c
    ${UQM_SRC}/uqm/sis.c
    ${UQM_SRC}/uqm/sounds.c
    ${UQM_SRC}/uqm/starbase.c
    ${UQM_SRC}/uqm/starcon.c
    ${UQM_SRC}/uqm/starmap.c
    ${UQM_SRC}/uqm/state.c
    ${UQM_SRC}/uqm/status.c
    ${UQM_SRC}/uqm/tactrans.c
    ${UQM_SRC}/uqm/trans.c
    ${UQM_SRC}/uqm/uqmdebug.c
    ${UQM_SRC}/uqm/util.c
    ${UQM_SRC}/uqm/velocity.c
    ${UQM_SRC}/uqm/weapon.c

    # top-level sources
    ${UQM_SRC}/options.c
    ${UQM_SRC}/port.c
    ${UQM_SRC}/uqm.c
    ${UQM_SRC}/crashhandler.c
)

# ── Build executable ─────────────────────────────────────────────────
add_executable(uqm WIN32 ${UQM_SOURCES})

# Link order matters for MinGW: SDLmain must come first (provides WinMain)
# Use mingw32 before SDLmain per SDL docs for MinGW
target_link_libraries(uqm
    mingw32
    SDLmain
    SDL_image_static
    png
    SDL
    vorbisfile
    vorbis
    ogg
    zlib
    opengl32
    glu32
    ws2_32
    winmm
    gdi32
    dxguid
    imm32
    version
    dbghelp
)

# Relax GCC 14 strictness for old C code
target_compile_options(uqm PRIVATE
    -O2
    -Wno-implicit-function-declaration
    -Wno-int-conversion
    -Wno-incompatible-pointer-types
    -Wno-builtin-declaration-mismatch
    -Wno-pointer-sign
    -Wno-deprecated-declarations
    -Wno-format
    -Wno-misleading-indentation
    -Wno-unused-result
    -Wno-parentheses
    -Wno-unused-variable
    -Wno-unused-but-set-variable
)
