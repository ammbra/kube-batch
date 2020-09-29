#!/bin/bash
set -e

java_debug_args=""

# Set debug options if required
if [ x"${ENABLE_DEBUG}" != x ] && [ "${ENABLE_DEBUG}" != "false" ]; then
  java_debug_args="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${DEBUG_PORT}"
fi

exec java -Djava.security.egd=file:/dev/./urandom -jar $java_debug_args app.jar
