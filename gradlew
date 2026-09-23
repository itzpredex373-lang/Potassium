#!/bin/sh
set -e

APP_HOME="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
GRADLE_VERSION="2.14.1"
GRADLE_DIR="$APP_HOME/.gradle-bootstrap"
GRADLE_HOME="$GRADLE_DIR/gradle-$GRADLE_VERSION"
GRADLE_ZIP="$GRADLE_DIR/gradle-$GRADLE_VERSION-bin.zip"
GRADLE_URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
    command -v java >/dev/null 2>&1 || {
        echo "ERROR: Java is required. Use JDK 8."
        exit 1
    }
    mkdir -p "$GRADLE_DIR"
    if [ ! -f "$GRADLE_ZIP" ]; then
        echo "Downloading Gradle $GRADLE_VERSION..."
        if command -v curl >/dev/null 2>&1; then
            curl -fL "$GRADLE_URL" -o "$GRADLE_ZIP"
        elif command -v wget >/dev/null 2>&1; then
            wget -O "$GRADLE_ZIP" "$GRADLE_URL"
        else
            echo "ERROR: curl or wget is required for the first run."
            exit 1
        fi
    fi
    rm -rf "$GRADLE_HOME"
    command -v unzip >/dev/null 2>&1 || {
        echo "ERROR: unzip is required for the first run."
        exit 1
    }
    unzip -q "$GRADLE_ZIP" -d "$GRADLE_DIR"
fi

exec "$GRADLE_HOME/bin/gradle" "$@"
