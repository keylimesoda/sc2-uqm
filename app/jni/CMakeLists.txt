cmake_minimum_required(VERSION 3.22)
project(uqm-android C)

# ══════════════════════════════════════════════════════════════════════
# Paths to source trees (relative to this CMakeLists.txt)
#   UQM source: committed directly in repo root as uqm-src/
#   Third-party libs: git submodules under deps/
# ══════════════════════════════════════════════════════════════════════
set(UQM_SRC       "${CMAKE_CURRENT_SOURCE_DIR}/../../uqm-src")
set(SDL2_SRC      "${CMAKE_CURRENT_SOURCE_DIR}/../../deps/SDL2")
set(LIBOGG_SRC    "${CMAKE_CURRENT_SOURCE_DIR}/../../deps/libogg")
set(LIBVORBIS_SRC "${CMAKE_CURRENT_SOURCE_DIR}/../../deps/libvorbis")
set(LIBPNG_SRC    "${CMAKE_CURRENT_SOURCE_DIR}/../../deps/libpng")

# ══════════════════════════════════════════════════════════════════════
# 1.  SDL2  (shared library – loaded by SDLActivity at runtime)
# ══════════════════════════════════════════════════════════════════════
set(SDL_SHARED  ON  CACHE BOOL "" FORCE)
set(SDL_STATIC  OFF CACHE BOOL "" FORCE)
set(SDL_TEST    OFF CACHE BOOL "" FORCE)
add_subdirectory(${SDL2_SRC} SDL2-build)

# ══════════════════════════════════════════════════════════════════════
# 2.  libogg (static)
# ══════════════════════════════════════════════════════════════════════
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
set(INSTALL_DOCS OFF CACHE BOOL "" FORCE)
set(BUILD_TESTING OFF CACHE BOOL "" FORCE)
add_subdirectory(${LIBOGG_SRC} libogg-build)

# ══════════════════════════════════════════════════════════════════════
# 3.  libvorbis (static)  –  needs Ogg
# ══════════════════════════════════════════════════════════════════════
set(OGG_INCLUDE_DIR  "${LIBOGG_SRC}/include"   CACHE PATH "" FORCE)
set(OGG_LIBRARY      "ogg"                     CACHE STRING "" FORCE)
add_subdirectory(${LIBVORBIS_SRC} libvorbis-build)

# ══════════════════════════════════════════════════════════════════════
# 4.  libpng (static)  –  uses NDK zlib
# ══════════════════════════════════════════════════════════════════════
set(PNG_SHARED  OFF CACHE BOOL "" FORCE)
set(PNG_STATIC  ON  CACHE BOOL "" FORCE)
set(PNG_TESTS   OFF CACHE BOOL "" FORCE)
set(PNG_TOOLS   OFF CACHE BOOL "" FORCE)
# libpng's CMake expects ZLIB_INCLUDE_DIR/ZLIB_LIBRARY; NDK zlib is in sysroot
find_package(ZLIB REQUIRED)
add_subdirectory(${LIBPNG_SRC} libpng-build)

