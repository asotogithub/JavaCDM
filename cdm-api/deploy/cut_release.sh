#!/usr/bin/env bash

set -e

SCRIPT_DIR=$( cd "$( dirname "${0}" )" && pwd )
BASE_DIR="$SCRIPT_DIR/.."

. ${SCRIPT_DIR}/functions.sh

VERSION=$1

if [ "$#" -ne 1 ]; then
  echo "USAGE: cut_release.sh <version>"
  exit 1
fi

git pull || echo
git checkout -b release/$VERSION

cd "$BASE_DIR"
mvn versions:set -DnewVersion=$VERSION
mvn versions:commit

cat $BASE_DIR/ui/package.json | sed "s/\"version\": *\".*\"/\"version\": \"$VERSION\"/g" > $BASE_DIR/ui/package.json.new
mv $BASE_DIR/ui/package.json.new $BASE_DIR/ui/package.json

git commit -am "Cut release for version $VERSION"
git push --set-upstream origin release/$VERSION