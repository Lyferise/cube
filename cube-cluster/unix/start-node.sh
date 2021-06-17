#!/bin/bash

# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
cd "$SCRIPT_DIR" || exit 1

# Check if already running
IS_RUNNING=$(pgrep -a java | grep "$SCRIPT_DIR" | grep -v grep)
if [ -n "$IS_RUNNING" ]; then
  echo "Process already running: $IS_RUNNING"
  exit 1
fi

EXECUTABLE=$(ls "$SCRIPT_DIR"/bin/*.jar)
CONSOLE_OUTPUT_FILE="$SCRIPT_DIR/console.log"

nohup "$JAVA" -jar "$EXECUTABLE" > "$CONSOLE_OUTPUT_FILE" 2>&1 < /dev/null &
PROCESS=$(pgrep -a java | grep "$SCRIPT_DIR" | grep -v grep)

echo "STARTED: $PROCESS"
cd "$PWD" || exit 1