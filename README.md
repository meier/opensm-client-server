OpenSM Monitoring Service (OMS)
=========================
by Tim Meier, [meier3@llnl.gov](mailto:meier3@llnl.gov)

**OMS** is a service and an interface for monitoring Infiniband Fabrics.

Released under the GNU LGPL, `LLNL-CODE-673346`.  See the `LICENSE`
file for details.

Overview
-------------------------

This java package (*OsmJpiServer*) is designed to be invoked by the native OsmJpiInterface library,
which is in turned loaded by opensm.

This package interfaces with the OsmJpiInterface event plugin though the Java Virtual Machine
and the native interface provided by the plugin.

Together, these packages provide a mechanism for OSM Clients and Listeners to connect
to the subnet manager and obtain information about the fabric.

This package provides secure remote I/O capabilities, as well as interfacing with the
native package to obtain the fabric information from shared memory.

Installation
-------------------------
Refer to the INSTALL document for setup and initial use for both client and server.

Testing
-------------------------
A few client utilities are included in this package and can be used to test the service.  They are located in the `/usr/share/java/OsmClientServer/bin/` directory.

* showServerStatus.sh - provides the version as well as uptime for the service and freshness of the data
* osmConsole.sh - provides a curses based tool, similar to opensm's console.
