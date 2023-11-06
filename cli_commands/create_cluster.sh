#!/bin/bash

# CLUSTER CONFIGURATION
CLUSTER_NAME="nyc-accidents-cluster"
REGION="us-west1"
MASTER_MACHINE="n1-standard-2"
MASTER_BOOT_SIZE=100
NUM_WORKERS=6
WORKER_MACHINE="n1-standard-2"
WORKER_BOOT_SIZE=100
IMAGE_VER="2.1-debian11"



# CREATE CLUSTER
create_cluster="gcloud dataproc clusters create $CLUSTER_NAME \
	--region $REGION \
	--master-machine-type $MASTER_MACHINE \
	--master-boot-disk-size $MASTER_BOOT_SIZE
	--num-workers $NUM_WORKERS \
	--worker-machine-type $WORKER_MACHINE \
	--worker-boot-disk-size $WORKER_BOOT_SIZE
	--image-version $IMAGE_VER"

eval $create_cluster
