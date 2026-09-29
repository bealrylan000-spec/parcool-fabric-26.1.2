#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

echo "ParCool Fabric 26.1.2 builder"
echo

if [ "$(uname -s)" = "Darwin" ] && [ -x /usr/libexec/java_home ]; then
  JAVA25_HOME="$(/usr/libexec/java_home -v 25 2>/dev/null || true)"
  if [ -n "$JAVA25_HOME" ]; then
    export JAVA_HOME="$JAVA25_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
  fi
fi

if ! command -v java >/dev/null 2>&1; then
  echo "ERROR: Java was not found. Install JDK 25, then run this file again."
  exit 1
fi

JAVA_MAJOR="$(java -version 2>&1 | awk -F'[\".]' '/version/ {print $2; exit}')"
if [ "$JAVA_MAJOR" != "25" ]; then
  echo "ERROR: This Minecraft/Fabric target requires Java 25."
  echo "Current Java:"
  java -version
  exit 1
fi

chmod +x ./gradlew
./gradlew clean build --no-daemon

JAR="$(find build/libs -maxdepth 1 -type f -name 'parcool-fabric-*.jar' ! -name '*-sources.jar' | sort | head -n 1)"
if [ -z "$JAR" ]; then
  echo "ERROR: Build finished but no installable mod JAR was found."
  exit 1
fi

echo
echo "SUCCESS"
echo "Your Fabric mod JAR is:"
echo "$JAR"

if [ "$(uname -s)" = "Darwin" ]; then
  open build/libs >/dev/null 2>&1 || true
fi
