#!/usr/bin/env bash

# The Mesos cluster to deploy to
# Assumed: Marathon on 8080
# Mesos: 5050
export MESOS_ADDRESS="mesostst.trueffect.com"
export INSTANCES=2

# The Envrionment URL
export LB_URL="https:\/\/my-tst.trueffect.com"

# TFA DB Settings
export TFA_HOSTNAME="runamuck.trueffect.com"
export TFA_SERVICE_NAME="tfatest.trueffect.com"
export TFA_USER="TE_XLS"
export TFA_PASSWORD="3t3xl$"
export TFA_PORT="1521"

# Metrics DB
export METRICS_HOSTNAME="runamuck.trueffect.com"
export METRICS_SCHEMA="tdmqa.trueffect.com"
export METRICS_DB_USER="DV_USER"
export METRICS_DB_PASS="dvg3td1m"
export METRICS_PORT="1521"

# DIM DB
export DIM_HOSTNAME="runamuck.trueffect.com"
export DIM_SCHEMA="dimtest.trueffect.com"
export DIM_DB_USER="DIM_OWNER"
export DIM_DB_PASS="d1m\$0wn3r"
export DIM_PORT="1521"

export CREATIVE_PATH="\/nfs\/taxls\/Test"

# AWS Settings
export AWS_ACCESS_KEY_ID="AKIAJPYE2VHEHV3Q5X2A"
export AWS_SECRET_ACCESS_KEY="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"

# ADM Settings
export DS_CONFIG_TABLE_NAME="STG_ADM_DATASET_CONFIG"
export DS_CONFIG_S3_PATH_INDEX_NAME="s3path-index"
export DS_CONFIG_AGENCY_ID_INDEX_NAME="agency_id-index"
export DS_CONFIG_DOMAIN_INDEX_NAME="domain-index"
export DS_CONFIG_REGION="us-east-1"
export ADM_FTP_BUCKET="te-sftp-stg"
export ADM_DEFAULT_NOTIFICATION_TOPIC="arn:aws:sns:us-east-1:147178689111:STG_TE_ADM_NOTIFICATIONS"
export REDSHIFT_URL="jdbc:redshift:\/\/stg-rs.cphorc3y7sfj.us-east-1.redshift.amazonaws.com:5439\/adm"
export REDSHIFT_USER="rsuser"
export REDSHIFT_PASSWORD="\$RSus0wr!"

# Trafficking Addresses
export TRAFFICKING_WSDL_URL="https:\/\/trafficking-test.trueffect.com\/1.1\/TraffickingService.xamlx?wsdl"
export DELIVERY_URL="https:\/\/truconnect-test.trueffect.com\/TruAPI\/Delivery\/"
export MEASUREMENT_URL='https:\/\/measurement-mstest.trueffect.com'
export TRUQ_URL="https:\/\/truconnect-test.trueffect.com\/TruApi\/Client\/TruQ\/InsertMessageToQueue"

# Internal Addresses (for TruAPI infrastructure)
export OAUTH_URL="https:\/\/truapi-test.trueffect.com\/oauth"
export PUBLIC_URL="https:\/\/truapi-test.trueffect.com\/cms"

# Logging level
export LOG_LEVEL="INFO"
export LOGGLY_LEVEL="INFO"

# Enviroment (for loggly)
export DEPLOY_ENV=tst

#Cassandra Node Addresses & Environment
export CASS_NODE_ADDRESSES="us-west-2-dev-core-cassandra-audience-dev.trueffect.net"
export CASS_ENV="test"
export CASS_SPECULATIVE_DELAY=500
export CASS_SPECULATIVE_MAX_ATTEMPTS=2