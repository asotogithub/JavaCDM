#!/usr/bin/env bash

set -e

if [ "$#" -ne 3 ]; then
    echo "USAGE: package_tomcat.sh <DEV|QA|CDM|PROD> <deploy version> <release version>"
    echo "  <deploy version> should be something like v2.4"
    echo "  <release version> should be something like 2.4.2"
    exit 1
fi

SCRIPT_DIR=$( cd "$( dirname "${0}" )" && pwd )
BASE_DIR="$SCRIPT_DIR/.."
REFERENCE_DIR="$BASE_DIR/resources/src/main/resources"

. ${SCRIPT_DIR}/functions.sh

VERSION=$3

DEPLOY_VERSION=$2

if { . "$SCRIPT_DIR/config/$1.sh"; }; then
    CM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${TFA_HOSTNAME})(PORT=${TFA_PORT}))(CONNECT_DATA=(SERVICE_NAME=${TFA_SERVICE_NAME})))"
    METRICS_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${METRICS_HOSTNAME})(PORT=${METRICS_PORT}))(CONNECT_DATA=(SERVICE_NAME=${METRICS_SCHEMA})))"
    DIM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${METRICS_HOSTNAME})(PORT=${DIM_PORT}))(CONNECT_DATA=(SERVICE_NAME=${DIM_SCHEMA})))"
    VERSIONED_PUBLIC_URL="${PUBLIC_URL}\/$DEPLOY_VERSION\/"
    VERSIONED_OAUTH_URL="${OAUTH_URL}\/$DEPLOY_VERSION\/"
    API_BASE_NAME=$DEPLOY_ENV-api-$VERSION

    # Hard-code the correct values for the configs
    cat ${REFERENCE_DIR}/reference.conf |
    sed "s/\${?CM_DB_URL}/\"$CM_DB_URL\"/g" | \
    sed "s/\${?CM_DB_USER}/\"$TFA_USER\"/g" | \
    sed "s/\${?CM_DB_PASS}/\"$TFA_PASSWORD\"/g" | \
    sed "s/\${?METRICS_DB_URL}/\"$METRICS_DB_URL\"/g" | \
    sed "s/\${?METRICS_DB_USER}/\"$METRICS_DB_USER\"/g" | \
    sed "s/\${?METRICS_DB_PASS}/\"$METRICS_DB_PASS\"/g" | \
    sed "s/\${?DIM_DB_URL}/\"$DIM_DB_URL\"/g" | \
    sed "s/\${?DIM_DB_USER}/\"$DIM_DB_USER\"/g" | \
    sed "s/\${?DIM_DB_PASS}/\"$DIM_DB_PASS\"/g" | \
    sed "s/\${?DELIVERY_URL}/\"$DELIVERY_URL\"/g" | \
    sed "s/\${?TRUQ_URL}/\"$TRUQ_URL\"/g" | \
    sed "s/\${?PUBLIC_URL}/\"$VERSIONED_PUBLIC_URL\"/g" | \
    sed "s/\${?OAUTH_URL}/\"$VERSIONED_OAUTH_URL\"/g" | \
    sed "s/\${?TRAFFICKING_WSDL_URL}/\"$TRAFFICKING_WSDL_URL\"/g" > ${REFERENCE_DIR}/reference.conf.tmp

    cat ${REFERENCE_DIR}/logback.xml |
    sed "s/\${LOG_LEVEL.*}/$LOG_LEVEL/g" | \
    sed "s/\${DEPLOY_ENV.*}/$DEPLOY_ENV/g" > ${REFERENCE_DIR}/logback.xml.tmp

    mv ${REFERENCE_DIR}/reference.conf.tmp ${REFERENCE_DIR}/reference.conf
    mv ${REFERENCE_DIR}/logback.xml.tmp ${REFERENCE_DIR}/logback.xml

    pushd .

    cd $BASE_DIR

    # Build the code
    mvn clean install

    rm -rf $API_BASE_NAME
    rm -f *.tar
    
    mkdir $API_BASE_NAME
    cp public/target/public*.war $API_BASE_NAME/cms#$VERSION.war
    cp tpasapi/target/tpasapi*.war $API_BASE_NAME/3pasapi#$VERSION.war
    cp oauth/target/oauth*.war $API_BASE_NAME/oauth#$VERSION.war
    cp deploy/tomcat/*.txt $API_BASE_NAME
    cp deploy/tomcat/deploy $API_BASE_NAME

    chmod +x $API_BASE_NAME/deploy
    
    tar cf $API_BASE_NAME.tar $API_BASE_NAME
    
    rm -rf $API_BASE_NAME

    # Remove any changes
    git checkout -- .

    popd

    echo "Done"
else
    echo "Ensure the environment passed as input exists in the config dir and the files syntax is correct."
    exit 1
fi
