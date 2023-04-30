#!/usr/bin/env nix-shell
#!nix-shell -i bash -p nodejs openjdk11

set -eu

error() {
  echo -e "$@" >/dev/fd/2
}

crash() {
  [ "$#" -gt 0 ] && error "$1"
  exit 1
}

validate-env() {
  if [ -z ${OFM_KEY+x} ]; then
    crash "The OFM_KEY environment variable is required but unset. Please set it to upload the new release to Sonatype."
  fi
  if [ -z ${OFM_PASSWORD+x} ]; then
    crash "The OFM_PASSWORD environment variable is required but unset. Please set it to upload the new release to Sonatype."
  fi
}

export ORG_GRADLE_PROJECT_signingKey="$OFM_KEY"
export ORG_GRADLE_PROJECT_signingPassword="$OFM_PASSWORD"
JAVA_HOME="$(dirname "$(dirname "$(which java)")")"
export JAVA_HOME

./gradlew publish --no-daemon
