#!/bin/bash

config() {
    while getopts ":v:t:b:" opt; do
        case $opt in
            v)
                VERSION=${OPTARG}
                ;;
            t)
                DOCKER_TAG=${OPTARG}
                ;;
            b)
                BASE_BRANCH=${OPTARG}
                ;;
            \?)
                echo "Invalid option: -$OPTARG" >&2
                exit 1
                ;;
            :)
                echo "Option -$OPTARG requires an argument." >&2
                exit 1
                ;;
        esac
    done
}

# $1 - mesos address
marathonLeader() {
    local leader=$(curl -s -X GET -H "Content-Type: application/json" http://$1:8080/v2/leader | cut -d\" -f4)

    echo "$leader"
}

branchName() {
    local branchName

    # Get the clean branch name
    if [ -z "${GIT_BRANCH}" ]; then
        branchName=$(git name-rev --name-only --tags HEAD) # Try to infer the branch
    else
        branchName=$GIT_BRANCH # Jenkins provided
    fi

    if [ $branchName = 'undefined' ]; then
        branchName=$(git rev-parse --abbrev-ref HEAD)
    fi

    echo $branchName | sed 's/\//-/g' | sed 's/origin-//' | sed 's/\^0//'
}

deployVersion() {
    local version=$(detectVersion)
    local branchName=$(branchName)
    local deployVersion
    if [[ "$branchName" =~ develop ]] ; then
        deployVersion="latest"
    elif [[ "$branchName" =~ release.* ]] ; then
        deployVersion="${version}-rc"
    elif [[ "$branchName" =~ master ]] || [[ "$branchName" =~ HEAD ]] ; then
        deployVersion="${version}"
    else
        deployVersion="${branchName}"
    fi

    echo "$deployVersion"
}

# $1 - version number
dockerTag() {
    local version
    if [ "$#" -ne 1 ]; then
        version=$(detectVersion)
    else
        version=$1
    fi
    local branchName=$(branchName)
    local tag

    if [[ "$branchName" =~ develop ]] ; then
        tag="latest"
    elif [[ "$branchName" =~ release.* ]] ; then
        tag="${version}-rc"
    elif [[ "$branchName" =~ master ]] || [[ "$branchName" =~ HEAD ]] ; then
        tag="${version}"
    else
        tag="${branchName}"
    fi

    echo "$tag"
}

# $1 - version number
secondaryDockerTag() {
    local version
    if [ "$#" -ne 1 ]; then
        version=$(detectVersion)
    else
        version=$1
    fi
    local branchName=$(branchName)
    local tag

    if [[ "$branchName" =~ develop ]] ; then
        unset tag
    elif [[ "$branchName" =~ release.* ]] ; then
        unset tag
    elif [[ "$branchName" =~ master ]] || [[ "$branchName" =~ HEAD ]] ; then
        unset tag
    else
        tag="edge"
    fi

    echo "$tag"
}

detectVersion() {
    echo $(cd ${BASE_DIR} && mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep "^[0-9][0-9]*\.[0-9]*\.[0-9]*.*")
}

# $1 - command name
assertInstalled() {
    command -v $1 >/dev/null 2>&1 || { echo >&2 "$1 is not installed or not on the PATH.  Aborting."; exit 1; }
}

# $1 - version number
uiVersionHtml() {
    local version
    if [ "$#" -ne 1 ]; then
        version=$(detectVersion)
    else
        version=$1
    fi
    local commitSha=$(git rev-parse HEAD)

    # Populate the version file for the UI
    local buildDateTime=$(date +"%Y-%m-%d-%H:%M")
    echo "ui:${version}_built_from:${commitSha}_on:${buildDateTime}" > "${BASE_DIR}/ui/client/views/partials/version.html"
}

# $1 - name of the image
# $2 - tag
pullImage() {
    docker pull quay.io/trueffect/$1:$2 &
}

# $1 - name of the image
# $2 - Dockerfile location
# $3 - primary tag of the image
# $4 - secondary tag of the image
buildImage() {
    docker build -t trueffect/$1:$3 $2
    docker tag -f trueffect/$1:$3 quay.io/trueffect/$1:$3
    if [ -n "${4}" ] ; then
        docker tag -f trueffect/$1:$3 quay.io/trueffect/$1:$4
    fi
}

# $1 - name of the image
# $2 - source tag
# $3 - destination tag
promoteImage() {
    docker tag -f quay.io/trueffect/$1:$2 quay.io/trueffect/$1:$3
    docker push quay.io/trueffect/$1:$3
}

# $1 - name of the image
# $2 - primary tag of the image
# $3 - secondary tag of the image
pushImage() {
    docker push quay.io/trueffect/$1:$2
    if [ -n "${3}" ] ; then
        docker push quay.io/trueffect/$1:$3
    fi
}

# $1 - name of the image
# $2 - primary tag of the image
# $3 - secondary tag of the image
formatOutput() {
    echo "$1"
    echo " $2"
    if [ -n "${3}" ] ; then
        echo " $3"
    fi
}
