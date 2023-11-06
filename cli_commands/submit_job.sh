#!/bin/bash

# ARGS: filename, partitions, eps, min_points
if [ "$#" -ne 4 ]; then
  echo "Usage: $0 <filename> <partitions> <eps> <min_points>"
  exit 1
fi

# EXECUTION CONFIGURATION
CLUSTER_NAME="nyc-accidents-cluster"
REGION="us-west1"
JAR="gs://nyc-accidents-bucket/main.jar"
MASTER="yarn"
FROM_FILE="gs://nyc-accidents-bucket/"
TO_FILE="gs://nyc-accidents-bucket/clustered"

FILENAME="$1"
PARTITIONS="$2"
EPS="$3"
MIN_POINTS="$4"

# SUBMIT JOB
submit_job="gcloud dataproc jobs submit spark \
	--cluster=$CLUSTER_NAME \
	--region=$REGION \
	--jar=$JAR \
	-- $MASTER $PARTITIONS ${FROM_FILE}${FILENAME} $EPS $MIN_POINTS $TO_FILE"

eval $submit_job
