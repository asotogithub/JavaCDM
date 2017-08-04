#!/usr/bin/env bash

set -e

SCRIPT_DIR=$( cd "$( dirname "${0}" )" && pwd )
BASE_DIR="$SCRIPT_DIR/.."

. ${SCRIPT_DIR}/functions.sh

config "$@"
shift $((OPTIND-1))

if [ -z "$VERSION" ] ; then
  VERSION=$(deployVersion)
fi

# Source the environments config
if { . "$SCRIPT_DIR/config/$1.sh"; }; then
  MARATHON_LEADER=$(marathonLeader $MESOS_ADDRESS)
  echo "Deploying UI version $VERSION to $MESOS_ADDRESS for ENV $1 to leader $MARATHON_LEADER"
  # Replace the MARATHON_URL placeholder with the cluster node passed in
  cat $SCRIPT_DIR/marathon/ta_ui.json |
  sed "s/<MESOS_ADDRESS>/$MESOS_ADDRESS/g" | \
  sed "s/<CLUSTER_DNS_ADDRESS>/$LB_URL/g" | \
  sed "s/<UI_VERSION>/$VERSION/g" | \
  sed "s/<API_VERSION>/$VERSION/g" | \
  sed "s/<MEASUREMENT_URL>/$MEASUREMENT_URL/g" | \
  sed "s/<DEPLOY_ENV>/$DEPLOY_ENV/g" | \
  sed "s/<INSTANCES>/$INSTANCES/g" | \
  curl -s -XPUT -H "Content-Type: application/json" --data @- http://$MARATHON_LEADER/v2/apps/truadvertiser-ui

  sleep 10
  # The deploy will not do anything if theres no change in the config.  Performing a rolling restart ensures the apps are updated
  curl -s -XPOST -H "Content-Type: application/json" http://$MESOS_ADDRESS:8080/v2/apps/truadvertiser-ui/restart

else
  echo "USAGE: ui_deploy.sh [options] <DEV|PROD|STG|TEST>"
  exit 1
fi
