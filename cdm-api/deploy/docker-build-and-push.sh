#!/bin/bash

set -e

SCRIPT_DIR=$( cd "$( dirname "${0}" )" && pwd )
BASE_DIR="${SCRIPT_DIR}/.."

. ${SCRIPT_DIR}/functions.sh

assertInstalled docker
assertInstalled npm
assertInstalled mvn

config "$@"

if [ -z "$DOCKER_TAG" ] ; then
    PRIMARY_TAG=$(dockerTag)
    SECONDARY_TAG=$(secondaryDockerTag)
    VERSION=$(deployVersion)
else
    PRIMARY_TAG="$DOCKER_TAG"
    VERSION="$DOCKER_TAG"
fi

uiVersionHtml $VERSION

echo "Active branch: '${BRANCH_NAME}'"
echo "Building and pushing following components:"

formatOutput "truadvertiser" $PRIMARY_TAG $SECONDARY_TAG
formatOutput "truadvertiser-api" $PRIMARY_TAG $SECONDARY_TAG

buildImage "truadvertiser" $BASE_DIR/ui $PRIMARY_TAG $SECONDARY_TAG
pushImage "truadvertiser" $PRIMARY_TAG $SECONDARY_TAG

buildImage "truadvertiser-api" $BASE_DIR $PRIMARY_TAG $SECONDARY_TAG
pushImage "truadvertiser-api" $PRIMARY_TAG $SECONDARY_TAG

echo "Done!"
