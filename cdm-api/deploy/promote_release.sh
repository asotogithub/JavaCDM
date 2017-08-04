#!/usr/bin/env bash

set -e

SCRIPT_DIR=$( cd "$( dirname "${0}" )" && pwd )
BASE_DIR="${SCRIPT_DIR}/.."

. ${SCRIPT_DIR}/functions.sh

VERSION=$1
if [ "$#" -ne 1 ]; then
    VERSION=$(detectVersion)
fi

pullImage "truadvertiser" "$VERSION-rc"
pullImage "truadvertiser-api" "$VERSION-rc"

promoteImage "truadvertiser" "$VERSION-rc" "$VERSION"
promoteImage "truadvertiser-api" "$VERSION-rc" "$VERSION"

git checkout release/$VERSION
git pull
git checkout master
git pull
git merge release/$VERSION
git push
git tag -a $VERSION -m "Version $VERSION"
git push origin $VERSION

git checkout develop
git pull
git merge release/$VERSION
git push

#git push origin --delete release/$VERSION

echo "Done"