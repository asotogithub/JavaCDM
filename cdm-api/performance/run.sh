#!/usr/bin/env bash

PORT="8080"
SERVER="localhost"
PROTOCOL="http"
ENVIRONMENT="DEV"
VERSION=""
NUM_USERS="1"
LOOP_COUNT="1"

usage() {
    cat << EOF
usage: $0 [options] [environment]

Options
  -s    Server
  -p    Port
  -v    Version
  -n    Number of Users
  -l    Number of Loops

Environment
  Must be one of DEV, STG, or PROD
EOF
    exit 1
}

ENVIRONMENT=${@: -1}

case $ENVIRONMENT in
    DEV)
        SERVER="my-dev.trueffect.com"
        PORT="443"
        PROTOCOL="https"
        VERSION="/latest"
        ;;
    STG)
        SERVER="my-stg.trueffect.com"
        PORT="443"
        PROTOCOL="https"
        ;;
    PROD)
        SERVER="my.trueffect.com"
        PORT="443"
        PROTOCOL="https"
        ;;
    \?)
        echo "Invalid environment"
        usage
        ;;
esac

while getopts ":hp:s:v:n:l:" opt; do
    case $opt in
        p)
            PORT=${OPTARG}
            ;;
        s)
            SERVER=${OPTARG}
            PROTOCOL="http"
            ;;
        v)
            VERSION="/${OPTARG}"
            ;;
        n)
            NUM_USERS=${OPTARG}
            ;;
        l)
            LOOP_COUNT=${OPTARG}
            ;;
        h)
            usage
            ;;
        \?)
            echo "Invalid option"
            usage
            ;;
    esac
done

echo "Running with parameters:"
echo "  Protocol            ${PROTOCOL}"
echo "  Server              ${SERVER}"
echo "  Port                ${PORT}"
echo "  Environment         ${ENVIRONMENT}"
echo "  Version             ${VERSION}"
echo "  Number of Users     ${NUM_USERS}"
echo "  Number of Loops     ${LOOP_COUNT}"

JMETER=$(which jmeter)
${JMETER:-/app/jmeter/bin/jmeter} -n -t cdm-test.jmx -l cdm-test.jmx.jtl -Jenvironment=$ENVIRONMENT -JapiVersion=$VERSION -JnumUsers=$NUM_USERS -JloopCount=$LOOP_COUNT -Jserver=$SERVER -Jprotocol=$PROTOCOL -Jport=$PORT