#!/bin/bash

DO_NOT_BUILD=false
DEV_ENV="tf"
RUN_FITNESSE=false
FITNESSE_MODE=auto
DEBUG_PORT=8000
DEBUG_OPTS=""
export LOG_LEVEL=${LOG_LEVEL:-DEBUG}
export LOGGLY_LEVEL="OFF"
BUILD_EXIT_CODE=0
export DEPLOY_ENV=local
export TZ="MST"

usage() {
    cat << EOF
usage: $0 [options]

Options
  -e    The environment to use (te | tf | ts | qa | local).  Default: tf.
  -d    Enable debugging for maven.  This will require you to attach a debugger before tomcat starts.
  -n    Do not build project first, just launch tomcat.
  -l    Launch the dependencies for the API locally.
  -h    Display this help.
  -f    Run fitnesse wiki mode.
  -a    Run fitnesse auto mode.
EOF
    exit 1
}

while getopts ":dnlfe:ha" opt; do
    case $opt in
        d)
            DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$DEBUG_PORT"
            ;;
        n)
            DO_NOT_BUILD=true
            ;;
        e)
            DEV_ENV=${OPTARG}
            ;;
        l)
            LAUNCH_DEPENDENCIES=true
            ;;
        f)
            RUN_FITNESSE=true
            FITNESSE_MODE=wiki
            ;;
        a)
            RUN_FITNESSE=true
            FITNESSE_MODE=auto
            ;;
        h)
            usage
            ;;
        \?)
            echo "Invalid"
            exit 1
            ;;
    esac
done

case $DEV_ENV in
    "qa")
        export CM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=runamuck.trueffect.com)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=tfaqa.trueffect.com)))"
        export CM_DB_USER="TE_XLS"
        export CM_DB_PASS="3t3xl$"
        ;;
    "ts")
        export CM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=runamuck.trueffect.com)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=tfatest.trueffect.com)))"
        export CM_DB_USER="TE_XLS"
        export CM_DB_PASS="3t3xl$"
        ;;
    "te")
        export CM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=runamuck.trueffect.com)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=tfadev.trueffect.com)))"
        export CM_DB_USER="TE_XLS"
        export CM_DB_PASS="3t3xl$"
        ;;
    "tf")
        export CM_DB_URL="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.13.152)(PORT=1521))(CONNECT_DATA=(SERVER=dedicated)(SERVICE_NAME=orcl.bolivia.assuresoft.com)))"
        export CM_DB_USER="te_xls"
        export CM_DB_PASS="texls"
        ;;
    "local")
        export ETCD_URL="http://localhost:4001"
        ;;
    \?)
        echo "Environment must be either te, tf, qa, ts, or local"
        exit 1
        ;;
esac

if [ "$LAUNCH_DEPENDENCIES" = true ] ; then
    ./deploy/environments/local/launch-dependencies.sh
fi

if [ "$DO_NOT_BUILD" = false ] && [ "$RUN_FITNESSE" = false ] ; then
    mvn clean install
    BUILD_EXIT_CODE=$?
fi

if [ "$BUILD_EXIT_CODE" -gt 0 ] ; then
    echo "Build was unsuccessful"
    exit 1
fi

if [ "$RUN_FITNESSE" = true ] ; then
    cd fitnesse-test
    mvn clean verify -P ${FITNESSE_MODE}
else
    export MAVEN_OPTS="-Xms1024m -Xmx4096m -XX:MaxPermSize=256m $DEBUG_OPTS"
    mvn tomcat7:run -Duser.timezone=$TZ
fi
