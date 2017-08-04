#!/usr/bin/env bash

# The Mesos cluster to deploy to
# Assumed: Marathon on 8080
# Mesos: 5050
export MESOS_ADDRESS="mesosuat.trueffect.com"
export INSTANCES=3

# The Envrionment URL
export LB_URL="https:\/\/my-uat.trueffect.com"

# TFA DB Settings
export TFA_HOSTNAME="optimus.trueffect.com"
export TFA_SERVICE_NAME="tfacdm1.trueffect.com"
export TFA_USER="TE_XLS"
export TFA_PASSWORD="3t3xl$"
export TFA_PORT="1523"

# Metrics DB
export METRICS_HOSTNAME="optimus.trueffect.com"
export METRICS_SCHEMA="tdmprod.trueffect.com"
export METRICS_DB_USER="DV_USER"
export METRICS_DB_PASS="dvg3td1m"
export METRICS_PORT="1521"

# DIM DB
export DIM_HOSTNAME="optimus.trueffect.com"
export DIM_SCHEMA="dimcdm1.trueffect.com"
export DIM_DB_USER="DIM_OWNER"
export DIM_DB_PASS="d1m\$0wn3r"
export DIM_PORT="1523"

export CREATIVE_PATH="\/nfs\/taxls\/cdm"

# AWS Settings - UAT account is Read-only for DynamoDB
export AWS_ACCESS_KEY_ID="AKIAIPGEJP34V5637ZZQ"
export AWS_SECRET_ACCESS_KEY="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"

# ADM Settings - UAT is pointing to PRD RedShift since there is no reporting in UAT
export DS_CONFIG_TABLE_NAME="PRD_ADM_DATASET_CONFIG"
export DS_CONFIG_S3_PATH_INDEX_NAME="s3path-index"
export DS_CONFIG_AGENCY_ID_INDEX_NAME="agency_id-index"
export DS_CONFIG_DOMAIN_INDEX_NAME="domain-index"
export DS_CONFIG_REGION="us-east-1"
export ADM_FTP_BUCKET="te-sftp-uat"
export ADM_DEFAULT_NOTIFICATION_TOPIC="arn:aws:sns:us-east-1:794962544463:UAT_TE_ADM_NOTIFICATIONS"
export REDSHIFT_URL="jdbc:redshift:\/\/prd-rs.crfwbxbzmnrh.us-east-1.redshift.amazonaws.com:5439\/adm"
export REDSHIFT_USER="rsuser"
export REDSHIFT_PASSWORD="\$RSus0wr!"

# Trafficking Addresses - UAT is pointing to PRD MicroStrategy since there is no reporting in UAT
export TRAFFICKING_WSDL_URL="http:\/\/trafficking-cdm.trueffect.com\/1.1\/TraffickingService.xamlx?wsdl"
export DELIVERY_URL="https:\/\/truconnect-cdm.trueffect.com\/TruAPI\/Delivery\/"
export MEASUREMENT_URL='https:\/\/measurement.trueffect.com'
export TRUQ_URL="https:\/\/truconnect-cdm.trueffect.com\/TruApi\/Client\/TruQ\/InsertMessageToQueue"

# Internal Addresses (for TruAPI infrastructure)
export OAUTH_URL="http:\/\/truapi-localhost.trueffect.com\/oauth"
export PUBLIC_URL="http:\/\/truapi-localhost.trueffect.com\/cms"

# Logging level
export LOG_LEVEL="INFO"
export LOGGLY_LEVEL="INFO"

# Enviroment (for loggly)
export DEPLOY_ENV=uat

#Cassandra Node Addresses & Environment
export CASS_NODE_ADDRESSES="us-west-2-dev-core-cassandra-audience-dev.trueffect.net"
export CASS_ENV="uat"
export CASS_SPECULATIVE_DELAY=500
export CASS_SPECULATIVE_MAX_ATTEMPTS=2