# ══════════════════════════════════════════════════════════════════════
# 5.  UQM  "main"  shared library
# ══════════════════════════════════════════════════════════════════════
set(UQM_SOURCES
    # Top-level
    ${UQM_SRC}/port.c
    ${UQM_SRC}/options.c
    ${UQM_SRC}/uqm.c

    # abxadec
    ${UQM_SRC}/abxadec/abxaud.c

    # getopt
    ${UQM_SRC}/getopt/getopt.c
    ${UQM_SRC}/getopt/getopt1.c

    # regex
    ${UQM_SRC}/regex/regex.c

    # ── uqm/ game logic ─────────────────────────────────────────
    ${UQM_SRC}/uqm/android_overlay.c
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
    ${UQM_SRC}/uqm/demo.c
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

    # uqm/comm/
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

    # uqm/planets/
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

    # uqm/planets/generate/
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

    # uqm/ships/
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

    # uqm/supermelee/
    ${UQM_SRC}/uqm/supermelee/buildpick.c
    ${UQM_SRC}/uqm/supermelee/loadmele.c
    ${UQM_SRC}/uqm/supermelee/melee.c
    ${UQM_SRC}/uqm/supermelee/meleesetup.c
    ${UQM_SRC}/uqm/supermelee/pickmele.c

    # uqm/supermelee/netplay/
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
    ${UQM_SRC}/uqm/supermelee/netplay/proto/npconfirm.c
    ${UQM_SRC}/uqm/supermelee/netplay/proto/ready.c
    ${UQM_SRC}/uqm/supermelee/netplay/proto/reset.c

    # ── libs/ ────────────────────────────────────────────────────
    # callback
    ${UQM_SRC}/libs/callback/alarm.c
    ${UQM_SRC}/libs/callback/async.c
    ${UQM_SRC}/libs/callback/callback.c

    # decomp
    ${UQM_SRC}/libs/decomp/lzdecode.c
    ${UQM_SRC}/libs/decomp/lzencode.c
    ${UQM_SRC}/libs/decomp/update.c

    # file
    ${UQM_SRC}/libs/file/dirs.c
    ${UQM_SRC}/libs/file/files.c
    ${UQM_SRC}/libs/file/temp.c

    # graphics (core)
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

    # graphics/sdl
    ${UQM_SRC}/libs/graphics/sdl/2xscalers.c
    ${UQM_SRC}/libs/graphics/sdl/2xscalers_3dnow.c
    ${UQM_SRC}/libs/graphics/sdl/2xscalers_mmx.c
    ${UQM_SRC}/libs/graphics/sdl/2xscalers_sse.c
    ${UQM_SRC}/libs/graphics/sdl/biadv2x.c
    ${UQM_SRC}/libs/graphics/sdl/bilinear2x.c
    ${UQM_SRC}/libs/graphics/sdl/canvas.c
    ${UQM_SRC}/libs/graphics/sdl/hq2x.c
    ${UQM_SRC}/libs/graphics/sdl/nearest2x.c
    ${UQM_SRC}/libs/graphics/sdl/palette.c
    ${UQM_SRC}/libs/graphics/sdl/png2sdl.c
    ${UQM_SRC}/libs/graphics/sdl/primitives.c
    ${UQM_SRC}/libs/graphics/sdl/rotozoom.c
    ${UQM_SRC}/libs/graphics/sdl/scalers.c
    ${UQM_SRC}/libs/graphics/sdl/sdl_common.c
    ${UQM_SRC}/libs/graphics/sdl/sdl2_common.c
    ${UQM_SRC}/libs/graphics/sdl/sdl2_pure.c
    ${UQM_SRC}/libs/graphics/sdl/sdluio.c
    ${UQM_SRC}/libs/graphics/sdl/triscan2x.c

    # heap
    ${UQM_SRC}/libs/heap/heap.c

    # input
    ${UQM_SRC}/libs/input/input_common.c
    ${UQM_SRC}/libs/input/sdl/input.c
    ${UQM_SRC}/libs/input/sdl/keynames.c
    ${UQM_SRC}/libs/input/sdl/vcontrol.c

    # list
    ${UQM_SRC}/libs/list/list.c

    # log  –  use msgbox_stub.c on Android (no Win32 MessageBox)
    ${UQM_SRC}/libs/log/msgbox_stub.c
    ${UQM_SRC}/libs/log/uqmlog.c

    # math
    ${UQM_SRC}/libs/math/random.c
    ${UQM_SRC}/libs/math/random2.c
    ${UQM_SRC}/libs/math/sqrt.c

    # md5
    ${UQM_SRC}/libs/md5/md5.c

    # memory
    ${UQM_SRC}/libs/memory/w_memlib.c

    # mikmod (internal)
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

    # network  –  BSD sockets on Android (not Win32)
    ${UQM_SRC}/libs/network/netport.c
    ${UQM_SRC}/libs/network/network_bsd.c
    ${UQM_SRC}/libs/network/connect/connect.c
    ${UQM_SRC}/libs/network/connect/listen.c
    ${UQM_SRC}/libs/network/connect/resolve.c
    ${UQM_SRC}/libs/network/netmanager/ndesc.c
    ${UQM_SRC}/libs/network/netmanager/netmanager_bsd.c
    ${UQM_SRC}/libs/network/socket/socket.c
    ${UQM_SRC}/libs/network/socket/socket_bsd.c

    # resource
    ${UQM_SRC}/libs/resource/direct.c
    ${UQM_SRC}/libs/resource/filecntl.c
    ${UQM_SRC}/libs/resource/getres.c
    ${UQM_SRC}/libs/resource/loadres.c
    ${UQM_SRC}/libs/resource/propfile.c
    ${UQM_SRC}/libs/resource/resinit.c
    ${UQM_SRC}/libs/resource/stringbank.c

    # sound
    ${UQM_SRC}/libs/sound/audiocore.c
    ${UQM_SRC}/libs/sound/fileinst.c
    ${UQM_SRC}/libs/sound/music.c
    ${UQM_SRC}/libs/sound/resinst.c
    ${UQM_SRC}/libs/sound/sfx.c
    ${UQM_SRC}/libs/sound/sound.c
    ${UQM_SRC}/libs/sound/stream.c
    ${UQM_SRC}/libs/sound/trackplayer.c
    ${UQM_SRC}/libs/sound/decoders/aiffaud.c
    ${UQM_SRC}/libs/sound/decoders/decoder.c
    ${UQM_SRC}/libs/sound/decoders/dukaud.c
    ${UQM_SRC}/libs/sound/decoders/modaud.c
    ${UQM_SRC}/libs/sound/decoders/oggaud.c
    ${UQM_SRC}/libs/sound/decoders/wav.c
    ${UQM_SRC}/libs/sound/mixer/mixer.c
    ${UQM_SRC}/libs/sound/mixer/sdl/audiodrv_sdl.c
    ${UQM_SRC}/libs/sound/mixer/nosound/audiodrv_nosound.c

    # strings
    ${UQM_SRC}/libs/strings/getstr.c
    ${UQM_SRC}/libs/strings/sfileins.c
    ${UQM_SRC}/libs/strings/sresins.c
    ${UQM_SRC}/libs/strings/stringhashtable.c
    ${UQM_SRC}/libs/strings/strings.c
    ${UQM_SRC}/libs/strings/unicode.c

    # task
    ${UQM_SRC}/libs/task/tasklib.c

    # threads (SDL)
    ${UQM_SRC}/libs/threads/thrcommon.c
    ${UQM_SRC}/libs/threads/sdl/sdlthreads.c

    # time (SDL)
    ${UQM_SRC}/libs/time/timecommon.c
    ${UQM_SRC}/libs/time/sdl/sdltime.c

    # uio
    ${UQM_SRC}/libs/uio/charhashtable.c
    ${UQM_SRC}/libs/uio/debug.c
    ${UQM_SRC}/libs/uio/defaultfs.c
    ${UQM_SRC}/libs/uio/fileblock.c
    ${UQM_SRC}/libs/uio/fstypes.c
    ${UQM_SRC}/libs/uio/gphys.c
    ${UQM_SRC}/libs/uio/hashtable.c
    ${UQM_SRC}/libs/uio/io.c
    ${UQM_SRC}/libs/uio/ioaux.c
    ${UQM_SRC}/libs/uio/match.c
    ${UQM_SRC}/libs/uio/memdebug.c
    ${UQM_SRC}/libs/uio/mount.c
    ${UQM_SRC}/libs/uio/mounttree.c
    ${UQM_SRC}/libs/uio/paths.c
    ${UQM_SRC}/libs/uio/physical.c
    ${UQM_SRC}/libs/uio/uiostream.c
    ${UQM_SRC}/libs/uio/uioutils.c
    ${UQM_SRC}/libs/uio/utils.c
    ${UQM_SRC}/libs/uio/stdio/stdio.c
    ${UQM_SRC}/libs/uio/zip/zip.c

    # video
    ${UQM_SRC}/libs/video/dukvid.c
    ${UQM_SRC}/libs/video/legacyplayer.c
    ${UQM_SRC}/libs/video/vfileins.c
    ${UQM_SRC}/libs/video/video.c
    ${UQM_SRC}/libs/video/videodec.c
    ${UQM_SRC}/libs/video/vidplayer.c
    ${UQM_SRC}/libs/video/vresins.c
)

