#!/usr/bin/env bash

set -eu

myself="$(basename "$0")"
version_file="$(pwd)/buildSrc/src/main/kotlin/Dependencies.kt"
current_branch="$(git rev-parse --abbrev-ref HEAD)"
current_commit="$(git log -1 --format='%H')"
dryrun=0
verbose=0
publish=0
bump="INVALID"

required_commands=("getopt" "pysemver" "git")
valid_types=('major' 'minor' 'patch' 'prerelease' 'build')

error() {
  echo -e "$@" >/dev/fd/2
}

crash() {
  [ "$#" -gt 0 ] && error "$1"
  exit 1
}

kotlin_opposite() {
  if [ "$1" = "true" ]; then
    echo "false"
  elif [ "$1" = "false" ]; then
    echo "true"
  else
    crash "Unknown Kotlin boolean value: '$1'"
  fi
}

set_snapshot() {
  sed -i -E "s/const val snapshot = $(kotlin_opposite "$1")/const val snapshot = $1/g" "$version_file"
}

set_version() {
  sed -i -E "s/const val baseVersion = \"$1\"/const val baseVersion = \"$2\"/g" "$version_file"
}

check-dependencies() {
  for com in "${required_commands[@]}"; do
    if ! command -v "$com" >/dev/null; then
      error "$com not found but it's required! Please install it with your favourite package manager."
      crash "\n\t\t\t\tOr just use Guix ;)\n"
    fi
  done
}

usage() {
  cat <<EOF
Usage: ${myself} -b <bump-type> [-hpbvd]
Release a new version of Orto Flavoured Markdown according to https://semver.org

-h,          --help                  Show this help message.

-b,          --bump                  Select a version bump type

-p,          --publish               Publish current release to Codeberg and Github.

-d,          --dryrun                Show operations, instead of carrying them out.

             --list-types            List all valid version bump types.

-v,          --verbose               Run script in verbose mode. Will print out each step of execution.
EOF
}

validate-bump-type() {
  if [[ ${valid_types[*]} =~ (^|[[:space:]])"$1"($|[[:space:]]) ]]; then
    true
  else
    crash "Invalid bump type: $1"
  fi
}

current-version() {
  grep 'const val baseVersion' "$version_file" | sed -E 's/(.*baseVersion = "|")//g'
}

release-new-version() {
  current="$(current-version)"
  next="$(pysemver bump "$1" "$current")"
  echo "RELEASING VERSION: ${next}"

  [ "$verbose" = "1" ] && echo "Updating ${version_file}..."
  [ "$dryrun" = "0" ] && set_version "$current" "$next"
  [ "$dryrun" = "0" ] && set_snapshot "false"

  [ "$verbose" = "1" ] && echo "Committing ${version_file}"
  [ "$dryrun" = "0" ] && git add "${version_file}" && git commit -m "Release v${next}."

  [ "$verbose" = "1" ] && echo "Tagging Git HEAD with v${next}"
  [ "$dryrun" = "0" ] && git tag "v${next}" || true
}

release-new-snapshot() {
  echo "Update to SNAPSHOT version"

  [ "$verbose" = "1" ] && echo "Setting snapshot = true in ${version_file}..."
  [ "$dryrun" = "0" ] && set_snapshot "true"

  [ "$verbose" = "1" ] && echo "Committing ${version_file}"
  [ "$dryrun" = "0" ] && git add "${version_file}" && git commit -m "Release SNAPSHOT." || true
}

parse-args() {
  [ "$#" -eq 0 ] && crash "$(usage)"

  options=$(getopt -l "help,publish,bump:,list-types,verbose,dryrun" -o "hpb:vd" -- "$@")

  # set --:
  # If no arguments follow this option, then the positional parameters are unset. Otherwise, the positional parameters
  # are set to the arguments, even if some of them begin with a ‘-’.
  eval set -- "$options"

  while true; do
    case $1 in
    -h | --help)
      usage
      exit 0
      ;;
    --list-types)
      echo "${valid_types[@]}"
      exit 0
      ;;
    -v | --verbose)
      verbose=1
      set -vx
      ;;
    -d | --dryrun)
      dryrun=1
      verbose=1
      ;;
    -p | --publish)
      publish=1
      ;;
    -b | --bump)
      shift
      bump="$1"
      ;;
    --)
      shift
      break
      ;;
    esac
    shift
  done

  if [ "$bump" = "INVALID" ] && [ "$publish" = "0" ]; then
    error "You can either build a new release or publish the current release to Git forges."
    crash "See ${myself} -h for more information."
  fi
}

parse-args "$@"

if [ "$bump" != "INVALID" ]; then
  check-dependencies
  validate-bump-type "$bump"
  release-new-version "$bump"
  release-new-snapshot
fi

if [ "$publish" = "1" ]; then
  set +e
  # If this command fails we still want to go back to the
  # branch we were on.
  git push
  git push github
  git push --tags
  git push github --tags
  set -e
fi

exit 0
