# need this for Doxygen integration
include $(top_srcdir)/aminclude.am

if SOLARIS
  SOLARIS_CPPFLAGS = -D_POSIX_PTHREAD_SEMANTICS
  SOLARIS_LIB_LDFLAGS = -lnsl -lsocket
endif

if WANT_OPENSSL
  OPENSSL_CPPFLAGS = -DHAVE_OPENSSL_H
  OPENSSL_LIB_LDFLAGS = -lssl -lcrypto
endif

if WANT_SASL
  SASL_CPPFLAGS = -DHAVE_CYRUS_SASL_H
  SASL_LIB_LDFLAGS = -lsasl2
  SASL_SRC = src/zk_sasl.c
endif

AM_CPPFLAGS = -I${srcdir}/include -I${srcdir}/generated $(SOLARIS_CPPFLAGS) $(OPENSSL_CPPFLAGS) $(SASL_CPPFLAGS)
AM_CFLAGS = -Wall -Werror -Wdeclaration-after-statement
AM_CXXFLAGS = -Wall $(USEIPV6)
LIB_LDFLAGS = -no-undefined -version-info 2 $(SOLARIS_LIB_LDFLAGS) $(OPENSSL_LIB_LDFLAGS) $(SASL_LIB_LDFLAGS)

# Additional flags for coverage testing (if enabled)
if ENABLEGCOV
  AM_CFLAGS += -fprofile-arcs -ftest-coverage
  AM_LDFLAGS = -lgcov
endif

pkginclude_HEADERS = include/zookeeper.h include/zookeeper_version.h include/zookeeper_log.h include/proto.h include/recordio.h generated/zookeeper.jute.h
EXTRA_DIST=LICENSE

HASHTABLE_SRC = src/hashtable/hashtable_itr.h src/hashtable/hashtable_itr.c \
    src/hashtable/hashtable_private.h src/hashtable/hashtable.h src/hashtable/hashtable.c

noinst_LTLIBRARIES = libhashtable.la
libhashtable_la_SOURCES = $(HASHTABLE_SRC)

COMMON_SRC = src/zookeeper.c include/zookeeper.h include/zookeeper_version.h include/zookeeper_log.h\
    src/recordio.c include/recordio.h include/proto.h \
    src/zk_adaptor.h generated/zookeeper.jute.c \
    src/zk_log.c src/zk_hashtable.h src/zk_hashtable.c \
    src/addrvec.h src/addrvec.c $(SASL_SRC)

# These are the symbols (classes, mostly) we want to export from our library.
EXPORT_SYMBOLS = '(zoo_|zookeeper_|zhandle|Z|format_log_message|log_message|logLevel|deallocate_|allocate_|zerror|is_unrecoverable)'
noinst_LTLIBRARIES += libzkst.la
libzkst_la_SOURCES =$(COMMON_SRC) src/st_adaptor.c
libzkst_la_LIBADD = -lm $(CLOCK_GETTIME_LIBS)

lib_LTLIBRARIES = libzookeeper_st.la
libzookeeper_st_la_SOURCES =
libzookeeper_st_la_LIBADD=libzkst.la libhashtable.la
libzookeeper_st_la_DEPENDENCIES=libzkst.la libhashtable.la
libzookeeper_st_la_LDFLAGS = $(LIB_LDFLAGS) -export-symbols-regex $(EXPORT_SYMBOLS)

if WANT_SYNCAPI
noinst_LTLIBRARIES += libzkmt.la
libzkmt_la_SOURCES =$(COMMON_SRC) src/mt_adaptor.c
libzkmt_la_CFLAGS = $(AM_CFLAGS) -DTHREADED
libzkmt_la_LIBADD = -lm $(CLOCK_GETTIME_LIBS)

lib_LTLIBRARIES += libzookeeper_mt.la
libzookeeper_mt_la_SOURCES =
libzookeeper_mt_la_LIBADD=libzkmt.la libhashtable.la -lpthread
libzookeeper_mt_la_DEPENDENCIES=libzkmt.la libhashtable.la
libzookeeper_mt_la_LDFLAGS = $(LIB_LDFLAGS) -export-symbols-regex $(EXPORT_SYMBOLS)
endif

bin_PROGRAMS = cli_st

cli_st_SOURCES = src/cli.c
cli_st_LDADD = libzookeeper_st.la $(SASL_LIB_LDFLAGS)

if WANT_SYNCAPI
bin_PROGRAMS += cli_mt load_gen

cli_mt_SOURCES = src/cli.c
cli_mt_LDADD = libzookeeper_mt.la $(SASL_LIB_LDFLAGS)
cli_mt_CFLAGS = $(AM_CFLAGS) -DTHREADED

load_gen_SOURCES = src/load_gen.c
load_gen_LDADD = libzookeeper_mt.la
load_gen_CFLAGS = $(AM_CFLAGS) -DTHREADED

endif

clean-local:
	$(RM) $(DX_CLEANFILES)
