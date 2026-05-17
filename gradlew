#!/bin/sh
# Gradle start up script for POSIX systems (UN*X)
##############################################################################
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)

MAX_FD="maximum"
warn() { echo "$*"; }
die() { echo; echo "$*"; echo; exit 1; }

if [ "$1" = "--version" ] || [ "$1" = "-version" ]; then :; fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" ] && [ "$darwin" = "false" ] && [ "$nonstop" = "false" ]; then
    MAX_FD_LIMIT=$(ulimit -H -n) || warn "Could not query maximum file descriptor limit"
    case $MAX_FD_LIMIT in
        '' | soft) :;;
        *) ulimit -n "$MAX_FD_LIMIT" || warn "Could not set maximum file descriptor limit to $MAX_FD_LIMIT";;
    esac
fi

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

JAVACMD="${JAVA_HOME:+${JAVA_HOME}/bin/}java"
if ! command -v "$JAVACMD" > /dev/null 2>&1; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi

CLASSPATH="${APP_HOME}/gradle/wrapper/gradle-wrapper.jar"

exec "$JAVACMD" \
    $DEFAULT_JVM_OPTS \
    $JAVA_OPTS \
    $GRADLE_OPTS \
    "-Dorg.gradle.appname=${APP_BASE_NAME}" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
