#!/usr/bin/env bash

set -e

if [ "$#" -ne 2 ]; then
    echo "USAGE: package_tomcat.sh <DEV|QA|CDM|PROD> <deploy version>"
    echo "  <deploy version> should be something like 2.4"
    exit 1
fi

DEPLOY_VERSION=$2
SCRIPT_DIR=$( cd "$( dirname "${0}" )" && pwd )
BASE_DIR="$SCRIPT_DIR/.."
REFERENCE_DIR="$BASE_DIR/resources/src/main/resources"
SDK_DIR="$BASE_DIR/TE_SDK_$DEPLOY_VERSION"

. ${SCRIPT_DIR}/functions.sh

if { . "$SCRIPT_DIR/config/$1.sh"; }; then
    CM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${TFA_HOSTNAME})(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=${TFA_SERVICE_NAME})))"
    METRICS_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${METRICS_HOSTNAME})(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=${METRICS_SCHEMA})))"
    DIM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${DIM_HOSTNAME})(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=${DIM_SCHEMA})))"
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

    rm -rf $SDK_DIR
    rm -f *.zip
    
    mkdir $SDK_DIR

    cp fitnesse-test/target/lib/commons-tpasapi-$DEPLOY_VERSION.jar $SDK_DIR/trueffect-tpasapi-common-$DEPLOY_VERSION.jar
    cp fitnesse-test/target/lib/commons-$DEPLOY_VERSION.jar $SDK_DIR/trueffect-common-$DEPLOY_VERSION.jar
    cp fitnesse-test/target/lib/search-$DEPLOY_VERSION.jar $SDK_DIR/trueffect-search-$DEPLOY_VERSION.jar
    cp fitnesse-test/target/lib/tpasapi-client-$DEPLOY_VERSION.jar $SDK_DIR/trueffect-tpasapi-client-$DEPLOY_VERSION.jar
    
    cp fitnesse-test/target/lib/commons-beanutils*.jar $SDK_DIR
    cp fitnesse-test/target/lib/commons-lang*.jar $SDK_DIR
    cp fitnesse-test/target/lib/jackson*.jar $SDK_DIR
    cp fitnesse-test/target/lib/jaxb*.jar $SDK_DIR
    cp fitnesse-test/target/lib/jersey-client*.jar $SDK_DIR
    cp fitnesse-test/target/lib/jersey-core*.jar $SDK_DIR
    cp fitnesse-test/target/lib/jersey-json*.jar $SDK_DIR
    
    cp server_config/sdk_assets/* $SDK_DIR
    
    sed s!\{version\}!${version}!g $SDK_DIR/compile.bat > $SDK_DIR/compile.bat2
    sed s!\{version\}!${version}!g $SDK_DIR/getimage.bat > $SDK_DIR/getimage.bat2
    sed s!\{version\}!${version}!g $SDK_DIR/getcg.bat > $SDK_DIR/getcg.bat2
    
    mv $dir/compile.bat2 $dir/compile.bat
    mv $dir/getimage.bat2 $dir/getimage.bat
    mv $dir/getcg.bat2 $dir/getcg.bat
    
    zip $dir.zip -xi $dir/*
    
    rm -rf $dir

    # Remove any changes
    git checkout -- .

    popd

    echo "Done"
else
    echo "Ensure the environment passed as input exists in the config dir and the files syntax is correct."
    exit 1
fi
