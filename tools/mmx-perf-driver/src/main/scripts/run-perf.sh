#!/bin/bash
#
# Simulate one application used by a number of concurrent clients to send random
# sizes of payloads in random intervals.  The application must be registered
# with the "-r" option first.  To simulate 50 clients to send 2kB-4kB messages
# to each other for 30 minutes with a new application DriveApp11:
#
# run-perf.sh -n DriveApp11 -r -c 50 -l d -s 2k -S 4k -d 30m
#
# Usage: $0 [-h host] -n appName [-r] [-c numClients] [-l loglevel] [-d time]
#           [-s minSize] [-S maxSize] [-w minTime] [-W maxTime] [-t 0..100]
#        where -n with a unique app name
#              -r to register a new app
#              -c number of concurrent clients
#              -d run the test with a duration
#              -w|-W the min/max wait time  
#              -s|-S the min/max payload size in bytes
#              -t percentage of clients to request for delivery receipts
#              log level as s|v|d|i|w|e
#              size payload size with M|m|K|k (e.g. 400K for 400 * 1024 bytes)
#              time with trailing modifier w|d|h|m|s|M (1w for week, 4h for 4 hours)
#


if [ $# -eq 0 ]; then
  echo "Usage: $0 [-r] -n app [-c clients][-w minTime][-W maxTime][-d time][-s size][-S size][-l log][-t 0..100]"
  exit 1
fi

LIBS=../../../target/mmx-perf-driver-1.0.1-shaded.jar

#DEBUG=-agentlib:jdwp=transport=dt_socket,address=127.0.0.1:8888,server=y,suspend=n
JAVAOPTS=-Xmx1024m
java ${JAVAOPTS} $DEBUG -classpath ${LIBS} com.magnet.mmx.client.perf.MessagePerfDriver $@
