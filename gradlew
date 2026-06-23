#!/bin/sh
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
MAX_FD="maximum"
warn () { echo "$*"; }
die () { echo; echo "$*"; echo; exit 1; }
JAVA_HOME_CANDIDATES=""
if [ -n "$JAVA_HOME" ] ; then
    JAVA_HOME_CANDIDATES="$JAVA_HOME/bin/java"
fi
for candidate in $JAVA_HOME_CANDIDATES; do
    if [ -f "$candidate" ]; then
        JAVACMD="$candidate"
        break
    fi
done
if [ -z "$JAVACMD" ]; then
    JAVACMD=$(which java 2>/dev/null)
fi
if [ -z "$JAVACMD" ]; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi
APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P ) || exit
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec "$JAVACMD" $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
