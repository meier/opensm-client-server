#!/bin/sh
# the default script for starting this oms-command
#
OMS_HOME="%h/.oms"
#
# typically only this line needs to be changed
CMD_CLASS=gov.llnl.lc.infiniband.opensm.client.test.ClientInterfaceExample
#
# command line arguments for this command will be appended
/usr/share/java/OsmClientServer/bin/oms-abstract $CMD_CLASS $@