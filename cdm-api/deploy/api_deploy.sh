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
  echo "Deploying API version $VERSION to $MESOS_ADDRESS for ENV $1 to leader $MARATHON_LEADER"
  cat $SCRIPT_DIR/marathon/ta_api.json |
  sed "s/<MESOS_ADDRESS>/$MESOS_ADDRESS/g" | \
  sed "s/<TFA_HOSTNAME>/$TFA_HOSTNAME/g" | \
  sed "s/<TFA_SCHEMA>/$TFA_SERVICE_NAME/g" | \
  sed "s/<TFA_USER>/$TFA_USER/g" | \
  sed "s/<TFA_PASSWORD>/$TFA_PASSWORD/g" | \
  sed "s/<TFA_PORT>/$TFA_PORT/g" | \
  sed "s/<METRICS_HOSTNAME>/$METRICS_HOSTNAME/g" | \
  sed "s/<METRICS_SCHEMA>/$METRICS_SCHEMA/g" | \
  sed "s/<METRICS_DB_USER>/$METRICS_DB_USER/g" | \
  sed "s/<METRICS_DB_PASS>/$METRICS_DB_PASS/g" | \
  sed "s/<METRICS_PORT>/$METRICS_PORT/g" | \
  sed "s/<DIM_HOSTNAME>/$DIM_HOSTNAME/g" | \
  sed "s/<DIM_SCHEMA>/$DIM_SCHEMA/g" | \
  sed "s/<DIM_DB_USER>/$DIM_DB_USER/g" | \
  sed "s/<DIM_DB_PASS>/$DIM_DB_PASS/g" | \
  sed "s/<DIM_PORT>/$DIM_PORT/g" | \
  sed "s/<API_VERSION>/$VERSION/g" | \
  sed "s/<CREATIVE_PATH>/$CREATIVE_PATH/g" | \
  sed "s/<DELIVERY_URL>/$DELIVERY_URL/g" | \
  sed "s/<TRUQ_URL>/$TRUQ_URL/g" | \
  sed "s/<TRAFFICKING_WSDL_URL>/$TRAFFICKING_WSDL_URL/g" | \
  sed "s/<LOG_LEVEL>/$LOG_LEVEL/g" | \
  sed "s/<DEPLOY_ENV>/$DEPLOY_ENV/g" | \
  sed "s/<INSTANCES>/$INSTANCES/g" | \
  sed "s/<AWS_ACCESS_KEY_ID>/$AWS_ACCESS_KEY_ID/g" | \
  sed "s/<AWS_SECRET_ACCESS_KEY>/$AWS_SECRET_ACCESS_KEY/g" | \
  sed "s/<DS_CONFIG_TABLE_NAME>/$DS_CONFIG_TABLE_NAME/g" | \
  sed "s/<DS_CONFIG_S3_PATH_INDEX_NAME>/$DS_CONFIG_S3_PATH_INDEX_NAME/g" | \
  sed "s/<DS_CONFIG_AGENCY_ID_INDEX_NAME>/$DS_CONFIG_AGENCY_ID_INDEX_NAME/g" | \
  sed "s/<REDSHIFT_URL>/$REDSHIFT_URL/g" | \
  sed "s/<REDSHIFT_USER>/$REDSHIFT_USER/g" | \
  sed "s/<REDSHIFT_PASSWORD>/$REDSHIFT_PASSWORD/g" | \
  curl -s -XPUT -H "Content-Type: application/json" --data @- http://$MARATHON_LEADER/v2/apps/truadvertiser/$VERSION/api

  sleep 10
  # The deploy will not do anything if theres no change in the config.  Performing a rolling restart ensures the apps are updated
  curl -s -XPOST -H "Content-Type: application/json" http://$MARATHON_LEADER/v2/apps/truadvertiser/$VERSION/api/restart
  echo "Done deploying to $MESOS_ADDRESS!"
else
  echo "USAGE: api_deploy.sh [options] <DEV|PROD|STG|TEST>"
  exit 1
fi