# ── Build the shared library named "main" (SDL convention) ───────
add_library(main SHARED ${UQM_SOURCES})

target_include_directories(main PRIVATE
    ${UQM_SRC}
    ${UQM_SRC}/libs
    ${UQM_SRC}/regex
    # SDL2 headers
    ${SDL2_SRC}/include
    # Third-party headers
    ${LIBPNG_SRC}
    ${CMAKE_CURRENT_BINARY_DIR}/libpng-build   # for pnglibconf.h
    ${LIBOGG_SRC}/include
    ${LIBVORBIS_SRC}/include
)

target_compile_definitions(main PRIVATE
    ANDROID
    GFXMODULE_SDL
    HAVE_JOYSTICK
    THREADLIB_SDL
    USE_INTERNAL_MIKMOD
    SOUNDMODULE_SDL
    OGGVORBIS
    USE_ZIP_IO
    HAVE_ZIP
    NETPLAY=NETPLAY_FULL
    _VW=320
    _VH=240
)

target_compile_options(main PRIVATE
    -std=gnu99
    -Wno-implicit-function-declaration
    -Wno-int-conversion
    -Wno-incompatible-pointer-types
    -Wno-pointer-sign
    -Wno-unused-variable
    -Wno-unused-but-set-variable
    -Wno-format
    -Wno-multichar
    -Wno-parentheses
    -Wno-deprecated-declarations
)

# Link against SDL2, our static libs, and NDK sysroot libs
target_link_libraries(main PRIVATE
    SDL2
    png_static
    vorbisfile
    vorbis
    ogg
    z       # NDK zlib
    log     # Android __android_log_print
    android # ANativeActivity
    dl
)
