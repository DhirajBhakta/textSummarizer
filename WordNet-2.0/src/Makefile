SHELL=/bin/sh

# Top level Makefile for WordNet 2.0 library and interface code

WN_ROOT = /usr/local/WordNet-2.0
WN_BINDIR = $(WN_ROOT)/bin
WN_INCDIR = $(WN_ROOT)/include
WN_LIBDIR = $(WN_ROOT)/lib

TCL_ROOT = /usr/local
TCL_INCDIR = $(TCL_ROOT)/include
TCL_LIBDIR = $(TCL_ROOT)/lib
TK_INCDIR = $(TCL_ROOT)/include
TK_LIBDIR = $(TCL_ROOT)/lib
X11_INCDIR = /usr/openwin/include
X11_LIBDIR = /usr/openwin/lib

# This version of WordNet was built with Tcl/Tk 8.3

WNB_LIBS = -ltk8.3 -ltcl8.3 -lX11 -lm -lsocket -lnsl -ldl -lpthread

CC = gcc
LOCAL_CFLAGS = -O -DUNIX
LOCAL_LDFLAGS =

LIBS = -lwn

LIBDIR = lib
APPDIRS = wn wnb grind
INCDIR = include
SUBDIRS = $(LIBDIR) $(APPDIRS) $(INCDIR)

all: inc library compile

inc:
	@( cd $(INCDIR) ; echo "Installing $(INCDIR).."; $(MAKE) WN_INSTALLDIR='$(WN_INCDIR)' all install)

library:
	@( cd $(LIBDIR) ; echo "Making WordNet library libwn.a" ; $(MAKE) -k CC='$(CC)' WN_INCDIR='$(WN_INCDIR)' LOCAL_CFLAGS='$(LOCAL_CFLAGS)' all )
	@echo "Done compiling library"
	@( cd $(LIBDIR) ; echo "Installing $(LIBDIR).."; $(MAKE) WN_INSTALLDIR='$(WN_LIBDIR)' install )

compile: 
	@for dir in $(APPDIRS) ; \
	 do \
		if [ -d $$dir -a $$dir = "wnb" ] ; then \
			echo "Compiling $$dir..." ; \
			(cd $$dir ; \
			$(MAKE) -k CC='$(CC)' LOCAL_CFLAGS='$(LOCAL_CFLAGS) -I$(TCL_INCDIR) -I$(TK_INCDIR) -I$(X11_INCDIR)' LOCAL_LDFLAGS='$(LOCAL_LDFLAGS) -L$(TCL_LIBDIR) -L$(TK_LIBDIR) -L$(X11_LIBDIR)' LIBS='$(WNB_LIBS) $(LIBS)' WN_INCDIR='$(WN_INCDIR)' WN_LIBDIR='$(WN_LIBDIR)' all )  \
		elif [ -d $$dir ] ; then \
			echo "Compiling $$dir..." ; \
			(cd $$dir ; \
			$(MAKE) -k CC='$(CC)' LOCAL_CFLAGS='$(LOCAL_CFLAGS)' LOCAL_LDFLAGS='$(LOCAL_LDFLAGS)' LIBS='$(LIBS)' WN_INCDIR='$(WN_INCDIR)' WN_LIBDIR='$(WN_LIBDIR)' all ) ; \
		else \
			echo "Skipping $$dir..." ; \
		fi ; \
	 done ;
	@echo "Done compiling interfaces"

install:
	@for dir in $(APPDIRS) ; \
	 do \
		if [ -d $$dir ] ; then \
			( cd $$dir ; echo "Installing $$dir...";  $(MAKE) WN_INSTALLDIR='$(WN_BINDIR)' install ) ; \
		else \
			echo "Skipping $$dir..." ; \
		fi ; \
	done ;
	@echo "Done installing interfaces"

uninstall:
	@for dir in $(APPDIRS) ; \
	 do \
		if [ -d $$dir ] ; then \
			( cd $$dir; echo "Uninstalling $$dir..." ; $(MAKE) WN_INSTALLDIR='$(WN_BINDIR)' uninstall ) \
		else \
			echo "Skipping $$dir..." ; \
		fi ; \
	 done ;
	@( cd lib ; echo "Uninstalling lib..." ; $(MAKE) WN_INSTALLDIR='$(WN_LIBDIR)' uninstall )
	@( cd include ; echo "Uninstalling include..." ; $(MAKE) WN_INSTALLDIR='$(WN_INCDIR)' uninstall )

clean::
	@for dir in $(SUBDIRS) ; \
	 do \
		if [ -d $$dir ] ; then \
			( cd $$dir; echo "Cleaning subdirectory $$dir" ; $(MAKE) clean ) \
		else \
			echo "Skipping $$dir..." ; \
		fi ; \
	 done 

clean::
	@rm -f *~ "#"*

distclean:: clean uninstall
