#!/bin/bash

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <source_file>"
  exit 1
fi

SOURCE="$1"
BUCKET="gs://nyc-accidents-bucket/"

load_data="gsutil cp $SOURCE $BUCKET"

eval $load_data
