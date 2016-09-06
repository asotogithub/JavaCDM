#!/usr/bin/env bash
SCRIPT_DIR=$( cd "$( dirname "${0}" )" && pwd )
. "$SCRIPT_DIR/config/$1.sh"
w=10
c=0
echo MESOS_ADDRESS=$MESOS_ADDRESS
while [[ $c -lt $w ]]; do
    t=$(mktemp)
    curl -s -XGET http://$MESOS_ADDRESS:8080/v2/apps/truadvertiser-ui|python -m json.tool > $t
    tasksHealthy=`grep tasksHealthy $t|sed -e 's/,//g'|awk -F: '{print $2}'|sed -e 's/ //g'`
    tasksRunning=`grep tasksRunning $t|sed -e 's/,//g'|awk -F: '{print $2}'|sed -e 's/ //g'`
    tasksUnhealthy=`grep tasksUnhealthy $t|sed -e 's/,//g'|awk -F: '{print $2}'|sed -e 's/ //g'`
    tasksStaged=`grep tasksStaged $t|sed -e 's/,//g'|awk -F: '{print $2}'|sed -e 's/ //g'`
    echo tasksHealthy=$tasksHealthy
    echo tasksRunning=$tasksRunning
    echo tasksUnhealthy=$tasksUnhealthy
    echo tasksStaged=$tasksStaged
    if [[ $tasksHealthy -eq $tasksRunning ]] && [[ $tasksUnhealthy -eq 0 ]] && [[ $tasksStaged -eq 0 ]]; then
        echo "Successfully Deployed"
        c=10
    else
        echo "Wait 60 seconds"
        c=$((c+1))
        sleep 60
    fi
    rm $t
done
# End
